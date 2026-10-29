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

import java.util.Locale;
import java.util.UUID;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IEditingContextPersistenceService;
import org.eclipse.sirius.components.emf.ResourceMetadataAdapter;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.components.events.ICause;
import org.eclipse.sirius.web.application.project.services.api.ISemanticDataInitializer;
import org.springframework.stereotype.Service;
import hed.domain.Messages;
import hed.domain.HedSamples;
import hed.homeelectrical.HomeElectricalPackage;

@Service
public class ProjectInitializer implements ISemanticDataInitializer {
    private final IEditingContextPersistenceService persistence;

    private final Locale locale;

    public ProjectInitializer(IEditingContextPersistenceService persistence, Locale locale) {
        this.persistence = persistence;
        this.locale = locale;
    }

    @Override
    public boolean canHandle(String id) {
        return ProjectTemplates.EMPTY.equals(id) || ProjectTemplates.EXAMPLE.equals(id);
    }

    @Override
    public void handle(ICause cause, IEditingContext context, String id) {
        if (canHandle(id) && context instanceof IEMFEditingContext emf) {
            var rs = emf.getDomain().getResourceSet();
            rs.getPackageRegistry().put(HomeElectricalPackage.eNS_URI, HomeElectricalPackage.eINSTANCE);
            var resource = new JSONResourceFactory().createResourceFromPath(UUID.randomUUID().toString());
            resource.eAdapters().add(new ResourceMetadataAdapter("Home Electrical Designer"));
            rs.getResources().add(resource);
            resource.getContents().add(ProjectTemplates.EXAMPLE.equals(id) ? HedSamples.example(locale) : HedSamples.empty(Messages.text(locale, "new.Dwelling"), locale));
            persistence.persist(cause, context);
        }
    }
}
