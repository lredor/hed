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
import { gql, useApolloClient } from '@apollo/client';
import { GQLMessage, useMultiToast } from '@eclipse-sirius/sirius-components-core';
import { DiagramContext, ReactFlowPropsCustomizer } from '@eclipse-sirius/sirius-components-diagrams';
import { useReactFlow } from '@xyflow/react';
import { useTranslation } from 'react-i18next';
import type { PanelData } from './PanelDiagram';

const dropNodes = gql`
  mutation dropNodes($input: DropNodesInput!) {
    dropNodes(input: $input) {
      __typename
      ... on SuccessPayload {
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

/** Sirius skips drops on the current parent; a DIN rail uses that gesture to append. */
export const usePanelRailDropProps: ReactFlowPropsCustomizer = (props) => {
  const { editingContextId, diagramId, readOnly } = useContext(DiagramContext);
  const { getNodes, getNode } = useReactFlow();
  const client = useApolloClient();
  const { addMessages, addErrorMessage } = useMultiToast();
  const { t } = useTranslation('sirius-components-diagrams');
  return {
    ...props,
    onNodeDragStop: (event, node, nodes) => {
      const target = getNodes().find((candidate) => candidate.data.isDropNodeTarget);
      const append =
        !readOnly &&
        nodes.length === 1 &&
        node.type === 'hedPanelNode' &&
        (node.data as PanelData).panel.role === 'device' &&
        target?.id === node.parentId &&
        target?.data.isDropNodeCandidate &&
        (target.data as PanelData).panel.role === 'rail';
      const position = { ...node.position };
      let parent = node.parentId ? getNode(node.parentId) : undefined;
      while (parent) {
        position.x += parent.position.x;
        position.y += parent.position.y;
        parent = parent.parentId ? getNode(parent.parentId) : undefined;
      }
      // Retain native drag cleanup, other drop tools and constrained layout handling.
      props.onNodeDragStop?.(event, node, nodes);
      if (append) {
        void client
          .mutate<{ dropNodes: { messages: GQLMessage[] } }>({
            mutation: dropNodes,
            variables: {
              input: {
                id: crypto.randomUUID(),
                editingContextId,
                representationId: diagramId,
                droppedElementIds: [node.id],
                targetElementId: target!.id,
                dropPositions: [position],
              },
            },
          })
          .then(({ data }) => {
            if (data?.dropNodes.messages) {
              addMessages(data.dropNodes.messages);
            }
          })
          .catch(() => addErrorMessage(t('useDropNodeMutation.errors.unexpected')));
      }
    },
  };
};
