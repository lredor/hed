/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { vi } from 'vitest';
import type { Node, NodeProps } from '@xyflow/react';
import {
  GQLViewModifier,
  type GQLDiagram,
  type GQLNode,
  type GQLNodeStyle,
  type INodeConverter,
  type NodeData,
} from '@eclipse-sirius/sirius-components-diagrams';
import { createElement, type ComponentType } from 'react';
import { render } from '@testing-library/react';

/** Minimal native graph fixture; fields unrelated to the HED converter are left at neutral values. */
export const semanticNode = (
  id: string,
  style: Record<string, unknown>,
  overrides: Partial<GQLNode<GQLNodeStyle>> = {}
): GQLNode<GQLNodeStyle> => ({
  id,
  type: 'node',
  targetObjectId: `semantic-${id}`,
  targetObjectKind: 'hed',
  targetObjectLabel: `Name ${id}`,
  descriptionId: 'node-description',
  state: GQLViewModifier.Normal,
  pinned: false,
  insideLabel: undefined,
  outsideLabels: [],
  style: {
    __typename: 'ImageNodeStyle',
    childrenLayoutStrategy: { __typename: 'FreeFormLayoutStrategy', kind: 'FreeForm' },
    ...style,
  },
  borderNodes: [],
  childNodes: [],
  position: { x: 0, y: 0 },
  size: { width: 0, height: 0 },
  defaultWidth: null,
  defaultHeight: null,
  labelEditable: false,
  deletable: true,
  customizedStyleProperties: [],
  initialBorderNodePosition: 'NONE',
  decorators: [],
  ...overrides,
});

export const savedNode = (id: string, x: number, y: number, width = 24, height = 24) => ({
  id,
  position: { x, y },
  size: { width, height },
  minComputedSize: { width, height },
  resizedByUser: false,
  movedByUser: true,
  handleLayoutData: [],
});

/** Invoke the registered converter just as the platform does, keeping conversion output inspectable. */
export function convert(
  converter: INodeConverter,
  node: GQLNode<GQLNodeStyle>,
  parent: GQLNode<GQLNodeStyle> | null = null,
  saved: GQLDiagram['layoutData']['nodeLayoutData'] = [],
  described = true
) {
  const nodes: Node<NodeData>[] = [];
  const engine = { convertNodes: vi.fn() };
  const description = {
    id: 'node-description',
    childNodeDescriptionIds: ['node-description'],
  } as Parameters<INodeConverter['handle']>[8][number];
  const diagram: GQLDiagram = {
    id: 'diagram-1',
    targetObjectId: 'storey-1',
    metadata: { kind: 'Diagram', label: 'Floor plan' },
    nodes: [],
    edges: [],
    style: { background: 'white' },
    layoutData: { nodeLayoutData: saved, labelLayoutData: [], edgeLayoutData: [], autoLaidOut: false },
  };
  converter.handle(
    engine,
    diagram,
    node,
    [],
    parent,
    false,
    nodes,
    { nodeDescriptions: [description] } as Parameters<INodeConverter['handle']>[7],
    described ? [description] : []
  );
  return { nodes, engine };
}

export function renderNode(component: ComponentType<NodeProps>, node: Node<NodeData>) {
  return render(
    createElement(component, {
      id: node.id,
      data: node.data,
      type: node.type ?? 'native',
      selected: false,
      dragging: false,
      draggable: node.draggable ?? true,
      selectable: node.selectable ?? true,
      deletable: node.deletable ?? true,
      zIndex: node.zIndex ?? 0,
      isConnectable: false,
      positionAbsoluteX: node.position.x,
      positionAbsoluteY: node.position.y,
    })
  );
}
