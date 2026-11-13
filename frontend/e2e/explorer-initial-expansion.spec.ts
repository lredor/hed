/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { expect, test } from '@playwright/test';
import { readFileSync } from 'node:fs';
import { randomUUID } from 'node:crypto';

const french = process.env.HED_TEST_LOCALE?.startsWith('fr') ?? false;
const messages = Object.fromEntries(
  readFileSync(
    new URL(
      `../../backend/hed-model/src/main/resources/i18n/hed-messages${french ? '_fr' : ''}.properties`,
      import.meta.url
    ),
    'utf8'
  )
    .split(/\r?\n/)
    .filter((line) => line.includes('='))
    .map((line) => [line.slice(0, line.indexOf('=')), line.slice(line.indexOf('=') + 1)])
);
const escape = (value: string) => value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

test('expand the project root on opening and reopening while allowing manual collapse', async ({ page, request }) => {
  const projectName = `E2E initial expansion ${randomUUID()}`;
  let projectId: string | undefined;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  const item = (name: string) =>
    page
      .locator('[data-treeitemid] p')
      .filter({ hasText: new RegExp(`^${escape(name)}$`) })
      .first();
  const children = [messages['class.Dwelling'], messages['explorer.gtl'], messages['class.Catalog']];
  try {
    await page.goto('/new/project?templateId=hed-example');
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\//);
    projectId = page.url().match(/\/projects\/([^/]+)/)?.[1];
    expect(projectId).toBeTruthy();
    for (const child of children) await expect(item(child)).toBeVisible();
    await page.getByTestId(`${projectName}-toggle`).click();
    for (const child of children) await expect(item(child)).toHaveCount(0);
    await page.getByTestId(`${projectName}-toggle`).click();
    for (const child of children) await expect(item(child)).toBeVisible();
    await page.reload();
    for (const child of children) await expect(item(child)).toBeVisible();
    expect(errors).toEqual([]);
  } finally {
    if (projectId) {
      const deletion = await request.post('/api/graphql', {
        data: {
          query: 'mutation($input:DeleteProjectInput!){deleteProject(input:$input){__typename}}',
          variables: { input: { id: randomUUID(), projectId } },
        },
      });
      expect((await deletion.json()).data.deleteProject.__typename).toMatch(/Success/);
    }
  }
});
