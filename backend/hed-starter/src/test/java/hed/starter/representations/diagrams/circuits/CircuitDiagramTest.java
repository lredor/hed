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
import org.eclipse.sirius.components.view.diagram.RectangularNodeStyleDescription;
import org.eclipse.sirius.components.view.diagram.ListLayoutStrategyDescription;
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.domain.HedValidation;
import hed.homeelectrical.DINRail;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.ControlChannel;
import hed.homeelectrical.ControlDevice;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.DistributionCircuit;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.Equipment;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.Room;
import hed.homeelectrical.ServiceConnection;
import hed.homeelectrical.Storey;

/** Evaluates the actual diagram AQL against complete and incomplete models to verify logical connection coverage. */
class CircuitDiagramTest {
    /** One directed semantic link projected by an edge description. */
    record Link(String kind, EObject owner, EObject source, EObject target, String label) {
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
                                var expression = description.getCenterLabelExpression();
                                var label = expression.isBlank() ? "" : interpreter.evaluateExpression(Map.of("self", candidate), expression).asString().orElse("");
                                links.add(new Link(description.getName(), candidate, source, target, label));
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
        var expected = HedDomain.contents(home).stream().filter(object -> object instanceof Storey || object instanceof Room || object instanceof DistributionBoard || object instanceof DINRail
                || object instanceof FinalCircuit circuit && !circuit.getBranches().isEmpty() || object instanceof Equipment
                || object instanceof ControlChannel channel && ((ControlDevice) channel.eContainer()).getChannels().size() > 1 || object instanceof ServiceConnection).toList();
        assertEquals(new HashSet<>(expected), projection.nodes.keySet());
    }

    @Test
    void existingModelProvidesEverySupplyCircuitAndIndependentControlEndpoint() {
        var home = HedSamples.example();
        var projection = new Projection(home);
        completeCoverage(home, projection);
        assertEquals(27, projection.nodes.size());
        assertEquals(18, projection.links.size());
        var circuit = home.getCircuits().get(0);
        var light = home.getEquipment().get(0);
        assertFalse(projection.nodes.containsKey(circuit));
        assertTrue(projection.connects(circuit.getProtectiveDevice(), light));
        assertTrue(projection.connects(light, home.getEquipment().get(4)));
        assertTrue(projection.connects(home.getEquipment().get(4), home.getEquipment().get(5)));
        assertFalse(projection.connects(circuit.getProtectiveDevice(), home.getEquipment().get(5)));
        assertTrue(projection.links.stream().filter(link -> link.kind().equals("hed-circuits-circuit-start") || link.kind().equals("hed-circuits-equipment-chain"))
                .filter(link -> link.target() == light || link.target() == home.getEquipment().get(4) || link.target() == home.getEquipment().get(5)).allMatch(link -> circuit.getName().equals(link.label())));
        var lighting = home.getControlFunctions().get(0);
        assertEquals(2, lighting.getChannels().size());
        for (var channel : lighting.getChannels()) {
            assertTrue(projection.connects(((ControlDevice) channel.eContainer()).getChannels().size() == 1 ? channel.eContainer() : channel, light));
        }
        var socket = home.getEquipment().get(1);
        assertTrue(projection.connects(home.getControlFunctions().get(1).getChannels().get(0), socket));
        assertFalse(projection.connects(lighting.getChannels().get(0), socket));
        var feeder = (DistributionCircuit) home.getCircuits().get(4);
        assertTrue(projection.connects(feeder.getProtectiveDevice(), feeder.getTargetBoard()));
        assertTrue(projection.links.stream().anyMatch(link -> link.owner() == feeder && link.label().equals(feeder.getName())));
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
        assertEquals(19, projection.links.size());
        for (var channel : function.getChannels()) {
            var source = ((ControlDevice) channel.eContainer()).getChannels().size() == 1 ? channel.eContainer() : channel;
            assertTrue(projection.connects(source, actuator));
            assertFalse(projection.links.stream().anyMatch(link -> link.kind().equals("hed-circuits-direct-control") && link.source() == source && link.target() == function.getTargets().get(0)));
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
        assertFalse(projection.nodes.containsKey(circuit));
        assertFalse(projection.connects(circuit, equipment));
        assertTrue(new HedValidation().validate(home).stream().anyMatch(issue -> issue.target() == equipment && issue.code().equals("missingCircuit")));
    }

    @Test
    void chainOrderFollowsModelOrderAndNamesStayVerbatim() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var service = new CircuitServices(Locale.FRENCH);
        var equipment = home.getEquipment().getFirst();
        var second = home.getEquipment().get(4);
        var third = home.getEquipment().get(5);
        assertEquals(List.of(equipment), service.getHedCircuitFirstEquipment(circuit));
        assertEquals(List.of(equipment), service.getHedCircuitPreviousEquipment(second));
        assertEquals(List.of(second), service.getHedCircuitPreviousEquipment(third));
        home.getEquipment().move(0, third);
        var projection = new Projection(home);
        assertTrue(projection.connects(circuit.getProtectiveDevice(), third));
        assertTrue(projection.connects(third, equipment));
        assertTrue(projection.connects(equipment, second));
        assertFalse(projection.connects(second, third));
        circuit.setName("Éclairage séjour");
        assertEquals("Éclairage séjour", service.getHedCircuitChainLabel(second));
        equipment.setName(null);
        assertEquals("", service.getHedCircuitNodeLabel(equipment));
        circuit.setName(null);
        assertEquals("", service.getHedCircuitChainLabel(second));
    }

    @Test
    void branchesReplaceDirectSupplyEdgesWithoutDuplicatingEquipmentOrChangingControlLinks() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var branch = HedDomain.named(HedDomain.F.createCircuitBranch(), "Window side");
        circuit.getBranches().add(branch);
        HedDomain.initializeFirstBranch(branch);
        var equipment = home.getEquipment().getFirst();
        var second = home.getEquipment().get(4);
        var third = home.getEquipment().get(5);
        HedDomain.assignToBranch(branch, List.of(second, equipment));
        branch.getEquipment().move(0, second);
        var other = HedDomain.named(HedDomain.F.createCircuitBranch(), "Door side");
        circuit.getBranches().add(other);
        HedDomain.assignToBranch(other, List.of(third));
        var projection = new Projection(home);
        completeCoverage(home, projection);
        assertEquals(28, projection.nodes.size());
        assertFalse(projection.nodes.containsKey(branch));
        assertTrue(projection.connects(circuit.getProtectiveDevice(), circuit));
        assertTrue(projection.connects(circuit, second));
        assertTrue(projection.connects(second, equipment));
        assertTrue(projection.connects(circuit, third));
        assertFalse(projection.connects(equipment, third));
        assertTrue(projection.links.stream().anyMatch(link -> link.owner() == branch && link.label().equals("Window side")));
        assertTrue(projection.links.stream().anyMatch(link -> link.source() == second && link.target() == equipment && link.label().equals("Window side")));
        assertTrue(projection.connects(home.getControlFunctions().getFirst().getChannels().getFirst(), equipment));
        var junction = projection.nodes.get(circuit);
        assertEquals("hed-circuits-junction", junction.getName());
        assertEquals("3", junction.getDefaultWidthExpression());
        assertEquals("3", junction.getDefaultHeightExpression());
        assertNull(junction.getInsideLabel());
        assertTrue(junction.getOutsideLabels().isEmpty());
        var style = (RectangularNodeStyleDescription) junction.getStyle();
        assertEquals(0, style.getBorderSize());
        assertEquals("#000000", ((org.eclipse.sirius.components.view.FixedColor) style.getBackground()).getValue());
        branch.getEquipment().move(0, equipment);
        var reordered = new Projection(home);
        assertTrue(reordered.connects(circuit, equipment));
        assertTrue(reordered.connects(equipment, second));
        circuit.setSourceBoard(null);
        var unattached = new Projection(home);
        completeCoverage(home, unattached);
        assertTrue(unattached.connects(circuit, equipment));
    }

    @Test
    void emptyBranchesAndMissingProtectionNeverInventAnEndpoint() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var empty = HedDomain.F.createCircuitBranch();
        circuit.getBranches().add(empty);
        circuit.setProtectiveDevice(null);
        var projection = new Projection(home);
        assertTrue(projection.nodes.containsKey(circuit));
        assertTrue(projection.links.stream().noneMatch(link -> link.owner() == empty || link.owner() == circuit));
        // A stale direct assignment must not bypass the rule requiring a branch in branched mode.
        assertTrue(projection.links.stream().noneMatch(link -> link.kind().equals("hed-circuits-equipment-chain") && link.target() == home.getEquipment().get(4)));
        assertEquals(List.of(), new CircuitServices(Locale.ENGLISH).getHedCircuitFirstEquipment(empty));
        circuit.getBranches().clear();
        var direct = new Projection(home);
        assertFalse(direct.nodes.containsKey(circuit));
        assertTrue(direct.connects(home.getEquipment().getFirst(), home.getEquipment().get(4)));
    }

    @Test
    void railsContainOnlyTheirOrderedTallDevicesAndCatalogWidthsAreUsed() {
        var home = HedSamples.example();
        var rail = home.getBoards().getFirst().getRails().getFirst();
        var projection = new Projection(home);
        assertEquals("hed-circuits-rail", projection.nodes.get(rail).getName());
        var description = projection.nodes.get(rail).getChildrenDescriptions().getFirst();
        assertEquals("112", description.getDefaultHeightExpression());
        assertEquals(List.copyOf(rail.getDevices()), projection.evaluate(rail, description.getSemanticCandidatesExpression()));
        var services = new CircuitServices(Locale.ENGLISH);
        assertEquals(64, services.getHedCircuitDeviceWidth(rail.getDevices().getFirst()));
        assertEquals(32, services.getHedCircuitDeviceWidth(rail.getDevices().get(1)));
        assertEquals(232, services.getHedCircuitRailWidth(rail));
        assertEquals(234, services.getHedCircuitBoardWidth(home.getBoards().getFirst()));
        var boardStyle = (RectangularNodeStyleDescription) projection.nodes.get(home.getBoards().getFirst()).getStyle();
        var layout = org.junit.jupiter.api.Assertions.assertInstanceOf(ListLayoutStrategyDescription.class, boardStyle.getChildrenLayoutStrategy());
        assertEquals("aql:false", layout.getAreChildNodesDraggableExpression());
    }

    @Test
    void singleChannelControlsConnectThroughTheirParentAndAdditionalChannelsRestoreBorderEndpoints() {
        var home = HedSamples.example();
        var control = (ControlDevice) home.getEquipment().get(5);
        var function = home.getControlFunctions().getFirst();
        var channel = control.getChannels().getFirst();
        var projection = new Projection(home);
        assertFalse(projection.nodes.containsKey(channel));
        assertTrue(projection.links.stream().anyMatch(link -> link.owner() == function && link.source() == control));
        control.getChannels().add(HedDomain.named(HedDomain.F.createControlChannel(), "Second channel"));
        var multi = new Projection(home);
        assertTrue(multi.nodes.containsKey(channel));
        assertTrue(multi.links.stream().anyMatch(link -> link.owner() == function && link.source() == channel));
        assertFalse(multi.links.stream().anyMatch(link -> link.owner() == function && link.source() == control));
        control.getChannels().removeLast();
        assertFalse(new Projection(home).nodes.containsKey(channel));
    }
}
