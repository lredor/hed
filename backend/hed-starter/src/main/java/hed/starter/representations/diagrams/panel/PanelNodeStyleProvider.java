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

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
import hed.homeelectrical.DINRail;
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.ModularDevice;

@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PanelNodeStyleProvider implements INodeStyleProvider {
    public static final String TYPE = "customnode:hed-panel";

    private final Locale locale;

    public PanelNodeStyleProvider(Locale locale) {
        this.locale = locale;
    }

    @Override
    public Optional<String> getNodeType(NodeStyleDescription style) {
        return style instanceof ImageNodeStyleDescription image && image.getShape() != null && image.getShape().startsWith("hed-panel:") ? Optional.of(TYPE) : Optional.empty();
    }

    @Override
    public Optional<INodeStyle> createNodeStyle(NodeStyleDescription style, ILayoutStrategy layout, AQLInterpreter interpreter, VariableManager variables) {
        if (getNodeType(style).isEmpty()) {
            return Optional.empty();
        }
        var role = ((ImageNodeStyleDescription) style).getShape().substring("hed-panel:".length());
        var self = variables.get(VariableManager.SELF, org.eclipse.emf.ecore.EObject.class).orElseThrow();
        int order = HedDomain.orderIndex(self), capacity = self instanceof DINRail rail ? rail.getCapacityModules() : 0;
        double used = self instanceof DINRail rail ? HedDomain.usedModules(rail) : 0;
        String caption = HedDomain.label(self, locale), image = "";
        if (self instanceof DistributionBoard board) {
            image = "/api/hed/panel/cabinet.svg?rails=" + board.getRails().size() + "&pitch=" + number(PanelGeometry.rowPitch(board) / PanelGeometry.scale(board));
        }
        if (self instanceof ModularDevice device) {
            caption = device.getName() == null || device.getName().isBlank() ? "" : device.getName();
            image = "/api/hed/panel/device.svg?kind=" + encode(Objects.toString(HedDomain.kind(device), "OTHER_MODULAR")) + "&current="
                    + encode(number(HedDomain.technicalNumber(device, "ratedCurrentA"))) + "&curve=" + encode(Objects.toString(HedDomain.get(device, "tripCurve"), "")) + "&residual="
                    + encode(number(HedDomain.technicalNumber(device, "residualCurrentMa"))) + "&residualType=" + encode(Objects.toString(HedDomain.get(device, "residualType"), "")) + "&manufacturer="
                    + encode(Objects.toString(device.getDeviceType() == null ? null : device.getDeviceType().getManufacturer(), ""));
        }
        return Optional.of(new PanelNodeStyle(role, image, order, caption, capacity, used, self instanceof ModularDevice device ? Objects.toString(device.getLabelText(), "") : "", layout));
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String number(Double value) {
        return value == null ? "" : java.math.BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }
}
