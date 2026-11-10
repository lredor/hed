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
  GQLNode,
  GQLNodeStyle,
  GQLViewModifier,
  INodeConverter,
  NodeData,
  NodeTypeContribution,
  INodeLayoutHandler,
  ILayoutEngine,
  RawDiagram,
  convertInsideLabel,
  convertOutsideLabels,
  useDropNodeStyle,
} from '@eclipse-sirius/sirius-components-diagrams';
import { Node, NodeProps } from '@xyflow/react';
import { useTranslation } from 'react-i18next';
import './panel.css';

interface PanelStyle extends GQLNodeStyle {
  role: 'board' | 'rail' | 'device';
  imageURL: string;
  order: number;
  caption: string;
  capacityModules: number;
  usedModules: number;
  labelText: string | null;
}
export interface PanelData extends NodeData {
  panel: PanelStyle;
  childCount: number;
}
const isPanel = (node: Node<NodeData>): node is Node<PanelData> => !!node.data.panel;
// Shared by conversion (including reopening saved diagrams) and subsequent layouts.
const childPosition = (role: PanelStyle['role'], parentWidth: number, index: number, precedingWidth: number) => {
  const scale = parentWidth / (role === 'board' ? 1200 : 1104);
  return role === 'board'
    ? { x: 48 * scale, y: 198 * scale + index * Math.max(252, 420 * scale) }
    : { x: 24 * scale + precedingWidth, y: 222 * scale - 178 / 2 };
};

// Extend the native diagram subscription without forking Sirius Web's diagram representation.
const panelFragment = gql`
  fragment HedPanelNodeStyle on PanelNodeStyle {
    role
    imageURL
    order
    caption
    capacityModules
    usedModules
    labelText
    childrenLayoutStrategy {
      ...childrenLayoutStrategyFragment
    }
  }
`.definitions[0];
export const panelDocumentTransform = new DocumentTransform((document) => {
  if (!document.definitions.some((d) => d.kind === Kind.OPERATION_DEFINITION && d.name?.value === 'diagramEvent'))
    return document;
  return visit(document, {
    Field(field) {
      const selections = field.selectionSet?.selections;
      if (
        field.name.value !== 'style' ||
        !selections?.some((s) => s.kind === Kind.INLINE_FRAGMENT && s.typeCondition?.name.value === 'ImageNodeStyle')
      )
        return;
      if (selections.some((s) => s.kind === Kind.INLINE_FRAGMENT && s.typeCondition?.name.value === 'PanelNodeStyle'))
        return;
      if (panelFragment.kind !== Kind.FRAGMENT_DEFINITION) return;
      return {
        ...field,
        selectionSet: {
          ...field.selectionSet,
          selections: [
            ...selections,
            {
              kind: Kind.INLINE_FRAGMENT,
              typeCondition: panelFragment.typeCondition,
              selectionSet: panelFragment.selectionSet,
            },
          ],
        },
      };
    },
  });
});

/** Converts one native semantic node per board, rail and device. */
class PanelNodeConverter implements INodeConverter {
  canHandle(node: GQLNode<GQLNodeStyle>) {
    return node.style.__typename === 'PanelNodeStyle';
  }
  handle: INodeConverter['handle'] = (
    engine,
    diagram,
    node,
    edges,
    parent,
    isBorder,
    nodes,
    description,
    nodeDescriptions
  ) => {
    const nodeDescription = nodeDescriptions.find((d) => d.id === node.descriptionId);
    if (!nodeDescription) return;
    const panel = node.style as PanelStyle;
    const saved = diagram.layoutData.nodeLayoutData.find((d) => d.id === node.id);
    const data: PanelData = {
      targetObjectId: node.targetObjectId,
      targetObjectLabel: node.targetObjectLabel,
      targetObjectKind: node.targetObjectKind,
      descriptionId: node.descriptionId,
      nodeDescription,
      panel,
      childCount: node.childNodes?.length ?? 0,
      insideLabel: null,
      outsideLabels: convertOutsideLabels(node.outsideLabels, diagram.layoutData.labelLayoutData),
      style: {},
      faded: node.state === GQLViewModifier.Faded,
      pinned: false,
      defaultWidth: node.defaultWidth,
      defaultHeight: node.defaultHeight,
      isBorderNode: false,
      borderNodePosition: null,
      labelEditable: false,
      deletable: node.deletable,
      connectionHandles: [],
      isNew: !saved,
      resizedByUser: false,
      movedByUser: false,
      isListChild: false,
      isDraggedNode: false,
      isDropNodeTarget: false,
      isDragNodeSource: false,
      isDropNodeCandidate: false,
      isHovered: false,
      isLastNodeSelected: false,
      connectionLinePositionOnNode: 'none',
      nodeAppearanceData: { gqlStyle: node.style, customizedStyleProperties: [] },
      minComputedWidth: null,
      minComputedHeight: null,
      moving: false,
      decorators: node.decorators,
    };
    data.insideLabel = convertInsideLabel(
      node.insideLabel,
      diagram.layoutData.labelLayoutData,
      data,
      '0px solid transparent'
    );
    const width = node.defaultWidth ?? 38,
      height = node.defaultHeight ?? 218;
    let position = saved?.position ?? { x: 0, y: 0 };
    if (parent?.style.__typename === 'PanelNodeStyle') {
      const parentStyle = parent.style as PanelStyle;
      const preceding = (parent.childNodes ?? []).filter(
        (sibling) => (sibling.style as PanelStyle).order < panel.order
      );
      position = childPosition(
        parentStyle.role,
        parent.defaultWidth ?? 38,
        panel.order,
        preceding.reduce((sum, sibling) => sum + (sibling.defaultWidth ?? 38), 0)
      );
    }
    nodes.push({
      id: node.id,
      type: 'hedPanelNode',
      data,
      parentId: parent?.id,
      position,
      width,
      height,
      measured: { width, height },
      style: { width, height },
      draggable: panel.role === 'device',
      hidden: node.state === GQLViewModifier.Hidden,
    });
    const childDescriptions = description.nodeDescriptions.filter((d) =>
      nodeDescription.childNodeDescriptionIds.includes(d.id)
    );
    engine.convertNodes(diagram, node.childNodes ?? [], node, nodes, description, childDescriptions);
  };
}

/** All placement comes from semantic order; saved free positions never override rail order. */
class PanelLayoutHandler implements INodeLayoutHandler<NodeData> {
  canHandle(node: Node<NodeData>) {
    return node.type === 'hedPanelNode';
  }
  handle(
    engine: ILayoutEngine,
    previous: RawDiagram | null,
    node: Node<NodeData>,
    visible: Node<NodeData>[],
    children: Node<NodeData>[],
    added: Node<NodeData>[]
  ) {
    engine.layoutNodes(previous, visible, children, added);
    if (!isPanel(node)) return;
    node.width = node.data.defaultWidth ?? 38;
    node.height = node.data.defaultHeight ?? 218;
    const ordered = children.filter(isPanel).sort((a, b) => a.data.panel.order - b.data.panel.order);
    // cabinet.svg: row starts at (48,198), device area at x=72, rail centre at y=420.
    let precedingWidth = 0;
    ordered.forEach((child, index) => {
      child.position = childPosition(node.data.panel.role, node.width!, index, precedingWidth);
      child.draggable = child.data.panel.role === 'device';
      precedingWidth += child.width ?? 38;
    });
    node.data.minComputedWidth = node.width;
    node.data.minComputedHeight = node.height;
  }
}

const PanelNode = memo(({ data: raw, selected, dragging }: NodeProps) => {
  const data = raw as PanelData;
  const panel = data.panel;
  const { httpOrigin } = useContext(ServerContext);
  const { t } = useTranslation('home-electrical-designer');
  const { style: dropStyle } = useDropNodeStyle(
    data.isDropNodeTarget,
    data.isDragNodeSource,
    data.isDropNodeCandidate,
    dragging
  );
  return (
    <div
      className={`hed-panel-node hed-panel-${panel.role} ${selected ? 'hed-selected' : ''} ${
        data.isDropNodeTarget ? 'hed-drop-target' : ''
      }`}
      style={{ ...dropStyle, opacity: data.faded ? 0.4 : 1 }}
      data-testid={`panel-${panel.role}-${data.targetObjectId}`}
      data-semantic-id={data.targetObjectId}
      data-order={panel.order}
      data-panel-role={panel.role}
      aria-label={data.targetObjectLabel}>
      {panel.role === 'board' ? (
        <>
          <img className="hed-cabinet-image" src={httpOrigin + panel.imageURL} alt="" draggable={false} />
          <div className="hed-panel-title">{panel.caption}</div>
          {data.childCount === 0 ? <div className="hed-panel-empty">{t('panel.empty')}</div> : null}
        </>
      ) : null}
      {panel.role === 'rail' ? (
        <>
          <div className="hed-rail-title">{panel.caption}</div>
          <span className={`hed-rail-capacity ${panel.usedModules > panel.capacityModules ? 'hed-over-capacity' : ''}`}>
            {panel.usedModules} / {panel.capacityModules} {t('panel.modules')}
          </span>
        </>
      ) : null}
      {panel.role === 'device' ? (
        <>
          <img
            className="hed-device-image"
            src={httpOrigin + panel.imageURL}
            alt={data.targetObjectLabel}
            draggable={false}
          />
          {panel.caption ? (
            <div className="hed-device-reference" title={panel.caption}>
              {panel.caption}
            </div>
          ) : null}
          {panel.labelText ? (
            <div className="hed-device-label" title={panel.labelText}>
              {panel.labelText}
            </div>
          ) : null}
        </>
      ) : null}
    </div>
  );
});

export const panelNodeRegistry = {
  nodeConverters: [new PanelNodeConverter()],
  nodeLayoutHandlers: [new PanelLayoutHandler()],
  nodeTypeContributions: [<NodeTypeContribution key="hed-panel" type="hedPanelNode" component={PanelNode} />],
};
