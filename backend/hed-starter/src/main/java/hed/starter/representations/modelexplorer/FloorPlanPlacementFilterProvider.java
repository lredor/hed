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

/** Supplies an opt-in placement highlight through Sirius Web's native Model Explorer filter menu. */
@Service
public class FloorPlanPlacementFilterProvider implements ITreeFilterProvider {
    public static final String FILTER_ID = "hed-highlight-unplaced-equipment";

    private final Locale locale;

    /**
     * Creates the localized filter contribution for the HED explorer.
     *
     * @param locale the language used by the application and explorer description
     */
    public FloorPlanPlacementFilterProvider(Locale locale) {
        this.locale = locale;
    }

    @Override
    public List<TreeFilter> get(String editingContextId, TreeDescription treeDescription) {
        if (Messages.text(this.locale, "explorer.title").equals(treeDescription.getLabel())) {
            return List.of(new TreeFilter(FILTER_ID, Messages.text(this.locale, "explorer.highlightUnplaced"), false));
        }
        return List.of();
    }
}
