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
import { randomUUID } from 'node:crypto';
import { readFileSync } from 'node:fs';
import { sampleName } from './sample';

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

test('room creation tools track catalog category ordering and retain the matching action', async ({
  page,
  request,
}) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = 'E2E creation order ' + randomUUID();
  let projectId: string | undefined;
  const item = (label: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp('^' + label.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + '$') })
      .first();
  const expand = async () => {
    await item(projectName).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
  };
  try {
    await page.goto('/');
    await page.locator('a[href="/new/project?templateId=hed-example"]').click();
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\//);
    projectId = page.url().match(/\/projects\/([^/]+)\//)?.[1];
    await expand();
    const type = item(messages['kind.SWITCH']);
    // Initial catalog order follows the metamodel; move the switch past every preceding type.
    const catalogLabels = () =>
      item(messages['explorer.terminalTypes'])
        .locator('xpath=ancestor::li[1]')
        .locator('img[alt*="entity=DeviceType"]')
        .locator('../..')
        .locator('p');
    const position = async () => (await catalogLabels().allTextContents()).indexOf(messages['kind.SWITCH']);
    await expect(type).toBeVisible();
    await expect.poll(position).toBeGreaterThanOrEqual(0);
    for (let preceding = await position(); preceding > 0; preceding--) {
      await type.locator('../..').getByRole('button').click();
      await page.getByRole('navigation').getByRole('button', { name: messages['action.moveUp'], exact: true }).click();
      await expect.poll(position).toBe(preceding - 1);
    }
    await item(sampleName('livingRoom')).locator('../..').getByRole('button').click();
    const tools = page
      .getByRole('navigation')
      .getByRole('button')
      .filter({ hasText: new RegExp('^' + messages['action.create'] + ' ') });
    await expect(tools.nth(0)).toHaveText(messages['action.create'] + ' ' + messages['kind.SWITCH']);
    await expect(tools.nth(1)).toHaveText(messages['action.create'] + ' ' + messages['kind.OUTLET']);
    await page
      .getByRole('button', { name: messages['action.create'] + ' ' + messages['kind.SWITCH'], exact: true })
      .click();
    await expand();
    await item(sampleName('livingRoom'))
      .locator('xpath=ancestor::li[1]')
      .locator('p')
      .filter({ hasText: new RegExp('^' + messages['kind.SWITCH'] + '$') })
      .click();
    await expect(page.getByTestId(messages['feature.deviceType']).getByRole('combobox')).toHaveText(
      messages['kind.SWITCH']
    );
    // A second block category entry must remain a distinct creation action with its own grid.
    await item(messages['class.Catalog']).locator('../..').getByRole('button').click();
    await page
      .getByRole('button', { name: messages['action.create'] + ' ' + messages['class.DeviceType'], exact: true })
      .click();
    await expand();
    await item(messages['new.DeviceType']).click();
    const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
    await name.fill('Variant 2x3');
    await name.press('Tab');
    await page.getByTestId(messages['feature.kind']).getByRole('combobox').click();
    await page.getByRole('option').filter({ hasText: messages['enum.DeviceKind.OUTLET_BLOCK'] }).click();
    for (const [feature, value] of [
      ['nbRows', '2'],
      ['nbColumns', '3'],
    ]) {
      const field = page.getByRole('textbox', { name: messages['feature.' + feature], exact: true });
      await field.fill(value);
      await field.press('Tab');
    }
    await item(sampleName('livingRoom')).locator('../..').getByRole('button').click();
    await expect(
      page.getByRole('button', { name: messages['action.create'] + ' ' + messages['kind.OUTLET_BLOCK'], exact: true })
    ).toBeVisible();
    await page.getByRole('button', { name: messages['action.create'] + ' Variant 2x3', exact: true }).click();
    await expand();
    const variant = item(sampleName('livingRoom'))
      .locator('xpath=ancestor::li[1]')
      .locator('p')
      .filter({ hasText: /^Variant 2x3$/ });
    await variant.click();
    await expect(page.getByTestId(messages['feature.deviceType']).getByRole('combobox')).toHaveText('Variant 2x3');
    const block = variant.locator('xpath=ancestor::li[1]');
    await variant.locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await expect(block.locator('img[alt*="entity=OutletRow"]')).toHaveCount(2);
    await expect(block.locator('img[alt*="entity=Outlet&"], img[alt$="entity=Outlet"]')).toHaveCount(6);
  } finally {
    if (projectId) {
      const response = await request.post('/api/graphql', {
        data: {
          query: 'mutation($input:DeleteProjectInput!){deleteProject(input:$input){__typename}}',
          variables: { input: { id: randomUUID(), projectId } },
        },
      });
      expect((await response.json()).errors).toBeUndefined();
    }
  }
});
