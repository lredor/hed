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

test('show circuit phases only with an explicit three-phase supply and retain hidden assignments', async ({
  page,
  request,
}) => {
  test.setTimeout(150000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E circuit phases ${randomUUID()}`;
  const finalCircuit = 'Final phase circuit';
  const distributionCircuit = 'Distribution phase circuit';
  const supply = 'Supply phase check';
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  let projectId: string | undefined;
  const item = (label: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp(`^${escape(label)}$`) })
      .first();
  const more = async (label: string) => {
    await item(label).locator('../..').getByRole('button').click();
  };
  const expand = async () => {
    await more(projectName);
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await item(messages['explorer.circuits']).waitFor();
  };
  const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
  const phase = () => page.getByTestId(messages['feature.phase']).getByRole('combobox');
  const phaseColumn = () =>
    page
      .getByRole('columnheader')
      .filter({ has: page.locator('p').filter({ hasText: new RegExp(`^${escape(messages['feature.phase'])}$`) }) });
  const select = async (label: string) => {
    await item(label).click();
    await expect(name).toHaveValue(label);
  };
  const choosePhase = async (value: string) => {
    await phase().click();
    const label = escape(value);
    await page.getByRole('option', { name: new RegExp(`^${label}(?: ${label})?$`) }).click();
    await expect(phase()).toHaveText(value);
  };
  const create = async (owner: string, concept: string, label: string) => {
    await more(owner);
    await page
      .getByRole('button', { name: `${messages['action.create']} ${messages[`class.${concept}`]}`, exact: true })
      .click();
    await expand();
    await select(messages[`new.${concept}`]);
    await name.fill(label);
    await name.press('Tab');
    await expect(item(label)).toBeVisible();
  };
  const row = (label: string) =>
    page
      .getByRole('row')
      .filter({ has: page.getByRole('cell', { name: label, exact: true }).filter({ has: page.getByRole('textbox') }) });
  try {
    await page.goto('/');
    await page.locator('a[href="/new/project?templateId=hed-empty"]').click();
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\//);
    projectId = page.url().match(/\/projects\/([^/]+)\//)?.[1];
    expect(projectId).toBeTruthy();
    await item(projectName).waitFor();
    await expand();
    await create(messages['explorer.circuits'], 'FinalCircuit', finalCircuit);
    await expect(phase()).toHaveCount(0);
    await create(messages['explorer.circuits'], 'DistributionCircuit', distributionCircuit);
    await expect(phase()).toHaveCount(0);
    await more(messages['class.Dwelling']);
    await page.getByRole('menuitem', { name: messages['action.newRepresentation'], exact: true }).last().click();
    const dialog = page.getByRole('dialog');
    await dialog.getByRole('combobox').click();
    const title = escape(messages['table.circuits']);
    await page.getByRole('option', { name: new RegExp(`^${title}(?: ${title})?$`) }).click();
    await dialog.getByRole('textbox').first().fill('Circuit phase visibility');
    await dialog.getByRole('button', { name: french ? 'Créer' : 'Create', exact: true }).click();
    await expect(page.getByRole('table')).toBeVisible();
    const representationPath = new URL(page.url()).pathname;
    await expect(phaseColumn()).toHaveCount(0);
    await expand();
    await create(messages['explorer.gtl'], 'ServiceConnection', supply);
    await select(supply);
    await expect(phase()).toHaveText(messages['enum.SupplyPhase.UNKNOWN']);
    await expect(phaseColumn()).toHaveCount(0);
    await choosePhase(messages['enum.SupplyPhase.THREE_PHASE']);
    await expect(phaseColumn()).toBeVisible();
    await select(finalCircuit);
    await choosePhase('L2');
    await expect(row(finalCircuit).getByRole('cell', { name: 'L2', exact: true })).toBeVisible();
    await select(distributionCircuit);
    await choosePhase('L3');
    await expect(row(distributionCircuit).getByRole('cell', { name: 'L3', exact: true })).toBeVisible();
    // Hidden controls and columns must preserve the assignments entered for each circuit kind.
    await select(supply);
    await choosePhase(messages['enum.SupplyPhase.SINGLE_PHASE']);
    await expect(phaseColumn()).toHaveCount(0);
    await select(finalCircuit);
    await expect(phase()).toHaveCount(0);
    await select(distributionCircuit);
    await expect(phase()).toHaveCount(0);
    await select(supply);
    await choosePhase(messages['enum.SupplyPhase.UNKNOWN']);
    await expect(phaseColumn()).toHaveCount(0);
    await select(finalCircuit);
    await expect(phase()).toHaveCount(0);
    await page.reload();
    await expect(page.getByRole('table')).toBeVisible();
    await expect(phaseColumn()).toHaveCount(0);
    await expand();
    await select(supply);
    await expect(phase()).toHaveText(messages['enum.SupplyPhase.UNKNOWN']);
    await choosePhase(messages['enum.SupplyPhase.THREE_PHASE']);
    await expect(phaseColumn()).toBeVisible();
    await select(finalCircuit);
    await expect(phase()).toHaveText('L2');
    await select(distributionCircuit);
    await expect(phase()).toHaveText('L3');
    await expect(row(finalCircuit).getByRole('cell', { name: 'L2', exact: true })).toBeVisible();
    await expect(row(distributionCircuit).getByRole('cell', { name: 'L3', exact: true })).toBeVisible();
    expect(new URL(page.url()).pathname).toBe(representationPath);
    await page.reload();
    await expect(phaseColumn()).toBeVisible();
    await expect(row(finalCircuit).getByRole('cell', { name: 'L2', exact: true })).toBeVisible();
    await expect(row(distributionCircuit).getByRole('cell', { name: 'L3', exact: true })).toBeVisible();
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
      const payload = await deletion.json();
      expect(payload.errors).toBeUndefined();
      expect(payload.data.deleteProject.__typename).toMatch(/Success/);
    }
  }
});
