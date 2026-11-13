/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { gql, useQuery } from '@apollo/client';
import { useMultiToast } from '@eclipse-sirius/sirius-components-core';
import type { GQLSelect, PropertySectionLabelDecoratorProps } from '@eclipse-sirius/sirius-components-forms';
import type { TreeItemContextMenuComponentProps } from '@eclipse-sirius/sirius-components-trees';
import { TreePaletteContext } from '@eclipse-sirius/sirius-components-trees';
import type { PaletteToolContributionComponentProps } from '@eclipse-sirius/sirius-components-palette';
import { usePalette } from '@eclipse-sirius/sirius-components-palette';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import IconButton from '@mui/material/IconButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import MenuItem from '@mui/material/MenuItem';
import Tooltip from '@mui/material/Tooltip';
import { useContext, useEffect } from 'react';
import { useTranslation } from 'react-i18next';

import { NavigationPaletteTool, useNavigateToSemanticElement } from './SemanticNavigation';

export const supplyCircuitQuery = gql`
  query HedSupplyCircuit($editingContextId: ID!, $objectId: ID!) {
    viewer {
      editingContext(editingContextId: $editingContextId) {
        hedSupplyCircuitId(objectId: $objectId)
      }
    }
  }
`;

/** Navigates beside a supply-circuit selector or a branch's parent circuit, including in read-only views. */
export function SupplyCircuitPropertyNavigation({ widget }: PropertySectionLabelDecoratorProps) {
  const navigate = useNavigateToSemanticElement();
  const { t } = useTranslation('home-electrical-designer');
  if (
    widget.__typename !== 'Select' ||
    !/^hed\.(Outlet|ElectricalLoad|ControlDevice|CircuitBranch)\.circuit$/.test(widget.id)
  )
    return null;
  const id = (widget as GQLSelect).value;
  const label = t('navigation.supplyCircuit');
  return (
    <Tooltip title={label}>
      <span>
        <IconButton size="small" aria-label={label} disabled={!id} onClick={() => navigate(id)}>
          <ArrowForwardIcon fontSize="small" />
        </IconButton>
      </span>
    </Tooltip>
  );
}

/** Reads the current supply circuit without inferring it from diagram placement or modifying any assignment. */
function useSupplyCircuitId(editingContextId: string, objectId: string | undefined) {
  const { addErrorMessage } = useMultiToast();
  const { data, error } = useQuery<{ viewer: { editingContext: { hedSupplyCircuitId: string | null } | null } }>(
    supplyCircuitQuery,
    {
      variables: { editingContextId, objectId },
      skip: !objectId,
      fetchPolicy: 'network-only',
    }
  );
  useEffect(() => {
    if (error) addErrorMessage(error.message);
  }, [error, addErrorMessage]);
  return data?.viewer.editingContext?.hedSupplyCircuitId;
}

/** Offers a project-scoped circuit destination only when the clicked object has an assigned circuit. */
export function SupplyCircuitExplorerNavigation({
  editingContextId,
  item,
  onClose,
}: TreeItemContextMenuComponentProps) {
  const navigate = useNavigateToSemanticElement();
  const { t } = useTranslation('home-electrical-designer');
  const id = useSupplyCircuitId(editingContextId, item.id);
  if (!id) return null;
  return (
    <MenuItem
      onClick={() => {
        navigate(id);
        onClose();
      }}>
      <ListItemIcon>
        <ArrowForwardIcon fontSize="small" />
      </ListItemIcon>
      <ListItemText primary={t('navigation.supplyCircuit')} />
    </MenuItem>
  );
}

/** Offers the same navigation in Sirius' tree palette, including its search and last-used tool presentation. */
export function SupplyCircuitPaletteNavigation({
  onInvoked,
  searchedValue,
  asLastToolUsed,
}: PaletteToolContributionComponentProps) {
  const { editingContextId, item, onClose } = useContext(TreePaletteContext);
  const id = useSupplyCircuitId(editingContextId, item?.id);
  const navigate = useNavigateToSemanticElement();
  const { hidePalette } = usePalette();
  const { t } = useTranslation('home-electrical-designer');
  const label = t('navigation.supplyCircuit');
  if (!id) return null;
  const invoke = () => {
    onInvoked();
    hidePalette();
    navigate(id);
    onClose();
  };
  return (
    <NavigationPaletteTool
      toolId="hed-navigate-supply-circuit"
      label={label}
      searchedValue={searchedValue}
      asLastToolUsed={asLastToolUsed}
      onInvoke={invoke}
    />
  );
}
