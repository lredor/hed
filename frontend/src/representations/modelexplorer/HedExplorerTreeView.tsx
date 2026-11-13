/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { TreeView as NativeTreeView } from '@eclipse-sirius/sirius-components-trees';
import { useEffect, useRef, type ComponentProps } from 'react';

/** Expands HED project roots once per opened explorer, then leaves expansion entirely under user control. */
export function HedExplorerTreeView(props: ComponentProps<typeof NativeTreeView>) {
  const { editingContextId, tree, expanded, maxDepth, onExpandedElementChange } = props;
  const initialized = useRef<string | null>(null);
  useEffect(() => {
    // Sirius includes expanded IDs and active filters in the tree ID. Neither starts a new explorer.
    const parameters = new URLSearchParams(tree.id.slice(tree.id.indexOf('?') + 1));
    const key = `${editingContextId}:${parameters.get('treeDescriptionId') ?? tree.id}`;
    if (initialized.current === key) return;
    const roots = tree.children.filter((item) => item.kind === 'hed-project');
    if (roots.length === 0) return;
    initialized.current = key;
    const additional = roots.map((root) => root.id).filter((id) => !expanded.includes(id));
    if (additional.length > 0) onExpandedElementChange([...expanded, ...additional], Math.max(2, maxDepth));
  }, [editingContextId, tree, expanded, maxDepth, onExpandedElementChange]);
  return <NativeTreeView {...props} />;
}
