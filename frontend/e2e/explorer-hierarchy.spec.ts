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

test('organize the project, classify catalog types and create appliances only on rails', async ({ page, request }) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E explorer hierarchy ${randomUUID()}`;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  let projectId: string | undefined;
  const item = (label: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp(`^${escape(label)}$`) })
      .first();
  const branch = (label: string) => item(label).locator('xpath=ancestor::li[1]');
  const parentLabel = (label: string) => item(label).locator('xpath=ancestor::li[2]').locator('p').first();
  const more = async (label: string) => {
    await item(label).locator('../..').getByRole('button').click();
  };
  const expand = async () => {
    await more(projectName);
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await expect(item(messages['explorer.terminalTypes'])).toBeVisible();
  };
  const create = async (owner: string, concept: string) => {
    await more(owner);
    await page
      .getByRole('button', { name: `${messages['action.create']} ${messages[`class.${concept}`]}`, exact: true })
      .click();
  };
  const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
  const renameCreated = async (concept: string, value: string) => {
    await expand();
    await item(messages[`new.${concept}`]).click();
    await expect(name).toHaveValue(messages[`new.${concept}`]);
    await name.fill(value);
    await name.press('Tab');
    await expect(item(value)).toBeVisible();
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
    for (const label of [messages['class.Dwelling'], messages['explorer.gtl'], messages['class.Catalog']]) {
      await expect(parentLabel(label)).toHaveText(projectName);
    }
    await expect(parentLabel(messages['explorer.controlFunctions'])).toHaveText(messages['class.Dwelling']);
    await expect(parentLabel(messages['explorer.circuits'])).toHaveText(messages['explorer.gtl']);
    const panelTypes = branch(messages['explorer.panelTypes']);
    const terminalTypes = branch(messages['explorer.terminalTypes']);
    await expect(panelTypes.getByText(messages['kind.MCB'], { exact: true })).toBeVisible();
    await expect(panelTypes.getByText(messages['kind.ENERGY_METER'], { exact: true })).toBeVisible();
    await expect(panelTypes.getByText(messages['kind.SERVICE_BREAKER'], { exact: true })).toBeVisible();
    await expect(terminalTypes.getByText(messages['kind.OUTLET'], { exact: true })).toBeVisible();
    await expect(terminalTypes.getByText(messages['kind.MCB'], { exact: true })).toHaveCount(0);
    await more(messages['class.Dwelling']);
    await expect(
      page.getByRole('button', {
        name: `${messages['action.create']} ${messages['class.DistributionBoard']}`,
        exact: true,
      })
    ).toHaveCount(0);
    await expect(page.getByRole('menuitem', { name: /^(Rename|Renommer)$/, exact: true })).toHaveCount(0);
    await page.getByRole('banner').getByRole('heading').click();
    await create(messages['explorer.panelTypes'], 'DeviceType');
    await renameCreated('DeviceType', 'Custom panel type');
    await expect(panelTypes.getByText('Custom panel type', { exact: true })).toBeVisible();
    await expect(page.getByTestId(messages['feature.kind']).getByRole('combobox')).toHaveText(messages['kind.MCB']);
    await page.getByTestId(messages['feature.kind']).getByRole('combobox').click();
    const outlet = escape(messages['kind.OUTLET']);
    await page.getByRole('option', { name: new RegExp(`^${outlet}(?: ${outlet})?$`) }).click();
    await expect(terminalTypes.getByText('Custom panel type', { exact: true })).toBeVisible();
    await expect(panelTypes.getByText('Custom panel type', { exact: true })).toHaveCount(0);
    await create(messages['explorer.terminalTypes'], 'DeviceType');
    await renameCreated('DeviceType', 'Custom terminal type');
    await expect(terminalTypes.getByText('Custom terminal type', { exact: true })).toBeVisible();
    await create(messages['explorer.gtl'], 'ServiceConnection');
    await expand();
    await expect(parentLabel(messages['new.ServiceConnection'])).toHaveText(messages['explorer.gtl']);
    await create(messages['explorer.gtl'], 'DistributionBoard');
    await renameCreated('DistributionBoard', 'Test board');
    await expect(parentLabel('Test board')).toHaveText(messages['explorer.gtl']);
    await more('Test board');
    await expect(
      page.getByRole('button', { name: `${messages['action.create']} ${messages['kind.MCB']}`, exact: true })
    ).toHaveCount(0);
    await page
      .getByRole('button', { name: `${messages['action.create']} ${messages['class.DINRail']}`, exact: true })
      .click();
    await renameCreated('DINRail', 'Test rail');
    await more('Test rail');
    await page.getByRole('menuitem', { name: messages['action.newObject'], exact: true }).last().click();
    const dialog = page.getByRole('dialog');
    await dialog.getByRole('combobox').click();
    const mcb = escape(messages['kind.MCB']);
    await page.getByRole('option', { name: new RegExp(`^${mcb}(?: ${mcb})?$`) }).click();
    await dialog.getByRole('button', { name: messages['action.create'], exact: true }).click();
    await expect(name).toHaveValue(messages['kind.MCB']);
    await name.fill('Q9');
    await name.press('Tab');
    const appliance = `Q9 - ${messages['kind.MCB']}`;
    await expand();
    await expect(parentLabel(appliance)).toHaveText('Test rail');
    await more('Test rail');
    await expect(page.getByRole('menuitem', { name: /^(Delete|Supprimer)$/, exact: true })).toHaveCount(0);
    await page.getByRole('banner').getByRole('heading').click();
    await page.reload();
    await item(projectName).waitFor();
    await expand();
    await expect(parentLabel(appliance)).toHaveText('Test rail');
    await expect(terminalTypes.getByText('Custom panel type', { exact: true })).toBeVisible();
    await expect(terminalTypes.getByText('Custom terminal type', { exact: true })).toBeVisible();
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
