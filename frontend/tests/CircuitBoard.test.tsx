/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { screen } from '@testing-library/react';
import type { ILayoutEngine } from '@eclipse-sirius/sirius-components-diagrams';
import { describe, expect, it, vi } from 'vitest';
import { circuitBoardNodeRegistry } from '../src/representations/diagrams/circuits/CircuitBoard';
import { convert, renderNode, savedNode, semanticNode } from './diagramFixtures';

const converter = circuitBoardNodeRegistry.nodeConverters[0];
const layout = circuitBoardNodeRegistry.nodeLayoutHandlers[0];
const Component = circuitBoardNodeRegistry.nodeTypeContributions[0].props.component;
const source = (id: string, role: 'rail' | 'device', order = 0, width = 32) =>
  semanticNode(
    id,
    { __typename: 'CircuitBoardNodeStyle', role, order, caption: id, iconURL: '/hed/mdi/electric-switch.svg' },
    { defaultWidth: width, defaultHeight: 112 }
  );
const device = (id: string, order: number, width = 32) =>
  convert(converter, source(id, 'device', order, width)).nodes[0];
const engine = () => ({ layoutNodes: vi.fn() } as unknown as ILayoutEngine);

describe('logical board constraints', () => {
  it('ignores other diagram styles and rejects unknown descriptions', () => {
    expect(converter.canHandle(source('rail', 'rail'))).toBe(true);
    expect(converter.canHandle(semanticNode('native', {}))).toBe(false);
    expect(convert(converter, source('rail', 'rail'), null, [], false).nodes).toEqual([]);
    expect(layout.canHandle({ type: 'freeFormNode' } as Parameters<typeof layout.canHandle>[0])).toBe(false);
  });

  it('aligns tall devices horizontally in semantic order, ignoring saved free positions', () => {
    const rail = convert(converter, source('rail', 'rail')).nodes[0];
    const first = device('rccb', 0, 64),
      second = device('Q1', 1),
      third = device('Q2', 2);
    const children = [third, first, second];
    children.forEach((child) => {
      child.position = { x: 999, y: 999 };
    });
    const delegate = engine();
    layout.handle(delegate, null, rail, [rail, ...children], children, []);
    expect(first.position).toEqual({ x: 12, y: 36 });
    expect(second.position).toEqual({ x: 80, y: 36 });
    expect(third.position).toEqual({ x: 116, y: 36 });
    expect(children.every((child) => !child.draggable)).toBe(true);
    expect(rail.width).toBe(160);
    expect(rail.height).toBe(160);
    expect(delegate.layoutNodes).toHaveBeenCalledWith(null, [rail, ...children], children, []);
    // Semantic reordering changes the row immediately, without changing its container.
    (third.data.circuitBoard as { order: number }).order = -1;
    layout.handle(delegate, null, rail, children, children, []);
    expect(third.position).toEqual({ x: 12, y: 36 });
    expect(first.position).toEqual({ x: 48, y: 36 });
  });

  it('restores catalog dimensions rather than obsolete saved device sizes and keeps empty containers usable', () => {
    const node = device('Q1', 0);
    node.width = 900;
    node.height = 900;
    layout.handle(engine(), null, node, [], [], []);
    expect(node).toMatchObject({ width: 32, height: 112 });
    const rail = convert(converter, source('empty', 'rail')).nodes[0];
    layout.handle(engine(), null, rail, [], [], []);
    expect(rail).toMatchObject({ width: 160, height: 160 });
  });

  it('converts content dimensions and semantic positions even on layout-only refreshes or reopening', () => {
    const rail = source('rail', 'rail');
    rail.childNodes = [
      source('Q2', 'device', 2),
      source('ID1', 'device', 0, 64),
      source('Q1', 'device', 1),
      source('Q3', 'device', 3),
      source('Q5', 'device', 4),
    ];
    const board = semanticNode('board', { __typename: 'RectangularNodeStyle' });
    board.childNodes = [rail];
    const converted = convert(converter, rail, board, [savedNode('rail', 1, 40, 10, 10)]).nodes[0];
    expect(converted).toMatchObject({
      width: 232,
      height: 160,
      measured: { width: 232, height: 160 },
      position: { x: 1, y: 40 },
    });
    expect(converted.style).toMatchObject({ width: 232, height: 160 });
    expect(converted.draggable).toBe(false);
    expect(convert(converter, rail.childNodes[0], rail, [savedNode('Q2', 999, 999)]).nodes[0].position).toEqual({
      x: 116,
      y: 36,
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
});
