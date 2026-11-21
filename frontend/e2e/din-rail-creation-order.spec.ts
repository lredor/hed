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

test('DIN rail tools follow catalog order and create the exact modular variant', async ({ page, request }) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = 'E2E rail creation order ' + randomUUID();
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
    const type = item(messages['kind.MCB']);
    // Initial catalog order follows the metamodel; move the MCB past every preceding type.
    const catalogLabels = () =>
      item(messages['explorer.panelTypes'])
        .locator('xpath=ancestor::li[1]')
        .locator('img[alt*="entity=DeviceType"]')
        .locator('../..')
        .locator('p');
    const position = async () => (await catalogLabels().allTextContents()).indexOf(messages['kind.MCB']);
    await expect(type).toBeVisible();
    await expect.poll(position).toBeGreaterThanOrEqual(0);
    for (let preceding = await position(); preceding > 0; preceding--) {
      await type.locator('../..').getByRole('button').click();
      await page.getByRole('navigation').getByRole('button', { name: messages['action.moveUp'], exact: true }).click();
      await expect.poll(position).toBe(preceding - 1);
    }
    await item(sampleName('row')).locator('../..').getByRole('button').click();
    const tools = page
      .getByRole('navigation')
      .getByRole('button')
      .filter({ hasText: new RegExp('^' + messages['action.create'] + ' ') });
    await expect(tools.nth(0)).toHaveText(messages['action.create'] + ' ' + messages['kind.MCB']);
    await expect(tools.nth(1)).toHaveText(messages['action.create'] + ' ' + messages['kind.RCCB']);
    await page
      .getByRole('button', { name: messages['action.create'] + ' ' + messages['kind.MCB'], exact: true })
      .click();
    // Each MCB catalog variant must have its own native action, in the current catalog order.
    await item(messages['class.Catalog']).locator('../..').getByRole('button').click();
    await page
      .getByRole('button', { name: messages['action.create'] + ' ' + messages['class.DeviceType'], exact: true })
      .click();
    await expand();
    await item(messages['new.DeviceType']).click();
    const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
    await name.fill('Compact MCB');
    await name.press('Tab');
    await page.getByTestId(messages['feature.kind']).getByRole('combobox').click();
    await page.getByRole('option').filter({ hasText: messages['enum.DeviceKind.MCB'] }).click();
    const width = page.getByRole('textbox', { name: messages['feature.widthModules'], exact: true });
    await width.fill('0.5');
    await width.press('Tab');
    await expect(width).toHaveValue('0.5');
    await item(sampleName('row')).locator('../..').getByRole('button').click();
    await expect(
      page.getByRole('button', { name: messages['action.create'] + ' ' + messages['kind.MCB'], exact: true })
    ).toBeVisible();
    await page.getByRole('button', { name: messages['action.create'] + ' Compact MCB', exact: true }).click();
    await expand();
    const variant = item(sampleName('row'))
      .locator('xpath=ancestor::li[1]')
      .locator('p')
      .filter({ hasText: /^Compact MCB - Compact MCB$/ });
    await variant.click();
    await expect(page.getByTestId(messages['feature.deviceType']).getByRole('combobox')).toHaveText('Compact MCB');
    // Moving the variant to the top must immediately move its action ahead of the other modular entries.
    const variantType = item('Compact MCB');
    const variantPosition = async () => (await catalogLabels().allTextContents()).indexOf('Compact MCB');
    await expect.poll(variantPosition).toBeGreaterThan(0);
    for (let preceding = await variantPosition(); preceding > 0; preceding--) {
      await variantType.locator('../..').getByRole('button').click();
      await page.getByRole('navigation').getByRole('button', { name: messages['action.moveUp'], exact: true }).click();
      await expect.poll(variantPosition).toBe(preceding - 1);
    }
    await item(sampleName('row')).locator('../..').getByRole('button').click();
    await expect(tools.nth(0)).toHaveText(messages['action.create'] + ' Compact MCB');
    await expect(tools.nth(1)).toHaveText(messages['action.create'] + ' ' + messages['kind.MCB']);
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
