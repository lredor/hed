/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { createElement, useContext, useEffect, useState } from 'react';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {
  ConfirmationDialogContext,
  representationFactoryExtensionPoint,
  type RepresentationComponentProps,
} from '@eclipse-sirius/sirius-components-core';
import { DefaultExtensionRegistryMergeStrategy } from '@eclipse-sirius/sirius-web-application';
import i18n from 'i18next';
import { describe, expect, it, vi } from 'vitest';
import {
  FloorPlanConfirmationMergeStrategy,
  FloorPlanRemovalContext,
  floorPlanConfirmationFactory,
} from '../src/representations/diagrams/floorplan/FloorPlanRemovalConfirmation';
import { platform } from './platform';

const representation = (kind: string) => ({ kind } as Parameters<typeof floorPlanConfirmationFactory>[0]);
const Adapter = floorPlanConfirmationFactory(representation('siriusComponents://representation?type=Diagram'))!;
const props: RepresentationComponentProps = {
  editingContextId: 'editing-1',
  representationId: 'diagram-1',
  readOnly: false,
};
let onConfirm = vi.fn();

function NativeDiagram() {
  const register = useContext(FloorPlanRemovalContext),
    confirmation = useContext(ConfirmationDialogContext);
  const [background, setBackground] = useState(true);
  useEffect(() => (background ? register() : undefined), [background, register]);
  const core = (key: string) =>
    String(i18n.t(`useDeletionConfirmationDialog.${key}`, { ns: 'sirius-components-core' }));
  return (
    <>
      <button onClick={() => setBackground(false)}>Remove background node</button>
      <button
        onClick={() =>
          confirmation.showConfirmation(
            core('title'),
            `${core('willDelete')}\n${core('willNotBeAbleToRevert')}`,
            core('buttonLabel'),
            onConfirm
          )
        }>
        Native Delete
      </button>
      <button onClick={() => confirmation.showConfirmation('Other operation', 'Other message', 'OK', onConfirm)}>
        Other operation
      </button>
    </>
  );
}

function setup() {
  onConfirm = vi.fn();
  const showConfirmation = vi.fn();
  platform.renderDiagram.mockImplementation(() => <NativeDiagram />);
  render(
    <ConfirmationDialogContext.Provider value={{ showConfirmation }}>
      {createElement(Adapter, props)}
    </ConfirmationDialogContext.Provider>
  );
  return showConfirmation;
}

describe.each(['en', 'fr'])('floor-plan removal confirmation in %s', (language) => {
  it('explains view removal and retains the original confirmation callback', async () => {
    await i18n.changeLanguage(language);
    const show = setup();
    await userEvent.click(screen.getByRole('button', { name: 'Native Delete' }));
    const text = (key: string) => i18n.t(`plan.${key}`, { ns: 'home-electrical-designer' });
    expect(show).toHaveBeenCalledWith(
      text('removeEquipment'),
      text('removeConfirmation'),
      text('removeEquipment'),
      onConfirm
    );
    expect(onConfirm).not.toHaveBeenCalled();
    show.mock.calls[0][3]();
    expect(onConfirm).toHaveBeenCalledOnce();
  });

  it('retains unrelated confirmation prompts and restores native deletion after unregistration', async () => {
    await i18n.changeLanguage(language);
    const show = setup();
    await userEvent.click(screen.getByRole('button', { name: 'Other operation' }));
    expect(show).toHaveBeenLastCalledWith('Other operation', 'Other message', 'OK', onConfirm);
    await userEvent.click(screen.getByRole('button', { name: 'Remove background node' }));
    await userEvent.click(screen.getByRole('button', { name: 'Native Delete' }));
    expect(show.mock.calls[1][0]).toBe(i18n.t('useDeletionConfirmationDialog.title', { ns: 'sirius-components-core' }));
    expect(show.mock.calls[1][3]).toBe(onConfirm);
  });
});

describe('representation factory integration', () => {
  it.each(['siriusComponents://representation?type=Table', 'custom', 'siriusComponents://representation?type=Tree'])(
    'leaves %s to native factories',
    (kind) => {
      expect(floorPlanConfirmationFactory(representation(kind))).toBeNull();
    }
  );

  it('prepends the adapter without discarding native factories and delegates other merge rules', () => {
    const native = vi.fn(),
      custom = vi.fn();
    const strategy = new FloorPlanConfirmationMergeStrategy();
    expect(
      strategy.mergeDataExtensions(
        representationFactoryExtensionPoint.identifier,
        { identifier: 'native', data: [native] },
        { identifier: 'hed', data: [custom] }
      )
    ).toEqual({ identifier: 'hed', data: [custom, native] });
    const existing = { identifier: 'existing', data: ['value'] };
    const base = vi
      .spyOn(DefaultExtensionRegistryMergeStrategy.prototype, 'mergeDataExtensions')
      .mockReturnValue(existing);
    expect(strategy.mergeDataExtensions('other', existing, { identifier: 'new', data: [] })).toBe(existing);
    expect(base).toHaveBeenCalledWith('other', existing, { identifier: 'new', data: [] });
  });
});
