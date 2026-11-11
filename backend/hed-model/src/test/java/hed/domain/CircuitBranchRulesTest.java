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
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import hed.homeelectrical.CircuitBranch;
import hed.homeelectrical.FinalCircuit;

/** Verifies that direct and branch-based circuit assignments cannot silently coexist through domain editing. */
class CircuitBranchRulesTest {
    private CircuitBranch branch(FinalCircuit circuit, String name) {
        var branch = HedDomain.named(HedDomain.F.createCircuitBranch(), name);
        circuit.getBranches().add(branch);
        HedDomain.initializeFirstBranch(branch);
        return branch;
    }

    @Test
    void directAssignmentsToBranchedCircuitsAreRejectedWithoutChangingExistingLinks() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var branch = branch(circuit, "Lighting branch");
        var members = List.copyOf(branch.getEquipment());
        var equipment = home.getEquipment().get(3);
        var originalCircuit = equipment.getCircuit();
        var room = equipment.getRoom();
        var failure = assertThrows(IllegalArgumentException.class, () -> HedDomain.set(equipment, "circuit", circuit));
        assertEquals("requiredBranch", failure.getMessage());
        assertSame(originalCircuit, equipment.getCircuit());
        assertSame(room, equipment.getRoom());
        assertNull(equipment.getBranch());
        assertEquals(members, branch.getEquipment());
        HedDomain.set(equipment, "branch", branch);
        assertSame(circuit, equipment.getCircuit());
        assertSame(branch, equipment.getBranch());
    }

    @Test
    void removingBranchMembershipClearsTheSupplyThroughEitherEditingDirection() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var branch = branch(circuit, "Lighting branch");
        var equipment = home.getEquipment().getFirst();
        var room = equipment.getRoom();
        HedDomain.set(equipment, "branch", null);
        assertNull(equipment.getCircuit());
        HedDomain.assignToBranch(branch, List.of(equipment));
        var remaining = new ArrayList<>(branch.getEquipment());
        remaining.remove(equipment);
        HedDomain.set(branch, "equipment", remaining);
        assertNull(equipment.getBranch());
        assertNull(equipment.getCircuit());
        assertSame(room, equipment.getRoom());
        assertEquals(remaining, HedDomain.served(circuit));
    }

    @Test
    void deletingOneBranchDisconnectsItsMembersAndDeletingTheLastRestoresDirectMode() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var first = branch(circuit, "First");
        var second = branch(circuit, "Second");
        var removed = List.copyOf(first.getEquipment());
        var outlet = home.getEquipment().get(1);
        HedDomain.assignToBranch(second, List.of(outlet));
        HedDomain.delete(first);
        for (var equipment : removed) {
            assertNull(equipment.getBranch());
            assertNull(equipment.getCircuit());
            assertTrue(home.getEquipment().contains(equipment));
        }
        assertEquals(List.of(outlet), HedDomain.served(circuit));
        HedDomain.delete(second);
        assertSame(circuit, outlet.getCircuit());
        assertNull(outlet.getBranch());
        assertTrue(circuit.getBranches().isEmpty());
    }

    @Test
    void movingTheFirstBranchGroupsTheDestinationCircuitsExistingDirectEquipment() {
        var home = HedSamples.example();
        var source = (FinalCircuit) home.getCircuits().getFirst();
        var target = (FinalCircuit) home.getCircuits().get(1);
        var branch = branch(source, "Moved branch");
        var expected = new ArrayList<>(branch.getEquipment());
        expected.addAll(HedDomain.served(target));
        HedDomain.set(branch, "circuit", target);
        assertEquals(expected, branch.getEquipment());
        assertTrue(source.getBranches().isEmpty());
        for (var equipment : expected) {
            assertSame(target, equipment.getCircuit());
            assertSame(branch, equipment.getBranch());
        }
        assertTrue(new HedValidation().validate(home).stream().noneMatch(issue -> issue.severity() == HedValidation.Severity.ERROR));
    }

    @Test
    void validationDiagnosesAnImportedDirectAssignmentToABranchedCircuit() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        branch(circuit, "Lighting branch");
        var equipment = home.getEquipment().get(3);
        equipment.setCircuit(circuit);
        assertTrue(new HedValidation().validate(home).stream().anyMatch(issue -> issue.target() == equipment && issue.code().equals("requiredBranch")));
    }
}
