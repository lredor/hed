/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { fireEvent, screen } from '@testing-library/react';
import type { ILayoutEngine, ConnectionHandle, EdgeData } from '@eclipse-sirius/sirius-components-diagrams';
import { Position, type Edge } from '@xyflow/react';
import { describe, expect, it, vi } from 'vitest';
import {
  circuitBoardNodeRegistry,
  routeCircuitProtectionsAbove,
} from '../src/representations/diagrams/circuits/CircuitBoard';
import { convert, renderNode, savedNode, semanticNode } from './diagramFixtures';
import { platform } from './platform';

const converter = circuitBoardNodeRegistry.nodeConverters[0];
const layout = circuitBoardNodeRegistry.nodeLayoutHandlers[0];
const Component = circuitBoardNodeRegistry.nodeTypeContributions[0].props.component;
const source = (id: string, role: 'rail' | 'device', order = 0, width = 64) =>
  semanticNode(
    id,
    { __typename: 'CircuitBoardNodeStyle', role, order, caption: id, iconURL: '/hed/mdi/electric-switch.svg' },
    { defaultWidth: width, defaultHeight: 112 }
  );
const device = (id: string, order: number, width = 64) =>
  convert(converter, source(id, 'device', order, width)).nodes[0];
const engine = () => ({ layoutNodes: vi.fn() } as unknown as ILayoutEngine);

describe('logical board constraints', () => {
  it('saves an explicit rail routing action through the native diagram layout service', () => {
    const rail = convert(converter, source('rail', 'rail')).nodes[0];
    rail.data.hasProtectionLinks = true;
    platform.getNodes.mockReturnValue([rail]);
    renderNode(Component, rail);
    fireEvent.click(screen.getByRole('button', { name: 'Route protection links above devices' }));
    expect(platform.synchronizeLayoutData).toHaveBeenCalledWith(
      expect.any(String),
      'layout',
      { nodes: [rail], edges: [] },
      'DEACTIVATE'
    );
  });
  it('ignores other diagram styles and rejects unknown descriptions', () => {
    expect(converter.canHandle(source('rail', 'rail'))).toBe(true);
    expect(converter.canHandle(semanticNode('native', {}))).toBe(false);
    expect(convert(converter, source('rail', 'rail'), null, [], false).nodes).toEqual([]);
    expect(layout.canHandle({ type: 'freeFormNode' } as Parameters<typeof layout.canHandle>[0])).toBe(false);
  });

  it('aligns tall devices horizontally in semantic order, ignoring saved free positions', () => {
    const rail = convert(converter, source('rail', 'rail')).nodes[0];
    const first = device('rccb', 0, 128),
      second = device('Q1', 1),
      third = device('Q2', 2);
    const children = [third, first, second];
    children.forEach((child) => {
      child.position = { x: 999, y: 999 };
    });
    const delegate = engine();
    layout.handle(delegate, null, rail, [rail, ...children], children, []);
    expect(first.position).toEqual({ x: 12, y: 80 });
    expect(second.position).toEqual({ x: 144, y: 80 });
    expect(third.position).toEqual({ x: 212, y: 80 });
    expect(children.every((child) => !child.draggable)).toBe(true);
    expect(rail.width).toBe(288);
    expect(rail.height).toBe(260);
    expect(delegate.layoutNodes).toHaveBeenCalledWith(null, [rail, ...children], children, []);
    // Semantic reordering changes the row immediately, without changing its container.
    (third.data.circuitBoard as { order: number }).order = -1;
    layout.handle(delegate, null, rail, children, children, []);
    expect(third.position).toEqual({ x: 12, y: 80 });
    expect(first.position).toEqual({ x: 80, y: 80 });
  });

  it('restores catalog dimensions rather than obsolete saved device sizes and keeps empty containers usable', () => {
    const node = device('Q1', 0);
    node.width = 900;
    node.height = 900;
    layout.handle(engine(), null, node, [], [], []);
    expect(node).toMatchObject({ width: 64, height: 112 });
    const rail = convert(converter, source('empty', 'rail')).nodes[0];
    layout.handle(engine(), null, rail, [], [], []);
    expect(rail).toMatchObject({ width: 160, height: 260 });
  });

  it('converts content dimensions and semantic positions even on layout-only refreshes or reopening', () => {
    const rail = source('rail', 'rail');
    rail.childNodes = [
      source('Q2', 'device', 2),
      source('ID1', 'device', 0, 128),
      source('Q1', 'device', 1),
      source('Q3', 'device', 3),
      source('Q5', 'device', 4),
    ];
    const board = semanticNode('board', { __typename: 'RectangularNodeStyle' });
    board.childNodes = [rail];
    const converted = convert(converter, rail, board, [savedNode('rail', 1, 40, 10, 10)]).nodes[0];
    expect(converted).toMatchObject({
      width: 424,
      height: 260,
      measured: { width: 424, height: 260 },
      position: { x: 1, y: 40 },
    });
    expect(converted.style).toMatchObject({ width: 424, height: 260 });
    expect(converted.draggable).toBe(false);
    expect(convert(converter, rail.childNodes[0], rail, [savedNode('Q2', 999, 999)]).nodes[0].position).toEqual({
      x: 212,
      y: 80,
    });
  });

  it('renders the shared icon, stored caption and semantic tooltip without introducing a type fallback', () => {
    const node = device('Q1', 0);
    renderNode(Component, node);
    expect(screen.getByText('Q1')).toBeVisible();
    const root = screen.getByTestId('circuit-device-semantic-Q1');
    expect(root.querySelector('img')).toHaveAttribute('src', 'http://localhost/api/images/hed/mdi/electric-switch.svg');
    expect(root).toHaveAttribute('title', 'Name Q1');
    const unnamed = source('unnamed', 'device');
    Object.assign(unnamed.style, { caption: '' });
    renderNode(Component, convert(converter, unnamed).nodes[0]);
    expect(screen.getByTestId('circuit-device-semantic-unnamed').querySelector('span')).toBeNull();
  });

  it('stretches short rails to the board width and preserves adjustable routing space across refreshes', () => {
    const saved = { ...savedNode('rail', 1, 40, 300, 420), resizedByUser: true };
    const rail = convert(converter, source('rail', 'rail'), null, [saved]).nodes[0];
    expect(rail.height).toBe(420);
    layout.handle(engine(), null, rail, [rail], [], [], { width: 424, height: null });
    expect(rail.width).toBe(424);
    expect(rail.height).toBe(420);
    expect(rail.data.minComputedHeight).toBe(260);
    // A live resize is kept even before the native resize-finished event sets resizedByUser.
    rail.data.resizedByUser = false;
    rail.resizing = true;
    rail.height = 500;
    layout.handle(engine(), null, rail, [rail], [], [], { width: 424, height: null });
    expect(rail.height).toBe(500);
    rail.height = 10;
    layout.handle(engine(), null, rail, [rail], [], []);
    expect(rail.height).toBe(260);
    expect(rail.width).toBe(424);
  });

  it('routes a rail explicitly without altering other links or semantic order', () => {
    const parent = convert(converter, source('rail', 'rail')).nodes[0];
    const first = device('ID1', 0, 128),
      second = device('Q1', 1);
    first.parentId = parent.id;
    second.parentId = parent.id;
    const handle = (nodeId: string, type: 'source' | 'target'): ConnectionHandle => ({
      id: type,
      edgeId: 'protection',
      nodeId,
      index: 0,
      type,
      position: Position.Right,
      XYPosition: null,
      isHidden: true,
      isVirtualHandle: false,
      handleStyleTransform: null,
    });
    first.data.connectionHandles = [handle(first.id, 'source')];
    second.data.connectionHandles = [handle(second.id, 'target')];
    const protection = {
      id: 'protection',
      source: first.id,
      target: second.id,
      data: { edgeAppearanceData: { gqlStyle: { lineStyle: 'Solid' } }, bendingPoints: [{ x: 1, y: 2 }] },
    } as Edge<EdgeData>;
    const other = { ...protection, id: 'control', target: 'room-equipment' };
    const result = routeCircuitProtectionsAbove([parent, first, second], [protection, other], parent.id);
    expect(result.nodes[0]).toBe(parent);
    expect(result.nodes[1].data.connectionHandles[0]).toMatchObject({
      position: Position.Top,
      XYPosition: { x: 64, y: 0 },
    });
    expect(result.nodes[2].data.connectionHandles[0]).toMatchObject({
      position: Position.Top,
      XYPosition: { x: 32, y: 0 },
    });
    expect(first.data.connectionHandles[0].position).toBe(Position.Right);
    expect(result.edges[0].data?.bendingPoints).toEqual([]);
    expect(result.edges[1]).toBe(other);
    expect(routeCircuitProtectionsAbove([first], [protection], 'other-rail').nodes[0]).toBe(first);
  });
});
