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
import { channelName, sampleName } from './sample';

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

test('move existing room equipment without cloning devices or changing their connections and retain floor-plan scope', async ({
  page,
  request,
}) => {
  test.setTimeout(180000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E room equipment move ${randomUUID()}`;
  let projectId: string | undefined;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  const item = (label: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp(`^${escape(label)}$`) })
      .first();
  const room = (label: string) => item(label).locator('xpath=ancestor::li[1]');
  const inRoom = (location: string, label: string) =>
    room(location)
      .locator('p')
      .filter({ hasText: new RegExp(`^${escape(label)}$`) })
      .first();
  const expand = async () => {
    await item(projectName).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await expect(item(sampleName('bedroom'))).toBeVisible();
  };
  const inspect = async (location: string, label: string) => {
    await inRoom(location, label).click();
    await expect(page.getByRole('textbox', { name: messages['feature.name'], exact: true })).toHaveValue(label);
    await expect(page.getByTestId(messages['feature.room']).getByRole('combobox')).toHaveText(location);
    const id = new URL(page.url()).searchParams.get('selection');
    expect(id).toBeTruthy();
    return {
      id,
      circuit: await page.getByTestId(messages['feature.circuit']).getByRole('combobox').innerText(),
      type: await page.getByTestId(messages['feature.deviceType']).getByRole('combobox').innerText(),
    };
  };
  const move = async (from: string, to: string, label: string) => {
    const response = page.waitForResponse(
      (result) =>
        result.url().endsWith('/api/graphql') && result.request().postDataJSON()?.operationName === 'dropTreeItem'
    );
    await inRoom(from, label).dragTo(item(to));
    const payload = await (await response).json();
    expect(payload.errors).toBeUndefined();
    expect(payload.data.dropTreeItem.__typename).toMatch(/Success/);
    await expect(inRoom(to, label)).toBeVisible();
    await expect(inRoom(from, label)).toHaveCount(0);
  };
  const createPlan = async (storey: string, title: string) => {
    await item(storey).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.newRepresentation'], exact: true }).last().click();
    const dialog = page.getByRole('dialog');
    await dialog.getByRole('combobox').click();
    const name = escape(messages['diagram.floorPlan']);
    await page.getByRole('option', { name: new RegExp(`^${name}(?: ${name})?$`) }).click();
    await dialog.getByRole('textbox').fill(title);
    await dialog.getByRole('button', { name: french ? 'Créer' : 'Create', exact: true }).click();
    await expect(page.getByTestId('floor-plan-background')).toBeVisible();
  };
  try {
    await page.setViewportSize({ width: 1800, height: 2400 });
    await page.goto('/new/project?templateId=hed-example');
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\//);
    projectId = page.url().match(/\/projects\/([^/]+)\//)?.[1];
    expect(projectId).toBeTruthy();
    await item(projectName).waitFor();
    await expand();
    const living = sampleName('livingRoom'),
      garage = sampleName('garage'),
      bedroom = sampleName('bedroom');
    const labels = [sampleName('sofaOutlet'), sampleName('ceilingLight'), sampleName('doubleSwitch')];
    const original = new Map<string, Awaited<ReturnType<typeof inspect>>>();
    for (const label of labels) original.set(label, await inspect(living, label));
    const devices = page.locator(
      'img[alt*="entity=Outlet"], img[alt*="entity=ControlDevice"], img[alt*="entity=ElectricalLoad"]'
    );
    const count = await devices.count();
    await createPlan(sampleName('groundFloor'), 'Room move ground plan');
    const background = page.getByTestId('floor-plan-background');
    const bounds = await background.boundingBox();
    await inRoom(living, labels[0]).dragTo(background, {
      targetPosition: { x: bounds!.width * 0.35, y: bounds!.height * 0.4 },
    });
    const outletId = original.get(labels[0])!.id!;
    const placed = page.getByTestId(`floor-plan-equipment-${outletId}`);
    await expect(placed).toBeVisible();
    const position = await placed.evaluate(
      (element) => (element.closest('.react-flow__node') as HTMLElement).style.transform
    );
    for (const label of labels) {
      await move(living, garage, label);
      expect(await inspect(garage, label)).toEqual(original.get(label));
    }
    await expect(devices).toHaveCount(count);
    await expect(placed).toBeVisible();
    expect(
      await placed.evaluate((element) => (element.closest('.react-flow__node') as HTMLElement).style.transform)
    ).toBe(position);
    const control = inRoom(garage, labels[2]).locator('xpath=ancestor::li[1]');
    await expect(control.locator('p').filter({ hasText: channelName(1) })).toBeVisible();
    await expect(control.locator('p').filter({ hasText: channelName(2) })).toBeVisible();
    await move(garage, bedroom, labels[0]);
    expect(await inspect(bedroom, labels[0])).toEqual(original.get(labels[0]));
    await expect(placed).toHaveCount(0);
    await page.reload();
    await expand();
    expect(await inspect(bedroom, labels[0])).toEqual(original.get(labels[0]));
    for (const label of labels.slice(1)) expect(await inspect(garage, label)).toEqual(original.get(label));
    await expect(devices).toHaveCount(count);
    await createPlan(sampleName('firstFloor'), 'Room move upper plan');
    const upperBackground = page.getByTestId('floor-plan-background');
    await inRoom(bedroom, labels[0]).dragTo(upperBackground, { targetPosition: { x: 180, y: 180 } });
    await expect(placed).toBeVisible();
    await expect(placed).toHaveAttribute('data-semantic-id', outletId);
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
