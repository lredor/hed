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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.math.BigDecimal;
import org.eclipse.emf.ecore.EObject;
import org.springframework.stereotype.Service;
import hed.domain.HedDomain;
import hed.domain.Messages;
import hed.homeelectrical.Circuit;
import hed.homeelectrical.ControlChannel;
import hed.homeelectrical.ControlDevice;
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.Equipment;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.Room;

/** Projections of existing logical connections; no inferred wiring or presentation state in Ecore. */
@Service
public class CircuitServices {
    private final Locale locale;

    /**
     * Creates the AQL services used by the logical circuit diagram.
     * @param locale selects fallback labels and circuit-phase translations
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
     * Finds circuits whose explicit source is the selected distribution board.
     * @param board the diagram's board container
     * @return circuits in model order, or an empty list for a detached board
     */
    public List<Circuit> getHedCircuitBoardCircuits(DistributionBoard board) {
        var home = HedDomain.dwelling(board);
        return home == null ? List.of() : home.getCircuits().stream().filter(circuit -> circuit.getSourceBoard() == board).toList();
    }

    /**
     * Exposes each independent channel as a border-node connection endpoint.
     * @param equipment the device represented by the owning node
     * @return its channels for a control device, otherwise an empty list
     */
    public List<ControlChannel> getHedCircuitChannels(FieldEquipment equipment) {
        return equipment instanceof ControlDevice control ? control.getChannels() : List.of();
    }

    /**
     * Labels devices with their stored name and circuits with their name and applicable electrical details.
     * The phase is omitted unless the dwelling has an explicit three-phase general supply.
     * @param object the semantic object represented by the node
     * @return an outside pictogram label or inside container label, with optional circuit details on a second line
     */
    public String getHedCircuitNodeLabel(EObject object) {
        if (object instanceof Equipment equipment) {
            return Objects.toString(equipment.getName(), "");
        }
        if (!(object instanceof Circuit circuit)) {
            return HedDomain.label(object, locale);
        }
        var label = HedDomain.label(circuit, locale);
        var details = new ArrayList<String>();
        if (circuit.getConductorSectionMm2() != null) {
            details.add(BigDecimal.valueOf(circuit.getConductorSectionMm2()).stripTrailingZeros().toPlainString() + " mm²");
        }
        if (HedDomain.hasThreePhaseSupply(circuit)) {
            details.add(Messages.text(locale, "enum.CircuitPhase." + circuit.getPhase().getName()));
        }
        return label + (details.isEmpty() ? "" : "\n" + String.join(" · ", details));
    }
}
