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

import java.util.Locale;
import java.util.Objects;
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
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.Storey;
import hed.starter.HedDomainIcons;

/** Maps Storey and FieldEquipment views to project-image backgrounds and the existing local Material Design pictograms. */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FloorPlanNodeStyleProvider implements INodeStyleProvider {
    public static final String TYPE = "customnode:hed-floor-plan";

    private final Locale locale;

    /**
     * Creates a localized style provider for floor-plan views.
     * @param locale selects fallback labels for unnamed semantic elements
     */
    public FloorPlanNodeStyleProvider(Locale locale) {
        this.locale = locale;
    }

    @Override
    public Optional<String> getNodeType(NodeStyleDescription style) {
        return style instanceof ImageNodeStyleDescription image && image.getShape() != null && image.getShape().startsWith("hed-plan:") ? Optional.of(TYPE) : Optional.empty();
    }

    @Override
    public Optional<INodeStyle> createNodeStyle(NodeStyleDescription style, ILayoutStrategy layout, AQLInterpreter interpreter, VariableManager variables) {
        if (getNodeType(style).isEmpty()) {
            return Optional.empty();
        }
        var self = variables.get(VariableManager.SELF, EObject.class).orElseThrow();
        if (self instanceof Storey storey) {
            var image = Objects.toString(storey.getPlanImageId(), "");
            return Optional.of(new FloorPlanNodeStyle(true, image.isBlank() ? "" : "/api/images/" + image, HedDomain.label(storey, locale), !image.isBlank(), layout));
        }
        var equipment = (FieldEquipment) self;
        return Optional.of(new FloorPlanNodeStyle(false, "/api/images" + HedDomainIcons.of(equipment), HedDomain.label(equipment, locale), true, layout));
    }
}
