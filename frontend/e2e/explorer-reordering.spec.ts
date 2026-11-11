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

test('reorder rails and mounted devices from Model Explorer and reopen the saved order', async ({ page, request }) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E explorer order ${randomUUID()}`;
  let projectId: string | undefined;
  const item = (name: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp(`^${name}(?: - .+)?$`) })
      .first();
  const more = async (name: string) => {
    await item(name).locator('../..').getByRole('button').click();
  };
  const expand = async () => {
    await item(projectName).waitFor();
    await more(projectName);
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await item('ID1').waitFor();
  };
  const deviceLabels = () => page.locator('img[alt*="entity=ModularDevice"]').locator('../..').locator('p');
  const railLabels = () => page.locator('img[alt*="entity=DINRail"]').locator('../..').locator('p');
  try {
    await page.goto('/');
    await page.locator('a[href="/new/project?templateId=hed-example"]').click();
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\//);
    projectId = page.url().match(/\/projects\/([^/]+)\//)?.[1];
    expect(projectId).toBeTruthy();
    await expand();
    await more('ID1');
    await expect(page.getByRole('button', { name: messages['action.moveLeft'], exact: true })).toHaveCount(0);
    await page.keyboard.press('Escape');
    await more('Q1');
    await page.getByRole('button', { name: messages['action.moveRight'], exact: true }).click();
    await expect(deviceLabels()).toHaveText(
      ['ID1', 'Q2', 'Q1', 'Q3', 'Q5', 'Q4'].map((name) => new RegExp(`^${name} - .+$`))
    );
    await item('Q1').click();
    await expect(page.getByTestId(messages['feature.upstreamRccb']).getByRole('combobox')).toContainText('ID1');
    await more(sampleName('mainBoard'));
    await page
      .getByRole('button', { name: `${messages['action.create']} ${messages['class.DINRail']}`, exact: true })
      .click();
    await expand();
    await item(messages['new.DINRail']).click();
    await page.getByRole('textbox', { name: messages['feature.name'], exact: true }).fill('Second rail');
    await page.keyboard.press('Tab');
    await expect(railLabels()).toHaveText([sampleName('row'), 'Second rail', sampleName('row')]);
    await more('Second rail');
    await expect(page.getByRole('button', { name: messages['action.moveDown'], exact: true })).toHaveCount(0);
    await page.getByRole('button', { name: messages['action.moveUp'], exact: true }).click();
    await expect(railLabels()).toHaveText(['Second rail', sampleName('row'), sampleName('row')]);
    await page.reload();
    await expand();
    await expect(deviceLabels()).toHaveText(
      ['ID1', 'Q2', 'Q1', 'Q3', 'Q5', 'Q4'].map((name) => new RegExp(`^${name} - .+$`))
    );
    await expect(railLabels()).toHaveText(['Second rail', sampleName('row'), sampleName('row')]);
    await item('Q1').click();
    await expect(page.getByTestId(messages['feature.upstreamRccb']).getByRole('combobox')).toContainText('ID1');
    await item(sampleName('groundFloorLighting')).click();
    await expect(page.getByTestId(messages['feature.protectiveDevice']).getByRole('combobox')).toContainText('Q1');
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
