/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { useState } from 'react';
import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import type { TreeFilter } from '@eclipse-sirius/sirius-components-trees';
import {
  COMPLETENESS_FILTER,
  PLACEMENT_FILTER,
  TreeToolBar,
  exclusiveHighlights,
} from '../src/representations/modelexplorer/ExclusiveTreeToolBar';

vi.mock('@eclipse-sirius/sirius-components-trees', () => ({
  TreeToolBar: ({
    treeFilters,
    onTreeFilterMenuItemClick,
  }: {
    treeFilters: TreeFilter[];
    onTreeFilterMenuItemClick: (filters: TreeFilter[]) => void;
  }) => (
    <>
      {treeFilters.map((filter) => (
        <label key={filter.id}>
          {filter.label}
          <input
            type="checkbox"
            checked={filter.state}
            onChange={(event) =>
              onTreeFilterMenuItemClick(
                treeFilters.map((value) => (value.id === filter.id ? { ...value, state: event.target.checked } : value))
              )
            }
          />
        </label>
      ))}
    </>
  ),
}));

const initial = (): TreeFilter[] => [
  { id: PLACEMENT_FILTER, label: 'Unplaced', state: false },
  { id: COMPLETENESS_FILTER, label: 'Incomplete circuits', state: false },
  { id: 'another-native-filter', label: 'Native filter', state: true },
];

describe('exclusive explorer highlights', () => {
  it.each([PLACEMENT_FILTER, COMPLETENESS_FILTER])(
    'activating %s disables the other without mutating inputs',
    (activated) => {
      const old = initial().map((filter) => ({ ...filter, state: filter.id !== activated }));
      const next = old.map((filter) => (filter.id === activated ? { ...filter, state: true } : filter));
      const output = exclusiveHighlights(old, next);
      expect(output.filter((filter) => filter.state).map((filter) => filter.id)).toEqual([
        activated,
        'another-native-filter',
      ]);
      expect(next.filter((filter) => filter.state)).toHaveLength(3);
    }
  );

  it('allows both highlights to be off and leaves unrelated native filters unchanged', () => {
    expect(exclusiveHighlights(initial(), initial())).toEqual(initial());
    expect(exclusiveHighlights([], [{ id: 'native', label: 'Native', state: true }])).toEqual([
      { id: 'native', label: 'Native', state: true },
    ]);
  });

  it('normalizes an old configuration containing both highlights using the server fallback priority', () => {
    const both = initial().map((filter) => ({ ...filter, state: true }));
    expect(
      exclusiveHighlights(both, both)
        .filter((filter) => filter.state)
        .map((filter) => filter.id)
    ).toEqual([COMPLETENESS_FILTER, 'another-native-filter']);
  });

  it('submits one coherent update per click and keeps two explorers independent', async () => {
    const updates = vi.fn<(filters: TreeFilter[]) => void>();
    function Explorer({ name }: { name: string }) {
      const [filters, setFilters] = useState(initial);
      return (
        <fieldset aria-label={name}>
          <TreeToolBar
            editingContextId="context"
            readOnly={false}
            treeFilters={filters}
            onTreeFilterMenuItemClick={(next) => {
              updates(next);
              setFilters(next);
            }}
            onRevealSelection={() => {}}
            onFilter={() => {}}
            treeToolBarContributionComponents={[]}
            children={null}
          />
        </fieldset>
      );
    }
    render(
      <>
        <Explorer name="First" />
        <Explorer name="Second" />
      </>
    );
    const first = within(screen.getByRole('group', { name: 'First' }));
    const second = within(screen.getByRole('group', { name: 'Second' }));
    await userEvent.click(first.getByRole('checkbox', { name: 'Unplaced' }));
    await userEvent.click(first.getByRole('checkbox', { name: 'Incomplete circuits' }));
    expect(first.getByRole('checkbox', { name: 'Unplaced' })).not.toBeChecked();
    expect(first.getByRole('checkbox', { name: 'Incomplete circuits' })).toBeChecked();
    expect(first.getByRole('checkbox', { name: 'Native filter' })).toBeChecked();
    expect(second.getByRole('checkbox', { name: 'Incomplete circuits' })).not.toBeChecked();
    expect(updates).toHaveBeenCalledTimes(2);
    expect(
      updates.mock.calls.every(
        ([filters]: [TreeFilter[]]) =>
          filters.filter((filter) => filter.state && [PLACEMENT_FILTER, COMPLETENESS_FILTER].includes(filter.id))
            .length === 1
      )
    ).toBe(true);
  });
});
