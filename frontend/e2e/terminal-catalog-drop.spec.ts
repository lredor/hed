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

test('create terminal devices from catalog types on a room and retain assignments when placing them on its floor plan', async ({
  page,
  request,
}) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E terminal catalog drop ${randomUUID()}`;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  let projectId: string | undefined;
  const item = (label: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp(`^${escape(label)}$`) })
      .first();
  const room = () => item(sampleName('livingRoom')).locator('xpath=ancestor::li[1]');
  const roomItem = (label: string) =>
    room()
      .locator('p')
      .filter({ hasText: new RegExp(`^${escape(label)}$`) })
      .first();
  const typeItem = (kind: string) =>
    page
      .locator('img[alt*="entity=DeviceType"]')
      .locator('../..')
      .filter({ has: item(messages[`kind.${kind}`]) })
      .locator('p');
  const expand = async () => {
    await item(projectName).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await expect(item(sampleName('livingRoom'))).toBeVisible();
    await expect(typeItem('OUTLET')).toBeVisible();
  };
  const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
  const current = page.getByRole('textbox', { name: messages['feature.ratedCurrentA'], exact: true });
  const checkReferences = async (kind: string) => {
    await expect(page.getByTestId(messages['feature.room']).getByRole('combobox')).toHaveText(sampleName('livingRoom'));
    await expect(page.getByTestId(messages['feature.deviceType']).getByRole('combobox')).toHaveText(
      messages[`kind.${kind}`]
    );
  };
  const renameCreated = async (kind: string, label: string) => {
    await expand();
    await roomItem(messages[`kind.${kind}`]).click();
    await expect(name).toHaveValue(messages[`kind.${kind}`]);
    await checkReferences(kind);
    const id = new URL(page.url()).searchParams.get('selection');
    expect(id).toBeTruthy();
    await name.fill(label);
    await name.press('Tab');
    await expect(roomItem(label)).toBeVisible();
    return id;
  };
  const drop = async (kind: string, target: string) => {
    const response = page.waitForResponse(
      (result) =>
        result.url().endsWith('/api/graphql') && result.request().postDataJSON()?.operationName === 'dropTreeItem'
    );
    await typeItem(kind).dragTo(item(target));
    const payload = await (await response).json();
    expect(payload.errors).toBeUndefined();
    return payload.data.dropTreeItem;
  };
  try {
    await page.setViewportSize({ width: 1800, height: 2400 });
    await page.goto('/');
    await page.locator('a[href="/new/project?templateId=hed-example"]').click();
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\//);
    projectId = page.url().match(/\/projects\/([^/]+)\//)?.[1];
    expect(projectId).toBeTruthy();
    await item(projectName).waitFor();
    await expand();
    const catalogCount = await page.locator('img[alt*="entity=DeviceType"]').count();
    const originalOutlets = await room().locator('img[alt*="entity=Outlet"]').count();
    await typeItem('OUTLET').click();
    await expect(name).toHaveValue(messages['kind.OUTLET']);
    await current.fill('16');
    await current.press('Tab');
    await expect(current).toHaveValue('16.0');
    await drop('OUTLET', sampleName('livingRoom'));
    const outletId = await renameCreated('OUTLET', 'ROOM-OUTLET-1');
    await page.getByRole('tab', { name: messages['properties.advanced'], exact: true }).click();
    await expect(current).toHaveValue('16.0');
    await page.getByRole('tab', { name: messages['properties.general'], exact: true }).click();
    await expect(room().locator('img[alt*="entity=Outlet"]')).toHaveCount(originalOutlets + 1);
    await drop('OUTLET', sampleName('livingRoom'));
    const secondId = await renameCreated('OUTLET', 'ROOM-OUTLET-2');
    expect(secondId).not.toBe(outletId);
    await expect(room().locator('img[alt*="entity=Outlet"]')).toHaveCount(originalOutlets + 2);

    await typeItem('PUSH_BUTTON').click();
    await expect(name).toHaveValue(messages['kind.PUSH_BUTTON']);
    const channels = page.getByRole('textbox', { name: messages['feature.channelCount'], exact: true });
    await channels.fill('2');
    await channels.press('Tab');
    await expect(channels).toHaveValue('2');
    await drop('PUSH_BUTTON', sampleName('livingRoom'));
    await renameCreated('PUSH_BUTTON', 'ROOM-BUTTON');
    const button = () => roomItem('ROOM-BUTTON').locator('xpath=ancestor::li[1]');
    for (const index of [1, 2]) {
      await expect(
        button()
          .locator('p')
          .filter({ hasText: new RegExp(`^${escape(messages['channel.defaultName'].replace('{0}', String(index)))}$`) })
      ).toBeVisible();
    }
    await drop('LUMINAIRE', sampleName('livingRoom'));
    await renameCreated('LUMINAIRE', 'ROOM-LIGHT');
    const wrongType = await drop('MCB', sampleName('livingRoom'));
    expect(wrongType.messages).toContainEqual(expect.objectContaining({ body: messages['drop.terminalType'] }));
    const wrongTarget = await drop('OUTLET', sampleName('groundFloor'));
    expect(wrongTarget.messages).toContainEqual(expect.objectContaining({ body: messages['drop.catalogTarget'] }));
    await expect(room().locator('img[alt*="entity=Outlet"]')).toHaveCount(originalOutlets + 2);
    await expect(page.locator('img[alt*="entity=DeviceType"]')).toHaveCount(catalogCount);

    // Room creation and floor-plan placement must refer to the same semantic device.
    await item(sampleName('groundFloor')).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.newRepresentation'], exact: true }).last().click();
    const dialog = page.getByRole('dialog');
    await dialog.getByRole('combobox').click();
    const title = escape(messages['diagram.floorPlan']);
    await page.getByRole('option', { name: new RegExp(`^${title}(?: ${title})?$`) }).click();
    await dialog.getByRole('textbox').fill('Room drop placement');
    await dialog.getByRole('button', { name: french ? 'Créer' : 'Create', exact: true }).click();
    const background = page.getByTestId('floor-plan-background');
    const nodes = page.locator('[data-testid^="floor-plan-equipment-"]');
    await expect(background).toBeVisible();
    await expect(nodes).toHaveCount(0);
    const bounds = await background.boundingBox();
    await roomItem('ROOM-OUTLET-1').dragTo(background, {
      targetPosition: { x: bounds!.width * 0.35, y: bounds!.height * 0.4 },
    });
    await expect(nodes).toHaveCount(1);
    await expect(nodes.first()).toHaveAttribute('data-semantic-id', outletId!);
    await page.reload();
    await expect(nodes).toHaveCount(1);
    await expect(nodes.first()).toHaveAttribute('data-semantic-id', outletId!);
    await expand();
    await expect(roomItem('ROOM-BUTTON')).toBeVisible();
    await expect(roomItem('ROOM-LIGHT')).toBeVisible();
    await roomItem('ROOM-OUTLET-1').click();
    await expect(name).toHaveValue('ROOM-OUTLET-1');
    await checkReferences('OUTLET');
    await page.getByRole('tab', { name: messages['properties.advanced'], exact: true }).click();
    await expect(current).toHaveValue('16.0');
    expect(new URL(page.url()).searchParams.get('selection')).toBe(outletId);
    await expect(room().locator('img[alt*="entity=Outlet"]')).toHaveCount(originalOutlets + 2);
    await expect(page.locator('img[alt*="entity=DeviceType"]')).toHaveCount(catalogCount);
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
