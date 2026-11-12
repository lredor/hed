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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import hed.homeelectrical.HomeElectricalFactory;

/** Covers proportional resizing, existing plans, rounding stability and atomic rejection of invalid dimensions. */
class HedFloorPlansTest {
    @Test
    void emptyAndExistingPlansAcquireTheirCurrentProportionsOnTheFirstResize() {
        var storey = HomeElectricalFactory.eINSTANCE.createStorey();
        HedDomain.setText(storey, "planWidth", "2400");
        assertEquals(2400, storey.getPlanWidth());
        assertEquals(1600, storey.getPlanHeight());
        assertEquals(1.5, storey.getPlanAspectRatio());
        storey = HomeElectricalFactory.eINSTANCE.createStorey();
        storey.setPlanHeight(718);
        HedFloorPlans.resize(storey, 1000);
        assertEquals((double) 1200 / 718, storey.getPlanAspectRatio());
        assertEquals(598, storey.getPlanHeight());
    }

    @Test
    void repeatedWidthEditsDoNotAccumulateRoundedHeightErrors() {
        var storey = HomeElectricalFactory.eINSTANCE.createStorey();
        double ratio = 503.0 / 301.0;
        HedFloorPlans.assignImage(storey, "image", ratio);
        for (int iteration = 0; iteration < 50; iteration++) {
            for (int width : new int[] { 640, 1777, 1000, 1200 }) {
                HedFloorPlans.resize(storey, width);
                assertEquals(Math.round(width / ratio), storey.getPlanHeight());
                assertEquals(ratio, storey.getPlanAspectRatio());
            }
        }
        assertEquals(718, storey.getPlanHeight());
    }

    @Test
    void importingANewImageUsesItsIntrinsicRatioWithoutResettingTheWidth() {
        var storey = HomeElectricalFactory.eINSTANCE.createStorey();
        HedFloorPlans.resize(storey, 2400);
        HedFloorPlans.assignImage(storey, "image", 2.0);
        assertEquals(2400, storey.getPlanWidth());
        assertEquals(1200, storey.getPlanHeight());
        HedFloorPlans.assignImage(storey, "replacement", 1.5);
        assertEquals(2400, storey.getPlanWidth());
        assertEquals(1600, storey.getPlanHeight());
        assertEquals("replacement", storey.getPlanImageId());
    }

    @Test
    void invalidDimensionsAndDirectHeightEditsRetainThePreviousState() {
        var storey = HomeElectricalFactory.eINSTANCE.createStorey();
        HedFloorPlans.assignImage(storey, "image", 0.2);
        for (int width : new int[] { 0, -1, Integer.MAX_VALUE }) {
            assertThrows(IllegalArgumentException.class, () -> HedFloorPlans.resize(storey, width));
            assertEquals(1200, storey.getPlanWidth());
            assertEquals(6000, storey.getPlanHeight());
            assertEquals(0.2, storey.getPlanAspectRatio());
        }
        assertThrows(IllegalArgumentException.class, () -> HedDomain.setText(storey, "planHeight", "999"));
        assertEquals(6000, storey.getPlanHeight());
    }

    @Test
    void invalidImageRatiosAreRejectedBeforeTheCurrentImageChanges() {
        var storey = HomeElectricalFactory.eINSTANCE.createStorey();
        HedFloorPlans.assignImage(storey, "image", 1.5);
        for (double ratio : new double[] { 0, 0.1, 10.1, Double.NaN, Double.POSITIVE_INFINITY }) {
            assertThrows(IllegalArgumentException.class, () -> HedFloorPlans.assignImage(storey, "replacement", ratio));
            assertEquals("image", storey.getPlanImageId());
            assertEquals(1.5, storey.getPlanAspectRatio());
            assertEquals(800, storey.getPlanHeight());
        }
    }
}
