/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { TreeToolBar as NativeTreeToolBar, type TreeFilter } from '@eclipse-sirius/sirius-components-trees';
import type { ComponentProps } from 'react';

// Forward the public API, adapting HED highlight filters and the explorer's initial expansion.
export * from '@eclipse-sirius/sirius-components-trees';
export { HedExplorerTreeView as TreeView } from './HedExplorerTreeView';

export const PLACEMENT_FILTER = 'hed-highlight-unplaced-equipment';
export const COMPLETENESS_FILTER = 'hed-highlight-incomplete-circuits';
const highlights = new Set([PLACEMENT_FILTER, COMPLETENESS_FILTER]);

/** Activating either HED highlight disables the other, retaining unrelated native filters and immutable inputs. */
export function exclusiveHighlights(previous: TreeFilter[], next: TreeFilter[]): TreeFilter[] {
  const enabled = next.filter((filter) => highlights.has(filter.id) && filter.state);
  const selected =
    enabled.find((filter) => !previous.some((old) => old.id === filter.id && old.state)) ??
    enabled.find((filter) => filter.id === COMPLETENESS_FILTER) ??
    enabled[0];
  return next.map((filter) =>
    highlights.has(filter.id) && filter.state && filter.id !== selected?.id ? { ...filter, state: false } : filter
  );
}

/** Retains Sirius' toolbar, menu, subscriptions and per-explorer state; submits the exclusive state in one callback. */
export function TreeToolBar({
  treeFilters,
  onTreeFilterMenuItemClick,
  ...props
}: ComponentProps<typeof NativeTreeToolBar>) {
  return (
    <NativeTreeToolBar
      {...props}
      treeFilters={treeFilters}
      onTreeFilterMenuItemClick={(next) => onTreeFilterMenuItemClick(exclusiveHighlights(treeFilters, next))}
    />
  );
}
