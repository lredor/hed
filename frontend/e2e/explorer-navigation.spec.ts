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
const escape = (value: string) => value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

test('navigate from Details, the palette and branch occurrences to catalog types', async ({ page, request }) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E navigation ${randomUUID()}`;
  const label = french ? 'Naviguer vers le type d’équipement' : 'Navigate to device type';
  let projectId: string | undefined;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  const item = (name: string) =>
    page.locator('[data-treeitemid] p').filter({ hasText: new RegExp(`^${escape(name)}(?: - .+)?$`) });
  const expand = async () => {
    await item(projectName).first().locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await expect(item(sampleName('sofaOutlet')).first()).toBeVisible();
  };
  const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
  try {
    await page.goto('/new/project?templateId=hed-example');
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\//);
    projectId = page.url().match(/\/projects\/([^/]+)/)?.[1];
    expect(projectId).toBeTruthy();
    await expand();
    await item(sampleName('sofaOutlet')).first().click();
    await page.getByRole('tab', { name: messages['properties.general'], exact: true }).click();
    await page.getByTestId(`${messages['class.Catalog']}-toggle`).click();
    await page.getByRole('button', { name: label, exact: true }).click();
    await expect(name).toHaveValue(messages['kind.OUTLET']);
    await expect(item(messages['kind.OUTLET']).first()).toBeVisible();
    const outletTypeId = new URL(page.url()).searchParams.get('selection');
    expect(outletTypeId).toBeTruthy();

    await expand();
    await item('Q1').first().click();
    await page.getByRole('tab', { name: messages['properties.general'], exact: true }).click();
    const breakerTypeName = await page.getByTestId(messages['feature.deviceType']).getByRole('combobox').textContent();
    await item('Q1').first().locator('../..').getByRole('button').click();
    await page.getByRole('navigation').getByRole('button', { name: label, exact: true }).click();
    await expect(name).toHaveValue(breakerTypeName!);
    await expect(item(breakerTypeName!).first()).toBeVisible();

    await expand();
    await item(sampleName('livingRoomOutlets')).first().locator('../..').getByRole('button').click();
    await page
      .getByRole('button', { name: `${messages['action.create']} ${messages['class.CircuitBranch']}`, exact: true })
      .click();
    await expand();
    await expect(item(sampleName('sofaOutlet'))).toHaveCount(2);
    await item(sampleName('sofaOutlet')).last().locator('../..').getByRole('button').click();
    await page.getByRole('navigation').getByRole('button', { name: label, exact: true }).click();
    await expect(name).toHaveValue(messages['kind.OUTLET']);
    await expect.poll(() => new URL(page.url()).searchParams.get('selection')).toBe(outletTypeId);

    await page.reload();
    await expand();
    await item(messages['kind.OUTLET']).first().click();
    await expect(name).toHaveValue(messages['kind.OUTLET']);
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
