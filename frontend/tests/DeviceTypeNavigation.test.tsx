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
  DeviceTypeExplorerNavigation,
  DeviceTypePropertyNavigation,
  deviceTypeQuery,
} from '../src/representations/navigation/DeviceTypeNavigation';
import { platform } from './platform';

const widget = (value: string): GQLSelect =>
  ({ id: 'hed.Outlet.deviceType', __typename: 'Select', value, readOnly: true } as GQLSelect);
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

describe('device type navigation', () => {
  it('selects and reveals the current type from read-only Details and disables an unassigned type', async () => {
    const { rerender } = render(
      <DeviceTypePropertyNavigation editingContextId="context" formId="form" widget={widget('type-1')} />
    );
    await userEvent.click(screen.getByRole('button', { name: 'Navigate to device type' }));
    expect(platform.setSelection).toHaveBeenLastCalledWith({ entries: [{ id: 'type-1' }] });
    expect(platform.revealSelection).toHaveBeenLastCalledWith({ entries: [{ id: 'type-1' }] });
    rerender(<DeviceTypePropertyNavigation editingContextId="context" formId="form" widget={widget('type-2')} />);
    await userEvent.click(screen.getByRole('button', { name: 'Navigate to device type' }));
    expect(platform.setSelection).toHaveBeenLastCalledWith({ entries: [{ id: 'type-2' }] });
    rerender(<DeviceTypePropertyNavigation editingContextId="context" formId="form" widget={widget('')} />);
    expect(screen.getByRole('button')).toBeDisabled();
    rerender(
      <DeviceTypePropertyNavigation
        editingContextId="context"
        formId="form"
        widget={{ ...widget('type-2'), id: 'hed.Outlet.room' }}
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
            request: { query: deviceTypeQuery, variables: { editingContextId: 'context', objectId: 'device' } },
            result: { data: { viewer: { editingContext: { hedDeviceTypeId: 'type-3' } } } },
          },
        ]}>
        <DeviceTypeExplorerNavigation key="menu" {...props} />
      </MockedProvider>
    );
    await userEvent.click(await screen.findByRole('menuitem', { name: 'Navigate to device type' }));
    expect(platform.revealSelection).toHaveBeenLastCalledWith({ entries: [{ id: 'type-3' }] });
    expect(props.onClose).toHaveBeenCalledOnce();
  });

  it('does not offer navigation on an object without a type', async () => {
    const resolved = vi.fn(() => ({ data: { viewer: { editingContext: { hedDeviceTypeId: null } } } }));
    render(
      <MockedProvider
        mocks={[
          {
            request: { query: deviceTypeQuery, variables: { editingContextId: 'context', objectId: 'device' } },
            result: resolved,
          },
        ]}>
        <DeviceTypeExplorerNavigation key="menu" {...menuProps()} />
      </MockedProvider>
    );
    await waitFor(() => expect(resolved).toHaveBeenCalledOnce());
    expect(screen.queryByRole('menuitem')).not.toBeInTheDocument();
  });
});
