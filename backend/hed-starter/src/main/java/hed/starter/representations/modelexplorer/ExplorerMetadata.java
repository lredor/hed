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

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.eclipse.sirius.components.core.RepresentationMetadata;
import org.eclipse.sirius.components.core.api.IRepresentationMetadataProvider;
import org.eclipse.sirius.components.core.api.IURLParser;
import org.eclipse.sirius.components.trees.Tree;
import org.eclipse.sirius.web.application.views.explorer.ExplorerEventProcessorFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import hed.domain.Messages;

/**
 * Ephemeral explorer metadata is required by the tree palette and context menus.
 */
@Service
public class ExplorerMetadata implements IRepresentationMetadataProvider {
    private final IURLParser parser;

    private final ObjectProvider<ViewContribution> views;

    private final Locale locale;

    public ExplorerMetadata(IURLParser parser, ObjectProvider<ViewContribution> views, Locale locale) {
        this.parser = parser;
        this.views = views;
        this.locale = locale;
    }

    @Override
    public Optional<RepresentationMetadata> getMetadata(String contextId, String representationId) {
        if (!representationId.startsWith("explorer://")) {
            return Optional.empty();
        }
        var descriptionIds = parser.getParameterValues(representationId).get(ExplorerEventProcessorFactory.TREE_DESCRIPTION_ID_PARAMETER);
        var expected = views.getObject().explorerDescriptionId();
        if (descriptionIds == null || descriptionIds.isEmpty() || expected.isEmpty() || !expected.equals(descriptionIds.get(0))) {
            return Optional.empty();
        }
        return Optional.of(RepresentationMetadata.newRepresentationMetadata(representationId).kind(Tree.KIND).label(Messages.text(locale, "explorer.title")).descriptionId(expected)
                .iconURLs(List.of("/icons/svg/Default.svg")).build());
    }
}
