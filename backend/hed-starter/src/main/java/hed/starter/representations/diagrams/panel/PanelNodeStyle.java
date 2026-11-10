/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.diagrams.panel;

import org.eclipse.sirius.components.diagrams.ILayoutStrategy;
import org.eclipse.sirius.components.diagrams.INodeStyle;

/** Presentation metadata; placement is derived from semantic rail and device order. */
public record PanelNodeStyle(String role, String imageURL, int order, String caption, int capacityModules, double usedModules, String labelText, ILayoutStrategy childrenLayoutStrategy)
        implements INodeStyle {
    @Override
    public ILayoutStrategy getChildrenLayoutStrategy() {
        return childrenLayoutStrategy;
    }
}
