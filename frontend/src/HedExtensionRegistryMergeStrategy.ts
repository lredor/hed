/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import type { DataExtension } from '@eclipse-sirius/sirius-components-core';
import { apolloClientOptionsConfigurersExtensionPoint } from '@eclipse-sirius/sirius-web-application';
import { paletteToolExtensionPoint } from '@eclipse-sirius/sirius-components-palette';
import { FloorPlanConfirmationMergeStrategy } from './representations/diagrams/floorplan/FloorPlanRemovalConfirmation';

/** Adds HED tools and query extensions while preserving native widgets and the floor-plan representation factory. */
export class HedExtensionRegistryMergeStrategy extends FloorPlanConfirmationMergeStrategy {
  override mergeDataExtensions(
    identifier: string,
    existing: DataExtension<unknown>,
    contribution: DataExtension<unknown>
  ): DataExtension<unknown> {
    if (
      identifier === paletteToolExtensionPoint.identifier ||
      identifier === apolloClientOptionsConfigurersExtensionPoint.identifier
    ) {
      return {
        identifier: contribution.identifier,
        data: [...(existing.data as unknown[]), ...(contribution.data as unknown[])],
      };
    }
    return super.mergeDataExtensions(identifier, existing, contribution);
  }
}
