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

test('navigate to live supply circuits from Details, explorer occurrences and branches', async ({ page, request }) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E circuit navigation ${randomUUID()}`;
  const label = french ? 'Naviguer vers le circuit d’alimentation' : 'Navigate to supply circuit';
  const typeLabel = french ? 'Naviguer vers le type d’équipement' : 'Navigate to device type';
  const item = (name: string) =>
    page.locator('[data-treeitemid] p').filter({ hasText: new RegExp(`^${escape(name)}(?: - .+)?$`) });
  const expand = async () => {
    await item(projectName).first().locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await expect(item(sampleName('sofaOutlet')).first()).toBeVisible();
  };
  const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
  const supply = () => page.getByTestId(messages['feature.circuit']).getByRole('combobox');
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  let projectId: string | undefined;
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
    await page.getByTestId(`${messages['explorer.gtl']}-toggle`).click();
    await page.getByRole('button', { name: label, exact: true }).click();
    await expect(name).toHaveValue(sampleName('livingRoomOutlets'));
    await expect(item(sampleName('livingRoomOutlets')).first()).toBeVisible();
    const circuitId = new URL(page.url()).searchParams.get('selection');
    await expand();
    await item(sampleName('sofaOutlet')).first().locator('../..').getByRole('button').click();
    const palette = page.getByRole('navigation');
    for (const toolLabel of [label, typeLabel]) {
      const bounds = await palette.getByTestId(`tool-${toolLabel}`).locator('.MuiListItemText-root').boundingBox();
      const rename = await palette
        .getByRole('menuitem', { name: french ? 'Renommer' : 'Rename', exact: true })
        .locator('.MuiListItemText-root')
        .boundingBox();
      expect(Math.abs(bounds!.x - rename!.x)).toBeLessThan(1);
    }
    if (french) await palette.screenshot({ path: '../.cache/circuit-navigation/navigation-menu-fr.png' });
    await palette.getByTestId(`tool-${label}`).click();
    await expect(name).toHaveValue(sampleName('livingRoomOutlets'));
    await expect.poll(() => new URL(page.url()).searchParams.get('selection')).toBe(circuitId);
    await item(sampleName('livingRoomOutlets')).first().locator('../..').getByRole('button').click();
    await page
      .getByRole('button', { name: `${messages['action.create']} ${messages['class.CircuitBranch']}`, exact: true })
      .click();
    await expand();
    await item(messages['new.CircuitBranch']).first().click();
    await page.getByRole('button', { name: label, exact: true }).click();
    await expect(name).toHaveValue(sampleName('livingRoomOutlets'));
    await expand();
    await expect(item(sampleName('sofaOutlet'))).toHaveCount(2);
    await item(sampleName('sofaOutlet')).last().locator('../..').getByRole('button').click();
    await page.getByRole('navigation').getByTestId(`tool-${label}`).click();
    await expect(name).toHaveValue(sampleName('livingRoomOutlets'));
    await expand();
    await item(sampleName('sofaOutlet')).first().click();
    await supply().click();
    await page.getByRole('option', { name: messages['unassigned'], exact: true }).click();
    await expect(page.getByRole('button', { name: label, exact: true })).toBeDisabled();
    const lookup = page.waitForResponse(
      (response) =>
        response.url().endsWith('/api/graphql') &&
        response.request().postDataJSON()?.operationName === 'HedSupplyCircuit'
    );
    await item(sampleName('sofaOutlet')).first().locator('../..').getByRole('button').click();
    expect((await (await lookup).json()).data.viewer.editingContext.hedSupplyCircuitId).toBeNull();
    await expect(page.getByRole('navigation').getByTestId(`tool-${label}`)).toHaveCount(0);
    await page.getByRole('button', { name: 'close', exact: true }).click();
    await supply().click();
    await page
      .getByRole('option')
      .filter({ hasText: new RegExp(`^${escape(sampleName('bedroomLighting'))}$`) })
      .click();
    await page.getByRole('button', { name: label, exact: true }).click();
    await expect(name).toHaveValue(sampleName('bedroomLighting'));
    await expect(item(sampleName('bedroomLighting')).first()).toBeVisible();
    expect(errors).toEqual([]);
  } finally {
    if (projectId) {
      const result = await request.post('/api/graphql', {
        data: {
          query: 'mutation($input:DeleteProjectInput!){deleteProject(input:$input){__typename}}',
          variables: { input: { id: randomUUID(), projectId } },
        },
      });
      expect((await result.json()).data.deleteProject.__typename).toMatch(/Success/);
    }
  }
});
