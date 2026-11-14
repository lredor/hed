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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import hed.domain.standards.rules.NfcConductorProtectionRule;
import hed.homeelectrical.CircuitUse;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.StandardVersion;

/** Covers breaker sizing, historical fuse limits, incomplete data and live catalog-owned ratings. */
class NfcConductorProtectionTest {
    private FinalCircuit circuit(StandardVersion version, CircuitUse use, Double section, Double rating, DeviceKind kind) {
        var home = HedSamples.empty("Home");
        home.setStandardVersion(version);
        var board = HedDomain.F.createDistributionBoard();
        home.getBoards().add(board);
        var rail = HedDomain.F.createDINRail();
        board.getRails().add(rail);
        var type = HedDomain.type(home, kind);
        type.setRatedCurrentA(rating);
        var protection = HedDomain.addModularDevice(rail, type);
        var circuit = HedDomain.F.createFinalCircuit();
        home.getCircuits().add(circuit);
        circuit.setUsage(use);
        circuit.setConductorSectionMm2(section);
        circuit.setSourceBoard(board);
        circuit.setProtectiveDevice(protection);
        return circuit;
    }

    @Test
    void breakerAndRcboBoundariesAreAcceptedUnderBothSupportedProfiles() {
        for (var version : List.of(StandardVersion.NF_C15_100_BEFORE_A5, StandardVersion.NF_C15_100_A5_2015)) {
            for (var kind : List.of(DeviceKind.MCB, DeviceKind.RCBO)) {
                for (var use : List.of(CircuitUse.LIGHTING, CircuitUse.SOCKET_OUTLETS)) {
                    var circuit = circuit(version, use, 1.5, 16d, kind);
                    assertTrue(NfcConductorProtectionRule.validate(HedDomain.dwelling(circuit)).isEmpty());
                }
                var circuit = circuit(version, CircuitUse.SOCKET_OUTLETS, 2.5, 20d, kind);
                assertTrue(NfcConductorProtectionRule.validate(HedDomain.dwelling(circuit)).isEmpty());
            }
        }
    }

    @Test
    void oversizingProducesTargetedLocalizedWarnings() {
        for (var use : List.of(CircuitUse.LIGHTING, CircuitUse.SOCKET_OUTLETS)) {
            var circuit = circuit(StandardVersion.NF_C15_100_A5_2015, use, 1.5, 20d, DeviceKind.MCB);
            var issue = NfcConductorProtectionRule.validate(HedDomain.dwelling(circuit)).getFirst();
            assertEquals("nfc.protectionRating", issue.code());
            assertSame(circuit, issue.target());
            assertEquals(HedValidation.Severity.WARNING, issue.severity());
            for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
                assertTrue(issue.message(locale).contains("20"));
                assertTrue(issue.message(locale).contains("16"));
            }
        }
        var sockets = circuit(StandardVersion.NF_C15_100_A5_2015, CircuitUse.SOCKET_OUTLETS, 2.5, 25d, DeviceKind.MCB);
        assertEquals("nfc.protectionRating", NfcConductorProtectionRule.validate(HedDomain.dwelling(sockets)).getFirst().code());
    }

    @Test
    void insufficientCopperSectionIsReportedEvenWithASmallBreaker() {
        var circuit = circuit(StandardVersion.NF_C15_100_A5_2015, CircuitUse.LIGHTING, 0.75, 6d, DeviceKind.MCB);
        assertEquals("nfc.conductorSection", NfcConductorProtectionRule.validate(HedDomain.dwelling(circuit)).getFirst().code());
    }

    @Test
    void historicalFusesUseTheirOwnLimitsAndAreRejectedForSmallSocketSections() {
        var sockets = circuit(StandardVersion.NF_C15_100_BEFORE_A5, CircuitUse.SOCKET_OUTLETS, 2.5, 16d, DeviceKind.FUSE);
        assertTrue(NfcConductorProtectionRule.validate(HedDomain.dwelling(sockets)).isEmpty());
        sockets.getProtectiveDevice().getDeviceType().setRatedCurrentA(20d);
        assertEquals("nfc.protectionRating", NfcConductorProtectionRule.validate(HedDomain.dwelling(sockets)).getFirst().code());
        sockets.setConductorSectionMm2(1.5);
        assertEquals("nfc.fuseNotAllowed", NfcConductorProtectionRule.validate(HedDomain.dwelling(sockets)).getFirst().code());
        var lighting = circuit(StandardVersion.NF_C15_100_BEFORE_A5, CircuitUse.LIGHTING, 1.5, 10d, DeviceKind.FUSE);
        assertTrue(NfcConductorProtectionRule.validate(HedDomain.dwelling(lighting)).isEmpty());
        lighting.getProtectiveDevice().getDeviceType().setRatedCurrentA(16d);
        assertEquals("nfc.protectionRating", NfcConductorProtectionRule.validate(HedDomain.dwelling(lighting)).getFirst().code());
    }

    @Test
    void a5ForbidsFusesForTheSupportedCircuitUses() {
        for (var use : List.of(CircuitUse.LIGHTING, CircuitUse.SOCKET_OUTLETS)) {
            var circuit = circuit(StandardVersion.NF_C15_100_A5_2015, use, 2.5, 10d, DeviceKind.FUSE);
            assertEquals("nfc.fuseNotAllowed", NfcConductorProtectionRule.validate(HedDomain.dwelling(circuit)).getFirst().code());
        }
    }

    @Test
    void missingOrNonfiniteSizingDataIsNotTreatedAsAnAssessedCircuit() {
        var circuit = circuit(StandardVersion.NF_C15_100_A5_2015, CircuitUse.LIGHTING, null, 16d, DeviceKind.MCB);
        assertEquals("nfc.protectionUnassessed", NfcConductorProtectionRule.validate(HedDomain.dwelling(circuit)).getFirst().code());
        circuit.setConductorSectionMm2(1.5);
        for (var current : java.util.Arrays.asList(null, Double.NaN, Double.POSITIVE_INFINITY)) {
            circuit.getProtectiveDevice().getDeviceType().setRatedCurrentA(current);
            assertEquals("nfc.protectionUnassessed", NfcConductorProtectionRule.validate(HedDomain.dwelling(circuit)).getFirst().code());
        }
    }

    @Test
    void unspecifiedProfilesAndUnimplementedUsesDoNotApplyThisMatrix() {
        var circuit = circuit(StandardVersion.UNSPECIFIED, CircuitUse.LIGHTING, 0.75, 32d, DeviceKind.MCB);
        assertTrue(NfcConductorProtectionRule.validate(HedDomain.dwelling(circuit)).isEmpty());
        var home = HedDomain.dwelling(circuit);
        home.setStandardVersion(StandardVersion.NF_C15_100_A5_2015);
        circuit.setUsage(CircuitUse.HEATING);
        assertTrue(NfcConductorProtectionRule.validate(home).isEmpty());
        circuit.setUsage(CircuitUse.UNKNOWN);
        assertTrue(NfcConductorProtectionRule.validate(home).isEmpty());
    }

    @Test
    void catalogEditsRecomputeWarningsAndRemainSaveable() {
        var circuit = circuit(StandardVersion.NF_C15_100_A5_2015, CircuitUse.SOCKET_OUTLETS, 1.5, 16d, DeviceKind.MCB);
        var home = HedDomain.dwelling(circuit);
        var type = circuit.getProtectiveDevice().getDeviceType();
        assertTrue(NfcConductorProtectionRule.validate(home).isEmpty());
        assertDoesNotThrow(() -> HedDomain.set(type, "ratedCurrentA", 20d));
        assertEquals("nfc.protectionRating", NfcConductorProtectionRule.validate(home).getFirst().code());
        HedDomain.set(type, "ratedCurrentA", 16d);
        assertTrue(NfcConductorProtectionRule.validate(home).isEmpty());
    }
}
