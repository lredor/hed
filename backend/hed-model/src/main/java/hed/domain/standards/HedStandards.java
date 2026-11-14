/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.domain.standards;

import java.util.ArrayList;
import java.util.List;
import hed.domain.HedValidation.Issue;
import hed.domain.standards.rules.NfcConductorProtectionRule;
import hed.domain.standards.rules.NfcOutletCountRule;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.StandardVersion;

/** Dispatches documented, nonblocking design checks using the dwelling's explicitly selected standard profile. */
public final class HedStandards {
    private HedStandards() {
    }

    /**
     * Computes partial standard checks without changing the model or inferring an edition.
     * @param home the dwelling under validation
     * @return immutable warnings; an unspecified edition enables no normative rules
     */
    public static List<Issue> validate(Dwelling home) {
        if (home.getStandardVersion() == StandardVersion.UNSPECIFIED) {
            return List.of();
        }
        var issues = new ArrayList<Issue>();
        issues.addAll(NfcOutletCountRule.validate(home));
        issues.addAll(NfcConductorProtectionRule.validate(home));
        return List.copyOf(issues);
    }

    /**
     * Names the selected standard in diagnostic arguments independently of application language.
     * @param version a supported profile
     * @return its concise reference, suitable for a localized diagnostic
     */
    public static String reference(StandardVersion version) {
        return switch (version) {
            case NF_C15_100_BEFORE_A5 -> "NF C 15-100, pre-A5 (2010)";
            case NF_C15_100_A5_2015 -> "NF C 15-100, A5 (2015)";
            case UNSPECIFIED -> "";
        };
    }
}
