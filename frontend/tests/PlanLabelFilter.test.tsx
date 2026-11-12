/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import { usePlanLabels } from '../src/representations/diagrams/floorplan/PlanLabelFilter';

function Labels({ diagram, name }: { diagram: string; name: string }) {
  const [visible, setVisible] = usePlanLabels(diagram);
  return (
    <button aria-pressed={visible} onClick={() => setVisible(!visible)}>
      {name}
    </button>
  );
}

describe('floor-plan label preference', () => {
  it('defaults to hidden, synchronizes consumers and isolates diagrams', async () => {
    render(
      <>
        <Labels diagram="shared" name="Header" />
        <Labels diagram="shared" name="Equipment" />
        <Labels diagram="other" name="Other plan" />
      </>
    );
    expect(screen.getByRole('button', { name: 'Header' })).toHaveAttribute('aria-pressed', 'false');
    await userEvent.click(screen.getByRole('button', { name: 'Header' }));
    expect(screen.getByRole('button', { name: 'Equipment' })).toHaveAttribute('aria-pressed', 'true');
    expect(screen.getByRole('button', { name: 'Other plan' })).toHaveAttribute('aria-pressed', 'false');
    expect(localStorage.getItem('hed.floor-plan.labels:shared')).toBe('true');
    await userEvent.click(screen.getByRole('button', { name: 'Equipment' }));
    expect(screen.getByRole('button', { name: 'Header' })).toHaveAttribute('aria-pressed', 'false');
  });

  it('retains a saved preference when a plan is reopened', () => {
    localStorage.setItem('hed.floor-plan.labels:saved', 'true');
    const first = render(<Labels diagram="saved" name="Labels" />);
    expect(screen.getByRole('button')).toHaveAttribute('aria-pressed', 'true');
    first.unmount();
    render(<Labels diagram="saved" name="Labels" />);
    expect(screen.getByRole('button')).toHaveAttribute('aria-pressed', 'true');
  });

  it('responds to preference changes from another browser tab', () => {
    render(<Labels diagram="tabs" name="Labels" />);
    act(() => {
      localStorage.setItem('hed.floor-plan.labels:tabs', 'true');
      window.dispatchEvent(new StorageEvent('storage', { key: 'hed.floor-plan.labels:tabs' }));
    });
    expect(screen.getByRole('button')).toHaveAttribute('aria-pressed', 'true');
  });

  it('still synchronizes labels when browser storage is unavailable', async () => {
    vi.spyOn(Storage.prototype, 'getItem').mockImplementation(() => {
      throw new Error('blocked');
    });
    vi.spyOn(Storage.prototype, 'setItem').mockImplementation(() => {
      throw new Error('blocked');
    });
    render(
      <>
        <Labels diagram="blocked" name="Header" />
        <Labels diagram="blocked" name="Equipment" />
      </>
    );
    expect(screen.getByRole('button', { name: 'Header' })).toHaveAttribute('aria-pressed', 'false');
    await userEvent.click(screen.getByRole('button', { name: 'Header' }));
    expect(screen.getByRole('button', { name: 'Equipment' })).toHaveAttribute('aria-pressed', 'true');
  });
});
