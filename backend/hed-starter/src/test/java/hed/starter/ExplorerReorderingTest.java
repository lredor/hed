/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.junit.jupiter.api.Test;
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.HomeElectricalPackage;
import hed.starter.representations.modelexplorer.HedJavaServices;

class ExplorerReorderingTest {
    private HedJavaServices services(Locale locale) {
        return new HedJavaServices(new BootstrapIntegrationTest.Identity(), new IObjectSearchService.NoOp(), new BootstrapIntegrationTest.Feedback(), locale, dwelling -> dwelling.getName());
    }

    @Test
    void moveActionsRespectBoundariesAndKeepElectricalAssignments() throws Exception {
        var home = HedSamples.example();
        var board = home.getBoards().get(0);
        var firstRail = board.getRails().get(0);
        var secondRail = HedDomain.named(HedDomain.F.createDINRail(), "Second rail");
        board.getRails().add(secondRail);
        var rccb = firstRail.getDevices().get(0);
        var breaker = firstRail.getDevices().get(1);
        var service = services(Locale.ENGLISH);
        var circuit = HedDomain.protectedCircuits(breaker).get(0);
        assertFalse(service.canReorderHedObject(firstRail, -1));
        assertTrue(service.canReorderHedObject(firstRail, 1));
        assertFalse(service.canReorderHedObject(secondRail, 1));
        assertFalse(service.canReorderHedObject(home, 1));
        assertFalse(service.canReorderHedObject(new HedJavaServices.Group(home, "gtl"), 1));
        assertSame(firstRail, service.reorderHedObject(firstRail, 1));
        assertEquals(List.of(secondRail, firstRail), board.getRails());
        assertFalse(service.canReorderHedObject(firstRail, 1));
        assertSame(breaker, service.reorderHedObject(breaker, 1));
        assertEquals(List.of("ID1", "Q2", "Q1", "Q3", "Q5"), firstRail.getDevices().stream().map(device -> device.getName()).toList());
        assertSame(rccb, breaker.getUpstreamRccb());
        assertSame(breaker, circuit.getProtectiveDevice());
        assertSame(firstRail, breaker.getRail());
        var resources = new ResourceSetImpl();
        resources.getPackageRegistry().put(HomeElectricalPackage.eNS_URI, HomeElectricalPackage.eINSTANCE);
        var resource = new JSONResourceFactory().createResourceFromPath(UUID.randomUUID().toString());
        resources.getResources().add(resource);
        resource.getContents().add(home);
        var bytes = new ByteArrayOutputStream();
        resource.save(bytes, Map.of());
        var restored = new JSONResourceFactory().createResourceFromPath(UUID.randomUUID().toString());
        resources.getResources().add(restored);
        restored.load(new ByteArrayInputStream(bytes.toByteArray()), Map.of());
        var copy = (Dwelling) restored.getContents().get(0);
        assertEquals(List.of("Second rail", "Row 1"), copy.getBoards().get(0).getRails().stream().map(rail -> rail.getName()).toList());
        assertEquals(List.of("ID1", "Q2", "Q1", "Q3", "Q5"), copy.getBoards().get(0).getRails().get(1).getDevices().stream().map(device -> device.getName()).toList());
        assertSame(copy.getBoards().get(0).getDevices().get(0), copy.getCircuits().get(0).getProtectiveDevice().getUpstreamRccb());
    }

    @Test
    void moveLabelsMatchTheObjectAndLanguage() {
        var home = HedSamples.example();
        var rail = home.getBoards().get(0).getRails().get(0);
        var device = rail.getDevices().get(0);
        assertEquals("Move up", services(Locale.ENGLISH).getHedReorderLabel(rail, -1));
        assertEquals("Move down", services(Locale.ENGLISH).getHedReorderLabel(rail, 1));
        assertEquals("Move left", services(Locale.ENGLISH).getHedReorderLabel(device, -1));
        assertEquals("Move right", services(Locale.ENGLISH).getHedReorderLabel(device, 1));
        assertEquals("Déplacer vers le haut", services(Locale.FRENCH).getHedReorderLabel(rail, -1));
        assertEquals("Déplacer vers le bas", services(Locale.FRENCH).getHedReorderLabel(rail, 1));
        assertEquals("Déplacer à gauche", services(Locale.FRENCH).getHedReorderLabel(device, -1));
        assertEquals("Déplacer à droite", services(Locale.FRENCH).getHedReorderLabel(device, 1));
    }
}
