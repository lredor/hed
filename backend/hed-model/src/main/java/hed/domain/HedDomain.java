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

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import hed.homeelectrical.Catalog;
import hed.homeelectrical.Circuit;
import hed.homeelectrical.CircuitBranch;
import hed.homeelectrical.ControlChannel;
import hed.homeelectrical.ControlDevice;
import hed.homeelectrical.DINRail;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.ElectricalLoad;
import hed.homeelectrical.EnergyMeter;
import hed.homeelectrical.Equipment;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.HomeElectricalFactory;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.ModularDevice;
import hed.homeelectrical.NamedElement;
import hed.homeelectrical.Outlet;
import hed.homeelectrical.ServiceCircuitBreaker;
import hed.homeelectrical.SupplyPhase;

/** HedDomain operations shared by explorer, forms and tables. Generated code stays untouched. */
public final class HedDomain {
    public static final HomeElectricalFactory F = HomeElectricalFactory.eINSTANCE;

    public static final List<String> TECHNICAL_FEATURES = List.of("ratedCurrentA", "ratedPowerW", "widthModules", "residualCurrentMa", "residualType", "tripCurve", "poleConfiguration");

    private static final Set<DeviceKind> MODULAR = EnumSet.of(DeviceKind.MCB, DeviceKind.RCCB, DeviceKind.RCBO, DeviceKind.FUSE, DeviceKind.ISOLATOR, DeviceKind.SPD, DeviceKind.IMPULSE_RELAY,
            DeviceKind.CONTACTOR, DeviceKind.OTHER_MODULAR);

    private static final Set<DeviceKind> CONTROLS = EnumSet.of(DeviceKind.SWITCH, DeviceKind.PUSH_BUTTON, DeviceKind.DIMMER, DeviceKind.SHUTTER_CONTROL);

    public static Dwelling dwelling(EObject value) {
        while (value != null && !(value instanceof Dwelling)) {
            value = value.eContainer();
        }
        return (Dwelling) value;
    }

    public static List<EObject> contents(EObject root) {
        var result = new ArrayList<EObject>();
        result.add(root);
        root.eAllContents().forEachRemaining(result::add);
        return result;
    }

    public static boolean modular(DeviceKind kind) {
        return MODULAR.contains(kind);
    }

    public static DeviceKind kind(Equipment e) {
        return e.getDeviceType() == null ? null : e.getDeviceType().getKind();
    }

    public static boolean compatible(Equipment equipment, DeviceType type) {
        var kind = type.getKind();
        if (equipment instanceof ModularDevice) {
            return modular(kind);
        }
        if (equipment instanceof Outlet) {
            return kind == DeviceKind.OUTLET;
        }
        if (equipment instanceof ControlDevice) {
            return CONTROLS.contains(kind);
        }
        if (equipment instanceof EnergyMeter) {
            return kind == DeviceKind.ENERGY_METER;
        }
        if (equipment instanceof ServiceCircuitBreaker) {
            return kind == DeviceKind.SERVICE_BREAKER;
        }
        return equipment instanceof ElectricalLoad && !modular(kind) && !CONTROLS.contains(kind) && kind != DeviceKind.OUTLET && kind != DeviceKind.ENERGY_METER && kind != DeviceKind.SERVICE_BREAKER;
    }

    public static <T extends NamedElement> T named(T value, String name) {
        value.setName(name);
        return value;
    }

    public static void initialize(Equipment equipment, DeviceType type) {
        initialize(equipment, type, Locale.ENGLISH);
    }

    public static void initialize(Equipment equipment, DeviceType type, Locale locale) {
        if (!compatible(equipment, type)) {
            throw new IllegalArgumentException("incompatibleType");
        }
        equipment.setDeviceType(type);
        equipment.setName(label(type, locale));
        if (!(equipment instanceof ModularDevice)) {
            for (var feature : HomeElectricalPackage.Literals.TECHNICAL_ELEMENT.getEStructuralFeatures()) {
                equipment.eSet(feature, type.eGet(feature));
            }
        }
        if (equipment instanceof ControlDevice control) {
            for (int i = 0; i < Math.max(1, type.getChannelCount()); i++) {
                control.getChannels().add(named(F.createControlChannel(), Messages.text(locale, "channel.defaultName", i + 1)));
            }
        }
    }

    public static FieldEquipment addEquipment(Dwelling home, DeviceType type) {
        return addEquipment(home, type, Locale.ENGLISH);
    }

    public static FieldEquipment addEquipment(Dwelling home, DeviceType type, Locale locale) {
        if (dwelling(type) != home) {
            throw new IllegalArgumentException("foreignReference");
        }
        FieldEquipment result;
        if (type.getKind() == DeviceKind.OUTLET) {
            result = F.createOutlet();
        } else if (CONTROLS.contains(type.getKind())) {
            result = F.createControlDevice();
        } else {
            result = F.createElectricalLoad();
        }
        initialize(result, type, locale);
        home.getEquipment().add(result);
        return result;
    }

    public static ModularDevice addModularDevice(DINRail rail, DeviceType type) {
        return addModularDevice(rail, type, Locale.ENGLISH);
    }

    public static ModularDevice addModularDevice(DINRail rail, DeviceType type, Locale locale) {
        if (rail == null || !(rail.eContainer() instanceof DistributionBoard board)) {
            throw new IllegalArgumentException("requiredRail");
        }
        if (dwelling(type) != dwelling(board)) {
            throw new IllegalArgumentException("foreignReference");
        }
        if (!modular(type.getKind())) {
            throw new IllegalArgumentException("incompatibleType");
        }
        requireRailCapacity(rail, List.of(type));
        var result = F.createModularDevice();
        initialize(result, type, locale);
        board.getDevices().add(result);
        result.setRail(rail);
        synchronizeDeviceOrder(board);
        return result;
    }

    public static DeviceType type(Dwelling home, DeviceKind kind) {
        return home.getCatalog().getDeviceTypes().stream().filter(t -> t.getKind() == kind).findFirst().orElseThrow();
    }

    public static boolean hasThreePhaseSupply(EObject object) {
        var home = dwelling(object);
        return home != null && home.getServiceConnection() != null && home.getServiceConnection().getPhase() == SupplyPhase.THREE_PHASE;
    }

    public static String label(EObject object, Locale locale) {
        if (object instanceof ModularDevice device) {
            if (device.getName() != null && !device.getName().isBlank()) {
                return device.getName();
            }
            return device.getDeviceType() != null ? label(device.getDeviceType(), locale) : Messages.text(locale, "class.ModularDevice");
        }
        if (object instanceof NamedElement n && n.getName() != null && !n.getName().isBlank()) {
            return n.getName();
        }
        return Messages.text(locale, "class." + object.eClass().getName());
    }

    public static Object get(EObject object, String feature) {
        if (object instanceof ModularDevice device && TECHNICAL_FEATURES.contains(feature)) {
            return device.getDeviceType() == null ? null : get(device.getDeviceType(), feature);
        }
        return object.eGet(object.eClass().getEStructuralFeature(feature));
    }

    public static Double technicalNumber(ModularDevice device, String feature) {
        return (Double) get(device, feature);
    }

    public static void set(EObject object, String feature, Object value) {
        if (object instanceof ModularDevice && TECHNICAL_FEATURES.contains(feature)) {
            throw new IllegalArgumentException("catalogSpecifications");
        }
        var f = Objects.requireNonNull(object.eClass().getEStructuralFeature(feature), feature);
        if (object instanceof DeviceType type && feature.equals("kind") && !Objects.equals(type.getKind(), value) && inUse(type)) {
            throw new IllegalArgumentException("typeInUse");
        }
        var home = dwelling(object);
        boolean branchChange = object instanceof FieldEquipment && Set.of("circuit", "branch").contains(feature) || object instanceof CircuitBranch && Set.of("circuit", "equipment").contains(feature);
        if (branchChange) {
            var targets = value instanceof Collection<?> collection ? collection : value == null ? List.of() : List.of(value);
            for (var target : targets) {
                if (target instanceof EObject semantic && dwelling(semantic) != home) {
                    throw new IllegalArgumentException("foreignReference");
                }
            }
            if (object instanceof CircuitBranch && feature.equals("circuit") && value == null) {
                throw new IllegalArgumentException("requiredCircuit");
            }
        }
        var branchState = branchChange ? new BranchState(home) : null;
        var previousOverflow = new IdentityHashMap<DINRail, Double>();
        if (home != null) {
            for (var board : home.getBoards()) {
                for (var rail : board.getRails()) {
                    previousOverflow.put(rail, overflow(rail));
                }
            }
        }
        if (object instanceof ModularDevice device && feature.equals("rail") && value instanceof DINRail rail && rail.eContainer() == device.eContainer() && !canPlaceDevice(device, rail)) {
            throw new IllegalArgumentException("railCapacity");
        }
        if (object instanceof DINRail rail && feature.equals("devices") && value instanceof Collection<?> devices) {
            var used = sumModules(devices.stream().map(ModularDevice.class::cast).map(HedDomain::moduleWidth).toList());
            if (used > rail.getCapacityModules() && used > usedModules(rail)) {
                throw new IllegalArgumentException("railCapacity");
            }
        }
        var old = f.isMany() ? new ArrayList<>((Collection<?>) object.eGet(f)) : object.eGet(f);
        var inverseSnapshots = new LinkedHashMap<EObject, Object>();
        EReference opposite = f instanceof EReference reference ? reference.getEOpposite() : null;
        if (opposite != null) {
            var affected = new ArrayList<Object>();
            if (old instanceof Collection<?> collection) {
                affected.addAll(collection);
            } else if (old != null) {
                affected.add(old);
            }
            if (value instanceof Collection<?> collection) {
                affected.addAll(collection);
            } else if (value != null) {
                affected.add(value);
            }
            for (var item : affected) {
                if (item instanceof EObject target) {
                    inverseSnapshots.put(target, opposite.isMany() ? new ArrayList<>((Collection<?>) target.eGet(opposite)) : target.eGet(opposite));
                }
            }
        }
        object.eSet(f, value);
        if (object instanceof FieldEquipment equipment) {
            if (feature.equals("branch") && equipment.getBranch() != null) {
                equipment.setCircuit(equipment.getBranch().getCircuit());
            }
            if (feature.equals("circuit") && equipment.getBranch() != null && equipment.getBranch().getCircuit() != equipment.getCircuit()) {
                equipment.setBranch(null);
            }
        }
        if (object instanceof CircuitBranch branch && Set.of("circuit", "equipment").contains(feature)) {
            for (var equipment : branch.getEquipment()) {
                equipment.setCircuit(branch.getCircuit());
            }
        }
        var errors = home == null ? List.<HedValidation.Issue> of()
                : new HedValidation().validate(home).stream().filter(i -> i.severity() == HedValidation.Severity.ERROR)
                        .filter(i -> !i.code().equals("railCapacity") || overflow((DINRail) i.target()) > previousOverflow.getOrDefault((DINRail) i.target(), 0d)).toList();
        if (!errors.isEmpty()) {
            object.eSet(f, old);
            if (opposite != null) {
                inverseSnapshots.forEach((target, snapshot) -> target.eSet(opposite, snapshot));
            }
            if (branchState != null) {
                branchState.restore();
            }
            throw new IllegalArgumentException(errors.get(0).code());
        }
        if (object instanceof DINRail rail && feature.equals("devices")) {
            synchronizeDeviceOrder((DistributionBoard) rail.eContainer());
        }
        if (object instanceof ModularDevice device && feature.equals("rail")) {
            synchronizeDeviceOrder((DistributionBoard) device.eContainer());
        }
    }

    /** Related assignments form one edit, including rollback of inverse collection order. */
    private static final class BranchState {
        private final Map<FieldEquipment, FinalCircuit> circuits = new IdentityHashMap<>();

        private final Map<FieldEquipment, CircuitBranch> branches = new IdentityHashMap<>();

        private final Map<CircuitBranch, List<FieldEquipment>> members = new IdentityHashMap<>();

        BranchState(Dwelling home) {
            if (home != null) {
                for (var equipment : home.getEquipment()) {
                    circuits.put(equipment, equipment.getCircuit());
                    branches.put(equipment, equipment.getBranch());
                }
                for (var circuit : home.getCircuits()) {
                    if (circuit instanceof FinalCircuit terminal) {
                        for (var branch : terminal.getBranches()) {
                            members.put(branch, List.copyOf(branch.getEquipment()));
                        }
                    }
                }
            }
        }

        void restore() {
            circuits.forEach(FieldEquipment::setCircuit);
            branches.forEach(FieldEquipment::setBranch);
            members.forEach((branch, equipment) -> branch.eSet(HomeElectricalPackage.Literals.CIRCUIT_BRANCH__EQUIPMENT, equipment));
        }
    }

    public static void assignToBranch(CircuitBranch branch, List<FieldEquipment> equipment) {
        var members = new ArrayList<>(branch.getEquipment());
        for (var item : equipment) {
            if (!members.contains(item)) {
                members.add(item);
            }
        }
        set(branch, "equipment", members);
    }

    public static FieldEquipment addEquipmentOnBranch(CircuitBranch branch, DeviceType type, Locale locale) {
        var equipment = addEquipment(dwelling(branch), type, locale);
        try {
            set(equipment, "branch", branch);
            return equipment;
        } catch (IllegalArgumentException ex) {
            EcoreUtil.delete(equipment, true);
            throw ex;
        }
    }

    public static void setText(EObject object, String feature, String value) {
        if (object instanceof ModularDevice && TECHNICAL_FEATURES.contains(feature)) {
            throw new IllegalArgumentException("catalogSpecifications");
        }
        var attribute = (EAttribute) object.eClass().getEStructuralFeature(feature);
        Object parsed = value == null || value.isBlank() ? null : EcoreUtil.createFromString(attribute.getEAttributeType(), value);
        if (parsed == null && attribute.getEAttributeType().getInstanceClass().isPrimitive()) {
            throw new IllegalArgumentException("requiredValue");
        }
        set(object, feature, parsed);
    }

    public static boolean inUse(DeviceType type) {
        var home = dwelling(type);
        return home != null && contents(home).stream().anyMatch(o -> o instanceof Equipment e && e.getDeviceType() == type);
    }

    public static void delete(EObject object) {
        if (object instanceof DINRail rail && !rail.getDevices().isEmpty()) {
            throw new IllegalArgumentException("railNotEmpty");
        }
        if (object instanceof DeviceType type && inUse(type)) {
            throw new IllegalArgumentException("typeInUse");
        }
        if (object instanceof Catalog || object instanceof Dwelling) {
            throw new IllegalArgumentException("requiredRoot");
        }
        if (object instanceof ControlChannel channel && channel.eContainer() instanceof ControlDevice device && device.getChannels().size() == 1) {
            throw new IllegalArgumentException("lastChannel");
        }
        EcoreUtil.delete(object, true);
    }

    public static List<FieldEquipment> served(FinalCircuit circuit) {
        return dwelling(circuit).getEquipment().stream().filter(e -> e.getCircuit() == circuit).toList();
    }

    public static List<Circuit> protectedCircuits(ModularDevice device) {
        return dwelling(device).getCircuits().stream().filter(c -> c.getProtectiveDevice() == device).toList();
    }

    public static List<ModularDevice> downstreamDevices(ModularDevice rccb) {
        return dwelling(rccb).getBoards().stream().flatMap(b -> b.getDevices().stream()).filter(d -> d.getUpstreamRccb() == rccb).toList();
    }

    public static int orderIndex(EObject object) {
        if (object instanceof ModularDevice d && d.getRail() != null) {
            return d.getRail().getDevices().indexOf(d);
        }
        if (object instanceof DINRail rail && rail.eContainer() instanceof DistributionBoard board) {
            return board.getRails().indexOf(rail);
        }
        return -1;
    }

    public static boolean canReorder(EObject object, int delta) {
        int index = orderIndex(object);
        int size = object instanceof ModularDevice d && d.getRail() != null ? d.getRail().getDevices().size()
                : object instanceof DINRail rail && rail.eContainer() instanceof DistributionBoard board ? board.getRails().size() : 0;
        return index >= 0 && index + delta >= 0 && index + delta < size;
    }

    public static void reorder(EObject object, int delta) {
        if (!canReorder(object, delta)) {
            return;
        }
        int index = orderIndex(object) + delta;
        if (object instanceof ModularDevice device) {
            device.getRail().getDevices().move(index, device);
        } else if (object instanceof DINRail rail) {
            ((DistributionBoard) rail.eContainer()).getRails().move(index, rail);
        }
        synchronizeDeviceOrder((DistributionBoard) object.eContainer());
    }

    /** Drop onto a peer to take its current position, or onto a different rail to append. */
    public static void placeDevice(ModularDevice device, EObject target) {
        var rail = target instanceof DINRail r ? r : target instanceof ModularDevice d ? d.getRail() : null;
        if (rail == null || rail.eContainer() != device.eContainer()) {
            throw new IllegalArgumentException("railBoard");
        }
        if (target == device) {
            return;
        }
        var source = device.getRail();
        int index = target instanceof ModularDevice peer ? rail.getDevices().indexOf(peer) : rail.getDevices().size();
        var sourceBefore = source == null ? List.<ModularDevice> of() : new ArrayList<>(source.getDevices());
        var targetBefore = new ArrayList<>(rail.getDevices());
        var ordered = new ArrayList<>(targetBefore);
        ordered.remove(device);
        ordered.add(Math.min(index, ordered.size()), device);
        try {
            set(rail, "devices", ordered);
        } catch (IllegalArgumentException ex) {
            rail.getDevices().clear();
            rail.getDevices().addAll(targetBefore);
            if (source != null && source != rail) {
                source.getDevices().clear();
                source.getDevices().addAll(sourceBefore);
            }
            throw ex;
        }
    }

    /** Sirius JSON stores the rail inverse on each contained device. Keep containment order
     * consistent with rail order so loading that inverse reconstructs the physical sequence. */
    private static void synchronizeDeviceOrder(DistributionBoard board) {
        if (board == null) {
            return;
        }
        var ordered = new ArrayList<ModularDevice>();
        board.getRails().forEach(rail -> ordered.addAll(rail.getDevices()));
        board.getDevices().stream().filter(d -> d.getRail() == null).forEach(ordered::add);
        for (int i = 0; i < ordered.size(); i++) {
            if (board.getDevices().get(i) != ordered.get(i)) {
                board.getDevices().move(i, ordered.get(i));
            }
        }
    }

    public static double moduleWidth(DeviceType type) {
        if (type != null && type.getWidthModules() != null) {
            return type.getWidthModules();
        }
        return type != null && (type.getKind() == DeviceKind.RCCB || type.getKind() == DeviceKind.RCBO) ? 2 : 1;
    }

    public static double moduleWidth(ModularDevice device) {
        return moduleWidth(device.getDeviceType());
    }

    private static double sumModules(Collection<Double> widths) {
        var sum = java.math.BigDecimal.ZERO;
        for (var width : widths) {
            if (!Double.isFinite(width)) {
                return Double.POSITIVE_INFINITY;
            }
            sum = sum.add(java.math.BigDecimal.valueOf(width));
        }
        return sum.doubleValue();
    }

    public static double usedModules(DINRail rail) {
        return sumModules(rail.getDevices().stream().map(HedDomain::moduleWidth).toList());
    }

    private static double overflow(DINRail rail) {
        return Math.max(0, usedModules(rail) - rail.getCapacityModules());
    }

    public static void requireRailCapacity(DINRail rail, Collection<DeviceType> types) {
        var widths = new ArrayList<>(rail.getDevices().stream().map(HedDomain::moduleWidth).toList());
        types.forEach(type -> widths.add(moduleWidth(type)));
        if (sumModules(widths) > rail.getCapacityModules()) {
            throw new IllegalArgumentException("railCapacity");
        }
    }

    public static boolean canAddToRail(DINRail rail, DeviceType type) {
        if (rail == null || !(rail.eContainer() instanceof DistributionBoard) || type == null || !modular(type.getKind()) || dwelling(type) != dwelling(rail)) {
            return false;
        }
        try {
            requireRailCapacity(rail, List.of(type));
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static boolean canPlaceDevice(ModularDevice device, DINRail rail) {
        if (rail == null || rail.eContainer() != device.eContainer()) {
            return false;
        }
        if (rail == device.getRail()) {
            return true;
        }
        var widths = new ArrayList<>(rail.getDevices().stream().filter(d -> d != device).map(HedDomain::moduleWidth).toList());
        widths.add(moduleWidth(device));
        return sumModules(widths) <= rail.getCapacityModules();
    }
}
