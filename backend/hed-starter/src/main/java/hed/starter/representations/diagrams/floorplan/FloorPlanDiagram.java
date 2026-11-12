/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.diagrams.floorplan;

import java.util.Locale;
import org.eclipse.sirius.components.view.builder.generated.diagram.DiagramBuilders;
import org.eclipse.sirius.components.view.builder.generated.view.ViewBuilders;
import org.eclipse.sirius.components.view.diagram.DiagramDescription;
import org.eclipse.sirius.components.view.diagram.DiagramLayoutOption;
import org.eclipse.sirius.components.view.diagram.NodeContainmentKind;
import org.eclipse.sirius.components.view.diagram.SynchronizationPolicy;
import org.eclipse.sirius.components.view.diagram.UserResizableDirection;
import hed.domain.Messages;
import hed.starter.representations.HedRepresentationIcons;

/** An imported floor plan and explicitly placed views of existing equipment. */
public final class FloorPlanDiagram {
    private final Locale locale;

    /**
     * Creates a storey-plan description contribution.
     * @param locale selects the representation and tool labels
     */
    public FloorPlanDiagram(Locale locale) {
        this.locale = locale;
    }

    /**
     * Builds a fixed background containing freely positioned, unsynchronized 24-by-24 device views.
     * @return the native diagram description, creatable on a Storey
     */
    public DiagramDescription create() {
        var b = new DiagramBuilders();
        var ops = new ViewBuilders();
        var equipment = b.newNodeDescription().name("hed-floor-plan-equipment").domainType("homeelectrical::FieldEquipment").semanticCandidatesExpression("aql:self.getHedFloorPlanEquipment()")
                .preconditionExpression("aql:true").synchronizationPolicy(SynchronizationPolicy.UNSYNCHRONIZED).defaultWidthExpression("24").defaultHeightExpression("24")
                .userResizable(UserResizableDirection.NONE).style(b.newImageNodeStyleDescription().shape("hed-plan:equipment").build()).build();
        var remove = b.newDeleteView().viewExpression("aql:selectedNode").build();
        equipment.setPalette(b.newNodePalette().deleteTool(b.newDeleteTool().name(Messages.text(locale, "plan.removeEquipment")).body(remove).build())
                .nodeTools(b.newNodeTool().name(Messages.text(locale, "plan.removeEquipment")).body(b.newDeleteView().viewExpression("aql:selectedNode").build()).build()).build());
        var background = b.newNodeDescription().name("hed-floor-plan-background").domainType("homeelectrical::Storey").semanticCandidatesExpression("aql:self").preconditionExpression("aql:true")
                .defaultWidthExpression("aql:self.planWidth").defaultHeightExpression("aql:self.planHeight + 48").userResizable(UserResizableDirection.NONE)
                .style(b.newImageNodeStyleDescription().shape("hed-plan:background").build()).childrenDescriptions(equipment).build();
        var create = b.newCreateView().parentViewExpression("aql:self.getHedPlanBackground(diagramContext)").semanticElementExpression("aql:self").elementDescription(equipment)
                .containmentKind(NodeContainmentKind.CHILD_NODE).build();
        var drop = b.newDropTool().name("hed-floor-plan-place").body(ops.newIf().conditionExpression("aql:self.canPlaceHedEquipmentInPlan(diagramContext, editingContext)").children(create).build())
                .build();
        return b.newDiagramDescription().name(Messages.text(locale, "diagram.floorPlan")).domainType("homeelectrical::Storey").preconditionExpression("aql:true")
                .titleExpression(Messages.text(locale, "diagram.floorPlan")).iconExpression(HedRepresentationIcons.FLOOR_PLAN)
                .style(b.newDiagramStyleDescription().background(ops.newFixedColor().name("hed-plan-background").value("#f1f5f9").build()).build()).layoutOption(DiagramLayoutOption.NONE)
                .minimapVisible(false).nodeDescriptions(background).palette(b.newDiagramPalette().dropTool(drop).build()).build();
    }
}
