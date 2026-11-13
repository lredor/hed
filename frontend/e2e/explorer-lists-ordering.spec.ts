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

test('reorder catalog categories, storeys, rooms, room devices and functional controls, then reopen', async ({
  page,
  request,
}) => {
  test.setTimeout(180000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E list ordering ${randomUUID()}`;
  let projectId: string | undefined;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  const item = (name: string) =>
    page
      .locator('[data-treeitemid] p')
      .filter({ hasText: new RegExp(`^${escape(name)}$`) })
      .first();
  const more = (label: string) => item(label).locator('../..').getByRole('button').click();
  const expand = async () => {
    await more(projectName);
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await expect(item(messages['explorer.terminalTypes'])).toBeVisible();
  };
  const groups = [
    { parent: messages['class.Dwelling'], entities: ['Storey'] },
    { parent: sampleName('groundFloor'), entities: ['Room'] },
    { parent: sampleName('livingRoom'), entities: ['Outlet', 'ElectricalLoad', 'ControlDevice'] },
    { parent: messages['explorer.controlFunctions'], entities: ['ControlFunction'] },
    { parent: messages['explorer.panelTypes'], entities: ['DeviceType'] },
    { parent: messages['explorer.terminalTypes'], entities: ['DeviceType'] },
  ];
  const labels = (group: typeof groups[number]) =>
    item(group.parent)
      .locator('xpath=ancestor::li[1]')
      .locator(group.entities.map((entity) => `img[alt*="entity=${entity}"]`).join(','))
      .locator('../..')
      .locator('p');
  const expected: string[][] = [];
  const drop = async (source: string, destination: string, before: boolean) => {
    await item(source).click();
    const target = before
      ? page.getByTestId(`${destination}-fullrow`).locator('xpath=preceding-sibling::div[1]')
      : item(destination);
    const response = page.waitForResponse(
      (result) =>
        result.url().endsWith('/api/graphql') && result.request().postDataJSON()?.operationName === 'dropTreeItem'
    );
    await item(source).dragTo(target, before ? { targetPosition: { x: 8, y: 0.25 } } : {});
    const body = await (await response).json();
    expect(body.errors).toBeUndefined();
    expect(body.data.dropTreeItem.__typename).toMatch(/Success/);
  };
  try {
    await page.setViewportSize({ width: 1800, height: 2600 });
    await page.goto('/new/project?templateId=hed-example');
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\//);
    projectId = page.url().match(/\/projects\/([^/]+)/)?.[1];
    expect(projectId).toBeTruthy();
    await item(projectName).waitFor();
    await expand();
    for (const group of groups) {
      const original = await labels(group).allTextContents();
      expect(original.length).toBeGreaterThan(1);
      await more(original[0]!);
      await page
        .getByRole('navigation')
        .getByRole('button', { name: messages['action.moveDown'], exact: true })
        .click();
      const moved = [original[1]!, original[0]!, ...original.slice(2)];
      await expect(labels(group)).toHaveText(moved);
      const last = moved.at(-1)!;
      await drop(last, moved[0]!, true);
      await expect(labels(group)).toHaveText([last, ...moved.slice(0, -1)]);
      await drop(last, group.parent, false);
      await expect(labels(group)).toHaveText(moved);
      expected.push(moved);
    }
    await page.reload();
    await expand();
    for (const [index, group] of groups.entries()) await expect(labels(group)).toHaveText(expected[index]!);
    await item(sampleName('sofaOutlet')).click();
    await page.getByRole('tab', { name: messages['properties.general'], exact: true }).click();
    await expect(page.getByTestId(messages['feature.circuit']).getByRole('combobox')).toHaveText(
      sampleName('livingRoomOutlets')
    );
    await expect(page.getByTestId(messages['feature.room']).getByRole('combobox')).toHaveText(sampleName('livingRoom'));
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
