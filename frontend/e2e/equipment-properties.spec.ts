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
const escape = (text: string) => text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

test('separate general and advanced properties for installed terminal, modular and meter devices', async ({
  page,
  request,
}) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E equipment properties ${randomUUID()}`;
  let projectId: string | undefined;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  const item = (name: string) =>
    page
      .locator('[data-treeitemid] p')
      .filter({ hasText: new RegExp(`^${escape(name)}(?: - .+)?$`) })
      .first();
  const general = () => page.getByRole('tab', { name: messages['properties.general'], exact: true });
  const advanced = () => page.getByRole('tab', { name: messages['properties.advanced'], exact: true });
  const field = (name: string) => page.getByTestId(messages[`feature.${name}`]).getByRole('combobox');
  const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
  const current = page.getByRole('textbox', { name: messages['feature.ratedCurrentA'], exact: true });
  const expand = async () => {
    await item(projectName).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await expect(item(sampleName('sofaOutlet'))).toBeVisible();
  };
  try {
    await page.goto('/');
    await page.locator('a[href="/new/project?templateId=hed-example"]').click();
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\//);
    projectId = page.url().match(/\/projects\/([^/]+)/)?.[1];
    expect(projectId).toBeTruthy();
    await item(projectName).waitFor();
    await expand();
    await item(sampleName('livingRoom')).click();
    await expect(name).toHaveValue(sampleName('livingRoom'));
    await expect(page.getByRole('textbox', { name: messages['feature.description'], exact: true })).toBeVisible();
    await expect(page.getByTestId(messages['feature.usage'])).toHaveCount(0);
    await item(sampleName('sofaOutlet')).click();
    await general().click();
    await expect(name).toHaveValue(sampleName('sofaOutlet'));
    await expect(field('deviceType')).toContainText(messages['kind.OUTLET']);
    await expect(field('room')).toContainText(sampleName('livingRoom'));
    await expect(field('circuit')).toContainText(sampleName('livingRoomOutlets'));
    await expect(current).toHaveCount(0);
    await advanced().click();
    await expect(current).toBeVisible();
    await expect(current).toBeEnabled();
    await expect(name).toHaveCount(0);
    await expect(field('room')).toHaveCount(0);
    await general().click();

    await item(sampleName('livingRoomOutlets')).click();
    await expect(field('usage')).toBeVisible();

    await item('ID1').click();
    await general().click();
    await expect(name).toHaveValue('ID1');
    await expect(field('rail')).toBeVisible();
    await expect(field('upstreamServiceBreaker')).toBeVisible();
    await expect(current).toHaveCount(0);
    await advanced().click();
    await expect(current).toHaveValue('63.0');
    await expect(current).toBeDisabled();
    await general().click();

    const meter = page.locator('img[alt*="entity=EnergyMeter"]').locator('../..').locator('p').first();
    await meter.click();
    await general().click();
    await expect(field('deviceType')).toContainText(messages['kind.ENERGY_METER']);
    await expect(current).toHaveCount(0);
    await advanced().click();
    await expect(current).toBeVisible();
    await expect(page.getByRole('textbox', { name: messages['feature.serialNumber'], exact: true })).toBeVisible();

    await page.reload();
    await expand();
    await item(sampleName('sofaOutlet')).click();
    await general().click();
    await expect(name).toHaveValue(sampleName('sofaOutlet'));
    await expect(field('room')).toContainText(sampleName('livingRoom'));
    expect(errors).toEqual([]);
  } finally {
    if (projectId) {
      const deletion = await request.post('/api/graphql', {
        data: {
          query: 'mutation($input:DeleteProjectInput!){deleteProject(input:$input){__typename}}',
          variables: { input: { id: randomUUID(), projectId } },
        },
      });
      const result = await deletion.json();
      expect(result.errors).toBeUndefined();
      expect(result.data.deleteProject.__typename).toMatch(/Success/);
    }
  }
});
