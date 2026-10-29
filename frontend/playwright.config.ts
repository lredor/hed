/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './e2e',
  fullyParallel: false,
  workers: 1,
  timeout: 60000,
  expect: { timeout: 10000 },
  outputDir: '../test-results/bootstrap',
  use: {
    baseURL: process.env.HED_BASE_URL || 'http://localhost:8080',
    channel: 'chrome',
    headless: true,
    viewport: { width: 1500, height: 1050 },
    locale: process.env.HED_TEST_LOCALE?.startsWith('fr') ? 'fr-FR' : 'en-GB',
    screenshot: 'only-on-failure',
    trace: 'retain-on-failure',
  },
});
