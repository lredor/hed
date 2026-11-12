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
import { sampleName, samplePattern } from './sample';
import { randomUUID } from 'node:crypto';

test('import a storey plan, place existing equipment, move it and reopen the saved layout', async ({
  page,
  request,
}) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(10000);
  let projectId: string | undefined;
  const projectName = 'E2E floor plan ' + randomUUID(),
    errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  const item = (name: string | RegExp) =>
    page
      .locator('p')
      .filter({ hasText: typeof name === 'string' ? new RegExp('^' + name + '$') : name })
      .first();
  const expand = async () => {
    await item(projectName).locator('../..').getByRole('button').click();
    await page
      .getByRole('menuitem', { name: /^(Expand all|Développer tout)$/ })
      .last()
      .click();
  };
  const background = page.getByTestId('floor-plan-background'),
    equipment = page.locator('[data-testid^="floor-plan-equipment-"]');
  const device = (label: string) =>
    equipment.filter({ has: page.locator('span', { hasText: new RegExp('^' + label + '$') }) });
  const position = (label: string) =>
    device(label).evaluate((element) => {
      const matrix = new DOMMatrix(getComputedStyle(element.closest('.react-flow__node')!).transform);
      return { x: matrix.e, y: matrix.f };
    });
  const svg = (color: string) =>
    Buffer.from(
      `<svg xmlns="http://www.w3.org/2000/svg" width="1200" height="700" viewBox="0 0 1200 700"><rect width="1200" height="700" fill="${color}"/><g fill="none" stroke="#334155" stroke-width="8"><rect x="60" y="60" width="1080" height="580"/><path d="M600 60v580M60 380h540"/></g><text x="120" y="150" font-size="30">Living room</text><text x="700" y="150" font-size="30">Garage</text></svg>`
    );
  const upload = async (name: string, color: string) => {
    await background
      .locator('input[type="file"]')
      .setInputFiles({ name, mimeType: 'image/svg+xml', buffer: svg(color) });
    await expect(background.locator('.hed-floor-plan-image')).toBeVisible();
    await expect(background.getByRole('button', { name: /^(Replace floor plan|Remplacer le plan)$/ })).toBeEnabled();
    await expect
      .poll(() =>
        background.locator('.hed-floor-plan-image').evaluate((element) => (element as HTMLImageElement).naturalWidth)
      )
      .toBe(1200);
  };
  const drop = async (source: ReturnType<typeof item>, x: number, y: number) => {
    const box = await background.boundingBox();
    await source.dragTo(background, { targetPosition: { x: box!.width * x, y: box!.height * y } });
  };
  try {
    await page.goto('/');
    await page.getByRole('link', { name: /^(Sample dwelling|Logement d’exemple)$/ }).click();
    await page.getByRole('textbox', { name: /^(Enter project name|Saisir le nom du projet)$/ }).fill(projectName);
    await page.getByRole('button', { name: /^(Create Project|Créer le projet)$/ }).click();
    await page.waitForURL(/\/projects\/[^/]+\/edit/);
    projectId = page.url().match(/\/projects\/([^/]+)/)?.[1];
    await item(projectName).waitFor();
    await expand();
    await item(sampleName('groundFloor')).locator('../..').getByRole('button').click();
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
    await dialog.getByRole('textbox').fill('Ground floor plan');
    await dialog.getByRole('button', { name: /^(Create|Créer)$/ }).click();
    await expect(background).toBeVisible();
    await expect(equipment).toHaveCount(0);
    await upload('ground-floor.svg', '#f8fafc');
    const imageURL = await background.locator('.hed-floor-plan-image').getAttribute('src');
    expect((await request.get(imageURL!)).ok()).toBe(true);
    await drop(item(sampleName('sofaOutlet')), 0.28, 0.48);
    await expect(device(sampleName('sofaOutlet'))).toBeVisible();
    const placed = await position(sampleName('sofaOutlet'));
    expect(Math.abs(placed.x - 1200 * 0.28)).toBeLessThan(65);
    expect(Math.abs(placed.y - 748 * 0.48)).toBeLessThan(65);
    await drop(item(sampleName('doubleSwitch')), 0.63, 0.42);
    await expect(equipment).toHaveCount(2);
    const labels = background.getByRole('checkbox', { name: /^(Show equipment labels|Afficher les libellés)$/ });
    await expect(labels).not.toBeChecked();
    await expect(device(sampleName('sofaOutlet')).locator('.hed-floor-equipment-label')).toBeHidden();
    const dimensions = (label = sampleName('sofaOutlet')) =>
      device(label).evaluate((element) => {
        const box = element.closest('.react-flow__node') as HTMLElement;
        return { width: box.style.width, height: box.style.height };
      });
    await expect.poll(dimensions).toEqual({ width: '24px', height: '24px' });
    const beforeLabels = await position(sampleName('sofaOutlet'));
    await labels.check();
    await expect(device(sampleName('sofaOutlet')).locator('.hed-floor-equipment-label')).toBeVisible();
    const bounds = await device(sampleName('sofaOutlet')).boundingBox(),
      labelBounds = await device(sampleName('sofaOutlet')).locator('.hed-floor-equipment-label').boundingBox();
    expect(labelBounds!.y).toBeGreaterThanOrEqual(bounds!.y + bounds!.height);

    await expect.poll(dimensions).toEqual({ width: '24px', height: '24px' });
    expect(await position(sampleName('sofaOutlet'))).toEqual(beforeLabels);
    await page.reload();
    await expect(labels).toBeChecked();
    await expect(device(sampleName('sofaOutlet')).locator('.hed-floor-equipment-label')).toBeVisible();
    await expect.poll(() => position(sampleName('sofaOutlet'))).toEqual(beforeLabels);
    await labels.uncheck();
    await expect(device(sampleName('sofaOutlet')).locator('.hed-floor-equipment-label')).toBeHidden();
    await expand();
    const ids = await equipment.evaluateAll((elements) =>
      elements.map((element) => element.getAttribute('data-semantic-id')).sort()
    );
    await drop(item(sampleName('sofaOutlet')), 0.5, 0.5);
    await expect(equipment).toHaveCount(2);
    await expect(page.locator('img[alt*="entity=Outlet"]')).toHaveCount(1);
    await drop(item(sampleName('bedroomLight')), 0.7, 0.7);
    await expect(page.getByText(/^(Drop existing equipment|Déposez un équipement existant)/).first()).toBeVisible();
    await expect(equipment).toHaveCount(2);
    const type = page
      .locator('img[alt*="entity=DeviceType"]')
      .locator('xpath=ancestor::li[1]')
      .filter({ has: page.locator('p').filter({ hasText: /^(Outlet|Prise de courant)$/ }) })
      .locator('p')
      .first();
    await drop(type, 0.7, 0.7);
    await expect(equipment).toHaveCount(2);
    const before = await position(sampleName('sofaOutlet')),
      box = await device(sampleName('sofaOutlet')).boundingBox();
    const saved = page.waitForResponse(
      (response) =>
        response.url().endsWith('/api/graphql') && (response.request().postData() ?? '').includes('layoutDiagram')
    );
    await page.mouse.move(box!.x + box!.width / 2, box!.y + box!.height / 2);
    await page.mouse.down();
    await page.mouse.move(box!.x + box!.width / 2 + 70, box!.y + box!.height / 2 + 45, { steps: 15 });
    await page.mouse.up();
    await saved;
    await expect
      .poll(async () => Math.abs((await position(sampleName('sofaOutlet'))).x - before.x))
      .toBeGreaterThan(20);
    const moved = await position(sampleName('sofaOutlet'));
    await page.reload();
    await expect(equipment).toHaveCount(2);
    await expect.poll(() => position(sampleName('sofaOutlet'))).toEqual(moved);
    await expect(background.locator('.hed-floor-plan-image')).toHaveAttribute('src', imageURL!);

    expect(
      await equipment.evaluateAll((elements) =>
        elements.map((element) => element.getAttribute('data-semantic-id')).sort()
      )
    ).toEqual(ids);
    await upload('replacement.svg', '#e0f2fe');
    await expect(background.locator('.hed-floor-plan-image')).not.toHaveAttribute('src', imageURL!);
    await expect.poll(() => position(sampleName('sofaOutlet'))).toEqual(moved);
    await background.getByRole('button', { name: /^(Remove background|Retirer le fond)$/ }).click();
    await expect(background.locator('.hed-floor-plan-image')).toHaveCount(0);
    await expect(equipment).toHaveCount(2);
    await expect.poll(() => position(sampleName('sofaOutlet'))).toEqual(moved);
    await page.reload();
    await expect(equipment).toHaveCount(2);
    await expect(background.locator('.hed-floor-plan-image')).toHaveCount(0);
    await upload('final.svg', '#f8fafc');
    await device(sampleName('doubleSwitch')).click({ button: 'right' });
    await page
      .getByRole('button', { name: /^(Remove from plan|Retirer du plan)$/, exact: true })
      .last()
      .click();
    await expect(equipment).toHaveCount(1);
    await expand();
    await expect(item(sampleName('doubleSwitch'))).toBeVisible();
    await labels.check();
    await drop(item(sampleName('doubleSwitch')), 0.65, 0.6);
    await expect(equipment).toHaveCount(2);
    await expect(device(sampleName('doubleSwitch')).locator('.hed-floor-equipment-label')).toBeVisible();
    await device(sampleName('sofaOutlet')).click();
    await page
      .getByTestId(/^(Room|Pièce)$/)
      .getByRole('combobox')
      .click();
    await page
      .getByRole('option')
      .filter({ hasText: samplePattern('bedroom') })
      .click();
    await expect(equipment).toHaveCount(1);
    await page.reload();
    await expect(equipment).toHaveCount(1);
    await expect(device(sampleName('doubleSwitch'))).toBeVisible();
    await labels.check();
    const firstPlanURL = page.url(),
      sharedImage = await background.locator('.hed-floor-plan-image').getAttribute('src');
    await expand();
    await item(sampleName('groundFloor')).locator('../..').getByRole('button').click();
    await page
      .getByRole('menuitem', { name: /^(New representation|Nouvelle représentation)$/ })
      .last()
      .click();
    const secondDialog = page.getByRole('dialog');
    await secondDialog.getByRole('combobox').click();
    await page
      .getByRole('option')
      .filter({ hasText: /^(Floor Plan|Plan d’implantation)(?: \1)?$/ })
      .click();
    await secondDialog.getByRole('textbox').fill('Independent layout');
    await secondDialog.getByRole('button', { name: /^(Create|Créer)$/ }).click();
    await expect(equipment).toHaveCount(0);
    await expect(background.locator('.hed-floor-plan-image')).toHaveAttribute('src', sharedImage!);
    await expect(labels).not.toBeChecked();
    await labels.check();
    await expand();
    await drop(item(sampleName('doubleSwitch')), 0.42, 0.54);
    await expect(equipment).toHaveCount(1);
    const secondPlanURL = page.url(),
      secondPlaced = await position(sampleName('doubleSwitch'));
    await page.goto(firstPlanURL);
    await expect(equipment).toHaveCount(1);
    await expect(device(sampleName('doubleSwitch'))).toBeVisible();
    await expect(labels).toBeChecked();
    const firstPlaced = await position(sampleName('doubleSwitch'));
    await expand();
    await item(sampleName('groundFloor')).click();
    const planWidth = page.getByRole('textbox', { name: /^(Plan width|Largeur du plan)$/, exact: true }),
      planHeight = page.getByRole('textbox', { name: /^(Plan height|Hauteur du plan)$/, exact: true });
    await expect(planWidth).toHaveValue('1200');
    await expect(planHeight).toHaveValue('700');
    await expect(planHeight).not.toBeEditable();
    await planWidth.fill('2400');
    await planWidth.press('Tab');
    await expect(planHeight).toHaveValue('1400');
    const doubled = (point: { x: number; y: number }) => ({
      x: 2 * (point.x + 12) - 12,
      y: 48 + 2 * (point.y + 12 - 48) - 12,
    });
    const expectDoubled = async (point: { x: number; y: number }) => {
      const expected = doubled(point);
      await expect
        .poll(async () => {
          const actual = await position(sampleName('doubleSwitch'));
          return Math.abs(actual.x - expected.x) + Math.abs(actual.y - expected.y);
        })
        .toBeLessThan(0.1);
    };
    await expectDoubled(firstPlaced);
    await expect.poll(() => dimensions(sampleName('doubleSwitch'))).toEqual({ width: '24px', height: '24px' });
    await page.reload();
    await expectDoubled(firstPlaced);
    await page.goto(secondPlanURL);
    await expectDoubled(secondPlaced);
    await page.reload();
    await expectDoubled(secondPlaced);
    await upload('wide-plan.svg', '#e0f2fe');
    await expand();
    await item(sampleName('groundFloor')).click();
    await expect(planWidth).toHaveValue('2400');
    await expect(planHeight).toHaveValue('1400');
    await planWidth.fill('0');
    await planWidth.press('Tab');
    await expect(
      page.getByText(/^(Choose a positive whole width|Choisissez une largeur entière positive)/).first()
    ).toBeVisible();
    await page.reload();
    await expand();
    await item(sampleName('groundFloor')).click();
    await expect(planWidth).toHaveValue('2400');
    await expect(planHeight).toHaveValue('1400');
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
      const result = await deletion.json();
      expect(result.errors).toBeUndefined();
      expect(result.data.deleteProject.__typename).toMatch(/Success/);
    }
  }
});
