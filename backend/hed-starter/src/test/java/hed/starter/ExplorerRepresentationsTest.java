/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.tables.Table;
import org.eclipse.sirius.web.domain.boundedcontexts.representationdata.RepresentationIconURL;
import org.eclipse.sirius.web.domain.boundedcontexts.representationdata.RepresentationMetadata;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.junit.jupiter.api.Test;
import hed.domain.HedSamples;
import hed.homeelectrical.HomeElectricalFactory;
import hed.starter.representations.HedRepresentationIcons;
import hed.starter.representations.modelexplorer.HedJavaServices;

/** Verifies saved representation discovery and presentation independently of semantic objects. */
class ExplorerRepresentationsTest {
    private final BootstrapIntegrationTest.Identity identity = new BootstrapIntegrationTest.Identity();

    private final UUID contextId = UUID.randomUUID();

    private final IEditingContext context = () -> contextId.toString();

    private RepresentationMetadata representation(Object target, String label) {
        return representation(target, label, Table.KIND, List.of(new RepresentationIconURL(HedDomainIcons.path("table-large"))));
    }

    private RepresentationMetadata representation(Object target, String label, String kind, List<RepresentationIconURL> icons) {
        return RepresentationMetadata.newRepresentationMetadata(UUID.randomUUID().toString()).representationMetadataId(UUID.randomUUID()).semanticData(AggregateReference.to(contextId))
                .targetObjectId(identity.getId(target)).descriptionId("description").label(label).kind(kind).iconURLs(icons).documentation("").build(UUID::randomUUID);
    }

    @Test
    void representationsAreSortedUnderTheirTargetsWithoutChangingTheVirtualSemanticHierarchy() {
        var home = HedSamples.example();
        var board = home.getBoards().get(0);
        var circuits = representation(home, "Circuit Table");
        var equipment = representation(home, "Equipment Table");
        var panel = representation(board, "Board Table");
        var catalog = representation(home.getCatalog(), "Catalog Table");
        var all = List.of(equipment, panel, catalog, circuits);
        var service = new HedJavaServices(identity, new IObjectSearchService.NoOp(), new BootstrapIntegrationTest.Feedback(), Locale.ENGLISH, dwelling -> dwelling.getName());
        assertEquals(List.of(circuits, equipment), service.getHedChildren(home, all).subList(0, 2));
        assertEquals(service.getHedChildren(home), service.getHedChildren(home, all).subList(2, service.getHedChildren(home, all).size()));
        assertEquals(panel, service.getHedChildren(board, all).get(0));
        assertEquals(catalog, service.getHedChildren(home.getCatalog(), all).get(0));
        var group = new HedJavaServices.Group(home, "gtl");
        assertEquals(service.getHedChildren(group), service.getHedChildren(group, all));
        assertEquals(circuits.getRepresentationMetadataId().toString(), service.getHedId(circuits));
        assertEquals(Table.KIND, service.getHedKind(circuits));
        assertEquals("Circuit Table", service.getHedLabel(circuits));
        assertEquals(HedRepresentationIcons.TABLE, service.getHedIcon(circuits));
        assertTrue(service.isHedSelectable(circuits));
        assertTrue(service.isHedDeletable(circuits));
        assertFalse(service.hasHedChildren(circuits, all));
    }

    @Test
    void existingDiagramsReceiveDistinctDocumentIconsWithoutChangingTheirMetadata() {
        var home = HedSamples.example();
        var service = new HedJavaServices(identity, new IObjectSearchService.NoOp(), new BootstrapIntegrationTest.Feedback(), Locale.ENGLISH, dwelling -> dwelling.getName());
        var expected = Map.of("floor-plan", HedRepresentationIcons.FLOOR_PLAN, "view-dashboard-outline", HedRepresentationIcons.PANEL, "vector-polyline", HedRepresentationIcons.CIRCUITS);
        for (var entry : expected.entrySet()) {
            for (var prefix : List.of("/hed/mdi/", "/api/images/hed/mdi/")) {
                var oldIcons = List.of(new RepresentationIconURL(prefix + entry.getKey() + ".svg"));
                var diagram = representation(home, "My diagram", "siriusComponents://representation?type=Diagram", oldIcons);
                var id = diagram.getRepresentationMetadataId().toString();
                assertEquals(entry.getValue(), service.getHedIcon(diagram));
                assertEquals(id, service.getHedId(diagram));
                assertEquals("My diagram", service.getHedLabel(diagram));
                assertEquals(oldIcons, diagram.getIconURLs());
            }
        }
        var custom = representation(home, "Custom", "diagram", List.of(new RepresentationIconURL("/custom.svg")));
        assertEquals("/custom.svg", service.getHedIcon(custom));
        assertEquals(HedRepresentationIcons.FLOOR_PLAN, service.getHedIcon(representation(home, "No icon", "diagram", List.of())));
    }

    @Test
    void emptySemanticOwnersCanExpandToTheirTablesAndLookupRetainsTheNativeRepresentationObject() {
        var home = HedSamples.empty("Home");
        var board = HomeElectricalFactory.eINSTANCE.createDistributionBoard();
        home.getBoards().add(board);
        var table = representation(board, "Board table");
        var tableId = table.getRepresentationMetadataId().toString();
        var search = new IObjectSearchService() {
            @Override
            public Optional<Object> getObject(IEditingContext editing, String id) {
                return id.equals(tableId) ? Optional.of(table) : id.equals(identity.getId(board)) ? Optional.of(board) : Optional.empty();
            }
        };
        var service = new HedJavaServices(identity, search, new BootstrapIntegrationTest.Feedback(), Locale.ENGLISH, dwelling -> dwelling.getName());
        assertFalse(service.hasHedChildren(board));
        assertTrue(service.hasHedChildren(board, List.of(table)));
        assertEquals(List.of(table), service.getHedChildren(board, List.of(table)));
        assertSame(table, service.getHedObject(tableId, context));
        assertSame(board, service.getHedParent(table, context));
        assertFalse(service.canRepresentHedObject(table));
        assertFalse(service.canCreateHedObject(table, "Room"));
    }
}
