/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { useContext } from 'react';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { DiagramContext } from '@eclipse-sirius/sirius-components-diagrams';
import { ExplorerDragSourcesProvider } from '../src/representations/modelexplorer/ExplorerDragSources';
import { useFloorPlanDrop } from '../src/representations/diagrams/floorplan/FloorPlanDrop';
import { platform } from './platform';

const mime = 'sirius-component/drag/sources';
const transfer = (text = '', types = text ? [mime] : []): DataTransfer =>
  ({
    types,
    getData: (type: string) => (type === mime ? text : ''),
  } as unknown as DataTransfer);

function Target() {
  const { onDrop } = useFloorPlanDrop();
  return <div data-testid="target" onDrop={(event) => onDrop(event, 'background')} />;
}

function Harness({ readOnly = false }: { readOnly?: boolean }) {
  const context = useContext(DiagramContext);
  return (
    <DiagramContext.Provider value={{ ...context, readOnly }}>
      <ExplorerDragSourcesProvider>
        <div draggable data-testid="source">
          <span data-treeitemid="device-1">Switch</span>
        </div>
        <div draggable data-testid="foreign">
          External
        </div>
        <Target />
      </ExplorerDragSourcesProvider>
    </DiagramContext.Provider>
  );
}

const start = (ids = ['device-1']) =>
  fireEvent.dragStart(screen.getByTestId('source'), { dataTransfer: transfer(JSON.stringify(ids)) });
const drop = (data = transfer()) => {
  const event = new MouseEvent('drop', { bubbles: true, cancelable: true, clientX: 140, clientY: 260 });
  Object.defineProperty(event, 'dataTransfer', { value: data });
  fireEvent(screen.getByTestId('target'), event);
};

describe('floor-plan drops with missing browser transfer data', () => {
  it('recovers the complete active selection once, retaining coordinates and the native server operation', async () => {
    render(<Harness />);
    platform.mutate.mockResolvedValue({ data: { dropOnDiagram: { messages: [] } } });
    start(['device-1', 'device-2']);
    drop();
    await waitFor(() => expect(platform.mutate).toHaveBeenCalledTimes(1));
    expect(platform.mutate.mock.calls[0][0].variables.input).toEqual({
      id: expect.any(String),
      editingContextId: 'editing-1',
      representationId: 'diagram-1',
      objectIds: ['device-1', 'device-2'],
      startingPositionX: 140,
      startingPositionY: 260,
      diagramTargetElementId: 'background',
    });
    expect(platform.onDrop).not.toHaveBeenCalled();
    // A later empty drop must not reuse the selection from a completed gesture.
    await new Promise((resolve) => setTimeout(resolve, 0));
    drop();
    expect(platform.mutate).toHaveBeenCalledTimes(1);
    expect(platform.onDrop).toHaveBeenCalledTimes(1);
  });

  it('keeps valid native payloads and unrelated external payloads on the native path', () => {
    render(<Harness />);
    start();
    drop(transfer('["native-source"]'));
    expect(platform.onDrop).toHaveBeenCalledTimes(1);
    start();
    drop(transfer('', ['Files']));
    expect(platform.onDrop).toHaveBeenCalledTimes(2);
    expect(platform.mutate).not.toHaveBeenCalled();
  });

  it.each(['dragend', 'escape', 'pagehide', 'foreign', 'malformed', 'other-project'])(
    'does not recover stale or unrelated sources after %s',
    (reason) => {
      render(<Harness />);
      start();
      if (reason === 'dragend') fireEvent.dragEnd(screen.getByTestId('source'));
      if (reason === 'escape') fireEvent.keyDown(document, { key: 'Escape' });
      if (reason === 'pagehide') fireEvent(window, new Event('pagehide'));
      if (reason === 'foreign')
        fireEvent.dragStart(screen.getByTestId('foreign'), { dataTransfer: transfer('["device-1"]') });
      if (reason === 'malformed')
        fireEvent.dragStart(screen.getByTestId('source'), { dataTransfer: transfer('{broken') });
      if (reason === 'other-project') window.history.replaceState(null, '', '/projects/another-project/edit');
      drop();
      expect(platform.mutate).not.toHaveBeenCalled();
      expect(platform.onDrop).toHaveBeenCalledTimes(1);
    }
  );

  it('does not mutate a read-only diagram even with a recoverable selection', () => {
    render(<Harness readOnly />);
    start();
    drop();
    expect(platform.mutate).not.toHaveBeenCalled();
    expect(platform.onDrop).not.toHaveBeenCalled();
  });

  it('reports server validation feedback and transport failures', async () => {
    render(<Harness />);
    const messages = [{ body: 'Wrong storey', level: 'ERROR' }];
    platform.mutate.mockResolvedValueOnce({ data: { dropOnDiagram: { messages } } });
    start();
    drop();
    await waitFor(() => expect(platform.addMessages).toHaveBeenCalledWith(messages));
    platform.mutate.mockRejectedValueOnce(new Error('Offline'));
    start();
    drop();
    await waitFor(() => expect(platform.addErrorMessage).toHaveBeenCalledWith('useDrop.errors.unexpected'));
  });
});
