/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { hedExplorerFilters } from './hed-explorer-filters-plugin';
import { hedTablePresentation } from './hed-table-presentation-plugin';
export default defineConfig({
  plugins: [react(), hedExplorerFilters(), hedTablePresentation()],
  server: {
    port: 5173,
    proxy: {
      '/api': { target: 'http://localhost:8080', ws: true },
      '/help': { target: 'http://localhost:8080' },
      '/subscriptions': { target: 'ws://localhost:8080', ws: true },
    },
  },
  build: {
    outDir: '../backend/hed-application/src/main/resources/static',
    emptyOutDir: true,
    license: { fileName: 'assets/third-party-licenses.md' },
  },
});
