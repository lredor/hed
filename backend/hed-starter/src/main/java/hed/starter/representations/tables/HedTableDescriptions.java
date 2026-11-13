/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.eclipse.sirius.components.view.View;
import org.eclipse.sirius.components.view.table.CellDescription;
import org.eclipse.sirius.components.view.table.CellWidgetDescription;
import org.eclipse.sirius.components.view.table.ColumnDescription;
import org.eclipse.sirius.components.view.table.TableDescription;
import org.eclipse.sirius.components.view.builder.generated.table.TableBuilders;
import org.eclipse.sirius.components.view.builder.generated.view.ViewBuilders;
import org.springframework.stereotype.Service;
import hed.domain.Messages;
import hed.starter.representations.HedRepresentationIcons;

/** Editable tables sharing domain services with the explorer and properties. */
@Service
public class HedTableDescriptions {
    private static final Map<String, List<String>> FIELDS = Map.of("equipment", List.of("name", "room", "category", "deviceType", "circuit", "controls", "ratedPowerW", "issues", "branch"), "circuits",
            List.of("name", "description", "sourceBoard", "upstreamRccb", "protectiveDevice", "phase", "conductorSectionMm2", "served", "issues", "usage"), "board",
            List.of("name", "labelText", "rail", "order", "category", "ratedCurrentA", "residualCurrentMa", "residualType", "widthModules", "protectedCircuits", "issues", "upstreamProtection"),
            "catalog", List.of("name", "kind", "ratedCurrentA", "ratedPowerW", "widthModules", "residualCurrentMa", "manufacturer", "productReference"));

    private final Locale locale;

    /**
     * Creates the localized table description contribution.
     *
     * @param locale the language of table titles and column headers
     */
    public HedTableDescriptions(Locale locale) {
        this.locale = locale;
    }

    /**
     * Lists potential columns in their declared order, before context-dependent visibility is evaluated.
     *
     * @param table the equipment, circuits, board or catalog table key
     * @return its ordered field keys, or an empty list for an unknown key
     */
    public static List<String> fieldsFor(String table) {
        return FIELDS.getOrDefault(table, List.of());
    }

    /**
     * Appends the four electrical table descriptions in a stable order.
     *
     * @param view the view receiving equipment, circuit, board and catalog tables
     */
    public void addTo(View view) {
        view.getDescriptions().add(table("equipment", "Dwelling", Set.of("name", "ratedPowerW")));
        view.getDescriptions().add(table("circuits", "Dwelling", Set.of("name", "description", "conductorSectionMm2")));
        view.getDescriptions().add(table("board", "DistributionBoard", Set.of("name", "labelText")));
        view.getDescriptions().add(table("catalog", "Catalog", Set.of("name", "ratedCurrentA", "ratedPowerW", "widthModules", "residualCurrentMa", "manufacturer", "productReference")));
    }

    /** Builds a table whose circuit phase column is instantiated only for an explicit three-phase supply. */
    private TableDescription table(String table, String rootType, Set<String> editable) {
        var builders = new TableBuilders();
        var operations = new ViewBuilders();
        var columns = new ArrayList<ColumnDescription>();
        var cells = new ArrayList<CellDescription>();
        for (String field : fieldsFor(table)) {
            // Empty candidates hide the runtime column while keeping the description's stable identifier.
            String candidates = "aql:'" + field + "'";
            if (table.equals("circuits") && field.equals("phase")) {
                candidates = "aql:if self.hasHedThreePhaseSupply() then 'phase' else Sequence{} endif";
            } else if (table.equals("circuits") && field.equals("sourceBoard")) {
                candidates = "aql:if self.boards->size() > 1 then 'sourceBoard' else Sequence{} endif";
            }
            String headerKey = table.equals("circuits") && Set.of("protectiveDevice", "conductorSectionMm2", "served").contains(field) ? "table.column.circuits." + field : "feature." + field;
            columns.add(builders.newColumnDescription().name("hed-column-" + field).semanticCandidatesExpression(candidates).headerLabelExpression(Messages.text(locale, headerKey))
                    .initialWidthExpression("180").isResizableExpression("aql:true").isSortableExpression("aql:true").build());
            CellWidgetDescription widget = editable.contains(field)
                    ? builders.newCellTextfieldWidgetDescription().body(operations.newChangeContext().expression("aql:self.editHedCell('" + field + "',newValue)").build()).build()
                    : builders.newCellLabelWidgetDescription().build();
            cells.add(builders.newCellDescription().name("hed-cell-" + field).preconditionExpression("aql:columnTargetObject = '" + field + "'").valueExpression("aql:self.getHedCell('" + field + "')")
                    .selectedTargetObjectExpression("aql:self").cellWidgetDescription(widget).build());
        }
        var row = builders.newRowDescription().name("hed-row-" + table)
                .semanticCandidatesExpression("aql:self.getHedRows('" + table + "',globalFilterData,columnFilters,columnSort)->toPaginatedData(cursor,direction,size)")
                .headerLabelExpression("aql:self.getHedLabel()").headerIconExpression("aql:self.getHedIcon()").initialHeightExpression("36").isResizableExpression("aql:true").build();
        return builders.newTableDescription().name(Messages.text(locale, "table." + table)).domainType("homeelectrical::" + rootType).preconditionExpression("aql:true")
                .titleExpression(Messages.text(locale, "table." + table)).iconExpression(HedRepresentationIcons.TABLE).enableSubRows(false)
                .columnDescriptions(columns.toArray(ColumnDescription[]::new)).rowDescription(row).cellDescriptions(cells.toArray(CellDescription[]::new)).useStripedRowsExpression("aql:true")
                .pageSizeOptionsExpression("aql:Sequence{10,25,50}").defaultPageSizeIndexExpression("0").build();
    }
}
