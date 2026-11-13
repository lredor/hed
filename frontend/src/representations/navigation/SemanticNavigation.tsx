/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { useSelection, useSelectionTargets } from '@eclipse-sirius/sirius-components-core';
import type { PaletteToolContributionComponentProps } from '@eclipse-sirius/sirius-components-palette';
import { ToolListItemText } from '@eclipse-sirius/sirius-components-palette';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import MenuItem from '@mui/material/MenuItem';
import ListItemIcon from '@mui/material/ListItemIcon';

/** Selects a semantic destination and reveals its path using Sirius Web's explorer target. */
export function useNavigateToSemanticElement() {
  const { setSelection } = useSelection();
  const { selectionTargets } = useSelectionTargets();
  return (id: string) => {
    const selection = { entries: [{ id }] };
    setSelection(selection);
    selectionTargets.find((target) => target.id === 'explorer')?.applySelection(selection);
  };
}

interface NavigationPaletteToolProps
  extends Pick<PaletteToolContributionComponentProps, 'searchedValue' | 'asLastToolUsed'> {
  toolId: string;
  label: string;
  onInvoke: () => void;
}

/** Uses native spacing, truncation, search highlighting and tooltip behavior for semantic navigation actions. */
export function NavigationPaletteTool({
  toolId,
  label,
  searchedValue,
  asLastToolUsed,
  onInvoke,
}: NavigationPaletteToolProps) {
  if (searchedValue && !label.toLocaleLowerCase().includes(searchedValue.toLocaleLowerCase())) return null;
  if (asLastToolUsed)
    return (
      <Tooltip title={label}>
        <IconButton aria-label={label} onClick={onInvoke}>
          <ArrowForwardIcon />
        </IconButton>
      </Tooltip>
    );
  // Tree palettes render Rename/Delete as MenuItem entries, whose icon gutter differs from diagram ToolListItem buttons.
  return (
    <Tooltip title={label} placement="right">
      <MenuItem
        aria-label={label}
        data-testid={`tool-${label}`}
        onClick={(event) => {
          event.stopPropagation();
          onInvoke();
        }}>
        <ListItemIcon>
          <ArrowForwardIcon fontSize="small" />
        </ListItemIcon>
        <ToolListItemText label={label} searchedValue={searchedValue} />
      </MenuItem>
    </Tooltip>
  );
}
