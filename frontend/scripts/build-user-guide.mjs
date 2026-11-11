/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import Asciidoctor from '@asciidoctor/core';
import { cp, mkdir, writeFile } from 'node:fs/promises';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = resolve(dirname(fileURLToPath(import.meta.url)), '../..');
const output = resolve(root, 'backend/hed-application/src/main/resources/static/help');
const processor = Asciidoctor();
const diagnostics = processor.MemoryLogger.create();
processor.LoggerManager.setLogger(diagnostics);
const packageRoot = new URL('../../', import.meta.resolve('@asciidoctor/core'));

for (const language of ['en', 'fr']) {
  const destination = resolve(output, language);
  await mkdir(destination, { recursive: true });
  const html = processor.convertFile(resolve(root, `doc/user/${language}/index.adoc`), {
    safe: 'secure',
    standalone: true,
    to_file: false,
    attributes: [
      'toc=left',
      'toclevels=2',
      'webfonts!',
      'imagesdir=../images/representations',
      `other-guide=../${language === 'en' ? 'fr' : 'en'}/index.html`,
    ],
  });
  const errors = diagnostics.getMessages().filter((message) => ['ERROR', 'FATAL'].includes(message.severity));
  if (errors.length) throw new Error(`Invalid ${language} user guide: ${JSON.stringify(errors)}`);
  await writeFile(resolve(destination, 'index.html'), html, 'utf8');
  await cp(fileURLToPath(new URL('dist/css/asciidoctor.css', packageRoot)), resolve(destination, 'asciidoctor.css'));
}
await cp(fileURLToPath(new URL('LICENSE', packageRoot)), resolve(output, 'ASCIIDOCTOR-LICENSE.txt'));
await cp(resolve(root, 'doc/images/representations'), resolve(output, 'images/representations'), { recursive: true });
console.log('Built English and French user guides with local images.');
