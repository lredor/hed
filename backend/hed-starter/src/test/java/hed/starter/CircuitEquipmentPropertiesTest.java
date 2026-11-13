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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.eclipse.sirius.components.widget.reference.ReferenceWidgetDescription;
import org.eclipse.sirius.components.forms.description.PageDescription;
import org.eclipse.sirius.components.forms.description.IfDescription;
import org.eclipse.sirius.components.forms.description.SelectDescription;
import org.eclipse.sirius.components.representations.Failure;
import org.eclipse.sirius.components.representations.Success;
import org.eclipse.sirius.components.representations.VariableManager;
import org.junit.jupiter.api.Test;
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.homeelectrical.FinalCircuit;
import hed.starter.representations.CircuitEquipment;

/** Checks editable direct circuit assignments and the live, protected union of optional branch members in Details. */
class CircuitEquipmentPropertiesTest {
    private final BootstrapIntegrationTest.Identity identity = new BootstrapIntegrationTest.Identity();

    private VariableManager variables(FinalCircuit circuit) {
        var variables = new VariableManager();
        variables.put(VariableManager.SELF, circuit);
        return variables;
    }

    private ReferenceWidgetDescription equipmentControl(FinalCircuit circuit) {
        var pages = new ArrayList<PageDescription>();
        BootstrapIntegrationTest.properties(identity, Locale.ENGLISH).addPropertiesDescriptions(pages::add);
        var variables = variables(circuit);
        return pages.stream().filter(page -> page.getCanCreatePredicate().test(variables)).flatMap(page -> page.getGroupDescriptions().stream())
                .flatMap(group -> group.getControlDescriptions().stream()).filter(ReferenceWidgetDescription.class::isInstance).map(ReferenceWidgetDescription.class::cast)
                .filter(control -> control.getIdProvider().apply(variables).equals("hed.FinalCircuit.equipment")).findFirst().orElseThrow();
    }

    @Test
    void directCircuitEquipmentIsEditableAndRetainsIndependentRooms() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().get(1);
        var equipment = home.getEquipment().get(0);
        var room = equipment.getRoom();
        var widget = equipmentControl(circuit);
        var variables = variables(circuit);
        assertFalse(widget.getIsReadOnlyProvider().apply(variables));
        variables.put("newValue", equipment);
        assertInstanceOf(Success.class, widget.getSetHandlerProvider().apply(variables));
        assertEquals(List.of(equipment), CircuitEquipment.getAssociatedEquipment(circuit));
        assertSame(circuit, equipment.getCircuit());
        assertSame(room, equipment.getRoom());
        assertEquals(List.of(equipment), widget.getItemsProvider().apply(variables));
        assertInstanceOf(Success.class, widget.getClearHandlerProvider().apply(variables));
        assertTrue(CircuitEquipment.getAssociatedEquipment(circuit).isEmpty());
    }

    @Test
    void branchesMakeTheListTheirOrderedUnionAndProtectStaleEditableForms() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().get(1);
        var variables = variables(circuit);
        var widget = equipmentControl(circuit);
        assertFalse(widget.getIsReadOnlyProvider().apply(variables));
        var first = HedDomain.named(HedDomain.F.createCircuitBranch(), "Window side");
        var second = HedDomain.named(HedDomain.F.createCircuitBranch(), "Door side");
        circuit.getBranches().add(first);
        HedDomain.initializeFirstBranch(first);
        circuit.getBranches().add(second);
        var one = home.getEquipment().get(1);
        var two = home.getEquipment().get(0);
        HedDomain.assignToBranch(first, List.of(one));
        HedDomain.assignToBranch(second, List.of(two));
        assertTrue(widget.getIsReadOnlyProvider().apply(variables));
        assertEquals(List.of(one, two), CircuitEquipment.getAssociatedEquipment(circuit));
        assertEquals(List.of(one, two), widget.getItemsProvider().apply(variables));
        assertInstanceOf(Failure.class, widget.getClearHandlerProvider().apply(variables));
        assertEquals(List.of(one), first.getEquipment());
        assertEquals(List.of(two), second.getEquipment());
        HedDomain.delete(first);
        assertEquals(List.of(two), CircuitEquipment.getAssociatedEquipment(circuit));
        HedDomain.delete(second);
        assertFalse(widget.getIsReadOnlyProvider().apply(variables));
        assertEquals(List.of(two), CircuitEquipment.getAssociatedEquipment(circuit));
    }

    @Test
    void invalidSelectionsAreRejectedBeforeAnyCircuitOrBranchMembershipChanges() {
        var home = HedSamples.example();
        var foreign = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().get(1);
        var before = CircuitEquipment.getAssociatedEquipment(circuit);
        assertThrows(IllegalArgumentException.class, () -> CircuitEquipment.setAssociatedEquipment(circuit, List.of(home.getEquipment().get(0), foreign.getEquipment().get(0))));
        assertEquals(before, CircuitEquipment.getAssociatedEquipment(circuit));
        var invalid = variables(circuit);
        invalid.put("newValue", List.of("unknown"));
        assertInstanceOf(Failure.class, equipmentControl(circuit).getAddHandlerProvider().apply(invalid));
        assertEquals(before, CircuitEquipment.getAssociatedEquipment(circuit));
    }

    @Test
    void nativeTransferDialogReordersMembersAndRejectsStaleIndices() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var widget = equipmentControl(circuit);
        var before = List.copyOf(CircuitEquipment.getAssociatedEquipment(circuit));
        var variables = variables(circuit);
        variables.put("item", before.getFirst());
        variables.put("fromIndex", 0);
        variables.put("toIndex", before.size() - 1);
        assertInstanceOf(Success.class, widget.getMoveHandlerProvider().apply(variables));
        var reordered = new ArrayList<>(before);
        reordered.add(reordered.removeFirst());
        assertEquals(reordered, CircuitEquipment.getAssociatedEquipment(circuit));
        assertInstanceOf(Failure.class, widget.getMoveHandlerProvider().apply(variables));
        assertEquals(reordered, CircuitEquipment.getAssociatedEquipment(circuit));
        variables.put("fromIndex", -1);
        assertInstanceOf(Failure.class, widget.getMoveHandlerProvider().apply(variables));
        var branch = HedDomain.F.createCircuitBranch();
        circuit.getBranches().add(branch);
        HedDomain.initializeFirstBranch(branch);
        variables.put("fromIndex", reordered.size() - 1);
        variables.put("toIndex", 0);
        assertInstanceOf(Failure.class, widget.getMoveHandlerProvider().apply(variables));
        assertEquals(reordered, branch.getEquipment());
    }

    @Test
    void branchUsesTheSameNativeOrderedReferenceEditor() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var branch = HedDomain.F.createCircuitBranch();
        circuit.getBranches().add(branch);
        HedDomain.initializeFirstBranch(branch);
        var pages = new ArrayList<PageDescription>();
        BootstrapIntegrationTest.properties(identity, Locale.ENGLISH).addPropertiesDescriptions(pages::add);
        var variables = new VariableManager();
        variables.put(VariableManager.SELF, branch);
        var widget = pages.stream().filter(page -> page.getCanCreatePredicate().test(variables)).flatMap(page -> page.getGroupDescriptions().stream())
                .flatMap(group -> group.getControlDescriptions().stream()).filter(IfDescription.class::isInstance).map(IfDescription.class::cast)
                .flatMap(condition -> condition.getControlDescriptions().stream()).filter(ReferenceWidgetDescription.class::isInstance).map(ReferenceWidgetDescription.class::cast).findFirst()
                .orElseThrow();
        var first = branch.getEquipment().getFirst();
        variables.put("item", first);
        variables.put("fromIndex", 0);
        variables.put("toIndex", 2);
        assertInstanceOf(Success.class, widget.getMoveHandlerProvider().apply(variables));
        assertSame(first, branch.getEquipment().getLast());
        assertEquals(branch.getEquipment(), CircuitEquipment.getAssociatedEquipment(circuit));
        assertInstanceOf(Success.class, widget.getItemRemoveHandlerProvider().apply(variables));
        assertNull(first.getCircuit());
        assertNull(first.getBranch());
        variables.put("newValue", List.of(first));
        assertInstanceOf(Success.class, widget.getAddHandlerProvider().apply(variables));
        assertSame(branch, first.getBranch());
        assertSame(circuit, first.getCircuit());
    }

    @Test
    void equipmentCircuitSelectorOmitsBranchedTargetsAndRejectsStaleSelections() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var equipment = home.getEquipment().get(3);
        var originalCircuit = equipment.getCircuit();
        var pages = new ArrayList<PageDescription>();
        BootstrapIntegrationTest.properties(identity, Locale.ENGLISH).addPropertiesDescriptions(pages::add);
        var variables = new VariableManager();
        variables.put(VariableManager.SELF, equipment);
        var selector = pages.stream().filter(page -> page.getCanCreatePredicate().test(variables)).flatMap(page -> page.getGroupDescriptions().stream())
                .flatMap(group -> group.getControlDescriptions().stream()).filter(IfDescription.class::isInstance).map(IfDescription.class::cast)
                .flatMap(conditional -> conditional.getControlDescriptions().stream()).filter(SelectDescription.class::isInstance).map(SelectDescription.class::cast)
                .filter(control -> control.getIdProvider().apply(variables).endsWith(".circuit")).findFirst().orElseThrow();
        assertTrue(selector.getOptionsProvider().apply(variables).contains(circuit));
        var branch = HedDomain.named(HedDomain.F.createCircuitBranch(), "Lighting branch");
        circuit.getBranches().add(branch);
        HedDomain.initializeFirstBranch(branch);
        assertFalse(selector.getOptionsProvider().apply(variables).contains(circuit));
        assertInstanceOf(Failure.class, selector.getNewValueHandler().apply(variables, identity.getId(circuit)));
        assertSame(originalCircuit, equipment.getCircuit());
        assertNull(equipment.getBranch());
        HedDomain.set(equipment, "branch", branch);
        assertTrue(selector.getOptionsProvider().apply(variables).contains(circuit));
        assertEquals(new java.util.HashSet<>(CircuitEquipment.getAssociatedEquipment(circuit)), new java.util.HashSet<>(HedDomain.served(circuit)));
    }
}
