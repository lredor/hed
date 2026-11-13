/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.diagrams.circuits;

import org.eclipse.sirius.components.diagrams.ILayoutStrategy;
import org.eclipse.sirius.components.diagrams.INodeStyle;

/**
 * Marks circuit junctions for a dedicated, easily manipulated frontend rendering without adding a semantic concept.
 * @param childrenLayoutStrategy native layout metadata required when saving and reopening the diagram
 */
public record CircuitJunctionNodeStyle(ILayoutStrategy childrenLayoutStrategy) implements INodeStyle {
    @Override
    public ILayoutStrategy getChildrenLayoutStrategy() {
        return childrenLayoutStrategy;
    }
}
