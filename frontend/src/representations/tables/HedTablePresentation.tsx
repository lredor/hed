/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import type { ComponentType } from 'react';
import Tooltip from '@mui/material/Tooltip';
import { useTranslation } from 'react-i18next';
import type { GQLColumn, GQLTable } from '@eclipse-sirius/sirius-components-tables';

type SemanticColumn = GQLColumn & { targetObjectId?: string };
const circuitFields = [
  'name',
  'description',
  'sourceBoard',
  'upstreamRccb',
  'protectiveDevice',
  'phase',
  'conductorSectionMm2',
  'served',
  'issues',
  'usage',
];
const presentations = new WeakMap<GQLTable, GQLTable>();

/** Identifies only HED's circuit summary, including when optional columns are not instantiated. */
export function hasHedSummaryColumns(table: GQLTable): boolean {
  const fields = new Set((table.columns as SemanticColumn[]).map((column) => column.targetObjectId));
  return (
    fields.has('description') &&
    fields.has('served') &&
    fields.has('upstreamRccb') &&
    [...fields].every((field) => field !== undefined && circuitFields.includes(field))
  );
}

/** Keeps the requested semantic order when native refreshes introduce columns with a zero default index. */
export function presentHedTable(table: GQLTable): GQLTable {
  if (!hasHedSummaryColumns(table)) return table;
  const cached = presentations.get(table);
  if (cached) return cached;
  const columns = [...table.columns] as SemanticColumn[];
  columns.sort(
    (left, right) => circuitFields.indexOf(left.targetObjectId!) - circuitFields.indexOf(right.targetObjectId!)
  );
  const result = columns.every((column, index) => table.columns[index] === column) ? table : { ...table, columns };
  presentations.set(table, result);
  return result;
}

/** Shortens the RCCB heading while retaining its full translated term in a tooltip and native column metadata. */
export function hedColumnHeader(column: SemanticColumn, NativeHeader: ComponentType<{ column: GQLColumn }>) {
  if (column.targetObjectId !== 'upstreamRccb') return <NativeHeader column={column} />;
  return <ResidualHeader column={column} NativeHeader={NativeHeader} />;
}

function ResidualHeader({
  column,
  NativeHeader,
}: {
  column: SemanticColumn;
  NativeHeader: ComponentType<{ column: GQLColumn }>;
}) {
  const { t } = useTranslation('home-electrical-designer');
  return (
    <Tooltip title={column.headerLabel}>
      <div>
        <NativeHeader column={{ ...column, headerLabel: t('tables.upstreamRccbShort') }} />
      </div>
    </Tooltip>
  );
}
