/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { resolve } from 'node:path';
import { normalizePath, type Plugin } from 'vite';
import { adaptTablePresentation } from './table-presentation-adapter';

/** Keeps the native table renderer while supplying HED's fixed summary order and descriptive headers. */
export function hedTablePresentation(): Plugin {
  let adapter = '';
  let adapted = false;
  let production = false;
  return {
    name: 'hed-table-presentation',
    enforce: 'pre',
    config: () => ({ optimizeDeps: { exclude: ['@eclipse-sirius/sirius-components-tables'] } }),
    configResolved(config) {
      adapter = normalizePath(resolve(config.root, 'src/representations/tables/HedTablePresentation.tsx'));
      production = config.command === 'build';
    },
    transform(code, id) {
      if (!normalizePath(id).endsWith('/@eclipse-sirius/sirius-components-tables/dist/sirius-components-tables.es.js'))
        return null;
      adapted = true;
      return { code: adaptTablePresentation(code, adapter), map: null };
    },
    buildEnd(error) {
      if (production && !error && !adapted)
        this.error('The Sirius table module changed; update the HED table presentation adapter.');
    },
  };
}
