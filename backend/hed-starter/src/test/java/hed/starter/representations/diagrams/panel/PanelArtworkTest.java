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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.homeelectrical.HomeElectricalFactory;

class PanelArtworkTest {
    private Document svg(String text) throws Exception {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
    }

    private List<Element> elements(Document svg) {
        var nodes = svg.getElementsByTagName("*");
        var result = new ArrayList<Element>();
        for (int i = 0; i < nodes.getLength(); i++) {
            result.add((Element) nodes.item(i));
        }
        return result;
    }

    private Element find(Document svg, String id) {
        return elements(svg).stream().filter(e -> e.getAttribute("id").equals(id)).findFirst().orElseThrow();
    }

    @Test
    void reusesCanvaShapesAndExtendsTheFrameForAnyRowCount() throws Exception {
        var cabinet = new PanelCabinetSvg();
        for (int count : new int[] { 0, 1, 3, 4, 8 }) {
            var rendered = svg(cabinet.render(count, 540));
            assertEquals(count, rendered.getElementsByTagName("g").getLength());
            double height = Double.parseDouble(rendered.getDocumentElement().getAttribute("height"));
            assertEquals(height - 4, Double.parseDouble(find(rendered, "coffret-contour").getAttribute("height")));
            assertEquals(height - 118, Double.parseDouble(find(rendered, "fixation-basse-gauche-bague").getAttribute("cy")));
            var ids = elements(rendered).stream().map(e -> e.getAttribute("id")).filter(id -> !id.isEmpty()).toList();
            assertEquals(ids.size(), new HashSet<>(ids).size(), "Repeated rows must have unique SVG identifiers");
            for (int i = 1; i <= count; i++) {
                assertEquals("#CDE6EF", find(rendered, "etiquette-" + i + "-bleu").getAttribute("fill"));
                assertEquals("#92A4AD", find(rendered, "rail-" + i + "-ame").getAttribute("fill"));
                assertEquals("translate(0 " + ((i - 1) * 540) + ")", find(rendered, "rangee-" + i).getAttribute("transform"));
            }
        }
    }

    @Test
    void artworkDimensionsTrackTheBoardWhileModuleWidthsStayConsistent() {
        var home = HedSamples.example();
        var board = home.getBoards().get(0);
        var services = new PanelServices(null, Locale.ENGLISH);
        int initialWidth = services.getHedPanelWidth(board), initialHeight = services.getHedPanelHeight(board);
        var rail = HomeElectricalFactory.eINSTANCE.createDINRail();
        board.getRails().add(rail);
        HedDomain.set(rail, "capacityModules", 18);
        assertTrue(services.getHedPanelWidth(board) > initialWidth);
        assertTrue(services.getHedPanelHeight(board) > initialHeight);
        assertEquals(38, services.getHedPanelWidth(board.getDevices().get(1)));
        assertEquals(76, services.getHedPanelWidth(board.getDevices().get(0)));
        assertEquals(services.getHedPanelWidth(rail), services.getHedPanelWidth(board.getRails().get(0)));
        assertTrue(services.getHedPanelHeight(rail) >= 252);
    }

    @Test
    void svgEndpointReturnsVectorArtworkAndRejectsInvalidDimensions() throws Exception {
        var controller = new PanelAssetController();
        var response = controller.cabinet(4, 540);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getHeaders().getCacheControl().contains("max-age="));
        assertEquals(4, svg(response.getBody()).getElementsByTagName("g").getLength());
        assertEquals(400, controller.cabinet(-1, 420).getStatusCode().value());
        assertEquals(400, controller.cabinet(3, Double.NaN).getStatusCode().value());
    }

    @Test
    void rccbArtworkShowsTheCatalogResidualTypeBelowItsSensitivity() throws Exception {
        var controller = new PanelAssetController();
        for (String type : List.of("A", "AC", "F", "B", "  ", " A ", "A & <special>")) {
            var rendered = svg(controller.image("RCCB", "63", "", "30", type, "Manufacturer").getBody());
            var label = find(rendered, "Type différentiel-texte");
            assertEquals(type.isBlank() ? "" : "Type " + type.strip(), label.getTextContent());
            assertEquals("30", find(rendered, "Sensibilité différentielle-texte").getTextContent());
            assertTrue(Double.parseDouble(label.getAttribute("y")) > Double.parseDouble(find(rendered, "Unité de sensibilité-texte").getAttribute("y")));
        }
    }

}
