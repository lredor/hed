/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.modelexplorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.eclipse.sirius.components.collaborative.api.ChangeKind;
import org.eclipse.sirius.components.collaborative.trees.api.ISingleClickTreeItemContextMenuEntryExecutor;
import org.eclipse.sirius.components.collaborative.trees.api.ITreeInput;
import org.eclipse.sirius.components.collaborative.trees.palette.api.ITreeItemPaletteCustomizer;
import org.eclipse.sirius.components.palette.dto.Palette;
import org.eclipse.sirius.components.palette.dto.IPaletteEntry;
import org.eclipse.sirius.components.collaborative.trees.dto.palette.SingleClickTreeItemTool;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IIdentityService;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.representations.Failure;
import org.eclipse.sirius.components.representations.IStatus;
import org.eclipse.sirius.components.representations.Success;
import org.eclipse.sirius.components.trees.Tree;
import org.eclipse.sirius.components.trees.TreeItem;
import org.eclipse.sirius.components.trees.description.TreeDescription;
import org.springframework.stereotype.Service;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import hed.domain.HedDomain;
import hed.domain.Messages;
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.DINRail;
import hed.starter.HedDomainIcons;
import hed.starter.representations.CatalogDropService;

/** Offers one native DIN rail creation action per catalog entry, including variants of the same category. */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DINRailCatalogCreationTools implements ITreeItemPaletteCustomizer, ISingleClickTreeItemContextMenuEntryExecutor {
    private static final String PREFIX = "hed-rail-create-type:";

    private final IObjectSearchService search;

    private final IIdentityService identity;

    private final CatalogDropService drops;

    private final Locale locale;

    /**
     * Connects native menu discovery and invocation to the shared catalog creation service.
     * @param search resolves the selected rail and exact catalog entry
     * @param identity supplies stable type identifiers, independent of name and order
     * @param drops validates and creates the selected variant on its rail
     * @param locale selects creation labels and diagnostics
     */
    public DINRailCatalogCreationTools(IObjectSearchService search, IIdentityService identity, CatalogDropService drops, Locale locale) {
        this.search = search;
        this.identity = identity;
        this.drops = drops;
        this.locale = locale;
    }

    @Override
    public boolean canHandle(IEditingContext context, TreeDescription description, Tree tree, TreeItem item) {
        return Messages.text(locale, "explorer.title").equals(description.getLabel()) && search.getObject(context, item.getId()).filter(DINRail.class::isInstance).isPresent();
    }

    @Override
    public Palette customize(IEditingContext context, TreeDescription description, Tree tree, TreeItem item, Palette palette) {
        var result = new ArrayList<IPaletteEntry>();
        search.getObject(context, item.getId()).filter(DINRail.class::isInstance).map(DINRail.class::cast).ifPresent(rail -> result.addAll(entries(rail)));
        result.addAll(palette.paletteEntries());
        return new Palette(palette.id(), palette.quickAccessTools(), result);
    }

    /**
     * Lists every modular catalog entry that fits the rail in stored order, without merging equal categories or labels.
     * @param rail the destination DIN rail
     * @return native creation actions whose identifiers remain valid across catalog reordering
     */
    public List<SingleClickTreeItemTool> entries(DINRail rail) {
        var home = HedDomain.dwelling(rail);
        return home == null || home.getCatalog() == null ? List.of()
                : home.getCatalog().getDeviceTypes().stream().filter(type -> HedDomain.canAddToRail(rail, type)).map(type -> new SingleClickTreeItemTool(PREFIX + identity.getId(type),
                        Messages.text(locale, "action.create") + " " + HedDomain.label(type, locale), List.of(HedDomainIcons.of(type)), false, List.of())).toList();
    }

    @Override
    public boolean canExecute(IEditingContext context, TreeDescription description, String entryId, ITreeInput input) {
        return Messages.text(locale, "explorer.title").equals(description.getLabel()) && entryId.startsWith(PREFIX);
    }

    @Override
    public IStatus execute(IEditingContext context, TreeDescription description, Tree tree, TreeItem item, String entryId, ITreeInput input) {
        var rail = search.getObject(context, item.getId()).filter(DINRail.class::isInstance).map(DINRail.class::cast);
        var type = entryId.startsWith(PREFIX) ? search.getObject(context, entryId.substring(PREFIX.length())).filter(DeviceType.class::isInstance).map(DeviceType.class::cast)
                : Optional.<DeviceType> empty();
        if (rail.isEmpty() || type.isEmpty()) {
            return new Failure(Messages.text(locale, "foreignReference"));
        }
        try {
            drops.create(rail.get(), List.of(type.get()));
            return new Success(ChangeKind.SEMANTIC_CHANGE, Map.of());
        } catch (IllegalArgumentException error) {
            return new Failure(Messages.text(locale, error.getMessage()));
        }
    }
}
