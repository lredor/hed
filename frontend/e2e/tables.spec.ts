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
import { showTableColumn } from './table';
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
    .map((line) => [line.slice(0, line.indexOf('=')), line.slice(line.indexOf('=') + 1)])
);
const escape = (text: string) => text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

test('create all four tables, filter and edit values, and reopen persisted tables', async ({ page, request }) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E tables ${randomUUID()}`;
  const equipmentName = `Luminaire de revue ${randomUUID()}`;
  const typeName = `Type de revue ${randomUUID()}`;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  let projectId: string | undefined;
  const item = (name: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp(`^${escape(name)}$`) })
      .first();
  const expand = async () => {
    await item(projectName).waitFor();
    await item(projectName).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await item(sampleName('bedroomLight')).waitFor();
    await expect(item(messages['class.Dwelling'])).toBeVisible();
  };
  const openTable = async (element: string, table: string) => {
    if (!(await item(element).isVisible())) {
      await expand();
    }
    await item(element).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.newRepresentation'], exact: true }).last().click();
    const dialog = page.getByRole('dialog');
    await expect(dialog).toBeVisible();
    const title = messages[`table.${table}`];
    if (await dialog.getByRole('combobox').count()) {
      await dialog.getByRole('combobox').click();
      await page.getByRole('option', { name: new RegExp(`^${escape(title)}(?: ${escape(title)})?$`) }).click();
    }
    await expect(dialog.getByRole('textbox').first()).toHaveValue(title);
    await dialog.getByRole('button', { name: french ? 'Créer' : 'Create', exact: true }).click();
    await expect(page.getByRole('table')).toBeVisible();
  };
  const search = () => page.getByRole('textbox', { name: /^(Search|Rechercher)$/ });
  const editable = (value: string) =>
    page
      .getByRole('table')
      .getByRole('cell', { name: value, exact: true })
      .filter({ has: page.getByRole('textbox') })
      .getByRole('textbox');
  try {
    await page.goto('/');
    await page.locator('a[href="/new/project?templateId=hed-example"]').click();
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\//);
    projectId = page.url().match(/\/projects\/([^/]+)\//)?.[1];
    expect(projectId).toBeTruthy();
    await expand();
    await openTable(messages['class.Dwelling'], 'equipment');
    const equipmentUrl = page.url();
    await showTableColumn(page, messages['feature.name']);
    await expect(editable(sampleName('ceilingLight'))).toBeVisible();
    const equipmentRowCount = await page.getByRole('row').count();
    await search().fill(sampleName('sofaOutlet'));
    await expect(page.getByRole('row')).toHaveCount(2);
    await search().fill('');
    await expect(page.getByRole('row')).toHaveCount(equipmentRowCount);
    const nameColumn = page.getByRole('columnheader').filter({
      has: page.locator('p').filter({ hasText: new RegExp(`^${escape(messages['feature.name'])}$`) }),
    });
    await nameColumn.getByRole('button', { name: /^(Sort by|Trier par) / }).click();
    const sortedButton = nameColumn.getByRole('button', { name: /^(Sorted by|Trié par) / });
    await expect(sortedButton).toBeVisible();
    const readNames = () =>
      page
        .getByRole('table')
        .locator('tbody tr')
        .evaluateAll((rows) =>
          rows.map((row) => row.querySelector('input')?.value).filter((value): value is string => value !== undefined)
        );
    const sortedNames = [...(await readNames())].sort((left, right) =>
      left.toLowerCase() < right.toLowerCase() ? -1 : left.toLowerCase() > right.toLowerCase() ? 1 : 0
    );
    if (/descending|décroissant/.test((await sortedButton.getAttribute('aria-label')) ?? '')) {
      sortedNames.reverse();
    }
    await expect.poll(readNames).toEqual(sortedNames);
    await editable(sampleName('ceilingLight')).fill(equipmentName);
    await page.keyboard.press('Tab');
    await expect(editable(equipmentName)).toBeVisible();
    await page.reload();
    await search().waitFor();
    await search().fill('');
    await expect(editable(equipmentName)).toBeVisible();
    await expand();
    await openTable(messages['class.Dwelling'], 'circuits');
    await page.setViewportSize({ width: 1900, height: 900 });
    const fullScreen = page.getByRole('button', { name: /^(Toggle full screen|Passer en plein écran)$/ });
    await fullScreen.click();
    await page.screenshot({ path: '../.cache/circuit-table-sample.png' });
    await fullScreen.click();
    await page.setViewportSize({ width: 1500, height: 1050 });
    await showTableColumn(page, messages['feature.name']);
    await expect(editable(sampleName('livingRoomOutlets'))).toBeVisible();
    await openTable(sampleName('mainBoard'), 'board');
    await expect(editable('ID1')).toBeVisible();
    const rccb = page
      .getByRole('row')
      .filter({ has: page.getByRole('cell', { name: 'ID1', exact: true }).filter({ has: page.getByRole('textbox') }) });
    await expect(rccb.getByRole('cell', { name: messages['kind.SERVICE_BREAKER'], exact: true })).toHaveText(
      messages['kind.SERVICE_BREAKER']
    );
    const mcb = page.getByRole('row').filter({ has: page.getByRole('cell', { name: 'Q1', exact: true }) });
    await expect(mcb.getByRole('cell', { name: 'ID1', exact: true })).toHaveText('ID1');
    await expect(rccb.getByRole('cell', { name: '63.0', exact: true })).toBeVisible();
    await expect(rccb.getByRole('cell', { name: '63.0', exact: true }).getByRole('textbox')).toHaveCount(0);
    await openTable(messages['class.Catalog'], 'catalog');
    await search().fill(messages['kind.OUTLET']);
    await expect(page.getByRole('row')).toHaveCount(2);
    await editable(messages['kind.OUTLET']).fill(typeName);
    await page.keyboard.press('Tab');
    await expect(editable(typeName)).toBeVisible();
    await page.reload();
    await search().waitFor();
    await search().fill(typeName);
    await expect(editable(typeName)).toBeVisible();
    await page.goto(equipmentUrl);
    await search().waitFor();
    await search().fill(sampleName('sofaOutlet'));
    await expect(page.getByRole('table').getByRole('cell', { name: typeName, exact: true })).toBeVisible();
    expect(errors).toEqual([]);
  } finally {
    if (projectId) {
      const result = await request.post('/api/graphql', {
        data: { query: 'query($projectId:ID!){viewer{project(projectId:$projectId){name}}}', variables: { projectId } },
      });
      expect((await result.json()).data?.viewer?.project?.name).toBe(projectName);
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
