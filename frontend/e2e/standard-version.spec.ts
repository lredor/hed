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
import { randomUUID } from 'node:crypto';
import { readFileSync } from 'node:fs';
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

test('persist the standard profile and recompute historical and A5 counts for branched outlet blocks', async ({
  page,
  request,
}) => {
  test.setTimeout(180000);
  page.setDefaultTimeout(15000);
  await page.setViewportSize({ width: 1800, height: 4200 });
  const projectName = 'E2E standard profile ' + randomUUID();
  let projectId: string | undefined;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  const item = (name: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp('^' + escape(name) + '$') })
      .first();
  const expand = async () => {
    await item(projectName).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
  };
  const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
  const drop = async (source: ReturnType<typeof item>, target: ReturnType<typeof item>) => {
    const response = page.waitForResponse(
      (result) =>
        result.url().endsWith('/api/graphql') && result.request().postDataJSON()?.operationName === 'dropTreeItem'
    );
    await source.dragTo(target);
    const payload = await (await response).json();
    expect(payload.errors).toBeUndefined();
    expect(payload.data.dropTreeItem.__typename).toBe('SuccessPayload');
  };
  const selectProfile = async (version: string) => {
    await item(messages['class.Dwelling']).click();
    await page.getByTestId(messages['feature.standardVersion']).getByRole('combobox').click();
    await page.getByRole('option', { name: messages['enum.StandardVersion.' + version], exact: true }).click();
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
    // The sample deliberately leaves conductor sections unspecified; record the test circuit's section.
    await item(sampleName('livingRoomOutlets')).click();
    const section = page.getByRole('textbox', { name: messages['feature.conductorSectionMm2'], exact: true });
    await section.fill('2.5');
    await section.press('Tab');
    await item(sampleName('livingRoomOutlets')).locator('../..').getByRole('button').click();
    await page
      .getByRole('button', { name: `${messages['action.create']} ${messages['class.CircuitBranch']}`, exact: true })
      .click();
    await expand();
    await item(messages['new.CircuitBranch']).click();
    await name.fill('Norm branch');
    await name.press('Tab');
    await expect(item('Norm branch')).toBeVisible();
    for (let index = 0; index < 7; index++) {
      const type = page
        .locator('img[alt*="entity=DeviceType"]')
        .locator('../..')
        .filter({ has: item(messages['kind.OUTLET_BLOCK']) })
        .locator('p');
      await type.click();
      await expect(name).toHaveValue(messages['kind.OUTLET_BLOCK']);
      await drop(type, item(sampleName('livingRoom')));
      await expand();
      const room = item(sampleName('livingRoom')).locator('xpath=ancestor::li[1]');
      await room
        .locator('p')
        .filter({ hasText: new RegExp('^' + escape(messages['kind.OUTLET_BLOCK']) + '$') })
        .first()
        .click();
      // The catalog type and its instance share a name; wait for the installed device's Details.
      await expect(page.getByTestId(messages['feature.room']).getByRole('combobox')).toHaveText(
        sampleName('livingRoom')
      );
      await expect(page.getByTestId(messages['feature.deviceType']).getByRole('combobox')).toHaveText(
        messages['kind.OUTLET_BLOCK']
      );
      await name.fill('NORM-BLOCK-' + index);
      await name.press('Tab');
      const created = room.locator('p').filter({ hasText: 'NORM-BLOCK-' + index });
      await expect(created).toHaveCount(1);
      await expect(created).toBeVisible();
      await expect(type).toHaveText(messages['kind.OUTLET_BLOCK']);
      await drop(created, item('Norm branch'));
    }
    await selectProfile('NF_C_15_100_A5_2015');
    const validation = page.getByRole('textbox', { name: messages['validation'], exact: true });
    const countWarning = french ? /15 prises décomptées.*12/ : /15 counted outlets.*12/;
    await expect(validation).toHaveValue(countWarning);
    await selectProfile('NF_C_15_100_BEFORE_A5');
    await expect(validation).not.toHaveValue(countWarning);
    await selectProfile('NF_C_15_100_A5_2015');
    await expect(validation).toHaveValue(countWarning);
    await page.reload();
    await expand();
    await item(messages['class.Dwelling']).click();
    await expect(page.getByTestId(messages['feature.standardVersion']).getByRole('combobox')).toHaveText(
      messages['enum.StandardVersion.NF_C_15_100_A5_2015']
    );
    await expect(validation).toHaveValue(countWarning);
    await selectProfile('UNSPECIFIED');
    await expect(validation).toHaveValue(messages['consistent']);
    expect(errors).toEqual([]);
  } finally {
    if (projectId) {
      const response = await request.post('/api/graphql', {
        data: {
          query: 'mutation($input:DeleteProjectInput!){deleteProject(input:$input){__typename}}',
          variables: { input: { id: randomUUID(), projectId } },
        },
      });
      const payload = await response.json();
      expect(payload.errors).toBeUndefined();
      expect(payload.data.deleteProject.__typename).toMatch(/Success/);
    }
  }
});
