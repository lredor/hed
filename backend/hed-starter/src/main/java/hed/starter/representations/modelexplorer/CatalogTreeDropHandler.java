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

import java.util.Locale;
import java.util.Map;
import org.eclipse.sirius.components.collaborative.api.ChangeKind;
import org.eclipse.sirius.components.collaborative.trees.api.IDropTreeItemHandler;
import org.eclipse.sirius.components.collaborative.trees.dto.DropTreeItemInput;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.representations.Failure;
import org.eclipse.sirius.components.representations.IStatus;
import org.eclipse.sirius.components.representations.Success;
import org.eclipse.sirius.components.trees.Tree;
import org.eclipse.sirius.components.view.emf.IViewRepresentationDescriptionSearchService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import hed.domain.Messages;
import hed.domain.HedDomain;
import hed.homeelectrical.CircuitBranch;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.DINRail;
import hed.homeelectrical.Room;
import hed.starter.representations.CatalogDropService;

/** Creates devices from catalog types, moves terminal devices between rooms and associates them with circuit branches. */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CatalogTreeDropHandler implements IDropTreeItemHandler {
    private final IObjectSearchService search;

    private final IViewRepresentationDescriptionSearchService descriptions;

    private final CatalogDropService drops;

    private final Locale locale;

    /**
     * Creates the drop handler restricted to the HED explorer projection.
     *
     * @param search resolves catalog types or existing devices and the destination rail, room or branch
     * @param descriptions identifies the domain explorer description
     * @param drops validates and creates the complete selection
     * @param locale selects explorer and rejection messages
     */
    public CatalogTreeDropHandler(IObjectSearchService search, IViewRepresentationDescriptionSearchService descriptions, CatalogDropService drops, Locale locale) {
        this.search = search;
        this.descriptions = descriptions;
        this.drops = drops;
        this.locale = locale;
    }

    @Override
    public boolean canHandle(IEditingContext context, Tree tree) {
        return tree.getId().startsWith("explorer://")
                && descriptions.findById(context, tree.getDescriptionId()).map(description -> description.getName().equals(Messages.text(locale, "explorer.title"))).orElse(false);
    }

    @Override
    public IStatus handle(IEditingContext context, Tree tree, DropTreeItemInput input) {
        try {
            // Indexed drops target gaps; creation, room moves and branch assignment require a semantic destination.
            if (input.index() >= 0) {
                throw new IllegalArgumentException("drop.catalogTarget");
            }
            var target = search.getObject(context, input.targetElementId()).orElse(null);
            var sources = input.droppedElementIds().stream().map(id -> search.getObject(context, id).orElse(null)).toList();
            if (target instanceof CircuitBranch branch) {
                if (sources.isEmpty() || sources.stream().anyMatch(source -> !(source instanceof FieldEquipment))) {
                    throw new IllegalArgumentException("drop.branchEquipment");
                }
                HedDomain.assignToBranch(branch, sources.stream().map(FieldEquipment.class::cast).toList());
            } else if (target instanceof Room room) {
                if (sources.stream().anyMatch(FieldEquipment.class::isInstance)) {
                    if (sources.stream().anyMatch(source -> !(source instanceof FieldEquipment))) {
                        throw new IllegalArgumentException("drop.roomEquipment");
                    }
                    HedDomain.moveToRoom(room, sources.stream().map(FieldEquipment.class::cast).toList());
                } else {
                    drops.createInRoom(room, sources);
                }
            } else if (target instanceof DINRail) {
                drops.create(target, sources);
            } else {
                throw new IllegalArgumentException("drop.catalogTarget");
            }
            return new Success(ChangeKind.SEMANTIC_CHANGE, Map.of());
        } catch (IllegalArgumentException ex) {
            return new Failure(Messages.text(locale, ex.getMessage()));
        }
    }
}
