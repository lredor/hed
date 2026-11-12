/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations;

import org.eclipse.sirius.components.tables.Table;
import org.eclipse.sirius.web.domain.boundedcontexts.representationdata.RepresentationMetadata;
import hed.starter.HedDomainIcons;

/** Document pictograms distinguish saved representations from their semantic owners. */
public final class HedRepresentationIcons {
    public static final String TABLE = HedDomainIcons.path("file-table-outline");

    public static final String FLOOR_PLAN = HedDomainIcons.path("file-image-outline");

    public static final String PANEL = HedDomainIcons.path("file-cad");

    public static final String CIRCUITS = HedDomainIcons.path("file-tree-outline");

    private HedRepresentationIcons() {
    }

    /**
     * Supplies the current representation icon even when saved metadata retains an older semantic pictogram.
     * Metadata, labels and representation identifiers are left intact; unrelated custom diagram icons are retained.
     *
     * @param representation the non-null metadata displayed in Model Explorer
     * @return the current document pictogram, or an existing custom icon
     */
    public static String of(RepresentationMetadata representation) {
        if (Table.KIND.equals(representation.getKind())) {
            return TABLE;
        }
        var icon = representation.getIconURLs().stream().map(value -> value.url()).findFirst().orElse(FLOOR_PLAN);
        // Older saved diagrams need the same visual distinction as newly created ones, without a data migration.
        return switch (icon) {
            case "/hed/mdi/floor-plan.svg", "/api/images/hed/mdi/floor-plan.svg" -> FLOOR_PLAN;
            case "/hed/mdi/view-dashboard-outline.svg", "/api/images/hed/mdi/view-dashboard-outline.svg" -> PANEL;
            case "/hed/mdi/vector-polyline.svg", "/api/images/hed/mdi/vector-polyline.svg" -> CIRCUITS;
            default -> icon;
        };
    }
}
