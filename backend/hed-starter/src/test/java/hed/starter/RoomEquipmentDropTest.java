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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.domain.Messages;
import hed.homeelectrical.DeviceKind;
import hed.starter.representations.CatalogDropService;
import hed.starter.representations.diagrams.floorplan.FloorPlanServices;
import hed.starter.representations.modelexplorer.CatalogTreeDropHandler;
import hed.starter.representations.modelexplorer.HedJavaServices;

/** Verifies move-vs-create dispatch, stable explorer identity, floor-plan scope and localized atomic rejection. */
class RoomEquipmentDropTest {
    private IObjectSearchService search(Map<String, Object> objects) {
        return new IObjectSearchService() {
            @Override
            public Optional<Object> getObject(IEditingContext context, String id) {
                return Optional.ofNullable(objects.get(id));
            }
        };
    }

    private DropTreeItemInput input(List<String> sources, String target, int index) {
        return new DropTreeItemInput(UUID.randomUUID(), "context", "tree", sources, target, index);
    }

    @Test
    void movesExistingDevicesAndCreatesNewDevicesFromCatalogTypes() {
        var home = HedSamples.example();
        var sourceRoom = home.getStoreys().getFirst().getRooms().getFirst();
        var targetRoom = home.getStoreys().getFirst().getRooms().get(1);
        var device = home.getEquipment().getFirst();
        var before = List.copyOf(home.getEquipment());
        var search = search(Map.of("room", targetRoom, "device", device, "type", HedDomain.type(home, DeviceKind.OUTLET)));
        var feedback = new BootstrapIntegrationTest.Feedback();
        var handler = new CatalogTreeDropHandler(search, null, new CatalogDropService(search, feedback, Locale.ENGLISH),
                new HedJavaServices(new BootstrapIntegrationTest.Identity(), search, new BootstrapIntegrationTest.Feedback(), Locale.ENGLISH, dwelling -> dwelling.getName()), Locale.ENGLISH);
        var explorer = new HedJavaServices(new BootstrapIntegrationTest.Identity(), search, feedback, Locale.ENGLISH, dwelling -> dwelling.getName());
        var identity = explorer.getHedId(device);
        assertInstanceOf(Success.class, handler.handle(null, null, input(List.of("device"), "room", -1)));
        assertSame(targetRoom, device.getRoom());
        assertSame(targetRoom, explorer.getHedParent(device));
        assertTrue(explorer.getHedChildren(targetRoom).contains(device));
        assertFalse(explorer.getHedChildren(sourceRoom).contains(device));
        assertEquals(identity, explorer.getHedId(device));
        assertEquals(before, home.getEquipment());
        assertInstanceOf(Success.class, handler.handle(null, null, input(List.of("type"), "room", -1)));
        assertEquals(before.size() + 1, home.getEquipment().size());
        assertSame(targetRoom, home.getEquipment().getLast().getRoom());
    }

    @Test
    void movingBetweenStoreysChangesPlanEligibilityWithoutChangingDeviceIdentity() {
        var home = HedSamples.example();
        var ground = home.getStoreys().getFirst();
        var upper = home.getStoreys().get(1);
        var device = home.getEquipment().getFirst();
        var search = search(Map.of("room", upper.getRooms().getFirst(), "device", device));
        var handler = new CatalogTreeDropHandler(search, null, null,
                new HedJavaServices(new BootstrapIntegrationTest.Identity(), search, new BootstrapIntegrationTest.Feedback(), Locale.ENGLISH, dwelling -> dwelling.getName()), Locale.ENGLISH);
        assertTrue(FloorPlanServices.belongsTo(device, ground));
        assertFalse(FloorPlanServices.belongsTo(device, upper));
        assertInstanceOf(Success.class, handler.handle(null, null, input(List.of("device"), "room", -1)));
        assertFalse(FloorPlanServices.belongsTo(device, ground));
        assertTrue(FloorPlanServices.belongsTo(device, upper));
    }

    @Test
    void aMixedRoomSelectionCanMoveToARoomThatAlreadyContainsItsFirstDevice() {
        var home = HedSamples.example();
        var alreadyThere = home.getEquipment().getFirst();
        var incoming = home.getEquipment().get(3);
        var room = alreadyThere.getRoom();
        var original = List.copyOf(home.getEquipment());
        var search = search(Map.of("room", room, "first", alreadyThere, "incoming", incoming));
        var handler = new CatalogTreeDropHandler(search, null, null,
                new HedJavaServices(new BootstrapIntegrationTest.Identity(), search, new BootstrapIntegrationTest.Feedback(), Locale.ENGLISH, dwelling -> dwelling.getName()), Locale.ENGLISH);
        assertInstanceOf(Success.class, handler.handle(null, null, input(List.of("first", "incoming"), "room", -1)));
        assertSame(room, incoming.getRoom());
        assertSame(room, alreadyThere.getRoom());
        assertEquals(original, home.getEquipment());
    }

    @Test
    void rejectsMixedForeignStaleAndIndexedSelectionsInBothLanguages() {
        for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            var home = HedSamples.example(locale);
            var device = home.getEquipment().getFirst();
            var oldRoom = device.getRoom();
            var target = home.getStoreys().getFirst().getRooms().get(1);
            var search = search(Map.of("room", target, "device", device, "other", HedSamples.example().getEquipment().getFirst(), "type", HedDomain.type(home, DeviceKind.OUTLET)));
            var feedback = new BootstrapIntegrationTest.Feedback();
            var handler = new CatalogTreeDropHandler(search, null, new CatalogDropService(search, feedback, locale),
                    new HedJavaServices(new BootstrapIntegrationTest.Identity(), search, new BootstrapIntegrationTest.Feedback(), locale, dwelling -> dwelling.getName()), locale);
            assertEquals(Messages.text(locale, "drop.roomEquipment"),
                    assertInstanceOf(Failure.class, handler.handle(null, null, input(List.of("device", "type"), "room", -1))).getMessages().getFirst().body());
            assertEquals(Messages.text(locale, "foreignReference"),
                    assertInstanceOf(Failure.class, handler.handle(null, null, input(List.of("device", "other"), "room", -1))).getMessages().getFirst().body());
            for (var input : List.of(input(List.of("device", "missing"), "room", -1), input(List.of("device"), "room", 0), input(List.of("device"), "missing", -1))) {
                assertInstanceOf(Failure.class, handler.handle(null, null, input));
                assertSame(oldRoom, device.getRoom());
            }
            assertSame(oldRoom, device.getRoom());
        }
    }
}
