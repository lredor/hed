/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { createContext, useEffect, useRef, type ReactNode } from 'react';
import { DRAG_SOURCES_TYPE } from '@eclipse-sirius/sirius-components-core';

type Source = { ids: string[]; project: string };
const project = () => window.location.pathname.match(/^\/projects\/([^/]+)/)?.[1];

/** Recovers sources only when an active, same-project explorer drag arrives with an empty browser payload. */
export const ExplorerDragSourcesContext = createContext<(transfer: DataTransfer | null) => string[]>(() => []);

/** Retains native source identifiers for one document-local drag; nothing is stored beyond the gesture. */
export function ExplorerDragSourcesProvider({ children }: { children: ReactNode }) {
  const active = useRef<Source | null>(null);
  useEffect(() => {
    const clear = () => {
      active.current = null;
    };
    const start = (event: DragEvent) => {
      clear();
      const target = event.target instanceof Element ? event.target : null;
      const item =
        target?.closest('[data-treeitemid]') ??
        target?.closest('[draggable="true"]')?.querySelector('[data-treeitemid]');
      const owner = project();
      if (!item || !owner) return;
      try {
        const ids: unknown = JSON.parse(event.dataTransfer?.getData(DRAG_SOURCES_TYPE) ?? '');
        if (
          Array.isArray(ids) &&
          ids.length > 0 &&
          ids.every((id) => typeof id === 'string' && id.length > 0) &&
          ids.includes(item.getAttribute('data-treeitemid'))
        ) {
          active.current = { ids: [...ids], project: owner };
        }
      } catch {
        /* An unrelated or malformed drag must not reuse a previous selection. */
      }
    };
    // The tree sets its native payload while bubbling; observe it after that handler has run.
    const drop = () => {
      const gesture = active.current;
      // Browsers may run microtasks between capture and React's listener; wait for the entire event.
      setTimeout(() => {
        if (active.current === gesture) clear();
      }, 0);
    };
    const key = (event: KeyboardEvent) => {
      if (event.key === 'Escape') clear();
    };
    document.addEventListener('dragstart', start);
    document.addEventListener('drop', drop, true);
    document.addEventListener('dragend', clear, true);
    document.addEventListener('keydown', key, true);
    window.addEventListener('pagehide', clear);
    return () => {
      clear();
      document.removeEventListener('dragstart', start);
      document.removeEventListener('drop', drop, true);
      document.removeEventListener('dragend', clear, true);
      document.removeEventListener('keydown', key, true);
      window.removeEventListener('pagehide', clear);
    };
  }, []);
  const recover = (transfer: DataTransfer | null) => {
    if (
      !transfer ||
      transfer.getData(DRAG_SOURCES_TYPE) ||
      Array.from(transfer.types).some((type) => type !== DRAG_SOURCES_TYPE)
    )
      return [];
    const source = active.current;
    return source && source.project === project() ? [...source.ids] : [];
  };
  return <ExplorerDragSourcesContext.Provider value={recover}>{children}</ExplorerDragSourcesContext.Provider>;
}
