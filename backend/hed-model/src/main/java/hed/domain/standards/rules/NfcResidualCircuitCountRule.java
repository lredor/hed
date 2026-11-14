/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.domain.standards.rules;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import hed.domain.HedDomain;
import hed.domain.HedValidation.Issue;
import hed.domain.HedValidation.Severity;
import hed.domain.standards.HedStandards;
import hed.homeelectrical.Circuit;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.ModularDevice;
import hed.homeelectrical.StandardVersion;

/** Checks the A5 limit of eight overcurrent-protected outgoing circuits under each final 30 mA RCCB. */
public final class NfcResidualCircuitCountRule {
    private NfcResidualCircuitCountRule() {
    }

    /**
     * Counts recorded circuits through distinct protective devices, not spare breakers, room devices or branches.
     * Intermediate RCCBs are followed; malformed cycles terminate and remain structural validation errors.
     * @param home the dwelling whose A5 profile enables this rule
     * @return warnings on overloaded 30 mA RCCBs, or unassessed warnings when their sensitivity is unknown
     */
    public static List<Issue> validate(Dwelling home) {
        if (home.getStandardVersion() != StandardVersion.NF_C15_100_A5_2015) {
            return List.of();
        }
        var devices = home.getBoards().stream().flatMap(board -> board.getDevices().stream()).toList();
        var outgoing = home.getCircuits().stream().map(Circuit::getProtectiveDevice).filter(Objects::nonNull)
                .filter(device -> EnumSet.of(DeviceKind.MCB, DeviceKind.RCBO, DeviceKind.FUSE).contains(HedDomain.kind(device))).distinct().toList();
        var issues = new ArrayList<Issue>();
        for (var rccb : devices) {
            if (HedDomain.kind(rccb) != DeviceKind.RCCB) {
                continue;
            }
            long count = outgoing.stream().filter(device -> below(device, rccb)).count();
            if (count <= 8) {
                continue;
            }
            var sensitivity = HedDomain.technicalNumber(rccb, "residualCurrentMa");
            if (sensitivity == null || !Double.isFinite(sensitivity) || sensitivity <= 0) {
                issues.add(new Issue(Severity.WARNING, "nfc.rccbCountUnassessed", rccb, List.of(HedStandards.reference(home.getStandardVersion()))));
            } else if (sensitivity <= 30) {
                issues.add(new Issue(Severity.WARNING, "nfc.rccbCircuitCount", rccb, List.of(count, 8, HedStandards.reference(home.getStandardVersion()))));
            }
        }
        return List.copyOf(issues);
    }

    /** Traverses only recorded upstream RCCB links and terminates when malformed data contains a cycle. */
    private static boolean below(ModularDevice device, ModularDevice rccb) {
        var visited = new HashSet<ModularDevice>();
        for (var current = device.getUpstreamRccb(); current != null && visited.add(current); current = current.getUpstreamRccb()) {
            if (current == rccb) {
                return true;
            }
        }
        return false;
    }
}
