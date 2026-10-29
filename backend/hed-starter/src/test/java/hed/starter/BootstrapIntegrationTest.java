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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IFeedbackMessageService;
import org.eclipse.sirius.components.core.api.IIdentityService;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.emf.services.IDAdapter;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.forms.description.IfDescription;
import org.eclipse.sirius.components.forms.description.PageDescription;
import org.eclipse.sirius.components.forms.description.TextfieldDescription;
import org.eclipse.sirius.components.representations.Failure;
import org.eclipse.sirius.components.representations.Message;
import org.eclipse.sirius.components.representations.VariableManager;
import org.junit.jupiter.api.Test;
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.domain.HedValidation;
import hed.homeelectrical.CircuitBranch;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.ModularDevice;
import hed.homeelectrical.Outlet;
import hed.homeelectrical.ServiceConnection;
import hed.starter.representations.details.HedPropertiesDescriptionRegistryConfigurer;
import hed.starter.representations.modelexplorer.HedDomainEditing;
import hed.starter.representations.modelexplorer.HedJavaServices;
import hed.starter.representations.modelexplorer.ViewContribution;
import org.eclipse.sirius.components.emf.services.DefaultEMFLabelService;
import org.eclipse.sirius.components.emf.services.ComposedEMFLabelService;
import org.eclipse.sirius.components.emf.services.api.IEMFLabelService;
import org.eclipse.sirius.components.emf.services.api.IDefaultEMFLabelService;
import hed.homeelectrical.provider.HomeElectricalItemProviderAdapterFactory;
import hed.starter.representations.modelexplorer.HedDomainLabels;

class BootstrapIntegrationTest {
    static final class Identity implements IIdentityService {
        final Map<Object, String> ids = new IdentityHashMap<>();

        public String getId(Object object) {
            return ids.computeIfAbsent(object, key -> UUID.randomUUID().toString());
        }

        public String getKind(Object object) {
            return "siriusComponents://semantic?domain=homeelectrical";
        }
    }

    static final class Feedback implements IFeedbackMessageService {
        final List<Message> messages = new ArrayList<>();

        public void addFeedbackMessage(Message message) {
            messages.add(message);
        }

        public List<Message> getFeedbackMessages() {
            return messages;
        }
    }

    private IDefaultEMFLabelService defaultLabels() {
        return new DefaultEMFLabelService(List.of(HomeElectricalItemProviderAdapterFactory::new), object -> Optional.empty());
    }

    private IEMFLabelService nativeLabels() {
        return new ComposedEMFLabelService(defaultLabels(), List.of());
    }

    private HedJavaServices services() {
        return new HedJavaServices(new Identity(), new IObjectSearchService.NoOp(), new Feedback(), Locale.ENGLISH, nativeLabels(), dwelling -> dwelling.getName());
    }

    @Test
    void nativeJsonRestoresCatalogProtectionBranchAndRoomReferences() throws Exception {
        var home = HedSamples.example(Locale.FRENCH);
        var circuit = (FinalCircuit) home.getCircuits().get(0);
        var branch = HedDomain.named(HedDomain.F.createCircuitBranch(), "Côté fenêtre");
        circuit.getBranches().add(branch);
        HedDomain.set(home.getEquipment().get(0), "branch", branch);
        var set = new ResourceSetImpl();
        set.getPackageRegistry().put(HomeElectricalPackage.eNS_URI, HomeElectricalPackage.eINSTANCE);
        var source = new JSONResourceFactory().createResourceFromPath(UUID.randomUUID().toString());
        set.getResources().add(source);
        source.getContents().add(home);
        var bytes = new ByteArrayOutputStream();
        source.save(bytes, Map.of());
        var loadedSet = new ResourceSetImpl();
        loadedSet.getPackageRegistry().put(HomeElectricalPackage.eNS_URI, HomeElectricalPackage.eINSTANCE);
        var loaded = new JSONResourceFactory().createResource(source.getURI());
        loadedSet.getResources().add(loaded);
        loaded.load(new ByteArrayInputStream(bytes.toByteArray()), Map.of());
        var copy = (Dwelling) loaded.getContents().get(0);
        var equipment = copy.getEquipment().get(0);
        var copiedCircuit = (FinalCircuit) copy.getCircuits().get(0);
        assertSame(copiedCircuit, equipment.getCircuit());
        assertSame(copiedCircuit.getBranches().get(0), equipment.getBranch());
        assertSame(equipment, equipment.getBranch().getEquipment().get(0));
        assertSame(copy.getStoreys().get(0).getRooms().get(0), equipment.getRoom());
        assertEquals(List.of(), new HedValidation().validate(copy));
        var rccb = copy.getBoards().get(0).getDevices().get(0);
        assertSame(copy.getServiceConnection().getMainBreaker(), rccb.getUpstreamServiceBreaker());
        assertTrue(copy.getCatalog().getDeviceTypes().contains(rccb.getDeviceType()));
        assertTrue(rccb.getDeviceType().getName().startsWith("Interrupteur différentiel"));
        assertEquals("Prise de courant", HedDomain.label(HedDomain.type(copy, DeviceKind.OUTLET), Locale.ENGLISH));
    }

    @Test
    void explorerProjectsEachObjectOnceWithItsDisplayedParent() {
        var home = HedSamples.example();
        var service = services();
        service.createHedObject(home.getCircuits().get(0), "CircuitBranch");
        var visited = Collections.newSetFromMap(new IdentityHashMap<EObject, Boolean>());
        traverse(new HedJavaServices.ProjectRoot(home), service, visited);
        assertEquals(new HashSet<>(HedDomain.contents(home)), visited);
    }

    private void traverse(Object object, HedJavaServices service, Set<EObject> visited) {
        if (object instanceof EObject semantic) {
            assertTrue(visited.add(semantic), "Duplicate explorer object: " + semantic.eClass().getName());
        }
        for (var child : service.getHedChildren(object)) {
            assertEquals(object, service.getHedParent(child));
            traverse(child, service, visited);
        }
    }

    @Test
    void creationUsesRoomAndRailOwnershipAndProtectsNonemptyRails() {
        var home = HedSamples.example();
        var service = services();
        var board = home.getBoards().get(0);
        var rail = board.getRails().get(0);
        assertFalse(service.canCreateHedObject(board, "MCB"));
        assertTrue(service.canCreateHedObject(rail, "MCB"));
        assertFalse(service.isHedDeletable(rail));
        var device = assertInstanceOf(ModularDevice.class, service.createHedObject(rail, "MCB"));
        assertSame(rail, device.getRail());
        assertSame(board, device.eContainer());
        var room = home.getStoreys().get(0).getRooms().get(0);
        var outlet = assertInstanceOf(Outlet.class, service.createHedObject(room, "OUTLET"));
        assertSame(home, outlet.eContainer());
        assertSame(room, outlet.getRoom());
        var branch = assertInstanceOf(CircuitBranch.class, service.createHedObject(home.getCircuits().get(0), "CircuitBranch"));
        assertSame(home.getCircuits().get(0), branch.getCircuit());
    }

    @Test
    void nativeNewObjectDialogRejectsAStaleCapacityChoiceWithoutCreatingAnything() {
        var home = HedSamples.example();
        var rail = home.getBoards().get(0).getRails().get(0);
        var type = HedDomain.type(home, DeviceKind.MCB);
        rail.setCapacityModules((int) HedDomain.usedModules(rail) + 1);
        var identity = new Identity();
        var feedback = new Feedback();
        IObjectSearchService search = new IObjectSearchService() {
            public Optional<Object> getObject(IEditingContext context, String id) {
                return identity.ids.entrySet().stream().filter(e -> e.getValue().equals(id)).map(Map.Entry::getKey).findFirst();
            }
        };
        var service = new HedJavaServices(identity, search, feedback, Locale.FRENCH, nativeLabels(), dwelling -> dwelling.getName());
        var editing = new HedDomainEditing(service, identity, Locale.FRENCH, feedback);
        var description = "hed-type:" + identity.getId(type);
        var railId = identity.getId(rail);
        assertTrue(editing.getChildCreationDescriptions(null, railId, "").stream().anyMatch(d -> d.id().equals(description)));
        HedDomain.addModularDevice(rail, type);
        int before = rail.getDevices().size();
        assertTrue(editing.createChild(null, rail, description).isEmpty());
        assertEquals(before, rail.getDevices().size());
        assertEquals(1, feedback.messages.size());
        assertFalse(service.canCreateHedObject(rail, "MCB"));
        assertTrue(editing.getChildCreationDescriptions(null, railId, "").isEmpty());
    }

    @Test
    void propertiesReadModularRatingsFromCatalogAndRejectInstanceEdits() {
        var pages = new ArrayList<PageDescription>();
        new HedPropertiesDescriptionRegistryConfigurer(new Identity(), Locale.ENGLISH, nativeLabels()).addPropertiesDescriptions(pages::add);
        var rccb = HedSamples.example().getBoards().get(0).getDevices().get(0);
        var vm = new VariableManager();
        vm.put(VariableManager.SELF, rccb);
        var page = pages.stream().filter(p -> p.getCanCreatePredicate().test(vm)).findFirst().orElseThrow();
        var widgets = page.getGroupDescriptions().stream().flatMap(g -> g.getControlDescriptions().stream())
                .flatMap(c -> c instanceof IfDescription condition ? condition.getControlDescriptions().stream() : java.util.stream.Stream.of(c)).toList();
        var current = widgets.stream().filter(TextfieldDescription.class::isInstance).map(TextfieldDescription.class::cast)
                .filter(w -> w.getIdProvider().apply(vm).equals("hed.ModularDevice.ratedCurrentA")).findFirst().orElseThrow();
        assertTrue(current.getIsReadOnlyProvider().apply(vm));
        assertEquals("63.0", current.getValueProvider().apply(vm));
        assertInstanceOf(Failure.class, current.getNewValueHandler().apply(vm, "40"));
        assertEquals(63d, rccb.getDeviceType().getRatedCurrentA());
        HedDomain.set(rccb.getDeviceType(), "ratedCurrentA", 40d);
        assertEquals("40.0", current.getValueProvider().apply(vm));
    }

    @Test
    void explorerDescriptionIdsAreUniqueAndStableAcrossRebuildsAndLanguages() {
        var first = new ViewContribution(null, null, null, Locale.ENGLISH).createView();
        var again = new ViewContribution(null, null, null, Locale.ENGLISH).createView();
        var french = new ViewContribution(null, null, null, Locale.FRENCH).createView();
        assertEquals(ids(first), ids(again));
        assertEquals(ids(first), ids(french));
        assertEquals(ids(first).size(), new HashSet<>(ids(first)).size());
    }

    private List<UUID> ids(EObject root) {
        return HedDomain.contents(root).stream().flatMap(object -> object.eAdapters().stream()).filter(IDAdapter.class::isInstance).map(IDAdapter.class::cast).map(IDAdapter::getId).toList();
    }

    @Test
    void generatedEmfIconsAreUsedByDefault() {
        var home = HedSamples.example();
        var equipment = HedDomain.addEquipment(home, HedDomain.type(home, DeviceKind.OUTLET));
        var defaults = defaultLabels();
        var labels = new HedDomainLabels(Locale.ENGLISH, defaults);
        assertEquals(defaults.getImagePaths(equipment), labels.getImagePaths(equipment));
        assertTrue(labels.getImagePaths(equipment).stream().anyMatch(path -> path.contains("/icons/full/obj16/Outlet")));
    }

    @Test
    void deletingUnusedSupplyTypesDisablesCreationRequiringThoseTypes() {
        var home = HedSamples.empty("Draft");
        var service = services();
        var connection = assertInstanceOf(ServiceConnection.class, service.createHedObject(new HedJavaServices.Group(home, "gtl"), "ServiceConnection"));
        assertTrue(service.canCreateHedObject(connection, "EnergyMeter"));
        assertTrue(service.canCreateHedObject(connection, "ServiceCircuitBreaker"));
        HedDomain.delete(HedDomain.type(home, DeviceKind.ENERGY_METER));
        HedDomain.delete(HedDomain.type(home, DeviceKind.SERVICE_BREAKER));
        assertFalse(service.canCreateHedObject(connection, "EnergyMeter"));
        assertFalse(service.canCreateHedObject(connection, "ServiceCircuitBreaker"));
        assertNull(connection.getMeter());
        assertNull(connection.getMainBreaker());
    }
}
