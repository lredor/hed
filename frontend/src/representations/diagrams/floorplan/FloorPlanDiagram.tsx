/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { memo, useContext, useEffect, useRef, useState } from 'react';
import { DocumentTransform, gql, useApolloClient } from '@apollo/client';
import { Kind, visit } from 'graphql';
import { ServerContext, useMultiToast } from '@eclipse-sirius/sirius-components-core';
import { sendFile } from '@eclipse-sirius/sirius-web-application';
import {
  DiagramContext,
  GQLNode,
  GQLNodeStyle,
  GQLViewModifier,
  INodeConverter,
  INodeLayoutHandler,
  NodeData,
  NodeTypeContribution,
  convertOutsideLabels,
} from '@eclipse-sirius/sirius-components-diagrams';
import { Node, NodeProps, useReactFlow } from '@xyflow/react';
import { useTranslation } from 'react-i18next';
import './floor-plan.css';
import { usePlanLabels } from './PlanLabelFilter';
import { FloorPlanRemovalContext } from './FloorPlanRemovalConfirmation';
import { useFloorPlanDrop } from './FloorPlanDrop';

interface PlanStyle extends GQLNodeStyle {
  backgroundNode: boolean;
  imageURL: string;
  caption: string;
  hasImage: boolean;
}
interface PlanData extends NodeData {
  plan: PlanStyle;
  planDiagramId: string;
}
const equipmentSize = 24;
const headerHeight = 48;

/** Keeps each pictogram's center at the same image-relative location while its size stays fixed. */
const scalePlanPosition = (
  position: { x: number; y: number },
  before: { width: number; height: number },
  after: { width: number; height: number }
) => {
  if (
    before.width <= 0 ||
    before.height <= headerHeight ||
    (before.width === after.width && before.height === after.height)
  )
    return position;
  const half = equipmentSize / 2;
  return {
    x: ((position.x + half) * after.width) / before.width - half,
    y:
      headerHeight +
      ((position.y + half - headerHeight) * (after.height - headerHeight)) / (before.height - headerHeight) -
      half,
  };
};
const fragment = gql`
  fragment HedFloorPlanStyle on FloorPlanNodeStyle {
    backgroundNode
    imageURL
    caption
    hasImage
    childrenLayoutStrategy {
      ...childrenLayoutStrategyFragment
    }
  }
`.definitions[0];
export const floorPlanDocumentTransform = new DocumentTransform((document) => {
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
      if (
        fragment.kind !== Kind.FRAGMENT_DEFINITION ||
        selections.some((s) => s.kind === Kind.INLINE_FRAGMENT && s.typeCondition?.name.value === 'FloorPlanNodeStyle')
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

class PlanConverter implements INodeConverter {
  canHandle(node: GQLNode<GQLNodeStyle>) {
    return node.style.__typename === 'FloorPlanNodeStyle';
  }
  handle: INodeConverter['handle'] = (
    engine,
    diagram,
    node,
    _edges,
    parent,
    _isBorder,
    nodes,
    description,
    descriptions
  ) => {
    const nodeDescription = descriptions.find((d) => d.id === node.descriptionId);
    if (!nodeDescription) return;
    const plan = node.style as PlanStyle,
      saved = diagram.layoutData.nodeLayoutData.find((d) => d.id === node.id);
    // Pictogram dimensions stay fixed when labels are toggled or saved layouts are reopened.
    const width = plan.backgroundNode ? node.defaultWidth ?? 1200 : equipmentSize,
      height = plan.backgroundNode ? node.defaultHeight ?? 848 : equipmentSize;
    let position = saved?.position ?? { x: 0, y: 0 };
    if (!plan.backgroundNode && saved && parent) {
      const background = diagram.layoutData.nodeLayoutData.find((entry) => entry.id === parent.id);
      if (background) {
        // Closed plans retain their previous background size in native layout data.
        const before = background.size.width > 0 ? background.size : background.minComputedSize;
        position = scalePlanPosition(position, before, {
          width: parent.defaultWidth ?? 1200,
          height: parent.defaultHeight ?? 848,
        });
      }
    }
    const data: PlanData = {
      plan,
      planDiagramId: diagram.id,
      targetObjectId: node.targetObjectId,
      targetObjectLabel: node.targetObjectLabel,
      targetObjectKind: node.targetObjectKind,
      descriptionId: node.descriptionId,
      nodeDescription,
      insideLabel: null,
      outsideLabels: convertOutsideLabels(node.outsideLabels, diagram.layoutData.labelLayoutData),
      style: {},
      faded: node.state === GQLViewModifier.Faded,
      pinned: plan.backgroundNode || node.pinned,
      defaultWidth: width,
      defaultHeight: height,
      isBorderNode: false,
      borderNodePosition: null,
      labelEditable: false,
      deletable: !plan.backgroundNode,
      connectionHandles: [],
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
      minComputedWidth: null,
      minComputedHeight: null,
      moving: false,
      decorators: node.decorators,
    };
    nodes.push({
      id: node.id,
      type: 'hedFloorPlanNode',
      data,
      parentId: parent?.id,
      position: plan.backgroundNode ? { x: 0, y: 0 } : position,
      width,
      height,
      measured: { width, height },
      style: { width, height },
      draggable: !plan.backgroundNode,
      selectable: !plan.backgroundNode,
      focusable: !plan.backgroundNode,
      zIndex: plan.backgroundNode ? 0 : 10,
      hidden: node.state === GQLViewModifier.Hidden,
    });
    const childDescriptions = description.nodeDescriptions.filter((d) =>
      nodeDescription.childNodeDescriptionIds.includes(d.id)
    );
    engine.convertNodes(diagram, node.childNodes ?? [], node, nodes, description, childDescriptions);
  };
}
class PlanLayout implements INodeLayoutHandler<NodeData> {
  canHandle(node: Node<NodeData>) {
    return node.type === 'hedFloorPlanNode';
  }
  handle: INodeLayoutHandler<NodeData>['handle'] = (engine, previous, node, visible, children, added) => {
    engine.layoutNodes(previous, visible, children, added);
    const oldBackground = previous?.nodes.find((candidate) => candidate.id === node.id);
    // New drops already use the current image size; only existing placements need scaling.
    for (const child of children) {
      const created = added.find((candidate) => candidate.id === child.id),
        old = previous?.nodes.find((candidate) => candidate.id === child.id);
      if (created) child.position = created.position;
      else if (!child.data.isNew && old) {
        child.position =
          (node.data as PlanData).plan.backgroundNode && oldBackground
            ? scalePlanPosition(
                old.position,
                { width: oldBackground.width ?? 1200, height: oldBackground.height ?? 848 },
                { width: node.data.defaultWidth ?? 1200, height: node.data.defaultHeight ?? 848 }
              )
            : old.position;
      }
    }
    node.width = node.data.defaultWidth ?? equipmentSize;
    node.height = node.data.defaultHeight ?? equipmentSize;
    node.data.minComputedWidth = node.width;
    node.data.minComputedHeight = node.height;
  };
}
const uploadMutation = `mutation UploadPlanImage($input:UploadImageInput!){uploadImage(input:$input){__typename ... on UploadImageSuccessPayload{imageId} ... on ErrorPayload{message}}}`;
const backgroundMutation = gql`
  mutation SetPlanBackground($input: InvokeEditingContextActionInput!) {
    invokeEditingContextAction(input: $input) {
      __typename
      ... on ErrorPayload {
        messages {
          body
        }
      }
    }
  }
`;
const imageSize = (file: File) =>
  new Promise<{ width: number; height: number }>((resolve, reject) => {
    const url = URL.createObjectURL(file),
      image = new Image();
    image.onload = () => {
      URL.revokeObjectURL(url);
      resolve({ width: image.naturalWidth, height: image.naturalHeight });
    };
    image.onerror = () => {
      URL.revokeObjectURL(url);
      reject(new Error('image'));
    };
    image.src = url;
  });
const PlanNode = memo(({ id, data: raw }: NodeProps) => {
  const data = raw as PlanData,
    { plan } = data;
  const { httpOrigin } = useContext(ServerContext),
    { editingContextId, readOnly } = useContext(DiagramContext);
  const { t } = useTranslation('home-electrical-designer'),
    client = useApolloClient(),
    { addErrorMessage } = useMultiToast();
  const [showLabels, setShowLabels] = usePlanLabels(data.planDiagramId);
  const registerFloorPlan = useContext(FloorPlanRemovalContext);
  useEffect(() => (plan.backgroundNode ? registerFloorPlan() : undefined), [plan.backgroundNode, registerFloorPlan]);
  const { onDrop, onDragOver } = useFloorPlanDrop(),
    { getNode } = useReactFlow();
  const input = useRef<HTMLInputElement>(null);
  const [busy, setBusy] = useState(false);
  const setBackground = async (imageId: string, aspectRatio = 1) => {
    const result = await client.mutate({
      mutation: backgroundMutation,
      variables: {
        input: {
          id: crypto.randomUUID(),
          editingContextId,
          actionId: `hed-plan-background:${encodeURIComponent(data.targetObjectId)}:${imageId}:${aspectRatio}`,
        },
      },
    });
    if (result.data?.invokeEditingContextAction.__typename === 'ErrorPayload')
      throw new Error(result.data.invokeEditingContextAction.messages?.[0]?.body ?? t('plan.uploadError'));
  };
  const upload = async (file: File) => {
    if (readOnly || busy) return;
    setBusy(true);
    try {
      if (!['image/png', 'image/jpeg', 'image/svg+xml'].includes(file.type) || file.size > 10 * 1024 * 1024)
        throw new Error(t('plan.imageError'));
      const size = await imageSize(file);
      const aspectRatio = size.width / size.height;
      if (!Number.isFinite(aspectRatio) || aspectRatio < 0.2 || aspectRatio > 10) throw new Error(t('plan.imageError'));
      const projectId = window.location.pathname.match(/\/projects\/([^/]+)/)?.[1];
      if (!projectId) throw new Error(t('plan.uploadError'));
      const result = await sendFile(
        httpOrigin,
        uploadMutation,
        { input: { id: crypto.randomUUID(), projectId, label: file.name, file: null } },
        file
      );
      const payload = result.data?.uploadImage;
      if (payload?.__typename !== 'UploadImageSuccessPayload') throw new Error(t('plan.uploadError'));
      await setBackground(payload.imageId, aspectRatio);
    } catch (error) {
      addErrorMessage(error instanceof Error && error.message !== 'image' ? error.message : t('plan.imageError'));
    } finally {
      setBusy(false);
      if (input.current) input.current.value = '';
    }
  };
  const clear = async () => {
    if (readOnly || busy) return;
    setBusy(true);
    try {
      await setBackground('');
    } catch {
      addErrorMessage(t('plan.uploadError'));
    } finally {
      setBusy(false);
    }
  };
  return (
    <div
      className={plan.backgroundNode ? 'hed-floor-plan' : 'hed-floor-equipment'}
      data-testid={plan.backgroundNode ? 'floor-plan-background' : `floor-plan-equipment-${data.targetObjectId}`}
      data-semantic-id={data.targetObjectId}
      title={
        plan.backgroundNode
          ? undefined
          : plan.caption === data.targetObjectLabel
          ? data.targetObjectLabel
          : `${plan.caption} — ${data.targetObjectLabel}`
      }
      style={{ opacity: data.faded ? 0.4 : 1 }}
      onDragOver={onDragOver}
      onDrop={(event) => onDrop(event, plan.backgroundNode ? id : getNode(id)?.parentId)}>
      {plan.backgroundNode ? (
        <>
          <div className="hed-floor-plan-header nodrag nopan" onClick={(event) => event.stopPropagation()}>
            <strong>{plan.caption}</strong>
            <span>{t('plan.hint')}</span>
            <label className="hed-floor-plan-label-filter">
              <input type="checkbox" checked={showLabels} onChange={(event) => setShowLabels(event.target.checked)} />
              {t('plan.showLabels')}
            </label>
            <button disabled={readOnly || busy} onClick={() => input.current?.click()}>
              {busy ? t('plan.loading') : t(plan.hasImage ? 'plan.replace' : 'plan.import')}
            </button>
            {plan.hasImage ? (
              <button disabled={readOnly || busy} onClick={clear}>
                {t('plan.clear')}
              </button>
            ) : null}
            <input
              ref={input}
              type="file"
              aria-label={t('plan.import')}
              accept="image/png,image/jpeg,image/svg+xml"
              hidden
              onChange={(event) => {
                const file = event.target.files?.[0];
                if (file) void upload(file);
              }}
            />
          </div>
          {plan.hasImage ? (
            <img
              className="hed-floor-plan-image"
              src={httpOrigin + plan.imageURL}
              alt={plan.caption}
              draggable={false}
            />
          ) : (
            <div className="hed-floor-plan-placeholder">{t('plan.placeholder')}</div>
          )}
        </>
      ) : (
        <>
          <img src={httpOrigin + plan.imageURL} alt="" draggable={false} />
          <span className="hed-floor-equipment-label" hidden={!showLabels}>
            {plan.caption}
          </span>
        </>
      )}
    </div>
  );
});
export const floorPlanNodeRegistry = {
  nodeConverters: [new PlanConverter()],
  nodeLayoutHandlers: [new PlanLayout()],
  nodeTypeContributions: [<NodeTypeContribution key="hed-floor-plan" type="hedFloorPlanNode" component={PlanNode} />],
};
