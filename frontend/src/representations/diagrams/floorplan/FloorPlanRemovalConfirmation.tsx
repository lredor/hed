/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { createContext, forwardRef, useCallback, useContext, useMemo, useState } from 'react';
import {
  ConfirmationDialogContext,
  DataExtension,
  RepresentationComponentFactory,
  RepresentationComponentProps,
  WorkbenchMainRepresentationHandle,
  representationFactoryExtensionPoint,
} from '@eclipse-sirius/sirius-components-core';
import { DiagramRepresentation } from '@eclipse-sirius/sirius-components-diagrams';
import { DefaultExtensionRegistryMergeStrategy } from '@eclipse-sirius/sirius-web-application';
import { useTranslation } from 'react-i18next';

/** The background identifies its diagram without relying on translated names, icons or generated description IDs. */
export const FloorPlanRemovalContext = createContext<() => () => void>(() => () => {});

/** Adapt only the native deletion prompt; the original dialog and tool callback keep their normal behavior. */
const FloorPlanAwareDiagram = forwardRef<WorkbenchMainRepresentationHandle, RepresentationComponentProps>(
  (props, ref) => {
    const parent = useContext(ConfirmationDialogContext);
    const [floorPlan, setFloorPlan] = useState(false);
    const { t } = useTranslation('home-electrical-designer');
    const { t: core } = useTranslation('sirius-components-core', { keyPrefix: 'useDeletionConfirmationDialog' });
    const register = useCallback(() => {
      setFloorPlan(true);
      return () => setFloorPlan(false);
    }, []);
    const confirmation = useMemo(
      () => ({
        showConfirmation: (title: string, message: string, buttonLabel: string, onConfirm: () => void) => {
          const deletion =
            title === core('title') &&
            message === `${core('willDelete')}\n${core('willNotBeAbleToRevert')}` &&
            buttonLabel === core('buttonLabel');
          if (floorPlan && deletion) {
            parent.showConfirmation(
              t('plan.removeEquipment'),
              t('plan.removeConfirmation'),
              t('plan.removeEquipment'),
              onConfirm
            );
          } else {
            parent.showConfirmation(title, message, buttonLabel, onConfirm);
          }
        },
      }),
      [parent, floorPlan, core, t]
    );
    return (
      <FloorPlanRemovalContext.Provider value={register}>
        <ConfirmationDialogContext.Provider value={confirmation}>
          <DiagramRepresentation {...props} ref={ref} />
        </ConfirmationDialogContext.Provider>
      </FloorPlanRemovalContext.Provider>
    );
  }
);

export const floorPlanConfirmationFactory: RepresentationComponentFactory = (representation) =>
  new URLSearchParams(representation.kind.split('?')[1]).get('type') === 'Diagram' ? FloorPlanAwareDiagram : null;

/** Prepend the diagram adapter while retaining every native representation factory and the other default merge rules. */
export class FloorPlanConfirmationMergeStrategy extends DefaultExtensionRegistryMergeStrategy {
  override mergeDataExtensions(
    identifier: string,
    existing: DataExtension<unknown>,
    contribution: DataExtension<unknown>
  ): DataExtension<unknown> {
    if (identifier === representationFactoryExtensionPoint.identifier) {
      return {
        identifier: contribution.identifier,
        data: [
          ...(contribution.data as RepresentationComponentFactory[]),
          ...(existing.data as RepresentationComponentFactory[]),
        ],
      };
    }
    return super.mergeDataExtensions(identifier, existing, contribution);
  }
}
