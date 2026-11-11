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
import { sampleName } from './sample';
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
const escape = (text: string) => text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

test('show saved tables under their semantic owners and retain native open, rename and delete actions', async ({
  page,
  request,
}) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E explorer representations ${randomUUID()}`;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  let projectId: string | undefined;
  const item = (name: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp(`^${escape(name)}$`) })
      .first();
  const parentLabel = (name: string) => item(name).locator('xpath=ancestor::li[2]').locator('p').first();
  const expand = async () => {
    await item(projectName).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await item(sampleName('bedroomLight')).waitFor();
    await expect(item(messages['class.Dwelling'])).toBeVisible();
  };
  const create = async (owner: string, table: string, label: string) => {
    await item(owner).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.newRepresentation'], exact: true }).last().click();
    const dialog = page.getByRole('dialog');
    await expect(dialog).toBeVisible();
    const title = messages[`table.${table}`];
    if (await dialog.getByRole('combobox').count()) {
      await dialog.getByRole('combobox').click();
      await page.getByRole('option', { name: new RegExp(`^${escape(title)}(?: ${escape(title)})?$`) }).click();
    }
    await dialog.getByRole('textbox').first().fill(label);
    const previous = new URL(page.url()).pathname;
    await dialog.getByRole('button', { name: french ? 'Créer' : 'Create', exact: true }).click();
    await page.waitForURL((url) => url.pathname !== previous && /\/edit\/[^/?]+/.test(url.pathname));
    const url = page.url();
    await expand();
    await expect(item(label)).toBeVisible();
    await expect(parentLabel(label)).toHaveText(owner);
    return url;
  };
  try {
    await page.goto('/');
    await page.locator('a[href="/new/project?templateId=hed-example"]').click();
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\//);
    projectId = page.url().match(/\/projects\/([^/]+)\//)?.[1];
    expect(projectId).toBeTruthy();
    await item(projectName).waitFor();
    await expand();
    const equipmentUrl = await create(messages['class.Dwelling'], 'equipment', 'Equipment overview');
    await create(messages['class.Dwelling'], 'circuits', 'Circuit overview');
    await create(sampleName('mainBoard'), 'board', 'Board overview');
    const catalogUrl = await create(messages['class.Catalog'], 'catalog', 'Catalog overview');
    await item('Equipment overview').click();
    await page.waitForURL((url) => url.pathname === new URL(equipmentUrl).pathname);
    await expect(page.getByRole('table')).toBeVisible();
    await item('Catalog overview').click();
    await page.waitForURL((url) => url.pathname === new URL(catalogUrl).pathname);
    await expect(page.getByRole('table')).toBeVisible();
    const icon = item('Catalog overview').locator('..').locator('img').first();
    await expect
      .poll(() => icon.evaluate((element: HTMLImageElement) => element.complete && element.naturalWidth > 0))
      .toBe(true);
    await item('Catalog overview').locator('../..').getByRole('button').click();
    await page
      .getByRole('menuitem', { name: french ? 'Renommer' : 'Rename', exact: true })
      .last()
      .click();
    const rename = page.locator('input:focus');
    await expect(rename).toHaveValue('Catalog overview');
    await rename.fill('Renamed catalog overview');
    await rename.press('Enter');
    await expect(item('Renamed catalog overview')).toBeVisible();
    await page.reload();
    await item(projectName).waitFor();
    await expand();
    await expect(item('Renamed catalog overview')).toBeVisible();
    await expect(parentLabel('Renamed catalog overview')).toHaveText(messages['class.Catalog']);
    await item('Renamed catalog overview').locator('../..').getByRole('button').click();
    const deletion = french ? 'Supprimer' : 'Delete';
    await page.getByRole('menuitem', { name: deletion, exact: true }).last().click();
    await page.getByRole('dialog').getByRole('button', { name: deletion, exact: true }).click();
    await expect(item('Renamed catalog overview')).toHaveCount(0);
    await expect(item(messages['class.Catalog'])).toBeVisible();
    await expect(item(messages['kind.OUTLET'])).toBeVisible();
    await expect(item('Equipment overview')).toBeVisible();
    expect(errors).toEqual([]);
  } finally {
    if (projectId) {
      const check = await request.post('/api/graphql', {
        data: { query: 'query($projectId:ID!){viewer{project(projectId:$projectId){name}}}', variables: { projectId } },
      });
      expect((await check.json()).data?.viewer?.project?.name).toBe(projectName);
      const deletion = await request.post('/api/graphql', {
        data: {
          query: 'mutation($input:DeleteProjectInput!){deleteProject(input:$input){__typename}}',
          variables: { input: { id: randomUUID(), projectId } },
        },
      });
      const payload = await deletion.json();
      expect(payload.errors).toBeUndefined();
      expect(payload.data.deleteProject.__typename).toMatch(/Success/);
    }
  }
});
