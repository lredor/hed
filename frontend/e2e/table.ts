/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { expect, type Page } from '@playwright/test';

/** Uses native persisted column visibility rather than editing the table's semantic description. */
export async function showTableColumn(page: Page, label: string) {
  await page.getByRole('button', { name: /show.*hide.*columns|afficher.*(masquer|cacher).*colonnes/i }).click();
  const toggle = page.getByRole('menu').getByLabel(label, { exact: true });
  if (!(await toggle.isChecked())) await toggle.click();
  // The native controlled switch updates only after the server refresh has arrived.
  await expect(toggle).toBeChecked();
  await page.keyboard.press('Escape');
}
