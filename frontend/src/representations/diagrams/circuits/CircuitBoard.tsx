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
import { ServerContext } from '@eclipse-sirius/sirius-components-core';
import {
  GQLNodeStyle,
  GQLNode,
  GQLViewModifier,
  INodeConverter,
  INodeLayoutHandler,
  NodeData,
  NodeTypeContribution,
  ConnectionHandles,
  convertHandles,
  useRefreshConnectionHandles,
} from '@eclipse-sirius/sirius-components-diagrams';
import { Node, NodeProps } from '@xyflow/react';
import './circuits.css';

interface CircuitBoardStyle extends GQLNodeStyle {
  role: 'rail' | 'device';
  order: number;
  caption: string;
  iconURL: string;
}
interface CircuitBoardData extends NodeData {
  circuitBoard: CircuitBoardStyle;
}
const isBoardNode = (node: Node<NodeData>): node is Node<CircuitBoardData> => !!node.data.circuitBoard;

interface BoardChild {
  id: string;
  order: number;
  width: number;
  height: number;
}

/** Shared by conversion and layout so layout-only subscription events cannot restore stale sizes or positions. */
function arrangeRail(children: BoardChild[]) {
  const positions = new Map<string, { x: number; y: number }>();
  let offset = 12;
  [...children]
    .sort((a, b) => a.order - b.order)
    .forEach((child) => {
      positions.set(child.id, { x: offset, y: 36 });
      offset += child.width + 4;
    });
  return {
    positions,
    width: Math.max(160, offset + 8),
    height: 160,
  };
}

function semanticBounds(node: GQLNode<GQLNodeStyle>): { width: number; height: number } {
  const role = (node.style as CircuitBoardStyle).role;
  if (role === 'device') return { width: node.defaultWidth ?? 32, height: node.defaultHeight ?? 112 };
  const { width, height } = arrangeRail(semanticChildren(node));
  return { width, height };
}

function semanticChildren(node: GQLNode<GQLNodeStyle>): BoardChild[] {
  return (node.childNodes ?? [])
    .filter((child) => child.style.__typename === 'CircuitBoardNodeStyle')
    .map((child) => ({ id: child.id, order: (child.style as CircuitBoardStyle).order, ...semanticBounds(child) }));
}

const fragment = gql`
  fragment HedCircuitBoardStyle on CircuitBoardNodeStyle {
    role
    order
    caption
    iconURL
    childrenLayoutStrategy {
      ...childrenLayoutStrategyFragment
    }
  }
`.definitions[0];

/** Adds only the logical board style to the native diagram subscription. */
export const circuitBoardDocumentTransform = new DocumentTransform((document) => {
  if (!document.definitions.some((d) => d.kind === Kind.OPERATION_DEFINITION && d.name?.value === 'diagramEvent'))
    return document;
  return visit(document, {
    Field(field) {
      const selections = field.selectionSet?.selections;
      if (
        field.name.value !== 'style' ||
        !selections?.some((s) => s.kind === Kind.INLINE_FRAGMENT && s.typeCondition?.name.value === 'ImageNodeStyle') ||
        selections.some(
          (s) => s.kind === Kind.INLINE_FRAGMENT && s.typeCondition?.name.value === 'CircuitBoardNodeStyle'
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

/** Converts board nodes while retaining the platform's semantic selection and connection handles. */
class CircuitBoardConverter implements INodeConverter {
  canHandle: INodeConverter['canHandle'] = (node) => node.style.__typename === 'CircuitBoardNodeStyle';

  handle: INodeConverter['handle'] = (
    engine,
    diagram,
    node,
    edges,
    parent,
    _border,
    nodes,
    description,
    descriptions
  ) => {
    const nodeDescription = descriptions.find((candidate) => candidate.id === node.descriptionId);
    if (!nodeDescription) return;
    const saved = diagram.layoutData.nodeLayoutData.find((entry) => entry.id === node.id);
    const board = node.style as CircuitBoardStyle;
    const bounds = semanticBounds(node);
    const parentArrangement =
      parent?.style.__typename === 'CircuitBoardNodeStyle' ? arrangeRail(semanticChildren(parent)) : null;
    const data: CircuitBoardData = {
      circuitBoard: board,
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
      defaultWidth: node.defaultWidth,
      defaultHeight: node.defaultHeight,
      isBorderNode: false,
      borderNodePosition: null,
      labelEditable: false,
      deletable: node.deletable,
      connectionHandles: convertHandles(node.id, edges, saved?.handleLayoutData ?? []),
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
      minComputedWidth: bounds.width,
      minComputedHeight: bounds.height,
      moving: false,
      decorators: node.decorators,
    };
    nodes.push({
      id: node.id,
      type: 'hedCircuitBoardNode',
      data,
      parentId: parent?.id,
      position: parentArrangement?.positions.get(node.id) ?? saved?.position ?? { x: 0, y: 0 },
      ...bounds,
      measured: bounds,
      style: bounds,
      draggable: false,
      hidden: node.state === GQLViewModifier.Hidden,
    });
    const children = description.nodeDescriptions.filter((candidate) =>
      nodeDescription.childNodeDescriptionIds.includes(candidate.id)
    );
    engine.convertNodes(diagram, node.childNodes ?? [], node, nodes, description, children);
  };
}

/** Aligns rail devices horizontally; their board uses the native vertical-list layout. */
class CircuitBoardLayout implements INodeLayoutHandler<NodeData> {
  canHandle: INodeLayoutHandler<NodeData>['canHandle'] = (node) => node.type === 'hedCircuitBoardNode';

  handle: INodeLayoutHandler<NodeData>['handle'] = (engine, previous, node, visible, children, added) => {
    engine.layoutNodes(previous, visible, children, added);
    if (!isBoardNode(node)) return;
    const role = node.data.circuitBoard.role;
    if (role === 'device') {
      node.width = node.data.defaultWidth ?? 32;
      node.height = node.data.defaultHeight ?? 112;
    } else {
      const ordered = children.filter(isBoardNode);
      const arrangement = arrangeRail(
        ordered.map((child) => ({
          id: child.id,
          order: child.data.circuitBoard.order,
          width: child.width ?? 32,
          height: child.height ?? 160,
        }))
      );
      ordered.forEach((child) => {
        child.position = arrangement.positions.get(child.id)!;
        child.draggable = false;
      });
      node.width = arrangement.width;
      node.height = arrangement.height;
    }
    node.data.minComputedWidth = node.width;
    node.data.minComputedHeight = node.height;
  };
}

/** A compact rectangular body with existing MDI icons and native edge anchors. */
const CircuitBoardNode = memo(({ id, data: raw, selected }: NodeProps) => {
  const data = raw as CircuitBoardData;
  const board = data.circuitBoard;
  const { httpOrigin } = useContext(ServerContext);
  useRefreshConnectionHandles(id, data.connectionHandles);
  return (
    <div
      className={`hed-circuit-board-node hed-circuit-${board.role} ${selected ? 'hed-circuit-selected' : ''}`}
      style={{ opacity: data.faded ? 0.4 : 1 }}
      title={data.targetObjectLabel}
      data-testid={`circuit-${board.role}-${data.targetObjectId}`}
      data-order={board.order}>
      <div className="hed-circuit-caption">
        <img src={httpOrigin + '/api/images' + board.iconURL} alt="" draggable={false} />
        {board.caption ? <span>{board.caption}</span> : null}
      </div>
      <ConnectionHandles connectionHandles={data.connectionHandles} />
    </div>
  );
});

export const circuitBoardNodeRegistry = {
  nodeConverters: [new CircuitBoardConverter()],
  nodeLayoutHandlers: [new CircuitBoardLayout()],
  nodeTypeContributions: [
    <NodeTypeContribution key="hed-circuit-board" type="hedCircuitBoardNode" component={CircuitBoardNode} />,
  ],
};
