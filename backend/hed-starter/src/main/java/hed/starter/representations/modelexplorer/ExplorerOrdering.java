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
import org.eclipse.sirius.web.application.views.explorer.dto.ExplorerDescriptionMetadata;
import org.eclipse.sirius.web.application.views.explorer.services.ExplorerDescriptionProvider;
import org.eclipse.sirius.web.application.views.explorer.services.api.IExplorerTreeDescriptionOrderer;
import org.springframework.stereotype.Service;
import hed.domain.Messages;

/** Prefer the domain projection when it is available in the editing context. */
@Service
public class ExplorerOrdering implements IExplorerTreeDescriptionOrderer {
    private final Locale locale;

    public ExplorerOrdering(Locale locale) {
        this.locale = locale;
    }

    @Override
    public void order(List<ExplorerDescriptionMetadata> descriptions) {
        if (descriptions.stream().anyMatch(description -> description.label().equals(Messages.text(locale, "explorer.title")))) {
            descriptions.removeIf(description -> description.id().equals(ExplorerDescriptionProvider.DESCRIPTION_ID));
        }
    }
}
