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

import java.util.List;
import java.util.Locale;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.core.api.IFeedbackMessageService;
import org.eclipse.sirius.components.representations.Message;
import org.eclipse.sirius.components.representations.MessageLevel;
import org.springframework.stereotype.Service;
import hed.domain.HedDomain;
import hed.domain.Messages;
import hed.homeelectrical.DINRail;
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.ModularDevice;

@Service
public class PanelServices {
    public static final int MODULE_WIDTH = PanelGeometry.MODULE_WIDTH;

    private final IFeedbackMessageService feedback;

    private final Locale locale;

    public PanelServices(IFeedbackMessageService feedback, Locale locale) {
        this.feedback = feedback;
        this.locale = locale;
    }

    public List<ModularDevice> getHedPanelDevices(EObject self) {
        if (self instanceof DINRail rail) {
            return List.copyOf(rail.getDevices());
        }
        return List.of();
    }

    public static double modules(ModularDevice device) {
        return HedDomain.moduleWidth(device);
    }

    public int getHedPanelWidth(EObject self) {
        if (self instanceof ModularDevice device) {
            return (int) Math.ceil(MODULE_WIDTH * modules(device));
        }
        if (self instanceof DINRail rail) {
            return (int) Math.ceil(1104 * PanelGeometry.scale((DistributionBoard) rail.eContainer()));
        }
        if (self instanceof DistributionBoard board) {
            return PanelGeometry.boardWidth(board);
        }
        return MODULE_WIDTH;
    }

    public int getHedPanelHeight(EObject self) {
        if (self instanceof DistributionBoard board) {
            return PanelGeometry.boardHeight(board);
        }
        if (self instanceof DINRail rail) {
            return (int) Math.ceil(PanelGeometry.rowPitch((DistributionBoard) rail.eContainer()));
        }
        return PanelGeometry.DEVICE_HEIGHT;
    }

    public EObject dropHedPanelDevice(EObject target, List<?> dropped) {
        if (dropped.size() != 1) {
            feedback.addFeedbackMessage(new Message(Messages.text(locale, "panel.singleDevice"), MessageLevel.ERROR));
            return target;
        }
        if (dropped.get(0) instanceof ModularDevice device) {
            try {
                HedDomain.placeDevice(device, target);
            } catch (IllegalArgumentException ex) {
                feedback.addFeedbackMessage(new Message(Messages.text(locale, ex.getMessage()), MessageLevel.ERROR));
            }
        }
        return target;
    }
}
