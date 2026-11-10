/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.eclipse.sirius.components.collaborative.trees.dto.InitialDirectEditElementLabelInput;
import org.eclipse.sirius.components.collaborative.trees.dto.InitialDirectEditElementLabelSuccessPayload;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.trees.Tree;
import org.eclipse.sirius.web.application.views.explorer.services.ExplorerInitialDirectEditTreeItemLabelProvider;
import org.junit.jupiter.api.Test;
import hed.domain.HedSamples;
import hed.starter.representations.modelexplorer.HedInitialDirectEditTreeItemLabelProvider;
import hed.starter.representations.modelexplorer.HedJavaServices;

class ExplorerLabelsTest {
    private HedJavaServices services(Locale locale) {
        return new HedJavaServices(new BootstrapIntegrationTest.Identity(), new IObjectSearchService.NoOp(), new BootstrapIntegrationTest.Feedback(), locale, dwelling -> dwelling.getName());
    }

    @Test
    void explorerCombinesTheStoredNameAndTypeWithoutChangingOtherLabelsOrData() {
        var home = HedSamples.example(Locale.FRENCH);
        var device = home.getBoards().get(0).getDevices().get(1);
        var service = services(Locale.ENGLISH);
        var typeName = device.getDeviceType().getName();
        assertEquals("Q1 - " + typeName, service.getHedExplorerLabel(device));
        assertEquals("Q1", service.getHedLabel(device));
        assertEquals("Q1", service.getHedCell(device, "name"));
        device.getDeviceType().setName("Disjoncteur côté jardin");
        assertEquals("Q1 - Disjoncteur côté jardin", service.getHedExplorerLabel(device));
        assertEquals("Q1", device.getName());
        assertEquals(service.getHedExplorerLabel(device), services(Locale.FRENCH).getHedExplorerLabel(device));
        assertEquals("Dwelling", service.getHedExplorerLabel(home));
        assertEquals(home.getEquipment().get(0).getName(), service.getHedExplorerLabel(home.getEquipment().get(0)));
        var group = new HedJavaServices.Group(home, "gtl");
        assertEquals(service.getHedLabel(group), service.getHedExplorerLabel(group));
    }

    @Test
    void unnamedDevicesShowOnlyTheirTypeAndDevicesWithoutATypeKeepTheirName() {
        var device = HedSamples.example().getBoards().get(0).getDevices().get(1);
        var service = services(Locale.ENGLISH);
        for (String name : new String[] { null, "", "  " }) {
            device.setName(name);
            assertEquals(device.getDeviceType().getName(), service.getHedExplorerLabel(device));
        }
        device.setName("Q1 - Kitchen");
        device.setDeviceType(null);
        assertEquals("Q1 - Kitchen", service.getHedExplorerLabel(device));
    }

    @Test
    void initialInlineEditUsesTheRawNameAndDelegatesOtherObjectsToTheNativeProvider() {
        var device = HedSamples.example().getBoards().get(0).getDevices().get(1);
        IObjectSearchService search = new IObjectSearchService() {
            @Override
            public Optional<Object> getObject(IEditingContext context, String id) {
                return id.equals("device") ? Optional.of(device) : Optional.empty();
            }
        };
        var defaults = new ExplorerInitialDirectEditTreeItemLabelProvider() {
            @Override
            public IPayload handle(IEditingContext context, Tree tree, InitialDirectEditElementLabelInput input) {
                return new InitialDirectEditElementLabelSuccessPayload(input.id(), "Native label");
            }
        };
        var provider = new HedInitialDirectEditTreeItemLabelProvider(search, defaults);
        var tree = Tree.newTree("explorer://test").descriptionId("description").targetObjectId("home").children(List.of()).build();
        assertTrue(provider.canHandle(tree));
        assertFalse(provider.canHandle(Tree.newTree("other://test").descriptionId("description").targetObjectId("home").children(List.of()).build()));
        for (String name : new String[] { "Q1", "Q1 - Kitchen", "", null }) {
            device.setName(name);
            var input = new InitialDirectEditElementLabelInput(UUID.randomUUID(), "editing-context", tree.getId(), "device", "Composite label");
            var payload = assertInstanceOf(InitialDirectEditElementLabelSuccessPayload.class, provider.handle(null, tree, input));
            assertEquals(name == null ? "" : name, payload.initialDirectEditElementLabel());
            assertEquals(input.id(), payload.id());
        }
        var input = new InitialDirectEditElementLabelInput(UUID.randomUUID(), "editing-context", tree.getId(), "other", "Other label");
        var payload = assertInstanceOf(InitialDirectEditElementLabelSuccessPayload.class, provider.handle(null, tree, input));
        assertEquals("Native label", payload.initialDirectEditElementLabel());
    }
}
