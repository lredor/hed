/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import hed.homeelectrical.DeviceKind;

/** Checks explorer subgroup ordering without altering containment, unrelated siblings or electrical associations. */
class ExplorerElementOrderTest {
    @Test
    void storeysRoomsAndFunctionalControlsMoveOnlyWithinTheirOwnParent() {
        var home = HedSamples.example();
        var ground = home.getStoreys().getFirst();
        var upper = home.getStoreys().get(1);
        var living = ground.getRooms().getFirst();
        var garage = ground.getRooms().get(1);
        var control = home.getControlFunctions().getFirst();
        var channels = List.copyOf(control.getChannels());
        HedDomain.reorder(ground, 1);
        assertEquals(List.of(upper, ground), home.getStoreys());
        assertEquals(0, ground.getLevelNumber());
        assertFalse(HedDomain.canReorder(upper, -1));
        HedDomain.placeElements(List.of(living), null);
        assertEquals(List.of(garage, living), ground.getRooms());
        assertThrows(IllegalArgumentException.class, () -> HedDomain.placeElements(List.of(living), upper.getRooms().getFirst()));
        HedDomain.placeElements(List.of(control), null);
        assertSame(control, home.getControlFunctions().getLast());
        assertEquals(channels, control.getChannels());
        assertSame(ground, living.eContainer());
    }

    @Test
    void catalogGroupsRetainOtherCategoryPositionsAndRejectCrossCategoryDrops() {
        var home = HedSamples.example();
        var terminal = HedDomain.type(home, DeviceKind.OUTLET);
        var otherTerminal = HedDomain.type(home, DeviceKind.LUMINAIRE);
        var panel = HedDomain.type(home, DeviceKind.MCB);
        var catalog = home.getCatalog().getDeviceTypes();
        catalog.move(1, panel);
        var before = List.copyOf(catalog);
        var panelIndex = catalog.indexOf(panel);
        HedDomain.placeElements(List.of(terminal), null);
        assertSame(panel, catalog.get(panelIndex));
        assertSame(terminal, HedDomain.orderingPeers(terminal).getLast());
        assertThrows(IllegalArgumentException.class, () -> HedDomain.placeElements(List.of(panel), otherTerminal));
        var panelOrder = HedDomain.orderingPeers(panel);
        HedDomain.reorder(panel, 1);
        assertSame(panel, HedDomain.orderingPeers(panel).get(1));
        assertEquals(panelOrder.size(), HedDomain.orderingPeers(panel).size());
        assertEquals(before.size(), catalog.size());
    }

    @Test
    void roomEquipmentOrderPreservesOtherRoomSlotsAndAllConnections() {
        var home = HedSamples.example();
        var first = home.getEquipment().getFirst();
        var peers = HedDomain.orderingPeers(first);
        var last = peers.getLast();
        var otherRoomDevice = home.getEquipment().get(3);
        var otherIndex = home.getEquipment().indexOf(otherRoomDevice);
        var original = List.copyOf(home.getEquipment());
        var rooms = original.stream().map(device -> device.getRoom()).toList();
        var circuits = original.stream().map(device -> device.getCircuit()).toList();
        var types = original.stream().map(device -> device.getDeviceType()).toList();
        HedDomain.placeElements(List.of(last), first);
        assertSame(last, HedDomain.orderingPeers(first).getFirst());
        assertSame(otherRoomDevice, home.getEquipment().get(otherIndex));
        assertEquals(rooms, original.stream().map(device -> device.getRoom()).toList());
        assertEquals(circuits, original.stream().map(device -> device.getCircuit()).toList());
        assertEquals(types, original.stream().map(device -> device.getDeviceType()).toList());
        var before = List.copyOf(home.getEquipment());
        assertThrows(IllegalArgumentException.class, () -> HedDomain.placeElements(List.of(first, otherRoomDevice), null));
        assertEquals(before, home.getEquipment());
    }

    @Test
    void multipleSelectionsKeepModelOrderAndInvalidSelectionsAreAtomic() {
        var home = HedSamples.example();
        var first = home.getEquipment().getFirst();
        var peers = HedDomain.orderingPeers(first);
        var second = peers.get(1);
        HedDomain.placeElements(List.of(second, first), null);
        var expected = new ArrayList<>(peers.subList(2, peers.size()));
        expected.addAll(List.of(first, second));
        assertEquals(expected, HedDomain.orderingPeers(first));
        var before = List.copyOf(home.getEquipment());
        assertThrows(IllegalArgumentException.class, () -> HedDomain.placeElements(List.of(first, HedSamples.example().getEquipment().getFirst()), null));
        assertEquals(before, home.getEquipment());
        assertThrows(IllegalArgumentException.class, () -> HedDomain.placeElements(List.of(), null));
        assertFalse(HedDomain.canReorder(HedDomain.F.createRoom(), 1));
    }
}
