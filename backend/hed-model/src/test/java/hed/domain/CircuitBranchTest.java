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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static hed.domain.HedDomain.F;
import static hed.domain.HedDomain.addEquipmentOnBranch;
import static hed.domain.HedDomain.assignToBranch;
import static hed.domain.HedDomain.delete;
import static hed.domain.HedDomain.named;
import static hed.domain.HedDomain.set;
import static hed.domain.HedDomain.type;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import hed.homeelectrical.CircuitBranch;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.FinalCircuit;

class CircuitBranchTest {
    private CircuitBranch branch(FinalCircuit circuit, String name) {
        var branch = named(F.createCircuitBranch(), name);
        circuit.getBranches().add(branch);
        return branch;
    }

    @Test
    void assigningBranchesKeepsRoomEquipmentAndControlsAndUsesTheirCommonCircuit() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().get(0);
        var first = branch(circuit, "Window side");
        var second = branch(circuit, "Door side");
        var light = home.getEquipment().get(0);
        var outlet = home.getEquipment().get(1);
        var room = outlet.getRoom();
        var controls = List.copyOf(home.getControlFunctions());
        set(light, "branch", first);
        set(second, "equipment", List.of(outlet));
        assertSame(circuit, light.getCircuit());
        assertSame(circuit, outlet.getCircuit());
        assertSame(second, outlet.getBranch());
        assertEquals(List.of(outlet), second.getEquipment());
        assertSame(room, outlet.getRoom());
        assertSame(home, outlet.eContainer());
        assertEquals(controls, home.getControlFunctions());
        assertSame(outlet, controls.get(1).getTargets().get(0));
        assertEquals(List.of(), new HedValidation().validate(home));
    }

    @Test
    void changingCircuitDetachesAnIncompatibleBranchAndRemovingTheBranchKeepsTheCircuit() {
        var home = HedSamples.example();
        var a = (FinalCircuit) home.getCircuits().get(0);
        var b = (FinalCircuit) home.getCircuits().get(1);
        var branch = branch(a, "A");
        var equipment = home.getEquipment().get(0);
        set(equipment, "branch", branch);
        set(equipment, "circuit", b);
        assertNull(equipment.getBranch());
        assertTrue(branch.getEquipment().isEmpty());
        assertSame(b, equipment.getCircuit());
        set(equipment, "branch", branch);
        assertSame(a, equipment.getCircuit());
        set(equipment, "branch", null);
        assertSame(a, equipment.getCircuit());
        assertTrue(branch.getEquipment().isEmpty());
    }

    @Test
    void movingAWholeBranchReassignsItsEquipmentWithoutMovingTheirRooms() {
        var home = HedSamples.example();
        var source = (FinalCircuit) home.getCircuits().get(0);
        var target = (FinalCircuit) home.getCircuits().get(1);
        var branch = branch(source, "Office");
        var equipment = home.getEquipment().subList(0, 2);
        var rooms = equipment.stream().map(FieldEquipment::getRoom).toList();
        assignToBranch(branch, equipment);
        set(branch, "circuit", target);
        assertTrue(source.getBranches().isEmpty());
        assertEquals(List.of(branch), target.getBranches());
        for (int i = 0; i < equipment.size(); i++) {
            assertSame(target, equipment.get(i).getCircuit());
            assertSame(branch, equipment.get(i).getBranch());
            assertSame(rooms.get(i), equipment.get(i).getRoom());
        }
        assertThrows(IllegalArgumentException.class, () -> set(branch, "circuit", null));
        assertSame(target, branch.getCircuit());
        assertEquals(List.of(), new HedValidation().validate(home));
    }

    @Test
    void foreignSelectionsAreRejectedAtomicallyAndFailedEditsRestoreRelatedOrder() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().get(0);
        var a = branch(circuit, "A");
        var b = branch(circuit, "B");
        var one = home.getEquipment().get(0);
        var two = home.getEquipment().get(1);
        var three = home.getEquipment().get(2);
        assignToBranch(a, List.of(one, two));
        assignToBranch(b, List.of(three));
        var foreign = HedSamples.example().getEquipment().get(0);
        var foreignCircuit = foreign.getCircuit();
        assertThrows(IllegalArgumentException.class, () -> assignToBranch(b, List.of(two, foreign)));
        assertSame(a, two.getBranch());
        assertSame(foreignCircuit, foreign.getCircuit());
        assertNull(foreign.getBranch());
        one.setRatedPowerW(-1d);
        assertThrows(IllegalArgumentException.class, () -> assignToBranch(b, List.of(two)));
        assertEquals(List.of(one, two), a.getEquipment());
        assertEquals(List.of(three), b.getEquipment());
        assertSame(a, two.getBranch());
        assertSame(circuit, two.getCircuit());
        var otherCircuit = (FinalCircuit) home.getCircuits().get(1);
        assertThrows(IllegalArgumentException.class, () -> set(a, "circuit", otherCircuit));
        assertSame(circuit, a.getCircuit());
        assertEquals(List.of(a, b), circuit.getBranches());
        assertTrue(otherCircuit.getBranches().isEmpty());
        assertEquals(List.of(one, two), a.getEquipment());
    }

    @Test
    void deletingABranchKeepsItsEquipmentOnTheCircuitAndDeletingTheCircuitKeepsTheEquipment() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().get(0);
        var a = branch(circuit, "A");
        var equipment = home.getEquipment().get(0);
        var room = equipment.getRoom();
        set(equipment, "branch", a);
        delete(a);
        assertNull(equipment.getBranch());
        assertSame(circuit, equipment.getCircuit());
        assertSame(room, equipment.getRoom());
        assertTrue(home.getEquipment().contains(equipment));
        var b = branch(circuit, "B");
        set(equipment, "branch", b);
        delete(circuit);
        assertNull(equipment.getBranch());
        assertNull(equipment.getCircuit());
        assertSame(room, equipment.getRoom());
        assertTrue(home.getEquipment().contains(equipment));
        assertSame(equipment, home.getControlFunctions().get(0).getTargets().get(0));
    }

    @Test
    void branchesAreOptionalAndInconsistentLinksAreDiagnosed() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().get(0);
        assertTrue(circuit.getBranches().isEmpty());
        assertEquals(List.of(), new HedValidation().validate(home));
        var branch = branch(circuit, "A");
        var equipment = home.getEquipment().get(1);
        equipment.setBranch(branch);
        assertTrue(new HedValidation().validate(home).stream().anyMatch(issue -> issue.code().equals("invalidBranch") && issue.target() == equipment));
    }

    @Test
    void refusedCreationOnABranchLeavesNoUnassignedEquipmentBehind() {
        var home = HedSamples.example();
        var branch = branch((FinalCircuit) home.getCircuits().get(0), "A");
        var before = List.copyOf(home.getEquipment());
        home.getEquipment().get(0).setRatedPowerW(-1d);
        assertThrows(IllegalArgumentException.class, () -> addEquipmentOnBranch(branch, type(home, DeviceKind.OUTLET), Locale.FRENCH));
        assertEquals(before, home.getEquipment());
        assertTrue(branch.getEquipment().isEmpty());
    }
}
