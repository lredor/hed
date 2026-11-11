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

import java.util.ArrayList;
import java.util.Locale;
import org.eclipse.sirius.components.view.UserColor;
import org.eclipse.sirius.components.view.builder.generated.diagram.DiagramBuilders;
import org.eclipse.sirius.components.view.builder.generated.view.ViewBuilders;
import org.eclipse.sirius.components.view.diagram.ArrangeLayoutDirection;
import org.eclipse.sirius.components.view.diagram.ArrowStyle;
import org.eclipse.sirius.components.view.diagram.DiagramDescription;
import org.eclipse.sirius.components.view.diagram.DiagramLayoutOption;
import org.eclipse.sirius.components.view.diagram.EdgeDescription;
import org.eclipse.sirius.components.view.diagram.EdgeType;
import org.eclipse.sirius.components.view.diagram.InsideLabelPosition;
import org.eclipse.sirius.components.view.diagram.LineStyle;
import org.eclipse.sirius.components.view.diagram.NodeDescription;
import org.eclipse.sirius.components.view.diagram.NodePalette;
import org.eclipse.sirius.components.view.diagram.OutsideLabelPosition;
import org.eclipse.sirius.components.view.diagram.SynchronizationPolicy;
import org.eclipse.sirius.components.view.diagram.UserResizableDirection;
import hed.domain.Messages;
import hed.starter.HedDomainIcons;

/** A native, freely arranged logical view of the dwelling's circuits and controls. */
public final class CircuitDiagram {
    private final Locale locale;

    private final DiagramBuilders b = new DiagramBuilders();

    private final ViewBuilders v = new ViewBuilders();

    /**
     * Creates the native circuit-diagram description builder.
     * @param locale selects representation and tool translations
     */
    public CircuitDiagram(Locale locale) {
        this.locale = locale;
    }

    /**
     * Builds a synchronized logical diagram on a dwelling, using free-form containers and shared pictograms.
     * Positions belong to the representation; moving a node does not edit its room or circuit assignment.
     * @return a new description, to append after existing representations so their identifiers remain stable
     */
    public DiagramDescription create() {
        var channel = pictogram("channel", "ControlChannel", "aql:self.getHedCircuitChannels()", 16);
        var equipment = pictogram("equipment", "FieldEquipment", "aql:self.getHedCircuitRoomEquipment()", 24);
        equipment.getBorderNodesDescriptions().add(channel);
        var unlocated = pictogram("unlocated-equipment", "FieldEquipment", "aql:self.equipment->select(e | e.room = null)", 24);
        unlocated.getReusedBorderNodeDescriptions().add(channel);
        var room = container("room", "Room", "aql:self.rooms", "#ffffff", equipment);
        var storey = container("storey", "Storey", "aql:self.storeys", "#eff6ff", room);
        var modular = pictogram("modular", "ModularDevice", "aql:self.devices", 24);
        var circuit = pictogram("circuit", "Circuit", "aql:self.getHedCircuitBoardCircuits()", 24);
        var unattached = pictogram("unattached-circuit", "Circuit", "aql:self.circuits->select(c | c.sourceBoard = null)", 24);
        var branch = pictogram("branch", "CircuitBranch", "aql:self.getHedCircuitBranches()", 24);
        var unattachedBranch = pictogram("unattached-branch", "CircuitBranch", "aql:self.getHedCircuitBranches()", 24);
        // Append branch descriptions to preserve the paths and IDs of existing saved nodes and edges.
        var board = container("board", "DistributionBoard", "aql:self.boards", "#fff7ed", modular, circuit, branch);
        var meter = pictogram("meter", "EnergyMeter", "aql:self.meter", 24);
        var serviceBreaker = pictogram("service-breaker", "ServiceCircuitBreaker", "aql:self.mainBreaker", 24);
        var supply = container("supply", "ServiceConnection", "aql:self.serviceConnection", "#f8fafc", meter, serviceBreaker);
        NodeDescription[] terminals = { equipment, unlocated }, circuits = { circuit, unattached };
        var edges = new ArrayList<EdgeDescription>();
        // Domain-based edges project recorded links only; a multi-channel control keeps distinct endpoints.
        edges.add(edge("meter-supply", "ServiceConnection", "aql:self.serviceConnection", "aql:self.meter", "aql:self.mainBreaker", new NodeDescription[] { meter },
                new NodeDescription[] { serviceBreaker }, false, ""));
        edges.add(edge("service-protection", "ModularDevice", "aql:self.boards.devices", "aql:self.upstreamServiceBreaker", "aql:self", new NodeDescription[] { serviceBreaker },
                new NodeDescription[] { modular }, false, ""));
        edges.add(edge("residual-protection", "ModularDevice", "aql:self.boards.devices", "aql:self.upstreamRccb", "aql:self", new NodeDescription[] { modular }, new NodeDescription[] { modular },
                false, ""));
        edges.add(edge("circuit-protection", "Circuit", "aql:self.circuits", "aql:self.protectiveDevice", "aql:self", new NodeDescription[] { modular }, circuits, false, ""));
        edges.add(edge("circuit-equipment", "FieldEquipment", "aql:self.equipment->select(e | e.branch = null)", "aql:self.circuit", "aql:self", circuits, terminals, false, ""));
        edges.add(edge("board-feeder", "DistributionCircuit", "aql:self.circuits->filter(homeelectrical::DistributionCircuit)", "aql:self", "aql:self.targetBoard", circuits,
                new NodeDescription[] { board }, false, ""));
        edges.add(edge("direct-control", "ControlFunction", "aql:self.controlFunctions->select(f | f.actuator = null)", "aql:self.channels", "aql:self.targets", new NodeDescription[] { channel },
                terminals, true, "aql:self.name"));
        edges.add(edge("actuator-control", "ControlFunction", "aql:self.controlFunctions->select(f | f.actuator <> null)", "aql:self.channels", "aql:self.actuator", new NodeDescription[] { channel },
                new NodeDescription[] { modular }, true, "aql:self.name"));
        edges.add(edge("actuator-target", "ControlFunction", "aql:self.controlFunctions->select(f | f.actuator <> null)", "aql:self.actuator", "aql:self.targets", new NodeDescription[] { modular },
                terminals, true, "aql:self.name"));
        NodeDescription[] branches = { branch, unattachedBranch };
        edges.add(edge("circuit-branch", "CircuitBranch", "aql:self.circuits->filter(homeelectrical::FinalCircuit).branches", "aql:self.circuit", "aql:self", circuits, branches, false, ""));
        edges.add(edge("branch-equipment", "FieldEquipment", "aql:self.equipment->select(e | e.branch <> null)", "aql:self.branch", "aql:self", branches, terminals, false, ""));
        return b.newDiagramDescription().name(Messages.text(locale, "diagram.circuits")).domainType("homeelectrical::Dwelling").preconditionExpression("aql:true")
                .titleExpression(Messages.text(locale, "diagram.circuits")).iconExpression(HedDomainIcons.concept("Circuit")).description(Messages.text(locale, "diagram.circuits.description"))
                .endUserDocumentation(Messages.text(locale, "diagram.circuits.description")).style(b.newDiagramStyleDescription().background(color("#ffffff")).build())
                .layoutOption(DiagramLayoutOption.AUTO_UNTIL_MANUAL).arrangeLayoutDirection(ArrangeLayoutDirection.RIGHT)
                .nodeDescriptions(storey, board, supply, unlocated, unattached, unattachedBranch).edgeDescriptions(edges.toArray(EdgeDescription[]::new)).build();
    }

    private UserColor color(String value) {
        return v.newFixedColor().name("hed-circuits-" + value.substring(1)).value(value).build();
    }

    private NodePalette palette() {
        return b.newNodePalette().deleteTool(b.newDeleteTool().name("Delete").preconditionExpression("aql:false").build())
                .labelEditTool(b.newLabelEditTool().name("Rename").preconditionExpression("aql:false").build()).build();
    }

    private NodeDescription pictogram(String name, String type, String candidates, int size) {
        return b.newNodeDescription().name("hed-circuits-" + name).domainType("homeelectrical::" + type).semanticCandidatesExpression(candidates).preconditionExpression("aql:true")
                .synchronizationPolicy(SynchronizationPolicy.SYNCHRONIZED).defaultWidthExpression(Integer.toString(size)).defaultHeightExpression(Integer.toString(size))
                .userResizable(UserResizableDirection.NONE).style(b.newImageNodeStyleDescription().shape("hed-circuits:icon").borderSize(0).build()).palette(palette())
                .outsideLabels(b.newOutsideLabelDescription().labelExpression("aql:self.getHedCircuitNodeLabel()").position(OutsideLabelPosition.BOTTOM_CENTER).style(
                        b.newOutsideLabelStyle().fontSize(12).labelColor(color("#0f172a")).background(color("#ffffff")).borderSize(0).showIconExpression("aql:false").maxWidthExpression("220").build())
                        .build())
                .build();
    }

    private NodeDescription container(String name, String type, String candidates, String background, NodeDescription... children) {
        return b.newNodeDescription().name("hed-circuits-" + name).domainType("homeelectrical::" + type).semanticCandidatesExpression(candidates).preconditionExpression("aql:true")
                .synchronizationPolicy(SynchronizationPolicy.SYNCHRONIZED).userResizable(UserResizableDirection.BOTH)
                .style(b.newRectangularNodeStyleDescription().background(color(background)).borderColor(color("#94a3b8")).borderSize(1).borderRadius(4)
                        .childrenLayoutStrategy(b.newFreeFormLayoutStrategyDescription().build()).build())
                .palette(palette()).childrenDescriptions(children)
                .insideLabel(b.newInsideLabelDescription().labelExpression("aql:self.getHedCircuitNodeLabel()").position(InsideLabelPosition.TOP_LEFT).style(b.newInsideLabelStyle().fontSize(14)
                        .bold(true).labelColor(color("#0f172a")).borderSize(0).withHeader(true).showIconExpression("aql:true").labelIcon(HedDomainIcons.concept(type)).build()).build())
                .build();
    }

    private EdgeDescription edge(String name, String type, String candidates, String source, String target, NodeDescription[] sources, NodeDescription[] targets, boolean control, String label) {
        return b.newEdgeDescription().name("hed-circuits-" + name).domainType("homeelectrical::" + type).semanticCandidatesExpression(candidates).preconditionExpression("aql:true")
                .synchronizationPolicy(SynchronizationPolicy.SYNCHRONIZED).isDomainBasedEdge(true).sourceExpression(source).targetExpression(target).sourceDescriptions(sources)
                .targetDescriptions(targets).centerLabelExpression(label)
                .style(b.newEdgeStyle().edgeType(EdgeType.MANHATTAN).lineStyle(control ? LineStyle.DASH : LineStyle.SOLID).color(color(control ? "#7c3aed" : "#334155")).background(color("#ffffff"))
                        .borderSize(0).edgeWidth(1).targetArrowStyle(ArrowStyle.INPUT_ARROW).showIcon(false).fontSize(11).build())
                .palette(b.newEdgePalette().deleteTool(b.newDeleteTool().name("Delete").preconditionExpression("aql:false").build()).build()).build();
    }
}
