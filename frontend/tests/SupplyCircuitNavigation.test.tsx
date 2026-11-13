/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { MockedProvider } from '@apollo/client/testing';
import type { GQLSelect } from '@eclipse-sirius/sirius-components-forms';
import type { TreeItemContextMenuComponentProps } from '@eclipse-sirius/sirius-components-trees';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import {
  SupplyCircuitExplorerNavigation,
  SupplyCircuitPropertyNavigation,
  supplyCircuitQuery,
} from '../src/representations/navigation/SupplyCircuitNavigation';
import { platform } from './platform';

const widget = (value: string): GQLSelect =>
  ({ id: 'hed.Outlet.circuit', __typename: 'Select', value, readOnly: true } as GQLSelect);
const menuProps = (): Omit<TreeItemContextMenuComponentProps, 'key'> => ({
  editingContextId: 'context',
  treeId: 'tree',
  item: { id: 'device' } as TreeItemContextMenuComponentProps['item'],
  entry: null,
  readOnly: true,
  expandItem: vi.fn(),
  selectTreeItems: vi.fn(),
  onExpandedElementChange: vi.fn(),
  onClose: vi.fn(),
  expanded: [],
  maxDepth: 1,
  selectedTreeItemIds: ['device'],
});

beforeEach(() => {
  platform.setSelection.mockReset();
  platform.revealSelection.mockReset();
});

describe('supply circuit navigation', () => {
  it('selects and reveals the current circuit from read-only Details and disables an unassigned circuit', async () => {
    const { rerender } = render(
      <SupplyCircuitPropertyNavigation editingContextId="context" formId="form" widget={widget('circuit-1')} />
    );
    await userEvent.click(screen.getByRole('button', { name: 'Navigate to supply circuit' }));
    expect(platform.setSelection).toHaveBeenLastCalledWith({ entries: [{ id: 'circuit-1' }] });
    expect(platform.revealSelection).toHaveBeenLastCalledWith({ entries: [{ id: 'circuit-1' }] });
    rerender(<SupplyCircuitPropertyNavigation editingContextId="context" formId="form" widget={widget('circuit-2')} />);
    await userEvent.click(screen.getByRole('button', { name: 'Navigate to supply circuit' }));
    expect(platform.setSelection).toHaveBeenLastCalledWith({ entries: [{ id: 'circuit-2' }] });
    rerender(<SupplyCircuitPropertyNavigation editingContextId="context" formId="form" widget={widget('')} />);
    expect(screen.getByRole('button')).toBeDisabled();
    rerender(
      <SupplyCircuitPropertyNavigation
        editingContextId="context"
        formId="form"
        widget={{ ...widget('circuit-2'), id: 'hed.Outlet.room' }}
      />
    );
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
  });

  it('uses a project-scoped read query from the explorer, reveals its result and closes the menu', async () => {
    const props = menuProps();
    render(
      <MockedProvider
        mocks={[
          {
            request: { query: supplyCircuitQuery, variables: { editingContextId: 'context', objectId: 'device' } },
            result: { data: { viewer: { editingContext: { hedSupplyCircuitId: 'circuit-3' } } } },
          },
        ]}>
        <SupplyCircuitExplorerNavigation key="menu" {...props} />
      </MockedProvider>
    );
    await userEvent.click(await screen.findByRole('menuitem', { name: 'Navigate to supply circuit' }));
    expect(platform.revealSelection).toHaveBeenLastCalledWith({ entries: [{ id: 'circuit-3' }] });
    expect(props.onClose).toHaveBeenCalledOnce();
  });

  it('does not offer navigation on an object without a supply circuit', async () => {
    const resolved = vi.fn(() => ({ data: { viewer: { editingContext: { hedSupplyCircuitId: null } } } }));
    render(
      <MockedProvider
        mocks={[
          {
            request: { query: supplyCircuitQuery, variables: { editingContextId: 'context', objectId: 'device' } },
            result: resolved,
          },
        ]}>
        <SupplyCircuitExplorerNavigation key="menu" {...menuProps()} />
      </MockedProvider>
    );
    await waitFor(() => expect(resolved).toHaveBeenCalledOnce());
    expect(screen.queryByRole('menuitem')).not.toBeInTheDocument();
  });
});
