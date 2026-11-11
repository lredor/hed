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

test('create modular devices from catalog types in the explorer and diagram, reject invalid or full rails and reopen', async ({
  page,
  request,
}) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E catalog drop ${randomUUID()}`;
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
    await item(messages['kind.MCB']).waitFor();
  };
  const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
  const create = async (owner: string, concept: string, label: string) => {
    await more(owner);
    await page
      .getByRole('button', { name: `${messages['action.create']} ${messages[`class.${concept}`]}`, exact: true })
      .click();
    await expand();
    await item(messages[`new.${concept}`]).click();
    await expect(name).toHaveValue(messages[`new.${concept}`]);
    await name.fill(label);
    await name.press('Tab');
    await expect(item(label)).toBeVisible();
  };
  const typeItem = (kind: string) =>
    page
      .locator('img[alt*="entity=DeviceType"]')
      .locator('../..')
      .filter({ has: item(messages[`kind.${kind}`]) })
      .locator('p');
  const devices = page.locator('[data-panel-role="device"]');
  const drag = async (
    source: ReturnType<typeof typeItem>,
    target: ReturnType<typeof item>,
    operation: string,
    targetPosition?: { x: number; y: number }
  ) => {
    const response = page.waitForResponse(
      (response) =>
        response.url().endsWith('/api/graphql') && response.request().postDataJSON()?.operationName === operation
    );
    await source.dragTo(target, { targetPosition });
    const payload = await (await response).json();
    expect(payload.errors).toBeUndefined();
    return payload.data[operation];
  };
  const renameDevice = async (index: number, label: string) => {
    await expand();
    await page.locator('img[alt*="entity=ModularDevice"]').nth(index).locator('../..').locator('p').click();
    await expect(name).toHaveValue(messages['kind.MCB']);
    await name.fill(label);
    await name.press('Tab');
    await expect(devices.locator('.hed-device-reference').filter({ hasText: new RegExp(`^${label}$`) })).toBeVisible();
  };
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
    await create(messages['explorer.gtl'], 'DistributionBoard', 'Drop board');
    await create('Drop board', 'DINRail', 'Drop rail');
    const capacity = page.getByRole('textbox', { name: messages['feature.capacityModules'], exact: true });
    await capacity.fill('3');
    await capacity.press('Tab');
    const breakerType = typeItem('MCB');
    await breakerType.click();
    await expect(name).toHaveValue(messages['kind.MCB']);
    const current = page.getByRole('textbox', { name: messages['feature.ratedCurrentA'], exact: true });
    await current.fill('16');
    await current.press('Tab');
    const typeCount = await page.locator('img[alt*="entity=DeviceType"]').count();
    await more('Drop board');
    await page.getByRole('menuitem', { name: messages['action.newRepresentation'], exact: true }).last().click();
    const dialog = page.getByRole('dialog');
    await dialog.getByRole('combobox').click();
    const title = escape(messages['diagram.panel']);
    await page.getByRole('option', { name: new RegExp(`^${title}(?: ${title})?$`) }).click();
    await dialog.getByRole('textbox').first().fill('Catalog drops');
    await dialog.getByRole('button', { name: french ? 'Créer' : 'Create', exact: true }).click();
    const rail = page
      .locator('[data-panel-role="rail"]')
      .filter({ has: page.locator('.hed-rail-title', { hasText: /^Drop rail$/ }) });
    await expect(rail).toBeVisible();
    await expect(devices).toHaveCount(0);
    await drag(breakerType, item('Drop rail'), 'dropTreeItem');
    await expect(devices).toHaveCount(1);
    await renameDevice(0, 'TREE1');
    const occupiedRailDrop = await drag(breakerType, devices.first(), 'dropOnDiagram');
    expect(occupiedRailDrop.messages).not.toContainEqual(
      expect.objectContaining({ body: messages['drop.catalogRail'] })
    );
    await expect(devices).toHaveCount(2);
    await renameDevice(1, 'DIAGRAM1');
    // Exercise the visible metal strip as well as the rail title and the area covered by a modular device.
    const railBounds = await rail.boundingBox();
    expect(railBounds).not.toBeNull();
    await drag(breakerType, rail, 'dropOnDiagram', { x: railBounds!.width * 0.8, y: railBounds!.height * 0.42 });
    await expect(devices).toHaveCount(3);
    await renameDevice(2, 'EMPTY1');
    await expect(current).toHaveValue('16.0');
    await expect(current).toBeDisabled();
    await expect(page.getByTestId(messages['feature.rail']).getByRole('combobox')).toHaveText('Drop rail');
    const ids = await devices.evaluateAll((elements) =>
      elements.map((element) => element.getAttribute('data-semantic-id')).sort()
    );
    const fullOccupiedRail = await drag(breakerType, devices.first(), 'dropOnDiagram');
    expect(fullOccupiedRail.messages).toContainEqual(expect.objectContaining({ body: messages['railCapacity'] }));
    const fullDiagram = await drag(breakerType, rail.locator('.hed-rail-title'), 'dropOnDiagram');
    expect(fullDiagram.messages).toContainEqual(expect.objectContaining({ body: messages['railCapacity'] }));
    const fullExplorer = await drag(breakerType, item('Drop rail'), 'dropTreeItem');
    expect(fullExplorer.messages).toContainEqual(expect.objectContaining({ body: messages['railCapacity'] }));
    await expect(devices).toHaveCount(3);
    const invalidType = await drag(typeItem('OUTLET'), rail.locator('.hed-rail-title'), 'dropOnDiagram');
    expect(invalidType.messages).toContainEqual(expect.objectContaining({ body: messages['drop.modularType'] }));
    const invalidTarget = await drag(breakerType, page.locator('.hed-panel-title'), 'dropOnDiagram');
    expect(invalidTarget.messages).toContainEqual(expect.objectContaining({ body: messages['drop.catalogRail'] }));
    await expect(devices).toHaveCount(3);
    await expect(page.locator('img[alt*="entity=DeviceType"]')).toHaveCount(typeCount);
    await page.reload();
    await expect(devices).toHaveCount(3);
    await expect(devices.locator('.hed-device-reference')).toHaveText(['TREE1', 'DIAGRAM1', 'EMPTY1']);
    expect(
      await devices.evaluateAll((elements) =>
        elements.map((element) => element.getAttribute('data-semantic-id')).sort()
      )
    ).toEqual(ids);
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
