/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.diagrams.floorplan;

import org.eclipse.sirius.components.diagrams.INodeStyle;
import org.eclipse.sirius.components.diagrams.ILayoutStrategy;

/**
 * Carries floor-plan rendering data through GraphQL and representation persistence.
 * @param backgroundNode whether the view is the fixed storey background
 * @param imageURL a local project-image or Material Design pictogram URL
 * @param caption the stored semantic name displayed in the header, tooltip or optional outside label
 * @param hasImage whether an imported background is available
 * @param rows number of physical outlet rows; one for a standalone pictogram
 * @param columns number of physical outlet columns; one for a standalone pictogram
 * @param childrenLayoutStrategy the native strategy for child views
 */
public record FloorPlanNodeStyle(boolean backgroundNode, String imageURL, String caption, boolean hasImage, ILayoutStrategy childrenLayoutStrategy, int rows, int columns) implements INodeStyle {
    /**
     * Creates an ordinary pictogram or background with no grouped grid.
     * @param backgroundNode whether this is the background
     * @param imageURL the image URL
     * @param caption the displayed name
     * @param hasImage whether an image is available
     * @param childrenLayoutStrategy the layout of child views
     */
    public FloorPlanNodeStyle(boolean backgroundNode, String imageURL, String caption, boolean hasImage, ILayoutStrategy childrenLayoutStrategy) {
        this(backgroundNode, imageURL, caption, hasImage, childrenLayoutStrategy, 1, 1);
    }

    @Override
    public ILayoutStrategy getChildrenLayoutStrategy() {
        return childrenLayoutStrategy;
    }
}
