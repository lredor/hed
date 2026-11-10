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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.sirius.components.core.api.IFeedbackMessageService;
import org.eclipse.sirius.components.representations.Message;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.view.builder.generated.diagram.DiagramBuilders;
import org.junit.jupiter.api.Test;
import hed.domain.HedDomain;
import hed.domain.HedSamples;

class PanelIntegrationTest {
    static final class Feedback implements IFeedbackMessageService {
        final List<Message> messages = new ArrayList<>();

        public void addFeedbackMessage(Message message) {
            messages.add(message);
        }

        public List<Message> getFeedbackMessages() {
            return messages;
        }
    }

    @Test
    void deviceArtworkUsesCatalogSpecificationsAndOnlyItsStoredNameForTheCaption() {
        var home = HedSamples.example();
        var device = home.getBoards().get(0).getDevices().get(0);
        var vm = new VariableManager();
        vm.put(VariableManager.SELF, device);
        var provider = new PanelNodeStyleProvider(Locale.ENGLISH);
        var description = new DiagramBuilders().newImageNodeStyleDescription().shape("hed-panel:device").build();
        var style = assertInstanceOf(PanelNodeStyle.class, provider.createNodeStyle(description, null, null, vm).orElseThrow());
        assertEquals("ID1", style.caption());
        assertEquals("Ground floor", style.labelText());
        assertTrue(style.imageURL().contains("current=63"));
        assertTrue(style.imageURL().contains("residual=30"));
        assertTrue(style.imageURL().contains("residualType=A"));
        HedDomain.set(device.getDeviceType(), "ratedCurrentA", 40d);
        HedDomain.set(device, "name", "");
        style = assertInstanceOf(PanelNodeStyle.class, provider.createNodeStyle(description, null, null, vm).orElseThrow());
        assertEquals("", style.caption());
        assertTrue(style.imageURL().contains("current=40"));
        HedDomain.set(device, "name", "  ");
        style = assertInstanceOf(PanelNodeStyle.class, provider.createNodeStyle(description, null, null, vm).orElseThrow());
        assertEquals("", style.caption());
        assertTrue(provider.getNodeType(new DiagramBuilders().newImageNodeStyleDescription().shape("unrelated-image").build()).isEmpty());
    }

    @Test
    void nativeDropServiceMovesWithinTheBoardAndReportsRejectedEdits() {
        var home = HedSamples.example();
        var board = home.getBoards().get(0);
        var source = board.getRails().get(0);
        var target = HedDomain.named(HedDomain.F.createDINRail(), "Second rail");
        board.getRails().add(target);
        var device = source.getDevices().get(1);
        var rccb = device.getUpstreamRccb();
        var circuit = HedDomain.protectedCircuits(device).get(0);
        var feedback = new Feedback();
        var services = new PanelServices(feedback, Locale.ENGLISH);
        source.setCapacityModules(6);
        assertSame(source, services.dropHedPanelDevice(source, List.of(device)));
        assertEquals(4, source.getDevices().indexOf(device));
        assertEquals(6d, HedDomain.usedModules(source));
        assertTrue(feedback.messages.isEmpty());
        assertSame(target, services.dropHedPanelDevice(target, List.of(device)));
        assertSame(target, device.getRail());
        assertSame(rccb, device.getUpstreamRccb());
        assertSame(device, circuit.getProtectiveDevice());
        assertTrue(feedback.messages.isEmpty());
        services.dropHedPanelDevice(home.getBoards().get(1).getRails().get(0), List.of(device));
        assertSame(target, device.getRail());
        assertEquals(1, feedback.messages.size());
        services.dropHedPanelDevice(source, List.of(device, rccb));
        assertEquals(2, feedback.messages.size());
        target.setCapacityModules(1);
        services.dropHedPanelDevice(target, List.of(rccb));
        assertSame(source, rccb.getRail());
        assertEquals(List.of(device), target.getDevices());
        assertEquals(3, feedback.messages.size());
    }

    @Test
    void deviceSvgSubstitutesRatingsAndEscapesEditableManufacturerText() throws Exception {
        var response = new PanelAssetController().image("MCB", "20", "C", "", "", "A & B <Company>");
        assertEquals(200, response.getStatusCode().value());
        var factory = DocumentBuilderFactory.newInstance();
        var document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(response.getBody().getBytes(StandardCharsets.UTF_8)));
        var nodes = document.getElementsByTagName("text");
        var values = new java.util.HashMap<String, String>();
        for (int index = 0; index < nodes.getLength(); index++) {
            var node = nodes.item(index);
            values.put(node.getAttributes().getNamedItem("id").getNodeValue(), node.getTextContent());
        }
        assertEquals("20", values.get("Calibre en ampères"));
        assertEquals("C", values.get("Courbe"));
        assertEquals("A & B <Company>", values.get("Marque"));
    }
}
