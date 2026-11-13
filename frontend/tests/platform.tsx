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
import type { Node } from '@xyflow/react';
import type { NodeData } from '@eclipse-sirius/sirius-components-diagrams';

// Mock platform boundaries, retaining the real React, Apollo document transforms and GraphQL parser.
// Playwright remains responsible for integration with the actual Sirius workbench and server.
const platform = vi.hoisted(() => ({
  mutate: vi.fn(),
  sendFile: vi.fn(),
  addMessages: vi.fn(),
  addErrorMessage: vi.fn(),
  onDrop: vi.fn(),
  onDragOver: vi.fn(),
  getNodes: vi.fn<() => Node<NodeData>[]>(() => []),
  getNode: vi.fn<(id: string) => Node<NodeData> | undefined>(),
  getEdges: vi.fn(() => []),
  synchronizeLayoutData: vi.fn(),
  screenToFlowPosition: vi.fn((position: { x: number; y: number }) => position),
  renderDiagram: vi.fn(),
  setSelection: vi.fn(),
  revealSelection: vi.fn(),
}));
export { platform };

vi.mock('@apollo/client', async (original) => ({
  ...(await original<typeof import('@apollo/client')>()),
  useApolloClient: () => ({ mutate: platform.mutate }),
}));

vi.mock('@xyflow/react', () => ({
  Position: { Top: 'top', Bottom: 'bottom', Left: 'left', Right: 'right' },
  useReactFlow: () => ({
    getNodes: platform.getNodes,
    getNode: platform.getNode,
    getEdges: platform.getEdges,
    screenToFlowPosition: platform.screenToFlowPosition,
  }),
}));

vi.mock('@eclipse-sirius/sirius-components-core', async () => {
  const { createContext } = await import('react');
  return {
    DRAG_SOURCES_TYPE: 'sirius-component/drag/sources',
    ServerContext: createContext({ httpOrigin: 'http://localhost' }),
    ConfirmationDialogContext: createContext({ showConfirmation: vi.fn() }),
    representationFactoryExtensionPoint: { identifier: 'representationFactory' },
    useMultiToast: () => ({ addMessages: platform.addMessages, addErrorMessage: platform.addErrorMessage }),
    useSelection: () => ({ selection: { entries: [] }, setSelection: platform.setSelection }),
    useSelectionTargets: () => ({ selectionTargets: [{ id: 'explorer', applySelection: platform.revealSelection }] }),
  };
});

vi.mock('@eclipse-sirius/sirius-components-diagrams', async () => {
  const { createContext, forwardRef } = await import('react');
  return {
    DiagramContext: createContext({ editingContextId: 'editing-1', diagramId: 'diagram-1', readOnly: false }),
    DiagramRepresentation: forwardRef((props, ref) => platform.renderDiagram(props, ref)),
    GQLViewModifier: { Normal: 'Normal', Faded: 'Faded', Hidden: 'Hidden' },
    NodeTypeContribution: () => null,
    convertOutsideLabels: vi.fn((labels) => labels ?? []),
    convertInsideLabel: vi.fn((label) => label ?? null),
    convertHandles: vi.fn(() => []),
    useRefreshConnectionHandles: vi.fn(),
    ConnectionHandles: () => null,
    Resizer: () => null,
    useSynchronizeLayoutData: () => ({ synchronizeLayoutData: platform.synchronizeLayoutData }),
    useDrop: () => ({ onDrop: platform.onDrop, onDragOver: platform.onDragOver }),
    useDropNodeStyle: () => ({ style: {} }),
  };
});

vi.mock('@eclipse-sirius/sirius-web-application', () => ({
  sendFile: platform.sendFile,
  apolloClientOptionsConfigurersExtensionPoint: { identifier: 'apolloClientOptionsConfigurers' },
  DefaultExtensionRegistryMergeStrategy: class {
    mergeDataExtensions(_identifier: string, existing: unknown) {
      return existing;
    }
  },
}));
