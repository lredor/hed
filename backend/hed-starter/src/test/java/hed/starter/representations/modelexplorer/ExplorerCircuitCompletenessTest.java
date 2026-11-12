/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.modelexplorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.trees.renderer.TreeRenderer;
import org.junit.jupiter.api.Test;
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.domain.Messages;
import hed.homeelectrical.DeviceKind;

/** Checks circuit omissions, indirect module usage, semantic parent propagation and isolated opt-in render snapshots. */
class ExplorerCircuitCompletenessTest {
    private final ExplorerCircuitCompleteness service = new ExplorerCircuitCompleteness(Locale.ENGLISH);

    @Test
    void filterIsLocalizedDefaultOffAndScopedToTheDomainExplorer() {
        for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            var provider = new CircuitCompletenessFilterProvider(locale);
            var filters = provider.get("context", ExplorerPlanPlacementTest.tree(Messages.text(locale, "explorer.title")));
            assertEquals(1, filters.size());
            assertEquals(CircuitCompletenessFilterProvider.FILTER_ID, filters.getFirst().id());
            assertEquals(Messages.text(locale, "explorer.highlightIncompleteCircuits"), filters.getFirst().label());
            assertFalse(filters.getFirst().defaultState());
            assertTrue(provider.get("context", ExplorerPlanPlacementTest.tree("Other explorer")).isEmpty());
        }
    }

    @Test
    void aConnectedSampleDoesNotHighlightUpstreamDifferentialsOrTheCatalog() {
        var home = HedSamples.example();
        var result = service.incompleteElements(home);
        assertEquals(Set.of(), result);
        assertFalse(service.isHedCircuitIncomplete(home.getCatalog(), result, true));
        assertFalse(service.isHedCircuitIncomplete(new HedJavaServices.ProjectRoot(home), result, true));
    }

    @Test
    void unprotectedFinalAndDistributionCircuitsAndTheirParentsAreHighlighted() {
        var home = HedSamples.example();
        var finalCircuit = home.getCircuits().getFirst();
        var distribution = home.getCircuits().getLast();
        var unused = finalCircuit.getProtectiveDevice();
        finalCircuit.setProtectiveDevice(null);
        distribution.setProtectiveDevice(null);
        var result = service.incompleteElements(home);
        assertTrue(result
                .containsAll(List.of(finalCircuit, distribution, unused, unused.getRail(), unused.eContainer(), new HedJavaServices.Group(home, "circuits"), new HedJavaServices.Group(home, "gtl"))));
        assertFalse(result.contains(home));
        assertFalse(result.contains(unused.getUpstreamRccb()));
    }

    @Test
    void missingCircuitLinksPropagateThroughRoomsStoreysAndTheDwellingIncludingUnlocatedDevices() {
        var home = HedSamples.example();
        var device = home.getEquipment().getFirst();
        device.setCircuit(null);
        var result = service.incompleteElements(home);
        assertTrue(result.containsAll(List.of(device, device.getRoom(), device.getRoom().eContainer(), home)));
        assertTrue(service.isHedCircuitIncomplete(new BranchEquipmentLinks.EquipmentLink(device), result, true));
        assertFalse(service.isHedCircuitIncomplete(device, result, false));
        assertFalse(service.isHedCircuitIncomplete(null, result, true));
        assertFalse(result.contains(home.getStoreys().getFirst().getRooms().get(1)));
        device.setRoom(null);
        var unlocated = service.incompleteElements(home);
        assertTrue(unlocated.contains(new HedJavaServices.Group(home, "unlocated")));
        assertTrue(unlocated.contains(home));
    }

    @Test
    void actuatorUsageFollowsCircuitConnectedTargetsOrControlChannelsAndUnusedModulesRemainRed() {
        var home = HedSamples.example();
        var rail = home.getBoards().getFirst().getRails().getFirst();
        rail.setCapacityModules(100);
        var relay = HedDomain.addModularDevice(rail, HedDomain.type(home, DeviceKind.IMPULSE_RELAY));
        var spare = HedDomain.addModularDevice(rail, HedDomain.type(home, DeviceKind.MCB));
        var control = home.getControlFunctions().getFirst();
        control.setActuator(relay);
        assertFalse(service.incompleteElements(home).contains(relay));
        assertTrue(service.incompleteElements(home).contains(spare));
        control.getTargets().clear();
        assertFalse(service.incompleteElements(home).contains(relay));
        control.getChannels().clear();
        assertTrue(service.incompleteElements(home).contains(relay));
    }

    @Test
    void aMalformedUpstreamCycleDoesNotHangTheHighlight() {
        var home = HedSamples.example();
        var breaker = home.getCircuits().getFirst().getProtectiveDevice();
        breaker.getUpstreamRccb().setUpstreamRccb(breaker);
        assertTrue(service.incompleteElements(home).isEmpty());
    }

    @Test
    void onlyEnabledDomainRendersTraverseTheModelAndSnapshotsStayImmutable() {
        var home = HedSamples.example();
        var resourceSet = new ResourceSetImpl();
        var resource = new ResourceImpl(URI.createURI("memory:/home"));
        resourceSet.getResources().add(resource);
        resource.getContents().add(home);
        var domain = new AdapterFactoryEditingDomain(new ComposedAdapterFactory(), new BasicCommandStack(), resourceSet);
        var reads = new int[] { 0 };
        var context = (IEMFEditingContext) Proxy.newProxyInstance(IEMFEditingContext.class.getClassLoader(), new Class<?>[] { IEMFEditingContext.class }, (proxy, method, args) -> {
            if (method.getName().equals("getDomain")) {
                reads[0]++;
                return domain;
            }
            return method.getName().equals("getId") ? UUID.randomUUID().toString() : null;
        });
        var variables = new VariableManager();
        variables.put(IEditingContext.EDITING_CONTEXT, context);
        var tree = ExplorerPlanPlacementTest.tree(Messages.text(Locale.ENGLISH, "explorer.title"));
        var disabled = service.customize(tree, variables);
        assertEquals(0, reads[0]);
        assertEquals(Set.of(), disabled.get(ExplorerCircuitCompleteness.ELEMENTS_VARIABLE, Set.class).orElseThrow());
        variables.put(TreeRenderer.ACTIVE_FILTER_IDS, List.of(CircuitCompletenessFilterProvider.FILTER_ID));
        assertSame(variables, service.customize(ExplorerPlanPlacementTest.tree("Other tree"), variables));
        assertEquals(0, reads[0]);
        var before = service.customize(tree, variables).get(ExplorerCircuitCompleteness.ELEMENTS_VARIABLE, Set.class).orElseThrow();
        home.getEquipment().getFirst().setCircuit(null);
        var after = service.customize(tree, variables).get(ExplorerCircuitCompleteness.ELEMENTS_VARIABLE, Set.class).orElseThrow();
        assertTrue(before.isEmpty());
        assertTrue(after.contains(home.getEquipment().getFirst()));
        assertThrows(UnsupportedOperationException.class, after::clear);
    }
}
