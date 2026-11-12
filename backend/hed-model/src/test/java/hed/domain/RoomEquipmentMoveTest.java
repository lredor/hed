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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import hed.homeelectrical.ControlDevice;
import hed.homeelectrical.FieldEquipment;

/** Checks atomic room reassignment while keeping the devices, circuits, branches and functional controls intact. */
class RoomEquipmentMoveTest {
    @Test
    void movesLoadsOutletsAndControlsWithoutChangingOwnershipOrElectricalRelationships() {
        var home = HedSamples.example();
        var selected = List.of(home.getEquipment().get(0), home.getEquipment().get(1), home.getEquipment().get(4));
        var destination = home.getStoreys().getFirst().getRooms().get(1);
        var outlet = selected.get(1);
        var circuit = outlet.getCircuit();
        var branch = HedDomain.named(HedDomain.F.createCircuitBranch(), "Outlets branch");
        circuit.getBranches().add(branch);
        HedDomain.initializeFirstBranch(branch);
        var devices = List.copyOf(home.getEquipment());
        var types = selected.stream().map(FieldEquipment::getDeviceType).toList();
        var circuits = selected.stream().map(FieldEquipment::getCircuit).toList();
        var channels = List.copyOf(((ControlDevice) selected.get(2)).getChannels());
        var controls = List.copyOf(home.getControlFunctions());
        var channelLinks = controls.stream().map(control -> List.copyOf(control.getChannels())).toList();
        var targetLinks = controls.stream().map(control -> List.copyOf(control.getTargets())).toList();
        HedDomain.moveToRoom(destination, selected);
        assertEquals(devices, home.getEquipment());
        assertEquals(types, selected.stream().map(FieldEquipment::getDeviceType).toList());
        assertEquals(circuits, selected.stream().map(FieldEquipment::getCircuit).toList());
        assertEquals(channels, ((ControlDevice) selected.get(2)).getChannels());
        assertEquals(controls, home.getControlFunctions());
        assertEquals(channelLinks, controls.stream().map(control -> List.copyOf(control.getChannels())).toList());
        assertEquals(targetLinks, controls.stream().map(control -> List.copyOf(control.getTargets())).toList());
        assertSame(branch, outlet.getBranch());
        assertEquals(List.of(outlet), branch.getEquipment());
        for (var device : selected) {
            assertSame(destination, device.getRoom());
            assertSame(home, device.eContainer());
        }
        assertEquals(List.of(), new HedValidation().validate(home));
    }

    @Test
    void allowsOtherStoreysUnlocatedDevicesAndRepeatedDropsWithoutDuplicates() {
        var home = HedSamples.example();
        var equipment = home.getEquipment().getFirst();
        var destination = home.getStoreys().get(1).getRooms().getFirst();
        var before = List.copyOf(home.getEquipment());
        equipment.setRoom(null);
        HedDomain.moveToRoom(destination, List.of(equipment, equipment));
        HedDomain.moveToRoom(destination, List.of(equipment));
        assertSame(destination, equipment.getRoom());
        assertEquals(before, home.getEquipment());
    }

    @Test
    void rejectsForeignDetachedAndNullSourcesBeforeChangingAnyDevice() {
        var home = HedSamples.example();
        var equipment = home.getEquipment().getFirst();
        var previousRoom = equipment.getRoom();
        var destination = home.getStoreys().getFirst().getRooms().get(1);
        for (var invalid : Arrays.asList(HedSamples.example().getEquipment().getFirst(), HedDomain.F.createOutlet(), null)) {
            assertThrows(IllegalArgumentException.class, () -> HedDomain.moveToRoom(destination, Arrays.asList(equipment, invalid)));
            assertSame(previousRoom, equipment.getRoom());
        }
        assertEquals("drop.roomEquipment", assertThrows(IllegalArgumentException.class, () -> HedDomain.moveToRoom(destination, List.of())).getMessage());
        assertEquals("drop.roomEquipment", assertThrows(IllegalArgumentException.class, () -> HedDomain.moveToRoom(destination, null)).getMessage());
        assertEquals("foreignReference",
                assertThrows(IllegalArgumentException.class, () -> HedDomain.moveToRoom(destination, List.of(equipment, HedSamples.example().getEquipment().getFirst()))).getMessage());
        assertSame(previousRoom, equipment.getRoom());
    }

    @Test
    void rejectsDetachedDestinationsWithoutChangingTheSourceRoom() {
        var home = HedSamples.example();
        var equipment = home.getEquipment().getFirst();
        var previousRoom = equipment.getRoom();
        var detached = HedDomain.F.createRoom();
        var detachedStoreyRoom = HedDomain.F.createRoom();
        HedDomain.F.createStorey().getRooms().add(detachedStoreyRoom);
        for (var destination : Arrays.asList(null, detached, detachedStoreyRoom)) {
            assertEquals("drop.catalogTarget", assertThrows(IllegalArgumentException.class, () -> HedDomain.moveToRoom(destination, List.of(equipment))).getMessage());
            assertSame(previousRoom, equipment.getRoom());
        }
    }
}
