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

test('create a Canva panel diagram, reorder and transfer devices, and reopen the constrained layout', async ({
  page,
  request,
}) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E panel ${randomUUID()}`;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  let projectId: string | undefined;
  const item = (name: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp(`^${escape(name)}$`) })
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
  const devices = page.locator('[data-panel-role="device"]');
  const component = (name: string) =>
    devices.filter({ has: page.locator('.hed-device-reference').filter({ hasText: new RegExp(`^${escape(name)}$`) }) });
  const rail = (name: string) =>
    page
      .locator('[data-panel-role="rail"]')
      .filter({ has: page.locator('.hed-rail-title').filter({ hasText: new RegExp(`^${escape(name)}$`) }) });
  const drag = async (
    source: ReturnType<typeof component>,
    target: ReturnType<typeof component>,
    endOfRail = false
  ) => {
    await expect
      .poll(async () => {
        const before = await source.boundingBox();
        await page.evaluate(
          () => new Promise((resolve) => requestAnimationFrame(() => requestAnimationFrame(resolve)))
        );
        const after = await source.boundingBox();
        return !!before && !!after && Math.abs(before.x - after.x) < 0.1 && Math.abs(before.y - after.y) < 0.1;
      })
      .toBe(true);
    const from = await source.boundingBox();
    const to = await target.boundingBox();
    expect(from).not.toBeNull();
    expect(to).not.toBeNull();
    const dropped = page.waitForResponse(
      (response) =>
        response.url().endsWith('/api/graphql') && response.request().postDataJSON()?.operationName === 'dropNodes'
    );
    await page.mouse.move(from!.x + from!.width / 2, from!.y + Math.min(50, from!.height / 3));
    await page.mouse.down();
    await page.mouse.move(to!.x + (endOfRail ? to!.width - 4 : to!.width / 2), to!.y + Math.min(50, to!.height / 3), {
      steps: 20,
    });
    await page.mouse.up();
    const payload = await (await dropped).json();
    expect(payload.errors).toBeUndefined();
    expect(payload.data.dropNodes.__typename).toBe('SuccessPayload');
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
    for (const name of ['Rail 2', 'Rail 3']) {
      await more('Main distribution board');
      await page
        .getByRole('button', { name: `${messages['action.create']} ${messages['class.DINRail']}`, exact: true })
        .click();
      await expand();
      await item(messages['new.DINRail']).click();
      await page.getByRole('textbox', { name: messages['feature.name'], exact: true }).fill(name);
      await page.keyboard.press('Tab');
      await expect(item(name)).toBeVisible();
    }
    await more('Rail 3');
    await page
      .getByRole('button', { name: `${messages['action.create']} ${messages['kind.MCB']}`, exact: true })
      .click();
    await expand();
    await page
      .getByTestId(messages['kind.MCB'])
      .filter({ has: page.locator('img[alt*="entity=ModularDevice"]') })
      .click();
    await page.getByRole('textbox', { name: messages['feature.name'], exact: true }).fill('Q9');
    await page.keyboard.press('Tab');
    await item('Q9').waitFor();
    await item('Row 1').click();
    const capacity = page.getByRole('textbox', { name: messages['feature.capacityModules'], exact: true });
    await capacity.fill('6');
    await capacity.press('Tab');
    await more('Main distribution board');
    await page.getByRole('menuitem', { name: messages['action.newRepresentation'], exact: true }).last().click();
    const dialog = page.getByRole('dialog');
    await dialog.getByRole('combobox').click();
    const title = escape(messages['diagram.panel']);
    await page.getByRole('option', { name: new RegExp(`^${title}(?: ${title})?$`) }).click();
    await dialog.getByRole('textbox').first().fill('Panel layout');
    await dialog.getByRole('button', { name: french ? 'Créer' : 'Create', exact: true }).click();
    await expect(devices).toHaveCount(6);
    await expect(page.locator('[data-panel-role="rail"]')).toHaveCount(3);
    const cabinet = page.locator('.hed-cabinet-image');
    await expect
      .poll(() => cabinet.evaluate((element: HTMLImageElement) => element.complete && element.naturalWidth > 0))
      .toBe(true);
    const response = await request.get((await cabinet.getAttribute('src'))!);
    expect(response.headers()['content-type']).toContain('image/svg+xml');
    const svg = await response.text();
    expect(svg.match(/<g\b/g)).toHaveLength(3);
    expect(svg).toContain('id="etiquette-3-bleu"');
    expect(svg).toContain('id="rail-3-ame"');
    await expect(page.locator('.hed-din-bar,.hed-cabinet-screw,[data-panel-role="unmounted"]')).toHaveCount(0);
    await expect
      .poll(async () => {
        const device = await component('Q1').locator('img').boundingBox();
        const board = await cabinet.boundingBox();
        return Math.abs(device!.y + device!.height / 2 - (board!.y + (420 * board!.width) / 1200)) < 1;
      })
      .toBe(true);
    const mcb = await component('Q1').boundingBox();
    const rccb = await component('ID1').boundingBox();
    expect(rccb!.width / mcb!.width).toBeCloseTo(2, 1);
    const rccbImage = await component('ID1').locator('img').getAttribute('src');
    const rccbUrl = new URL(rccbImage!);
    expect(rccbUrl.searchParams.get('residualType')).toBe('A');
    const typeA = await (await request.get(rccbUrl.toString())).text();
    expect(typeA).toMatch(/>Type A<\/text>/);
    rccbUrl.searchParams.set('residualType', 'AC');
    const typeAC = await (await request.get(rccbUrl.toString())).text();
    expect(typeAC).toMatch(/>Type AC<\/text>/);
    await expect(rail('Row 1').locator('.hed-rail-capacity')).toHaveText(/^6 \/ 6 /);
    const appended = page.waitForResponse(
      (response) =>
        response.url().endsWith('/api/graphql') && response.request().postDataJSON()?.operationName === 'dropNodes'
    );
    await drag(component('Q1'), rail('Row 1'), true);
    expect((await (await appended).json()).data.dropNodes.__typename).toBe('SuccessPayload');
    await expect(component('Q1')).toHaveAttribute('data-order', '4');
    await page.reload();
    await expect(component('Q1')).toHaveAttribute('data-order', '4');
    await expect(rail('Row 1').locator('.hed-rail-capacity')).toHaveText(/^6 \/ 6 /);
    await drag(component('Q1'), component('Q2'));
    await expect(component('Q1')).toHaveAttribute('data-order', '1');
    await expand();
    await item('Row 1').click();
    await capacity.fill('13');
    await capacity.press('Tab');
    await drag(component('Q1'), component('Q3'));
    await expect(component('Q1')).toHaveAttribute('data-order', '3');
    await page.reload();
    await expect(component('Q1')).toHaveAttribute('data-order', '3');
    await expand();
    await expect(item('Panel layout').locator('xpath=ancestor::li[2]').locator('p').first()).toHaveText(
      'Main distribution board'
    );
    await more('Q1');
    await page.getByRole('button', { name: messages['action.moveLeft'], exact: true }).click();
    await expect(component('Q1')).toHaveAttribute('data-order', '2');
    await drag(component('Q1'), rail('Rail 2'));
    await expect(component('Q1')).toHaveAttribute('data-order', '0');
    await expect
      .poll(async () => {
        const device = await component('Q1').boundingBox();
        const row = await rail('Rail 2').boundingBox();
        return device!.y > row!.y && device!.y < row!.y + row!.height;
      })
      .toBe(true);
    await component('Q1').click();
    await expect(page.getByRole('textbox', { name: messages['feature.name'], exact: true })).toHaveValue('Q1');
    await expect(page.getByRole('textbox', { name: messages['feature.labelText'], exact: true })).toHaveValue(
      'Ground floor lighting'
    );
    await expect(page.getByTestId(messages['feature.rail']).getByRole('combobox')).toContainText('Rail 2');
    await expect(page.getByTestId(messages['feature.upstreamRccb']).getByRole('combobox')).toContainText('ID1');
    await page.reload();
    await expect(component('Q1')).toHaveAttribute('data-order', '0');
    await expect
      .poll(async () => {
        const device = await component('Q2').locator('img').boundingBox();
        const board = await cabinet.boundingBox();
        return Math.abs(device!.y + device!.height / 2 - (board!.y + (420 * board!.width) / 1200)) < 1;
      })
      .toBe(true);
    const deviceSvg = await (await request.get((await component('Q2').locator('img').getAttribute('src'))!)).text();
    expect(deviceSvg).toMatch(/>20<\/text>/);
    const unnamedId = await component('Q9').getAttribute('data-semantic-id');
    const unnamed = devices.and(page.locator(`[data-semantic-id="${unnamedId}"]`));
    await component('Q9').click();
    const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
    await expect(name).toHaveValue('Q9');
    for (const value of ['', '  ']) {
      await name.fill(value);
      await name.press('Tab');
      await expect(unnamed.locator('.hed-device-reference')).toHaveCount(0);
      await expect(unnamed.locator('img')).toBeVisible();
    }
    await page.reload();
    await expect(unnamed).toBeVisible();
    await expect(unnamed.locator('.hed-device-reference')).toHaveCount(0);
    await unnamed.click();
    await expect(name).toHaveValue('');
    await name.fill('Q9');
    await name.press('Tab');
    await expect(component('Q9')).toBeVisible();
    await page.locator('.react-flow__pane').click({ position: { x: 15, y: 15 } });
    await page.locator('[data-panel-role="board"]').screenshot({ path: test.info().outputPath('panel.png') });
    await component('Q9').click();
    await expect(name).toHaveValue('Q9');
    await name.focus();
    await name.press('End');
    await name.press('Delete');
    await expect(devices).toHaveCount(6);
    await expect(page.getByRole('dialog')).toHaveCount(0);
    await component('Q9').click();
    await page.keyboard.press('Delete');
    const confirmation = page.getByRole('dialog');
    await expect(confirmation).toBeVisible();
    await confirmation.getByRole('button', { name: /^(Cancel|Annuler)$/ }).click();
    await expect(component('Q9')).toBeVisible();
    await component('Q9').click();
    await page.keyboard.press('Delete');
    await confirmation.getByRole('button', { name: /^(Delete|Supprimer)$/ }).click();
    await expect(component('Q9')).toHaveCount(0);
    await expect(devices).toHaveCount(5);
    await expand();
    await expect(item('Q9')).toHaveCount(0);
    await page.reload();
    await expect(devices).toHaveCount(5);
    await expect(component('Q9')).toHaveCount(0);
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
