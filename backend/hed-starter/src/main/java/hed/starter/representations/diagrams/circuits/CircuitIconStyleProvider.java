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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.components.diagrams.ILayoutStrategy;
import org.eclipse.sirius.components.diagrams.INodeStyle;
import org.eclipse.sirius.components.diagrams.NodeType;
import org.eclipse.sirius.components.interpreter.AQLInterpreter;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.view.diagram.ImageNodeStyleDescription;
import org.eclipse.sirius.components.view.diagram.NodeStyleDescription;
import org.eclipse.sirius.components.view.emf.diagram.INodeStyleProvider;
import org.eclipse.sirius.components.view.emf.diagram.ImageNodeStyleProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import hed.starter.HedDomainIcons;

/** Resolve the existing MDI pictogram into Sirius's standard image-node style and renderer. */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CircuitIconStyleProvider implements INodeStyleProvider {
    private final ImageNodeStyleProvider standard = new ImageNodeStyleProvider();

    @Override
    public Optional<String> getNodeType(NodeStyleDescription style) {
        return style instanceof ImageNodeStyleDescription image && "hed-circuits:icon".equals(image.getShape()) ? Optional.of(NodeType.NODE_IMAGE) : Optional.empty();
    }

    @Override
    public Optional<INodeStyle> createNodeStyle(NodeStyleDescription style, ILayoutStrategy layout, AQLInterpreter interpreter, VariableManager variables) {
        if (getNodeType(style).isEmpty()) {
            return Optional.empty();
        }
        // Copy the description so resolving one device's icon never changes the shared description for other nodes.
        var image = EcoreUtil.copy((ImageNodeStyleDescription) style);
        image.setShape(HedDomainIcons.of(variables.get(VariableManager.SELF, EObject.class).orElseThrow()));
        return standard.createNodeStyle(image, layout, interpreter, variables);
    }
}
