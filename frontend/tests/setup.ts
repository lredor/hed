/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import './platform';
import '@testing-library/jest-dom/vitest';
import { afterEach, beforeEach } from 'vitest';
import { cleanup } from '@testing-library/react';
import { readFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import { platform } from './platform';

const resources = Object.fromEntries(
  ['en', 'fr'].map((language) => [
    language,
    {
      'home-electrical-designer': JSON.parse(
        readFileSync(
          resolve(
            dirname(fileURLToPath(import.meta.url)),
            `../../backend/hed-starter/src/main/resources/i18n/${language}/home-electrical-designer.json`
          ),
          'utf8'
        )
      ),
      'sirius-components-core': {
        useDeletionConfirmationDialog: {
          title: language === 'fr' ? 'Supprimer les éléments' : 'Delete elements',
          willDelete: language === 'fr' ? 'Supprime les éléments sélectionnés.' : 'Deletes selected elements.',
          willNotBeAbleToRevert: language === 'fr' ? 'Action irréversible.' : 'Cannot be undone.',
          buttonLabel: language === 'fr' ? 'Supprimer' : 'Delete',
        },
      },
    },
  ])
);
await i18n
  .use(initReactI18next)
  .init({ resources, lng: 'en', fallbackLng: 'en', interpolation: { escapeValue: false } });

beforeEach(async () => {
  localStorage.clear();
  window.history.replaceState(null, '', '/projects/project-1/edit');
  await i18n.changeLanguage('en');
  platform.mutate.mockReset().mockResolvedValue({
    data: {
      invokeEditingContextAction: { __typename: 'SuccessPayload' },
      dropNodes: { messages: [] },
    },
  });
  platform.sendFile.mockReset();
  platform.getNodes.mockReset().mockReturnValue([]);
  platform.getNode.mockReset();
  platform.getEdges.mockReset().mockReturnValue([]);
  platform.synchronizeLayoutData.mockReset();
  platform.renderDiagram.mockReset().mockReturnValue(null);
});
afterEach(cleanup);
