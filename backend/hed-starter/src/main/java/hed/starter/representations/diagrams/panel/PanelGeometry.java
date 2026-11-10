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

import hed.domain.HedDomain;
import hed.homeelectrical.DistributionBoard;

/** Coordinates in cabinet.svg; the frontend uses the same artwork anchors. */
public final class PanelGeometry {
    public static final int MODULE_WIDTH = 38, ART_WIDTH = 1200, DEVICE_LEFT = 72, DEVICE_RIGHT = 1128;

    public static final int ROW_LEFT = 48, ROW_TOP = 198, RAIL_CENTER = 420, ART_ROW_PITCH = 420;

    public static final int MIN_ROW_PITCH = 252, DEVICE_IMAGE_HEIGHT = 178, DEVICE_HEIGHT = 218;

    private PanelGeometry() {
    }

    public static int boardWidth(DistributionBoard board) {
        double modules = Math.max(13, board.getRails().stream().mapToDouble(r -> Math.max(r.getCapacityModules(), HedDomain.usedModules(r))).max().orElse(0));
        return (int) Math.ceil(ART_WIDTH * MODULE_WIDTH * modules / (DEVICE_RIGHT - DEVICE_LEFT));
    }

    public static double scale(DistributionBoard board) {
        return (double) boardWidth(board) / ART_WIDTH;
    }

    public static double rowPitch(DistributionBoard board) {
        return Math.max(MIN_ROW_PITCH, ART_ROW_PITCH * scale(board));
    }

    public static int boardHeight(DistributionBoard board) {
        return (int) Math.ceil(856 * scale(board) + (Math.max(1, board.getRails().size()) - 1) * rowPitch(board));
    }
}
