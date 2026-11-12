/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { useContext, type DragEvent } from 'react';
import { gql, useApolloClient } from '@apollo/client';
import { type GQLMessage, useMultiToast } from '@eclipse-sirius/sirius-components-core';
import { DiagramContext, useDrop } from '@eclipse-sirius/sirius-components-diagrams';
import { useReactFlow } from '@xyflow/react';
import { useTranslation } from 'react-i18next';
import { ExplorerDragSourcesContext } from '../../modelexplorer/ExplorerDragSources';

const dropOnDiagram = gql`
  mutation dropOnDiagram($input: DropOnDiagramInput!) {
    dropOnDiagram(input: $input) {
      __typename
      ... on DropOnDiagramSuccessPayload {
        diagram {
          id
        }
        messages {
          body
          level
        }
      }
      ... on ErrorPayload {
        messages {
          body
          level
        }
      }
    }
  }
`;

/** Retains native drops and recovers document-local explorer sources when the browser loses their transfer payload. */
export function useFloorPlanDrop() {
  const native = useDrop();
  const recover = useContext(ExplorerDragSourcesContext);
  const { editingContextId, diagramId, readOnly } = useContext(DiagramContext);
  const { screenToFlowPosition } = useReactFlow();
  const client = useApolloClient();
  const { addMessages, addErrorMessage } = useMultiToast();
  const { t } = useTranslation('sirius-components-diagrams', { keyPrefix: 'useDrop' });
  return {
    onDragOver: native.onDragOver,
    onDrop: (event: DragEvent, diagramElementId?: string) => {
      const ids = recover(event.dataTransfer);
      if (!ids.length) {
        native.onDrop(event, diagramElementId);
        return;
      }
      event.preventDefault();
      event.stopPropagation();
      if (readOnly) return;
      const position = screenToFlowPosition({ x: event.clientX, y: event.clientY });
      // Use the same server operation and eligibility checks as the native hook; only restore the lost identifiers.
      void client
        .mutate<{ dropOnDiagram: { messages?: GQLMessage[] } }>({
          mutation: dropOnDiagram,
          variables: {
            input: {
              id: crypto.randomUUID(),
              editingContextId,
              representationId: diagramId,
              objectIds: ids,
              startingPositionX: position.x,
              startingPositionY: position.y,
              diagramTargetElementId: diagramElementId ?? diagramId,
            },
          },
        })
        .then(({ data }) => {
          if (data?.dropOnDiagram.messages) addMessages(data.dropOnDiagram.messages);
        })
        .catch(() => addErrorMessage(t('errors.unexpected')));
    },
  };
}
