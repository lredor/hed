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

import java.util.Locale;
import java.util.Optional;
import org.eclipse.emf.ecore.EObject;
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
import hed.domain.HedDomain;
import hed.starter.HedDomainIcons;

/** Supplies ordered board metadata without changing the existing physical panel or terminal pictograms. */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CircuitBoardStyleProvider implements INodeStyleProvider {
    /** Discriminator used to restore the custom style of a saved circuit diagram. */
    public static final String TYPE = "customnode:hed-circuit-board";

    private final CircuitServices services;

    /**
     * Creates the logical board style provider.
     * @param locale selects fallback names of boards and rails
     */
    public CircuitBoardStyleProvider(Locale locale) {
        services = new CircuitServices(locale);
    }

    @Override
    public Optional<String> getNodeType(NodeStyleDescription style) {
        return style instanceof ImageNodeStyleDescription image && image.getShape() != null && image.getShape().startsWith("hed-circuits:board-")
                ? Optional.of(TYPE) : Optional.empty();
    }

    @Override
    public Optional<INodeStyle> createNodeStyle(NodeStyleDescription style, ILayoutStrategy layout, AQLInterpreter interpreter, VariableManager variables) {
        if (getNodeType(style).isEmpty()) {
            return Optional.empty();
        }
        var object = variables.get(VariableManager.SELF, EObject.class).orElseThrow();
        var role = ((ImageNodeStyleDescription) style).getShape().substring("hed-circuits:board-".length());
        return Optional.of(new CircuitBoardNodeStyle(role, HedDomain.orderIndex(object), services.getHedCircuitNodeLabel(object), HedDomainIcons.of(object), layout));
    }
}
