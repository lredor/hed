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
import type { ILayoutEngine, RawDiagram } from '@eclipse-sirius/sirius-components-diagrams';
import { describe, expect, it, vi } from 'vitest';
import { panelNodeRegistry } from '../src/representations/diagrams/panel/PanelDiagram';
import { convert, renderNode, savedNode, semanticNode } from './diagramFixtures';
import { platform } from './platform';

const converter = panelNodeRegistry.nodeConverters[0];
const layout = panelNodeRegistry.nodeLayoutHandlers[0];
const Component = panelNodeRegistry.nodeTypeContributions[0].props.component;
const panel = (id: string, role: 'board' | 'rail' | 'device', order = 0, width = 38) =>
  semanticNode(
    id,
    {
      __typename: 'PanelNodeStyle',
      role,
      order,
      imageURL: '/cabinet.svg',
      caption: id,
      capacityModules: 13,
      usedModules: 4,
      labelText: null,
    },
    { defaultWidth: width, defaultHeight: 218 }
  );

describe('physical board conversion and constrained layout', () => {
  it('handles only panel styles and skips nodes without a matching description', () => {
    expect(converter.canHandle(panel('board', 'board'))).toBe(true);
    expect(converter.canHandle(semanticNode('native', {}))).toBe(false);
    const { nodes, engine } = convert(converter, panel('unknown', 'device'), null, [], false);
    expect(nodes).toEqual([]);
    expect(engine.convertNodes).not.toHaveBeenCalled();
  });

  it('positions devices by semantic order and catalog width, ignoring old free positions', () => {
    const first = panel('rccb', 'device', 0, 76),
      second = panel('mcb-1', 'device', 1),
      third = panel('mcb-2', 'device', 2);
    const rail = panel('rail', 'rail', 0, 1104);
    rail.childNodes = [third, first, second];
    const { nodes, engine } = convert(converter, third, rail, [savedNode(third.id, 999, 999)]);
    expect(nodes[0]).toMatchObject({
      id: third.id,
      parentId: rail.id,
      position: { x: 138, y: 133 },
      width: 38,
      draggable: true,
    });
    expect(nodes[0].data.targetObjectId).toBe(third.targetObjectId);
    expect(engine.convertNodes).toHaveBeenCalledOnce();
  });

  it('positions rails on the SVG coordinates rather than their saved layout', () => {
    const board = panel('board', 'board', 0, 1200),
      rail = panel('third-rail', 'rail', 2, 1104);
    const { nodes } = convert(converter, rail, board, [savedNode(rail.id, 1, 1)]);
    expect(nodes[0]).toMatchObject({ position: { x: 48, y: 1038 }, draggable: false });
  });

  it('reapplies contiguous catalog widths after the native child layout', () => {
    const root = convert(converter, panel('rail', 'rail', 0, 1104)).nodes[0];
    const children = [
      convert(converter, panel('mcb', 'device', 1)).nodes[0],
      convert(converter, panel('rccb', 'device', 0, 76)).nodes[0],
    ];
    const engine = { layoutNodes: vi.fn() };
    expect(layout.canHandle(root)).toBe(true);
    expect(layout.canHandle({ ...root, type: 'native' })).toBe(false);
    layout.handle(engine as unknown as ILayoutEngine, null, root, children, children, []);
    expect(engine.layoutNodes).toHaveBeenCalledOnce();
    expect(children[1].position).toEqual({ x: 24, y: 133 });
    expect(children[0].position).toEqual({ x: 100, y: 133 });
    expect(root.data.minComputedWidth).toBe(1104);
  });

  it('lays out rails in order even when graph children arrive in reverse order', () => {
    const root = convert(converter, panel('board', 'board', 0, 1200)).nodes[0];
    const children = [
      convert(converter, panel('second', 'rail', 1)).nodes[0],
      convert(converter, panel('first', 'rail', 0)).nodes[0],
    ];
    layout.handle(
      { layoutNodes: vi.fn() } as unknown as ILayoutEngine,
      { nodes: [] } as unknown as RawDiagram,
      root,
      children,
      children,
      []
    );
    expect(children[1].position).toEqual({ x: 48, y: 198 });
    expect(children[0].position).toEqual({ x: 48, y: 618 });
    expect(children.every((child) => !child.draggable)).toBe(true);
  });
});

describe('panel node rendering', () => {
  it('shows an empty cabinet and delegates catalog drops to its native tool', () => {
    const node = convert(converter, panel('board', 'board')).nodes[0];
    renderNode(Component, node);
    expect(screen.getByText('Create DIN rails in Model Explorer to populate the board.')).toBeVisible();
    const element = screen.getByTestId(`panel-board-${node.data.targetObjectId}`);
    fireEvent.dragOver(element);
    fireEvent.drop(element);
    expect(platform.onDragOver).toHaveBeenCalledOnce();
    expect(platform.onDrop).toHaveBeenCalledWith(expect.anything(), node.id);
  });

  it.each([4, 14])('shows %s occupied modules and flags capacity overflow', (usedModules) => {
    const source = panel('rail', 'rail');
    Object.assign(source.style, { usedModules });
    renderNode(Component, convert(converter, source).nodes[0]);
    const capacity = screen.getByText(`${usedModules} / 13 modules`);
    expect(capacity.classList.contains('hed-over-capacity')).toBe(usedModules > 13);
  });

  it('keeps unnamed device captions empty without falling back to the type name', () => {
    const source = panel('device', 'device');
    Object.assign(source.style, { caption: '', labelText: null });
    const view = renderNode(Component, convert(converter, source).nodes[0]);
    expect(screen.getByAltText(source.targetObjectLabel)).toHaveAttribute('draggable', 'false');
    expect(view.container.querySelector('.hed-device-reference')).toBeNull();
    expect(view.container.querySelector('.hed-device-label')).toBeNull();
  });

  it('displays the device name and printed label independently', () => {
    const source = panel('Q1', 'device');
    Object.assign(source.style, { labelText: 'Kitchen outlets' });
    renderNode(Component, convert(converter, source).nodes[0]);
    expect(screen.getByText('Q1')).toBeVisible();
    expect(screen.getByText('Kitchen outlets')).toBeVisible();
  });
});
