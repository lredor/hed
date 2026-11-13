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
import java.util.ArrayList;
import java.util.Locale;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.forms.description.IfDescription;
import org.eclipse.sirius.components.forms.description.PageDescription;
import org.eclipse.sirius.components.forms.description.SelectDescription;
import org.eclipse.sirius.components.representations.Success;
import org.eclipse.sirius.components.representations.VariableManager;
import org.junit.jupiter.api.Test;
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.homeelectrical.CircuitPhase;
import hed.homeelectrical.SupplyPhase;
import hed.starter.representations.modelexplorer.HedJavaServices;

/** Verifies supply-dependent circuit controls and retention of phase assignments while those controls are hidden. */
class CircuitPhaseVisibilityTest {
    private final BootstrapIntegrationTest.Identity identity = new BootstrapIntegrationTest.Identity();

    private final HedJavaServices services = new HedJavaServices(identity, new IObjectSearchService.NoOp(), new BootstrapIntegrationTest.Feedback(), Locale.ENGLISH, home -> home.getName());

    private VariableManager variables(EObject object) {
        var variables = new VariableManager();
        variables.put(VariableManager.SELF, object);
        return variables;
    }

    /** Finds the actual generated phase condition on the properties page selected for this semantic type. */
    private IfDescription phaseCondition(EObject object) {
        var pages = new ArrayList<PageDescription>();
        BootstrapIntegrationTest.properties(identity, Locale.ENGLISH).addPropertiesDescriptions(pages::add);
        var variables = variables(object);
        var page = pages.stream().filter(candidate -> candidate.getCanCreatePredicate().test(variables)).findFirst().orElseThrow();
        return page.getGroupDescriptions().stream().flatMap(group -> group.getControlDescriptions().stream()).filter(IfDescription.class::isInstance).map(IfDescription.class::cast).filter(
                condition -> condition.getControlDescriptions().stream().anyMatch(control -> control instanceof SelectDescription select && select.getIdProvider().apply(variables).endsWith(".phase")))
                .findFirst().orElseThrow();
    }

    @Test
    void bothCircuitKindsUseTheLiveDwellingSupplyForTheirPhaseVisibility() {
        var home = HedSamples.empty("Supply fixture");
        var finalCircuit = HedDomain.F.createFinalCircuit();
        var distributionCircuit = HedDomain.F.createDistributionCircuit();
        home.getCircuits().add(finalCircuit);
        home.getCircuits().add(distributionCircuit);
        var finalCondition = phaseCondition(finalCircuit);
        var distributionCondition = phaseCondition(distributionCircuit);
        assertFalse(finalCondition.getPredicate().apply(variables(finalCircuit)));
        assertFalse(distributionCondition.getPredicate().apply(variables(distributionCircuit)));
        assertFalse(services.hasHedThreePhaseSupply(home));
        var connection = HedDomain.F.createServiceConnection();
        home.setServiceConnection(connection);
        for (var phase : SupplyPhase.values()) {
            connection.setPhase(phase);
            boolean expected = phase == SupplyPhase.THREE_PHASE;
            assertEquals(expected, finalCondition.getPredicate().apply(variables(finalCircuit)));
            assertEquals(expected, distributionCondition.getPredicate().apply(variables(distributionCircuit)));
            assertEquals(expected, services.hasHedThreePhaseSupply(home));
            assertEquals(expected, services.hasHedThreePhaseSupply(finalCircuit));
            assertEquals(expected, services.hasHedThreePhaseSupply(distributionCircuit));
        }
        home.setServiceConnection(null);
        assertFalse(finalCondition.getPredicate().apply(variables(finalCircuit)));
        assertFalse(distributionCondition.getPredicate().apply(variables(distributionCircuit)));
    }

    @Test
    void changingTheAlwaysAvailableSupplySelectorRetainsHiddenCircuitValues() {
        var home = HedSamples.empty("Supply fixture");
        var circuit = HedDomain.named(HedDomain.F.createFinalCircuit(), "Lighting");
        circuit.setPhase(CircuitPhase.L2);
        home.getCircuits().add(circuit);
        var connection = HedDomain.named(HedDomain.F.createServiceConnection(), "Supply");
        home.setServiceConnection(connection);
        var supplyCondition = phaseCondition(connection);
        var supplySelector = assertInstanceOf(SelectDescription.class, supplyCondition.getControlDescriptions().getFirst());
        var circuitCondition = phaseCondition(circuit);
        for (var phase : SupplyPhase.values()) {
            assertTrue(supplyCondition.getPredicate().apply(variables(connection)));
            assertInstanceOf(Success.class, supplySelector.getNewValueHandler().apply(variables(connection), phase.getLiteral()));
            assertEquals(phase == SupplyPhase.THREE_PHASE, circuitCondition.getPredicate().apply(variables(circuit)));
            assertEquals(CircuitPhase.L2, circuit.getPhase());
        }
    }
}
