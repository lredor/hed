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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.interpreter.AQLInterpreter;
import org.eclipse.sirius.components.tables.Column;
import org.eclipse.sirius.components.tables.Table;
import org.eclipse.sirius.components.tables.PaginationData;
import org.eclipse.sirius.components.tables.ColumnFilter;
import org.eclipse.sirius.components.tables.ColumnSort;
import org.eclipse.sirius.components.view.builder.generated.view.ViewBuilders;
import org.eclipse.sirius.components.view.table.CellLabelWidgetDescription;
import org.eclipse.sirius.components.view.table.CellTextfieldWidgetDescription;
import org.eclipse.sirius.components.view.table.TableDescription;
import org.junit.jupiter.api.Test;
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.homeelectrical.ElectricalLoad;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.Outlet;
import hed.homeelectrical.NamedElement;
import hed.homeelectrical.HomeElectricalPackage;
import hed.starter.representations.modelexplorer.HedJavaServices;
import hed.starter.representations.tables.HedTableDescriptions;
import hed.starter.representations.tables.HedTableCreationService;
import hed.starter.representations.tables.HedEquipmentTableScope;

/** Checks table filtering, semantic edits and the boundary between editable and calculated cells. */
class TableIntegrationTest {
    private HedJavaServices services(BootstrapIntegrationTest.Feedback feedback) {
        return new HedJavaServices(new BootstrapIntegrationTest.Identity(), new IObjectSearchService.NoOp(), feedback, Locale.ENGLISH, dwelling -> dwelling.getName());
    }

    @Test
    void circuitSummaryUsesConciseOrderedColumnsAndOnlyShowsTheSourceForSeveralBoards() {
        var home = HedSamples.example();
        var view = new ViewBuilders().newView().build();
        new HedTableDescriptions(Locale.FRENCH).addTo(view);
        var table = (TableDescription) view.getDescriptions().get(1);
        assertEquals(List.of("name", "description", "sourceBoard", "upstreamRccb", "protectiveDevice", "phase", "conductorSectionMm2", "served", "issues", "usage"),
                HedTableDescriptions.fieldsFor("circuits"));
        var headers = table.getColumnDescriptions().stream().map(column -> column.getHeaderLabelExpression()).toList();
        assertEquals("Protection", headers.get(4));
        assertEquals("Section (mm²)", headers.get(6));
        assertEquals("Équipements", headers.get(7));
        var source = table.getColumnDescriptions().stream().filter(column -> column.getName().equals("hed-column-sourceBoard")).findFirst().orElseThrow();
        var interpreter = new AQLInterpreter(List.of(), List.of(), List.of(HomeElectricalPackage.eINSTANCE));
        assertEquals(List.of("sourceBoard"), interpreter.evaluateExpression(Map.of("self", home), source.getSemanticCandidatesExpression()).asObjects().orElseThrow());
        home.getBoards().removeLast();
        assertTrue(interpreter.evaluateExpression(Map.of("self", home), source.getSemanticCandidatesExpression()).asObjects().orElseThrow().isEmpty());
        home.getBoards().clear();
        assertTrue(interpreter.evaluateExpression(Map.of("self", home), source.getSemanticCandidatesExpression()).asObjects().orElseThrow().isEmpty());
        var description = table.getCellDescriptions().stream().filter(cell -> cell.getName().equals("hed-cell-description")).findFirst().orElseThrow();
        assertInstanceOf(CellTextfieldWidgetDescription.class, description.getCellWidgetDescription());
        var service = services(new BootstrapIntegrationTest.Feedback());
        var editableCircuit = HedSamples.example().getCircuits().getFirst();
        service.editHedCell(editableCircuit, "description", "Circuit côté fenêtre");
        assertEquals("Circuit côté fenêtre", service.getHedCell(editableCircuit, "description"));
    }

    @Test
    void defaultNameVisibilityPreservesNativeTableIdentitiesCellsAndColumnSettings() {
        var name = Column.newColumn(UUID.randomUUID()).descriptionId("name-description").targetObjectId("name").targetObjectKind("string").headerLabel("Name").headerIconURLs(List.of())
                .headerIndexLabel("").width(180).resizable(true).hidden(false).filterVariant("text").sortable(true).index(0).build();
        var source = Column.newColumn(UUID.randomUUID()).descriptionId("source-description").targetObjectId("sourceBoard").targetObjectKind("string").headerLabel("Source").headerIconURLs(List.of())
                .headerIndexLabel("").width(200).resizable(true).hidden(false).filterVariant("text").sortable(true).index(1).build();
        var table = Table.newTable("summary").descriptionId("description").targetObjectId("home").targetObjectKind("Dwelling").stripeRow(true).lines(List.of()).columns(List.of(name, source))
                .paginationData(new PaginationData(false, false, 0)).globalFilter("filter").columnFilters(List.of(new ColumnFilter("name", "Q"))).enableSubRows(false)
                .columnSort(List.of(new ColumnSort("name", true))).pageSizeOptions(List.of(10, 25)).defaultPageSize(10).build();
        var result = HedTableCreationService.withHiddenName(table);
        assertTrue(result.getColumns().getFirst().isHidden());
        assertFalse(table.getColumns().getFirst().isHidden());
        assertFalse(result.getColumns().get(1).isHidden());
        assertEquals(table.getId(), result.getId());
        assertEquals(table.getDescriptionId(), result.getDescriptionId());
        assertEquals(table.getTargetObjectId(), result.getTargetObjectId());
        assertEquals(table.getLines(), result.getLines());
        assertEquals(table.getColumnFilters(), result.getColumnFilters());
        assertEquals(table.getColumnSort(), result.getColumnSort());
        assertEquals(table.getGlobalFilter(), result.getGlobalFilter());
        assertEquals(name.getId(), result.getColumns().getFirst().getId());
        assertEquals(name.getWidth(), result.getColumns().getFirst().getWidth());
        assertEquals(name.getFilterVariant(), result.getColumns().getFirst().getFilterVariant());
        assertTrue(result.getColumns().getFirst().isSortable());
    }

    @Test
    void filtersAndSortingAreAppliedToAllRowsBeforePagination() {
        var home = HedSamples.example();
        var service = services(new BootstrapIntegrationTest.Feedback());
        var filtered = service.getHedRows(home, "equipment", "", List.of(new ColumnFilter("deviceType", "outlet")), null);
        assertEquals(1, filtered.size());
        assertInstanceOf(Outlet.class, filtered.get(0));
        var all = service.getHedRows(home, "equipment", "", List.of(), List.of(new ColumnSort("name", true)));
        assertEquals(home.getEquipment().size(), all.size());
        var names = all.stream().map(object -> service.getHedCell(object, "name")).toList();
        var expected = new ArrayList<>(names);
        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());
        assertEquals(expected, names);
        assertTrue(service.getHedRows(home, "equipment", "nonexistent equipment", List.of(), null).isEmpty());
    }

    @Test
    void mixedNumericAndTextNamesUseTheSameTextOrderInEveryTable() {
        var home = HedSamples.example();
        var service = services(new BootstrapIntegrationTest.Feedback());
        var labels = List.of("2", "10", "10-A", "01", "outlet", "Outlet", "");
        for (var table : List.of("equipment", "circuits", "board", "catalog")) {
            EObject root = switch (table) {
                case "board" -> home.getBoards().getFirst();
                case "catalog" -> home.getCatalog();
                default -> home;
            };
            var expected = new ArrayList<>(service.getHedRows(root, table, "", null, null));
            for (int index = 0; index < expected.size(); index++) {
                ((NamedElement) expected.get(index)).setName(labels.get(index % labels.size()));
            }
            var ascending = Comparator.comparing((EObject row) -> service.getHedCell(row, "name"), String.CASE_INSENSITIVE_ORDER);
            expected.sort(ascending);
            assertEquals(expected, service.getHedRows(root, table, "", null, List.of(new ColumnSort("name", false))), table);
            expected.sort(ascending.reversed());
            assertEquals(expected, service.getHedRows(root, table, "", null, List.of(new ColumnSort("name", true))), table);
        }
    }

    @Test
    void mixedNamesDoNotViolateTheSortContractBeforePagination() {
        var home = HedSamples.empty("Sorting regression");
        var service = services(new BootstrapIntegrationTest.Feedback());
        // This order triggered TimSort's comparator-contract exception with pair-dependent numeric/text comparisons.
        var labels = List.of("2", "20", "10", "2", "20", "01", "2-A", "1", "1", "11", "10-A", "100", "20", "11", "3", "2-A", "20", "1", "10-A", "2-A", "100", "20", "11", "1", "10", "1-A", "01", "10",
                "01", "10", "11", "2-A", "2", "1", "1", "10-A", "10-A", "10-A", "1", "10-A", "2-A", "3", "1", "10", "10-A", "1-A", "01", "2-A", "11", "1", "01", "100", "10", "20", "100", "2-A", "100",
                "01", "3", "3", "100", "10", "10-A", "11");
        for (var label : labels) {
            HedDomain.addEquipment(home, HedDomain.type(home, hed.homeelectrical.DeviceKind.OUTLET)).setName(label);
        }
        var expected = labels.stream().sorted(String.CASE_INSENSITIVE_ORDER).toList();
        assertEquals(expected, service.getHedRows(home, "equipment", "", null, List.of(new ColumnSort("name", false))).stream().map(row -> service.getHedCell(row, "name")).toList());
        assertEquals(labels.size(), home.getEquipment().size());
    }

    @Test
    void numericColumnsSortMissingValuesAndUseTextTieBreakersInBothDirections() {
        var home = HedSamples.empty("Numeric sorting");
        var types = home.getCatalog().getDeviceTypes();
        var values = java.util.Arrays.asList(null, 10d, 2d, 0.5d);
        for (int index = 0; index < types.size(); index++) {
            types.get(index).setRatedCurrentA(values.get(index % values.size()));
            types.get(index).setName(index % 2 == 0 ? "2" : "10-A");
        }
        var service = services(new BootstrapIntegrationTest.Feedback());
        for (boolean descending : List.of(false, true)) {
            Comparator<hed.homeelectrical.DeviceType> ratings = Comparator.comparing(hed.homeelectrical.DeviceType::getRatedCurrentA, Comparator.nullsFirst(Double::compare));
            if (descending) {
                ratings = ratings.reversed();
            }
            var expected = types.stream().sorted(ratings.thenComparing(hed.homeelectrical.DeviceType::getName, String.CASE_INSENSITIVE_ORDER)).toList();
            assertEquals(expected, service.getHedRows(home.getCatalog(), "catalog", "", null, List.of(new ColumnSort("ratedCurrentA", descending), new ColumnSort("name", false))));
        }
    }

    @Test
    void boardRowsFollowRailsAndUseLiveCatalogRatingsForNumericSorting() {
        var home = HedSamples.example();
        var board = home.getBoards().get(0);
        var feedback = new BootstrapIntegrationTest.Feedback();
        var service = services(feedback);
        List<EObject> expected = new ArrayList<>();
        board.getRails().forEach(rail -> expected.addAll(rail.getDevices()));
        assertEquals(expected, service.getHedRows(board, "board", "", null, null));
        var device = board.getDevices().get(0);
        assertEquals("63.0", service.getHedCell(device, "ratedCurrentA"));
        service.editHedCell(device, "ratedCurrentA", "40");
        assertEquals("63.0", service.getHedCell(device, "ratedCurrentA"));
        assertEquals(1, feedback.messages.size());
        HedDomain.set(device.getDeviceType(), "ratedCurrentA", 40d);
        assertEquals("40.0", service.getHedCell(device, "ratedCurrentA"));
        var ratings = service.getHedRows(board, "board", "", null, List.of(new ColumnSort("ratedCurrentA", false))).stream().map(object -> Double.valueOf(service.getHedCell(object, "ratedCurrentA")))
                .toList();
        assertEquals(ratings.stream().sorted().toList(), ratings);
    }

    @Test
    void invalidCellEditPreservesTheValueAndReportsTheDomainError() {
        var home = HedSamples.example();
        var feedback = new BootstrapIntegrationTest.Feedback();
        var service = services(feedback);
        var light = home.getEquipment().stream().filter(ElectricalLoad.class::isInstance).findFirst().orElseThrow();
        var before = light.getRatedPowerW();
        service.editHedCell(light, "ratedPowerW", "-10");
        assertEquals(before, light.getRatedPowerW());
        assertEquals(1, feedback.messages.size());
    }

    @Test
    void boardUpstreamProtectionUsesTheDirectSupplyAndParticipatesInFiltering() {
        var home = HedSamples.example();
        var board = home.getBoards().get(0);
        var service = services(new BootstrapIntegrationTest.Feedback());
        var rccb = board.getDevices().get(0);
        var breaker = board.getDevices().get(1);
        var serviceBreaker = home.getServiceConnection().getMainBreaker();
        assertEquals(serviceBreaker.getName(), service.getHedCell(rccb, "upstreamProtection"));
        assertEquals("ID1", service.getHedCell(breaker, "upstreamProtection"));
        assertEquals(List.of(rccb), service.getHedRows(board, "board", serviceBreaker.getName(), null, null));
        assertEquals(HedDomain.downstreamDevices(rccb), service.getHedRows(board, "board", "", List.of(new ColumnFilter("upstreamProtection", "ID1")), null));
        HedDomain.set(rccb, "upstreamServiceBreaker", null);
        assertEquals("", service.getHedCell(rccb, "upstreamProtection"));
        HedDomain.set(breaker, "upstreamRccb", null);
        HedDomain.set(breaker, "upstreamServiceBreaker", serviceBreaker);
        assertEquals(serviceBreaker.getName(), service.getHedCell(breaker, "upstreamProtection"));
        var view = new ViewBuilders().newView().build();
        new HedTableDescriptions(Locale.ENGLISH).addTo(view);
        var table = view.getDescriptions().stream().map(TableDescription.class::cast).filter(description -> description.getDomainType().endsWith("::DistributionBoard")).findFirst().orElseThrow();
        var upstreamCell = table.getCellDescriptions().stream().filter(cell -> cell.getName().equals("hed-cell-upstreamProtection")).findFirst().orElseThrow();
        assertInstanceOf(CellLabelWidgetDescription.class, upstreamCell.getCellWidgetDescription());
    }

    @Test
    void descriptionsKeepNamesOnlyAndCatalogOwnedSpecificationsReadOnlyOnInstances() {
        var home = HedSamples.example();
        var service = services(new BootstrapIntegrationTest.Feedback());
        assertTrue(service.canRepresentHedObject(home));
        assertTrue(service.canRepresentHedObject(home.getBoards().get(0)));
        assertTrue(service.canRepresentHedObject(home.getCatalog()));
        assertFalse(service.canRepresentHedObject(home.getBoards().get(0).getRails().get(0)));
        var view = new ViewBuilders().newView().build();
        new HedTableDescriptions(Locale.ENGLISH).addTo(view);
        var descriptions = view.getDescriptions().stream().map(TableDescription.class::cast).toList();
        var board = descriptions.stream().filter(table -> table.getDomainType().endsWith("::DistributionBoard")).findFirst().orElseThrow();
        var catalog = descriptions.stream().filter(table -> table.getDomainType().endsWith("::Catalog")).findFirst().orElseThrow();
        var boardCurrent = board.getCellDescriptions().stream().filter(cell -> cell.getName().equals("hed-cell-ratedCurrentA")).findFirst().orElseThrow();
        var catalogCurrent = catalog.getCellDescriptions().stream().filter(cell -> cell.getName().equals("hed-cell-ratedCurrentA")).findFirst().orElseThrow();
        assertInstanceOf(CellLabelWidgetDescription.class, boardCurrent.getCellWidgetDescription());
        assertInstanceOf(CellTextfieldWidgetDescription.class, catalogCurrent.getCellWidgetDescription());
        for (String table : List.of("equipment", "circuits", "board", "catalog")) {
            assertFalse(HedTableDescriptions.fieldsFor(table).contains("reference"));
            assertFalse(HedTableDescriptions.fieldsFor(table).contains("frenchName"));
        }
    }

    @Test
    void equipmentTableDisplaysAndSearchesBranchNamesWithoutDuplicatingDevices() {
        var home = HedSamples.example();
        var branch = HedDomain.named(HedDomain.F.createCircuitBranch(), "Window side");
        ((FinalCircuit) home.getCircuits().getFirst()).getBranches().add(branch);
        HedDomain.initializeFirstBranch(branch);
        var equipment = home.getEquipment().get(1);
        HedDomain.assignToBranch(branch, List.of(equipment));
        var service = services(new BootstrapIntegrationTest.Feedback());
        assertEquals("Window side", service.getHedCell(equipment, "branch"));
        assertEquals(home.getEquipment().stream().filter(device -> device.getBranch() == branch).toList(), service.getHedRows(home, "equipment", "Window side", null, null));
        assertEquals(home.getEquipment().size(), service.getHedRows(home, "equipment", "", null, null).size());
        var view = new ViewBuilders().newView().build();
        new HedTableDescriptions(Locale.ENGLISH).addTo(view);
        var equipmentTable = (TableDescription) view.getDescriptions().getFirst();
        var circuitCell = equipmentTable.getCellDescriptions().stream().filter(cell -> cell.getName().equals("hed-cell-circuit")).findFirst().orElseThrow();
        assertInstanceOf(CellLabelWidgetDescription.class, circuitCell.getCellWidgetDescription());
        assertEquals(equipment.getCircuit().getName() + " / Window side", service.getHedCell(equipment, "circuit"));
    }

    @Test
    void inventoriesFollowLiveLocationScopesAndKeepUnlocatedEquipmentOnlyAtDwellingLevel() {
        var home = HedSamples.example();
        var ground = home.getStoreys().getFirst();
        var first = home.getStoreys().getLast();
        var living = ground.getRooms().getFirst();
        var bedroom = first.getRooms().getFirst();
        var service = services(new BootstrapIntegrationTest.Feedback());
        assertEquals(7, service.getHedRows(home, "equipment", "", null, null).size());
        assertEquals(6, service.getHedRows(ground, "equipment", "", null, null).size());
        assertEquals(6, service.getHedRows(living, "equipment", "", null, null).size());
        assertEquals(1, service.getHedRows(first, "equipment", "", null, null).size());
        var light = home.getEquipment().getFirst();
        light.setRoom(bedroom);
        assertEquals(5, service.getHedRows(living, "equipment", "", null, null).size());
        assertEquals(2, service.getHedRows(first, "equipment", "", null, null).size());
        assertEquals("First floor - Bedroom", service.getHedCell(light, "room"));
        light.setRoom(null);
        assertEquals("", service.getHedCell(light, "room"));
        assertEquals(7, service.getHedRows(home, "equipment", "", null, null).size());
        assertEquals(1, service.getHedRows(first, "equipment", "", null, null).size());
        assertTrue(HedEquipmentTableScope.equipment(HedDomain.F.createRoom()).isEmpty());
        assertTrue(HedEquipmentTableScope.equipment(home.getCatalog()).isEmpty());
    }

    @Test
    void equipmentDescriptionAcceptsOnlyLocationOwnersAndUsesReadOnlyCompositeRelationships() {
        var home = HedSamples.example();
        var service = services(new BootstrapIntegrationTest.Feedback());
        var view = new ViewBuilders().newView().build();
        new HedTableDescriptions(Locale.FRENCH).addTo(view);
        var table = (TableDescription) view.getDescriptions().getFirst();
        assertEquals("homeelectrical::NamedElement", table.getDomainType());
        assertEquals(List.of("name", "deviceType", "circuit", "room", "controls", "issues"), HedTableDescriptions.fieldsFor("equipment"));
        assertEquals(List.of("Nom", "Type", "Circuit / Branche", "Étage / Pièce", "Fonctions de commande", "Anomalies"),
                table.getColumnDescriptions().stream().map(column -> column.getHeaderLabelExpression()).toList());
        var interpreter = new AQLInterpreter(List.of(), List.of(service), List.of(HomeElectricalPackage.eINSTANCE));
        for (EObject owner : List.of(home, home.getStoreys().getFirst(), home.getStoreys().getFirst().getRooms().getFirst())) {
            assertTrue(service.canRepresentHedObject(owner));
            assertEquals(true, interpreter.evaluateExpression(Map.of("self", owner), table.getPreconditionExpression()).asBoolean().orElseThrow());
        }
        for (EObject owner : List.of(home.getCatalog(), home.getEquipment().getFirst(), home.getCircuits().getFirst(), home.getBoards().getFirst())) {
            assertEquals(false, interpreter.evaluateExpression(Map.of("self", owner), table.getPreconditionExpression()).asBoolean().orElseThrow());
        }
        assertFalse(service.canCreateHedEquipmentTable(null));
        for (var cell : table.getCellDescriptions()) {
            if (cell.getName().equals("hed-cell-name")) {
                assertInstanceOf(CellTextfieldWidgetDescription.class, cell.getCellWidgetDescription());
            } else {
                assertInstanceOf(CellLabelWidgetDescription.class, cell.getCellWidgetDescription());
            }
        }
    }

    @Test
    void compositeAssignmentsParticipateInFilteringAndRetiredColumnsDoNotHideExistingRows() {
        var home = HedSamples.example();
        var service = services(new BootstrapIntegrationTest.Feedback());
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var branch = HedDomain.named(HedDomain.F.createCircuitBranch(), "Window side");
        circuit.getBranches().add(branch);
        HedDomain.initializeFirstBranch(branch);
        var light = home.getEquipment().getFirst();
        String assignment = circuit.getName() + " / Window side";
        assertEquals(assignment, service.getHedCell(light, "circuit"));
        assertEquals("Ground floor - Living room", service.getHedCell(light, "room"));
        assertEquals(branch.getEquipment(), service.getHedRows(home, "equipment", assignment, null, null));
        assertEquals(6, service.getHedRows(home, "equipment", "Ground floor - Living room", null, null).size());
        assertEquals(6, service.getHedRows(home, "equipment", "", List.of(new ColumnFilter("room", "Ground floor - Living room")), null).size());
        assertEquals(home.getEquipment(), service.getHedRows(home, "equipment", "", List.of(new ColumnFilter("category", "removed value")), List.of(new ColumnSort("branch", true))));
        branch.setName("");
        assertEquals(circuit.getName(), service.getHedCell(light, "circuit"));
        light.setCircuit(null);
        assertEquals("", service.getHedCell(light, "circuit"));
    }
}
