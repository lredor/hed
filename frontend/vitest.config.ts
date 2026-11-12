/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import react from '@vitejs/plugin-react';
import { defineConfig } from 'vitest/config';

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    environmentOptions: { jsdom: { url: 'http://localhost/projects/project-1/edit' } },
    include: ['tests/**/*.test.{ts,tsx}'],
    setupFiles: ['./tests/setup.ts'],
    restoreMocks: true,
    clearMocks: true,
    unstubGlobals: true,
    forbidOnly: Boolean(process.env.CI),
    maxWorkers: 2,
    reporters: ['default', 'junit'],
    outputFile: { junit: '../test-results/frontend-unit/results.xml' },
    coverage: {
      provider: 'v8',
      include: ['src/representations/**/*.{ts,tsx}'],
      reporter: ['text', 'html', 'lcov', 'json-summary'],
      reportsDirectory: '../coverage/frontend',
      reportOnFailure: true,
    },
  },
});
