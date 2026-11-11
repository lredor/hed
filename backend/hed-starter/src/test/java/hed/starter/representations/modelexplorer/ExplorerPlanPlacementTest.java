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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.eclipse.sirius.components.collaborative.api.IRepresentationEventProcessor;
import org.eclipse.sirius.components.collaborative.api.IRepresentationSearchService;
import org.eclipse.sirius.components.collaborative.representations.api.IRepresentationEventProcessorRegistry;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IIdentityService;
import org.eclipse.sirius.components.core.api.labels.StyledString;
import org.eclipse.sirius.components.diagrams.Diagram;
import org.eclipse.sirius.components.diagrams.Node;
import org.eclipse.sirius.components.diagrams.DiagramStyle;
import org.eclipse.sirius.components.diagrams.ViewModifier;
import org.eclipse.sirius.components.diagrams.CollapsingState;
import org.eclipse.sirius.components.diagrams.components.BorderNodePosition;
import org.eclipse.sirius.components.diagrams.layoutdata.DiagramLayoutData;
import org.eclipse.sirius.components.representations.IRepresentation;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.trees.description.TreeDescription;
import org.eclipse.sirius.components.trees.renderer.TreeRenderer;
import org.eclipse.sirius.web.domain.boundedcontexts.representationdata.RepresentationMetadata;
import org.eclipse.sirius.web.domain.boundedcontexts.representationdata.services.api.IRepresentationMetadataSearchService;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import hed.domain.HedSamples;
import hed.domain.Messages;
import hed.starter.representations.diagrams.floorplan.FloorPlanNodeStyle;
import hed.starter.representations.diagrams.floorplan.FloorPlanNodeStyleProvider;

/** Verifies opt-in filter defaults, isolated render snapshots and placement accounting for live and closed floor plans. */
class ExplorerPlanPlacementTest {
    private static TreeDescription tree(String label) {
        return TreeDescription.newTreeDescription("hed-test-tree").label(label).idProvider(vm -> "explorer://test").treeItemIdProvider(vm -> "item").kindProvider(vm -> "equipment")
                .labelProvider(vm -> new StyledString(List.of())).targetObjectIdProvider(vm -> "root").parentObjectProvider(vm -> null).treeItemIconURLsProvider(vm -> List.of())
                .editableProvider(vm -> false).deletableProvider(vm -> false).selectableProvider(vm -> true).elementsProvider(vm -> List.of()).childrenProvider(vm -> List.of())
                .hasChildrenProvider(vm -> false).canCreatePredicate(vm -> false).deleteHandler(vm -> null).renameHandler((vm, name) -> null).treeItemObjectProvider(vm -> null)
                .treeItemLabelProvider(vm -> new StyledString(List.of())).iconURLsProvider(vm -> List.of()).build();
    }

    @Test
    void nativeFilterIsLocalizedDisabledByDefaultAndRestrictedToTheDomainExplorer() {
        for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            var provider = new FloorPlanPlacementFilterProvider(locale);
            var filters = provider.get("context", tree(Messages.text(locale, "explorer.title")));
            assertEquals(1, filters.size());
            assertEquals(FloorPlanPlacementFilterProvider.FILTER_ID, filters.getFirst().id());
            assertEquals(Messages.text(locale, "explorer.highlightUnplaced"), filters.getFirst().label());
            assertFalse(filters.getFirst().defaultState());
            assertTrue(provider.get("context", tree("Another tree")).isEmpty());
        }
    }

    @Test
    void disabledOrUnrelatedExplorersPerformNoPlacementReads() {
        var fixture = new Fixture();
        var equipment = fixture.home.getEquipment().getFirst();
        var absent = fixture.service.customize(tree(Messages.text(Locale.ENGLISH, "explorer.title")), fixture.variables(null));
        assertFalse(fixture.missing(equipment, absent));
        assertTrue(fixture.placements(absent).isEmpty());
        var unrelatedFilters = fixture.service.customize(tree(Messages.text(Locale.ENGLISH, "explorer.title")), fixture.variables(List.of("another-filter")));
        assertFalse(fixture.missing(equipment, unrelatedFilters));
        var unrelatedTree = fixture.variables(List.of(FloorPlanPlacementFilterProvider.FILTER_ID));
        assertSame(unrelatedTree, fixture.service.customize(tree("Another tree"), unrelatedTree));
        assertEquals(0, fixture.metadataReads);
        assertEquals(0, fixture.representationReads);
        assertEquals(0, fixture.liveReads);
    }

    @Test
    void closedSavedPlansCountOnlyForTheDevicesCurrentStorey() {
        var fixture = new Fixture();
        var equipment = fixture.home.getEquipment().getFirst();
        var storey = equipment.getRoom().eContainer();
        fixture.saved.put(UUID.randomUUID().toString(), fixture.plan("saved", fixture.id(storey), fixture.node(fixture.id(equipment), false)));
        var render = fixture.render(true);
        assertFalse(fixture.missing(equipment, render));
        assertFalse(fixture.missing(new BranchEquipmentLinks.EquipmentLink(equipment), render));
        assertTrue(fixture.missing(fixture.home.getEquipment().get(1), render));
        assertTrue(fixture.missing(new BranchEquipmentLinks.EquipmentLink(fixture.home.getEquipment().get(1)), render));
        assertTrue(fixture.missing(equipment.getRoom(), render));
        assertFalse(fixture.missing(fixture.home.getCatalog().getDeviceTypes().getFirst(), render));
        assertFalse(fixture.missing(fixture.home.getBoards().getFirst().getDevices().getFirst(), render));
        equipment.setRoom(fixture.home.getStoreys().get(1).getRooms().getFirst());
        assertTrue(fixture.missing(equipment, fixture.render(true)));
        equipment.setRoom(null);
        assertFalse(fixture.missing(equipment, fixture.render(true)));
    }

    @Test
    void missingViewsPropagateThroughSemanticParentsAndClearAfterTheLastPlacement() {
        var fixture = new Fixture();
        var ground = fixture.home.getStoreys().getFirst();
        var first = fixture.home.getStoreys().get(1);
        var livingRoom = ground.getRooms().getFirst();
        var garage = ground.getRooms().get(1);
        var bedroom = first.getRooms().getFirst();
        var initial = fixture.render(true);
        assertTrue(fixture.missing(livingRoom, initial));
        assertTrue(fixture.missing(ground, initial));
        assertTrue(fixture.missing(first, initial));
        assertTrue(fixture.missing(fixture.home, initial));
        assertFalse(fixture.missing(garage, initial));
        assertFalse(fixture.missing(new Object(), initial));
        assertFalse(fixture.missing(fixture.home, fixture.render(false)));
        var nodes = fixture.home.getEquipment().stream().filter(equipment -> equipment.getRoom() == livingRoom).map(equipment -> fixture.node(fixture.id(equipment), false)).toArray(Node[]::new);
        var groundPlan = fixture.plan(UUID.randomUUID().toString(), fixture.id(ground), nodes);
        fixture.saved.put(groundPlan.getId(), groundPlan);
        var partlyPlaced = fixture.render(true);
        assertFalse(fixture.missing(livingRoom, partlyPlaced));
        assertFalse(fixture.missing(ground, partlyPlaced));
        assertTrue(fixture.missing(first, partlyPlaced));
        assertTrue(fixture.missing(fixture.home, partlyPlaced));
        var equipment = fixture.home.getEquipment().stream().filter(device -> device.getRoom() == bedroom).findFirst().orElseThrow();
        var firstPlan = fixture.plan(UUID.randomUUID().toString(), fixture.id(first), fixture.node(fixture.id(equipment), false));
        fixture.saved.put(firstPlan.getId(), firstPlan);
        var allPlaced = fixture.render(true);
        assertFalse(fixture.missing(bedroom, allPlaced));
        assertFalse(fixture.missing(first, allPlaced));
        assertFalse(fixture.missing(fixture.home, allPlaced));
        fixture.live.add(processor(fixture.plan(firstPlan.getId(), fixture.id(first))));
        var removed = fixture.render(true);
        assertTrue(fixture.missing(bedroom, removed));
        assertTrue(fixture.missing(first, removed));
        assertTrue(fixture.missing(fixture.home, removed));
        assertFalse(fixture.missing(ground, removed));
        equipment.setRoom(garage);
        var reassigned = fixture.render(true);
        assertFalse(fixture.missing(bedroom, reassigned));
        assertFalse(fixture.missing(first, reassigned));
        assertTrue(fixture.missing(garage, reassigned));
        assertTrue(fixture.missing(ground, reassigned));
        equipment.setRoom(null);
        assertFalse(fixture.missing(fixture.home, fixture.render(true)));
    }

    @Test
    void liveViewDeletionOverridesSavedContentAndASecondPlanStillCounts() {
        var fixture = new Fixture();
        var equipment = fixture.home.getEquipment().getFirst();
        var storey = fixture.id(equipment.getRoom().eContainer());
        var first = fixture.plan(UUID.randomUUID().toString(), storey, fixture.node(fixture.id(equipment), false));
        fixture.saved.put(first.getId(), first);
        fixture.live.add(processor(fixture.plan(first.getId(), storey)));
        assertTrue(fixture.missing(equipment, fixture.render(true)));
        assertEquals(0, fixture.representationReads);
        var second = fixture.plan(UUID.randomUUID().toString(), storey, fixture.node(fixture.id(equipment), false));
        fixture.saved.put(second.getId(), second);
        assertFalse(fixture.missing(equipment, fixture.render(true)));
        fixture.live.add(processor(fixture.plan(second.getId(), storey)));
        assertTrue(fixture.missing(equipment, fixture.render(true)));
    }

    @Test
    void eachRenderHasAnIndependentSnapshotAndCanReuseExplorerMetadata() {
        var fixture = new Fixture();
        var equipment = fixture.home.getEquipment().getFirst();
        var empty = fixture.render(true);
        assertTrue(fixture.missing(equipment, empty));
        var diagram = fixture.plan(UUID.randomUUID().toString(), fixture.id(equipment.getRoom().eContainer()), fixture.node(fixture.id(equipment), false));
        fixture.saved.put(diagram.getId(), diagram);
        var variables = fixture.variables(List.of(FloorPlanPlacementFilterProvider.FILTER_ID));
        variables.put(hed.starter.representations.modelexplorer.ExplorerRepresentations.VARIABLE, fixture.metadata());
        fixture.metadataReads = 0;
        var placed = fixture.service.customize(tree(Messages.text(Locale.ENGLISH, "explorer.title")), variables);
        assertFalse(fixture.missing(equipment, placed));
        assertTrue(fixture.missing(equipment, empty));
        assertEquals(0, fixture.metadataReads);
        int loads = fixture.representationReads;
        var disabled = fixture.render(false);
        assertFalse(fixture.missing(equipment, disabled));
        assertEquals(loads, fixture.representationReads);
    }

    @SuppressWarnings("unchecked")
    private static <T> T stub(Class<T> type, java.util.function.BiFunction<String, Object[], Object> response) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, (proxy, method, args) -> switch (method.getName()) {
            case "hashCode" -> System.identityHashCode(proxy);
            case "equals" -> proxy == args[0];
            default -> response.apply(method.getName(), args);
        });
    }

    private static IRepresentationEventProcessor processor(Diagram diagram) {
        return stub(IRepresentationEventProcessor.class, (name, args) -> diagram);
    }

    /** Models saved diagrams separately from their current in-memory versions. */
    private static final class Fixture {
        final hed.homeelectrical.Dwelling home = HedSamples.example();

        final UUID contextId = UUID.randomUUID();

        final Map<Object, String> ids = new IdentityHashMap<>();

        final Map<String, Diagram> saved = new LinkedHashMap<>();

        final List<IRepresentationEventProcessor> live = new ArrayList<>();

        final IEditingContext context = stub(IEditingContext.class, (name, args) -> contextId.toString());

        final ExplorerPlanPlacement service;

        int metadataReads;

        int representationReads;

        int liveReads;

        Fixture() {
            var identity = new IIdentityService() {
                public String getId(Object object) {
                    return id(object);
                }

                public String getKind(Object object) {
                    return "equipment";
                }
            };
            var metadata = stub(IRepresentationMetadataSearchService.class, (name, args) -> {
                metadataReads++;
                return metadata();
            });
            var representations = new IRepresentationSearchService() {
                @Override
                public <T extends IRepresentation> Optional<T> findById(IEditingContext ctx, String id, Class<T> type) {
                    representationReads++;
                    return Optional.ofNullable(saved.get(id)).filter(type::isInstance).map(type::cast);
                }

                @Override
                public boolean existByIdAndKind(IEditingContext ctx, String id, List<String> kinds) {
                    return saved.containsKey(id);
                }
            };
            service = new ExplorerPlanPlacement(identity, metadata, representations, stub(IRepresentationEventProcessorRegistry.class, (name, args) -> {
                liveReads++;
                return live;
            }), Locale.ENGLISH);
        }

        String id(Object object) {
            return ids.computeIfAbsent(object, key -> UUID.randomUUID().toString());
        }

        List<RepresentationMetadata> metadata() {
            return saved.entrySet().stream()
                    .map(entry -> RepresentationMetadata.newRepresentationMetadata(UUID.randomUUID().toString()).representationMetadataId(UUID.fromString(entry.getKey()))
                            .semanticData(AggregateReference.to(contextId)).targetObjectId(entry.getValue().getTargetObjectId()).descriptionId("plan").label("Plan").kind(Diagram.KIND)
                            .iconURLs(List.of()).documentation("").build(UUID::randomUUID))
                    .toList();
        }

        VariableManager variables(List<String> filters) {
            var variables = new VariableManager();
            variables.put(IEditingContext.EDITING_CONTEXT, context);
            if (filters != null) {
                variables.put(TreeRenderer.ACTIVE_FILTER_IDS, filters);
            }
            return variables;
        }

        VariableManager render(boolean enabled) {
            return service.customize(tree(Messages.text(Locale.ENGLISH, "explorer.title")), variables(enabled ? List.of(FloorPlanPlacementFilterProvider.FILTER_ID) : List.of()));
        }

        @SuppressWarnings("unchecked")
        Map<String, Set<String>> placements(VariableManager variables) {
            return (Map<String, Set<String>>) variables.get(ExplorerPlanPlacement.PLACEMENTS_VARIABLE, Map.class).orElseThrow();
        }

        boolean missing(Object object, VariableManager variables) {
            return service.isHedMissingFromFloorPlans(object, placements(variables), variables.get(ExplorerPlanPlacement.ENABLED_VARIABLE, Boolean.class).orElse(false));
        }

        Diagram plan(String id, String storey, Node... equipment) {
            return Diagram.newDiagram(id).targetObjectId(storey).descriptionId("plan").nodes(List.of(Node.newNode(node(storey, true)).childNodes(List.of(equipment)).build())).edges(List.of())
                    .style(DiagramStyle.newDiagramStyle().background("white").build()).layoutData(new DiagramLayoutData(Map.of(), Map.of(), Map.of(), false)).build();
        }

        Node node(String target, boolean background) {
            return Node.newNode(UUID.randomUUID().toString()).type(FloorPlanNodeStyleProvider.TYPE).targetObjectId(target).targetObjectKind("equipment").targetObjectLabel("")
                    .descriptionId("plan-node").initialBorderNodePosition(BorderNodePosition.NONE).modifiers(Set.of()).state(ViewModifier.Normal).collapsingState(CollapsingState.EXPANDED)
                    .outsideLabels(List.of()).style(new FloorPlanNodeStyle(background, "", "", false, null)).borderNodes(List.of()).childNodes(List.of()).customizedStyleProperties(Set.of())
                    .decorators(List.of()).build();
        }
    }
}
