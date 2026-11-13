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
import { useMultiToast, useSelection, useSelectionTargets } from '@eclipse-sirius/sirius-components-core';
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
import ListItemButton from '@mui/material/ListItemButton';
import Tooltip from '@mui/material/Tooltip';
import { useContext, useEffect } from 'react';
import { useTranslation } from 'react-i18next';

export const deviceTypeQuery = gql`
  query HedDeviceType($editingContextId: ID!, $objectId: ID!) {
    viewer {
      editingContext(editingContextId: $editingContextId) {
        hedDeviceTypeId(objectId: $objectId)
      }
    }
  }
`;

/** Updates the workbench selection and uses the native explorer target to expand the catalog path. */
export function useNavigateToDeviceType() {
  const { setSelection } = useSelection();
  const { selectionTargets } = useSelectionTargets();
  return (id: string) => {
    const selection = { entries: [{ id }] };
    setSelection(selection);
    selectionTargets.find((target) => target.id === 'explorer')?.applySelection(selection);
  };
}

/** Adds navigation beside the installed device's type selector, including in read-only views. */
export function DeviceTypePropertyNavigation({ widget }: PropertySectionLabelDecoratorProps) {
  const navigate = useNavigateToDeviceType();
  const { t } = useTranslation('home-electrical-designer');
  if (
    widget.__typename !== 'Select' ||
    !/^hed\.(Outlet|ElectricalLoad|ControlDevice|ModularDevice|EnergyMeter|ServiceCircuitBreaker)\.deviceType$/.test(
      widget.id
    )
  )
    return null;
  const id = (widget as GQLSelect).value;
  const label = t('navigation.deviceType');
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

/** Reads the latest type, including for virtual occurrences, without performing a model edit. */
function useDeviceTypeId(editingContextId: string, objectId: string | undefined) {
  const { addErrorMessage } = useMultiToast();
  const { data, error } = useQuery<{ viewer: { editingContext: { hedDeviceTypeId: string | null } | null } }>(
    deviceTypeQuery,
    {
      variables: { editingContextId, objectId },
      skip: !objectId,
      fetchPolicy: 'network-only',
    }
  );
  useEffect(() => {
    if (error) addErrorMessage(error.message);
  }, [error, addErrorMessage]);
  return data?.viewer.editingContext?.hedDeviceTypeId;
}

/** Resolves the clicked occurrence's current type and contributes a read-only action only when it exists. */
export function DeviceTypeExplorerNavigation({ editingContextId, item, onClose }: TreeItemContextMenuComponentProps) {
  const navigate = useNavigateToDeviceType();
  const { t } = useTranslation('home-electrical-designer');
  const id = useDeviceTypeId(editingContextId, item.id);
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
      <ListItemText primary={t('navigation.deviceType')} />
    </MenuItem>
  );
}

/** Offers the same navigation in Sirius' tree palette, including its search and last-used tool presentation. */
export function DeviceTypePaletteNavigation({
  onInvoked,
  searchedValue,
  asLastToolUsed,
}: PaletteToolContributionComponentProps) {
  const { editingContextId, item, onClose } = useContext(TreePaletteContext);
  const id = useDeviceTypeId(editingContextId, item?.id);
  const navigate = useNavigateToDeviceType();
  const { hidePalette } = usePalette();
  const { t } = useTranslation('home-electrical-designer');
  const label = t('navigation.deviceType');
  if (!id || (searchedValue && !label.toLocaleLowerCase().includes(searchedValue.toLocaleLowerCase()))) return null;
  const invoke = () => {
    onInvoked();
    hidePalette();
    navigate(id);
    onClose();
  };
  if (asLastToolUsed)
    return (
      <Tooltip title={label}>
        <IconButton aria-label={label} onClick={invoke}>
          <ArrowForwardIcon />
        </IconButton>
      </Tooltip>
    );
  return (
    <ListItemButton component="button" onClick={invoke} aria-label={label}>
      <ListItemIcon>
        <ArrowForwardIcon fontSize="small" />
      </ListItemIcon>
      <ListItemText primary={label} />
    </ListItemButton>
  );
}
