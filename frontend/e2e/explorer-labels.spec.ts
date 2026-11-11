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

test('show device name and catalog type, rename only the name and preserve stored catalog names', async ({
  page,
  request,
}) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E explorer labels ${randomUUID()}`;
  const newName = 'DJ1 - Kitchen';
  const newTypeName = `Côté jardin ${randomUUID().slice(0, 8)}`;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  let projectId: string | undefined;
  const item = (name: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp(`^${escape(name)}(?: - .+)?$`) })
      .first();
  const exactDevice = (label: string) =>
    page
      .locator('img[alt*="entity=ModularDevice"]')
      .locator('../..')
      .filter({ has: page.locator('p').filter({ hasText: new RegExp(`^${escape(label)}$`) }) });
  const expand = async () => {
    await item(projectName).waitFor();
    await item(projectName).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await item(sampleName('bedroomLight')).waitFor();
  };
  const rename = async (label: string) => {
    await item(label).locator('../..').getByRole('button').click();
    await page
      .getByRole('menuitem', { name: /^(Rename|Renommer)$/, exact: true })
      .last()
      .click();
    return page.locator('input:focus');
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
    await item('Q1').click();
    const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
    await expect(name).toHaveValue('Q1');
    const type = page.getByTestId(messages['feature.deviceType']).getByRole('combobox');
    const typeName = (await type.textContent())!.trim();
    await expect(exactDevice(`Q1 - ${typeName}`)).toBeVisible();
    const editing = await rename('Q1');
    await expect(editing).toHaveValue('Q1');
    await editing.fill(newName);
    await editing.press('Enter');
    await expect(exactDevice(`${newName} - ${typeName}`)).toBeVisible();
    await item(newName).click();
    await expect(name).toHaveValue(newName);
    await expect(page.getByRole('textbox', { name: messages['feature.labelText'], exact: true })).toHaveValue(
      sampleName('groundFloorLighting')
    );
    await expect(type).toContainText(typeName);
    const catalogType = page
      .locator('img[alt*="entity=DeviceType"]')
      .locator('../..')
      .filter({ has: page.locator('p').filter({ hasText: new RegExp(`^${escape(typeName)}$`) }) });
    await catalogType.click();
    await expect(name).toHaveValue(typeName);
    await name.fill(newTypeName);
    await name.press('Tab');
    await expect(exactDevice(`${newName} - ${newTypeName}`)).toBeVisible();
    await expect(exactDevice(`Q3 - ${newTypeName}`)).toBeVisible();
    await page.reload();
    await expand();
    await expect(exactDevice(`${newName} - ${newTypeName}`)).toBeVisible();
    await item(newName).click();
    await expect(name).toHaveValue(newName);
    await expect(type).toContainText(newTypeName);
    await name.fill('');
    await name.press('Tab');
    const unnamed = exactDevice(newTypeName);
    await expect(unnamed).toBeVisible();
    await unnamed.locator('p').locator('../..').getByRole('button').click();
    await page
      .getByRole('menuitem', { name: /^(Rename|Renommer)$/, exact: true })
      .last()
      .click();
    await expect(page.locator('input:focus')).toHaveValue('');
    await page.keyboard.press('Escape');
    await expect(item(sampleName('sofaOutlet'))).toHaveText(sampleName('sofaOutlet'));
    await expect(item(messages['class.Dwelling'])).toHaveText(messages['class.Dwelling']);
    expect(errors).toEqual([]);
  } finally {
    if (projectId) {
      const response = await request.post('/api/graphql', {
        data: { query: 'query($projectId:ID!){viewer{project(projectId:$projectId){name}}}', variables: { projectId } },
      });
      expect((await response.json()).data?.viewer?.project?.name).toBe(projectName);
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
