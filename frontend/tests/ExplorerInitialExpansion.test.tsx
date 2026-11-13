/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { render } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import type { ComponentProps } from 'react';
import { HedExplorerTreeView } from '../src/representations/modelexplorer/HedExplorerTreeView';

vi.mock('@eclipse-sirius/sirius-components-trees', () => ({ TreeView: () => null }));

function props(): ComponentProps<typeof HedExplorerTreeView> {
  return {
    editingContextId: 'context',
    readOnly: false,
    tree: {
      id: 'tree',
      capabilities: { useTreePalette: false },
      children: [
        {
          id: 'project',
          kind: 'hed-project',
          label: { styledStringFragments: [] },
          iconURL: [''],
          children: [],
          hasChildren: true,
          expanded: false,
          editable: false,
          selectable: false,
          deletable: false,
        },
      ],
    },
    expanded: [],
    maxDepth: 1,
    onExpandedElementChange: vi.fn(),
    textToHighlight: null,
    textToFilter: null,
    onTreeItemClick: vi.fn(),
    selectTreeItems: vi.fn(),
    selectedTreeItemIds: [],
    useTreePalette: false,
  };
}

describe('initial project expansion', () => {
  it('expands only the root once and respects later manual collapse and refreshes', () => {
    const initial = props();
    const { rerender } = render(<HedExplorerTreeView {...initial} />);
    expect(initial.onExpandedElementChange).toHaveBeenCalledExactlyOnceWith(['project'], 2);
    rerender(<HedExplorerTreeView {...initial} expanded={['project']} />);
    rerender(<HedExplorerTreeView {...initial} tree={{ ...initial.tree }} expanded={[]} />);
    expect(initial.onExpandedElementChange).toHaveBeenCalledTimes(1);
    rerender(<HedExplorerTreeView {...initial} editingContextId="another-context" />);
    expect(initial.onExpandedElementChange).toHaveBeenCalledTimes(2);
  });

  it('ignores expansion and filter parameters in native explorer subscription IDs', () => {
    const initial = props();
    initial.tree.id = 'explorer://?treeDescriptionId=hed&expandedIds=[]&activeFilterIds=[]';
    const { rerender } = render(<HedExplorerTreeView {...initial} />);
    rerender(
      <HedExplorerTreeView
        {...initial}
        tree={{ ...initial.tree, id: 'explorer://?treeDescriptionId=hed&expandedIds=[project]&activeFilterIds=[]' }}
        expanded={['project']}
      />
    );
    rerender(
      <HedExplorerTreeView
        {...initial}
        tree={{ ...initial.tree, id: 'explorer://?treeDescriptionId=hed&expandedIds=[]&activeFilterIds=[highlight]' }}
        expanded={[]}
      />
    );
    expect(initial.onExpandedElementChange).toHaveBeenCalledTimes(1);
  });

  it('waits for roots, keeps existing expansion and leaves unrelated trees alone', () => {
    const initial = props();
    const { rerender } = render(<HedExplorerTreeView {...initial} tree={{ ...initial.tree, children: [] }} />);
    expect(initial.onExpandedElementChange).not.toHaveBeenCalled();
    rerender(<HedExplorerTreeView {...initial} expanded={['project', 'catalog']} maxDepth={4} />);
    expect(initial.onExpandedElementChange).not.toHaveBeenCalled();
    rerender(<HedExplorerTreeView {...initial} expanded={[]} />);
    expect(initial.onExpandedElementChange).not.toHaveBeenCalled();
    const unrelated = props();
    unrelated.tree.children[0]!.kind = 'another-kind';
    render(<HedExplorerTreeView {...unrelated} />);
    expect(unrelated.onExpandedElementChange).not.toHaveBeenCalled();
  });
});
