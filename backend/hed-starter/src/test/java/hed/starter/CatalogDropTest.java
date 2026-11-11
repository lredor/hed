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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.eclipse.sirius.components.collaborative.trees.dto.DropTreeItemInput;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.representations.Failure;
import org.eclipse.sirius.components.representations.Success;
import org.junit.jupiter.api.Test;
import hed.domain.HedSamples;
import hed.domain.HedValidation;
import hed.domain.Messages;
import hed.domain.HedDomain;
import hed.starter.representations.CatalogDropService;
import hed.starter.representations.modelexplorer.CatalogTreeDropHandler;
import hed.homeelectrical.DeviceKind;

/** Verifies catalog-drop creation, preserved references and rejection without partial explorer creation. */
class CatalogDropTest {
    private CatalogDropService service() {
        return new CatalogDropService(new IObjectSearchService.NoOp(), new BootstrapIntegrationTest.Feedback(), Locale.ENGLISH);
    }

    @Test
    void createsNewModularDevicesWithoutMovingCatalogTypesAndKeepsRailOrder() {
        var home = HedSamples.example();
        var rail = home.getBoards().get(0).getRails().get(0);
        var before = new ArrayList<>(rail.getDevices());
        var type = HedDomain.type(home, DeviceKind.MCB);
        int typeCount = home.getCatalog().getDeviceTypes().size();
        var created = service().create(rail, List.of(type, type));
        assertEquals(2, created.size());
        assertNotSame(created.get(0), created.get(1));
        assertEquals(before, rail.getDevices().subList(0, before.size()));
        assertEquals(created, rail.getDevices().subList(before.size(), rail.getDevices().size()));
        for (var device : created) {
            assertSame(type, device.getDeviceType());
            assertSame(rail, device.getRail());
            assertSame(rail.eContainer(), device.eContainer());
        }
        assertSame(home.getCatalog(), type.eContainer());
        assertEquals(typeCount, home.getCatalog().getDeviceTypes().size());
        HedDomain.set(type, "ratedCurrentA", 25d);
        for (var device : created) {
            assertEquals(25d, HedDomain.get(device, "ratedCurrentA"));
        }
        assertEquals(List.of(), new HedValidation().validate(home));
    }

    @Test
    void invalidOrForeignSelectionsAreRejectedBeforeAnythingIsCreated() {
        var home = HedSamples.example();
        var rail = home.getBoards().get(0).getRails().get(0);
        var before = new ArrayList<>(rail.getDevices());
        var type = HedDomain.type(home, DeviceKind.MCB);
        assertThrows(IllegalArgumentException.class, () -> service().create(rail, List.of(type, HedDomain.type(home, DeviceKind.OUTLET))));
        assertThrows(IllegalArgumentException.class, () -> service().create(rail, List.of(type, HedDomain.type(HedSamples.example(), DeviceKind.MCB))));
        assertThrows(IllegalArgumentException.class, () -> service().create(rail, List.of(HedDomain.type(home, DeviceKind.ENERGY_METER))));
        assertThrows(IllegalArgumentException.class, () -> service().create(rail, List.of(before.get(0))));
        assertThrows(IllegalArgumentException.class, () -> service().create(rail.eContainer(), List.of(type)));
        assertThrows(IllegalArgumentException.class, () -> service().create(rail, List.of()));
        assertEquals(before, rail.getDevices());
    }

    @Test
    void explorerHandlerCreatesAModularDeviceAndRejectsDroppingBetweenRows() {
        var home = HedSamples.example();
        var rail = home.getBoards().get(0).getRails().get(0);
        int count = rail.getDevices().size();
        var objects = Map.<String, Object> of("rail", rail, "type", HedDomain.type(home, DeviceKind.MCB));
        IObjectSearchService search = new IObjectSearchService() {
            @Override
            public Optional<Object> getObject(IEditingContext context, String id) {
                return Optional.ofNullable(objects.get(id));
            }
        };
        var handler = new CatalogTreeDropHandler(search, null, new CatalogDropService(search, new BootstrapIntegrationTest.Feedback(), Locale.ENGLISH), Locale.ENGLISH);
        var input = new DropTreeItemInput(UUID.randomUUID(), "context", "tree", List.of("type"), "rail", -1);
        assertInstanceOf(Success.class, handler.handle(null, null, input));
        assertEquals(count + 1, rail.getDevices().size());
        assertInstanceOf(Failure.class, handler.handle(null, null, new DropTreeItemInput(UUID.randomUUID(), "context", "tree", List.of("type"), "rail", 0)));
        assertInstanceOf(Failure.class, handler.handle(null, null, new DropTreeItemInput(UUID.randomUUID(), "context", "tree", List.of("missing"), "rail", -1)));
        assertEquals(count + 1, rail.getDevices().size());
    }

    @Test
    void rejectsAnOversizedSelectionBeforeCreatingAnyDevice() {
        var home = HedSamples.example();
        var rail = home.getBoards().get(0).getRails().get(0);
        var type = HedDomain.type(home, DeviceKind.MCB);
        var before = new ArrayList<>(rail.getDevices());
        var board = (hed.homeelectrical.DistributionBoard) rail.eContainer();
        int boardCount = board.getDevices().size();
        rail.setCapacityModules((int) HedDomain.usedModules(rail) + 1);
        assertEquals("railCapacity", assertThrows(IllegalArgumentException.class, () -> service().create(rail, List.of(type, type))).getMessage());
        assertEquals(before, rail.getDevices());
        assertEquals(boardCount, board.getDevices().size());
        service().create(rail, List.of(type));
        assertEquals(rail.getCapacityModules(), HedDomain.usedModules(rail));
        assertThrows(IllegalArgumentException.class, () -> service().create(rail, List.of(type)));
        assertEquals(boardCount + 1, board.getDevices().size());
    }

    @Test
    void diagramDropReportsALocalizedInvalidTargetWithoutChangingTheSource() {
        var home = HedSamples.example();
        var type = HedDomain.type(home, DeviceKind.MCB);
        var feedback = new BootstrapIntegrationTest.Feedback();
        var service = new CatalogDropService(new IObjectSearchService.NoOp(), feedback, Locale.FRENCH);
        assertSame(type, service.dropHedCatalogType(type, null, null));
        assertSame(home.getCatalog(), type.eContainer());
        assertEquals(Messages.text(Locale.FRENCH, "drop.catalogRail"), feedback.messages.get(0).body());
    }
}
