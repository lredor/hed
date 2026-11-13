/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { describe, expect, it } from 'vitest';
import { screen } from '@testing-library/react';
import { DiagramContext } from '@eclipse-sirius/sirius-components-diagrams';
import type { ConnectionHandle } from '@eclipse-sirius/sirius-components-diagrams';
import type { NodeProps } from '@xyflow/react';
import { Position } from '@xyflow/react';
import {
  CircuitJunctionNode,
  circuitJunctionNodeRegistry,
} from '../src/representations/diagrams/circuits/CircuitJunction';
import { convert, renderNode, savedNode, semanticNode } from './diagramFixtures';
import { useContext } from 'react';

const converter = circuitJunctionNodeRegistry.nodeConverters[0];
const layout = circuitJunctionNodeRegistry.nodeLayoutHandlers[0];
const source = () => semanticNode('junction', { __typename: 'CircuitJunctionNodeStyle' });

describe('manipulable circuit junctions', () => {
  it('uses a fixed 24-unit hit area without changing semantic identity, labels or pinning', () => {
    const node = convert(converter, source()).nodes[0];
    expect(node.width).toBe(24);
    expect(node.height).toBe(24);
    expect(node.data.targetObjectId).toBe('semantic-junction');
    expect(node.data.insideLabel).toBeNull();
    expect(node.data.outsideLabels).toEqual({});
    expect(node.draggable).toBeUndefined(); // Native workbench read-only behavior remains authoritative.
    expect(convert(converter, { ...source(), pinned: true }).nodes[0].draggable).toBe(false);
    expect(converter.canHandle(semanticNode('equipment', {}))).toBe(false);
    expect(convert(converter, source(), null, [], false).nodes).toEqual([]);
  });

  it('keeps the centre of existing three-unit junctions and never shifts already-upgraded positions again', () => {
    const old = savedNode('junction', 100, 50, 3, 3);
    const upgraded = convert(converter, source(), null, [old]).nodes[0];
    expect(upgraded.position).toEqual({ x: 89.5, y: 39.5 });
    const saved = savedNode('junction', upgraded.position.x, upgraded.position.y, 24, 24);
    const reopened = convert(converter, source(), null, [saved]).nodes[0];
    expect(reopened.position).toEqual(upgraded.position);
    expect(reopened.data.movedByUser).toBe(true);
    expect(old.position).toEqual({ x: 100, y: 50 });
  });

  it('keeps connection anchors centred despite manual edge anchors or layout changes', () => {
    const node = convert(converter, source()).nodes[0];
    node.data.connectionHandles = [
      { XYPosition: { x: 0, y: 6 }, handleStyleTransform: 'translateX(8px)' } as ConnectionHandle,
    ];
    node.width = 3;
    layout.handle(
      { layoutNodes: () => {}, registerNodeLayoutHandlerContribution: () => {} },
      null,
      node,
      [node],
      [],
      []
    );
    expect(node.width).toBe(24);
    expect(node.height).toBe(24);
    expect(node.data.connectionHandles[0].XYPosition).toEqual({ x: 12, y: 12 });
    expect(node.data.connectionHandles[0].handleStyleTransform).toBeNull();
    expect(
      layout.calculateCustomNodeEdgeHandlePosition(node, Position.Left, {
        id: 'edge',
        x: 0,
        y: 0,
        width: 1,
        height: 1,
        position: Position.Left,
        type: 'target',
      })
    ).toEqual({ x: 12, y: 12 });
  });

  it('renders the visible symbol and leaves read-only junctions without a drag affordance', () => {
    const node = convert(converter, source()).nodes[0];
    renderNode(CircuitJunctionNode, node);
    const surface = screen.getByTestId('circuit-junction-semantic-junction');
    expect(surface.querySelector('.hed-circuit-junction-symbol')).toBeInTheDocument();
    expect(surface).toHaveStyle({ cursor: 'grab' });
    expect(surface).not.toHaveClass('nodrag');
  });

  it('blocks pointer dragging in read-only diagrams and on pinned nodes', () => {
    const node = convert(converter, source()).nodes[0];
    const ReadOnly = (props: NodeProps) => {
      const context = useContext(DiagramContext);
      return (
        <DiagramContext.Provider value={{ ...context, readOnly: true }}>
          <CircuitJunctionNode {...props} selected={true} />
        </DiagramContext.Provider>
      );
    };
    const rendered = renderNode(ReadOnly, node);
    expect(screen.getByTestId('circuit-junction-semantic-junction')).toHaveClass(
      'nodrag',
      'hed-circuit-junction-selected'
    );
    expect(screen.getByTestId('circuit-junction-semantic-junction')).toHaveStyle({ cursor: 'default' });
    rendered.unmount();
    node.data.pinned = true;
    renderNode(CircuitJunctionNode, node);
    expect(screen.getByTestId('circuit-junction-semantic-junction')).toHaveClass('nodrag');
  });
});
