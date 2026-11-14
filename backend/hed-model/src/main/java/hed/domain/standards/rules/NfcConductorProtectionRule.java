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
import java.util.List;
import hed.domain.HedDomain;
import hed.domain.HedValidation.Issue;
import hed.domain.HedValidation.Severity;
import hed.domain.standards.HedStandards;
import hed.homeelectrical.CircuitUse;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.StandardVersion;

/** Checks the documented copper-section/protection matrix for general socket and lighting circuits. */
public final class NfcConductorProtectionRule {
    private NfcConductorProtectionRule() {
    }

    /**
     * Reads protection ratings from catalog types and keeps historical fuse allowances separate from A5.
     * Missing protection is handled by structural validation; incomplete known protections are marked unassessed.
     * @param home the dwelling whose explicitly selected profile governs these checks
     * @return warnings targeted at affected final circuits; unspecified or unsupported profiles enable no checks
     */
    public static List<Issue> validate(Dwelling home) {
        var version = home.getStandardVersion();
        if (version != StandardVersion.NF_C15_100_BEFORE_A5 && version != StandardVersion.NF_C15_100_A5_2015) {
            return List.of();
        }
        var issues = new ArrayList<Issue>();
        for (var item : home.getCircuits()) {
            if (!(item instanceof FinalCircuit circuit) || !EnumSet.of(CircuitUse.SOCKET_OUTLETS, CircuitUse.LIGHTING).contains(circuit.getUsage())) {
                continue;
            }
            var protection = circuit.getProtectiveDevice();
            var kind = protection == null ? null : HedDomain.kind(protection);
            if (!EnumSet.of(DeviceKind.MCB, DeviceKind.RCBO, DeviceKind.FUSE).contains(kind)) {
                continue;
            }
            var reference = HedStandards.reference(version);
            if (kind == DeviceKind.FUSE && version == StandardVersion.NF_C15_100_A5_2015) {
                issues.add(new Issue(Severity.WARNING, "nfc.fuseNotAllowed", circuit, List.of(reference)));
                continue;
            }
            var section = circuit.getConductorSectionMm2();
            var current = HedDomain.technicalNumber(protection, "ratedCurrentA");
            if (section == null || !Double.isFinite(section) || section <= 0 || current == null || !Double.isFinite(current) || current <= 0) {
                issues.add(new Issue(Severity.WARNING, "nfc.protectionUnassessed", circuit, List.of(reference)));
                continue;
            }
            if (section < 1.5) {
                issues.add(new Issue(Severity.WARNING, "nfc.conductorSection", circuit, List.of(section, 1.5, reference)));
                continue;
            }
            boolean lighting = circuit.getUsage() == CircuitUse.LIGHTING;
            if (kind == DeviceKind.FUSE && !lighting && section < 2.5) {
                issues.add(new Issue(Severity.WARNING, "nfc.fuseNotAllowed", circuit, List.of(reference)));
                continue;
            }
            int maximum = kind == DeviceKind.FUSE ? lighting ? 10 : 16 : lighting || section < 2.5 ? 16 : 20;
            if (current > maximum) {
                issues.add(new Issue(Severity.WARNING, "nfc.protectionRating", circuit, List.of(current, maximum, section, reference)));
            }
        }
        return List.copyOf(issues);
    }
}
