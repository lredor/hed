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
 * @param childrenLayoutStrategy the native strategy for child views
 */
public record FloorPlanNodeStyle(boolean backgroundNode, String imageURL, String caption, boolean hasImage, ILayoutStrategy childrenLayoutStrategy) implements INodeStyle {
    @Override
    public ILayoutStrategy getChildrenLayoutStrategy() {
        return childrenLayoutStrategy;
    }
}
