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
  navigationBarIconExtensionPoint,
  footerExtensionPoint,
} from '@eclipse-sirius/sirius-web-application';
import { ExtensionRegistry } from '@eclipse-sirius/sirius-components-core';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import Tooltip from '@mui/material/Tooltip';
import '@fontsource/roboto/400.css';
import '@xyflow/react/dist/style.css';
import './style.css';
const httpOrigin = import.meta.env.VITE_BACKEND_URL || window.location.origin;
const wsOrigin = httpOrigin.replace(/^http/, 'ws');
const registry = new ExtensionRegistry();
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
          fontSize: 20,
          padding: '4px 12px',
          color: 'inherit',
          textDecoration: 'none',
        }}>
        <span aria-hidden="true">⚡</span>
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
registry.addComponent(footerExtensionPoint, { identifier: 'hed-footer', Component: Footer });
createRoot(document.getElementById('root')!).render(
  <SiriusWebApplication httpOrigin={httpOrigin} wsOrigin={wsOrigin} extensionRegistry={registry} />
);
