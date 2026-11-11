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
import { channelName, sampleName, samplePattern } from './sample';
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

test('highlight unplaced room equipment only when its native explorer filter is enabled', async ({ page, request }) => {
  test.setTimeout(150000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E floor plan highlight ${randomUUID()}`;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  let projectId: string | undefined;
  const item = (label: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp(`^${escape(label)}$`) })
      .first();
  const color = (label: string) =>
    item(label).evaluate((element) => {
      const text = document.createTreeWalker(element, NodeFilter.SHOW_TEXT).nextNode();
      return getComputedStyle(text!.parentElement!).color;
    });
  const red = 'rgb(198, 40, 40)';
  const expand = async () => {
    await item(projectName).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await expect(item(sampleName('sofaOutlet'))).toBeVisible();
  };
  const filter = async (enabled?: boolean) => {
    await page.getByTestId('tree-filter-menu-icon').click();
    const checkbox = page.getByRole('checkbox', { name: messages['explorer.highlightUnplaced'], exact: true });
    if (enabled === undefined) await expect(checkbox).not.toBeChecked();
    else {
      await checkbox.setChecked(enabled);
      await expect(checkbox).toBeChecked({ checked: enabled });
    }
    await page.keyboard.press('Escape');
  };
  const background = page.getByTestId('floor-plan-background');
  const nodes = page.locator('[data-testid^="floor-plan-equipment-"]');
  const createPlan = async (label: string, storey = sampleName('groundFloor')) => {
    await item(storey).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.newRepresentation'], exact: true }).last().click();
    const dialog = page.getByRole('dialog');
    await dialog.getByRole('combobox').click();
    const title = escape(messages['diagram.floorPlan']);
    await page.getByRole('option', { name: new RegExp(`^${title}(?: ${title})?$`) }).click();
    await dialog.getByRole('textbox').fill(label);
    await dialog.getByRole('button', { name: french ? 'Créer' : 'Create', exact: true }).click();
    await expect(background).toBeVisible();
    await expect(nodes).toHaveCount(0);
    return page.url();
  };
  const place = async () => {
    const box = await background.boundingBox();
    await item(sampleName('sofaOutlet')).dragTo(background, {
      targetPosition: { x: box!.width * 0.35, y: box!.height * 0.4 },
    });
    await expect(nodes).toHaveCount(1);
    await expect.poll(() => color(sampleName('sofaOutlet'))).not.toBe(red);
  };
  const room = page.getByTestId(messages['feature.room']).getByRole('combobox');
  const selectRoom = async (label: string) => {
    await room.click();
    const value = escape(label);
    await page.getByRole('option', { name: new RegExp(`^${value}(?: ${value})?$`) }).click();
    await expect(room).toHaveText(label);
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
    const normal = await color(sampleName('sofaOutlet'));
    expect(normal).not.toBe(red);
    await filter();
    const count = await page.locator('img[alt*="entity=Outlet"]').count();
    await filter(true);
    await expect.poll(() => color(sampleName('sofaOutlet'))).toBe(red);
    await expect.poll(() => color(sampleName('doubleSwitch'))).toBe(red);
    for (const parent of [
      sampleName('livingRoom'),
      sampleName('groundFloor'),
      sampleName('bedroom'),
      sampleName('firstFloor'),
      messages['class.Dwelling'],
    ]) {
      await expect.poll(() => color(parent)).toBe(red);
    }
    expect(await color(projectName)).not.toBe(red);
    expect(await color(sampleName('garage'))).not.toBe(red);
    expect(await color(channelName(1))).not.toBe(red);
    const catalogType = page
      .locator('img[alt*="entity=DeviceType"]')
      .locator('../..')
      .filter({ has: item(messages['kind.OUTLET']) })
      .locator('p');
    expect(await catalogType.evaluate((element) => getComputedStyle(element).color)).not.toBe(red);
    await expect(page.locator('img[alt*="entity=Outlet"]')).toHaveCount(count);
    // Parent colors come from semantic room assignments, even when their children are not rendered.
    for (const [parent, child] of [
      [sampleName('livingRoom'), sampleName('sofaOutlet')],
      [sampleName('groundFloor'), sampleName('livingRoom')],
      [messages['class.Dwelling'], sampleName('groundFloor')],
    ]) {
      await page.getByTestId(`${parent}-toggle`).click();
      await expect(item(child)).not.toBeVisible();
      await expect.poll(() => color(parent)).toBe(red);
    }
    await filter(false);
    await expect.poll(() => color(messages['class.Dwelling'])).not.toBe(red);
    await filter(true);
    await expect.poll(() => color(messages['class.Dwelling'])).toBe(red);
    await expand();
    await filter(false);
    await expect.poll(() => color(sampleName('sofaOutlet'))).toBe(normal);
    await filter(true);
    const first = await createPlan('Highlight plan one');
    await place();
    // A separate explorer starts with its own disabled filter, even while the first is enabled.
    const other = await page.context().newPage();
    try {
      await other.goto(first);
      const root = other
        .locator('img[alt="hed-project"]')
        .first()
        .locator('xpath=ancestor::li[1]')
        .locator('p')
        .first();
      await root.locator('../..').getByRole('button').click();
      await other.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
      const control = other
        .locator('p')
        .filter({ hasText: samplePattern('doubleSwitch') })
        .first();
      await expect(control).toBeVisible();
      expect(
        await control.evaluate((element) => getComputedStyle(element.querySelector('span') ?? element).color)
      ).not.toBe(red);
      await other.getByTestId('tree-filter-menu-icon').click();
      await expect(
        other.getByRole('checkbox', { name: messages['explorer.highlightUnplaced'], exact: true })
      ).not.toBeChecked();
      await expect.poll(() => color(sampleName('doubleSwitch'))).toBe(red);
    } finally {
      await other.close();
    }
    await expand();
    await createPlan('Highlight plan two');
    await place();
    await page.goto(first);
    await expect(nodes).toHaveCount(1);
    await expand();
    await filter();
    await filter(true);
    await nodes.first().click({ button: 'right' });
    await page.getByRole('button', { name: messages['plan.removeEquipment'], exact: true }).last().click();
    await expect(nodes).toHaveCount(0);
    await expect.poll(() => color(sampleName('sofaOutlet'))).not.toBe(red);
    // The only remaining placement is now in the other saved plan.
    await item('Highlight plan two').locator('../..').getByRole('button').click();
    const deletion = french ? 'Supprimer' : 'Delete';
    await page.getByRole('menuitem', { name: deletion, exact: true }).last().click();
    await page.getByRole('dialog').getByRole('button', { name: deletion, exact: true }).click();
    await expect(item('Highlight plan two')).toHaveCount(0);
    await expect.poll(() => color(sampleName('sofaOutlet'))).toBe(red);
    await item('Highlight plan one').click();
    await expect(background).toBeVisible();
    await place();
    await nodes.first().click();
    await selectRoom(sampleName('bedroom'));
    await expect(nodes).toHaveCount(0);
    await expand();
    await expect.poll(() => color(sampleName('sofaOutlet'))).toBe(red);
    await item(sampleName('sofaOutlet')).click();
    await selectRoom(messages['unassigned']);
    await expand();
    await expect.poll(() => color(sampleName('sofaOutlet'))).not.toBe(red);
    await page.reload();
    await expect(background).toBeVisible();
    await expand();
    await filter();
    await expect.poll(() => color(sampleName('doubleSwitch'))).not.toBe(red);
    await filter(true);
    await expect.poll(() => color(sampleName('doubleSwitch'))).toBe(red);
    await expect.poll(() => color(sampleName('sofaOutlet'))).not.toBe(red);
    // Clear one storey, then the entire dwelling; an unlocated device never makes a parent red.
    const remaining = [
      sampleName('ceilingLight'),
      sampleName('livingRoomShutter'),
      sampleName('doubleSwitch'),
      sampleName('secondControl'),
      sampleName('shutterControl'),
    ];
    for (let index = 0; index < remaining.length; index++) {
      await item(remaining[index]).dragTo(background, { targetPosition: { x: 100 + index * 80, y: 180 } });
      await expect(nodes).toHaveCount(index + 1);
    }
    await expect.poll(() => color(sampleName('livingRoom'))).not.toBe(red);
    await expect.poll(() => color(sampleName('groundFloor'))).not.toBe(red);
    await expect.poll(() => color(messages['class.Dwelling'])).toBe(red);
    await createPlan('Highlight upper plan', sampleName('firstFloor'));
    await item(sampleName('bedroomLight')).dragTo(background, { targetPosition: { x: 200, y: 180 } });
    await expect(nodes).toHaveCount(1);
    for (const parent of [sampleName('bedroom'), sampleName('firstFloor'), messages['class.Dwelling']]) {
      await expect.poll(() => color(parent)).not.toBe(red);
    }
    await nodes.first().click({ button: 'right' });
    await page.getByRole('button', { name: messages['plan.removeEquipment'], exact: true }).last().click();
    await expect(nodes).toHaveCount(0);
    for (const parent of [sampleName('bedroom'), sampleName('firstFloor'), messages['class.Dwelling']]) {
      await expect.poll(() => color(parent)).toBe(red);
    }
    expect(await color(sampleName('groundFloor'))).not.toBe(red);
    expect(await color(projectName)).not.toBe(red);
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
