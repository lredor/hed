/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.domain;

import hed.homeelectrical.Storey;

/** Maintains floor-plan dimensions and an image aspect ratio independent of rounded canvas heights. */
public final class HedFloorPlans {
    private HedFloorPlans() {
    }

    /**
     * Changes the canvas width while deriving its height from the stored image proportions.
     * A plan without a recorded ratio uses its current dimensions as the reference, preserving existing plans.
     *
     * @param storey the non-null storey whose plans share these dimensions
     * @param width the positive integral width in diagram units
     * @throws IllegalArgumentException if the width or resulting height cannot be represented
     */
    public static void resize(Storey storey, int width) {
        double ratio = storey.getPlanAspectRatio();
        if (!Double.isFinite(ratio) || ratio <= 0) {
            ratio = storey.getPlanWidth() > 0 && storey.getPlanHeight() > 0 ? (double) storey.getPlanWidth() / storey.getPlanHeight() : 1.5;
        }
        int height = height(width, ratio);
        // Keep the reference ratio rather than accumulating rounding on every width edit.
        storey.setPlanAspectRatio(ratio);
        storey.setPlanWidth(width);
        storey.setPlanHeight(height);
    }

    /**
     * Assigns an imported image without resetting the user's canvas width.
     *
     * @param storey the non-null storey receiving the background
     * @param imageId the validated project-owned image identifier
     * @param aspectRatio the image's intrinsic width divided by its intrinsic height, from 0.2 through 10
     * @throws IllegalArgumentException for invalid proportions or an unrepresentable canvas size, before any mutation
     */
    public static void assignImage(Storey storey, String imageId, double aspectRatio) {
        if (imageId == null || imageId.isBlank() || !Double.isFinite(aspectRatio) || aspectRatio < 0.2 || aspectRatio > 10) {
            throw new IllegalArgumentException("plan.invalidBackground");
        }
        int height = height(storey.getPlanWidth(), aspectRatio);
        storey.setPlanAspectRatio(aspectRatio);
        storey.setPlanHeight(height);
        storey.setPlanImageId(imageId);
    }

    private static int height(int width, double aspectRatio) {
        long height = Math.max(1, Math.round(width / aspectRatio));
        if (width <= 0 || height > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("plan.invalidDimensions");
        }
        return (int) height;
    }
}
