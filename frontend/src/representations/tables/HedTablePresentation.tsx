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
const equipmentFields = ['name', 'deviceType', 'circuit', 'room', 'controls', 'issues'];

function summaryFields(table: GQLTable): string[] | undefined {
  const fields = new Set((table.columns as SemanticColumn[]).map((column) => column.targetObjectId));
  if (
    fields.has('description') &&
    fields.has('served') &&
    fields.has('upstreamRccb') &&
    [...fields].every((field) => field !== undefined && circuitFields.includes(field))
  )
    return circuitFields;
  if (
    fields.has('deviceType') &&
    fields.has('controls') &&
    fields.has('room') &&
    [...fields].every((field) => field !== undefined && equipmentFields.includes(field))
  )
    return equipmentFields;
  return undefined;
}

/** Identifies HED's circuit and equipment summaries without affecting board or catalog tables. */
export function hasHedSummaryColumns(table: GQLTable): boolean {
  return summaryFields(table) !== undefined;
}

/** Keeps the requested semantic order when native refreshes introduce columns with a zero default index. */
export function presentHedTable(table: GQLTable): GQLTable {
  const fields = summaryFields(table);
  if (!fields) return table;
  const cached = presentations.get(table);
  if (cached) return cached;
  const columns = [...table.columns] as SemanticColumn[];
  columns.sort((left, right) => fields.indexOf(left.targetObjectId!) - fields.indexOf(right.targetObjectId!));
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
