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

import org.eclipse.emf.ecore.EObject;
import hed.domain.HedDomain;
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.Equipment;
import hed.starter.representations.modelexplorer.HedJavaServices;

/** Shared MDI pictograms for explorer items, creation tools and reference selectors. */
public final class HedDomainIcons {
    private HedDomainIcons() {
    }

    public static String path(String name) {
        return "/hed/mdi/" + name + ".svg";
    }

    public static String of(Object object) {
        object = hed.starter.representations.modelexplorer.BranchEquipmentLinks.semantic(object);
        if (object instanceof HedJavaServices.ProjectRoot) {
            return path("folder-home-outline");
        }
        if (object instanceof HedJavaServices.Group group) {
            return path(switch (group.feature()) {
                case "gtl" -> "home-lightning-bolt-outline";
                case "panelTypes" -> "view-dashboard-outline";
                case "terminalTypes" -> "power-plug-outline";
                case "circuits" -> "vector-polyline";
                case "controlFunctions" -> "gesture-tap";
                case "unlocated" -> "map-marker-question-outline";
                default -> "folder-home-outline";
            });
        }
        if (object instanceof DeviceType type) {
            return concept(type.getKind().getName());
        }
        if (object instanceof Equipment equipment && HedDomain.kind(equipment) != null) {
            return concept(HedDomain.kind(equipment).getName());
        }
        return concept(object instanceof EObject semantic ? semantic.eClass().getName() : "");
    }

    public static String concept(String concept) {
        return path(switch (concept) {
            case "Dwelling" -> "home-outline";
            case "Storey" -> "layers-outline";
            case "Room" -> "floor-plan";
            case "DistributionBoard" -> "view-dashboard-outline";
            case "DINRail" -> "view-sequential-outline";
            case "Catalog" -> "book-open-variant";
            case "DeviceType" -> "tag-outline";
            case "FinalCircuit" -> "vector-line";
            case "CircuitBranch" -> "source-branch";
            case "Circuit", "DistributionCircuit" -> "vector-polyline";
            case "ControlFunction" -> "gesture-tap";
            case "ControlChannel" -> "tune-vertical-variant";
            case "ServiceConnection" -> "transmission-tower-import";
            case "EnergyMeter", "ENERGY_METER" -> "meter-electric-outline";
            case "ServiceCircuitBreaker", "SERVICE_BREAKER" -> "shield-home-outline";
            case "Outlet", "OUTLET" -> "power-socket-fr";
            case "ControlDevice", "SWITCH" -> "toggle-switch-outline";
            case "PUSH_BUTTON" -> "gesture-tap-button";
            case "DIMMER" -> "brightness-6";
            case "SHUTTER_CONTROL" -> "window-shutter-settings";
            case "LUMINAIRE" -> "lightbulb-outline";
            case "SHUTTER_MOTOR" -> "window-shutter";
            case "HEATER" -> "radiator";
            case "WATER_HEATER" -> "water-boiler";
            case "VENTILATION" -> "fan";
            case "COOKER" -> "stove";
            case "FIXED_APPLIANCE", "OTHER_FIELD", "Equipment", "FieldEquipment" -> "power-plug-outline";
            case "ElectricalLoad" -> "flash-outline";
            case "MCB" -> "electric-switch";
            case "RCCB" -> "shield-half-full";
            case "RCBO" -> "shield-check-outline";
            case "FUSE" -> "fuse";
            case "ISOLATOR" -> "electric-switch-closed";
            case "SPD" -> "lightning-bolt-outline";
            case "IMPULSE_RELAY" -> "toggle-switch-variant";
            case "CONTACTOR" -> "electric-switch-closed";
            case "ModularDevice", "OTHER_MODULAR" -> "view-sequential-outline";
            default -> "shape-outline";
        });
    }
}
