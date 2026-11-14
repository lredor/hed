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

import hed.homeelectrical.OutletBlock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.junit.jupiter.api.Test;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.Dwelling;

/** Exercises catalog expansion, electrical independence, physical ownership and persistence of outlet blocks. */
class OutletBlockTest {
    @Test
    void defaultBlockCreatesOneRowOfTwoLocalizedOutlets() {
        var home = HedSamples.empty("Home", Locale.FRENCH);
        var type = HedDomain.type(home, DeviceKind.OUTLET_BLOCK);
        assertEquals("Bloc de prises de courant", type.getName());
        var block = assertInstanceOf(OutletBlock.class, HedDomain.addEquipment(home, type, Locale.FRENCH));
        assertEquals(1, block.getRows().size());
        assertEquals("Rangée 1", block.getRows().getFirst().getName());
        assertEquals(2, HedOutletBlocks.outlets(block).size());
        assertEquals("Prise 1.2", HedOutletBlocks.outlets(block).getLast().getName());
        assertEquals("Prise 1.2", HedDomain.label(HedOutletBlocks.outlets(block).getLast(), Locale.FRENCH));
        assertEquals(List.of(block), home.getEquipment());
        assertEquals(1, HedOutletBlocks.terminals(home).size());
    }

    @Test
    void twoByThreeBlockSharesOneCircuitAndOneBranch() {
        var home = HedSamples.empty("Home");
        var type = HedDomain.type(home, DeviceKind.OUTLET_BLOCK);
        type.setNbRows(2);
        type.setNbColumns(3);
        var block = (OutletBlock) HedDomain.addEquipment(home, type);
        var first = HedDomain.F.createFinalCircuit();
        var second = HedDomain.F.createFinalCircuit();
        home.getCircuits().addAll(List.of(first, second));
        HedDomain.set(block, "circuit", first);
        assertEquals(List.of(block), HedDomain.served(first));
        var branch = HedDomain.F.createCircuitBranch();
        second.getBranches().add(branch);
        HedDomain.set(block, "branch", branch);
        assertTrue(HedDomain.served(first).isEmpty());
        assertEquals(List.of(block), branch.getEquipment());
        for (var outlet : HedOutletBlocks.outlets(block)) {
            assertSame(second, outlet.getCircuit());
            assertSame(branch, outlet.getBranch());
            assertThrows(IllegalArgumentException.class, () -> HedDomain.set(outlet, "circuit", first));
        }
    }

    @Test
    void movingWholeBlockPropagatesRoomWhileMembersRemainFixed() {
        var home = HedSamples.empty("Home");
        var storey = HedDomain.F.createStorey();
        home.getStoreys().add(storey);
        var room = HedDomain.F.createRoom();
        storey.getRooms().add(room);
        var block = (OutletBlock) HedDomain.addEquipment(home, HedDomain.type(home, DeviceKind.OUTLET_BLOCK));
        HedDomain.moveToRoom(room, List.of(block));
        assertSame(room, block.getRoom());
        for (var outlet : HedOutletBlocks.outlets(block)) {
            assertSame(room, outlet.getRoom());
            assertThrows(IllegalArgumentException.class, () -> HedDomain.set(outlet, "room", null));
            assertThrows(IllegalArgumentException.class, () -> HedDomain.moveToRoom(room, List.of(outlet)));
            assertThrows(IllegalArgumentException.class, () -> HedDomain.delete(outlet));
        }
        assertThrows(IllegalArgumentException.class, () -> HedDomain.delete(block.getRows().getFirst()));
    }

    @Test
    void invalidDimensionsFailBeforeAllocationAndUsedDimensionsCannotChange() {
        var home = HedSamples.empty("Home");
        var type = HedDomain.type(home, DeviceKind.OUTLET_BLOCK);
        for (var dimensions : List.of(new int[] { 0, 2 }, new int[] { 2, -1 }, new int[] { 256, 256 })) {
            type.setNbRows(dimensions[0]);
            type.setNbColumns(dimensions[1]);
            assertThrows(IllegalArgumentException.class, () -> HedDomain.addEquipment(home, type));
            assertEquals(0, home.getEquipment().size());
        }
        type.setNbRows(1);
        type.setNbColumns(2);
        HedDomain.addEquipment(home, type);
        assertThrows(IllegalArgumentException.class, () -> HedDomain.set(type, "nbColumns", 3));
        assertEquals(2, type.getNbColumns());
    }

    @Test
    void serializingAndDeletingBlockPreservesThenClearsNestedElectricalReferences() throws Exception {
        var home = HedSamples.empty("Home");
        var block = (OutletBlock) HedDomain.addEquipment(home, HedDomain.type(home, DeviceKind.OUTLET_BLOCK));
        var circuit = HedDomain.F.createFinalCircuit();
        home.getCircuits().add(circuit);
        HedDomain.set(circuit, "equipment", List.of(block));
        var resource = new XMIResourceImpl(URI.createURI("memory:/blocks.xmi"));
        resource.getContents().add(home);
        var bytes = new ByteArrayOutputStream();
        resource.save(bytes, Map.of());
        var restored = new XMIResourceImpl(URI.createURI("memory:/restored.xmi"));
        restored.load(new ByteArrayInputStream(bytes.toByteArray()), Map.of());
        var loaded = (Dwelling) restored.getContents().getFirst();
        var loadedBlock = (OutletBlock) loaded.getEquipment().getFirst();
        assertEquals(2, HedOutletBlocks.outlets(loadedBlock).size());
        HedOutletBlocks.outlets(loadedBlock).forEach(outlet -> assertSame(loadedBlock.getCircuit(), outlet.getCircuit()));
        assertEquals(1, HedDomain.served((hed.homeelectrical.FinalCircuit) loaded.getCircuits().getFirst()).size());
        HedDomain.delete(block);
        assertFalse(home.getEquipment().contains(block));
        assertEquals(0, circuit.getEquipment().size());
        assertNull(HedOutletBlocks.block(circuit));
    }

    @Test
    void blockCanBeCreatedAfterRemovingTheUnusedStandaloneOutletType() {
        var home = HedSamples.empty("Home");
        home.getCatalog().getDeviceTypes().removeIf(type -> type.getKind() == DeviceKind.OUTLET);
        var block = (OutletBlock) HedDomain.addEquipment(home, HedDomain.type(home, DeviceKind.OUTLET_BLOCK));
        var standalone = HedDomain.type(home, DeviceKind.OUTLET);
        HedOutletBlocks.outlets(block).forEach(outlet -> assertSame(standalone, outlet.getDeviceType()));
    }
}
