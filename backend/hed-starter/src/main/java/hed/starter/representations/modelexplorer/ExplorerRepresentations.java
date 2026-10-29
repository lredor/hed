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
import java.util.UUID;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IIdentityService;
import org.eclipse.sirius.components.representations.IRepresentationDescription;
import org.eclipse.sirius.components.representations.IRepresentationRenderVariableCustomizer;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.trees.description.TreeDescription;
import org.eclipse.sirius.web.domain.boundedcontexts.representationdata.services.api.IRepresentationMetadataSearchService;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;
import hed.domain.Messages;

/** Supply native representation metadata once per render of the domain explorer. */
@Service
public class ExplorerRepresentations implements IRepresentationRenderVariableCustomizer {
    public static final String VARIABLE = "hedRepresentations";

    private final IRepresentationMetadataSearchService metadata;

    private final IIdentityService identity;

    private final Locale locale;

    public ExplorerRepresentations(IRepresentationMetadataSearchService metadata, IIdentityService identity, Locale locale) {
        this.metadata = metadata;
        this.identity = identity;
        this.locale = locale;
    }

    @Override
    public VariableManager customize(IRepresentationDescription description, VariableManager variables) {
        if (description instanceof TreeDescription tree && Messages.text(locale, "explorer.title").equals(tree.getLabel())) {
            var context = variables.get(IEditingContext.EDITING_CONTEXT, IEditingContext.class);
            if (context.isPresent()) {
                var child = variables.createChild();
                child.put(VARIABLE, metadata.findAllRepresentationMetadataBySemanticData(AggregateReference.to(UUID.fromString(context.get().getId()))));
                return child;
            }
        }
        return variables;
    }

    /** Palette evaluation has its own variables, independently of the tree renderer. */
    public boolean hasHedRepresentations(Object source, IEditingContext context) {
        return source instanceof EObject && metadata.existAnyRepresentationMetadataForSemanticDataAndTargetObjectId(AggregateReference.to(UUID.fromString(context.getId())), identity.getId(source));
    }
}
