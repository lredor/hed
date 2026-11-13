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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.junit.jupiter.api.Test;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.HomeElectricalPackage;

/** Checks per-circuit ordering, inverse assignments, persistence and compatibility with older resources. */
class CircuitEquipmentOrderTest {
    @Test
    void circuitOrderIsIndependentOfRoomOrderAndInverseAssignmentsStayConsistent() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var rooms = List.copyOf(home.getEquipment());
        var reordered = new ArrayList<>(circuit.getEquipment());
        Collections.reverse(reordered);
        HedDomain.set(circuit, "equipment", reordered);
        assertEquals(reordered, HedDomain.served(circuit));
        assertEquals(rooms, home.getEquipment());
        home.getEquipment().move(0, rooms.getLast());
        assertEquals(reordered, HedDomain.served(circuit));
        var incoming = home.getEquipment().stream().filter(device -> device.getCircuit() != circuit).findFirst().orElseThrow();
        var old = incoming.getCircuit();
        HedDomain.set(circuit, "equipment", List.of(incoming, reordered.getFirst()));
        assertSame(circuit, incoming.getCircuit());
        assertEquals(List.of(incoming, reordered.getFirst()), circuit.getEquipment());
        assertFalse(old.getEquipment().contains(incoming));
    }

    @Test
    void xmiRetainsCircuitOrder() throws Exception {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var order = new ArrayList<>(circuit.getEquipment());
        Collections.reverse(order);
        HedDomain.set(circuit, "equipment", order);
        var resource = new XMIResourceImpl(URI.createURI("memory:/ordered.hed"));
        resource.getContents().add(home);
        var output = new ByteArrayOutputStream();
        resource.save(output, Map.of());
        String xml = output.toString(StandardCharsets.UTF_8);
        var loaded = load(xml);
        assertEquals(order.stream().map(FieldEquipment::getName).toList(), HedDomain.served((FinalCircuit) loaded.getCircuits().getFirst()).stream().map(FieldEquipment::getName).toList());
    }

    @Test
    void theFirstBranchInheritsDirectOrderAndDeletingTheLastBranchKeepsItsOrder() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var reversed = new ArrayList<>(circuit.getEquipment());
        Collections.reverse(reversed);
        HedDomain.set(circuit, "equipment", reversed);
        var branch = HedDomain.F.createCircuitBranch();
        circuit.getBranches().add(branch);
        HedDomain.initializeFirstBranch(branch);
        assertEquals(reversed, branch.getEquipment());
        branch.getEquipment().move(0, reversed.getLast());
        var expected = List.copyOf(branch.getEquipment());
        assertThrows(IllegalArgumentException.class, () -> HedDomain.set(circuit, "equipment", List.of()));
        HedDomain.delete(branch);
        assertEquals(expected, HedDomain.served(circuit));
    }

    @Test
    void foreignSelectionsAreRejectedWithoutChangingAnyCircuitOrder() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var original = List.copyOf(circuit.getEquipment());
        assertThrows(IllegalArgumentException.class, () -> HedDomain.set(circuit, "equipment", List.of(original.getLast(), HedSamples.example().getEquipment().getFirst())));
        assertEquals(original, circuit.getEquipment());
    }

    private Dwelling load(String xml) throws Exception {
        var set = new ResourceSetImpl();
        set.getPackageRegistry().put(HomeElectricalPackage.eNS_URI, HomeElectricalPackage.eINSTANCE);
        var resource = new XMIResourceImpl(URI.createURI("memory:/reloaded.hed"));
        set.getResources().add(resource);
        resource.load(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), Map.of());
        return (Dwelling) resource.getContents().getFirst();
    }
}
