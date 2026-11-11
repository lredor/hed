/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { test, expect } from '@playwright/test';
import { randomUUID } from 'node:crypto';

test('import PNG and JPEG plans and retain the background when an invalid image is rejected', async ({
  page,
  request,
}) => {
  test.setTimeout(60000);
  let projectId: string | undefined;
  const projectName = 'E2E plan images ' + randomUUID();
  const item = (name: string) =>
    page
      .locator('p')
      .filter({ hasText: new RegExp('^' + name + '$') })
      .first();
  try {
    await page.goto('/');
    await page.getByRole('link', { name: /^(Sample dwelling|Logement d’exemple)$/ }).click();
    await page.getByRole('textbox', { name: /^(Enter project name|Saisir le nom du projet)$/ }).fill(projectName);
    await page.getByRole('button', { name: /^(Create Project|Créer le projet)$/ }).click();
    await page.waitForURL(/\/projects\/[^/]+\/edit/);
    projectId = page.url().match(/\/projects\/([^/]+)/)?.[1];
    await item(projectName).waitFor();
    await item(projectName).locator('../..').getByRole('button').click();
    await page
      .getByRole('menuitem', { name: /^(Expand all|Développer tout)$/ })
      .last()
      .click();
    await item('Ground floor').locator('../..').getByRole('button').click();
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
    await dialog.getByRole('textbox').fill('Image formats');
    await dialog.getByRole('button', { name: /^(Create|Créer)$/ }).click();
    const background = page.getByTestId('floor-plan-background'),
      image = background.locator('.hed-floor-plan-image'),
      file = background.locator('input[type="file"]');
    await expect(background).toBeVisible();
    let previousURL: string | null = null;
    for (const type of ['image/png', 'image/jpeg']) {
      const data = await page.evaluate((type) => {
        const canvas = document.createElement('canvas');
        canvas.width = 500;
        canvas.height = 300;
        const context = canvas.getContext('2d')!;
        context.fillStyle = '#f8fafc';
        context.fillRect(0, 0, 500, 300);
        context.strokeStyle = '#334155';
        context.lineWidth = 4;
        context.strokeRect(20, 20, 460, 260);
        context.fillStyle = '#334155';
        context.font = '20px sans-serif';
        context.fillText('Test floor plan', 40, 60);
        return canvas.toDataURL(type).split(',')[1];
      }, type);
      await file.setInputFiles({
        name: type === 'image/png' ? 'plan.png' : 'plan.jpg',
        mimeType: type,
        buffer: Buffer.from(data, 'base64'),
      });
      await expect(image).toBeVisible();
      await expect(background.getByRole('button', { name: /^(Replace floor plan|Remplacer le plan)$/ })).toBeEnabled();
      if (previousURL) await expect(image).not.toHaveAttribute('src', previousURL);
      await expect.poll(() => image.evaluate((element) => (element as HTMLImageElement).naturalWidth)).toBe(500);
      previousURL = await image.getAttribute('src');
    }
    await file.setInputFiles({
      name: 'broken.svg',
      mimeType: 'image/svg+xml',
      buffer: Buffer.from('this is not an SVG image'),
    });
    await expect(page.getByText(/^(Choose a valid PNG|Choisissez une image PNG)/).first()).toBeVisible();
    await expect(image).toHaveAttribute('src', previousURL!);
    await page.reload();
    await expect(image).toHaveAttribute('src', previousURL!);
    await expect.poll(() => image.evaluate((element) => (element as HTMLImageElement).naturalWidth)).toBe(500);
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
      const result = await deletion.json();
      expect(result.errors).toBeUndefined();
      expect(result.data.deleteProject.__typename).toMatch(/Success/);
    }
  }
});
