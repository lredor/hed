/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.project;

import java.util.Objects;
import java.util.UUID;
import org.eclipse.sirius.web.application.editingcontext.services.EditingContextAdapter;
import org.eclipse.sirius.web.domain.boundedcontexts.project.services.api.IProjectSearchService;
import org.eclipse.sirius.web.domain.boundedcontexts.projectsemanticdata.services.api.IProjectSemanticDataSearchService;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;
import hed.homeelectrical.Dwelling;

/** Resolve the native Sirius project name without adding project metadata to the domain model. */
@Service
public class ProjectNames implements ProjectNameProvider {
    private final IProjectSearchService projects;

    private final IProjectSemanticDataSearchService semanticData;

    public ProjectNames(IProjectSearchService projects, IProjectSemanticDataSearchService semanticData) {
        this.projects = projects;
        this.semanticData = semanticData;
    }

    @Override
    public String getName(Dwelling home) {
        if (home.eResource() != null && home.eResource().getResourceSet() != null) {
            var contextId = home.eResource().getResourceSet().eAdapters().stream().filter(EditingContextAdapter.class::isInstance).map(EditingContextAdapter.class::cast)
                    .map(EditingContextAdapter::getEditingContextId).findFirst();
            if (contextId.isPresent()) {
                var project = semanticData.findBySemanticDataId(AggregateReference.to(UUID.fromString(contextId.get()))).flatMap(link -> projects.findById(link.getProject().getId()));
                if (project.isPresent()) {
                    return project.get().getName();
                }
            }
        }
        return Objects.toString(home.getName(), "");
    }
}
