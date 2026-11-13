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

test('create named branches, associate equipment through Explorer and retain circuit and room relationships', async ({
  page,
  request,
}) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(10000);
  let projectId: string | undefined, diagram: any;
  const projectName = 'E2E circuit branches ' + randomUUID(),
    errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  const capture = (value: any) => {
    if (!value || typeof value !== 'object') return;
    if (value.targetObjectId && Array.isArray(value.nodes) && Array.isArray(value.edges)) diagram = value;
    else Object.values(value).forEach(capture);
  };
  page.on('websocket', (socket) =>
    socket.on('framereceived', (frame) => {
      try {
        capture(JSON.parse(frame.payload.toString()));
      } catch {}
    })
  );
  const flat = (nodes: any[] = diagram?.nodes ?? []): any[] =>
    nodes.flatMap((node) => [node, ...flat(node.childNodes ?? []), ...flat(node.borderNodes ?? [])]);
  const semantic = (label: string) => flat().find((node) => node.targetObjectLabel === label);
  const linked = (from: string, to: string) => {
    const source = semantic(from),
      target = semantic(to);
    return !!diagram?.edges.some((edge: any) =>
      source && target
        ? edge.sourceId === source.id && edge.targetId === target.id
        : source
        ? edge.sourceId === source.id && edge.centerLabel?.text === to
        : target
        ? edge.targetId === target.id && edge.centerLabel?.text === from
        : false
    );
  };
  const item = (label: string | RegExp) =>
    page
      .locator('[data-treeitemid] p')
      .filter({ hasText: typeof label === 'string' ? new RegExp('^' + label + '$') : label })
      .first();
  const treeBranch = (label: string) => item(label).locator('xpath=ancestor::li[1]');
  const expand = async () => {
    await item(projectName).locator('../..').getByRole('button').click();
    await page
      .getByRole('menuitem', { name: /^(Expand all|Développer tout)$/ })
      .last()
      .click();
  };
  const name = () => page.getByRole('textbox', { name: /^(Name|Nom)$/, exact: true });
  const supply = () => page.getByTestId(/^(Supply circuit|Circuit d’alimentation)$/).getByRole('combobox');
  const branchProperty = () => page.getByTestId(/^(Branch|Branche)$/).getByRole('combobox');
  const createBranch = async (label: string) => {
    await expand();
    await item(sampleName('livingRoomOutlets')).locator('../..').getByRole('button').click();
    await page.getByRole('button', { name: /^(Create Branch|Créer Branche)$/ }).click();
    await expand();
    await item(/^(New branch|Nouvelle branche)$/).click();
    await name().fill(label);
    await page.keyboard.press('Tab');
    await expect(item(label)).toBeVisible();
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
    await item(/^(Dwelling|Logement)$/)
      .locator('../..')
      .getByRole('button')
      .click();
    await page
      .getByRole('menuitem', { name: /^(New representation|Nouvelle représentation)$/ })
      .last()
      .click();
    const dialog = page.getByRole('dialog');
    await dialog.getByRole('combobox').click();
    await page.getByRole('option', { name: /^(Circuit Diagram|Schéma des circuits)$/ }).click();
    await dialog.getByRole('textbox').fill('Branch circuit diagram');
    await dialog.getByRole('button', { name: /^(Create|Créer)$/ }).click();
    await expect.poll(() => flat().length).toBe(27);
    await createBranch('Window side');
    await createBranch('Door side');
    await createBranch('Desk side');
    await expect.poll(() => flat().length).toBe(28);
    await expect.poll(() => linked(sampleName('livingRoomOutlets'), 'Window side')).toBe(true);
    expect(semantic('Window side')).toBeUndefined();
    const junction = semantic(sampleName('livingRoomOutlets'));
    expect(junction.defaultWidth).toBe(3);
    expect(junction.defaultHeight).toBe(3);
    expect(junction.insideLabel).toBeNull();
    expect(junction.outsideLabels).toEqual([]);
    expect(junction.style.background).toBe('#000000');
    await expect.poll(() => linked('Q2', sampleName('livingRoomOutlets'))).toBe(true);
    const junctionNode = page.locator(`.react-flow__node[data-id="${junction.id}"]`);
    await expect.poll(() => junctionNode.evaluate((element) => parseFloat(getComputedStyle(element).width))).toBe(3);
    const room = treeBranch(sampleName('livingRoom'));
    const originalOutlet = room
      .locator('p')
      .filter({ hasText: samplePattern('sofaOutlet') })
      .first();
    // Keep both drag endpoints visible in the scrollable explorer.
    for (const label of [
      sampleName('firstFloor'),
      sampleName('mainBoard'),
      sampleName('garageBoard'),
      sampleName('serviceConnection'),
    ])
      await page.getByTestId(label + '-toggle').click();
    await originalOutlet.click();
    await originalOutlet.dragTo(item('Window side'));
    await expand();
    const alias = () =>
      treeBranch('Window side')
        .locator('p')
        .filter({ hasText: samplePattern('sofaOutlet') })
        .first();
    await expect(alias()).toBeVisible();
    await expect(originalOutlet).toBeVisible();
    await alias().click();
    await expect(name()).toHaveValue(sampleName('sofaOutlet'));
    await expect(branchProperty()).toContainText('Window side');
    await expect(supply()).toContainText(sampleName('livingRoomOutlets'));
    await expect.poll(() => linked('Window side', sampleName('sofaOutlet'))).toBe(true);
    expect(
      diagram.edges.find(
        (edge: any) => edge.sourceId === junction.id && edge.targetId === semantic(sampleName('sofaOutlet')).id
      ).centerLabel.text
    ).toBe('Window side');
    // Renaming the linked tree occurrence edits the original equipment, including its room occurrence.
    await alias().locator('../..').getByRole('button').click();
    await page
      .getByRole('menuitem', { name: /^(Rename|Renommer)$/, exact: true })
      .last()
      .click();
    const rename = page.getByTestId('name-edit').getByRole('textbox');
    await expect(rename).toHaveValue(sampleName('sofaOutlet'));
    await rename.fill('Window outlet');
    await rename.press('Enter');
    await expect(room.locator('p').filter({ hasText: /^Window outlet$/ })).toBeVisible();
    await expect.poll(() => linked('Window side', 'Window outlet')).toBe(true);
    await item('Door side').locator('../..').getByRole('button').click();
    await page.getByRole('button', { name: /^(Create Outlet|Créer Prise de courant)$/ }).click();
    await expand();
    await treeBranch('Door side')
      .locator('p')
      .filter({ hasText: /^(Outlet|Prise de courant)$/ })
      .first()
      .click();
    await name().fill('Door outlet');
    await page.keyboard.press('Tab');
    await expect(branchProperty()).toContainText('Door side');
    await expect(supply()).toContainText(sampleName('livingRoomOutlets'));
    await page
      .getByTestId(/^(Room|Pièce)$/)
      .getByRole('combobox')
      .click();
    await page
      .getByRole('option')
      .filter({ hasText: samplePattern('garage') })
      .click();
    await expect.poll(() => linked('Door side', 'Door outlet')).toBe(true);
    // Two members of the same branch form a chain, rather than parallel edges from the junction.
    await room
      .locator('p')
      .filter({ hasText: /^Window outlet$/ })
      .click();
    await branchProperty().click();
    await page
      .getByRole('option')
      .filter({ hasText: /^Door side$/ })
      .click();
    await expect.poll(() => linked('Door outlet', 'Window outlet')).toBe(true);
    expect(
      diagram.edges.find(
        (edge: any) => edge.sourceId === semantic('Door outlet').id && edge.targetId === semantic('Window outlet').id
      ).centerLabel.text
    ).toBe('Door side');
    expect(linked(sampleName('livingRoomOutlets'), 'Window outlet')).toBe(false);
    await branchProperty().click();
    await page
      .getByRole('option')
      .filter({ hasText: /^Window side$/ })
      .click();
    await expect.poll(() => linked('Window side', 'Window outlet')).toBe(true);
    await item('Window side').click();
    await supply().click();
    await page
      .getByRole('option')
      .filter({ hasText: samplePattern('groundFloorLighting') })
      .click();
    await expand();
    await expect(
      treeBranch(sampleName('groundFloorLighting'))
        .locator('p')
        .filter({ hasText: /^Window side$/ })
    ).toBeVisible();
    await expect.poll(() => linked(sampleName('groundFloorLighting'), 'Window side')).toBe(true);
    expect(linked(sampleName('livingRoomOutlets'), 'Window side')).toBe(false);
    await treeBranch('Window side')
      .locator('p')
      .filter({ hasText: /^Window outlet$/ })
      .click();
    await expect(supply()).toContainText(sampleName('groundFloorLighting'));
    await expect(page.getByTestId(/^(Room|Pièce)$/).getByRole('combobox')).toContainText(sampleName('livingRoom'));
    await page.screenshot({ path: '../.cache/circuit-branches-assigned.png' });
    const ids = flat()
      .map((node) => node.targetObjectId)
      .sort();
    await page.reload();
    await expect.poll(() => flat().length).toBe(30);
    await expect.poll(() => linked(sampleName('groundFloorLighting'), 'Window side')).toBe(true);
    expect(
      flat()
        .map((node) => node.targetObjectId)
        .sort()
    ).toEqual(ids);
    await expand();
    await treeBranch('Window side')
      .locator('p')
      .filter({ hasText: /^Window outlet$/ })
      .click();
    await supply().click();
    await expect(page.getByRole('option').filter({ hasText: samplePattern('livingRoomOutlets') })).toHaveCount(0);
    await page.keyboard.press('Escape');
    await branchProperty().click();
    await page
      .getByRole('option')
      .filter({ hasText: /^Desk side$/ })
      .click();
    await expect(branchProperty()).toContainText('Desk side');
    await expect.poll(() => linked('Desk side', 'Window outlet')).toBe(true);
    await expect(
      treeBranch('Window side')
        .locator('p')
        .filter({ hasText: /^Window outlet$/ })
    ).toHaveCount(0);
    await expect(room.locator('p').filter({ hasText: /^Window outlet$/ })).toBeVisible();
    await item('Door side').locator('../..').getByRole('button').click();
    await page
      .getByRole('menuitem', { name: /^(Delete|Supprimer)$/, exact: true })
      .last()
      .click();
    await page
      .getByRole('dialog')
      .getByRole('button', { name: /^(Delete|Supprimer)$/, exact: true })
      .click();
    await expect(item('Door side')).toHaveCount(0);
    await expect.poll(() => linked(sampleName('livingRoomOutlets'), 'Door outlet')).toBe(false);
    await expect(
      treeBranch(sampleName('garage'))
        .locator('p')
        .filter({ hasText: /^Door outlet$/ })
    ).toBeVisible();
    await page.reload();
    await expect.poll(() => flat().length).toBe(30);
    await expect.poll(() => linked(sampleName('livingRoomOutlets'), 'Door outlet')).toBe(false);
    await expand();
    await expect(item('Door side')).toHaveCount(0);
    await room
      .locator('p')
      .filter({ hasText: /^Window outlet$/ })
      .click();
    await branchProperty().click();
    await page
      .getByRole('option')
      .filter({ hasText: /^Desk side$/ })
      .click();
    await expand();
    const linkedOutlet = treeBranch('Desk side')
      .locator('p')
      .filter({ hasText: /^Window outlet$/ });
    await expect(linkedOutlet).toBeVisible();
    await linkedOutlet.locator('../..').getByRole('button').click();
    await page
      .getByRole('menuitem', { name: /^(Delete|Supprimer)$/, exact: true })
      .last()
      .click();
    await page
      .getByRole('dialog')
      .getByRole('button', { name: /^(Delete|Supprimer)$/, exact: true })
      .click();
    await expect(page.locator('p').filter({ hasText: /^Window outlet$/ })).toHaveCount(0);
    await expect.poll(() => flat().length).toBe(29);
    await expect(item('Desk side')).toBeVisible();
    await expect.poll(() => linked(sampleName('livingRoomOutlets'), 'Door outlet')).toBe(false);
    await page.screenshot({ path: '../.cache/circuit-branches-checked.png' });
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
          variables: { input: { id: crypto.randomUUID(), projectId } },
        },
      });
      const result = await deletion.json();
      expect(result.errors).toBeUndefined();
      expect(result.data.deleteProject.__typename).toMatch(/Success/);
    }
  }
});
