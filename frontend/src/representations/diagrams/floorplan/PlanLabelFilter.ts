/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { useSyncExternalStore } from 'react';

const changeEvent = 'hed-plan-labels-changed';
const fallback = new Map<string, boolean>();
const key = (diagramId: string) => 'hed.floor-plan.labels:' + diagramId;
const read = (diagramId: string) => {
  if (fallback.has(diagramId)) return fallback.get(diagramId)!;
  try {
    return window.localStorage.getItem(key(diagramId)) === 'true';
  } catch {
    return fallback.get(diagramId) ?? false;
  }
};
const subscribe = (callback: () => void) => {
  window.addEventListener(changeEvent, callback);
  window.addEventListener('storage', callback);
  return () => {
    window.removeEventListener(changeEvent, callback);
    window.removeEventListener('storage', callback);
  };
};

/** A local display preference per diagram, independent of semantic data and layout. */
export function usePlanLabels(diagramId: string) {
  const visible = useSyncExternalStore(
    subscribe,
    () => read(diagramId),
    () => false
  );
  const setVisible = (value: boolean) => {
    try {
      window.localStorage.setItem(key(diagramId), String(value));
      fallback.delete(diagramId);
    } catch {
      fallback.set(diagramId, value);
    }
    window.dispatchEvent(new Event(changeEvent));
  };
  return [visible, setVisible] as const;
}
