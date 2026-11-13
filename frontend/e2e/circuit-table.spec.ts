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
import { readFileSync } from 'node:fs';
import { randomUUID } from 'node:crypto';
import { showTableColumn } from './table';

const french = process.env.HED_TEST_LOCALE?.startsWith('fr') ?? false;
const escape = (value: string) => value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
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

test('simplify circuit columns, edit descriptions and persist optional name visibility', async ({ page, request }) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = 'E2E circuit summary ' + randomUUID();
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  let projectId: string | undefined;
  const item = (label: string) =>
    page
      .locator('[data-treeitemid] p')
      .filter({ hasText: new RegExp('^' + escape(label) + '$') })
      .first();
  const expand = async () => {
    await item(projectName).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
  };
  const create = async (kind: string, label: string) => {
    await expand();
    await item(messages[kind === 'FinalCircuit' ? 'explorer.circuits' : 'explorer.gtl'])
      .locator('../..')
      .getByRole('button')
      .click();
    await page
      .getByRole('button', { name: `${messages['action.create']} ${messages['class.' + kind]}`, exact: true })
      .click();
    await expand();
    await item(messages['new.' + kind]).click();
    const name = page.getByRole('textbox', { name: messages['feature.name'], exact: true });
    await name.fill(label);
    await name.press('Tab');
    await expect(item(label)).toBeVisible();
  };
  const headers = () =>
    page.locator('thead th').evaluateAll((elements) =>
      elements
        .map((element) =>
          [...element.querySelectorAll('p')]
            .map((p) => p.textContent?.trim())
            .filter(Boolean)
            .join(' ')
        )
        .filter(Boolean)
    );
  try {
    await page.goto('/');
    await page.locator('a[href="/new/project?templateId=hed-empty"]').click();
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\/[^/]+\/edit/);
    projectId = page.url().match(/\/projects\/([^/]+)/)?.[1];
    await create('DistributionBoard', 'Board A');
    await create('FinalCircuit', 'Circuit A');
    await create('FinalCircuit', 'Circuit B');
    await item(messages['class.Dwelling']).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.newRepresentation'], exact: true }).last().click();
    const dialog = page.getByRole('dialog');
    await dialog.getByRole('combobox').click();
    await page
      .getByRole('option')
      .filter({ hasText: new RegExp('^' + messages['table.circuits'] + '$') })
      .click();
    await dialog.getByRole('button', { name: french ? 'Créer' : 'Create', exact: true }).click();
    await expect(page.getByRole('table')).toBeVisible();
    const expected = [
      'feature.description',
      'feature.upstreamRccb',
      'table.column.circuits.protectiveDevice',
      'table.column.circuits.conductorSectionMm2',
      'table.column.circuits.served',
      'feature.issues',
      'feature.usage',
    ].map((key) => messages[key]);
    expected[1] = french ? 'Inter diff' : 'RCCB';
    await expect.poll(headers).toEqual(expected);
    // The tooltip must preserve the native header's block layout and vertical alignment.
    await expect
      .poll(async () => {
        const residual = await page.getByRole('columnheader').getByText(expected[1], { exact: true }).boundingBox();
        const description = await page
          .getByRole('columnheader')
          .getByText(messages['feature.description'], { exact: true })
          .boundingBox();
        return Math.abs(residual!.y + residual!.height / 2 - description!.y - description!.height / 2);
      })
      .toBeLessThan(1);
    await page
      .getByRole('columnheader')
      .getByText(french ? 'Inter diff' : 'RCCB', { exact: true })
      .hover();
    await expect(page.getByRole('tooltip', { name: messages['feature.upstreamRccb'], exact: true })).toBeVisible();
    await page.mouse.move(0, 0);
    const row = page.getByRole('row').filter({ has: page.getByText('Circuit A', { exact: true }) });
    await row.getByRole('textbox').first().fill('Éclairage côté fenêtre');
    await page.keyboard.press('Tab');
    await expect(row.getByRole('textbox').first()).toHaveValue('Éclairage côté fenêtre');
    // A hidden name still participates in global filtering.
    const search = page.getByRole('textbox', { name: /^(Search|Rechercher)$/ });
    await search.fill('Circuit B');
    await expect(page.locator('tbody tr')).toHaveCount(1);
    await search.fill('');
    await expect(page.locator('tbody tr')).toHaveCount(2);
    await create('DistributionBoard', 'Board B');
    expected.splice(1, 0, messages['feature.sourceBoard']);
    await expect.poll(headers).toEqual(expected);
    await page.screenshot({ path: '../.cache/circuit-table-default.png' });
    await showTableColumn(page, messages['feature.name']);
    await expect.poll(headers).toEqual([messages['feature.name'], ...expected]);
    await page.reload();
    await expect.poll(headers).toEqual([messages['feature.name'], ...expected]);
    // The native table URL can retain the last search used during the session.
    await search.fill('');
    await expect(
      page
        .getByRole('row')
        .filter({ has: page.getByText('Circuit A', { exact: true }) })
        .getByRole('textbox')
        .nth(1)
    ).toHaveValue('Éclairage côté fenêtre');
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
