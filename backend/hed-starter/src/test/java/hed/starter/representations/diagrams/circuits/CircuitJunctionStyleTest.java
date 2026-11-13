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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.eclipse.sirius.components.diagrams.FreeFormLayoutStrategy;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.view.builder.generated.diagram.DiagramBuilders;
import org.junit.jupiter.api.Test;

/** Checks junction-specific style dispatch and the persisted native layout contract. */
class CircuitJunctionStyleTest {
    @Test
    void dedicatedStyleKeepsItsNativeLayoutAndSerializationDiscriminator() {
        var provider = new CircuitJunctionStyleProvider();
        var description = new DiagramBuilders().newImageNodeStyleDescription().shape("hed-circuits:junction").build();
        var layout = new FreeFormLayoutStrategy();
        assertEquals(CircuitJunctionStyleProvider.TYPE, provider.getNodeType(description).orElseThrow());
        var style = assertInstanceOf(CircuitJunctionNodeStyle.class, provider.createNodeStyle(description, layout, null, new VariableManager()).orElseThrow());
        assertSame(layout, style.getChildrenLayoutStrategy());
        assertTrue(new CircuitJunctionStyleDeserializer().canHandle(CircuitJunctionStyleProvider.TYPE));
    }

    @Test
    void otherCircuitAndPhysicalViewsKeepTheirOwnStyles() {
        var provider = new CircuitJunctionStyleProvider();
        for (var shape : new String[] { "hed-circuits:icon", "hed-circuits:board-rail", "hed-panel:device", "hed-floor-plan:equipment" }) {
            var description = new DiagramBuilders().newImageNodeStyleDescription().shape(shape).build();
            assertTrue(provider.getNodeType(description).isEmpty());
            assertTrue(provider.createNodeStyle(description, null, null, new VariableManager()).isEmpty());
        }
    }
}
