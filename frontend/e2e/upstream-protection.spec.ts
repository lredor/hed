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

const french = process.env.HED_TEST_LOCALE?.startsWith('fr') ?? false;
const properties = new URL(
  `../../backend/hed-model/src/main/resources/i18n/hed-messages${french ? '_fr' : ''}.properties`,
  import.meta.url
);
const messages = Object.fromEntries(
  readFileSync(properties, 'utf8')
    .split(/\r?\n/)
    .filter((line) => line.includes('='))
    .map((line) => {
      const separator = line.indexOf('=');
      return [line.slice(0, separator), line.slice(separator + 1)];
    })
);

test('connect protection devices and reopen both downstream relationships', async ({ page, request }) => {
  test.setTimeout(120000);
  page.setDefaultTimeout(15000);
  const projectName = `E2E upstream protection ${randomUUID()}`;
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  let projectId: string | undefined;
  const item = (name: string) =>
    page
      .locator('p')
      .filter({ hasText: name })
      .filter({ hasText: new RegExp(`^${name}(?: - .+)?$`) })
      .first();
  const field = (key: string) => page.getByTestId(messages[`feature.${key}`]).getByRole('combobox');
  const expand = async () => {
    await item(projectName).waitFor();
    await item(projectName).locator('../..').getByRole('button').click();
    await page.getByRole('menuitem', { name: messages['action.expandAll'], exact: true }).last().click();
    await item('ID1').waitFor();
  };
  const serviceBreaker = messages['kind.SERVICE_BREAKER'];
  try {
    await page.goto('/');
    await page.locator('a[href="/new/project?templateId=hed-example"]').click();
    await page.getByRole('main').getByRole('textbox').first().fill(projectName);
    await page.getByRole('button', { name: french ? 'Créer le projet' : 'Create Project', exact: true }).click();
    await page.waitForURL(/\/projects\//);
    projectId = page.url().match(/\/projects\/([^/]+)\//)?.[1];
    expect(projectId).toBeTruthy();
    await expand();
    await item('ID1').click();
    const downstream = page.getByRole('textbox', { name: messages['feature.downstreamDevices'], exact: true });
    await expect(downstream).toBeDisabled();
    await expect(downstream).toHaveValue('Q1, Q2, Q3, Q5');
    await expect(field('upstreamServiceBreaker')).toContainText(serviceBreaker);
    await field('upstreamServiceBreaker').click();
    await page.getByRole('option', { name: messages['unassigned'], exact: true }).click();
    await expect(field('upstreamServiceBreaker')).not.toContainText(serviceBreaker);
    await page
      .getByTestId(serviceBreaker)
      .filter({ has: page.locator('img[alt*="entity=ServiceCircuitBreaker"]') })
      .click();
    await expect(field('downstreamDevices')).not.toContainText('ID1');
    await item('ID1').click();
    await field('upstreamServiceBreaker').click();
    await page.getByRole('option').filter({ hasText: serviceBreaker }).click();
    await expect(field('upstreamServiceBreaker')).toContainText(serviceBreaker);
    await page
      .getByTestId(serviceBreaker)
      .filter({ has: page.locator('img[alt*="entity=ServiceCircuitBreaker"]') })
      .click();
    await expect(field('downstreamDevices')).toContainText('ID1');
    await item('Q1').click();
    await expect(downstream).toHaveCount(0);
    await field('upstreamRccb').click();
    await page.getByRole('option', { name: messages['unassigned'], exact: true }).click();
    await expect(field('upstreamRccb')).not.toContainText('ID1');
    await item('ID1').click();
    await expect(downstream).toHaveValue('Q2, Q3, Q5');
    await item('Q1').click();
    await field('upstreamRccb').click();
    await page.getByRole('option').filter({ hasText: 'ID1' }).click();
    await expect(field('upstreamRccb')).toContainText('ID1');
    await page.reload();
    await expand();
    await item('ID1').click();
    await expect(field('upstreamServiceBreaker')).toContainText(serviceBreaker);
    await expect(downstream).toHaveValue('Q1, Q2, Q3, Q5');
    await page
      .getByTestId(serviceBreaker)
      .filter({ has: page.locator('img[alt*="entity=ServiceCircuitBreaker"]') })
      .click();
    await expect(field('downstreamDevices')).toContainText('ID1');
    expect(errors).toEqual([]);
  } finally {
    if (projectId) {
      const response = await request.post('/api/graphql', {
        data: { query: 'query($projectId:ID!){viewer{project(projectId:$projectId){name}}}', variables: { projectId } },
      });
      expect((await response.json()).data?.viewer?.project?.name).toBe(projectName);
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
