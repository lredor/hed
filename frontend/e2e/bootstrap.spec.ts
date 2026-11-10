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
const properties = new URL(
  `../../backend/hed-model/src/main/resources/i18n/hed-messages${french ? '_fr' : ''}.properties`,
  import.meta.url
);
const messages = Object.fromEntries(
  readFileSync(properties, 'utf8')
    .split(/\r?\n/)
    .filter((line) => line.includes('='))
    .map((line) => {
      const separator = line.indexOf('=');
      return [line.slice(0, separator), line.slice(separator + 1)];
    })
);

test('create, edit and reopen a dwelling with catalog names, category icons and rail specifications', async ({
  page,
  request,
}) => {
  const projectName = `E2E bootstrap ${randomUUID()}`;
  const typeName = `Garden type ${randomUUID()}`;
  const equipmentName = `Outlet ${randomUUID()}`;
  const roomName = `Temporary room ${randomUUID()}`;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  let projectId: string | undefined;
  const item = (name: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp(`^${name.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}(?: - .+)?$`) })
      .first();
  const more = async (name: string) => {
    await item(name).locator('../..').getByRole('button').click();
  };
  const expand = async () => {
    await item(projectName).waitFor();
    await more(projectName);
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).click();
    await item('Bedroom light').waitFor();
  };
  try {
    await page.goto('/');
    await page.locator('a[href="/new/project?templateId=hed-example"]').click();
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\//);
    projectId = page.url().match(/\/projects\/([^/]+)\//)?.[1];
    expect(projectId).toBeTruthy();
    await expand();

    const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
    await item(messages['kind.OUTLET']).click();
    await expect(name).toHaveValue(messages['kind.OUTLET']);
    await name.fill(typeName);
    await name.press('Tab');
    await item(typeName).waitFor();
    await more('Living room');
    await page.getByRole('menuitem', { name: messages['action.newObject'], exact: true }).click();
    const dialog = page.getByRole('dialog');
    await expect(dialog.getByRole('combobox')).toContainText(typeName);
    await dialog.getByRole('button', { name: messages['action.create'], exact: true }).click();
    await page
      .locator(`[data-testid="${typeName}"]`)
      .filter({ has: page.locator('img[alt*="entity=Outlet"]') })
      .click();
    await expect(name).toHaveValue(typeName);
    await name.fill(equipmentName);
    await name.press('Tab');
    await item(equipmentName).waitFor();
    await page.getByTestId(messages['feature.circuit']).getByRole('combobox').click();
    await page.getByRole('option').filter({ hasText: 'Living room outlets' }).first().click();
    await expect(page.getByRole('textbox', { name: messages['validation'], exact: true })).toHaveValue(
      messages['consistent']
    );

    await page.reload();
    await expand();
    await item(equipmentName).click();
    await expect(name).toHaveValue(equipmentName);
    await expect(page.getByTestId(messages['feature.circuit']).getByRole('combobox')).toHaveText('Living room outlets');
    await expect(page.getByTestId(messages['feature.room']).getByRole('combobox')).toHaveText('Living room');

    await more('Ground floor');
    await page
      .getByRole('button', { name: `${messages['action.create']} ${messages['class.Room']}`, exact: true })
      .click();
    await item(messages['new.Room']).waitFor();
    await more(messages['new.Room']);
    await page.getByRole('menuitem', { name: french ? 'Renommer' : 'Rename', exact: true }).click();
    const rename = page.locator('input:focus');
    await expect(rename).toHaveValue(messages['new.Room']);
    await rename.fill(roomName);
    await rename.press('Enter');
    await item(roomName).waitFor();
    await more(roomName);
    const deleteLabel = french ? 'Supprimer' : 'Delete';
    await page.getByRole('menuitem', { name: deleteLabel, exact: true }).click();
    await page.getByRole('dialog').getByRole('button', { name: deleteLabel, exact: true }).click();
    await expect(item(roomName)).toHaveCount(0);

    await item('ID1').click();
    const current = page.getByRole('textbox', { name: messages['feature.ratedCurrentA'], exact: true });
    await expect(current).toBeDisabled();
    await expect(current).toHaveValue('63.0');
    const icon = page.locator('img[src$="/hed/mdi/home-outline.svg"]').first();
    await expect(icon).toBeVisible();
    expect((await request.get((await icon.getAttribute('src'))!)).ok()).toBeTruthy();
    const favicon = await page.locator('link[rel="icon"]').getAttribute('href');
    expect((await request.get(favicon!)).headers()['content-type']).toContain('image/svg+xml');
    await page.getByRole('banner').getByRole('link').first().click();
    await expect(
      page.getByRole('heading', { name: french ? 'Créer un nouveau projet' : 'Create a new project', exact: true })
    ).toBeVisible();
    expect(errors).toEqual([]);
  } finally {
    if (projectId) {
      const response = await request.post('/api/graphql', {
        data: { query: 'query($projectId:ID!){viewer{project(projectId:$projectId){name}}}', variables: { projectId } },
      });
      const result = await response.json();
      expect(result.data?.viewer?.project?.name).toBe(projectName);
      const deleted = await request.post('/api/graphql', {
        data: {
          query: 'mutation($input:DeleteProjectInput!){deleteProject(input:$input){__typename}}',
          variables: { input: { id: randomUUID(), projectId } },
        },
      });
      const payload = await deleted.json();
      expect(payload.errors).toBeUndefined();
      expect(payload.data.deleteProject.__typename).toMatch(/Success/);
    }
  }
});
