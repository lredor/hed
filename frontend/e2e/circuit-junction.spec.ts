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
import { readFileSync } from 'node:fs';
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
    .filter((l) => l.includes('='))
    .map((l) => [l.slice(0, l.indexOf('=')), l.slice(l.indexOf('=') + 1)])
);
const escape = (value: string) => value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

test('grab a junction above selected incident edges and retain centred links and the saved position', async ({
  page,
  request,
}) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  await page.setViewportSize({ width: 2200, height: 1500 });
  const projectName = 'E2E junction ' + randomUUID();
  let projectId: string | undefined, diagram: any;
  const errors: string[] = [];
  page.on('pageerror', (e) => errors.push(e.message));
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
    nodes.flatMap((n) => [n, ...flat(n.childNodes ?? []), ...flat(n.borderNodes ?? [])]);
  const item = (name: string) =>
    page
      .locator('[data-treeitemid] p')
      .filter({ hasText: new RegExp('^' + escape(name) + '$') })
      .first();
  const expand = async () => {
    await item(projectName).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
  };
  const rename = async (row: ReturnType<typeof item>, label: string) => {
    await row.locator('../..').getByRole('button').click();
    await page
      .getByRole('menuitem', { name: french ? 'Renommer' : 'Rename', exact: true })
      .last()
      .click();
    const input = page.getByTestId('name-edit').getByRole('textbox');
    await input.fill('');
    await input.pressSequentially(label, { delay: 10 });
    await input.press('Enter');
    await expect(item(label)).toBeVisible();
  };
  const createBranch = async (name: string, outlet?: string) => {
    await item(sampleName('livingRoomOutlets')).locator('../..').getByRole('button').click();
    await page
      .getByRole('button', { name: `${messages['action.create']} ${messages['class.CircuitBranch']}`, exact: true })
      .click();
    await expand();
    await rename(item(messages['new.CircuitBranch']), name);
    if (outlet) {
      await item(name).locator('../..').getByRole('button').click();
      await page
        .getByRole('button', { name: `${messages['action.create']} ${messages['class.Outlet']}`, exact: true })
        .click();
      await expand();
      const alias = item(name)
        .locator('xpath=ancestor::li[1]')
        .locator('p')
        .filter({ hasText: new RegExp('^' + escape(messages['kind.OUTLET']) + '$') })
        .first();
      await rename(alias, outlet);
    }
  };
  try {
    await page.goto('/new/project?templateId=hed-example');
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\//);
    projectId = page.url().match(/\/projects\/([^/]+)/)?.[1];
    expect(projectId).toBeTruthy();
    await item(projectName).waitFor();
    await expand();
    await createBranch('Window side');
    await createBranch('Door side', 'Door outlet');
    await createBranch('Desk side', 'Desk outlet');
    await item(messages['class.Dwelling']).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.newRepresentation'], exact: true }).last().click();
    const dialog = page.getByRole('dialog');
    await dialog.getByRole('combobox').click();
    await page.getByRole('option', { name: french ? 'Schéma des circuits' : 'Circuit Diagram', exact: true }).click();
    await dialog.getByRole('textbox').fill('Junction movement');
    await dialog.getByRole('button', { name: french ? 'Créer' : 'Create', exact: true }).click();
    await expect.poll(() => flat().length).toBe(30);
    const junction = flat().find((n) => n.style.__typename === 'CircuitJunctionNodeStyle');
    expect(junction.targetObjectLabel).toBe(sampleName('livingRoomOutlets'));
    const node = page.locator(`.react-flow__node[data-id="${junction.id}"]`);
    const surface = page.getByTestId(`circuit-junction-${junction.targetObjectId}`);
    await expect.poll(() => surface.evaluate((e) => parseFloat(getComputedStyle(e).width))).toBe(24);
    await expect
      .poll(() =>
        surface.locator('.hed-circuit-junction-symbol').evaluate((e) => parseFloat(getComputedStyle(e).width))
      )
      .toBe(8);
    const incident = diagram.edges.filter((e: any) => e.sourceId === junction.id || e.targetId === junction.id);
    expect(incident).toHaveLength(4);
    const ids = incident.map((e: any) => e.id).sort();
    const endpoints = async () => {
      const bounds = (await node.boundingBox())!;
      const center = { x: bounds.x + bounds.width / 2, y: bounds.y + bounds.height / 2 };
      for (const edge of incident) {
        const endpoint = await page
          .locator(`.react-flow__edge[data-id="${edge.id}"] .react-flow__edge-path`)
          .first()
          .evaluate((path, atStart) => {
            const svg = path as SVGPathElement;
            const p = svg.getPointAtLength(atStart ? 0 : svg.getTotalLength());
            const s = new DOMPoint(p.x, p.y).matrixTransform(svg.getScreenCTM()!);
            return { x: s.x, y: s.y };
          }, edge.sourceId === junction.id);
        expect(Math.abs(endpoint.x - center.x)).toBeLessThan(2);
        expect(Math.abs(endpoint.y - center.y)).toBeLessThan(2);
      }
    };
    await expect(endpoints).toPass({ timeout: 10000 });
    await surface.hover();
    await expect(page.getByRole('tooltip', { name: sampleName('livingRoomOutlets'), exact: true })).toBeVisible();
    expect(await surface.evaluate((e) => getComputedStyle(e).outlineColor)).toBe('rgb(25, 118, 210)');
    const edge = incident.find((e: any) => e.sourceId === junction.id);
    const edgePath = page.locator(`.react-flow__edge[data-id="${edge.id}"] .react-flow__edge-path`).first();
    const point = await edgePath.evaluate((path) => {
      const svg = path as SVGPathElement;
      const p = svg.getPointAtLength(svg.getTotalLength() * 0.7);
      const screen = new DOMPoint(p.x, p.y).matrixTransform(svg.getScreenCTM()!);
      return { x: screen.x, y: screen.y };
    });
    await page.mouse.click(point.x, point.y);
    await expect(page.locator(`.react-flow__edge[data-id="${edge.id}"]`)).toHaveClass(/selected/);
    const before = (await node.boundingBox())!;
    const start = { x: before.x + before.width / 6, y: before.y + before.height / 6 };
    expect(
      await page.evaluate((p) => !!document.elementFromPoint(p.x, p.y)?.closest('.hed-circuit-junction'), start)
    ).toBe(true);
    await page.mouse.move(start.x, start.y);
    await page.mouse.down();
    await page.mouse.move(start.x + 80, start.y + 45, { steps: 12 });
    await page.mouse.up();
    await expect(node).toHaveClass(/selected/);
    await expect(surface).toHaveClass(/hed-circuit-junction-selected/);
    const saved = () => diagram.layoutData.nodeLayoutData.find((entry: any) => entry.id === junction.id);
    await expect.poll(() => saved()?.movedByUser).toBe(true);
    const after = (await node.boundingBox())!;
    expect(after.x - before.x).toBeGreaterThan(50);
    expect(after.y - before.y).toBeGreaterThan(25);
    await expect(endpoints).toPass({ timeout: 10000 });
    const position = { ...saved().position };
    await page.reload();
    await expect
      .poll(() => flat().find((n) => n.id === junction.id)?.style.__typename)
      .toBe('CircuitJunctionNodeStyle');
    await expect.poll(() => saved()?.position).toEqual(position);
    await expect(surface).toBeVisible();
    expect(
      diagram.edges
        .filter((e: any) => e.sourceId === junction.id || e.targetId === junction.id)
        .map((e: any) => e.id)
        .sort()
    ).toEqual(ids);
    await expect(endpoints).toPass({ timeout: 10000 });
    expect(errors).toEqual([]);
  } finally {
    if (projectId) {
      const response = await request.post('/api/graphql', {
        data: {
          query: 'mutation($input:DeleteProjectInput!){deleteProject(input:$input){__typename}}',
          variables: { input: { id: randomUUID(), projectId } },
        },
      });
      expect((await response.json()).data.deleteProject.__typename).toMatch(/Success/);
    }
  }
});
