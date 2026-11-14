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

import hed.starter.representations.diagrams.OutletBlockArtwork;

import hed.domain.HedOutletBlocks;

import hed.homeelectrical.OutletBlock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Locale;
import org.eclipse.sirius.components.core.api.IFeedbackMessageService;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.junit.jupiter.api.Test;
import hed.domain.HedDomain;

import hed.domain.HedSamples;
import hed.homeelectrical.DeviceKind;

import hed.starter.representations.diagrams.circuits.CircuitServices;
import hed.starter.representations.diagrams.floorplan.FloorPlanServices;

/** Verifies whole-block diagram eligibility, circuit endpoint projection and composed MDI artwork. */
class OutletBlockIntegrationTest {
    @Test
    void onlyCompleteBlockIsEligibleForAStoreyPlan() {
        var home = HedSamples.empty("Home");
        var storey = HedDomain.F.createStorey();
        home.getStoreys().add(storey);
        var room = HedDomain.F.createRoom();
        storey.getRooms().add(room);
        var type = HedDomain.type(home, DeviceKind.OUTLET_BLOCK);
        type.setNbRows(2);
        type.setNbColumns(3);
        var block = (OutletBlock) HedDomain.addEquipment(home, type);
        HedDomain.moveToRoom(room, List.of(block));
        var plans = new FloorPlanServices(new IObjectSearchService.NoOp(), new IFeedbackMessageService() {
            @Override
            public void addFeedbackMessage(org.eclipse.sirius.components.representations.Message message) {
            }

            @Override
            public List<org.eclipse.sirius.components.representations.Message> getFeedbackMessages() {
                return List.of();
            }
        }, Locale.ENGLISH);
        assertEquals(List.of(block), plans.getHedFloorPlanEquipment(storey));
        assertEquals(72, plans.getHedPlanEquipmentWidth(block));
        assertEquals(48, plans.getHedPlanEquipmentHeight(block));
        assertTrue(FloorPlanServices.belongsTo(block, storey));
        HedOutletBlocks.outlets(block).forEach(outlet -> {
            assertFalse(FloorPlanServices.belongsTo(outlet, storey));
            assertFalse(plans.canPlaceHedEquipmentInPlan(outlet, null, null));
        });
        var circuits = new CircuitServices(Locale.ENGLISH);
        assertEquals(List.of(block), circuits.getHedCircuitRoomEquipment(room));
        assertEquals(144, circuits.getHedCircuitEquipmentWidth(block));
        assertEquals(96, circuits.getHedCircuitEquipmentHeight(block));
    }

    @Test
    void circuitPathsCollapseConsecutiveBlockMembersWithoutLosingIndependentCircuits() {
        var home = HedSamples.empty("Home");
        var block = (OutletBlock) HedDomain.addEquipment(home, HedDomain.type(home, DeviceKind.OUTLET_BLOCK));
        var before = HedDomain.addEquipment(home, HedDomain.type(home, DeviceKind.OUTLET));
        var after = HedDomain.addEquipment(home, HedDomain.type(home, DeviceKind.OUTLET));
        var circuit = HedDomain.F.createFinalCircuit();
        home.getCircuits().add(circuit);
        var outlets = HedOutletBlocks.outlets(block);
        HedDomain.set(circuit, "equipment", List.of(before, block, after));
        var services = new CircuitServices(Locale.ENGLISH);
        assertEquals(List.of(before, block, after), services.getHedCircuitChainEquipment(home));
        assertEquals(List.of(block), services.getHedCircuitPreviousEquipment(after));
        assertSame(block, services.getHedCircuitEndpoint(outlets.getFirst()));
        var other = HedDomain.F.createFinalCircuit();
        home.getCircuits().add(other);
        HedDomain.set(block, "circuit", other);
        assertEquals(List.of(block), services.getHedCircuitFirstEquipment(other));
    }

    @Test
    void svgUsesOneTranslatedMdiPictogramPerOutletAndRejectsInvalidDimensions() {
        var artwork = new OutletBlockArtwork();
        var image = artwork.image(2, 3);
        assertEquals(200, image.getStatusCode().value());
        assertTrue(image.getBody().contains("viewBox=\"0 0 72 48\""));
        assertEquals(6, image.getBody().split("<g transform=", -1).length - 1);
        assertTrue(image.getBody().contains("translate(48 24)"));
        assertEquals(400, artwork.image(0, 3).getStatusCode().value());
        assertEquals(400, artwork.image(1000, 1000).getStatusCode().value());
    }
}
