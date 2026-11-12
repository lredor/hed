/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { renderHook, waitFor } from '@testing-library/react';
import { type ContextType, type PropsWithChildren } from 'react';
import { DiagramContext, type NodeData } from '@eclipse-sirius/sirius-components-diagrams';
import type { Node } from '@xyflow/react';
import { describe, expect, it, vi } from 'vitest';
import { usePanelRailDropProps } from '../src/representations/diagrams/panel/PanelRailDrop';
import { platform } from './platform';
import { panelNodeRegistry } from '../src/representations/diagrams/panel/PanelDiagram';
import { convert, semanticNode } from './diagramFixtures';

const node = (id: string, role: string, parentId?: string): Node<NodeData> => ({
  id,
  type: 'hedPanelNode',
  parentId,
  position: { x: 5, y: 10 },
  data: convert(panelNodeRegistry.nodeConverters[0], semanticNode(id, { __typename: 'PanelNodeStyle', role, order: 0 }))
    .nodes[0].data,
});

function setup(readOnly = false) {
  const device = node('device', 'device', 'rail');
  const rail = node('rail', 'rail', 'board');
  rail.position = { x: 24, y: 222 };
  rail.data.isDropNodeTarget = true;
  rail.data.isDropNodeCandidate = true;
  const board = node('board', 'board');
  board.position = { x: 48, y: 198 };
  platform.getNodes.mockReturnValue([board, rail, device]);
  platform.getNode.mockImplementation((id) => [board, rail, device].find((value) => value.id === id));
  const nativeStop = vi.fn();
  const { result } = renderHook(() => usePanelRailDropProps({ onNodeDragStop: nativeStop }), {
    wrapper: ({ children }: PropsWithChildren) => (
      <DiagramContext.Provider
        value={
          { editingContextId: 'editing-1', diagramId: 'diagram-1', readOnly } as ContextType<typeof DiagramContext>
        }>
        {children}
      </DiagramContext.Provider>
    ),
  });
  const stop = (selected = [device]) =>
    result.current.onNodeDragStop!(
      new MouseEvent('mouseup') as unknown as Parameters<NonNullable<typeof result.current.onNodeDragStop>>[0],
      device,
      selected
    );
  return { device, rail, board, nativeStop, stop };
}

describe('append a modular device on its current rail', () => {
  it('retains native cleanup and sends absolute coordinates before cleanup alters them', async () => {
    const { device, nativeStop, stop } = setup();
    nativeStop.mockImplementation(() => {
      device.position.x = 999;
    });
    platform.mutate.mockResolvedValue({ data: { dropNodes: { messages: [{ body: 'Moved', level: 'INFO' }] } } });
    stop();
    expect(nativeStop).toHaveBeenCalledOnce();
    expect(platform.mutate).toHaveBeenCalledWith(
      expect.objectContaining({
        variables: {
          input: {
            id: expect.any(String),
            editingContextId: 'editing-1',
            representationId: 'diagram-1',
            droppedElementIds: ['device'],
            targetElementId: 'rail',
            dropPositions: [{ x: 77, y: 430 }],
          },
        },
      })
    );
    expect(nativeStop.mock.invocationCallOrder[0]).toBeLessThan(platform.mutate.mock.invocationCallOrder[0]);
    await waitFor(() => expect(platform.addMessages).toHaveBeenCalledWith([{ body: 'Moved', level: 'INFO' }]));
  });

  it.each(['readOnly', 'multiple', 'otherParent', 'wrongType', 'wrongRole', 'notCandidate', 'noTarget'])(
    'delegates %s drops to Sirius without an extra mutation',
    (condition) => {
      const scenario = setup(condition === 'readOnly');
      if (condition === 'otherParent') scenario.device.parentId = 'another-rail';
      if (condition === 'wrongType') scenario.device.type = 'nativeNode';
      if (condition === 'wrongRole') scenario.device.data.panel = { role: 'board' };
      if (condition === 'notCandidate') scenario.rail.data.isDropNodeCandidate = false;
      if (condition === 'noTarget') scenario.rail.data.isDropNodeTarget = false;
      scenario.stop(condition === 'multiple' ? [scenario.device, node('other', 'device', 'rail')] : undefined);
      expect(scenario.nativeStop).toHaveBeenCalledOnce();
      expect(platform.mutate).not.toHaveBeenCalled();
    }
  );

  it('reports a failed server request without skipping native cleanup', async () => {
    const { nativeStop, stop } = setup();
    platform.mutate.mockRejectedValue(new Error('network'));
    stop();
    expect(nativeStop).toHaveBeenCalledOnce();
    await waitFor(() => expect(platform.addErrorMessage).toHaveBeenCalledWith('useDropNodeMutation.errors.unexpected'));
  });

  it('accepts a successful response without notification messages', async () => {
    const { stop } = setup();
    platform.mutate.mockResolvedValue({ data: undefined });
    stop();
    await waitFor(() => expect(platform.mutate).toHaveBeenCalledOnce());
    expect(platform.addMessages).not.toHaveBeenCalled();
    expect(platform.addErrorMessage).not.toHaveBeenCalled();
  });
});
