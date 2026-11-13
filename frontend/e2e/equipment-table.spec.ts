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
import { showTableColumn } from './table';

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

test('scope equipment inventories to dwellings, storeys and rooms with composite relationships', async ({
  page,
  request,
}) => {
  test.setTimeout(150000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E equipment inventory ${randomUUID()}`;
  let projectId: string | undefined;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  const item = (label: string) =>
    page
      .locator('[data-treeitemid] p')
      .filter({ hasText: new RegExp('^' + escape(label) + '$') })
      .first();
  const expand = async () => {
    await item(projectName).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await expect(item(sampleName('sofaOutlet'))).toBeVisible();
  };
  const openInventory = async (owner: string, label: string) => {
    await expand();
    await item(owner).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.newRepresentation'], exact: true }).last().click();
    const dialog = page.getByRole('dialog');
    if (await dialog.getByRole('combobox').count()) {
      await dialog.getByRole('combobox').click();
      await page
        .getByRole('option')
        .filter({ hasText: new RegExp('^' + escape(messages['table.equipment']) + '$') })
        .click();
    }
    await dialog.getByRole('textbox').first().fill(label);
    await dialog.getByRole('button', { name: french ? 'Créer' : 'Create', exact: true }).click();
    await expect(page.getByRole('table')).toBeVisible();
    await expect(page.getByRole('columnheader').getByText(messages['feature.name'], { exact: true })).toHaveCount(0);
  };
  const row = (label: string) => page.getByRole('row').filter({ has: page.getByText(label, { exact: true }) });
  const rows = () => page.locator('tbody tr');
  try {
    await page.goto('/');
    await page.locator('a[href="/new/project?templateId=hed-example"]').click();
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\/[^/]+\/edit/);
    projectId = page.url().match(/\/projects\/([^/]+)/)?.[1];
    await expand();
    await item(sampleName('livingRoomOutlets')).locator('../..').getByRole('button').click();
    await page
      .getByRole('button', { name: `${messages['action.create']} ${messages['class.CircuitBranch']}`, exact: true })
      .click();
    await expand();
    await item(messages['new.CircuitBranch']).click();
    const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
    await name.fill('Window side');
    await name.press('Tab');
    await expect(item('Window side')).toBeVisible();
    await openInventory(messages['class.Dwelling'], 'Whole dwelling inventory');
    const dwellingUrl = page.url();
    await expect(rows()).toHaveCount(7);
    const headings = await page.locator('thead th p').allTextContents();
    expect(headings.filter(Boolean)).toEqual(
      [
        'table.column.equipment.deviceType',
        'table.column.equipment.circuit',
        'table.column.equipment.room',
        'feature.controls',
        'feature.issues',
      ].map((key) => messages[key])
    );
    const assignment = sampleName('livingRoomOutlets') + ' / Window side';
    const location = sampleName('groundFloor') + ' - ' + sampleName('livingRoom');
    await expect(row(sampleName('sofaOutlet')).getByRole('cell', { name: assignment, exact: true })).toBeVisible();
    await expect(row(sampleName('sofaOutlet')).getByRole('cell', { name: location, exact: true })).toBeVisible();
    await expect(
      row(sampleName('ceilingLight')).getByRole('cell', { name: sampleName('livingRoomLighting'), exact: true })
    ).toBeVisible();
    const search = page.getByRole('textbox', { name: /^(Search|Rechercher)$/ });
    await search.fill(assignment);
    await expect(rows()).toHaveCount(1);
    await search.fill(location);
    await expect(rows()).toHaveCount(6);
    await search.fill('');
    await expect(rows()).toHaveCount(7);
    await page.setViewportSize({ width: 1900, height: 900 });
    const fullScreen = page.getByRole('button', { name: /^(Toggle full screen|Passer en plein écran)$/ });
    await fullScreen.click();
    await page.screenshot({ path: '../.cache/equipment-table-sample.png' });
    await fullScreen.click();
    await page.setViewportSize({ width: 1500, height: 1050 });
    await openInventory(sampleName('groundFloor'), 'Ground inventory');
    await expect(rows()).toHaveCount(6);
    await openInventory(sampleName('livingRoom'), 'Living inventory');
    const roomUrl = page.url();
    await expect(rows()).toHaveCount(6);
    await showTableColumn(page, messages['feature.name']);
    await expect(row(sampleName('sofaOutlet')).getByRole('textbox')).toHaveValue(sampleName('sofaOutlet'));
    await page.reload();
    await expect(rows()).toHaveCount(6);
    await expect(row(sampleName('sofaOutlet')).getByRole('textbox')).toHaveValue(sampleName('sofaOutlet'));
    await expand();
    await item(sampleName('sofaOutlet')).click();
    const room = page.getByTestId(messages['feature.room']).getByRole('combobox');
    await room.click();
    await page
      .getByRole('option')
      .filter({ hasText: new RegExp('^' + escape(sampleName('bedroom')) + '$') })
      .click();
    await expect(rows()).toHaveCount(5);
    await expect(row(sampleName('sofaOutlet'))).toHaveCount(0);
    await openInventory(sampleName('firstFloor'), 'First floor inventory');
    await expect(rows()).toHaveCount(2);
    await expect(
      row(sampleName('sofaOutlet')).getByRole('cell', {
        name: sampleName('firstFloor') + ' - ' + sampleName('bedroom'),
        exact: true,
      })
    ).toBeVisible();
    await page.goto(dwellingUrl);
    await search.waitFor();
    await search.fill('');
    await expect(rows()).toHaveCount(7);
    await page.goto(roomUrl);
    await expect(rows()).toHaveCount(5);
    expect(errors).toEqual([]);
  } finally {
    if (projectId) {
      const response = await request.post('/api/graphql', {
        data: {
          query: 'mutation($input:DeleteProjectInput!){deleteProject(input:$input){__typename}}',
          variables: { input: { id: randomUUID(), projectId } },
        },
      });
      expect((await response.json()).data.deleteProject.__typename).toMatch(/Success/);
    }
  }
});
