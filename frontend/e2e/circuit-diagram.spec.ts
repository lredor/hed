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
import { channelName, sampleName, samplePattern } from './sample';
import { randomUUID } from 'node:crypto';

test('show logical circuits inside storeys and rooms and retain freely arranged native nodes', async ({
  page,
  request,
}) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(10000);
  let projectId: string | undefined, diagram: any;
  const projectName = 'E2E circuit diagram ' + randomUUID(),
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
  const flat = (nodes: any[] = diagram?.nodes ?? [], parent?: string): any[] =>
    nodes.flatMap((node) => [
      { ...node, parent },
      ...flat(node.childNodes ?? [], node.id),
      ...flat(node.borderNodes ?? [], node.id),
    ]);
  const semantic = (label: string) => flat().find((node) => node.targetObjectLabel === label);
  const node = (label: string) => page.locator(`.react-flow__node[data-id="${semantic(label)?.id}"]`);
  const item = (label: string | RegExp) =>
    page
      .locator('p')
      .filter({ hasText: typeof label === 'string' ? new RegExp('^' + label + '$') : label })
      .first();
  const expand = async () => {
    await item(projectName).locator('../..').getByRole('button').click();
    await page
      .getByRole('menuitem', { name: /^(Expand all|Développer tout)$/ })
      .last()
      .click();
  };
  const link = (source: any, target: any) =>
    diagram.edges.find((edge: any) => edge.sourceId === source.id && edge.targetId === target.id);
  const position = (label: string) =>
    node(label).evaluate((element) => {
      const matrix = new DOMMatrix(getComputedStyle(element).transform);
      return { x: matrix.e, y: matrix.f };
    });
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
    await page
      .getByRole('option')
      .filter({ hasText: /^(Circuit Diagram|Schéma des circuits)$/ })
      .click();
    await dialog.getByRole('textbox').fill('Logical circuits');
    await dialog.getByRole('button', { name: /^(Create|Créer)$/ }).click();
    await expect.poll(() => flat().length).toBe(32);
    await expect.poll(() => diagram?.edges.length).toBe(23);
    await expand();
    await expect(item('Logical circuits')).toBeVisible();
    // This logical schematic does not count as placement on a storey's Floor Plan.
    await page.getByTestId('tree-filter-menu-icon').click();
    const highlight = page.getByRole('checkbox', {
      name: /^(Highlight equipments not in floor plans|Surligner les équipements absents des plans)$/,
    });
    await expect(highlight).not.toBeChecked();
    await highlight.check();
    await page.keyboard.press('Escape');
    await expect
      .poll(() =>
        item(sampleName('sofaOutlet')).evaluate((element) => {
          const text = document.createTreeWalker(element, NodeFilter.SHOW_TEXT).nextNode();
          return getComputedStyle(text!.parentElement!).color;
        })
      )
      .toBe('rgb(198, 40, 40)');
    await page.getByTestId('tree-filter-menu-icon').click();
    await highlight.uncheck();
    await page.keyboard.press('Escape');
    await expect(node(sampleName('sofaOutlet'))).toBeVisible();
    await expect(node(sampleName('groundFloor'))).toBeVisible();
    await expect(node(sampleName('livingRoom'))).toBeVisible();
    expect(semantic(sampleName('livingRoom')).parent).toBe(semantic(sampleName('groundFloor')).id);
    expect(semantic(sampleName('sofaOutlet')).parent).toBe(semantic(sampleName('livingRoom')).id);
    expect(semantic('Q1').parent).toBe(semantic(sampleName('mainBoard')).id);
    expect(semantic(sampleName('groundFloorLighting')).parent).toBe(semantic(sampleName('mainBoard')).id);
    expect(link(semantic('Q1'), semantic(sampleName('groundFloorLighting')))).toBeTruthy();
    expect(link(semantic(sampleName('livingRoomOutlets')), semantic(sampleName('sofaOutlet')))).toBeTruthy();
    expect(link(semantic(sampleName('garageBoardSupply')), semantic(sampleName('garageBoard')))).toBeTruthy();
    const channels = flat().filter((candidate) => candidate.parent === semantic(sampleName('doubleSwitch')).id);
    expect(channels).toHaveLength(2);
    expect(
      link(
        channels.find((channel) => channel.targetObjectLabel === channelName(1)),
        semantic(sampleName('ceilingLight'))
      ).style.lineStyle
    ).toBe('Dash');
    expect(
      link(
        channels.find((channel) => channel.targetObjectLabel === channelName(2)),
        semantic(sampleName('sofaOutlet'))
      ).style.lineStyle
    ).toBe('Dash');
    expect(semantic(sampleName('sofaOutlet')).style.imageURL).toBe('/api/images/hed/mdi/power-socket-fr.svg');
    expect(semantic(sampleName('livingRoom')).style.__typename).toBe('RectangularNodeStyle');
    expect(semantic(sampleName('sofaOutlet')).type).toBe('node:image');
    expect(semantic(sampleName('sofaOutlet')).defaultWidth).toBe(24);
    await expect.poll(() => diagram.layoutData?.nodeLayoutData.length ?? 0).toBe(32);
    expect((await request.get(semantic(sampleName('sofaOutlet')).style.imageURL)).ok()).toBe(true);
    const roots = await Promise.all(
      diagram.nodes.map((root: any) => page.locator(`.react-flow__node[data-id="${root.id}"]`).boundingBox())
    );
    await page.screenshot({ path: '../.cache/circuit-diagram-initial.png' });
    // Native content-box containers may share their one-pixel borders after layout.
    const borderTolerance = await page
      .locator('.react-flow__viewport')
      .evaluate((element) => 2 * new DOMMatrix(getComputedStyle(element).transform).a + 0.1);
    for (let i = 0; i < roots.length; i++)
      for (let j = i + 1; j < roots.length; j++) {
        const a = roots[i]!,
          b = roots[j]!;
        expect(
          Math.min(a.x + a.width, b.x + b.width) - Math.max(a.x, b.x) <= borderTolerance ||
            Math.min(a.y + a.height, b.y + b.height) - Math.max(a.y, b.y) <= borderTolerance,
          JSON.stringify({
            a: diagram.nodes[i].targetObjectLabel,
            b: diagram.nodes[j].targetObjectLabel,
            first: a,
            second: b,
          })
        ).toBe(true);
      }
    const before = await position(sampleName('sofaOutlet')),
      box = await node(sampleName('sofaOutlet')).boundingBox();
    const saved = page.waitForResponse(
      (response) =>
        response.url().endsWith('/api/graphql') && (response.request().postData() ?? '').includes('layoutDiagram')
    );
    const roomBox = await node(sampleName('livingRoom')).boundingBox(),
      dx = box!.x > roomBox!.x + roomBox!.width / 2 ? -25 : 25,
      dy = box!.y > roomBox!.y + roomBox!.height / 2 ? -18 : 18;
    await page.mouse.move(box!.x + box!.width / 2, box!.y + box!.height / 2);
    await page.mouse.down();
    await page.mouse.move(box!.x + box!.width / 2 + dx, box!.y + box!.height / 2 + dy, { steps: 12 });
    await page.mouse.up();
    await saved;
    await expect.poll(async () => Math.abs((await position(sampleName('sofaOutlet'))).x - before.x)).toBeGreaterThan(5);
    const moved = await position(sampleName('sofaOutlet'));
    await node(sampleName('livingRoom')).getByText(sampleName('livingRoom'), { exact: true }).click();
    const handle = node(sampleName('livingRoom')).locator('.react-flow__resize-control.handle.bottom.right');
    await expect(handle).toBeVisible();
    const grip = await handle.boundingBox(),
      size = await node(sampleName('livingRoom')).boundingBox();
    const resized = page.waitForResponse(
      (response) =>
        response.url().endsWith('/api/graphql') && (response.request().postData() ?? '').includes('layoutDiagram')
    );
    await page.mouse.move(grip!.x + grip!.width / 2, grip!.y + grip!.height / 2);
    await page.mouse.down();
    await page.mouse.move(grip!.x + grip!.width / 2 + 35, grip!.y + grip!.height / 2 + 20, { steps: 12 });
    await page.mouse.up();
    await resized;
    await expect
      .poll(async () => (await node(sampleName('livingRoom')).boundingBox())!.width)
      .toBeGreaterThan(size!.width + 15);
    await page.reload();
    await expect(node(sampleName('sofaOutlet'))).toBeVisible();
    await expect.poll(() => position(sampleName('sofaOutlet'))).toEqual(moved);
    await expect.poll(() => diagram.edges.length).toBe(23);
    await node(sampleName('sofaOutlet')).click();
    await expect(page.getByRole('textbox', { name: /^(Name|Nom)$/, exact: true })).toHaveValue(
      sampleName('sofaOutlet')
    );
    // Existing Properties edits update both semantic links and room containment.
    await page
      .getByTestId(/^(Supply circuit|Circuit d’alimentation)$/)
      .getByRole('combobox')
      .click();
    await page
      .getByRole('option')
      .filter({ hasText: samplePattern('groundFloorLighting') })
      .click();
    await expect
      .poll(() => !!link(semantic(sampleName('groundFloorLighting')), semantic(sampleName('sofaOutlet'))))
      .toBe(true);
    expect(link(semantic(sampleName('livingRoomOutlets')), semantic(sampleName('sofaOutlet')))).toBeUndefined();
    await page
      .getByTestId(/^(Room|Pièce)$/)
      .getByRole('combobox')
      .click();
    await page
      .getByRole('option')
      .filter({ hasText: samplePattern('bedroom') })
      .click();
    await expect.poll(() => semantic(sampleName('sofaOutlet')).parent).toBe(semantic(sampleName('bedroom')).id);
    await expect.poll(() => flat().length).toBe(32);
    await page.reload();
    await expect(node(sampleName('sofaOutlet'))).toBeVisible();
    await expect.poll(() => semantic(sampleName('sofaOutlet')).parent).toBe(semantic(sampleName('bedroom')).id);
    await page.screenshot({ path: '../.cache/circuit-diagram-checked.png' });
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
