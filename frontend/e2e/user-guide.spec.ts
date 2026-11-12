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

test('open the bundled localized user guide from native Help and switch languages', async ({ page, request }) => {
  const french = process.env.HED_TEST_LOCALE?.startsWith('fr') ?? false;
  await page.goto('/');
  // Sirius Web 2026.9.0's rightmost Help button has no accessible name; MUI omits icon test IDs in production.
  await page.getByRole('banner').getByRole('button').last().click();
  const menu = page.getByTestId('navigationbar-menu');
  const help = menu.getByRole('menuitem', { name: french ? 'Aide' : 'Help', exact: true });
  await expect(help).toHaveAttribute('href', new URL('/api/hed/help', page.url()).href);
  const [guide] = await Promise.all([page.waitForEvent('popup'), help.click()]);
  try {
    await expect(guide).toHaveURL(new RegExp(`/help/${french ? 'fr' : 'en'}/index\\.html$`));
    await expect(guide.locator('html')).toHaveAttribute('lang', french ? 'fr' : 'en');
    await expect(guide.getByRole('heading', { level: 1 })).toHaveText(
      french ? 'Home Electrical Designer : guide utilisateur' : 'Home Electrical Designer: User Guide'
    );
    await expect(guide.locator('#toc')).toBeVisible();
    await expect(guide.locator('table.tableblock')).toHaveCount(5);
    await expect(guide.locator('.admonitionblock.warning')).toContainText(french ? 'expérimental' : 'experimental');
    await expect(guide.locator('img')).toHaveCount(7);
    await expect
      .poll(() =>
        guide.locator('img').evaluateAll((images) => images.every((image) => image.complete && image.naturalWidth > 0))
      )
      .toBe(true);
    const stylesheet = await guide.locator('link[rel="stylesheet"]').getAttribute('href');
    expect((await request.get(new URL(stylesheet!, guide.url()).href)).ok()).toBe(true);
    const missingAnchors = await guide
      .locator('#toc a[href^="#"]')
      .evaluateAll((links) =>
        links
          .map((link) => link.getAttribute('href')!.slice(1))
          .filter((id) => !document.getElementById(decodeURIComponent(id)))
      );
    expect(missingAnchors).toEqual([]);
    const resources = await guide
      .locator('img[src], link[rel="stylesheet"], script[src]')
      .evaluateAll((elements) =>
        elements.map(
          (element) => new URL(element.getAttribute('src') ?? element.getAttribute('href')!, document.URL).origin
        )
      );
    expect(resources.every((origin) => origin === new URL(guide.url()).origin)).toBe(true);
    await guide.screenshot({ path: `../.cache/user-guide-${french ? 'fr' : 'en'}.png`, fullPage: false });
    await guide
      .getByRole('link', { name: french ? 'Read this guide in English' : 'Lire ce guide en français', exact: true })
      .click();
    await expect(guide.locator('html')).toHaveAttribute('lang', french ? 'en' : 'fr');
    await expect(guide).toHaveURL(new RegExp(`/help/${french ? 'en' : 'fr'}/index\\.html$`));
  } finally {
    await guide.close();
  }
});
