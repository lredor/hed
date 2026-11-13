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

import java.util.List;
import java.util.Locale;
import org.eclipse.sirius.components.collaborative.tables.services.TableCreationService;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.tables.Column;
import org.eclipse.sirius.components.tables.Table;
import org.eclipse.sirius.components.tables.components.ICustomCellDescriptor;
import org.eclipse.sirius.components.tables.descriptions.TableDescription;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import hed.domain.Messages;
import hed.homeelectrical.Dwelling;
import io.micrometer.core.instrument.MeterRegistry;

/** Initializes HED summary preferences once, leaving subsequent visibility changes to Sirius's native persistence. */
@Service
@Primary
public class HedTableCreationService extends TableCreationService {
    private final Locale locale;

    /**
     * Reuses native table creation, adding only the missing initial-visibility setting.
     * @param descriptors native and application-specific cell renderers
     * @param registry table rendering metrics
     * @param locale selects the application's table-description names
     */
    public HedTableCreationService(List<ICustomCellDescriptor> descriptors, MeterRegistry registry, Locale locale) {
        super(descriptors, registry);
        this.locale = locale;
    }

    @Override
    public Table create(String id, Object target, TableDescription description, IEditingContext context) {
        var table = super.create(id, target, description, context);
        return target instanceof Dwelling && Messages.text(locale, "table.circuits").equals(description.getLabel()) ? withHiddenName(table) : table;
    }

    /**
     * Hides the duplicate name column in a newly created summary while preserving every native column and cell.
     * The native renderer subsequently retains the user's show/hide choices; existing tables are not reset.
     * @param table the fresh native table
     * @return a table with the same identities, values and options, with its name column initially hidden
     */
    public static Table withHiddenName(Table table) {
        var columns = table.getColumns().stream().map(column -> "name".equals(column.getTargetObjectId()) ? hidden(column) : column).toList();
        return Table.newTable(table.getId()).targetObjectId(table.getTargetObjectId()).targetObjectKind(table.getTargetObjectKind()).descriptionId(table.getDescriptionId())
                .stripeRow(table.isStripeRow()).lines(table.getLines()).columns(columns).paginationData(table.getPaginationData()).globalFilter(table.getGlobalFilter())
                .columnFilters(table.getColumnFilters()).enableSubRows(table.isEnableSubRows()).columnSort(table.getColumnSort()).pageSizeOptions(table.getPageSizeOptions())
                .defaultPageSize(table.getDefaultPageSize()).build();
    }

    private static Column hidden(Column column) {
        return Column.newColumn(column.getId()).descriptionId(column.getDescriptionId()).targetObjectId(column.getTargetObjectId()).targetObjectKind(column.getTargetObjectKind())
                .headerLabel(column.getHeaderLabel()).headerIconURLs(column.getHeaderIconURLs()).headerIndexLabel(column.getHeaderIndexLabel()).width(column.getWidth())
                .resizable(column.isResizable()).hidden(true).filterVariant(column.getFilterVariant()).sortable(column.isSortable()).index(column.getIndex()).build();
    }
}
