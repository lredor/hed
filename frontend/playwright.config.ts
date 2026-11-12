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

const testLocale = process.env.HED_TEST_LOCALE || 'en_GB';

export default defineConfig({
  testDir: './e2e',
  fullyParallel: false,
  forbidOnly: Boolean(process.env.CI),
  globalTimeout: process.env.CI ? 20 * 60 * 1000 : undefined,
  workers: 1,
  timeout: 60000,
  expect: { timeout: 10000 },
  outputDir: `../test-results/${testLocale}/artifacts`,
  reporter: process.env.CI
    ? [
        ['line'],
        ['html', { outputFolder: `../playwright-report/${testLocale}`, open: 'never' }],
        ['junit', { outputFile: `../test-results/${testLocale}/results.xml` }],
      ]
    : 'list',
  use: {
    baseURL: process.env.HED_BASE_URL || 'http://localhost:8080',
    channel: 'chrome',
    headless: true,
    viewport: { width: 1500, height: 1050 },
    locale: testLocale.startsWith('fr') ? 'fr-FR' : 'en-GB',
    screenshot: 'only-on-failure',
    trace: 'retain-on-failure',
  },
});
