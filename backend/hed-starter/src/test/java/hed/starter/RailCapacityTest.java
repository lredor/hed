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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.junit.jupiter.api.Test;
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.Dwelling;
import hed.starter.representations.modelexplorer.HedDomainEditing;
import hed.starter.representations.modelexplorer.HedJavaServices;

/** Checks that rail creation menus expose every fitting catalog variant and disappear only when none fits. */
class RailCapacityTest {
    private final BootstrapIntegrationTest.Identity identity = new BootstrapIntegrationTest.Identity();

    private final BootstrapIntegrationTest.Feedback feedback = new BootstrapIntegrationTest.Feedback();

    private final IObjectSearchService search = new IObjectSearchService() {
        @Override
        public Optional<Object> getObject(IEditingContext context, String id) {
            return identity.ids.entrySet().stream().filter(entry -> entry.getValue().equals(id)).map(Map.Entry::getKey).findFirst();
        }
    };

    private final HedJavaServices services = new HedJavaServices(identity, search, feedback, Locale.ENGLISH, Dwelling::getName);

    private final HedDomainEditing editing = new HedDomainEditing(services, identity, Locale.ENGLISH, feedback);

    /** Builds a one-module rail whose default MCB is too wide but whose second MCB variant fits exactly. */
    private Dwelling homeWithCompactVariant() {
        var home = HedSamples.empty("Capacity fixture");
        var defaultType = HedDomain.type(home, DeviceKind.MCB);
        home.getCatalog().getDeviceTypes().removeIf(type -> type != defaultType);
        defaultType.setWidthModules(2d);
        var compact = EcoreUtil.copy(defaultType);
        compact.setName("Compact MCB");
        compact.setWidthModules(1d);
        home.getCatalog().getDeviceTypes().add(compact);
        var board = HedDomain.named(HedDomain.F.createDistributionBoard(), "Board");
        var rail = HedDomain.named(HedDomain.F.createDINRail(), "Rail");
        rail.setCapacityModules(1);
        board.getRails().add(rail);
        home.getBoards().add(board);
        return home;
    }

    @Test
    void nativeDialogRemainsAvailableWhenOnlyACustomVariantFits() {
        var home = homeWithCompactVariant();
        var rail = home.getBoards().get(0).getRails().get(0);
        var compact = home.getCatalog().getDeviceTypes().get(1);
        assertFalse(services.canCreateHedObject(rail, "MCB"));
        assertTrue(services.canAddHedObject(rail));
        var choices = editing.getChildCreationDescriptions(null, identity.getId(rail), "");
        assertEquals(List.of("hed-type:" + identity.getId(compact)), choices.stream().map(choice -> choice.id()).toList());
        assertSame(home.getCatalog().getDeviceTypes().get(0), HedDomain.type(home, DeviceKind.MCB));
    }

    @Test
    void fillingTheRailHidesCreationAndRejectsAStaleCustomChoice() {
        var home = homeWithCompactVariant();
        var rail = home.getBoards().get(0).getRails().get(0);
        var compact = home.getCatalog().getDeviceTypes().get(1);
        var choice = "hed-type:" + identity.getId(compact);
        var created = editing.createChild(null, rail, choice).orElseThrow();
        assertSame(rail.getDevices().get(0), created);
        assertEquals(1d, HedDomain.usedModules(rail));
        assertFalse(services.canAddHedObject(rail));
        assertTrue(editing.getChildCreationDescriptions(null, identity.getId(rail), "").isEmpty());
        assertTrue(editing.createChild(null, rail, choice).isEmpty());
        assertEquals(List.of(created), rail.getDevices());
        assertEquals(1, feedback.messages.size());
    }

    @Test
    void missingOrNonmodularCatalogChoicesDoNotEnableRailCreation() {
        var home = homeWithCompactVariant();
        var rail = home.getBoards().get(0).getRails().get(0);
        home.getCatalog().getDeviceTypes().forEach(type -> type.setKind(DeviceKind.OUTLET));
        assertFalse(services.canAddHedObject(rail));
        assertTrue(editing.getChildCreationDescriptions(null, identity.getId(rail), "").isEmpty());
        home.setCatalog(null);
        assertFalse(services.canAddHedObject(rail));
        assertFalse(services.canAddHedObject(HedDomain.F.createDINRail()));
    }
}
