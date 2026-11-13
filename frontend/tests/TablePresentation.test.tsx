/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { render, screen, fireEvent } from '@testing-library/react';
import type { GQLTable, GQLColumn } from '@eclipse-sirius/sirius-components-tables';
import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import i18n from 'i18next';
import { describe, expect, it } from 'vitest';
import {
  presentHedTable,
  hasHedSummaryColumns,
  hedColumnHeader,
} from '../src/representations/tables/HedTablePresentation';
import { adaptTablePresentation } from '../table-presentation-adapter';

const column = (field: string) => ({
  id: 'id-' + field,
  targetObjectId: field,
  headerLabel: field,
  headerIndexLabel: '',
  headerIconURLs: [],
  width: 180,
  hidden: field === 'name',
  isResizable: true,
  isSortable: true,
  filterVariant: 'text' as const,
});
const table = (fields: string[]): GQLTable => ({
  id: 'table',
  targetObjectId: 'home',
  columns: fields.map(column),
  lines: [],
  stripeRow: true,
  enableSubRows: false,
  pageSizeOptions: [10],
  defaultPageSize: 10,
  globalFilter: null,
  columnFilters: [],
  columnSort: [],
  paginationData: { hasPreviousPage: false, hasNextPage: false, totalRowCount: 0 },
});

describe('summary table presentation', () => {
  it('keeps conditional columns at their requested position without mutating server state or visibility', () => {
    const input = table([
      'sourceBoard',
      'phase',
      'name',
      'description',
      'usage',
      'upstreamRccb',
      'issues',
      'conductorSectionMm2',
      'protectiveDevice',
      'served',
    ]);
    Object.freeze(input.columns);
    Object.freeze(input);
    const result = presentHedTable(input);
    expect(result.columns.map((item) => (item as ReturnType<typeof column>).targetObjectId)).toEqual([
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
    ]);
    expect(result.columns[0].hidden).toBe(true);
    expect(result.lines).toBe(input.lines);
    expect(result.columnFilters).toBe(input.columnFilters);
    expect(result.columnSort).toBe(input.columnSort);
    expect(result.columns[2]).toBe(input.columns[0]);
    expect(presentHedTable(input)).toBe(result);
  });

  it('retains identity when already ordered and leaves other tables completely native', () => {
    const input = table([
      'name',
      'description',
      'upstreamRccb',
      'protectiveDevice',
      'conductorSectionMm2',
      'served',
      'issues',
      'usage',
    ]);
    expect(presentHedTable(input)).toBe(input);
    const catalog = table(['name', 'kind', 'ratedCurrentA']);
    expect(presentHedTable(catalog)).toBe(catalog);
    expect(hasHedSummaryColumns(catalog)).toBe(false);
    const unknown = table(['description', 'upstreamRccb', 'served', 'unknown-field']);
    expect(presentHedTable(unknown)).toBe(unknown);
  });

  it.each([
    ['fr', 'Interrupteur différentiel amont', 'Inter diff'],
    ['en', 'Upstream RCCB', 'RCCB'],
  ])('shows a short heading and the full %s tooltip', async (language, full, short) => {
    await i18n.changeLanguage(language);
    const input = { ...column('upstreamRccb'), headerLabel: full };
    const Native = ({ column: value }: { column: GQLColumn }) => <span>{value.headerLabel}</span>;
    render(hedColumnHeader(input, Native));
    fireEvent.mouseOver(screen.getByText(short));
    expect(await screen.findByRole('tooltip')).toHaveTextContent(full);
    expect(input.headerLabel).toBe(full);
  });

  it('delegates unrelated headers to the native component', () => {
    const input = column('sourceBoard');
    const Native = ({ column: value }: { column: GQLColumn }) => <span>{value.headerLabel}</span>;
    render(hedColumnHeader(input, Native));
    expect(screen.getByText('sourceBoard')).toBeVisible();
    expect(screen.queryByRole('tooltip')).toBeNull();
  });

  it('adapts exactly the pinned native renderer boundaries and rejects incompatible upgrades', () => {
    const source = readFileSync(
      resolve('node_modules/@eclipse-sirius/sirius-components-tables/dist/sirius-components-tables.es.js'),
      'utf8'
    );
    const result = adaptTablePresentation(source, '/HedTablePresentation.tsx');
    expect(result).toContain('hedColumnHeader(column, HedNativeColumnHeader)');
    expect(result).toContain('table = presentHedTable(table)');
    expect(result).toContain('enableColumnOrdering = enableColumnOrdering && !hasHedSummaryColumns(table)');
    expect(() => adaptTablePresentation('incompatible version', '/adapter')).toThrow(/boundaries changed/);
  });
});
