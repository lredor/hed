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
import java.util.IdentityHashMap;
import java.util.List;
import hed.domain.HedDomain;
import hed.domain.HedOutletBlocks;
import hed.domain.HedValidation.Issue;
import hed.domain.HedValidation.Severity;
import hed.domain.standards.HedStandards;
import hed.homeelectrical.CircuitUse;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.Outlet;
import hed.homeelectrical.OutletBlock;
import hed.homeelectrical.StandardVersion;

/** Checks general-purpose socket circuits using the pre-A5 or A5 counting method and copper-section limits. */
public final class NfcOutletCountRule {
    private NfcOutletCountRule() {
    }

    /**
     * Counts assigned outlets on the complete circuit, including all branches and nested block members.
     * Pre-A5 counts each same-circuit box as one for 1-2 outlets, two for 3-4, and three above four.
     * A5 counts every individual outlet; unassigned members of a block do not count on this circuit.
     * @param circuit the circuit whose actual electrical assignments are counted
     * @param version the selected supported edition
     * @return the edition-specific number of counted sockets
     * @throws IllegalArgumentException when no profile was selected
     */
    public static int count(FinalCircuit circuit, StandardVersion version) {
        if (version == StandardVersion.UNSPECIFIED) {
            throw new IllegalArgumentException("Select a standard profile before counting outlets");
        }
        var outlets = HedDomain.served(circuit).stream().flatMap(device -> device instanceof OutletBlock block ? HedOutletBlocks.outlets(block).stream() : java.util.stream.Stream.of(device))
                .filter(Outlet.class::isInstance).map(Outlet.class::cast).distinct().toList();
        if (version == StandardVersion.NF_C15_100_A5_2015) {
            return outlets.size();
        }
        if (version != StandardVersion.NF_C15_100_BEFORE_A5) {
            throw new IllegalArgumentException("Unsupported standard profile");
        }
        int standalone = 0;
        var boxes = new IdentityHashMap<OutletBlock, Integer>();
        for (var outlet : outlets) {
            var block = HedOutletBlocks.block(outlet);
            if (block == null) {
                standalone++;
            } else {
                boxes.merge(block, 1, Integer::sum);
            }
        }
        return standalone + boxes.values().stream().mapToInt(number -> number <= 2 ? 1 : number <= 4 ? 2 : 3).sum();
    }

    /**
     * Evaluates general-purpose circuits for the selected supported profile.
     * Only the documented 1.5 and 2.5 mm2 copper sections are assessed; unknown or other sections are reported as unassessed.
     * @param home the dwelling and its explicit standard profile
     * @return targeted, parameterized warnings; other circuit uses are outside this rule's scope
     */
    public static List<Issue> validate(Dwelling home) {
        var version = home.getStandardVersion();
        if (version != StandardVersion.NF_C15_100_BEFORE_A5 && version != StandardVersion.NF_C15_100_A5_2015) {
            return List.of();
        }
        var issues = new ArrayList<Issue>();
        for (var item : home.getCircuits()) {
            if (!(item instanceof FinalCircuit circuit) || circuit.getUsage() != CircuitUse.SOCKET_OUTLETS) {
                continue;
            }
            var section = circuit.getConductorSectionMm2();
            if (section == null || section != 1.5 && section != 2.5) {
                issues.add(new Issue(Severity.WARNING, "nfc.outletSectionUnassessed", circuit, List.of(HedStandards.reference(version))));
                continue;
            }
            boolean legacy = version == StandardVersion.NF_C15_100_BEFORE_A5;
            int limit = section == 1.5 ? legacy ? 5 : 8 : legacy ? 8 : 12;
            int count = count(circuit, version);
            if (count > limit) {
                issues.add(new Issue(Severity.WARNING, "nfc.outletCount", circuit, List.of(count, limit, section, HedStandards.reference(version))));
            }
        }
        return List.copyOf(issues);
    }
}
