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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Element;

/** Reuses the Canva shapes, repeating a row and extending the frame to fit the model. */
public final class PanelCabinetSvg {
    private final String template;

    public PanelCabinetSvg() throws IOException {
        try (var stream = new ClassPathResource("hed/panel/cabinet.svg").getInputStream()) {
            template = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public String render(int rails, double pitch) {
        if (rails < 0 || rails > 1000 || !Double.isFinite(pitch) || pitch < PanelGeometry.ART_ROW_PITCH || pitch > 10000) {
            throw new IllegalArgumentException("Invalid cabinet dimensions");
        }
        try {
            var factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            var document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(template.getBytes(StandardCharsets.UTF_8)));
            var root = document.getDocumentElement();
            double height = 856 + (Math.max(1, rails) - 1) * pitch, delta = height - 1696;
            root.setAttribute("height", number(height));
            root.setAttribute("viewBox", "0 0 1200 " + number(height));
            var row = (Element) find(root, "rangee-1").cloneNode(true);
            for (int i = 1; i <= 3; i++) {
                root.removeChild(find(root, "rangee-" + i));
            }
            find(root, "coffret-contour").setAttribute("height", number(height - 4));
            find(root, "coffret-face").setAttribute("height", number(height - 14));
            for (var side : java.util.List.of("gauche", "droit")) {
                find(root, "bord-interieur-" + side).setAttribute("height", number(1204 + delta));
            }
            for (var element : elements(root)) {
                if (element.getAttribute("id").startsWith("fixation-basse-")) {
                    String attribute = element.hasAttribute("cy") ? "cy" : "y";
                    element.setAttribute(attribute, number(Double.parseDouble(element.getAttribute(attribute)) + delta));
                }
            }
            for (int i = 0; i < rails; i++) {
                var copy = (Element) row.cloneNode(true);
                String index = Integer.toString(i + 1);
                copy.setAttribute("id", "rangee-" + index);
                copy.setAttribute("data-rail-center-y", number(420 + i * pitch));
                copy.setAttribute("transform", "translate(0 " + number(i * pitch) + ")");
                for (var element : elements(copy)) {
                    if (element.hasAttribute("id")) {
                        element.setAttribute("id", element.getAttribute("id").replaceFirst("(?<=-)1(?=-|$)", index));
                    }
                }
                root.appendChild(copy);
            }
            // The source metadata describes its original three-row coordinates.
            var metadata = root.getElementsByTagName("metadata");
            if (metadata.getLength() > 0) {
                root.removeChild(metadata.item(0));
            }
            root.getElementsByTagName("title").item(0).setTextContent("Distribution board");
            root.getElementsByTagName("desc").item(0).setTextContent("Cabinet with " + rails + " DIN rails.");
            var transformers = TransformerFactory.newInstance();
            transformers.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transformers.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            var transformer = transformers.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            var output = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(output));
            return output.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot render cabinet SVG", ex);
        }
    }

    private static Element find(Element root, String id) {
        return elements(root).stream().filter(e -> id.equals(e.getAttribute("id"))).findFirst().orElseThrow();
    }

    private static java.util.List<Element> elements(Element root) {
        var all = root.getElementsByTagName("*");
        var result = new java.util.ArrayList<Element>();
        for (int i = 0; i < all.getLength(); i++) {
            result.add((Element) all.item(i));
        }
        return result;
    }

    private static String number(double value) {
        return java.math.BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }
}
