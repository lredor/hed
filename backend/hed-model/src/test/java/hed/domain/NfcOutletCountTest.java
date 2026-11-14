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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.junit.jupiter.api.Test;
import hed.domain.standards.HedStandards;
import hed.domain.standards.rules.NfcOutletCountRule;
import hed.homeelectrical.CircuitUse;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.OutletBlock;
import hed.homeelectrical.StandardVersion;

/** Covers historical socket-box counting, A5 limits, circuit branches and persisted explicit standard selection. */
class NfcOutletCountTest {
    /** Creates an isolated circuit whose use and section are known but whose physical installation can be incomplete. */
    private FinalCircuit circuit(StandardVersion version, double section) {
        var home = HedSamples.empty("Home");
        home.setStandardVersion(version);
        var circuit = HedDomain.named(HedDomain.F.createFinalCircuit(), "Socket circuit");
        circuit.setUsage(CircuitUse.SOCKET_OUTLETS);
        circuit.setConductorSectionMm2(section);
        home.getCircuits().add(circuit);
        return circuit;
    }

    private void standalone(FinalCircuit circuit, int number) {
        var home = HedDomain.dwelling(circuit);
        for (int index = 0; index < number; index++) {
            HedDomain.addEquipment(home, HedDomain.type(home, DeviceKind.OUTLET)).setCircuit(circuit);
        }
    }

    @Test
    void newAndOldResourcesUseNoImplicitStandardAndExplicitSelectionPersists() throws Exception {
        var home = HedSamples.empty("Home");
        assertEquals(StandardVersion.UNSPECIFIED, home.getStandardVersion());
        for (var version : StandardVersion.values()) {
            home.setStandardVersion(version);
            var resource = new XMIResourceImpl(URI.createURI("memory:/standard.xmi"));
            resource.getContents().add(home);
            var bytes = new ByteArrayOutputStream();
            resource.save(bytes, Map.of());
            if (version == StandardVersion.UNSPECIFIED) {
                assertFalse(bytes.toString(java.nio.charset.StandardCharsets.UTF_8).contains("standardVersion="));
            }
            var loaded = new XMIResourceImpl(URI.createURI("memory:/loaded.xmi"));
            loaded.load(new ByteArrayInputStream(bytes.toByteArray()), Map.of());
            assertEquals(version, ((Dwelling) loaded.getContents().getFirst()).getStandardVersion());
        }
    }

    @Test
    void countsEverySocleAfterA5AndUsesHistoricalWeightsPerCompleteBoxBeforeA5() {
        for (var shape : List.of(new int[] { 1, 1, 1 }, new int[] { 1, 2, 1 }, new int[] { 1, 3, 2 }, new int[] { 2, 2, 2 }, new int[] { 1, 5, 3 }, new int[] { 2, 3, 3 })) {
            var circuit = circuit(StandardVersion.NF_C15_100_BEFORE_A5, 2.5);
            var home = HedDomain.dwelling(circuit);
            var type = HedDomain.type(home, DeviceKind.OUTLET_BLOCK);
            type.setNbRows(shape[0]);
            type.setNbColumns(shape[1]);
            var block = (OutletBlock) HedDomain.addEquipment(home, type);
            HedDomain.set(circuit, "equipment", List.of(block));
            assertEquals(shape[2], NfcOutletCountRule.count(circuit, StandardVersion.NF_C15_100_BEFORE_A5));
            assertEquals(shape[0] * shape[1], NfcOutletCountRule.count(circuit, StandardVersion.NF_C15_100_A5_2015));
        }
    }

    @Test
    void oldAndNewSectionLimitsAcceptTheirBoundaryAndWarnAboveIt() {
        for (var version : List.of(StandardVersion.NF_C15_100_BEFORE_A5, StandardVersion.NF_C15_100_A5_2015)) {
            for (double section : List.of(1.5, 2.5)) {
                boolean legacy = version == StandardVersion.NF_C15_100_BEFORE_A5;
                int limit = section == 1.5 ? legacy ? 5 : 8 : legacy ? 8 : 12;
                var circuit = circuit(version, section);
                var home = HedDomain.dwelling(circuit);
                standalone(circuit, limit);
                assertTrue(HedStandards.validate(home).isEmpty());
                standalone(circuit, 1);
                var issues = HedStandards.validate(home);
                assertEquals(1, issues.size());
                var issue = issues.getFirst();
                assertEquals("nfc.outletCount", issue.code());
                assertSame(circuit, issue.target());
                assertEquals(HedValidation.Severity.WARNING, issue.severity());
                assertEquals(limit + 1, issue.arguments().getFirst());
                for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
                    assertTrue(issue.message(locale).contains(Integer.toString(limit + 1)));
                    assertTrue(issue.message(locale).contains(Integer.toString(limit)));
                    assertFalse(issue.message(locale).contains("{0}"));
                    assertTrue(issue.message(locale).contains("NF C 15-100"));
                }
            }
        }
    }

    @Test
    void switchingEditionUpdatesWarningsWithoutPreventingDraftEdits() {
        var circuit = circuit(StandardVersion.UNSPECIFIED, 2.5);
        var home = HedDomain.dwelling(circuit);
        standalone(circuit, 9);
        assertTrue(HedStandards.validate(home).isEmpty());
        assertDoesNotThrow(() -> HedDomain.set(home, "standardVersion", StandardVersion.NF_C15_100_BEFORE_A5));
        assertEquals("nfc.outletCount", HedStandards.validate(home).getFirst().code());
        HedDomain.set(home, "standardVersion", StandardVersion.NF_C15_100_A5_2015);
        assertTrue(HedStandards.validate(home).isEmpty());
        HedDomain.set(home, "standardVersion", StandardVersion.UNSPECIFIED);
        assertTrue(HedStandards.validate(home).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> NfcOutletCountRule.count(circuit, StandardVersion.UNSPECIFIED));
    }

    @Test
    void movingAWholeBlockBetweenBranchesAndCircuitsMovesEveryCountedOutlet() {
        var first = circuit(StandardVersion.NF_C15_100_BEFORE_A5, 2.5);
        var home = HedDomain.dwelling(first);
        var second = HedDomain.F.createFinalCircuit();
        home.getCircuits().add(second);
        var type = HedDomain.type(home, DeviceKind.OUTLET_BLOCK);
        type.setNbRows(2);
        type.setNbColumns(3);
        var block = (OutletBlock) HedDomain.addEquipment(home, type);
        HedDomain.set(block, "circuit", first);
        assertEquals(3, NfcOutletCountRule.count(first, StandardVersion.NF_C15_100_BEFORE_A5));
        assertEquals(6, NfcOutletCountRule.count(first, StandardVersion.NF_C15_100_A5_2015));
        var branch = HedDomain.F.createCircuitBranch();
        second.getBranches().add(branch);
        HedDomain.assignToBranch(branch, List.of(block));
        assertEquals(0, NfcOutletCountRule.count(first, StandardVersion.NF_C15_100_A5_2015));
        assertEquals(6, NfcOutletCountRule.count(second, StandardVersion.NF_C15_100_A5_2015));
        assertEquals(3, NfcOutletCountRule.count(second, StandardVersion.NF_C15_100_BEFORE_A5));
    }

    @Test
    void unknownSectionsAreExplicitlyUnassessedAndSpecializedUsesAreExcluded() {
        var circuit = circuit(StandardVersion.NF_C15_100_A5_2015, 4);
        var home = HedDomain.dwelling(circuit);
        assertEquals("nfc.outletSectionUnassessed", HedStandards.validate(home).getFirst().code());
        circuit.setConductorSectionMm2(null);
        assertEquals("nfc.outletSectionUnassessed", HedStandards.validate(home).getFirst().code());
        circuit.setUsage(CircuitUse.DEDICATED_APPLIANCE);
        assertTrue(HedStandards.validate(home).isEmpty());
        circuit.setUsage(CircuitUse.UNKNOWN);
        assertTrue(HedStandards.validate(home).isEmpty());
    }
}
