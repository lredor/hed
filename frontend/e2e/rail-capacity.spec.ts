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

const escape = (text: string) => text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

test('keep New object available for a compact catalog variant and hide creation only when the rail is full', async ({
  page,
  request,
}) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E rail capacity ${randomUUID()}`;
  const compactType = 'Compact MCB';
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  let projectId: string | undefined;
  const item = (label: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp(`^${escape(label)}$`) })
      .first();
  const more = async (label: string) => {
    await item(label).locator('../..').getByRole('button').click();
  };
  const expand = async () => {
    await more(projectName);
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await item(messages['explorer.panelTypes']).waitFor();
  };
  const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
  const create = async (owner: string, concept: string, label: string) => {
    await more(owner);
    await page
      .getByRole('button', { name: `${messages['action.create']} ${messages[`class.${concept}`]}`, exact: true })
      .click();
    await expand();
    await item(messages[`new.${concept}`]).click();
    await expect(name).toHaveValue(messages[`new.${concept}`]);
    await name.fill(label);
    await name.press('Tab');
    await expect(item(label)).toBeVisible();
  };
  const devices = page.locator('img[alt*="entity=ModularDevice"]');
  const dropCompactType = async () => {
    const response = page.waitForResponse(
      (response) =>
        response.url().endsWith('/api/graphql') && response.request().postDataJSON()?.operationName === 'dropTreeItem'
    );
    await item(compactType).dragTo(item('Capacity rail'));
    const payload = await (await response).json();
    expect(payload.errors).toBeUndefined();
    return payload.data.dropTreeItem;
  };
  try {
    await page.goto('/');
    await page.locator('a[href="/new/project?templateId=hed-empty"]').click();
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\//);
    projectId = page.url().match(/\/projects\/([^/]+)\//)?.[1];
    expect(projectId).toBeTruthy();
    await item(projectName).waitFor();
    await expand();
    await create(messages['explorer.gtl'], 'DistributionBoard', 'Capacity board');
    await create('Capacity board', 'DINRail', 'Capacity rail');
    const capacity = page.getByRole('textbox', { name: messages['feature.capacityModules'], exact: true });
    await capacity.fill('1');
    await capacity.press('Tab');
    await create(messages['explorer.panelTypes'], 'DeviceType', compactType);
    const width = page.getByRole('textbox', { name: messages['feature.widthModules'], exact: true });
    await width.fill('0.5');
    await width.press('Tab');
    const firstDrop = await dropCompactType();
    expect(firstDrop.__typename).toMatch(/Success/);
    await expand();
    await expect(devices).toHaveCount(1);
    await more('Capacity rail');
    // Generic one-module types do not fit, but the half-module catalog variant still does.
    await expect(
      page.getByRole('button', { name: `${messages['action.create']} ${messages['kind.MCB']}`, exact: true })
    ).toHaveCount(0);
    await expect(page.getByRole('menuitem', { name: messages['action.newObject'], exact: true })).toBeVisible();
    await page.getByRole('menuitem', { name: messages['action.newObject'], exact: true }).click();
    const dialog = page.getByRole('dialog');
    await expect(dialog.getByRole('combobox')).toHaveText(compactType);
    await dialog.getByRole('combobox').click();
    const compact = escape(compactType);
    await expect(page.getByRole('option', { name: new RegExp(`^${compact}(?: ${compact})?$`) })).toBeVisible();
    const defaultType = escape(messages['kind.MCB']);
    await expect(page.getByRole('option', { name: new RegExp(`^${defaultType}(?: ${defaultType})?$`) })).toHaveCount(0);
    await page.getByRole('option', { name: new RegExp(`^${compact}(?: ${compact})?$`) }).click();
    await dialog.getByRole('button', { name: messages['action.create'], exact: true }).click();
    await expand();
    await expect(devices).toHaveCount(2);
    await more('Capacity rail');
    await expect(page.getByRole('menuitem', { name: messages['action.newObject'], exact: true })).toHaveCount(0);
    await expect(
      page.getByRole('button', { name: `${messages['action.create']} ${messages['kind.MCB']}`, exact: true })
    ).toHaveCount(0);
    await page.getByRole('banner').getByRole('heading').click();
    const rejected = await dropCompactType();
    expect(rejected.messages).toContainEqual(expect.objectContaining({ body: messages['railCapacity'] }));
    await expect(devices).toHaveCount(2);
    await page.reload();
    await item(projectName).waitFor();
    await expand();
    await expect(devices).toHaveCount(2);
    await item('Capacity rail').click();
    await expect(capacity).toHaveValue('1');
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
