/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.tables;

import java.util.List;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import hed.domain.HedDomain;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.NamedElement;
import hed.homeelectrical.Room;
import hed.homeelectrical.Storey;

/** Selects equipment in a dwelling, storey or room and composes its human-readable table relationships. */
public final class HedEquipmentTableScope {
    private HedEquipmentTableScope() {
    }

    /**
     * Tests the semantic owners on which an equipment inventory can be created.
     * @param owner the selected model element
     * @return whether it is a dwelling, storey or room
     */
    public static boolean supports(Object owner) {
        return owner instanceof Dwelling || owner instanceof Storey || owner instanceof Room;
    }

    /**
     * Selects current room assignments without duplicating devices or changing their semantic order.
     * @param owner a dwelling, storey or room
     * @return its equipment; the dwelling also includes equipment without a room
     */
    public static List<FieldEquipment> equipment(EObject owner) {
        var home = supports(owner) ? HedDomain.dwelling(owner) : null;
        if (home == null) {
            return List.of();
        }
        return home.getEquipment().stream()
                .filter(device -> owner instanceof Dwelling || device.getRoom() == owner || owner instanceof Storey && device.getRoom() != null && device.getRoom().eContainer() == owner).toList();
    }

    /**
     * Composes the circuit and optional branch names, retaining the user's stored names.
     * @param device the terminal equipment
     * @return circuit / branch, a circuit alone, or an empty string when unassigned
     */
    public static String circuitLabel(FieldEquipment device) {
        var circuit = device.getCircuit();
        var branch = device.getBranch();
        return names(" / ", circuit, branch != null && branch.eContainer() == circuit ? branch : null);
    }

    /**
     * Includes the storey so identically named rooms remain distinguishable in a dwelling inventory.
     * @param device the terminal equipment
     * @return storey - room, with no dangling separator for missing names
     */
    public static String locationLabel(FieldEquipment device) {
        var room = device.getRoom();
        return names(" - ", room != null && room.eContainer() instanceof Storey storey ? storey : null, room);
    }

    private static String names(String separator, NamedElement first, NamedElement second) {
        return Stream.of(first, second).filter(element -> element != null && element.getName() != null && !element.getName().isBlank()).map(NamedElement::getName)
                .reduce((left, right) -> left + separator + right).orElse("");
    }
}
