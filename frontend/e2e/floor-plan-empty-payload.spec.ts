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
import { samplePattern } from './sample';

test('recover an explorer drag whose browser payload is empty at drop time and retain the placed equipment', async ({
  page,
  request,
}) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(10000);
  const projectName = `E2E lost drag payload ${randomUUID()}`;
  let projectId: string | undefined;
  const item = (name: string | RegExp) =>
    page
      .locator('p')
      .filter({ hasText: typeof name === 'string' ? new RegExp(`^${name}$`) : name })
      .first();
  const background = page.getByTestId('floor-plan-background');
  const expand = async () => {
    await item(projectName).locator('../..').getByRole('button').click();
    await page
      .getByRole('menuitem', { name: /^(Expand all|Développer tout)$/ })
      .last()
      .click();
    await expect(item(samplePattern('sofaOutlet'))).toBeVisible();
  };
  try {
    await page.goto('/new/project?templateId=hed-example');
    const name = page.getByRole('main').getByRole('textbox').first();
    await expect(name).toHaveValue(/^(Sample dwelling|Logement d’exemple)$/);
    await name.fill(projectName);
    await expect(name).toHaveValue(projectName);
    await page.getByRole('button', { name: /^(Create Project|Créer le projet)$/, exact: true }).click();
    await page.waitForURL(/\/projects\//);
    projectId = page.url().match(/\/projects\/([^/]+)\//)?.[1];
    expect(projectId).toBeTruthy();
    await item(projectName).waitFor();
    await expand();
    await item(samplePattern('groundFloor')).locator('../..').getByRole('button').click();
    await page
      .getByRole('menuitem', { name: /^(New representation|Nouvelle représentation)$/ })
      .last()
      .click();
    const dialog = page.getByRole('dialog');
    await dialog.getByRole('combobox').click();
    await page
      .getByRole('option')
      .filter({ hasText: /^(Floor Plan|Plan d’implantation)(?: \1)?$/ })
      .click();
    await dialog.getByRole('textbox').fill('Lost payload plan');
    await dialog.getByRole('button', { name: /^(Create|Créer)$/, exact: true }).click();
    await background.waitFor();
    for (const [index, key] of ['doubleSwitch', 'sofaOutlet'].entries()) {
      const source = item(samplePattern(key));
      // Keep a real mouse drag and tree dragstart; emulate only the observed loss of data at the destination.
      await page.evaluate(() =>
        document.addEventListener(
          'drop',
          (event) => {
            document.documentElement.dataset.lostSources = event.dataTransfer?.getData('sirius-component/drag/sources');
            Object.defineProperty(event, 'dataTransfer', { value: new DataTransfer() });
          },
          { capture: true, once: true }
        )
      );
      const box = await background.boundingBox();
      const response = page.waitForResponse(
        (r) => r.url().endsWith('/api/graphql') && r.request().postDataJSON()?.operationName === 'dropOnDiagram'
      );
      await source.dragTo(background, {
        targetPosition: { x: box!.width * (0.3 + index * 0.2), y: box!.height * 0.5 },
      });
      const result = await (await response).json();
      expect(result.errors).toBeUndefined();
      expect(result.data.dropOnDiagram.__typename).toMatch(/Success/);
      await expect(page.locator('[data-testid^="floor-plan-equipment-"]')).toHaveCount(index + 1);
      expect(await page.locator('html').getAttribute('data-lost-sources')).toMatch(/^\[".+/);
    }
    await page.reload();
    await expect(page.locator('[data-testid^="floor-plan-equipment-"]')).toHaveCount(2);
    for (const key of ['doubleSwitch', 'sofaOutlet']) {
      await expect(
        page
          .locator('[data-testid^="floor-plan-equipment-"]')
          .filter({ has: page.locator('span', { hasText: samplePattern(key) }) })
      ).toHaveCount(1);
    }
  } finally {
    if (projectId) {
      const check = await request.post('/api/graphql', {
        data: { query: 'query($projectId:ID!){viewer{project(projectId:$projectId){name}}}', variables: { projectId } },
      });
      expect((await check.json()).data?.viewer?.project?.name).toBe(projectName);
      const deleted = await request.post('/api/graphql', {
        data: {
          query: 'mutation($input:DeleteProjectInput!){deleteProject(input:$input){__typename}}',
          variables: { input: { id: randomUUID(), projectId } },
        },
      });
      expect((await deleted.json()).data.deleteProject.__typename).toMatch(/Success/);
    }
  }
});
