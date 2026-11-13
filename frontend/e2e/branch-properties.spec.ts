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

test('explain branchless circuits and update the selector when branches are created or deleted', async ({
  page,
  request,
}) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E branch properties ${randomUUID()}`;
  let projectId: string | undefined;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  const item = (name: string) =>
    page
      .locator('[data-treeitemid] p')
      .filter({ hasText: new RegExp(`^${escape(name)}(?: - .+)?$`) })
      .first();
  const general = () => page.getByRole('tab', { name: messages['properties.general'], exact: true });
  const field = (name: string) => page.getByTestId(messages[`feature.${name}`]).getByRole('combobox');
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
    await item(sampleName('sofaOutlet')).click();
    await general().click();
    await expect(field('branch')).toBeDisabled();
    await expect(field('branch')).toHaveText(messages['circuitWithoutBranches']);

    await item(sampleName('livingRoomOutlets')).click();
    await item(sampleName('livingRoomOutlets')).locator('../..').getByRole('button').click();
    await page
      .getByRole('button', { name: `${messages['action.create']} ${messages['class.CircuitBranch']}`, exact: true })
      .click();
    await expand();
    await item(sampleName('sofaOutlet')).click();
    await expect(field('branch')).toBeEnabled();
    await expect(field('branch')).toHaveText(messages['new.CircuitBranch']);
    await item(messages['new.CircuitBranch']).locator('../..').getByRole('button').click();
    const deleteLabel = french ? 'Supprimer' : 'Delete';
    await page.getByRole('menuitem', { name: deleteLabel, exact: true }).last().click();
    await page.getByRole('dialog').getByRole('button', { name: deleteLabel, exact: true }).click();
    await item(sampleName('sofaOutlet')).click();
    await expect(field('branch')).toBeDisabled();
    await expect(field('branch')).toHaveText(messages['circuitWithoutBranches']);

    await page.reload();
    await expand();
    await item(sampleName('sofaOutlet')).click();
    await general().click();
    await expect(field('branch')).toBeDisabled();
    await expect(field('branch')).toHaveText(messages['circuitWithoutBranches']);
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
