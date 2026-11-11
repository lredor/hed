/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations;

import java.util.IdentityHashMap;
import java.util.List;
import hed.domain.HedDomain;
import hed.homeelectrical.CircuitBranch;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.FinalCircuit;

/** Presents a final circuit's direct equipment or the union of its optional branches without duplicating semantic references. */
public final class CircuitEquipment {
    private CircuitEquipment() {
    }

    /**
     * Computes the list displayed by Details for the circuit's current mode.
     * @param circuit the final circuit selected in Details
     * @return direct members without branches, otherwise the distinct union in branch/member order
     */
    public static List<FieldEquipment> getAssociatedEquipment(FinalCircuit circuit) {
        if (!circuit.getBranches().isEmpty()) {
            return circuit.getBranches().stream().flatMap(branch -> branch.getEquipment().stream()).distinct().toList();
        }
        var home = HedDomain.dwelling(circuit);
        return home == null ? List.of() : home.getEquipment().stream().filter(equipment -> equipment.getCircuit() == circuit).toList();
    }

    /**
     * Preserves existing direct assignments when a first branch is created through the explorer.
     * Later branches start empty so their membership can be distributed explicitly by the user.
     * @param branch the newly attached branch
     * @throws IllegalArgumentException if the domain rejects the complete assignment
     */
    public static void initializeFirstBranch(CircuitBranch branch) {
        HedDomain.initializeFirstBranch(branch);
    }

    /**
     * Replaces direct circuit assignments through the existing domain edit services as one atomic Details edit.
     * Branch-derived values cannot be edited, including through a stale form submitted after branch creation.
     * @param circuit a contained final circuit without branches
     * @param equipment the complete selection of devices from the same dwelling
     * @throws IllegalArgumentException if the list is derived, a device is foreign or a domain edit is rejected
     */
    public static void setAssociatedEquipment(FinalCircuit circuit, List<FieldEquipment> equipment) {
        if (!circuit.getBranches().isEmpty()) {
            throw new IllegalArgumentException("circuitEquipmentFromBranches");
        }
        var home = HedDomain.dwelling(circuit);
        if (home == null || equipment.stream().anyMatch(device -> device == null || HedDomain.dwelling(device) != home)) {
            throw new IllegalArgumentException("foreignReference");
        }
        var circuits = new IdentityHashMap<FieldEquipment, FinalCircuit>();
        var branches = new IdentityHashMap<FieldEquipment, CircuitBranch>();
        var members = new IdentityHashMap<CircuitBranch, List<FieldEquipment>>();
        for (var device : home.getEquipment()) {
            circuits.put(device, device.getCircuit());
            branches.put(device, device.getBranch());
        }
        home.getCircuits().stream().filter(FinalCircuit.class::isInstance).map(FinalCircuit.class::cast).flatMap(owner -> owner.getBranches().stream())
                .forEach(branch -> members.put(branch, List.copyOf(branch.getEquipment())));
        try {
            for (var device : home.getEquipment()) {
                if (device.getCircuit() == circuit && !equipment.contains(device)) {
                    HedDomain.set(device, "circuit", null);
                }
            }
            for (var device : equipment) {
                if (device.getCircuit() != circuit) {
                    HedDomain.set(device, "circuit", circuit);
                }
            }
        } catch (IllegalArgumentException exception) {
            // A multi-selection edit must also restore the inverse branch list order when a later item fails.
            circuits.forEach(FieldEquipment::setCircuit);
            branches.forEach(FieldEquipment::setBranch);
            members.forEach((branch, devices) -> {
                branch.getEquipment().clear();
                branch.getEquipment().addAll(devices);
            });
            throw exception;
        }
    }
}
