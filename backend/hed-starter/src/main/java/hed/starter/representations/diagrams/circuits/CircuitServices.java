/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.diagrams.circuits;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.eclipse.emf.ecore.EObject;
import org.springframework.stereotype.Service;
import hed.domain.HedDomain;
import hed.homeelectrical.Circuit;
import hed.homeelectrical.CircuitBranch;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.ControlChannel;
import hed.homeelectrical.ControlDevice;
import hed.homeelectrical.ControlFunction;
import hed.homeelectrical.DINRail;
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.ModularDevice;
import hed.homeelectrical.Equipment;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.Room;
import hed.starter.representations.CircuitEquipment;

/** Projections of existing logical connections; no inferred wiring or presentation state in Ecore. */
@Service
public class CircuitServices {
    private final Locale locale;

    /**
     * Creates the AQL services used by the logical circuit diagram.
     * @param locale selects fallback container labels
     */
    public CircuitServices(Locale locale) {
        this.locale = locale;
    }

    /**
     * Finds devices through their room reference, independently of their containment in the dwelling.
     * @param room the diagram's room container
     * @return assigned devices in model order, or an empty list for a detached room
     */
    public List<FieldEquipment> getHedCircuitRoomEquipment(Room room) {
        var home = HedDomain.dwelling(room);
        return home == null ? List.of() : home.getEquipment().stream().filter(equipment -> equipment.getRoom() == room).toList();
    }

    /**
     * Finds the circuits requiring one graphical junction, without creating semantic junction objects.
     * @param home the diagram's dwelling
     * @return final circuits with at least one branch, in model order
     */
    public List<FinalCircuit> getHedCircuitJunctions(Dwelling home) {
        return home.getCircuits().stream().filter(FinalCircuit.class::isInstance).map(FinalCircuit.class::cast).filter(circuit -> !circuit.getBranches().isEmpty()).toList();
    }

    /**
     * Resolves the first terminal endpoint of a circuit or branch.
     * @param owner a final circuit or one of its branches
     * @return its first associated device, or no endpoint when empty or when the circuit has branches
     */
    public List<FieldEquipment> getHedCircuitFirstEquipment(EObject owner) {
        return members(owner).stream().limit(1).toList();
    }

    /**
     * Resolves the preceding member of the same chain. Graphical positions never determine wiring order.
     * @param equipment the target of a continuation edge
     * @return the previous member, or no endpoint for an unassigned or first device
     */
    public List<FieldEquipment> getHedCircuitPreviousEquipment(FieldEquipment equipment) {
        var members = members(chainOwner(equipment));
        int index = members.indexOf(equipment);
        return index > 0 ? List.of(members.get(index - 1)) : List.of();
    }

    /**
     * Names a continuation edge after its circuit or branch, without a translated fallback.
     * @param equipment the device at the end of the segment
     * @return the stored chain name, or an empty label for an unassigned device
     */
    public String getHedCircuitChainLabel(FieldEquipment equipment) {
        var owner = chainOwner(equipment);
        return owner instanceof CircuitBranch branch ? Objects.toString(branch.getName(), "") : owner instanceof Circuit circuit ? Objects.toString(circuit.getName(), "") : "";
    }

    /**
     * Sizes logical modular rectangles from their catalog's module width.
     * @param device the rail-mounted device
     * @return width in diagram units, using 64 units per module
     */
    public int getHedCircuitDeviceWidth(ModularDevice device) {
        return (int) Math.ceil(Math.max(64, 64 * HedDomain.moduleWidth(device)));
    }

    /**
     * Gives every rail the board's inner width, independently of its own number of devices.
     * @param rail the ordered semantic rail
     * @return its minimum width in diagram units
     */
    public int getHedCircuitRailWidth(DINRail rail) {
        return rail.eContainer() instanceof DistributionBoard board ? getHedCircuitBoardWidth(board) - 2 : railContentWidth(rail);
    }

    private int railContentWidth(DINRail rail) {
        return Math.max(160, 24 + rail.getDevices().stream().mapToInt(this::getHedCircuitDeviceWidth).sum() + Math.max(0, rail.getDevices().size() - 1) * 4);
    }

    /**
     * Supplies the native board list's initial width so its rails fit before and after arrangement.
     * @param board the board containing the vertically stacked rails
     * @return its minimum width, including the native one-unit borders
     */
    public int getHedCircuitBoardWidth(DistributionBoard board) {
        return Math.max(184, 2 + board.getRails().stream().mapToInt(this::railContentWidth).max().orElse(160));
    }

    private EObject chainOwner(FieldEquipment equipment) {
        var circuit = equipment.getCircuit();
        if (circuit == null) {
            return null;
        }
        if (circuit.getBranches().isEmpty()) {
            return circuit;
        }
        var branch = equipment.getBranch();
        return branch != null && branch.getCircuit() == circuit ? branch : null;
    }

    private List<FieldEquipment> members(EObject owner) {
        if (owner instanceof FinalCircuit circuit && circuit.getBranches().isEmpty()) {
            return CircuitEquipment.getAssociatedEquipment(circuit).stream().filter(equipment -> !controlledOnSameCircuit(equipment)).toList();
        }
        if (owner instanceof CircuitBranch branch && branch.getCircuit() != null) {
            return branch.getEquipment().stream().filter(equipment -> equipment.getCircuit() == branch.getCircuit() && equipment.getBranch() == branch)
                    .filter(equipment -> !controlledOnSameCircuit(equipment)).toList();
        }
        return List.of();
    }

    /** A command edge already connects these receivers; retain their assignment but omit the redundant solid path. */
    private boolean controlledOnSameCircuit(FieldEquipment equipment) {
        var circuit = equipment.getCircuit();
        var home = HedDomain.dwelling(equipment);
        return circuit != null && home != null && home.getControlFunctions().stream().filter(function -> function.getTargets().contains(equipment)).flatMap(function -> function.getChannels().stream())
                .map(EObject::eContainer).filter(ControlDevice.class::isInstance).map(ControlDevice.class::cast).anyMatch(control -> control != equipment && control.getCircuit() == circuit);
    }

    /**
     * Exposes independent channels only when their device has several channels.
     * @param equipment the device represented by the owning node
     * @return its channels for a multi-channel control device, otherwise an empty list
     */
    public List<ControlChannel> getHedCircuitChannels(FieldEquipment equipment) {
        return equipment instanceof ControlDevice control && control.getChannels().size() > 1 ? control.getChannels() : List.of();
    }

    /**
     * Projects command channels onto their graphical endpoint, using the parent for a single-channel device.
     * @param function the functional command represented by the edge
     * @return distinct device or border-channel endpoints, preserving channel selection order
     */
    public List<EObject> getHedCircuitControlSources(ControlFunction function) {
        return function.getChannels().stream().map(channel -> channel.eContainer() instanceof ControlDevice control && control.getChannels().size() == 1 ? (EObject) control : channel).distinct()
                .toList();
    }

    /**
     * Labels devices with their stored name and containers with the existing localized domain label.
     * @param object the semantic object represented by the node
     * @return an outside pictogram label or inside container label
     */
    public String getHedCircuitNodeLabel(EObject object) {
        if (object instanceof Equipment equipment) {
            return Objects.toString(equipment.getName(), "");
        }
        return HedDomain.label(object, locale);
    }
}
