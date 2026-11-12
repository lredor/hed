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
import org.eclipse.sirius.components.collaborative.trees.api.ITreeFilterProvider;
import org.eclipse.sirius.components.collaborative.trees.api.TreeFilter;
import org.eclipse.sirius.components.trees.description.TreeDescription;
import org.springframework.stereotype.Service;
import hed.domain.Messages;

/** Adds the optional circuit-completeness highlight to the HED explorer's native filter menu. */
@Service
public class CircuitCompletenessFilterProvider implements ITreeFilterProvider {
    public static final String FILTER_ID = "hed-highlight-incomplete-circuits";

    private final Locale locale;

    /**
     * Creates the localized, default-off filter contribution.
     * @param locale the language of the application and domain explorer
     */
    public CircuitCompletenessFilterProvider(Locale locale) {
        this.locale = locale;
    }

    @Override
    public List<TreeFilter> get(String editingContextId, TreeDescription description) {
        return Messages.text(this.locale, "explorer.title").equals(description.getLabel())
                ? List.of(new TreeFilter(FILTER_ID, Messages.text(this.locale, "explorer.highlightIncompleteCircuits"), false))
                : List.of();
    }
}
