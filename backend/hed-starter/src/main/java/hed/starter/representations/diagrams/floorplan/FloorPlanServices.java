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

import java.util.List;
import java.util.Locale;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.core.api.IFeedbackMessageService;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.collaborative.diagrams.DiagramContext;
import org.eclipse.sirius.components.representations.Message;
import org.eclipse.sirius.components.representations.MessageLevel;
import org.springframework.stereotype.Service;
import hed.domain.HedDomain;
import hed.domain.Messages;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.Storey;

/** Supplies existing equipment eligible for a storey plan and validates explorer drops without changing semantic ownership. */
@Service
public class FloorPlanServices {
    private final IObjectSearchService search;

    private final IFeedbackMessageService feedback;

    private final Locale locale;

    /**
     * Creates the services used by floor-plan View DSL expressions.
     * @param search resolves the diagram's storey in the current editing context
     * @param feedback receives localized rejection messages for invalid drops
     * @param locale selects feedback text
     */
    public FloorPlanServices(IObjectSearchService search, IFeedbackMessageService feedback, Locale locale) {
        this.search = search;
        this.feedback = feedback;
        this.locale = locale;
    }

    /**
     * Lists existing equipment assigned to rooms contained in the storey.
     * @param storey the plan's semantic root
     * @return eligible equipment; an unattached storey yields an empty list
     */
    public List<FieldEquipment> getHedFloorPlanEquipment(Storey storey) {
        var home = HedDomain.dwelling(storey);
        return home == null ? List.of() : home.getEquipment().stream().filter(e -> belongsTo(e, storey)).toList();
    }

    /**
     * Tests the room's containment, independently of the equipment's dwelling ownership.
     * @param equipment an existing terminal device
     * @param storey the storey containing the room
     * @return whether the device is assigned to a room on this storey
     */
    public static boolean belongsTo(FieldEquipment equipment, Storey storey) {
        return equipment.getRoom() != null && equipment.getRoom().eContainer() == storey;
    }

    /**
     * Finds the parent view for an explicitly created equipment view.
     * @param source the semantic drop source, retained for AQL receiver dispatch
     * @param context the floor-plan diagram being edited
     * @return its fixed background node, or {@code null} if it has not been rendered
     */
    public org.eclipse.sirius.components.diagrams.Node getHedPlanBackground(Object source, DiagramContext context) {
        return context.diagram().getNodes().stream().filter(node -> node.getStyle() instanceof FloorPlanNodeStyle style && style.backgroundNode()).findFirst().orElse(null);
    }

    /**
     * Accepts only existing equipment belonging to this plan's storey, reporting invalid drops.
     * @param source the semantic object dragged from Model Explorer
     * @param diagramContext the diagram receiving the drop
     * @param context the editing context used to resolve the diagram root
     * @return whether a view may be created; never creates or moves a semantic object
     */
    public boolean canPlaceHedEquipmentInPlan(Object source, DiagramContext diagramContext, IEditingContext context) {
        var diagram = diagramContext.diagram();
        var storey = search.getObject(context, diagram.getTargetObjectId()).filter(Storey.class::isInstance).map(Storey.class::cast).orElse(null);
        if (storey != null && source instanceof FieldEquipment equipment && belongsTo(equipment, storey)) {
            return true;
        }
        feedback.addFeedbackMessage(new Message(Messages.text(locale, "plan.sameStorey"), MessageLevel.ERROR));
        return false;
    }
}
