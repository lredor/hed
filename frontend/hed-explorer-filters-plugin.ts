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

/** Routes the application's public tree imports through an adapter, whose own imports still resolve to native Sirius. */
export function hedExplorerFilters(): Plugin {
  let adapter = '';
  let production = false;
  let adapted = false;
  return {
    name: 'hed-exclusive-explorer-filters',
    enforce: 'pre',
    // Keep this import boundary available to the resolver in the development server too.
    config: () => ({ optimizeDeps: { exclude: ['@eclipse-sirius/sirius-web-application'] } }),
    configResolved(config) {
      adapter = normalizePath(resolve(config.root, 'src/representations/modelexplorer/ExclusiveTreeToolBar.tsx'));
      production = config.command === 'build';
    },
    resolveId(source, importer) {
      if (
        source === '@eclipse-sirius/sirius-components-trees' &&
        importer &&
        normalizePath(importer).includes('/node_modules/@eclipse-sirius/sirius-web-application/')
      ) {
        adapted = true;
        return adapter;
      }
      return null;
    },
    buildEnd(error) {
      if (production && !error && !adapted) {
        this.error('The Sirius tree-toolbar import boundary changed; update the HED exclusive-filter adapter.');
      }
    },
  };
}
