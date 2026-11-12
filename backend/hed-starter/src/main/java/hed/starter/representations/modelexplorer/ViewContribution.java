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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IEditingContextProcessor;
import org.eclipse.sirius.components.core.api.IRepresentationDescriptionSearchService;
import org.eclipse.sirius.components.emf.ResourceMetadataAdapter;
import org.eclipse.sirius.components.emf.services.IDAdapter;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.view.View;
import org.eclipse.sirius.components.view.builder.generated.tree.TreeBuilders;
import org.eclipse.sirius.components.view.builder.generated.view.ViewBuilders;
import org.eclipse.sirius.components.view.emf.IJavaServiceProvider;
import org.eclipse.sirius.components.view.emf.IViewRepresentationDescriptionSearchService;
import org.eclipse.sirius.components.view.emf.tree.ITreeIdProvider;
import org.eclipse.sirius.components.view.tree.TreeItemContextMenuEntry;
import org.eclipse.sirius.web.application.editingcontext.EditingContext;
import org.eclipse.sirius.web.application.views.explorer.services.api.IExplorerTreeDescriptionProvider;
import org.springframework.stereotype.Service;
import hed.homeelectrical.DeviceKind;
import hed.domain.Messages;
import hed.starter.HedDomainIcons;
import hed.starter.representations.CatalogDropService;
import hed.starter.representations.diagrams.circuits.CircuitDiagram;
import hed.starter.representations.diagrams.circuits.CircuitServices;
import hed.starter.representations.diagrams.panel.PanelDiagram;
import hed.starter.representations.diagrams.floorplan.FloorPlanDiagram;
import hed.starter.representations.diagrams.floorplan.FloorPlanServices;
import hed.starter.representations.diagrams.panel.PanelServices;
import hed.starter.representations.tables.HedTableDescriptions;

/** Contributes stable View DSL descriptions and the localized domain explorer to each editing context. */
@Service
public class ViewContribution implements IEditingContextProcessor, IJavaServiceProvider, IExplorerTreeDescriptionProvider {
    private final ITreeIdProvider ids;

    private final IRepresentationDescriptionSearchService descriptions;

    private final IViewRepresentationDescriptionSearchService viewDescriptions;

    private final Locale locale;

    private final HedTableDescriptions tables;

    private org.eclipse.sirius.components.view.tree.TreeDescription explorerDescription;

    /**
     * Creates the application's explorer and representation contribution.
     *
     * @param ids resolves native tree description identifiers
     * @param descriptions finds the converted runtime descriptions
     * @param viewDescriptions maps runtime identifiers to their View DSL descriptions
     * @param tables contributes the electrical tables in their stable order
     * @param locale selects representation, explorer and tool labels
     */
    public ViewContribution(ITreeIdProvider ids, IRepresentationDescriptionSearchService descriptions, IViewRepresentationDescriptionSearchService viewDescriptions, HedTableDescriptions tables,
            Locale locale) {
        this.ids = ids;
        this.descriptions = descriptions;
        this.viewDescriptions = viewDescriptions;
        this.locale = locale;
        this.tables = tables;
    }

    @Override
    public List<Class<?>> getServiceClasses(View view) {
        return List.of(HedJavaServices.class, ExplorerRepresentations.class, PanelServices.class, CatalogDropService.class, FloorPlanServices.class, ExplorerPlanPlacement.class, CircuitServices.class,
                ExplorerCircuitCompleteness.class);
    }

    @Override
    public void preProcess(IEditingContext context) {
        if (context instanceof EditingContext editing
                && editing.getViews().stream().noneMatch(v -> v.getDescriptions().stream().anyMatch(d -> d.getName().equals(Messages.text(locale, "explorer.title"))))) {
            editing.getViews().add(createView());
        }
    }

    /**
     * Builds the domain view with deterministic description identifiers and an optional placement label style.
     *
     * @return the view in its own native JSON resource
     */
    public View createView() {
        var tb = new TreeBuilders();
        var vb = new ViewBuilders();
        var actions = new ArrayList<TreeItemContextMenuEntry>();
        var names = new ArrayList<>(List.of("Storey", "Room", "DistributionBoard", "DINRail", "FinalCircuit", "CircuitBranch", "DistributionCircuit", "ControlFunction", "ControlChannel",
                "ServiceConnection", "EnergyMeter", "ServiceCircuitBreaker", "DeviceType"));
        Arrays.stream(DeviceKind.values()).filter(k -> k != DeviceKind.ENERGY_METER && k != DeviceKind.SERVICE_BREAKER).map(DeviceKind::getName).forEach(names::add);
        for (var name : names) {
            var key = Character.isUpperCase(name.charAt(0)) && name.contains("_") || Arrays.stream(DeviceKind.values()).anyMatch(k -> k.getName().equals(name)) ? "kind." + name : "class." + name;
            actions.add(tb.newSingleClickTreeItemContextMenuEntry().name("hed-create-" + name).preconditionExpression("aql:self.canCreateHedObject('" + name + "')")
                    .labelExpression("aql:self.getHedText('action.create') + ' ' + self.getHedText('" + key + "')").iconURLExpression(HedDomainIcons.concept(name))
                    .body(vb.newChangeContext().expression("aql:self.createHedObject('" + name + "')").build()).build());
        }
        actions.add(tb.newCustomTreeItemContextMenuEntry().name("hed-new-object").contributionId("new-object").labelExpression("aql:self.getHedText('action.newObject')")
                .preconditionExpression("aql:self.canAddHedObject()").build());
        actions.add(tb.newCustomTreeItemContextMenuEntry().name("hed-new-representation").contributionId("new-representation").labelExpression("aql:self.getHedText('action.newRepresentation')")
                .preconditionExpression("aql:self.canRepresentHedObject()").build());
        actions.add(tb.newCustomTreeItemContextMenuEntry().name("hed-expand-all").contributionId("expand-all").labelExpression("aql:self.getHedText('action.expandAll')")
                .preconditionExpression("aql:self.hasHedChildren() or self.hasHedRepresentations(editingContext)").build());
        for (int delta : new int[] { -1, 1 }) {
            actions.add(tb.newSingleClickTreeItemContextMenuEntry().name("hed-reorder-" + delta).preconditionExpression("aql:self.canReorderHedObject(" + delta + ")")
                    .labelExpression("aql:self.getHedReorderLabel(" + delta + ")").body(vb.newChangeContext().expression("aql:self.reorderHedObject(" + delta + ")").build()).build());
        }
        var tree = tb.newTreeDescription().name(Messages.text(locale, "explorer.title")).titleExpression("aql:editingContext.getHedText('explorer.title')").preconditionExpression("aql:false")
                .elementsExpression("aql:editingContext.getHedRoots()")
                .childrenExpression("aql:if expanded->includes(self.getHedId()) then self.getHedChildren(hedRepresentations) else Sequence{} endif")
                .hasChildrenExpression("aql:self.hasHedChildren(hedRepresentations)").treeItemIdExpression("aql:self.getHedId()").treeItemObjectExpression("aql:id.getHedObject(editingContext)")
                .treeItemIconExpression("aql:self.getHedIcon()").kindExpression("aql:self.getHedKind()").parentExpression("aql:self.getHedParent(editingContext)")
                .selectableExpression("aql:self.isHedSelectable()").editableExpression("aql:self.isHedEditable()").deletableExpression("aql:self.isHedDeletable()")
                .treeItemLabelDescriptions(tb.newTreeItemLabelDescription().name("hed-label").preconditionExpression(
                        "aql:not (self.isHedMissingFromFloorPlans(hedPlanPlacements, hedHighlightUnplaced) or self.isHedCircuitIncomplete(hedIncompleteCircuitElements, hedHighlightIncompleteCircuits))")
                        .children(tb.newTreeItemLabelFragmentDescription().labelExpression("aql:self.getHedExplorerLabel()").build()).build(),
                        tb.newTreeItemLabelDescription().name("hed-label-unplaced").preconditionExpression(
                                "aql:self.isHedMissingFromFloorPlans(hedPlanPlacements, hedHighlightUnplaced) or self.isHedCircuitIncomplete(hedIncompleteCircuitElements, hedHighlightIncompleteCircuits)")
                                .children(tb.newTreeItemLabelFragmentDescription().labelExpression("aql:self.getHedExplorerLabel()")
                                        .style(vb.newTextStyleDescription().name("hed-plan-placement").foregroundColorExpression(ExplorerPlanPlacement.UNPLACED_COLOR).build()).build())
                                .build())
                .contextMenuEntries(actions.toArray(TreeItemContextMenuEntry[]::new)).build();
        var view = vb.newView().build();
        view.getDescriptions().add(tree);
        contributeRepresentations(view);
        view.eAdapters().add(new IDAdapter(UUID.nameUUIDFromBytes("hed-view-v1".getBytes(StandardCharsets.UTF_8))));
        view.eAllContents().forEachRemaining(o -> o.eAdapters().add(new IDAdapter(UUID.nameUUIDFromBytes(("hed-view-v1/" + stablePath(view, o)).getBytes(StandardCharsets.UTF_8)))));
        var resource = new JSONResourceFactory().createResourceFromPath(UUID.nameUUIDFromBytes("hed-view".getBytes(StandardCharsets.UTF_8)).toString());
        resource.eAdapters().add(new ResourceMetadataAdapter("Home Electrical Designer Views"));
        resource.getContents().add(view);
        this.explorerDescription = tree;
        return view;
    }

    /**
     * Returns the native identifier once this contribution has created the explorer description.
     *
     * @return the explorer description identifier, or an empty string before initialization
     */
    public String explorerDescriptionId() {
        return this.explorerDescription == null ? "" : this.ids.getId(this.explorerDescription);
    }

    private String stablePath(org.eclipse.emf.ecore.EObject root, org.eclipse.emf.ecore.EObject object) {
        var segments = new ArrayDeque<String>();
        while (object != root) {
            var parent = object.eContainer();
            var feature = object.eContainingFeature();
            int index = feature.isMany() ? ((List<?>) parent.eGet(feature)).indexOf(object) : 0;
            segments.addFirst(feature.getName() + "." + index);
            object = parent;
        }
        return String.join("/", segments);
    }

    /** Appends new descriptions to retain the stable paths and identifiers of previously saved representations. */
    protected void contributeRepresentations(View view) {
        this.tables.addTo(view);
        view.getDescriptions().add(new PanelDiagram(locale).create());
        view.getDescriptions().add(new FloorPlanDiagram(locale).create());
        view.getDescriptions().add(new CircuitDiagram(locale).create());
    }

    @Override
    public List<org.eclipse.sirius.components.trees.description.TreeDescription> getDescriptions(IEditingContext context) {
        return descriptions.findAll(context).values().stream().filter(org.eclipse.sirius.components.trees.description.TreeDescription.class::isInstance)
                .map(org.eclipse.sirius.components.trees.description.TreeDescription.class::cast)
                .filter(tree -> viewDescriptions.findById(context, tree.getId()).map(description -> description.getName().equals(Messages.text(locale, "explorer.title"))).orElse(false)).toList();
    }
}
