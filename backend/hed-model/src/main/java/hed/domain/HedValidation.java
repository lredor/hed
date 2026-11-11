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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import hed.homeelectrical.Circuit;
import hed.homeelectrical.CircuitBranch;
import hed.homeelectrical.ControlDevice;
import hed.homeelectrical.ControlFunction;
import hed.homeelectrical.DINRail;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.DistributionCircuit;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.Equipment;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.ModularDevice;
import hed.homeelectrical.ServiceConnection;
import hed.homeelectrical.TechnicalElement;

/** Structural errors block mutations; incomplete design remains saveable with warnings. */
public final class HedValidation {
    public enum Severity {
        ERROR, WARNING
    }

    public record Issue(Severity severity, String code, EObject target) {
    }

    public List<Issue> validate(Dwelling home) {
        var issues = new ArrayList<Issue>();
        var all = HedDomain.contents(home);
        if (home.getCatalog() == null) {
            error(issues, "missingCatalog", home);
        }
        for (var object : all) {
            for (var reference : object.eClass().getEAllReferences()) {
                if (reference.isContainment() || reference.isContainer()) {
                    continue;
                }
                var value = object.eGet(reference);
                var values = reference.isMany() ? (Collection<?>) value : value == null ? List.of() : List.of(value);
                for (var target : values) {
                    if (target instanceof EObject e && HedDomain.dwelling(e) != home) {
                        error(issues, "foreignReference", object);
                    }
                }
            }
            if (object instanceof TechnicalElement tech) {
                for (var name : List.of("ratedCurrentA", "ratedPowerW", "widthModules", "residualCurrentMa")) {
                    var v = (Double) HedDomain.get(tech, name);
                    if (v != null && (!Double.isFinite(v) || v <= 0)) {
                        error(issues, "positiveValue", tech);
                    }
                }
            }
            if (object instanceof Equipment equipment) {
                if (equipment.getDeviceType() == null) {
                    warning(issues, "missingType", equipment);
                } else if (!HedDomain.compatible(equipment, equipment.getDeviceType())) {
                    error(issues, "incompatibleType", equipment);
                }
            }
            if (object instanceof FieldEquipment equipment) {
                if (equipment.getCircuit() != null && !equipment.getCircuit().getBranches().isEmpty() && equipment.getBranch() == null) {
                    error(issues, "requiredBranch", equipment);
                }
                if (equipment.getBranch() != null && equipment.getBranch().getCircuit() != equipment.getCircuit()) {
                    error(issues, "invalidBranch", equipment);
                }
                if (equipment.getRoom() == null) {
                    warning(issues, "missingRoom", equipment);
                }
                if (!(equipment instanceof ControlDevice) && equipment.getCircuit() == null) {
                    warning(issues, "missingCircuit", equipment);
                }
            }
            if (object instanceof CircuitBranch branch && branch.getCircuit() == null) {
                error(issues, "requiredCircuit", branch);
            }
            if (object instanceof DeviceType type && type.getChannelCount() < 1) {
                error(issues, "positiveValue", type);
            }
            if (object instanceof ControlDevice control && control.getChannels().isEmpty()) {
                error(issues, "lastChannel", control);
            }
            if (object instanceof DINRail rail) {
                if (rail.getCapacityModules() < 1) {
                    error(issues, "positiveValue", rail);
                }
                if (HedDomain.usedModules(rail) > rail.getCapacityModules()) {
                    error(issues, "railCapacity", rail);
                }
                for (var device : rail.getDevices()) {
                    if (device.eContainer() != rail.eContainer()) {
                        error(issues, "railBoard", device);
                    }
                }
            }
            if (object instanceof ModularDevice device) {
                if (device.getRail() == null) {
                    error(issues, "requiredRail", device);
                }
                var upstream = device.getUpstreamRccb();
                if (upstream != null && (upstream == device || HedDomain.kind(upstream) != DeviceKind.RCCB || upstream.eContainer() != device.eContainer())) {
                    error(issues, "invalidRccb", device);
                }
                if (upstream != null && device.getUpstreamServiceBreaker() != null) {
                    error(issues, "conflictingUpstream", device);
                }
                var visited = new HashSet<ModularDevice>();
                for (var current = device; current != null; current = current.getUpstreamRccb()) {
                    if (!visited.add(current)) {
                        error(issues, "protectionCycle", device);
                        break;
                    }
                }
            }
            if (object instanceof Circuit circuit) {
                if (circuit.getSourceBoard() == null) {
                    warning(issues, "missingSource", circuit);
                }
                var protection = circuit.getProtectiveDevice();
                if (protection == null) {
                    warning(issues, "missingProtection", circuit);
                } else if (!EnumSet.of(DeviceKind.MCB, DeviceKind.RCBO, DeviceKind.FUSE).contains(HedDomain.kind(protection)) || protection.eContainer() != circuit.getSourceBoard()) {
                    error(issues, "invalidProtection", circuit);
                }
                if (circuit.getConductorSectionMm2() != null && (!Double.isFinite(circuit.getConductorSectionMm2()) || circuit.getConductorSectionMm2() <= 0)) {
                    error(issues, "positiveValue", circuit);
                }
                if (circuit instanceof DistributionCircuit distribution && distribution.getTargetBoard() == null) {
                    warning(issues, "missingTargetBoard", circuit);
                }
            }
            if (object instanceof ControlFunction function) {
                if (function.getChannels().isEmpty() || function.getTargets().isEmpty()) {
                    warning(issues, "incompleteControl", function);
                }
                for (var target : function.getTargets()) {
                    if (target instanceof ControlDevice) {
                        error(issues, "invalidControlTarget", function);
                    }
                }
                if (function.getActuator() != null && !EnumSet.of(DeviceKind.IMPULSE_RELAY, DeviceKind.CONTACTOR, DeviceKind.OTHER_MODULAR).contains(HedDomain.kind(function.getActuator()))) {
                    error(issues, "invalidActuator", function);
                }
            }
            if (object instanceof ServiceConnection service && service.getSubscribedPowerKva() != null && (!Double.isFinite(service.getSubscribedPowerKva()) || service.getSubscribedPowerKva() <= 0)) {
                error(issues, "positiveValue", service);
            }
        }
        var protections = new HashSet<ModularDevice>();
        var incoming = new HashSet<DistributionBoard>();
        for (var circuit : home.getCircuits()) {
            if (circuit.getProtectiveDevice() != null && !protections.add(circuit.getProtectiveDevice())) {
                error(issues, "sharedProtection", circuit);
            }
            if (circuit instanceof DistributionCircuit d && d.getTargetBoard() != null && !incoming.add(d.getTargetBoard())) {
                error(issues, "multipleFeeders", circuit);
            }
        }
        for (var board : home.getBoards()) {
            if (cycle(home, board, new HashSet<>(), new HashSet<>())) {
                error(issues, "boardCycle", board);
                break;
            }
        }
        return List.copyOf(issues);
    }

    private boolean cycle(Dwelling home, DistributionBoard board, Set<DistributionBoard> path, Set<DistributionBoard> done) {
        if (path.contains(board)) {
            return true;
        }
        if (!done.add(board)) {
            return false;
        }
        path.add(board);
        for (var circuit : home.getCircuits()) {
            if (circuit instanceof DistributionCircuit d && d.getSourceBoard() == board && d.getTargetBoard() != null && cycle(home, d.getTargetBoard(), path, done)) {
                return true;
            }
        }
        path.remove(board);
        return false;
    }

    private void error(List<Issue> issues, String code, EObject target) {
        issues.add(new Issue(Severity.ERROR, code, target));
    }

    private void warning(List<Issue> issues, String code, EObject target) {
        issues.add(new Issue(Severity.WARNING, code, target));
    }
}
