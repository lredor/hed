/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.diagrams.circuits;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.interpreter.AQLInterpreter;
import org.eclipse.sirius.components.view.diagram.DiagramDescription;
import org.eclipse.sirius.components.view.diagram.NodeDescription;
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.domain.HedValidation;
import hed.homeelectrical.Circuit;
import hed.homeelectrical.CircuitBranch;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.ControlChannel;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.DistributionCircuit;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.Equipment;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.Room;
import hed.homeelectrical.ServiceConnection;
import hed.homeelectrical.Storey;
import hed.homeelectrical.SupplyPhase;

/** Evaluates the actual diagram AQL against complete and incomplete models to verify logical connection coverage. */
class CircuitDiagramTest {
    /** One directed semantic link projected by an edge description. */
    record Link(String kind, EObject source, EObject target) {
    }

    /** Evaluates candidate and endpoint expressions while rejecting duplicate semantic nodes. */
    private static final class Projection {
        final DiagramDescription diagram = new CircuitDiagram(Locale.ENGLISH).create();

        final AQLInterpreter interpreter = new AQLInterpreter(List.of(), List.of(new CircuitServices(Locale.ENGLISH)), List.of(HomeElectricalPackage.eINSTANCE));

        final Map<EObject, NodeDescription> nodes = new IdentityHashMap<>();

        final List<Link> links = new ArrayList<>();

        Projection(Dwelling home) {
            for (var description : diagram.getNodeDescriptions()) {
                nodes(description, home);
            }
            for (var description : diagram.getEdgeDescriptions()) {
                for (var candidate : evaluate(home, description.getSemanticCandidatesExpression())) {
                    for (var source : evaluate(candidate, description.getSourceExpression())) {
                        for (var target : evaluate(candidate, description.getTargetExpression())) {
                            if (description.getSourceDescriptions().contains(nodes.get(source)) && description.getTargetDescriptions().contains(nodes.get(target))) {
                                links.add(new Link(description.getName(), source, target));
                            }
                        }
                    }
                }
            }
        }

        List<EObject> evaluate(EObject self, String expression) {
            return interpreter.evaluateExpression(Map.of("self", self), expression).asObjects().orElse(List.of()).stream().map(EObject.class::cast).toList();
        }

        void nodes(NodeDescription description, EObject owner) {
            for (var candidate : evaluate(owner, description.getSemanticCandidatesExpression())) {
                assertNull(nodes.put(candidate, description), "An object must have only one view in this schematic");
                for (var child : description.getChildrenDescriptions()) {
                    nodes(child, candidate);
                }
                for (var child : description.getBorderNodesDescriptions()) {
                    nodes(child, candidate);
                }
                for (var child : description.getReusedBorderNodeDescriptions()) {
                    nodes(child, candidate);
                }
            }
        }

        boolean connects(EObject source, EObject target) {
            return links.stream().anyMatch(link -> link.source() == source && link.target() == target);
        }
    }

    private void completeCoverage(Dwelling home, Projection projection) {
        var expected = HedDomain.contents(home).stream().filter(object -> object instanceof Storey || object instanceof Room || object instanceof DistributionBoard || object instanceof Circuit
                || object instanceof CircuitBranch || object instanceof Equipment || object instanceof ControlChannel || object instanceof ServiceConnection).toList();
        assertEquals(new HashSet<>(expected), projection.nodes.keySet());
    }

    @Test
    void existingModelProvidesEverySupplyCircuitAndIndependentControlEndpoint() {
        var home = HedSamples.example();
        var projection = new Projection(home);
        completeCoverage(home, projection);
        assertEquals(32, projection.nodes.size());
        assertEquals(23, projection.links.size());
        var circuit = home.getCircuits().get(0);
        assertTrue(projection.connects(circuit.getProtectiveDevice(), circuit));
        var light = home.getEquipment().get(0);
        assertTrue(projection.connects(circuit, light));
        var lighting = home.getControlFunctions().get(0);
        assertEquals(2, lighting.getChannels().size());
        for (var channel : lighting.getChannels()) {
            assertTrue(projection.connects(channel, light));
        }
        var socket = home.getEquipment().get(1);
        assertTrue(projection.connects(home.getControlFunctions().get(1).getChannels().get(0), socket));
        assertFalse(projection.connects(lighting.getChannels().get(0), socket));
        var feeder = (DistributionCircuit) home.getCircuits().get(4);
        assertTrue(projection.connects(feeder, feeder.getTargetBoard()));
        assertTrue(projection.connects(home.getServiceConnection().getMeter(), home.getServiceConnection().getMainBreaker()));
    }

    @Test
    void anActuatorIsShownBetweenControlChannelsAndTheLoads() {
        var home = HedSamples.example();
        var function = home.getControlFunctions().get(0);
        var actuator = HedDomain.addModularDevice(home.getBoards().get(0).getRails().get(0), HedDomain.type(home, DeviceKind.IMPULSE_RELAY));
        function.setActuator(actuator);
        var projection = new Projection(home);
        completeCoverage(home, projection);
        assertEquals(24, projection.links.size());
        for (var channel : function.getChannels()) {
            assertTrue(projection.connects(channel, actuator));
            assertFalse(projection.connects(channel, function.getTargets().get(0)));
        }
        assertTrue(projection.connects(actuator, function.getTargets().get(0)));
        assertEquals(List.of(), new HedValidation().validate(home));
    }

    @Test
    void incompleteAssignmentsRemainVisibleWithoutInventedConnections() {
        var home = HedSamples.example();
        var equipment = home.getEquipment().get(0);
        equipment.setRoom(null);
        equipment.setCircuit(null);
        var circuit = home.getCircuits().get(0);
        circuit.setProtectiveDevice(null);
        circuit.setSourceBoard(null);
        home.setServiceConnection(null);
        var projection = new Projection(home);
        completeCoverage(home, projection);
        assertEquals("hed-circuits-unlocated-equipment", projection.nodes.get(equipment).getName());
        assertEquals("hed-circuits-unattached-circuit", projection.nodes.get(circuit).getName());
        assertFalse(projection.connects(circuit, equipment));
        assertTrue(new HedValidation().validate(home).stream().anyMatch(issue -> issue.target() == equipment && issue.code().equals("missingCircuit")));
    }

    @Test
    void circuitLabelsShowStoredNameSectionAndOnlyApplicablePhase() {
        var home = HedSamples.example();
        var circuit = home.getCircuits().get(0);
        circuit.setConductorSectionMm2(1.5);
        var service = new CircuitServices(Locale.FRENCH);
        assertEquals("Ground floor lighting\n1.5 mm²", service.getHedCircuitNodeLabel(circuit));
        home.getServiceConnection().setPhase(SupplyPhase.THREE_PHASE);
        assertTrue(service.getHedCircuitNodeLabel(circuit).endsWith(" · L1"));
        assertEquals("Ground floor lighting", circuit.getName());
        var equipment = home.getEquipment().get(0);
        equipment.setName(null);
        assertEquals("", service.getHedCircuitNodeLabel(equipment));
    }

    @Test
    void branchesReplaceDirectSupplyEdgesWithoutDuplicatingEquipmentOrChangingControlLinks() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var branch = HedDomain.named(HedDomain.F.createCircuitBranch(), "Window side");
        circuit.getBranches().add(branch);
        HedDomain.initializeFirstBranch(branch);
        var equipment = home.getEquipment().getFirst();
        HedDomain.assignToBranch(branch, List.of(equipment));
        var projection = new Projection(home);
        completeCoverage(home, projection);
        assertEquals(33, projection.nodes.size());
        assertTrue(projection.connects(circuit, branch));
        assertTrue(projection.connects(branch, equipment));
        assertFalse(projection.connects(circuit, equipment));
        assertTrue(projection.connects(home.getControlFunctions().getFirst().getChannels().getFirst(), equipment));
        assertEquals("hed-circuits-branch", projection.nodes.get(branch).getName());
        circuit.setSourceBoard(null);
        var unattached = new Projection(home);
        completeCoverage(home, unattached);
        assertEquals("hed-circuits-unattached-branch", unattached.nodes.get(branch).getName());
        assertTrue(unattached.connects(circuit, branch));
        assertTrue(unattached.connects(branch, equipment));
        assertEquals(List.of(branch), new CircuitServices(Locale.ENGLISH).getHedCircuitBranches(circuit));
    }
}
