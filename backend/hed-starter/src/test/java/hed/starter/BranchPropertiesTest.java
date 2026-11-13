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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.forms.description.AbstractControlDescription;
import org.eclipse.sirius.components.forms.description.IfDescription;
import org.eclipse.sirius.components.forms.description.PageDescription;
import org.eclipse.sirius.components.forms.description.SelectDescription;
import org.eclipse.sirius.components.representations.Failure;
import org.eclipse.sirius.components.representations.Success;
import org.eclipse.sirius.components.representations.VariableManager;
import org.junit.jupiter.api.Test;
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.homeelectrical.CircuitPhase;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.SupplyPhase;

/** Verifies contextual branch availability and live circuit assignments in Details. */
class BranchPropertiesTest {
    private final BootstrapIntegrationTest.Identity identity = new BootstrapIntegrationTest.Identity();

    private VariableManager variables(EObject object) {
        var variables = new VariableManager();
        variables.put(VariableManager.SELF, object);
        return variables;
    }

    private List<PageDescription> pages(EObject object, Locale locale) {
        var descriptions = new ArrayList<PageDescription>();
        BootstrapIntegrationTest.properties(identity, locale).addPropertiesDescriptions(descriptions::add);
        return descriptions.stream().filter(page -> page.getCanCreatePredicate().test(variables(object))).toList();
    }

    private List<AbstractControlDescription> controls(PageDescription page, VariableManager variables) {
        return page.getGroupDescriptions().stream().flatMap(group -> group.getControlDescriptions().stream())
                .flatMap(control -> control instanceof IfDescription condition
                        ? condition.getPredicate().apply(variables) ? condition.getControlDescriptions().stream() : java.util.stream.Stream.empty()
                        : java.util.stream.Stream.of(control))
                .toList();
    }

    private SelectDescription branch(EObject object, Locale locale) {
        return controls(pages(object, locale).getFirst(), variables(object)).stream().filter(SelectDescription.class::isInstance).map(SelectDescription.class::cast)
                .filter(control -> control.getId().endsWith(".branch")).findFirst().orElseThrow();
    }

    @Test
    void branchlessCircuitDisablesTheSelectorAndExplainsItsEmptyValueRegardlessOfPhase() {
        for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            var home = HedSamples.example(locale);
            var device = home.getEquipment().get(1);
            var circuit = device.getCircuit();
            var variables = variables(device);
            var selector = branch(device, locale);
            var otherCircuit = (FinalCircuit) home.getCircuits().getFirst();
            var foreignBranch = HedDomain.F.createCircuitBranch();
            otherCircuit.getBranches().add(foreignBranch);
            for (var supply : SupplyPhase.values()) {
                home.getServiceConnection().setPhase(supply);
                for (var phase : CircuitPhase.values()) {
                    circuit.setPhase(phase);
                    assertTrue(selector.getIsReadOnlyProvider().apply(variables));
                    assertEquals(List.of(""), selector.getOptionsProvider().apply(variables));
                    assertEquals("", selector.getValueProvider().apply(variables));
                    variables.put("candidate", "");
                    assertEquals(locale == Locale.FRENCH ? "Circuit sans branche" : "Circuit without branches", selector.getOptionLabelProvider().apply(variables));
                }
            }
            assertInstanceOf(Failure.class, selector.getNewValueHandler().apply(variables, identity.getId(foreignBranch)));
            assertSame(circuit, device.getCircuit());
            assertNull(device.getBranch());
        }
    }

    @Test
    void creatingAndDeletingBranchesUpdatesTheSameSelectorAndUnassignedEquipmentCanChooseABranch() {
        for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            var home = HedSamples.example(locale);
            var device = home.getEquipment().get(1);
            var circuit = device.getCircuit();
            var variables = variables(device);
            var selector = branch(device, locale);
            var first = HedDomain.named(HedDomain.F.createCircuitBranch(), "Window side");
            circuit.getBranches().add(first);
            HedDomain.initializeFirstBranch(first);
            assertFalse(selector.getIsReadOnlyProvider().apply(variables));
            assertTrue(selector.getOptionsProvider().apply(variables).contains(first));
            assertEquals(identity.getId(first), selector.getValueProvider().apply(variables));
            variables.put("candidate", first);
            assertEquals("Window side", selector.getOptionLabelProvider().apply(variables));
            HedDomain.delete(first);
            assertTrue(selector.getIsReadOnlyProvider().apply(variables));
            assertEquals("", selector.getValueProvider().apply(variables));
            assertInstanceOf(Failure.class, selector.getNewValueHandler().apply(variables, identity.getId(first)));
            var next = HedDomain.F.createCircuitBranch();
            circuit.getBranches().add(next);
            HedDomain.initializeFirstBranch(next);
            HedDomain.set(device, "circuit", null);
            assertNull(device.getCircuit());
            assertFalse(selector.getIsReadOnlyProvider().apply(variables));
            assertTrue(selector.getOptionsProvider().apply(variables).contains(next));
            assertInstanceOf(Success.class, selector.getNewValueHandler().apply(variables, identity.getId(next)));
            assertSame(next, device.getBranch());
            assertSame(circuit, device.getCircuit());
        }
    }
}
