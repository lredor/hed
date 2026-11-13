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
import { sampleName, samplePattern } from './sample';
import { readFileSync } from 'node:fs';
import { randomUUID } from 'node:crypto';

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
    .map((line) => {
      const separator = line.indexOf('=');
      return [line.slice(0, separator), line.slice(separator + 1)];
    })
);
const escape = (text: string) => text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

test('edit direct circuit equipment or display the read-only union of optional branches', async ({ page, request }) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E optional branches ${randomUUID()}`;
  const circuit = sampleName('livingRoomOutlets');
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  let projectId: string | undefined;
  const item = (label: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp(`^${escape(label)}$`) })
      .first();
  const equipment = () => page.getByTestId(messages['feature.equipment']).getByRole('combobox');
  const expand = async () => {
    await item(projectName).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await expect(item(circuit)).toBeVisible();
  };
  const toggleEquipment = async (label: string) => {
    await equipment().click();
    await page
      .getByRole('option')
      .filter({ hasText: new RegExp(`^${escape(label)}$`) })
      .click();
    await page.keyboard.press('Escape');
  };
  const createBranch = async (label: string) => {
    await item(circuit).locator('../..').getByRole('button').click();
    await page
      .getByRole('button', { name: `${messages['action.create']} ${messages['class.CircuitBranch']}`, exact: true })
      .click();
    await expand();
    await item(messages['new.CircuitBranch']).click();
    const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
    await name.fill(label);
    await name.press('Tab');
    await expect(item(label)).toBeVisible();
  };
  const deleteBranch = async (label: string) => {
    const deletion = french ? 'Supprimer' : 'Delete';
    await item(label).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: deletion, exact: true }).last().click();
    await page.getByRole('dialog').getByRole('button', { name: deletion, exact: true }).click();
    await expect(item(label)).toHaveCount(0);
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
    await item(circuit).click();
    await expect(equipment()).toBeEnabled();
    await expect(equipment()).toHaveText(sampleName('sofaOutlet'));
    await toggleEquipment(sampleName('ceilingLight'));
    await expect(equipment()).toContainText(sampleName('ceilingLight'));
    await expect(equipment()).toContainText(sampleName('sofaOutlet'));
    await page.reload();
    await expand();
    await item(circuit).click();
    await expect(equipment()).toBeEnabled();
    await expect(equipment()).toContainText(sampleName('ceilingLight'));
    await createBranch('Window side');
    await expect(equipment()).toBeEnabled();
    await expect(equipment()).toContainText(sampleName('ceilingLight'));
    await expect(equipment()).toContainText(sampleName('sofaOutlet'));
    await item(circuit).click();
    await expect(equipment()).toBeDisabled();
    await expect(equipment()).toContainText(sampleName('ceilingLight'));
    await expect(equipment()).toContainText(sampleName('sofaOutlet'));
    await createBranch('Door side');
    await toggleEquipment(sampleName('sofaOutlet'));
    await expect(equipment()).toHaveText(sampleName('sofaOutlet'));
    await item(circuit).click();
    await expect(equipment()).toBeDisabled();
    await expect(equipment()).toContainText(sampleName('ceilingLight'));
    await expect(equipment()).toContainText(sampleName('sofaOutlet'));
    expect(((await equipment().textContent()) ?? '').match(samplePattern('sofaOutlet', false, 'g'))).toHaveLength(1);
    await item(sampleName('bedroomLight')).click();
    const supply = page.getByTestId(messages['feature.circuit']).getByRole('combobox');
    await supply.click();
    await expect(page.getByRole('option').filter({ hasText: new RegExp(`^${escape(circuit)}$`) })).toHaveCount(0);
    await page.keyboard.press('Escape');
    await expect(supply).toHaveText(sampleName('bedroomLighting'));
    const branch = page.getByTestId(messages['feature.branch']).getByRole('combobox');
    await expect(branch).toBeDisabled();
    await expect(branch).toHaveText(messages['circuitWithoutBranches']);
    await supply.click();
    await page.getByRole('option', { name: messages['unassigned'], exact: true }).click();
    await expect(branch).toBeEnabled();
    await page.getByTestId(messages['feature.branch']).getByRole('combobox').click();
    await page
      .getByRole('option')
      .filter({ hasText: /^Window side$/ })
      .click();
    await expect(supply).toHaveText(circuit);
    await page.getByTestId(messages['feature.branch']).getByRole('combobox').click();
    await page
      .getByRole('option')
      .filter({ hasText: new RegExp(`^${escape(messages['unassigned'])}$`) })
      .click();
    await expect(supply).toHaveText(messages['unassigned']);
    await page.reload();
    await expand();
    await item(circuit).click();
    await expect(equipment()).toBeDisabled();
    await expect(equipment()).toContainText(sampleName('ceilingLight'));
    await deleteBranch('Window side');
    await item(circuit).click();
    await expect(equipment()).toBeDisabled();
    await expect(equipment()).toHaveText(sampleName('sofaOutlet'));
    await deleteBranch('Door side');
    await item(circuit).click();
    await expect(equipment()).toBeEnabled();
    await expect(equipment()).toHaveText(sampleName('sofaOutlet'));
    await toggleEquipment(sampleName('sofaOutlet'));
    await expect(equipment()).not.toContainText(sampleName('sofaOutlet'));
    await item(sampleName('sofaOutlet')).click();
    await expect(page.getByTestId(messages['feature.circuit']).getByRole('combobox')).toHaveText(
      messages['unassigned']
    );
    await expect(page.getByTestId(messages['feature.room']).getByRole('combobox')).toHaveText(sampleName('livingRoom'));
    await item(sampleName('ceilingLight')).click();
    await expect(page.getByTestId(messages['feature.circuit']).getByRole('combobox')).toHaveText(
      messages['unassigned']
    );
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
      const result = await deletion.json();
      expect(result.errors).toBeUndefined();
      expect(result.data.deleteProject.__typename).toMatch(/Success/);
    }
  }
});
