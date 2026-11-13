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
import org.eclipse.sirius.components.view.diagram.UserResizableDirection;
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
        var expected = HedDomain.contents(home).stream()
                .filter(object -> object instanceof Storey || object instanceof Room || object instanceof DistributionBoard || object instanceof DINRail
                        || object instanceof FinalCircuit circuit && !circuit.getBranches().isEmpty() || object instanceof Equipment
                        || object instanceof ControlChannel channel && ((ControlDevice) channel.eContainer()).getChannels().size() > 1 || object instanceof ServiceConnection)
                .toList();
        assertEquals(new HashSet<>(expected), projection.nodes.keySet());
    }

    @Test
    void existingModelProvidesEverySupplyCircuitAndIndependentControlEndpoint() {
        var home = HedSamples.example();
        var projection = new Projection(home);
        completeCoverage(home, projection);
        assertEquals(27, projection.nodes.size());
        assertEquals(16, projection.links.size());
        var circuit = home.getCircuits().get(0);
        var light = home.getEquipment().get(0);
        assertFalse(projection.nodes.containsKey(circuit));
        assertTrue(projection.connects(circuit.getProtectiveDevice(), home.getEquipment().get(4)));
        assertFalse(projection.connects(circuit.getProtectiveDevice(), light));
        assertFalse(projection.connects(light, home.getEquipment().get(4)));
        assertTrue(projection.connects(home.getEquipment().get(4), home.getEquipment().get(5)));
        assertFalse(projection.connects(circuit.getProtectiveDevice(), home.getEquipment().get(5)));
        assertTrue(projection.links.stream().filter(link -> link.kind().equals("hed-circuits-circuit-start"))
                .filter(link -> link.target() == light || link.target() == home.getEquipment().get(4) || link.target() == home.getEquipment().get(5))
                .allMatch(link -> circuit.getName().equals(link.label())));
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
        assertEquals(17, projection.links.size());
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
        home.getControlFunctions().clear();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var service = new CircuitServices(Locale.FRENCH);
        var equipment = home.getEquipment().getFirst();
        var second = home.getEquipment().get(4);
        var third = home.getEquipment().get(5);
        assertEquals(List.of(equipment), service.getHedCircuitFirstEquipment(circuit));
        assertEquals(List.of(equipment), service.getHedCircuitPreviousEquipment(second));
        assertEquals(List.of(second), service.getHedCircuitPreviousEquipment(third));
        circuit.getEquipment().move(0, third);
        home.getEquipment().move(0, second);
        var projection = new Projection(home);
        assertTrue(projection.connects(circuit.getProtectiveDevice(), third));
        assertTrue(projection.connects(third, equipment));
        assertTrue(projection.connects(equipment, second));
        assertFalse(projection.connects(second, third));
        second.setRoom(home.getStoreys().get(1).getRooms().getFirst());
        circuit.setName("Éclairage séjour");
        assertEquals("Éclairage séjour", service.getHedCircuitChainLabel(second));
        equipment.setName(null);
        assertEquals("", service.getHedCircuitNodeLabel(equipment));
        circuit.setName(null);
        assertEquals("", service.getHedCircuitChainLabel(second));
    }

    @Test
    void continuationLabelsAreHiddenOnlyWithinTheSameAssignedRoomForCircuitsAndBranches() {
        var home = HedSamples.example();
        home.getControlFunctions().clear();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var first = home.getEquipment().getFirst();
        var second = home.getEquipment().get(4);
        var third = home.getEquipment().get(5);
        var room = first.getRoom();
        var otherRoom = home.getStoreys().get(1).getRooms().getFirst();
        otherRoom.setName(room.getName()); // Room identity matters even when their display names are equal.
        var services = new CircuitServices(Locale.ENGLISH);
        assertEquals("", services.getHedCircuitChainLabel(second));
        assertEquals("", services.getHedCircuitChainLabel(third));
        second.setRoom(otherRoom);
        assertEquals(circuit.getName(), services.getHedCircuitChainLabel(second));
        assertEquals(circuit.getName(), services.getHedCircuitChainLabel(third));
        // Missing rooms are unknown locations, not a shared room.
        first.setRoom(null);
        second.setRoom(null);
        assertEquals(circuit.getName(), services.getHedCircuitChainLabel(second));
        var branch = HedDomain.named(HedDomain.F.createCircuitBranch(), "Window side");
        circuit.getBranches().add(branch);
        HedDomain.initializeFirstBranch(branch);
        assertEquals(branch.getName(), services.getHedCircuitChainLabel(second));
        first.setRoom(room);
        second.setRoom(room);
        assertEquals("", services.getHedCircuitChainLabel(second));
        third.setRoom(otherRoom);
        assertEquals(branch.getName(), services.getHedCircuitChainLabel(third));
        var projection = new Projection(home);
        assertTrue(projection.links.stream().anyMatch(link -> link.kind().equals("hed-circuits-circuit-junction") && link.label().equals(circuit.getName())));
        assertTrue(projection.links.stream().anyMatch(link -> link.kind().equals("hed-circuits-branch-start") && link.label().equals(branch.getName())));
    }

    @Test
    void branchesReplaceDirectSupplyEdgesWithoutDuplicatingEquipmentOrChangingControlLinks() {
        var home = HedSamples.example();
        home.getControlFunctions().getFirst().getTargets().clear();
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
        assertTrue(projection.links.stream().anyMatch(link -> link.source() == second && link.target() == equipment && link.label().isEmpty()));
        assertTrue(projection.connects(home.getControlFunctions().get(1).getChannels().getFirst(), home.getEquipment().get(1)));
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
        assertTrue(direct.connects(home.getEquipment().get(4), home.getEquipment().get(5)));
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
        assertEquals(128, services.getHedCircuitDeviceWidth(rail.getDevices().getFirst()));
        assertEquals(64, services.getHedCircuitDeviceWidth(rail.getDevices().get(1)));
        assertEquals(424, services.getHedCircuitRailWidth(rail));
        assertEquals(426, services.getHedCircuitBoardWidth(home.getBoards().getFirst()));
        assertEquals("48", projection.nodes.get(home.getEquipment().getFirst()).getDefaultWidthExpression());
        assertEquals("48", projection.nodes.get(home.getEquipment().getFirst()).getDefaultHeightExpression());
        assertEquals("260", projection.nodes.get(rail).getDefaultHeightExpression());
        assertEquals(UserResizableDirection.VERTICAL, projection.nodes.get(rail).getUserResizable());
        var empty = HedDomain.F.createDINRail();
        home.getBoards().getFirst().getRails().add(empty);
        assertEquals(424, services.getHedCircuitRailWidth(empty));
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

    @Test
    void receiversOnTheirCommandsCircuitKeepOnlyControlEdgesWithoutChangingAssociations() {
        var home = HedSamples.example();
        var projection = new Projection(home);
        var light = home.getEquipment().getFirst();
        var shutter = home.getEquipment().get(2);
        for (var receiver : List.of(light, shutter)) {
            assertTrue(projection.nodes.containsKey(receiver));
            assertTrue(projection.links.stream().noneMatch(link -> (link.source() == receiver || link.target() == receiver) && (link.kind().equals("hed-circuits-circuit-start"))));
            assertTrue(projection.links.stream().anyMatch(link -> link.target() == receiver && link.kind().equals("hed-circuits-direct-control")));
            assertTrue(hed.starter.representations.CircuitEquipment.getAssociatedEquipment(receiver.getCircuit()).contains(receiver));
        }
        // The switched outlet belongs to another circuit, so its solid supply path remains necessary.
        var outlet = home.getEquipment().get(1);
        assertTrue(projection.connects(outlet.getCircuit().getProtectiveDevice(), outlet));
        var lighting = home.getControlFunctions().getFirst();
        lighting.getChannels().forEach(channel -> ((ControlDevice) channel.eContainer()).setCircuit(null));
        assertTrue(new Projection(home).connects(light.getCircuit().getProtectiveDevice(), light));
    }

    @Test
    void branchPathsSkipControlledReceiversAtEveryPositionAndRestoreThemWhenTheFunctionIsRemoved() {
        var home = HedSamples.empty("Controlled branch");
        var circuit = HedDomain.named(HedDomain.F.createFinalCircuit(), "Lighting");
        home.getCircuits().add(circuit);
        var first = HedDomain.addEquipment(home, HedDomain.type(home, DeviceKind.LUMINAIRE));
        var command = (ControlDevice) HedDomain.addEquipment(home, HedDomain.type(home, DeviceKind.SWITCH));
        var middle = HedDomain.addEquipment(home, HedDomain.type(home, DeviceKind.LUMINAIRE));
        var second = HedDomain.addEquipment(home, HedDomain.type(home, DeviceKind.SWITCH));
        var last = HedDomain.addEquipment(home, HedDomain.type(home, DeviceKind.SHUTTER_MOTOR));
        home.getEquipment().forEach(device -> device.setCircuit(circuit));
        var branch = HedDomain.named(HedDomain.F.createCircuitBranch(), "Room supply");
        circuit.getBranches().add(branch);
        HedDomain.initializeFirstBranch(branch);
        var function = HedDomain.named(HedDomain.F.createControlFunction(), "Command");
        function.getChannels().add(command.getChannels().getFirst());
        function.getTargets().addAll(List.of(first, middle, last));
        home.getControlFunctions().add(function);
        var projection = new Projection(home);
        assertTrue(projection.connects(circuit, command));
        assertTrue(projection.connects(command, second));
        assertFalse(projection.connects(circuit, first));
        assertFalse(projection.connects(middle, second));
        assertEquals(List.copyOf(home.getEquipment()), List.copyOf(branch.getEquipment()));
        var receiverBranch = HedDomain.named(HedDomain.F.createCircuitBranch(), "Receiver only");
        circuit.getBranches().add(receiverBranch);
        HedDomain.assignToBranch(receiverBranch, List.of(last));
        assertTrue(new CircuitServices(Locale.ENGLISH).getHedCircuitFirstEquipment(receiverBranch).isEmpty());
        assertFalse(new Projection(home).links.stream().anyMatch(link -> link.owner() == receiverBranch));
        home.getControlFunctions().remove(function);
        var restored = new Projection(home);
        assertTrue(restored.connects(circuit, first));
        assertTrue(restored.connects(first, command));
        assertTrue(restored.connects(command, middle));
        assertTrue(restored.connects(circuit, last));
    }
}
