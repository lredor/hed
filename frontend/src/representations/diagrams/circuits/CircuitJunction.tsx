/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { memo, useContext } from 'react';
import { DocumentTransform, gql } from '@apollo/client';
import { Kind, visit } from 'graphql';
import Tooltip from '@mui/material/Tooltip';
import {
  DiagramContext,
  ConnectionHandles,
  convertHandles,
  useRefreshConnectionHandles,
  GQLViewModifier,
  NodeTypeContribution,
} from '@eclipse-sirius/sirius-components-diagrams';
import type { INodeConverter, INodeLayoutHandler, NodeData } from '@eclipse-sirius/sirius-components-diagrams';
import type { NodeProps } from '@xyflow/react';
import './circuit-junction.css';

/** Manipulation area in diagram units; the visible symbol is deliberately smaller. */
export const JUNCTION_SIZE = 24;
const CENTER = JUNCTION_SIZE / 2;
const fragment = gql`
  fragment HedCircuitJunctionStyle on CircuitJunctionNodeStyle {
    childrenLayoutStrategy {
      ...childrenLayoutStrategyFragment
    }
  }
`.definitions[0];

/** Adds the dedicated junction style without changing other diagram subscriptions. */
export const circuitJunctionDocumentTransform = new DocumentTransform((document) => {
  if (!document.definitions.some((d) => d.kind === Kind.OPERATION_DEFINITION && d.name?.value === 'diagramEvent'))
    return document;
  return visit(document, {
    Field(field) {
      const selections = field.selectionSet?.selections;
      if (
        field.name.value !== 'style' ||
        !selections?.some((s) => s.kind === Kind.INLINE_FRAGMENT && s.typeCondition?.name.value === 'ImageNodeStyle') ||
        selections.some(
          (s) => s.kind === Kind.INLINE_FRAGMENT && s.typeCondition?.name.value === 'CircuitJunctionNodeStyle'
        ) ||
        fragment.kind !== Kind.FRAGMENT_DEFINITION
      )
        return;
      return {
        ...field,
        selectionSet: {
          ...field.selectionSet,
          selections: [
            ...selections,
            { kind: Kind.INLINE_FRAGMENT, typeCondition: fragment.typeCondition, selectionSet: fragment.selectionSet },
          ],
        },
      };
    },
  });
});

/** Retains saved graph identities and centres when old three-unit junctions acquire a larger hit area. */
class CircuitJunctionConverter implements INodeConverter {
  canHandle: INodeConverter['canHandle'] = (node) => node.style.__typename === 'CircuitJunctionNodeStyle';
  handle: INodeConverter['handle'] = (
    _engine,
    diagram,
    node,
    edges,
    parent,
    _border,
    nodes,
    _description,
    descriptions
  ) => {
    const nodeDescription = descriptions.find((d) => d.id === node.descriptionId);
    if (!nodeDescription) return;
    const saved = diagram.layoutData.nodeLayoutData.find((entry) => entry.id === node.id);
    const position = saved
      ? {
          x: saved.position.x + (saved.size.width === 3 ? (3 - JUNCTION_SIZE) / 2 : 0),
          y: saved.position.y + (saved.size.height === 3 ? (3 - JUNCTION_SIZE) / 2 : 0),
        }
      : { x: 0, y: 0 };
    const connectionHandles = convertHandles(node.id, edges, saved?.handleLayoutData ?? []).map((handle) => ({
      ...handle,
      XYPosition: { x: CENTER, y: CENTER },
      handleStyleTransform: null,
      isHidden: true,
    }));
    const data: NodeData = {
      targetObjectId: node.targetObjectId,
      targetObjectLabel: node.targetObjectLabel,
      targetObjectKind: node.targetObjectKind,
      descriptionId: node.descriptionId,
      nodeDescription,
      insideLabel: null,
      outsideLabels: {},
      style: {},
      faded: node.state === GQLViewModifier.Faded,
      pinned: node.pinned,
      defaultWidth: JUNCTION_SIZE,
      defaultHeight: JUNCTION_SIZE,
      isBorderNode: false,
      borderNodePosition: null,
      labelEditable: false,
      deletable: node.deletable,
      connectionHandles,
      isNew: !saved,
      resizedByUser: false,
      movedByUser: saved?.movedByUser ?? false,
      isListChild: false,
      isDraggedNode: false,
      isDropNodeTarget: false,
      isDragNodeSource: false,
      isDropNodeCandidate: false,
      isHovered: false,
      isLastNodeSelected: false,
      connectionLinePositionOnNode: 'none',
      nodeAppearanceData: { gqlStyle: node.style, customizedStyleProperties: [] },
      minComputedWidth: JUNCTION_SIZE,
      minComputedHeight: JUNCTION_SIZE,
      moving: false,
      decorators: node.decorators,
    };
    nodes.push({
      id: node.id,
      type: 'hedCircuitJunctionNode',
      data,
      parentId: parent?.id,
      position,
      width: JUNCTION_SIZE,
      height: JUNCTION_SIZE,
      measured: { width: JUNCTION_SIZE, height: JUNCTION_SIZE },
      style: { width: JUNCTION_SIZE, height: JUNCTION_SIZE },
      draggable: node.pinned ? false : undefined,
      hidden: node.state === GQLViewModifier.Hidden,
      zIndex: 10,
    });
  };
}

/** Keeps the full hit area fixed and all edge endpoints at the visible symbol's centre, including during dragging. */
class CircuitJunctionLayout implements INodeLayoutHandler<NodeData> {
  canHandle: INodeLayoutHandler<NodeData>['canHandle'] = (node) => node.type === 'hedCircuitJunctionNode';
  handle: INodeLayoutHandler<NodeData>['handle'] = (_engine, _previous, node) => {
    node.width = JUNCTION_SIZE;
    node.height = JUNCTION_SIZE;
    node.style = { ...node.style, width: JUNCTION_SIZE, height: JUNCTION_SIZE };
    node.data.connectionHandles.forEach((handle) => {
      handle.XYPosition = { x: CENTER, y: CENTER };
      handle.handleStyleTransform = null;
    });
  };
  calculateCustomNodeEdgeHandlePosition: NonNullable<
    INodeLayoutHandler<NodeData>['calculateCustomNodeEdgeHandlePosition']
  > = () => ({ x: CENTER, y: CENTER });
}

/** Shows a small electrical junction while leaving its larger transparent surface available to native drag and selection. */
export const CircuitJunctionNode = memo(({ id, data: raw, selected }: NodeProps) => {
  const data = raw as NodeData;
  const { readOnly } = useContext(DiagramContext);
  const immovable = readOnly || data.pinned;
  useRefreshConnectionHandles(id, data.connectionHandles);
  return (
    <Tooltip title={data.targetObjectLabel}>
      <div
        data-testid={`circuit-junction-${data.targetObjectId}`}
        className={`hed-circuit-junction ${selected ? 'hed-circuit-junction-selected' : ''} ${
          immovable ? 'nodrag' : ''
        }`}
        style={{ opacity: data.faded ? 0.4 : 1, cursor: immovable ? 'default' : 'grab' }}>
        <span className="hed-circuit-junction-symbol" aria-hidden="true" />
        <ConnectionHandles connectionHandles={data.connectionHandles} />
      </div>
    </Tooltip>
  );
});

export const circuitJunctionNodeRegistry = {
  nodeConverters: [new CircuitJunctionConverter()],
  nodeLayoutHandlers: [new CircuitJunctionLayout()],
  nodeTypeContributions: [
    <NodeTypeContribution key="hed-circuit-junction" type="hedCircuitJunctionNode" component={CircuitJunctionNode} />,
  ],
};
