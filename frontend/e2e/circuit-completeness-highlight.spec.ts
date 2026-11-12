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

test('highlight incomplete circuits, unconnected equipment and unused modules with exclusive per-explorer filters', async ({
  page,
  request,
}) => {
  test.setTimeout(180000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E circuit highlight ${randomUUID()}`;
  let projectId: string | undefined;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
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
  const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
  const expand = async () => {
    await item(projectName).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await expect(item(sampleName('sofaOutlet'))).toBeVisible();
  };
  const apply = async (mode: 'none' | 'placement' | 'circuits') => {
    await page.getByTestId('tree-filter-menu-icon').click();
    const placement = page.getByRole('checkbox', { name: messages['explorer.highlightUnplaced'], exact: true });
    const completeness = page.getByRole('checkbox', {
      name: messages['explorer.highlightIncompleteCircuits'],
      exact: true,
    });
    if (mode === 'placement') await placement.check();
    else if (mode === 'circuits') await completeness.check();
    else {
      await completeness.uncheck();
      await placement.uncheck();
    }
    await expect(placement).toBeChecked({ checked: mode === 'placement' });
    await expect(completeness).toBeChecked({ checked: mode === 'circuits' });
    await page.keyboard.press('Escape');
  };
  const type = (kind: string) =>
    page
      .locator('img[alt*="entity=DeviceType"]')
      .locator('../..')
      .filter({ has: item(messages[`kind.${kind}`]) })
      .locator('p');
  const drop = async (kind: string, target: string) => {
    const response = page.waitForResponse(
      (result) =>
        result.url().endsWith('/api/graphql') && result.request().postDataJSON()?.operationName === 'dropTreeItem'
    );
    await type(kind).dragTo(item(target));
    expect((await (await response).json()).errors).toBeUndefined();
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
    await apply('none');
    await apply('placement');
    await expect.poll(() => color(sampleName('ceilingLight'))).toBe(red);
    await apply('circuits');
    await expect.poll(() => color(sampleName('ceilingLight'))).not.toBe(red);
    const rail = item(sampleName('row')).locator('xpath=ancestor::li[1]');
    const healthyRccbLabel = await rail
      .locator('img[alt*="entity=ModularDevice"]')
      .first()
      .locator('../..')
      .locator('p')
      .innerText();
    await item(messages['explorer.circuits']).locator('../..').getByRole('button').click();
    await page
      .getByRole('button', { name: `${messages['action.create']} ${messages['class.FinalCircuit']}`, exact: true })
      .click();
    await expand();
    await item(messages['new.FinalCircuit']).click();
    await name.fill('C-UNPROTECTED');
    await name.press('Tab');
    await expect.poll(() => color('C-UNPROTECTED')).toBe(red);
    await drop('OUTLET', sampleName('livingRoom'));
    const living = item(sampleName('livingRoom')).locator('xpath=ancestor::li[1]');
    await living
      .locator('p')
      .filter({ hasText: new RegExp(`^${escape(messages['kind.OUTLET'])}$`) })
      .click();
    await name.fill('UNCONNECTED');
    await name.press('Tab');
    await expect.poll(() => color('UNCONNECTED')).toBe(red);
    await drop('MCB', sampleName('row'));
    await rail.locator('img[alt*="entity=ModularDevice"]').last().locator('../..').locator('p').click();
    await name.fill('SPARE');
    await name.press('Tab');
    const spareLabel = `SPARE - ${messages['kind.MCB']}`;
    await expect.poll(() => color(spareLabel)).toBe(red);
    expect(await color(healthyRccbLabel)).not.toBe(red);
    expect(await color(messages['kind.MCB'])).not.toBe(red);
    for (const parent of [
      sampleName('livingRoom'),
      sampleName('groundFloor'),
      messages['class.Dwelling'],
      messages['explorer.circuits'],
      messages['explorer.gtl'],
      sampleName('mainBoard'),
      sampleName('row'),
    ]) {
      await expect.poll(() => color(parent)).toBe(red);
    }
    await apply('placement');
    await expect.poll(() => color('C-UNPROTECTED')).not.toBe(red);
    await expect.poll(() => color(spareLabel)).not.toBe(red);
    await expect.poll(() => color(sampleName('ceilingLight'))).toBe(red);
    await apply('circuits');
    await expect.poll(() => color(sampleName('ceilingLight'))).not.toBe(red);
    await page.getByTestId(`${sampleName('livingRoom')}-toggle`).click();
    await expect(item('UNCONNECTED')).not.toBeVisible();
    await expect.poll(() => color(sampleName('livingRoom'))).toBe(red);
    await page.getByTestId(`${messages['explorer.gtl']}-toggle`).click();
    await expect(item('C-UNPROTECTED')).not.toBeVisible();
    await expect.poll(() => color(messages['explorer.gtl'])).toBe(red);
    const other = await page.context().newPage();
    try {
      await other.goto(page.url());
      await other.getByTestId('tree-filter-menu-icon').click();
      for (const key of ['explorer.highlightUnplaced', 'explorer.highlightIncompleteCircuits']) {
        await expect(other.getByRole('checkbox', { name: messages[key], exact: true })).not.toBeChecked();
      }
    } finally {
      await other.close();
    }
    await page.reload();
    await expand();
    await apply('none');
    await expect.poll(() => color('C-UNPROTECTED')).not.toBe(red);
    await expect.poll(() => color('UNCONNECTED')).not.toBe(red);
    await expect.poll(() => color(spareLabel)).not.toBe(red);
    await apply('circuits');
    await expect.poll(() => color('C-UNPROTECTED')).toBe(red);
    await expect.poll(() => color('UNCONNECTED')).toBe(red);
    await expect.poll(() => color(spareLabel)).toBe(red);
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
      expect((await deletion.json()).data.deleteProject.__typename).toMatch(/Success/);
    }
  }
});
