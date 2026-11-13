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

import java.util.Optional;
import org.eclipse.sirius.components.diagrams.ILayoutStrategy;
import org.eclipse.sirius.components.diagrams.INodeStyle;
import org.eclipse.sirius.components.interpreter.AQLInterpreter;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.view.diagram.ImageNodeStyleDescription;
import org.eclipse.sirius.components.view.diagram.NodeStyleDescription;
import org.eclipse.sirius.components.view.emf.diagram.INodeStyleProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/** Selects the junction renderer exclusively for circuit-diagram junction descriptions. */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CircuitJunctionStyleProvider implements INodeStyleProvider {
    /** Discriminator retained in saved diagram styles. */
    public static final String TYPE = "customnode:hed-circuit-junction";

    @Override
    public Optional<String> getNodeType(NodeStyleDescription style) {
        return style instanceof ImageNodeStyleDescription image && "hed-circuits:junction".equals(image.getShape()) ? Optional.of(TYPE) : Optional.empty();
    }

    @Override
    public Optional<INodeStyle> createNodeStyle(NodeStyleDescription style, ILayoutStrategy layout, AQLInterpreter interpreter, VariableManager variables) {
        return getNodeType(style).isPresent() ? Optional.of(new CircuitJunctionNodeStyle(layout)) : Optional.empty();
    }
}
