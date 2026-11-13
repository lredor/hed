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
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import RouteIcon from '@mui/icons-material/Route';
import { useTranslation } from 'react-i18next';
import { ServerContext } from '@eclipse-sirius/sirius-components-core';
import {
  GQLNodeStyle,
  GQLNode,
  GQLViewModifier,
  INodeConverter,
  INodeLayoutHandler,
  NodeData,
  EdgeData,
  DiagramContext,
  NodeTypeContribution,
  ConnectionHandles,
  Resizer,
  convertHandles,
  useRefreshConnectionHandles,
  useSynchronizeLayoutData,
} from '@eclipse-sirius/sirius-components-diagrams';
import { Edge, Node, NodeProps, Position, useReactFlow } from '@xyflow/react';
import './circuits.css';

interface CircuitBoardStyle extends GQLNodeStyle {
  role: 'rail' | 'device';
  order: number;
  caption: string;
  iconURL: string;
}
interface CircuitBoardData extends NodeData {
  circuitBoard: CircuitBoardStyle;
  boardWidth?: number;
  hasProtectionLinks: boolean;
}
const isBoardNode = (node: Node<NodeData>): node is Node<CircuitBoardData> => !!node.data.circuitBoard;

/** Applies an explicit north-to-north protection route to one rail, preserving all other diagram elements. */
export function routeCircuitProtectionsAbove(nodes: Node<NodeData>[], edges: Edge<EdgeData>[], railId: string) {
  const devices = new Set(
    nodes
      .filter((node) => node.parentId === railId && isBoardNode(node) && node.data.circuitBoard.role === 'device')
      .map((node) => node.id)
  );
  const connections = new Set(
    edges
      .filter(
        (edge) =>
          devices.has(edge.source) &&
          devices.has(edge.target) &&
          edge.data?.edgeAppearanceData.gqlStyle.lineStyle === 'Solid'
      )
      .map((edge) => edge.id)
  );
  return {
    nodes: nodes.map((node) => {
      if (!devices.has(node.id)) return node;
      const affected = node.data.connectionHandles.filter((handle) => connections.has(handle.edgeId));
      if (!affected.length) return node;
      return {
        ...node,
        data: {
          ...node.data,
          connectionHandles: node.data.connectionHandles.map((handle) => {
            if (!connections.has(handle.edgeId)) return handle;
            const sameSide = affected.filter((candidate) => candidate.type === handle.type);
            return {
              ...handle,
              position: Position.Top,
              XYPosition: { x: ((node.width ?? 64) * (sameSide.indexOf(handle) + 1)) / (sameSide.length + 1), y: 0 },
            };
          }),
        },
      };
    }),
    edges: edges.map((edge) =>
      connections.has(edge.id)
        ? { ...edge, data: { ...edge.data!, bendingPoints: [], relativePositionBendingPoints: [] } }
        : edge
    ),
  };
}

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
      positions.set(child.id, { x: offset, y: 80 });
      offset += child.width + 4;
    });
  return {
    positions,
    width: Math.max(160, offset + 8),
    height: 260,
  };
}

function semanticBounds(node: GQLNode<GQLNodeStyle>): { width: number; height: number } {
  const role = (node.style as CircuitBoardStyle).role;
  if (role === 'device') return { width: node.defaultWidth ?? 64, height: node.defaultHeight ?? 112 };
  const { width, height } = arrangeRail(semanticChildren(node));
  return { width: Math.max(width, node.defaultWidth ?? 0), height: Math.max(height, node.defaultHeight ?? 0) };
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
    const minimum = semanticBounds(node);
    const resized = board.role === 'rail' && !!saved?.resizedByUser;
    const bounds = { ...minimum, height: resized ? Math.max(minimum.height, saved?.size.height ?? 0) : minimum.height };
    const parentArrangement =
      parent?.style.__typename === 'CircuitBoardNodeStyle' ? arrangeRail(semanticChildren(parent)) : null;
    const handles = convertHandles(node.id, edges, saved?.handleLayoutData ?? []);
    const deviceIds = new Set((node.childNodes ?? []).map((child) => child.id));
    const data: CircuitBoardData = {
      circuitBoard: board,
      hasProtectionLinks:
        board.role === 'rail' &&
        edges.some(
          (edge) => deviceIds.has(edge.sourceId) && deviceIds.has(edge.targetId) && edge.style.lineStyle === 'Solid'
        ),
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
      connectionHandles: handles,
      isNew: !saved,
      resizedByUser: resized,
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
      minComputedWidth: minimum.width,
      minComputedHeight: minimum.height,
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

  handle: INodeLayoutHandler<NodeData>['handle'] = (engine, previous, node, visible, children, added, forced) => {
    engine.layoutNodes(previous, visible, children, added);
    if (!isBoardNode(node)) return;
    const role = node.data.circuitBoard.role;
    if (role === 'device') {
      node.width = node.data.defaultWidth ?? 64;
      node.height = node.data.defaultHeight ?? 112;
    } else {
      const ordered = children.filter(isBoardNode);
      const arrangement = arrangeRail(
        ordered.map((child) => ({
          id: child.id,
          order: child.data.circuitBoard.order,
          width: child.width ?? 64,
          height: child.height ?? 112,
        }))
      );
      ordered.forEach((child) => {
        child.position = arrangement.positions.get(child.id)!;
        child.draggable = false;
      });
      const minimumWidth = Math.max(arrangement.width, node.data.defaultWidth ?? 0);
      const minimumHeight = Math.max(arrangement.height, node.data.defaultHeight ?? 0);
      // The native board list forces a common width; vertical resizing adds routing room only.
      // Layout may visit the rail again outside its parent pass; keep the width imposed by the list.
      if (forced?.width != null) node.data.boardWidth = forced.width;
      node.width = Math.max(minimumWidth, node.data.boardWidth ?? 0);
      node.height = Math.max(
        minimumHeight,
        node.data.resizedByUser || node.resizing ? node.height ?? 0 : 0,
        forced?.height ?? 0
      );
      node.data.minComputedWidth = minimumWidth;
      node.data.minComputedHeight = minimumHeight;
      node.style = { ...node.style, width: node.width, height: node.height };
      return;
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
  const { readOnly } = useContext(DiagramContext);
  const { getNodes, getEdges } = useReactFlow<Node<NodeData>, Edge<EdgeData>>();
  const { synchronizeLayoutData } = useSynchronizeLayoutData();
  const { t } = useTranslation('home-electrical-designer');
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
        {data.hasProtectionLinks && !readOnly ? (
          <Tooltip title={t('circuits.routeProtectionsAbove')}>
            <IconButton
              className="hed-circuit-route"
              size="small"
              aria-label={t('circuits.routeProtectionsAbove')}
              onClick={(event) => {
                event.stopPropagation();
                synchronizeLayoutData(
                  crypto.randomUUID(),
                  'layout',
                  routeCircuitProtectionsAbove(getNodes(), getEdges(), id),
                  'DEACTIVATE'
                );
              }}>
              <RouteIcon fontSize="small" />
            </IconButton>
          </Tooltip>
        ) : null}
      </div>
      <ConnectionHandles connectionHandles={data.connectionHandles} />
      {board.role === 'rail' ? <Resizer data={data} selected={selected} /> : null}
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
