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
 * Presentation metadata for the circuit diagram's ordered rails and tall modular devices.
 * @param role rail or device
 * @param order index within the semantic parent list
 * @param caption stored device name or localized container label
 * @param iconURL existing shared MDI pictogram
 * @param childrenLayoutStrategy native layout metadata required by the diagram protocol
 */
public record CircuitBoardNodeStyle(String role, int order, String caption, String iconURL, ILayoutStrategy childrenLayoutStrategy) implements INodeStyle {
    @Override
    public ILayoutStrategy getChildrenLayoutStrategy() {
        return childrenLayoutStrategy;
    }
}
