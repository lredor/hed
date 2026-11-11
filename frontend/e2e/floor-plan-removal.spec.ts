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
const translations = JSON.parse(
  readFileSync(
    new URL(
      `../../backend/hed-starter/src/main/resources/i18n/${french ? 'fr' : 'en'}/home-electrical-designer.json`,
      import.meta.url
    ),
    'utf8'
  )
);
const escape = (text: string) => text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

test('explain that Delete removes floor-plan views and preserve equipment and connections', async ({
  page,
  request,
}) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E floor plan removal ${randomUUID()}`;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  let projectId: string | undefined;
  const item = (label: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp(`^${escape(label)}$`) })
      .first();
  const expand = async () => {
    await item(projectName).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await expect(item('Sofa outlet')).toBeVisible();
  };
  const background = page.getByTestId('floor-plan-background');
  const devices = page.locator('[data-testid^="floor-plan-equipment-"]');
  const device = (label: string) =>
    devices.filter({
      has: page.locator('.hed-floor-equipment-label').filter({ hasText: new RegExp(`^${escape(label)}$`) }),
    });
  const place = async (label: string, x: number) => {
    const box = await background.boundingBox();
    await item(label).dragTo(background, { targetPosition: { x: box!.width * x, y: box!.height * 0.5 } });
    await expect(device(label)).toBeVisible();
  };
  const confirmation = page.getByRole('dialog');
  const confirm = async () => {
    await expect(confirmation).toContainText(translations.plan.removeConfirmation);
    await confirmation.getByRole('button', { name: translations.plan.removeEquipment, exact: true }).click();
    await expect(confirmation).toHaveCount(0);
  };
  try {
    await page.goto('/');
    await page.locator('a[href="/new/project?templateId=hed-example"]').click();
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\//);
    projectId = page.url().match(/\/projects\/([^/]+)\//)?.[1];
    expect(projectId).toBeTruthy();
    await item(projectName).waitFor();
    await expand();
    await item('Ground floor').locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.newRepresentation'], exact: true }).last().click();
    const dialog = page.getByRole('dialog');
    await dialog.getByRole('combobox').click();
    const title = escape(messages['diagram.floorPlan']);
    await page.getByRole('option', { name: new RegExp(`^${title}(?: ${title})?$`) }).click();
    await dialog.getByRole('textbox').fill('View-only removal');
    await dialog.getByRole('button', { name: french ? 'Créer' : 'Create', exact: true }).click();
    await expect(background).toBeVisible();
    await place('Sofa outlet', 0.3);
    await place('Ceiling light', 0.65);
    const outletId = await device('Sofa outlet').getAttribute('data-semantic-id');
    const lightId = await device('Ceiling light').getAttribute('data-semantic-id');
    await device('Sofa outlet').click();
    await page.keyboard.press('Delete');
    await expect(confirmation).toContainText(translations.plan.removeConfirmation);
    await expect(confirmation.getByRole('heading')).toHaveText(translations.plan.removeEquipment);
    await confirmation.getByRole('button', { name: /^(Annuler|Cancel)$/, exact: true }).click();
    await expect(devices).toHaveCount(2);
    await device('Sofa outlet').click();
    await page.keyboard.press('Delete');
    await confirm();
    await expect(devices).toHaveCount(1);
    await expand();
    await item('Sofa outlet').click();
    await expect(page.getByTestId(messages['feature.room']).getByRole('combobox')).toHaveText('Living room');
    await expect(page.getByTestId(messages['feature.circuit']).getByRole('combobox')).toHaveText('Living room outlets');
    expect(new URL(page.url()).searchParams.get('selection')).toBe(outletId);
    // Semantic deletion from Model Explorer keeps the standard warning, even while a floor plan is open.
    await item('Sofa outlet').locator('../..').getByRole('button').click();
    await page
      .getByRole('menuitem', { name: french ? 'Supprimer' : 'Delete', exact: true })
      .last()
      .click();
    await expect(confirmation).not.toContainText(translations.plan.removeConfirmation);
    await expect(confirmation).toContainText(french ? 'éléments sélectionnés' : 'delete the selected element');
    await confirmation.getByRole('button', { name: /^(Annuler|Cancel)$/, exact: true }).click();
    await place('Sofa outlet', 0.3);
    await device('Sofa outlet').click();
    await device('Ceiling light').click({ modifiers: ['Control'] });
    await expect(page.locator('.react-flow__node.selected').filter({ has: devices })).toHaveCount(2);
    await page.keyboard.press('Delete');
    await confirm();
    await expect(devices).toHaveCount(0);
    await expand();
    await expect(item('Sofa outlet')).toBeVisible();
    await expect(item('Ceiling light')).toBeVisible();
    await item('Ceiling light').click();
    await expect(page.getByTestId(messages['feature.room']).getByRole('combobox')).toHaveText('Living room');
    await expect(page.getByTestId(messages['feature.circuit']).getByRole('combobox')).toHaveText(
      'Ground floor lighting'
    );
    expect(new URL(page.url()).searchParams.get('selection')).toBe(lightId);
    await place('Sofa outlet', 0.4);
    await page.reload();
    await expect(devices).toHaveCount(1);
    await expect(device('Sofa outlet')).toHaveAttribute('data-semantic-id', outletId!);
    await expand();
    await expect(item('Ceiling light')).toBeVisible();
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
