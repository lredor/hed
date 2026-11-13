/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { describe, expect, it } from 'vitest';
import { paletteToolExtensionPoint } from '@eclipse-sirius/sirius-components-palette';
import { representationFactoryExtensionPoint } from '@eclipse-sirius/sirius-components-core';
import { HedExtensionRegistryMergeStrategy } from '../src/HedExtensionRegistryMergeStrategy';

describe('application extension merging', () => {
  it('retains native rename and delete when adding device-type navigation without mutating either list', () => {
    const native = { identifier: 'native', data: [{ id: 'direct-edit' }, { id: 'semantic-delete' }] };
    const hed = { identifier: 'hed', data: [{ id: 'hed-navigate-device-type' }] };
    const result = new HedExtensionRegistryMergeStrategy().mergeDataExtensions(
      paletteToolExtensionPoint.identifier,
      native,
      hed
    );
    expect(result.data).toEqual([...native.data, ...hed.data]);
    expect(native.data).toHaveLength(2);
    expect(hed.data).toHaveLength(1);
  });

  it('still prepends the floor-plan factory and delegates unrelated extension rules', () => {
    const native = { identifier: 'native', data: [() => null] };
    const hed = { identifier: 'hed', data: [() => null] };
    const strategy = new HedExtensionRegistryMergeStrategy();
    expect(strategy.mergeDataExtensions(representationFactoryExtensionPoint.identifier, native, hed).data).toEqual([
      ...hed.data,
      ...native.data,
    ]);
    expect(strategy.mergeDataExtensions('unrelated', native, hed)).toBe(native);
  });
});
