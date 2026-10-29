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

import java.util.List;
import java.util.Locale;
import org.eclipse.sirius.web.application.project.services.api.IProjectTemplateProvider;
import org.eclipse.sirius.web.application.project.services.api.ProjectTemplate;
import org.eclipse.sirius.web.application.project.services.api.ProjectTemplateNature;
import org.springframework.stereotype.Service;
import hed.domain.Messages;

@Service
public class ProjectTemplates implements IProjectTemplateProvider {
    public static final String EMPTY = "hed-empty", EXAMPLE = "hed-example", NATURE = "siriusWeb://nature?kind=home-electrical-designer";

    private final Locale locale;

    public ProjectTemplates(Locale locale) {
        this.locale = locale;
    }

    @Override
    public List<ProjectTemplate> getProjectTemplates() {
        var natures = List.of(new ProjectTemplateNature(NATURE));
        return List.of(new ProjectTemplate(EMPTY, Messages.text(locale, "template.empty"), "/icons/svg/Default.svg", natures),
                new ProjectTemplate(EXAMPLE, Messages.text(locale, "template.example"), "/icons/svg/Default.svg", natures));
    }
}
