/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { createRoot } from 'react-dom/client';
import {
  SiriusWebApplication,
  DiagramRepresentationConfiguration,
  apolloClientOptionsConfigurersExtensionPoint,
  navigationBarIconExtensionPoint,
  navigationBarMenuHelpURLExtensionPoint,
  footerExtensionPoint,
} from '@eclipse-sirius/sirius-web-application';
import { ExtensionRegistry, representationFactoryExtensionPoint } from '@eclipse-sirius/sirius-components-core';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import Tooltip from '@mui/material/Tooltip';
import '@fontsource/roboto/400.css';
import '@xyflow/react/dist/style.css';
import './style.css';
import { panelDocumentTransform, panelNodeRegistry } from './representations/diagrams/panel/PanelDiagram';
import { diagramRendererReactFlowPropsCustomizerExtensionPoint } from '@eclipse-sirius/sirius-components-diagrams';
import { usePanelRailDropProps } from './representations/diagrams/panel/PanelRailDrop';
import {
  floorPlanDocumentTransform,
  floorPlanNodeRegistry,
} from './representations/diagrams/floorplan/FloorPlanDiagram';
import {
  floorPlanConfirmationFactory,
  FloorPlanConfirmationMergeStrategy,
} from './representations/diagrams/floorplan/FloorPlanRemovalConfirmation';
const httpOrigin = import.meta.env.VITE_BACKEND_URL || window.location.origin;
const wsOrigin = httpOrigin.replace(/^http/, 'ws');
const registry = new ExtensionRegistry();
const registryMergeStrategy = new FloorPlanConfirmationMergeStrategy();
function Brand() {
  const { t } = useTranslation('sirius-web-application', { keyPrefix: 'navigationBarIcon' });
  const label = t('backToHomepage');
  return (
    <Tooltip title={label}>
      <Link
        to="/"
        aria-label={label}
        style={{
          display: 'inline-flex',
          alignItems: 'center',
          padding: '4px 12px',
          color: 'inherit',
          textDecoration: 'none',
        }}>
        <svg aria-hidden="true" focusable="false" width="24" height="24" viewBox="0 0 24 24">
          <use href="/api/images/hed/mdi/home-lightning-bolt-outline.svg?v=2#mdi-home-lightning-bolt-outline" />
        </svg>
      </Link>
    </Tooltip>
  );
}
function Footer() {
  const { t, i18n } = useTranslation('home-electrical-designer');
  document.documentElement.lang = i18n.language;
  return <span style={{ padding: '0 12px' }}>Home Electrical Designer · {t('scope')}</span>;
}
registry.addComponent(navigationBarIconExtensionPoint, { identifier: 'hed-brand', Component: Brand });
registry.putData(navigationBarMenuHelpURLExtensionPoint, {
  identifier: 'hed-user-guide',
  data: httpOrigin + '/api/hed/help',
});
registry.addComponent(footerExtensionPoint, { identifier: 'hed-footer', Component: Footer });
registry.putData(apolloClientOptionsConfigurersExtensionPoint, {
  identifier: 'hed-diagram-styles',
  data: [
    (options) => ({
      ...options,
      documentTransform: options.documentTransform
        ? options.documentTransform.concat(panelDocumentTransform).concat(floorPlanDocumentTransform)
        : panelDocumentTransform.concat(floorPlanDocumentTransform),
    }),
  ],
});
registry.putData(diagramRendererReactFlowPropsCustomizerExtensionPoint, {
  identifier: 'hed-rail-drop',
  data: [usePanelRailDropProps],
});
registry.putData(representationFactoryExtensionPoint, {
  identifier: 'hed-floor-plan-confirmation',
  data: [floorPlanConfirmationFactory],
});
createRoot(document.getElementById('root')!).render(
  <SiriusWebApplication
    httpOrigin={httpOrigin}
    wsOrigin={wsOrigin}
    extensionRegistry={registry}
    extensionRegistryMergeStrategy={registryMergeStrategy}>
    <DiagramRepresentationConfiguration
      nodeTypeRegistry={{
        nodeConverters: [...panelNodeRegistry.nodeConverters, ...floorPlanNodeRegistry.nodeConverters],
        nodeLayoutHandlers: [...panelNodeRegistry.nodeLayoutHandlers, ...floorPlanNodeRegistry.nodeLayoutHandlers],
        nodeTypeContributions: [
          ...panelNodeRegistry.nodeTypeContributions,
          ...floorPlanNodeRegistry.nodeTypeContributions,
        ],
      }}
    />
  </SiriusWebApplication>
);
