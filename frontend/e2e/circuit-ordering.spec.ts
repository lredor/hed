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

test('reorder final and distribution circuits with commands, peer gaps and append drops, then reopen', async ({
  page,
  request,
}) => {
  test.setTimeout(150000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E circuit order ${randomUUID()}`;
  let projectId: string | undefined;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  const item = (label: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp(`^${escape(label)}$`) })
      .first();
  const more = (label: string) => item(label).locator('../..').getByRole('button').click();
  const closePalette = () => page.getByRole('tooltip').getByRole('button', { name: 'close', exact: true }).click();
  const labels = () =>
    page
      .locator('img[alt*="entity=FinalCircuit"], img[alt*="entity=DistributionCircuit"]')
      .locator('../..')
      .locator('p');
  const expand = async () => {
    await more(projectName);
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await expect(item(sampleName('groundFloorLighting'))).toBeVisible();
  };
  const move = async (label: string, direction: 'moveUp' | 'moveDown') => {
    await more(label);
    await page.getByRole('button', { name: messages[`action.${direction}`], exact: true }).click();
  };
  const drop = async (source: string, destination: string, before: boolean) => {
    await item(source).click();
    const target = before
      ? page.getByTestId(`${destination}-fullrow`).locator('xpath=preceding-sibling::div[1]')
      : item(destination);
    const response = page.waitForResponse(
      (result) =>
        result.url().endsWith('/api/graphql') && result.request().postDataJSON()?.operationName === 'dropTreeItem'
    );
    // Native gap targets are only two pixels high; aim at their top-left interior to avoid the next row's content.
    await item(source).dragTo(target, before ? { targetPosition: { x: 8, y: 0.25 } } : {});
    const body = await (await response).json();
    expect(body.errors).toBeUndefined();
    expect(body.data.dropTreeItem.__typename).toMatch(/Success/);
  };
  const [lighting, outlets, shutters, bedroom, feeder] = [
    'groundFloorLighting',
    'livingRoomOutlets',
    'rollerShutters',
    'bedroomLighting',
    'garageBoardSupply',
  ].map(sampleName);
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
    await expect(labels()).toHaveText([lighting, outlets, shutters, bedroom, feeder]);
    const circuitId = await item(lighting)
      .locator('xpath=ancestor::*[@data-treeitemid][1]')
      .getAttribute('data-treeitemid');
    await more(lighting);
    await expect(page.getByRole('button', { name: messages['action.moveUp'], exact: true })).toHaveCount(0);
    await closePalette();
    await more(feeder);
    await expect(page.getByRole('button', { name: messages['action.moveDown'], exact: true })).toHaveCount(0);
    await closePalette();
    await move(lighting, 'moveDown');
    await expect(labels()).toHaveText([outlets, lighting, shutters, bedroom, feeder]);
    await move(feeder, 'moveUp');
    await expect(labels()).toHaveText([outlets, lighting, shutters, feeder, bedroom]);
    await drop(bedroom, outlets, true);
    await expect(labels()).toHaveText([bedroom, outlets, lighting, shutters, feeder]);
    await drop(outlets, feeder, true);
    await expect(labels()).toHaveText([bedroom, lighting, shutters, outlets, feeder]);
    // Appending to the virtual group is also available when the final peer is collapsed or has children.
    await drop(lighting, messages['explorer.circuits'], false);
    await expect(labels()).toHaveText([bedroom, shutters, outlets, feeder, lighting]);
    await expect(item(lighting).locator('xpath=ancestor::*[@data-treeitemid][1]')).toHaveAttribute(
      'data-treeitemid',
      circuitId!
    );
    await page.reload();
    await expand();
    await expect(labels()).toHaveText([bedroom, shutters, outlets, feeder, lighting]);
    await expect(item(lighting).locator('xpath=ancestor::*[@data-treeitemid][1]')).toHaveAttribute(
      'data-treeitemid',
      circuitId!
    );
    await item(lighting).click();
    await expect(page.getByTestId(messages['feature.protectiveDevice']).getByRole('combobox')).toContainText('Q1');
    await item(sampleName('ceilingLight')).click();
    await expect(page.getByTestId(messages['feature.circuit']).getByRole('combobox')).toContainText(lighting);
    expect(errors).toEqual([]);
  } finally {
    if (projectId) {
      const check = await request.post('/api/graphql', {
        data: {
          query: 'query($projectId:ID!){viewer{project(projectId:$projectId){name}}}',
          variables: { projectId },
        },
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
