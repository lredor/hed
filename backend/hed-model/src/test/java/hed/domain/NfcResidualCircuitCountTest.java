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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import hed.domain.standards.rules.NfcResidualCircuitCountRule;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.ModularDevice;
import hed.homeelectrical.StandardVersion;

/** Exercises A5 outgoing-circuit boundaries, profile gating, intermediate devices and catalog sensitivity edits. */
class NfcResidualCircuitCountTest {
    private ModularDevice rccb(Dwelling home) {
        if (home.getBoards().isEmpty()) {
            var board = HedDomain.F.createDistributionBoard();
            home.getBoards().add(board);
            var rail = HedDomain.F.createDINRail();
            rail.setCapacityModules(100);
            board.getRails().add(rail);
        }
        var type = HedDomain.type(home, DeviceKind.RCCB);
        type.setResidualCurrentMa(30d);
        type.setRatedCurrentA(63d);
        return HedDomain.addModularDevice(home.getBoards().getFirst().getRails().getFirst(), type);
    }

    private void departures(ModularDevice rccb, int count, DeviceKind kind) {
        var home = HedDomain.dwelling(rccb);
        var rail = home.getBoards().getFirst().getRails().getFirst();
        for (int index = 0; index < count; index++) {
            var device = HedDomain.addModularDevice(rail, HedDomain.type(home, kind));
            device.setUpstreamRccb(rccb);
            if (kind == DeviceKind.MCB || kind == DeviceKind.RCBO || kind == DeviceKind.FUSE) {
                var circuit = HedDomain.F.createFinalCircuit();
                circuit.setSourceBoard(home.getBoards().getFirst());
                circuit.setProtectiveDevice(device);
                home.getCircuits().add(circuit);
            }
        }
    }

    @Test
    void eightRecordedDeparturesPassAndTheNinthWarnsOnTheRccb() {
        var home = HedSamples.empty("Home");
        home.setStandardVersion(StandardVersion.NF_C15_100_A5_2015);
        var rccb = rccb(home);
        departures(rccb, 8, DeviceKind.MCB);
        assertTrue(NfcResidualCircuitCountRule.validate(home).isEmpty());
        departures(rccb, 1, DeviceKind.MCB);
        var issues = NfcResidualCircuitCountRule.validate(home);
        assertEquals(1, issues.size());
        var issue = issues.getFirst();
        assertEquals("nfc.rccbCircuitCount", issue.code());
        assertSame(rccb, issue.target());
        for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            assertTrue(issue.message(locale).contains("9"));
            assertTrue(issue.message(locale).contains("8"));
        }
    }

    @Test
    void spareBreakersWithoutAnAssociatedCircuitDoNotCountAsDepartures() {
        var home = HedSamples.empty("Home");
        home.setStandardVersion(StandardVersion.NF_C15_100_A5_2015);
        var rccb = rccb(home);
        departures(rccb, 9, DeviceKind.MCB);
        home.getCircuits().clear();
        assertTrue(NfcResidualCircuitCountRule.validate(home).isEmpty());
    }

    @Test
    void historicalAndUnspecifiedProfilesDoNotInheritTheA5Limit() {
        var home = HedSamples.empty("Home");
        var rccb = rccb(home);
        departures(rccb, 9, DeviceKind.MCB);
        for (var version : List.of(StandardVersion.UNSPECIFIED, StandardVersion.NF_C15_100_BEFORE_A5)) {
            home.setStandardVersion(version);
            assertTrue(NfcResidualCircuitCountRule.validate(home).isEmpty());
        }
    }

    @Test
    void controlsAndSurgeProtectionAreExcludedAndIndependentRccbsStaySeparate() {
        var home = HedSamples.empty("Home");
        home.setStandardVersion(StandardVersion.NF_C15_100_A5_2015);
        var first = rccb(home);
        var second = rccb(home);
        departures(first, 8, DeviceKind.MCB);
        departures(second, 8, DeviceKind.RCBO);
        departures(first, 5, DeviceKind.IMPULSE_RELAY);
        departures(first, 5, DeviceKind.CONTACTOR);
        departures(first, 5, DeviceKind.SPD);
        assertTrue(NfcResidualCircuitCountRule.validate(home).isEmpty());
        departures(second, 1, DeviceKind.RCBO);
        assertSame(second, NfcResidualCircuitCountRule.validate(home).getFirst().target());
    }

    @Test
    void unknownSensitivityIsUnassessedAndHigherSensitivityIsOutsideTheFinal30mARule() {
        var home = HedSamples.empty("Home");
        home.setStandardVersion(StandardVersion.NF_C15_100_A5_2015);
        var rccb = rccb(home);
        departures(rccb, 9, DeviceKind.MCB);
        var type = rccb.getDeviceType();
        type.setResidualCurrentMa(null);
        assertEquals("nfc.rccbCountUnassessed", NfcResidualCircuitCountRule.validate(home).getFirst().code());
        type.setResidualCurrentMa(300d);
        assertTrue(NfcResidualCircuitCountRule.validate(home).isEmpty());
        type.setResidualCurrentMa(30d);
        assertEquals("nfc.rccbCircuitCount", NfcResidualCircuitCountRule.validate(home).getFirst().code());
    }

    @Test
    void nestedRccbsPropagateTheirDeparturesAndMalformedCyclesTerminate() {
        var home = HedSamples.empty("Home");
        home.setStandardVersion(StandardVersion.NF_C15_100_A5_2015);
        var parent = rccb(home);
        var child = rccb(home);
        rccb(home);
        child.setUpstreamRccb(parent);
        departures(child, 9, DeviceKind.MCB);
        assertEquals(2, NfcResidualCircuitCountRule.validate(home).size());
        parent.setUpstreamRccb(child);
        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> assertEquals(2, NfcResidualCircuitCountRule.validate(home).size()));
    }
}
