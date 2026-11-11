/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.modelexplorer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.collaborative.api.IRepresentationSearchService;
import org.eclipse.sirius.components.collaborative.representations.api.IRepresentationEventProcessorRegistry;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IIdentityService;
import org.eclipse.sirius.components.diagrams.Diagram;
import org.eclipse.sirius.components.diagrams.Node;
import org.eclipse.sirius.components.representations.IRepresentationDescription;
import org.eclipse.sirius.components.representations.IRepresentationRenderVariableCustomizer;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.trees.description.TreeDescription;
import org.eclipse.sirius.components.trees.renderer.TreeRenderer;
import org.eclipse.sirius.web.domain.boundedcontexts.representationdata.RepresentationMetadata;
import org.eclipse.sirius.web.domain.boundedcontexts.representationdata.services.api.IRepresentationMetadataSearchService;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;
import hed.domain.HedDomain;
import hed.domain.Messages;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.Room;
import hed.homeelectrical.Storey;
import hed.starter.representations.diagrams.floorplan.FloorPlanNodeStyle;

/** Builds an immutable, render-local snapshot of floor-plan views only when their explorer highlight is enabled. */
@Service
public class ExplorerPlanPlacement implements IRepresentationRenderVariableCustomizer {
    public static final String ENABLED_VARIABLE = "hedHighlightUnplaced";

    public static final String PLACEMENTS_VARIABLE = "hedPlanPlacements";

    public static final String UNPLACED_COLOR = "#c62828";

    private final IIdentityService identity;

    private final IRepresentationMetadataSearchService metadata;

    private final IRepresentationSearchService representations;

    private final IRepresentationEventProcessorRegistry processors;

    private final Locale locale;

    /**
     * Creates the placement contribution without storing user preferences or semantic placement flags.
     *
     * @param identity resolves stable storey and device identifiers
     * @param metadata lists saved representations in the current editing context
     * @param representations loads closed diagrams when the filter is enabled
     * @param processors supplies the current versions of diagrams being edited
     * @param locale identifies the HED explorer by its localized description label
     */
    public ExplorerPlanPlacement(IIdentityService identity, IRepresentationMetadataSearchService metadata, IRepresentationSearchService representations,
            IRepresentationEventProcessorRegistry processors, Locale locale) {
        this.identity = identity;
        this.metadata = metadata;
        this.representations = representations;
        this.processors = processors;
        this.locale = locale;
    }

    @Override
    public VariableManager customize(IRepresentationDescription description, VariableManager variables) {
        if (!(description instanceof TreeDescription tree) || !Messages.text(this.locale, "explorer.title").equals(tree.getLabel())) {
            return variables;
        }
        boolean enabled = variables.get(TreeRenderer.ACTIVE_FILTER_IDS, List.class).map(filters -> filters.contains(FloorPlanPlacementFilterProvider.FILTER_ID)).orElse(false);
        var child = variables.createChild();
        child.put(ENABLED_VARIABLE, enabled);
        // Disabled explorers retain their original labels and perform no placement-related representation reads.
        var placements = enabled ? variables.get(IEditingContext.EDITING_CONTEXT, IEditingContext.class).map(context -> snapshot(context, variables)).orElse(Map.of()) : Map.of();
        child.put(PLACEMENTS_VARIABLE, placements);
        return child;
    }

    /**
     * Identifies missing room-equipment views and propagates their highlight to the room, storey and dwelling.
     * Semantic associations are inspected even when the corresponding explorer items are collapsed.
     *
     * @param source the current Model Explorer item
     * @param placements the snapshot for this tree render, indexed by storey identifier
     * @param enabled whether this explorer's native placement filter is selected
     * @return whether the device or one of the item's room devices lacks a view on its current storey
     */
    public boolean isHedMissingFromFloorPlans(Object source, Map<String, Set<String>> placements, boolean enabled) {
        if (!enabled) {
            return false;
        }
        if (source instanceof FieldEquipment equipment) {
            return missing(equipment, placements);
        }
        if (source instanceof Room || source instanceof Storey || source instanceof Dwelling) {
            var home = HedDomain.dwelling((EObject) source);
            // Devices are owned by the dwelling and reference their room; they are not contained in the room.
            return home != null
                    && home.getEquipment().stream().filter(equipment -> source == home || source == equipment.getRoom() || equipment.getRoom() != null && source == equipment.getRoom().eContainer())
                            .anyMatch(equipment -> missing(equipment, placements));
        }
        return false;
    }

    private boolean missing(FieldEquipment equipment, Map<String, Set<String>> placements) {
        if (equipment.getRoom() == null || !(equipment.getRoom().eContainer() instanceof Storey storey)) {
            return false;
        }
        return !placements.getOrDefault(this.identity.getId(storey), Set.of()).contains(this.identity.getId(equipment));
    }

    /** Live diagrams override persisted versions so creating or removing a view changes the next tree render immediately. */
    private Map<String, Set<String>> snapshot(IEditingContext context, VariableManager variables) {
        var diagrams = new LinkedHashMap<String, Diagram>();
        for (var processor : this.processors.values(context.getId())) {
            if (processor.getRepresentation() instanceof Diagram diagram) {
                diagrams.put(diagram.getId(), diagram);
            }
        }
        for (var item : representationMetadata(context, variables)) {
            var id = item.getRepresentationMetadataId().toString();
            if (Diagram.KIND.equals(item.getKind()) && !diagrams.containsKey(id)) {
                this.representations.findById(context, id, Diagram.class).ifPresent(diagram -> diagrams.put(id, diagram));
            }
        }
        var placements = new HashMap<String, Set<String>>();
        for (var diagram : diagrams.values()) {
            var ids = placements.computeIfAbsent(diagram.getTargetObjectId(), key -> new HashSet<>());
            collect(diagram.getNodes(), ids);
        }
        placements.replaceAll((storey, ids) -> Set.copyOf(ids));
        return Map.copyOf(placements);
    }

    /** Reuses the explorer's existing metadata snapshot when its render customizer has already run. */
    private List<RepresentationMetadata> representationMetadata(IEditingContext context, VariableManager variables) {
        List<?> existing = variables.get(ExplorerRepresentations.VARIABLE, List.class).orElse(null);
        if (existing != null) {
            return existing.stream().filter(RepresentationMetadata.class::isInstance).map(RepresentationMetadata.class::cast).toList();
        }
        return this.metadata.findAllRepresentationMetadataBySemanticData(AggregateReference.to(UUID.fromString(context.getId())));
    }

    private static void collect(List<Node> nodes, Set<String> ids) {
        for (var node : nodes) {
            if (node.getStyle() instanceof FloorPlanNodeStyle style && !style.backgroundNode()) {
                ids.add(node.getTargetObjectId());
            }
            collect(node.getChildNodes(), ids);
            collect(node.getBorderNodes(), ids);
        }
    }
}
