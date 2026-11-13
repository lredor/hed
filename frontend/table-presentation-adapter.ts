/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

/** Adds the missing presentation hooks to the pinned Sirius bundle; incompatible upgrades fail explicitly. */
export function adaptTablePresentation(code: string, adapter: string): string {
  const header = 'var ColumnHeader = ({ column }) => {';
  const content = code.split('\n').filter((line) => line.startsWith('var TableContent = memo(('));
  if (
    code.split(header).length !== 2 ||
    content.length !== 1 ||
    !content[0].endsWith('=> {') ||
    !content[0].includes('enableColumnOrdering')
  )
    throw new Error('The Sirius table presentation boundaries changed; update the HED table adapter.');
  return (
    `import { presentHedTable, hasHedSummaryColumns, hedColumnHeader } from ${JSON.stringify(adapter)};\n` +
    code
      .replace(
        header,
        'var ColumnHeader = ({ column }) => hedColumnHeader(column, HedNativeColumnHeader);\nvar HedNativeColumnHeader = ({ column }) => {'
      )
      .replace(
        content[0],
        content[0] +
          '\n table = presentHedTable(table);\n enableColumnOrdering = enableColumnOrdering && !hasHedSummaryColumns(table);'
      )
  );
}
