/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.diagrams.panel;

import java.util.Locale;
import org.eclipse.sirius.components.view.builder.generated.diagram.DiagramBuilders;
import org.eclipse.sirius.components.view.builder.generated.view.ViewBuilders;
import org.eclipse.sirius.components.view.diagram.DiagramDescription;
import org.eclipse.sirius.components.view.diagram.DiagramLayoutOption;
import org.eclipse.sirius.components.view.diagram.UserResizableDirection;
import hed.domain.Messages;
import hed.starter.HedDomainIcons;

/** A physical panel: graphical nesting follows board, DIN rails and ordered devices. */
public final class PanelDiagram {
    private final Locale locale;

    public PanelDiagram(Locale locale) {
        this.locale = locale;
    }

    public DiagramDescription create() {
        var b = new DiagramBuilders();
        var operations = new ViewBuilders();
        var device = b.newNodeDescription().name("hed-panel-device").domainType("homeelectrical::ModularDevice").semanticCandidatesExpression("aql:self.getHedPanelDevices()")
                .preconditionExpression("aql:true").defaultWidthExpression("aql:self.getHedPanelWidth()").defaultHeightExpression("218").userResizable(UserResizableDirection.NONE)
                .style(b.newImageNodeStyleDescription().shape("hed-panel:device").build()).build();
        var rail = b.newNodeDescription().name("hed-panel-rail").domainType("homeelectrical::DINRail").semanticCandidatesExpression("aql:self.rails").preconditionExpression("aql:true")
                .defaultWidthExpression("aql:self.getHedPanelWidth()").defaultHeightExpression("aql:self.getHedPanelHeight()").userResizable(UserResizableDirection.NONE)
                .style(b.newImageNodeStyleDescription().shape("hed-panel:rail").build()).childrenDescriptions(device).build();
        var drop = b.newDropNodeTool().name("hed-panel-place-device").acceptedNodeTypes(device)
                .body(operations.newChangeContext().expression("aql:targetElement.dropHedPanelDevice(droppedElements)").build()).build();
        device.setPalette(b.newNodePalette().dropNodeTool(drop)
                .deleteTool(b.newDeleteTool().name("hed-panel-delete-device").body(operations.newChangeContext().expression("aql:self.deleteHedObject()").build()).build()).build());
        rail.setPalette(b.newNodePalette().dropNodeTool(b.newDropNodeTool().name("hed-panel-mount-device").acceptedNodeTypes(device)
                .body(operations.newChangeContext().expression("aql:targetElement.dropHedPanelDevice(droppedElements)").build()).build()).build());
        var board = b.newNodeDescription().name("hed-panel-board").domainType("homeelectrical::DistributionBoard").semanticCandidatesExpression("aql:self").preconditionExpression("aql:true")
                .defaultWidthExpression("aql:self.getHedPanelWidth()").defaultHeightExpression("aql:self.getHedPanelHeight()").userResizable(UserResizableDirection.NONE)
                .style(b.newImageNodeStyleDescription().shape("hed-panel:board").build()).childrenDescriptions(rail).build();
        return b.newDiagramDescription().name(Messages.text(locale, "diagram.panel")).domainType("homeelectrical::DistributionBoard").preconditionExpression("aql:true")
                .titleExpression(Messages.text(locale, "diagram.panel")).iconExpression(HedDomainIcons.path("view-dashboard-outline"))
                .style(b.newDiagramStyleDescription().background(operations.newFixedColor().name("hed-panel-background").value("#ffffff").build()).build()).layoutOption(DiagramLayoutOption.NONE)
                .nodeDescriptions(board).minimapVisible(false).build();
    }
}
