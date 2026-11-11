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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.collaborative.trees.dto.DropTreeItemInput;
import org.eclipse.sirius.components.representations.Success;
import org.eclipse.sirius.components.representations.Failure;
import org.junit.jupiter.api.Test;
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.CircuitBranch;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.HomeElectricalFactory;
import hed.homeelectrical.DeviceKind;
import hed.starter.representations.modelexplorer.HedJavaServices;
import hed.starter.representations.modelexplorer.HedDomainEditing;
import hed.starter.representations.modelexplorer.BranchEquipmentLinks;
import hed.starter.representations.modelexplorer.BranchEquipmentRenameHandler;
import hed.starter.representations.modelexplorer.CatalogTreeDropHandler;

/** Checks distinct explorer occurrences, semantic selection/rename/delete and atomic branch drops. */
class BranchIntegrationTest {
    @Test
    void explorerLinksHaveDistinctStableIdsAndSelectAndEditTheOriginalEquipment() {
        var home = HedSamples.example(Locale.FRENCH);
        var identity = new BootstrapIntegrationTest.Identity();
        var feedback = new BootstrapIntegrationTest.Feedback();
        var byId = new HashMap<String, Object>();
        IObjectSearchService search = new IObjectSearchService() {
            public Optional<Object> getObject(IEditingContext context, String id) {
                return Optional.ofNullable(byId.get(id));
            }
        };
        var services = new HedJavaServices(identity, search, feedback, Locale.FRENCH, Dwelling::getName);
        var circuit = (FinalCircuit) home.getCircuits().get(0);
        var resourceSet = new ResourceSetImpl();
        var resource = new JSONResourceFactory().createResourceFromPath(UUID.randomUUID().toString());
        resourceSet.getResources().add(resource);
        resource.getContents().add(home);
        var domain = new AdapterFactoryEditingDomain(new ComposedAdapterFactory(), new BasicCommandStack(), resourceSet);
        var context = (IEMFEditingContext) java.lang.reflect.Proxy.newProxyInstance(IEMFEditingContext.class.getClassLoader(), new Class<?>[] { IEMFEditingContext.class },
                (proxy, method, args) -> method.getName().equals("getDomain") ? domain : null);
        var branch = (CircuitBranch) services.createHedObject(circuit, "CircuitBranch");
        assertEquals("Nouvelle branche", branch.getName());
        var equipment = home.getEquipment().get(0);
        HedDomain.set(equipment, "branch", branch);
        byId.put(identity.getId(branch), branch);
        byId.put(identity.getId(equipment), equipment);
        var link = (BranchEquipmentLinks.EquipmentLink) services.getHedChildren(branch).get(0);
        var alias = services.getHedId(link);
        assertNotEquals(identity.getId(equipment), alias);
        assertEquals(alias, services.getHedId(services.getHedChildren(branch).get(0)));
        assertSame(branch, services.getHedParent(link));
        assertTrue(services.getHedChildren(equipment.getRoom()).contains(equipment));
        assertEquals(equipment.getName(), services.getHedExplorerLabel(link));
        assertEquals(HedDomainIcons.of(equipment), services.getHedIcon(link));
        assertTrue(services.isHedSelectable(link));
        assertTrue(services.isHedEditable(link));
        assertEquals(3, UUID.fromString(alias).version());
        assertEquals(link, services.getHedObject(alias, context));
        var resolver = new BranchEquipmentLinks(identity);
        assertSame(equipment, resolver.getObject(context, alias).orElseThrow());
        var rename = new BranchEquipmentRenameHandler(identity, Locale.FRENCH);
        var item = org.eclipse.sirius.components.trees.TreeItem.newTreeItem(alias).kind(identity.getKind(equipment))
                .label(org.eclipse.sirius.components.core.api.labels.StyledString.of(equipment.getName())).iconURL(List.of()).children(List.of()).build();
        assertTrue(rename.canHandle(context, item, "Kitchen light"));
        assertInstanceOf(Success.class, rename.handle(context, item, "Kitchen light", null));
        assertEquals("Kitchen light", services.getHedLabel(link));
        HedDomain.set(equipment, "branch", null);
        assertSame(equipment, resolver.getObject(context, alias).orElseThrow());
        assertSame(equipment, services.getHedObject(alias, context));
        assertTrue(resolver.getObject(context, "invalid").isEmpty());
        var editing = new HedDomainEditing(services, identity, Locale.FRENCH, feedback);
        assertTrue(editing.canHandle(link));
        editing.delete(link);
        assertFalse(home.getEquipment().contains(equipment));
        assertTrue(resolver.getObject(context, alias).isEmpty());
        var created = (FieldEquipment) services.createHedObject(branch, "OUTLET");
        assertEquals("Prise de courant", created.getName());
        assertSame(branch, created.getBranch());
        assertSame(circuit, created.getCircuit());
        assertSame(home, created.eContainer());
        var outletType = HedDomain.type(home, DeviceKind.OUTLET);
        byId.put(identity.getId(outletType), outletType);
        assertFalse(editing.getChildCreationDescriptions(context, identity.getId(branch), "").isEmpty());
        var nativeCreated = (FieldEquipment) editing.createChild(context, branch, "hed-type:" + identity.getId(outletType)).orElseThrow();
        assertSame(branch, nativeCreated.getBranch());
        assertSame(circuit, nativeCreated.getCircuit());
        assertFalse(services.canCreateHedObject(home.getCircuits().get(4), "CircuitBranch"));
    }

    @Test
    void explorerDropAssociatesExistingEquipmentAndRejectsMixedSelectionsBeforeChangingAnything() {
        var home = HedSamples.example();
        var branch = HomeElectricalFactory.eINSTANCE.createCircuitBranch();
        ((FinalCircuit) home.getCircuits().get(0)).getBranches().add(branch);
        HedDomain.initializeFirstBranch(branch);
        var equipment = home.getEquipment().get(1);
        var previousCircuit = equipment.getCircuit();
        var objects = Map.<String, Object> of("branch", branch, "equipment", equipment, "type", HedDomain.type(home, DeviceKind.OUTLET));
        IObjectSearchService search = new IObjectSearchService() {
            public Optional<Object> getObject(IEditingContext context, String id) {
                return Optional.ofNullable(objects.get(id));
            }
        };
        var handler = new CatalogTreeDropHandler(search, null, null, Locale.ENGLISH);
        assertInstanceOf(Failure.class, handler.handle(null, null, new DropTreeItemInput(UUID.randomUUID(), "context", "tree", List.of("equipment", "type"), "branch", -1)));
        assertNull(equipment.getBranch());
        assertSame(previousCircuit, equipment.getCircuit());
        assertInstanceOf(Success.class, handler.handle(null, null, new DropTreeItemInput(UUID.randomUUID(), "context", "tree", List.of("equipment"), "branch", -1)));
        assertSame(branch, equipment.getBranch());
        assertSame(branch.getCircuit(), equipment.getCircuit());
    }

    @Test
    void firstExplorerBranchPreservesDirectEquipmentAndSubsequentBranchesStartEmpty() {
        var home = HedSamples.example();
        var circuit = (FinalCircuit) home.getCircuits().getFirst();
        var before = HedDomain.served(circuit);
        var services = new HedJavaServices(new BootstrapIntegrationTest.Identity(), new IObjectSearchService.NoOp(), new BootstrapIntegrationTest.Feedback(), Locale.ENGLISH, Dwelling::getName);
        var first = (CircuitBranch) services.createHedObject(circuit, "CircuitBranch");
        assertEquals(before, first.getEquipment());
        for (var equipment : before) {
            assertSame(first, equipment.getBranch());
            assertSame(circuit, equipment.getCircuit());
        }
        var second = (CircuitBranch) services.createHedObject(circuit, "CircuitBranch");
        assertTrue(second.getEquipment().isEmpty());
        assertEquals(before, first.getEquipment());
        HedDomain.delete(second);
        HedDomain.delete(first);
        assertEquals(before, HedDomain.served(circuit));
        assertTrue(before.stream().allMatch(equipment -> equipment.getBranch() == null));
    }
}
