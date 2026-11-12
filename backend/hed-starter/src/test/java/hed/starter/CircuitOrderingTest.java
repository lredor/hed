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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.sirius.components.collaborative.trees.dto.DropTreeItemInput;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.representations.Failure;
import org.eclipse.sirius.components.representations.Success;
import org.junit.jupiter.api.Test;
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.domain.Messages;
import hed.homeelectrical.Circuit;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.HomeElectricalPackage;
import hed.starter.representations.modelexplorer.CatalogTreeDropHandler;
import hed.starter.representations.modelexplorer.HedJavaServices;

/** Verifies circuit ordering, atomic explorer drops and native persistence without changing electrical assignments. */
class CircuitOrderingTest {
    private final Map<String, Object> objects = new HashMap<>();

    private final BootstrapIntegrationTest.Identity identity = new BootstrapIntegrationTest.Identity();

    private final IObjectSearchService search = new IObjectSearchService() {
        @Override
        public Optional<Object> getObject(IEditingContext context, String id) {
            return Optional.ofNullable(objects.get(id));
        }
    };

    private HedJavaServices explorer(Locale locale) {
        return new HedJavaServices(identity, search, new BootstrapIntegrationTest.Feedback(), locale, Dwelling::getName);
    }

    private CatalogTreeDropHandler handler(Locale locale) {
        return new CatalogTreeDropHandler(search, null, null, explorer(locale), locale);
    }

    private DropTreeItemInput input(List<String> sources, String target, int index) {
        return new DropTreeItemInput(UUID.randomUUID(), "context", "tree", sources, target, index);
    }

    private void register(Dwelling home) {
        objects.put(explorer(Locale.ENGLISH).getHedId(home), home);
        for (int i = 0; i < home.getCircuits().size(); i++) {
            objects.put("c" + i, home.getCircuits().get(i));
        }
    }

    @Test
    void upDownActionsWorkForBothCircuitKindsAndKeepAssignments() {
        var home = HedSamples.example();
        var before = List.copyOf(home.getCircuits());
        var circuit = (FinalCircuit) before.getFirst();
        var protection = circuit.getProtectiveDevice();
        var equipment = HedDomain.served(circuit);
        var branch = HedDomain.F.createCircuitBranch();
        circuit.getBranches().add(branch);
        HedDomain.initializeFirstBranch(branch);
        var service = explorer(Locale.ENGLISH);
        assertFalse(service.canReorderHedObject(circuit, -1));
        assertFalse(service.canReorderHedObject(before.getLast(), 1));
        assertFalse(service.canReorderHedObject(HedDomain.F.createFinalCircuit(), 1));
        assertSame(circuit, service.reorderHedObject(circuit, 1));
        assertEquals(List.of(before.get(1), before.get(0), before.get(2), before.get(3), before.get(4)), home.getCircuits());
        service.reorderHedObject(before.getLast(), -1);
        assertEquals(List.of(before.get(1), before.get(0), before.get(2), before.get(4), before.get(3)), home.getCircuits());
        service.reorderHedObject(home.getCircuits().getFirst(), -1);
        assertSame(before.get(1), home.getCircuits().getFirst());
        assertSame(protection, circuit.getProtectiveDevice());
        assertEquals(equipment, branch.getEquipment());
        assertTrue(equipment.stream().allMatch(device -> device.getCircuit() == circuit && device.getBranch() == branch));
        for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            for (var candidate : List.of(circuit, before.getLast())) {
                assertEquals(Messages.text(locale, "action.moveUp"), explorer(locale).getHedReorderLabel(candidate, -1));
                assertEquals(Messages.text(locale, "action.moveDown"), explorer(locale).getHedReorderLabel(candidate, 1));
            }
        }
    }

    @Test
    void peerDropsUseStableIdsAndGroupDropsAppendWhileKeepingSelectionOrder() {
        var home = HedSamples.example();
        var before = List.copyOf(home.getCircuits());
        register(home);
        var handler = handler(Locale.ENGLISH);
        var group = explorer(Locale.ENGLISH).getHedId(new HedJavaServices.Group(home, "circuits"));
        // The native gap index is a rendering hint; the following circuit's identity determines the insertion point.
        assertInstanceOf(Success.class, handler.handle(null, null, input(List.of("c3"), "c0", 0)));
        assertEquals(List.of(before.get(3), before.get(0), before.get(1), before.get(2), before.get(4)), home.getCircuits());
        assertInstanceOf(Success.class, handler.handle(null, null, input(List.of("c0"), "c4", 99)));
        assertEquals(List.of(before.get(3), before.get(1), before.get(2), before.get(0), before.get(4)), home.getCircuits());
        // Reversed input IDs retain the existing model order, not selection-click order.
        assertInstanceOf(Success.class, handler.handle(null, null, input(List.of("c1", "c3"), group, -1)));
        assertEquals(List.of(before.get(2), before.get(0), before.get(4), before.get(3), before.get(1)), home.getCircuits());
        var unchanged = List.copyOf(home.getCircuits());
        assertInstanceOf(Success.class, handler.handle(null, null, input(List.of("c4"), "c4", -1)));
        assertEquals(unchanged, home.getCircuits());
        assertInstanceOf(Success.class, handler.handle(null, null, input(List.of("c1"), "c2", -1)));
        assertSame(before.get(1), home.getCircuits().getFirst());
    }

    @Test
    void mixedForeignStaleAndWrongDestinationsAreRejectedAtomicallyInBothLanguages() {
        for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            var home = HedSamples.example(locale);
            register(home);
            var before = List.copyOf(home.getCircuits());
            objects.put("other", HedSamples.example().getCircuits().getFirst());
            objects.put("device", home.getEquipment().getFirst());
            objects.put("detached", HedDomain.F.createFinalCircuit());
            var service = explorer(locale);
            var group = service.getHedId(new HedJavaServices.Group(home, "circuits"));
            var wrongGroup = service.getHedId(new HedJavaServices.Group(home, "gtl"));
            for (var input : List.of(input(List.of("c0", "device"), "c1", -1), input(List.of("c0", "other"), group, -1), input(List.of("detached"), group, -1),
                    input(List.of("c0", "missing"), "c1", 1), input(List.of("c0"), "missing", -1), input(List.of("c0"), wrongGroup, -1), input(List.of("c0"), group, 0))) {
                assertEquals(Messages.text(locale, "drop.circuitOrder"), assertInstanceOf(Failure.class, handler(locale).handle(null, null, input)).getMessages().getFirst().body());
                assertEquals(before, home.getCircuits());
            }
            assertInstanceOf(Failure.class, handler(locale).handle(null, null, input(List.of(), group, -1)));
            assertEquals(before, home.getCircuits());
        }
    }

    @Test
    void nativeJsonRoundTripPreservesOrderBranchesEquipmentAndProtection() throws Exception {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var branch = HedDomain.named(HedDomain.F.createCircuitBranch(), "Window side");
        circuit.getBranches().add(branch);
        HedDomain.initializeFirstBranch(branch);
        HedDomain.placeCircuits(home, List.of(circuit), null);
        var names = home.getCircuits().stream().map(Circuit::getName).toList();
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
        var copy = (Dwelling) restored.getContents().getFirst();
        assertEquals(names, copy.getCircuits().stream().map(Circuit::getName).toList());
        var moved = (FinalCircuit) copy.getCircuits().getLast();
        assertEquals("Q1", moved.getProtectiveDevice().getName());
        assertEquals("Window side", moved.getBranches().getFirst().getName());
        assertFalse(moved.getBranches().getFirst().getEquipment().isEmpty());
        assertTrue(moved.getBranches().getFirst().getEquipment().stream().allMatch(device -> device.getCircuit() == moved && device.getBranch() == moved.getBranches().getFirst()));
    }
}
