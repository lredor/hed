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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static hed.domain.HedDomain.F;
import static hed.domain.HedDomain.addEquipment;
import static hed.domain.HedDomain.addModularDevice;
import static hed.domain.HedDomain.canAddToRail;
import static hed.domain.HedDomain.canReorder;
import static hed.domain.HedDomain.contents;
import static hed.domain.HedDomain.delete;
import static hed.domain.HedDomain.downstreamDevices;
import static hed.domain.HedDomain.get;
import static hed.domain.HedDomain.label;
import static hed.domain.HedDomain.moduleWidth;
import static hed.domain.HedDomain.named;
import static hed.domain.HedDomain.orderIndex;
import static hed.domain.HedDomain.placeDevice;
import static hed.domain.HedDomain.reorder;
import static hed.domain.HedDomain.set;
import static hed.domain.HedDomain.type;
import static hed.domain.HedDomain.usedModules;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.junit.jupiter.api.Test;
import hed.homeelectrical.ControlDevice;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.Outlet;

class DomainTest {
    @Test
    void allBusinessElementsUseNamesWithoutASeparateReferenceAttribute() {
        for (var classifier : HomeElectricalPackage.eINSTANCE.getEClassifiers()) {
            if (classifier instanceof org.eclipse.emf.ecore.EClass type) {
                assertNull(type.getEStructuralFeature("reference"), type.getName());
            }
        }
        assertNotNull(HomeElectricalPackage.Literals.DEVICE_TYPE.getEStructuralFeature("productReference"));
        var home = HedSamples.example();
        set(home.getCircuits().get(1), "name", home.getCircuits().get(0).getName());
        assertEquals(List.of(), new HedValidation().validate(home));
    }

    @Test
    void movingAnApplianceChangesRailOrderWithoutChangingElectricalLinks() {
        var home = HedSamples.example();
        var rail = home.getBoards().get(0).getRails().get(0);
        var before = new ArrayList<>(rail.getDevices());
        var device = before.get(1);
        var circuit = home.getCircuits().get(0);
        var upstream = device.getUpstreamRccb();
        placeDevice(device, before.get(3));
        assertEquals(List.of(before.get(0), before.get(2), before.get(3), device, before.get(4)), rail.getDevices());
        assertSame(device, circuit.getProtectiveDevice());
        assertSame(upstream, device.getUpstreamRccb());
        reorder(device, -1);
        assertEquals(2, orderIndex(device));
        assertFalse(canReorder(before.get(0), -1));
    }

    @Test
    void movingAcrossRailsKeepsOwnershipAndRejectsAnotherBoard() {
        var home = HedSamples.example();
        var board = home.getBoards().get(0);
        var source = board.getRails().get(0);
        var device = source.getDevices().get(1);
        var target = named(F.createDINRail(), "Second rail");
        board.getRails().add(target);
        placeDevice(device, target);
        assertSame(target, device.getRail());
        assertEquals(List.of(device), target.getDevices());
        assertSame(board, device.eContainer());
        assertThrows(IllegalArgumentException.class, () -> placeDevice(device, home.getBoards().get(1).getRails().get(0)));
        assertSame(target, device.getRail());
        assertEquals(List.of(device), target.getDevices());
        reorder(target, -1);
        assertSame(target, board.getRails().get(0));
    }

    @Test
    void rejectedRailTransferRestoresBothOrderedLists() {
        var home = HedSamples.example();
        var board = home.getBoards().get(0);
        var source = board.getRails().get(0);
        var before = new ArrayList<>(source.getDevices());
        var target = named(F.createDINRail(), "Second rail");
        board.getRails().add(target);
        var mounted = addModularDevice(target, type(home, DeviceKind.MCB));
        home.getEquipment().get(0).setRatedPowerW(-1d);
        assertThrows(IllegalArgumentException.class, () -> placeDevice(before.get(2), target));
        assertEquals(before, source.getDevices());
        assertEquals(List.of(mounted), target.getDevices());
        assertSame(source, before.get(2).getRail());
    }

    @Test
    void serviceBreakerAndRccbExposeTheirDirectDownstreamDevices() {
        var home = HedSamples.example();
        var main = home.getServiceConnection().getMainBreaker();
        var board = home.getBoards().get(0);
        var rccb = board.getDevices().get(0);
        assertSame(main, rccb.getUpstreamServiceBreaker());
        assertEquals(List.of(rccb), main.getDownstreamDevices());
        assertEquals(new HashSet<>(board.getDevices().subList(1, board.getDevices().size())), new HashSet<>(downstreamDevices(rccb)));
        set(main, "downstreamDevices", List.of());
        assertNull(rccb.getUpstreamServiceBreaker());
        set(main, "downstreamDevices", List.of(rccb));
        assertSame(main, rccb.getUpstreamServiceBreaker());
    }

    @Test
    void invalidUpstreamAssignmentRestoresBothSides() {
        var home = HedSamples.example();
        var main = home.getServiceConnection().getMainBreaker();
        var rccb = home.getBoards().get(0).getDevices().get(0);
        var other = HedSamples.example();
        var foreignMain = other.getServiceConnection().getMainBreaker();
        var foreignChildren = new ArrayList<>(foreignMain.getDownstreamDevices());
        assertThrows(IllegalArgumentException.class, () -> set(rccb, "upstreamServiceBreaker", foreignMain));
        assertSame(main, rccb.getUpstreamServiceBreaker());
        assertEquals(List.of(rccb), main.getDownstreamDevices());
        assertEquals(foreignChildren, foreignMain.getDownstreamDevices());
        var mcb = home.getBoards().get(0).getDevices().get(1);
        var conflict = assertThrows(IllegalArgumentException.class, () -> set(mcb, "upstreamServiceBreaker", main));
        assertEquals("conflictingUpstream", conflict.getMessage());
        assertNull(mcb.getUpstreamServiceBreaker());
        assertSame(rccb, mcb.getUpstreamRccb());
        assertEquals(List.of(rccb), main.getDownstreamDevices());
    }

    @Test
    void protectionCyclesAreRejectedWithoutBreakingExistingLinks() {
        var home = HedSamples.example();
        var first = home.getBoards().get(0).getDevices().get(0);
        var second = addModularDevice(first.getRail(), type(home, DeviceKind.RCCB));
        set(first, "upstreamServiceBreaker", null);
        set(first, "upstreamRccb", second);
        var error = assertThrows(IllegalArgumentException.class, () -> set(second, "upstreamRccb", first));
        assertEquals("protectionCycle", error.getMessage());
        assertNull(second.getUpstreamRccb());
        assertSame(second, first.getUpstreamRccb());
        assertEquals(List.of(first), downstreamDevices(second));
    }

    @Test
    void deletingServiceBreakerPreservesDownstreamProtectionAndCircuits() {
        var home = HedSamples.example();
        var resource = new XMIResourceImpl();
        resource.getContents().add(home);
        var rccb = home.getBoards().get(0).getDevices().get(0);
        var downstream = new ArrayList<>(downstreamDevices(rccb));
        delete(home.getServiceConnection().getMainBreaker());
        assertNull(home.getServiceConnection().getMainBreaker());
        assertNull(rccb.getUpstreamServiceBreaker());
        assertEquals(downstream, downstreamDevices(rccb));
        assertEquals(5, home.getCircuits().size());
    }

    @Test
    void incompleteDesignAppearsInStandardEmfValidation() {
        var home = HedSamples.empty("Draft");
        home.getCircuits().add(F.createFinalCircuit());
        var registry = org.eclipse.emf.ecore.EValidator.Registry.INSTANCE;
        var previous = registry.put(HomeElectricalPackage.eINSTANCE, new HedDomainEValidator(Locale.ENGLISH));
        try {
            var diagnostic = org.eclipse.emf.ecore.util.Diagnostician.INSTANCE.validate(home);
            assertTrue(diagnostic.getChildren().stream().anyMatch(d -> d.getSource().equals("hed") && d.getSeverity() == org.eclipse.emf.common.util.Diagnostic.WARNING));
        } finally {
            if (previous == null) {
                registry.remove(HomeElectricalPackage.eINSTANCE);
            } else {
                registry.put(HomeElectricalPackage.eINSTANCE, previous);
            }
        }
    }

    @Test
    void failedBulkMountRestoresBothRailsAndTheirOrdering() {
        var home = HedSamples.example();
        var a = home.getBoards().get(0).getRails().get(0);
        var b = home.getBoards().get(1).getRails().get(0);
        var beforeA = new ArrayList<>(a.getDevices());
        var beforeB = new ArrayList<>(b.getDevices());
        var invalid = new ArrayList<>(beforeA);
        invalid.addAll(beforeB);
        assertThrows(IllegalArgumentException.class, () -> set(a, "devices", invalid));
        assertEquals(beforeA, a.getDevices());
        assertEquals(beforeB, b.getDevices());
        assertSame(b, beforeB.get(0).getRail());
    }

    @Test
    void missingTypesDoNotCrashValidation() {
        var home = HedSamples.example();
        home.getCircuits().get(0).getProtectiveDevice().setDeviceType(null);
        assertDoesNotThrow(() -> new HedValidation().validate(home));
    }

    @Test
    void exampleIsCompleteAndConsistent() {
        assertEquals(List.of(), new HedValidation().validate(HedSamples.example()));
    }

    @Test
    void modularSpecificationsComeFromTheCatalogAndUpdateAllInstancesOfTheType() {
        var a = HedSamples.example();
        var b = HedSamples.empty("Other");
        var first = a.getCircuits().get(0).getProtectiveDevice();
        var sameType = a.getCircuits().get(2).getProtectiveDevice();
        assertSame(first.getDeviceType(), sameType.getDeviceType());
        set(first.getDeviceType(), "ratedCurrentA", 25d);
        assertEquals(25d, get(first, "ratedCurrentA"));
        assertEquals(25d, get(sameType, "ratedCurrentA"));
        assertEquals(20d, get(a.getCircuits().get(1).getProtectiveDevice(), "ratedCurrentA"));
        assertNull(first.eClass().getEStructuralFeature("ratedCurrentA"));
        assertNotNull(first.eClass().getEStructuralFeature("name"));
        assertNull(first.eClass().getEStructuralFeature("reference"));
        assertThrows(IllegalArgumentException.class, () -> set(first, "ratedCurrentA", 16d));
        assertNull(type(b, DeviceKind.MCB).getRatedCurrentA());
    }

    @Test
    void multipleCommandsAndIndependentChannelsArePreserved() {
        var home = HedSamples.example();
        var function = home.getControlFunctions().get(0);
        assertEquals(2, function.getChannels().size());
        assertNotSame(function.getChannels().get(0).eContainer(), function.getChannels().get(1).eContainer());
        var control = (ControlDevice) function.getChannels().get(0).eContainer();
        assertEquals(2, control.getChannels().size());
        assertInstanceOf(Outlet.class, home.getControlFunctions().get(1).getTargets().get(0));
    }

    @Test
    void movingLocationsAndRailsKeepsSupplyAndControlLinks() {
        var home = HedSamples.example();
        var equipment = home.getEquipment().get(0);
        var circuit = equipment.getCircuit();
        set(equipment, "room", home.getStoreys().get(1).getRooms().get(0));
        assertSame(circuit, equipment.getCircuit());
        assertSame(equipment, home.getControlFunctions().get(0).getTargets().get(0));
        var board = home.getBoards().get(0);
        var device = board.getDevices().get(1);
        var rail = named(F.createDINRail(), "Row 2");
        board.getRails().add(rail);
        set(device, "rail", rail);
        assertSame(device, home.getCircuits().get(0).getProtectiveDevice());
        assertSame(rail, device.getRail());
        assertFalse(board.getRails().get(0).getDevices().contains(device));
    }

    @Test
    void deletingRoomRetainsEquipmentAndDeletingAnOccupiedRailIsRejected() {
        var home = HedSamples.example();
        var resource = new XMIResourceImpl();
        resource.getContents().add(home);
        var equipment = home.getEquipment().get(0);
        var circuit = equipment.getCircuit();
        delete(equipment.getRoom());
        assertNull(equipment.getRoom());
        assertSame(circuit, equipment.getCircuit());
        assertTrue(home.getEquipment().contains(equipment));
        var device = home.getBoards().get(0).getDevices().get(1);
        var rail = device.getRail();
        assertThrows(IllegalArgumentException.class, () -> delete(rail));
        assertSame(rail, device.getRail());
        assertSame(device, circuit.getProtectiveDevice());
        var empty = named(F.createDINRail(), "Empty");
        home.getBoards().get(0).getRails().add(empty);
        delete(empty);
        assertNull(empty.eContainer());
    }

    @Test
    void appliancesAreCreatedOnRailsAndCannotBeDetached() {
        var home = HedSamples.example();
        var rail = home.getBoards().get(0).getRails().get(0);
        var device = addModularDevice(rail, type(home, DeviceKind.MCB));
        assertSame(rail, device.getRail());
        assertTrue(rail.getDevices().contains(device));
        assertThrows(IllegalArgumentException.class, () -> set(device, "rail", null));
        assertSame(rail, device.getRail());
        var before = new ArrayList<>(rail.getDevices());
        assertThrows(IllegalArgumentException.class, () -> set(rail, "devices", List.of()));
        assertEquals(before, rail.getDevices());
        assertThrows(IllegalArgumentException.class, () -> addModularDevice(null, type(home, DeviceKind.MCB)));
        assertEquals(1, HomeElectricalPackage.Literals.MODULAR_DEVICE__RAIL.getLowerBound());
    }

    @Test
    void deletingProtectionRetainsCircuitWithWarning() {
        var home = HedSamples.example();
        var resource = new XMIResourceImpl();
        resource.getContents().add(home);
        var circuit = home.getCircuits().get(0);
        delete(circuit.getProtectiveDevice());
        assertTrue(home.getCircuits().contains(circuit));
        assertNull(circuit.getProtectiveDevice());
        assertTrue(new HedValidation().validate(home).stream().anyMatch(i -> i.code().equals("missingProtection") && i.target() == circuit));
    }

    @Test
    void deletingEquipmentCleansCommands() {
        var home = HedSamples.example();
        var resource = new XMIResourceImpl();
        resource.getContents().add(home);
        var light = home.getEquipment().get(0);
        delete(light);
        assertTrue(home.getControlFunctions().get(0).getTargets().isEmpty());
    }

    @Test
    void incompatibleOrForeignReferencesAreRejectedAndRolledBack() {
        var home = HedSamples.example();
        var equipment = home.getEquipment().get(0);
        var original = equipment.getRoom();
        var other = HedSamples.example();
        assertThrows(IllegalArgumentException.class, () -> set(equipment, "room", other.getStoreys().get(0).getRooms().get(0)));
        assertSame(original, equipment.getRoom());
        var outlet = home.getEquipment().stream().filter(Outlet.class::isInstance).findFirst().orElseThrow();
        var oldType = outlet.getDeviceType();
        assertThrows(IllegalArgumentException.class, () -> set(outlet, "deviceType", type(home, DeviceKind.SWITCH)));
        assertSame(oldType, outlet.getDeviceType());
    }

    @Test
    void rccbCannotReplaceCircuitOvercurrentProtection() {
        var home = HedSamples.example();
        var circuit = home.getCircuits().get(0);
        var original = circuit.getProtectiveDevice();
        assertThrows(IllegalArgumentException.class, () -> set(circuit, "protectiveDevice", home.getBoards().get(0).getDevices().get(0)));
        assertSame(original, circuit.getProtectiveDevice());
    }

    @Test
    void catalogTypesInUseCannotBeDeletedOrRecategorized() {
        var home = HedSamples.example();
        var type = type(home, DeviceKind.OUTLET);
        assertThrows(IllegalArgumentException.class, () -> delete(type));
        assertThrows(IllegalArgumentException.class, () -> set(type, "kind", DeviceKind.SWITCH));
        assertEquals(DeviceKind.OUTLET, type.getKind());
    }

    @Test
    void reducingCapacityBelowOccupancyAndCrossBoardMountingAreRejected() {
        var home = HedSamples.example();
        var rail = home.getBoards().get(0).getRails().get(0);
        assertThrows(IllegalArgumentException.class, () -> set(rail, "capacityModules", 1));
        assertEquals(13, rail.getCapacityModules());
        var device = home.getBoards().get(1).getDevices().get(0);
        var original = device.getRail();
        assertThrows(IllegalArgumentException.class, () -> set(device, "rail", rail));
        assertSame(original, device.getRail());
    }

    @Test
    void creationFitsExactlyAndReservesFractionalOrUnknownWidths() {
        var home = HedSamples.empty("Capacity");
        var board = named(F.createDistributionBoard(), "Board");
        home.getBoards().add(board);
        var rail = named(F.createDINRail(), "Rail");
        board.getRails().add(rail);
        rail.setCapacityModules(1);
        var mcb = type(home, DeviceKind.MCB);
        set(mcb, "widthModules", 0.1d);
        for (int i = 0; i < 10; i++) {
            addModularDevice(rail, mcb);
        }
        assertEquals(1d, usedModules(rail));
        var before = new ArrayList<>(rail.getDevices());
        assertThrows(IllegalArgumentException.class, () -> addModularDevice(rail, mcb));
        assertEquals(before, board.getDevices());
        assertEquals(before, rail.getDevices());
        var unknown = type(home, DeviceKind.RCCB);
        set(unknown, "widthModules", null);
        assertEquals(2d, moduleWidth(unknown));
        assertFalse(canAddToRail(rail, unknown));
    }

    @Test
    void fullRailRejectsTransfersWithoutChangingOrderOrElectricalReferences() {
        var home = HedSamples.example();
        var board = home.getBoards().get(0);
        var source = board.getRails().get(0);
        var device = source.getDevices().get(1);
        var circuit = home.getCircuits().get(0);
        var upstream = device.getUpstreamRccb();
        set(source, "capacityModules", (int) usedModules(source));
        var target = named(F.createDINRail(), "Full rail");
        board.getRails().add(target);
        target.setCapacityModules(1);
        var mounted = addModularDevice(target, type(home, DeviceKind.MCB));
        var beforeSource = new ArrayList<>(source.getDevices());
        var beforeTarget = new ArrayList<>(target.getDevices());
        assertThrows(IllegalArgumentException.class, () -> placeDevice(device, target));
        assertThrows(IllegalArgumentException.class, () -> set(device, "rail", target));
        assertThrows(IllegalArgumentException.class, () -> set(target, "devices", List.of(mounted, device)));
        assertEquals(beforeSource, source.getDevices());
        assertEquals(beforeTarget, target.getDevices());
        assertSame(source, device.getRail());
        assertSame(device, circuit.getProtectiveDevice());
        assertSame(upstream, device.getUpstreamRccb());
        placeDevice(device, beforeSource.get(3));
        assertEquals(3, orderIndex(device));
        assertEquals(source.getCapacityModules(), usedModules(source));
    }

    @Test
    void catalogWidthAndTypeChangesCannotOverfillAnyRail() {
        var home = HedSamples.example();
        var rail = home.getBoards().get(0).getRails().get(0);
        set(rail, "capacityModules", (int) usedModules(rail));
        var device = home.getCircuits().get(0).getProtectiveDevice();
        var original = device.getDeviceType();
        assertThrows(IllegalArgumentException.class, () -> set(original, "widthModules", 2d));
        assertEquals(1d, original.getWidthModules());
        var wide = org.eclipse.emf.ecore.util.EcoreUtil.copy(original);
        wide.setWidthModules(2d);
        home.getCatalog().getDeviceTypes().add(wide);
        assertThrows(IllegalArgumentException.class, () -> set(device, "deviceType", wide));
        assertSame(original, device.getDeviceType());
        assertEquals(6d, usedModules(rail));
    }

    @Test
    void overcapacityCanBeRepairedProgressivelyWithoutBeingIncreased() {
        var home = HedSamples.example();
        var board = home.getBoards().get(0);
        var rail = board.getRails().get(0);
        rail.setCapacityModules(3);
        var other = home.getBoards().get(1).getRails().get(0);
        other.setCapacityModules(1);
        assertTrue(new HedValidation().validate(home).stream().anyMatch(i -> i.code().equals("railCapacity") && i.severity() == HedValidation.Severity.ERROR));
        set(rail, "name", "Existing rail");
        set(rail, "capacityModules", 4);
        assertThrows(IllegalArgumentException.class, () -> set(rail, "capacityModules", 3));
        assertEquals(4, rail.getCapacityModules());
        assertThrows(IllegalArgumentException.class, () -> addModularDevice(rail, type(home, DeviceKind.MCB)));
        var target = named(F.createDINRail(), "Spare");
        board.getRails().add(target);
        placeDevice(rail.getDevices().get(1), target);
        placeDevice(rail.getDevices().get(1), target);
        set(other, "capacityModules", 2);
        assertEquals(List.of(), new HedValidation().validate(home));
    }

    @Test
    void feederCyclesAreRejected() {
        var home = HedSamples.example();
        var reverse = named(F.createDistributionCircuit(), "Reverse feeder");
        reverse.setSourceBoard(home.getBoards().get(1));
        home.getCircuits().add(reverse);
        assertThrows(IllegalArgumentException.class, () -> set(reverse, "targetBoard", home.getBoards().get(0)));
        assertNull(reverse.getTargetBoard());
    }

    @Test
    void unknownValuesAndDraftModelsRemainValid() {
        var home = HedSamples.empty("Draft");
        var circuit = F.createFinalCircuit();
        home.getCircuits().add(circuit);
        var issues = new HedValidation().validate(home);
        assertFalse(issues.isEmpty());
        assertTrue(issues.stream().allMatch(i -> i.severity() == HedValidation.Severity.WARNING));
        assertThrows(IllegalArgumentException.class, () -> set(circuit, "conductorSectionMm2", Double.NaN));
        assertNull(circuit.getConductorSectionMm2());
    }

    @Test
    void serializationPreservesReferencesAndIdentity() throws Exception {
        var home = HedSamples.example();
        var source = new XMIResourceImpl(URI.createURI("memory:/home.xmi"));
        source.getContents().add(home);
        for (var o : contents(home)) {
            source.setID(o, UUID.randomUUID().toString());
        }
        var id = source.getID(home.getEquipment().get(0));
        var out = new ByteArrayOutputStream();
        source.save(out, Map.of());
        var rs = new ResourceSetImpl();
        rs.getPackageRegistry().put(HomeElectricalPackage.eNS_URI, HomeElectricalPackage.eINSTANCE);
        var loaded = new XMIResourceImpl(URI.createURI("memory:/home.xmi"));
        rs.getResources().add(loaded);
        loaded.load(new ByteArrayInputStream(out.toByteArray()), Map.of());
        var copy = (Dwelling) loaded.getContents().get(0);
        assertEquals(List.of(), new HedValidation().validate(copy));
        assertSame(copy.getEquipment().get(0), loaded.getEObject(id));
        assertSame(copy.getCircuits().get(0), copy.getEquipment().get(0).getCircuit());
        assertSame(copy.getEquipment().get(0), copy.getControlFunctions().get(0).getTargets().get(0));
    }

    @Test
    void translationsAreCompleteAndDoNotTranslateUserNames() {
        var en = ResourceBundle.getBundle("i18n.hed-messages", Locale.ENGLISH);
        var fr = ResourceBundle.getBundle("i18n.hed-messages", Locale.FRENCH);
        assertEquals(en.keySet(), fr.keySet());
        assertEquals("Prise de courant", Messages.text(Locale.FRENCH, "kind.OUTLET"));
        assertEquals("Outlet", Messages.text(Locale.GERMAN, "kind.OUTLET"));
        var home = HedSamples.example();
        assertEquals("Sample home", label(home, Locale.FRENCH));
    }

    @Test
    void catalogNamesAreLocalizedOnlyWhenTheProjectIsCreated() {
        var french = HedSamples.empty("Maison", Locale.FRENCH);
        var english = HedSamples.empty("House");
        assertEquals("Catalogue", french.getCatalog().getName());
        assertEquals("Device catalog", english.getCatalog().getName());
        assertEquals("Prise de courant", type(french, DeviceKind.OUTLET).getName());
        assertEquals("Outlet", type(english, DeviceKind.OUTLET).getName());
        assertEquals("Interrupteur différentiel", type(french, DeviceKind.RCCB).getName());
        assertEquals("RCCB", type(english, DeviceKind.RCCB).getName());
        assertEquals("Télérupteur", type(french, DeviceKind.IMPULSE_RELAY).getName());
        assertEquals("Impulse relay", type(english, DeviceKind.IMPULSE_RELAY).getName());
        assertNull(HomeElectricalPackage.Literals.DEVICE_TYPE.getEStructuralFeature("frenchName"));
        var sample = HedSamples.example(Locale.FRENCH);
        assertEquals("Prise de courant", type(sample, DeviceKind.OUTLET).getName());
        var rccb = sample.getBoards().get(0).getDevices().get(0);
        assertTrue(rccb.getDeviceType().getName().startsWith("Interrupteur différentiel"));
        assertEquals(rccb.getDeviceType().getName(), label(rccb.getDeviceType(), Locale.ENGLISH));
        assertEquals(List.of(), new HedValidation().validate(sample));
    }

    @Test
    void editedCatalogNamesAndEquipmentNamesRemainUnchangedAcrossLanguages() {
        var home = HedSamples.example(Locale.FRENCH);
        var outletType = type(home, DeviceKind.OUTLET);
        assertEquals("Prise de courant", addEquipment(home, outletType, Locale.ENGLISH).getName());
        set(outletType, "name", "Prise extérieure / Garden socket");
        assertEquals("Prise extérieure / Garden socket", label(outletType, Locale.ENGLISH));
        assertEquals(outletType.getName(), label(outletType, Locale.FRENCH));
        var french = addEquipment(home, outletType, Locale.FRENCH);
        var english = addEquipment(home, outletType, Locale.ENGLISH);
        assertEquals(outletType.getName(), french.getName());
        assertEquals(outletType.getName(), english.getName());
        set(french, "name", "Terrasse");
        set(outletType, "name", "Nouveau type");
        assertEquals("Terrasse", label(french, Locale.ENGLISH));
        assertEquals("Prise extérieure / Garden socket", english.getName());
        var rail = home.getBoards().get(0).getRails().get(0);
        var mcbType = type(home, DeviceKind.MCB);
        set(mcbType, "name", "B10 atelier");
        assertEquals("B10 atelier", addModularDevice(rail, mcbType, Locale.ENGLISH).getName());
        var englishHome = HedSamples.example();
        assertEquals("Outlet", addEquipment(englishHome, type(englishHome, DeviceKind.OUTLET), Locale.FRENCH).getName());
        var control = (ControlDevice) addEquipment(englishHome, type(englishHome, DeviceKind.SWITCH), Locale.FRENCH);
        assertEquals("Switch", control.getName());
        assertEquals("Canal 1", control.getChannels().get(0).getName());
    }
}
