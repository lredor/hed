/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { readFileSync } from 'node:fs';

const suffix = process.env.HED_TEST_LOCALE?.startsWith('fr') ? '_fr' : '';
const messages = Object.fromEntries(
  readFileSync(
    new URL(`../../backend/hed-model/src/main/resources/i18n/hed-messages${suffix}.properties`, import.meta.url),
    'utf8'
  )
    .split(/\r?\n/)
    .filter((line) => line.includes('='))
    .map((line) => [line.slice(0, line.indexOf('=')), line.slice(line.indexOf('=') + 1)])
);

/** Returns the sample's stored name in the backend's creation language. */
export function sampleName(key: string): string {
  const value = messages[`sample.${key}`];
  if (!value) throw new Error(`Missing sample name: ${key}`);
  return value;
}

/** Returns a control channel's localized creation name. */
export function channelName(index: number): string {
  return messages['channel.defaultName'].replace('{0}', String(index));
}

/** Matches a localized sample name without interpreting punctuation as regular-expression syntax. */
export function samplePattern(key: string, exact = true, flags = ''): RegExp {
  const value = sampleName(key).replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  return new RegExp(`${exact ? '^' : ''}${value}${exact ? '$' : ''}`, flags);
}
