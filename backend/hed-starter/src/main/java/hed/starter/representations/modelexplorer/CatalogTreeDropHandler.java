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
import hed.starter.representations.CatalogDropService;

/** Create modular devices from catalog types dropped onto rails in the domain explorer. */
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
     * @param search resolves the selected catalog types and destination rail
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
            // An indexed drop targets a gap between rows; creation requires dropping onto the rail itself.
            if (input.index() >= 0) {
                throw new IllegalArgumentException("drop.catalogRail");
            }
            var target = search.getObject(context, input.targetElementId()).orElse(null);
            var sources = input.droppedElementIds().stream().map(id -> search.getObject(context, id).orElse(null)).toList();
            drops.create(target, sources);
            return new Success(ChangeKind.SEMANTIC_CHANGE, Map.of());
        } catch (IllegalArgumentException ex) {
            return new Failure(Messages.text(locale, ex.getMessage()));
        }
    }
}
