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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.view.builder.generated.diagram.DiagramBuilders;
import org.junit.jupiter.api.Test;
import hed.domain.HedSamples;
import hed.starter.HedDomainIcons;

/** Checks that board presentation metadata follows the model without changing shared pictogram descriptions. */
class CircuitBoardStyleTest {
    @Test
    void deviceMetadataTracksSemanticOrderNamesAndTheExistingIcon() {
        var home = HedSamples.example();
        var rail = home.getBoards().getFirst().getRails().getFirst();
        var device = rail.getDevices().get(1);
        var variables = new VariableManager();
        variables.put(VariableManager.SELF, device);
        var provider = new CircuitBoardStyleProvider(Locale.FRENCH);
        var description = new DiagramBuilders().newImageNodeStyleDescription().shape("hed-circuits:board-device").build();
        var style = assertInstanceOf(CircuitBoardNodeStyle.class, provider.createNodeStyle(description, null, null, variables).orElseThrow());
        assertEquals("device", style.role());
        assertEquals(1, style.order());
        assertEquals("Q1", style.caption());
        assertEquals(HedDomainIcons.of(device), style.iconURL());
        rail.getDevices().move(0, device);
        device.setName(null);
        var updated = assertInstanceOf(CircuitBoardNodeStyle.class, provider.createNodeStyle(description, null, null, variables).orElseThrow());
        assertEquals(0, updated.order());
        assertEquals("", updated.caption());
        assertEquals("hed-circuits:board-device", description.getShape());
    }

    @Test
    void unrelatedNativePictogramsAndPhysicalPanelStylesKeepTheirOwnProvider() {
        var provider = new CircuitBoardStyleProvider(Locale.ENGLISH);
        for (var shape : new String[] { "hed-circuits:icon", "hed-panel:device", "hed-floor-plan:equipment", "unrelated" }) {
            var description = new DiagramBuilders().newImageNodeStyleDescription().shape(shape).build();
            assertTrue(provider.getNodeType(description).isEmpty());
            assertTrue(provider.createNodeStyle(description, null, null, new VariableManager()).isEmpty());
        }
    }
}
