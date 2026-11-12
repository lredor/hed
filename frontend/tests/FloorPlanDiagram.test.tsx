/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { fireEvent, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { ILayoutEngine, RawDiagram } from '@eclipse-sirius/sirius-components-diagrams';
import { describe, expect, it, vi } from 'vitest';
import { floorPlanNodeRegistry } from '../src/representations/diagrams/floorplan/FloorPlanDiagram';
import { convert, renderNode, savedNode, semanticNode } from './diagramFixtures';
import { platform } from './platform';

const converter = floorPlanNodeRegistry.nodeConverters[0],
  layout = floorPlanNodeRegistry.nodeLayoutHandlers[0];
const Component = floorPlanNodeRegistry.nodeTypeContributions[0].props.component;
const plan = (background = false) =>
  semanticNode(background ? 'background' : 'outlet', {
    __typename: 'FloorPlanNodeStyle',
    backgroundNode: background,
    imageURL: '/plan.svg',
    caption: background ? 'Ground floor' : 'Outlet 1',
    hasImage: background,
  });

describe('floor-plan conversion and placement', () => {
  it('selects only its custom style and ignores unknown descriptions', () => {
    expect(converter.canHandle(plan())).toBe(true);
    expect(converter.canHandle(semanticNode('other', {}))).toBe(false);
    expect(convert(converter, plan(), null, [], false).nodes).toEqual([]);
  });

  it('pins the background and keeps equipment at 24 by 24 despite saved dimensions', () => {
    const source = plan(true);
    const background = convert(converter, source, null, [savedNode(source.id, 500, 600)]).nodes[0];
    expect(background).toMatchObject({
      position: { x: 0, y: 0 },
      width: 1200,
      height: 848,
      draggable: false,
      selectable: false,
    });
    expect(background.data.pinned).toBe(true);
    const equipment = plan();
    equipment.defaultWidth = 200;
    equipment.defaultHeight = 200;
    const node = convert(converter, equipment, source, [savedNode(equipment.id, 100, 150, 200, 200)]).nodes[0];
    expect(node).toMatchObject({
      position: { x: 100, y: 150 },
      width: 24,
      height: 24,
      draggable: true,
      selectable: true,
    });
    expect(node.data.deletable).toBe(true);
  });

  it.each([false, true])('preserves relative icon centers on reopening, using fallback size: %s', (fallback) => {
    const source = plan(true);
    source.defaultWidth = 2400;
    source.defaultHeight = 1648;
    const oldBackground = savedNode(source.id, 0, 0, 1200, 848);
    if (fallback) oldBackground.size.width = 0;
    const equipment = plan();
    const node = convert(converter, equipment, source, [oldBackground, savedNode(equipment.id, 108, 236)]).nodes[0];
    expect(node.position).toEqual({ x: 228, y: 436 });
    expect(node.width).toBe(24);
  });

  it.each([
    [1200, 848],
    [0, 848],
    [1200, 48],
  ])('does not scale unchanged or unusable saved bounds %s by %s', (width, height) => {
    const source = plan(true),
      equipment = plan();
    const old = savedNode(source.id, 0, 0, width, height);
    const node = convert(converter, equipment, source, [old, savedNode(equipment.id, 100, 200)]).nodes[0];
    expect(node.position).toEqual({ x: 100, y: 200 });
  });

  it('scales existing live positions once and retains new drop coordinates', () => {
    const source = plan(true);
    source.defaultWidth = 2400;
    source.defaultHeight = 1648;
    const root = convert(converter, source).nodes[0];
    const existing = convert(converter, plan(), source, [savedNode('outlet', 108, 236)]).nodes[0];
    const dropped = { ...existing, id: 'new', position: { x: 0, y: 0 } };
    const previous = {
      nodes: [
        { ...root, width: 1200, height: 848 },
        { ...existing, position: { x: 108, y: 236 } },
      ],
    } as unknown as RawDiagram;
    const engine = { layoutNodes: vi.fn() };
    expect(layout.canHandle(root)).toBe(true);
    expect(layout.canHandle({ ...root, type: 'native' })).toBe(false);
    layout.handle(
      engine as unknown as ILayoutEngine,
      previous,
      root,
      [existing, dropped],
      [existing, dropped],
      [{ ...dropped, position: { x: 400, y: 500 } }]
    );
    expect(existing.position).toEqual({ x: 228, y: 436 });
    expect(dropped.position).toEqual({ x: 400, y: 500 });
    expect(root.data.minComputedWidth).toBe(2400);
  });
});

describe('floor-plan controls and imports', () => {
  it('toggles outside labels without resizing equipment and forwards drops to the background', async () => {
    const source = plan(true),
      equipment = plan();
    const backgroundNode = convert(converter, source).nodes[0],
      equipmentNode = convert(converter, equipment, source).nodes[0];
    const background = renderNode(Component, backgroundNode);
    const rendered = renderNode(Component, equipmentNode);
    expect(rendered.container.querySelector('.hed-floor-equipment-label')).not.toBeVisible();
    await userEvent.click(screen.getByRole('checkbox', { name: 'Show equipment labels' }));
    expect(rendered.container.querySelector('.hed-floor-equipment-label')).toBeVisible();
    expect(equipmentNode.width).toBe(24);
    platform.getNode.mockReturnValue(equipmentNode);
    fireEvent.drop(screen.getByTestId(`floor-plan-equipment-${equipment.targetObjectId}`));
    expect(platform.onDrop).toHaveBeenLastCalledWith(expect.anything(), source.id);
    fireEvent.drop(background.getByTestId('floor-plan-background'));
    expect(platform.onDrop).toHaveBeenLastCalledWith(expect.anything(), source.id);
  });

  it('uploads the image into the current project and passes its intrinsic proportions', async () => {
    const revoke = mockImage(600, 400);
    platform.sendFile.mockResolvedValue({
      data: { uploadImage: { __typename: 'UploadImageSuccessPayload', imageId: 'image-42' } },
    });
    renderNode(Component, convert(converter, plan(true)).nodes[0]);
    const file = new File(['image'], 'house.png', { type: 'image/png' });
    await userEvent.upload(screen.getByLabelText('Import floor plan'), file);
    await waitFor(() =>
      expect(platform.mutate).toHaveBeenCalledWith(
        expect.objectContaining({
          variables: {
            input: {
              id: expect.any(String),
              editingContextId: 'editing-1',
              actionId: 'hed-plan-background:semantic-background:image-42:1.5',
            },
          },
        })
      )
    );
    expect(platform.sendFile).toHaveBeenCalledWith(
      'http://localhost',
      expect.any(String),
      expect.objectContaining({ input: expect.objectContaining({ projectId: 'project-1', label: 'house.png' }) }),
      file
    );
    expect(revoke).toHaveBeenCalledWith('blob:plan');
    expect(platform.addErrorMessage).not.toHaveBeenCalled();
    await waitFor(() => expect(screen.getByRole('button', { name: 'Replace floor plan' })).toBeEnabled());
  });

  it.each(['format', 'size', 'ratio', 'decode'])(
    'rejects an invalid image (%s) without sending it to the server',
    async (reason) => {
      mockImage(reason === 'ratio' ? 10000 : 600, 400, reason === 'decode');
      renderNode(Component, convert(converter, plan(true)).nodes[0]);
      const file = new File([new Uint8Array(reason === 'size' ? 10 * 1024 * 1024 + 1 : 1)], 'bad.png', {
        type: reason === 'format' ? 'text/plain' : 'image/png',
      });
      await userEvent.setup({ applyAccept: false }).upload(screen.getByLabelText('Import floor plan'), file);
      await waitFor(() =>
        expect(platform.addErrorMessage).toHaveBeenCalledWith(expect.stringContaining('Choose a valid PNG'))
      );
      expect(platform.sendFile).not.toHaveBeenCalled();
      expect(platform.mutate).not.toHaveBeenCalled();
      expect(screen.getByRole('button', { name: 'Replace floor plan' })).toBeEnabled();
    }
  );

  it('keeps the current background when uploading fails', async () => {
    mockImage(600, 400);
    platform.sendFile.mockResolvedValue({ data: { uploadImage: { __typename: 'ErrorPayload' } } });
    renderNode(Component, convert(converter, plan(true)).nodes[0]);
    await userEvent.upload(
      screen.getByLabelText('Import floor plan'),
      new File(['image'], 'house.png', { type: 'image/png' })
    );
    await waitFor(() => expect(platform.addErrorMessage).toHaveBeenCalledWith('The floor plan could not be imported.'));
    expect(platform.mutate).not.toHaveBeenCalled();
    expect(screen.getByAltText('Ground floor')).toHaveAttribute('src', 'http://localhost/plan.svg');
  });

  it('removes only the image reference when clearing the background', async () => {
    renderNode(Component, convert(converter, plan(true)).nodes[0]);
    await userEvent.click(screen.getByRole('button', { name: 'Remove background' }));
    expect(platform.mutate).toHaveBeenCalledWith(
      expect.objectContaining({
        variables: {
          input: {
            id: expect.any(String),
            editingContextId: 'editing-1',
            actionId: 'hed-plan-background:semantic-background::1',
          },
        },
      })
    );
    expect(platform.sendFile).not.toHaveBeenCalled();
    expect(platform.addErrorMessage).not.toHaveBeenCalled();
  });

  it('reports a rejected background assignment and releases the busy state', async () => {
    mockImage(600, 400);
    platform.sendFile.mockResolvedValue({
      data: { uploadImage: { __typename: 'UploadImageSuccessPayload', imageId: 'image-42' } },
    });
    platform.mutate.mockResolvedValue({
      data: {
        invokeEditingContextAction: {
          __typename: 'ErrorPayload',
          messages: [{ body: 'Image belongs to another project' }],
        },
      },
    });
    renderNode(Component, convert(converter, plan(true)).nodes[0]);
    await userEvent.upload(
      screen.getByLabelText('Import floor plan'),
      new File(['image'], 'house.png', { type: 'image/png' })
    );
    await waitFor(() => expect(platform.addErrorMessage).toHaveBeenCalledWith('Image belongs to another project'));
    expect(screen.getByRole('button', { name: 'Replace floor plan' })).toBeEnabled();
  });

  it('reports a failed clear and retains its existing background', async () => {
    platform.mutate.mockRejectedValue(new Error('network'));
    renderNode(Component, convert(converter, plan(true)).nodes[0]);
    await userEvent.click(screen.getByRole('button', { name: 'Remove background' }));
    await waitFor(() => expect(platform.addErrorMessage).toHaveBeenCalledWith('The floor plan could not be imported.'));
    expect(screen.getByAltText('Ground floor')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Remove background' })).toBeEnabled();
  });
});

function mockImage(width: number, height: number, fail = false) {
  const revoke = vi.fn();
  vi.stubGlobal(
    'URL',
    class extends URL {
      static createObjectURL = () => 'blob:plan';
      static revokeObjectURL = revoke;
    }
  );
  vi.stubGlobal(
    'Image',
    class {
      naturalWidth = width;
      naturalHeight = height;
      onload: (() => void) | null = null;
      onerror: (() => void) | null = null;
      set src(_url: string) {
        queueMicrotask(() => (fail ? this.onerror?.() : this.onload?.()));
      }
    }
  );
  return revoke;
}
