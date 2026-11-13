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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.forms.description.AbstractControlDescription;
import org.eclipse.sirius.components.forms.description.IfDescription;
import org.eclipse.sirius.components.forms.description.PageDescription;
import org.eclipse.sirius.components.representations.VariableManager;
import org.junit.jupiter.api.Test;
import hed.domain.HedSamples;
import hed.homeelectrical.Equipment;
import hed.homeelectrical.ModularDevice;
import hed.homeelectrical.EnergyMeter;
import hed.starter.representations.details.HedPropertiesDescriptionRegistryConfigurer;

/** Verifies localized equipment tabs, ordered groups and retained catalog specifications. */
class EquipmentPropertiesTest {
    private final BootstrapIntegrationTest.Identity identity = new BootstrapIntegrationTest.Identity();

    private VariableManager variables(EObject object) {
        var variables = new VariableManager();
        variables.put(VariableManager.SELF, object);
        return variables;
    }

    private List<PageDescription> pages(EObject object, Locale locale) {
        var descriptions = new ArrayList<PageDescription>();
        new HedPropertiesDescriptionRegistryConfigurer(identity, locale).addPropertiesDescriptions(descriptions::add);
        return descriptions.stream().filter(page -> page.getCanCreatePredicate().test(variables(object))).toList();
    }

    private List<AbstractControlDescription> controls(PageDescription page, VariableManager variables) {
        return page.getGroupDescriptions().stream().flatMap(group -> group.getControlDescriptions().stream())
                .flatMap(control -> control instanceof IfDescription condition
                        ? condition.getPredicate().apply(variables) ? condition.getControlDescriptions().stream() : java.util.stream.Stream.empty()
                        : java.util.stream.Stream.of(control))
                .toList();
    }

    @Test
    void installedEquipmentUsesTwoLocalizedTabsWithTechnicalControlsOnlyInAdvanced() {
        for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            var home = HedSamples.example(locale);
            var equipment = new ArrayList<Equipment>(home.getEquipment());
            equipment.addAll(home.getBoards().getFirst().getDevices());
            equipment.add(home.getServiceConnection().getMeter());
            equipment.add(home.getServiceConnection().getMainBreaker());
            for (var device : equipment) {
                var variables = variables(device);
                var pages = pages(device, locale);
                assertEquals(locale == Locale.FRENCH ? List.of("Général", "Avancé") : List.of("General", "Advanced"), pages.stream().map(page -> page.getLabelProvider().apply(variables)).toList());
                var baseId = "hed.properties." + device.eClass().getName();
                assertEquals(List.of(baseId, baseId + ".advanced"), pages.stream().map(page -> page.getIdProvider().apply(variables)).toList());
                var generalGroups = pages.getFirst().getGroupDescriptions();
                assertTrue(generalGroups.getFirst().getIdProvider().apply(variables).endsWith(".general"));
                assertTrue(generalGroups.getLast().getIdProvider().apply(variables).endsWith(".validation"));
                assertFalse(generalGroups.stream().anyMatch(group -> group.getIdProvider().apply(variables).endsWith(".technical")));
                assertEquals(1, pages.getLast().getGroupDescriptions().size());
                assertTrue(pages.getLast().getGroupDescriptions().getFirst().getIdProvider().apply(variables).endsWith(".technical"));
                var general = generalGroups.getFirst().getControlDescriptions().stream().map(IfDescription.class::cast).flatMap(condition -> condition.getControlDescriptions().stream())
                        .map(AbstractControlDescription::getId).toList();
                var generalNames = device instanceof ModularDevice ? List.of("name", "labelText", "deviceType") : List.of("name", "description", "deviceType");
                assertEquals(generalNames.stream().map(feature -> "hed." + device.eClass().getName() + "." + feature).toList(), general);
                assertEquals(device instanceof EnergyMeter ? 2 : 3, generalGroups.size());
                assertTrue(controls(pages.getLast(), variables).stream().map(AbstractControlDescription::getId).anyMatch(id -> id.endsWith(".ratedCurrentA")));
            }
            assertEquals(1, pages(home.getCatalog().getDeviceTypes().getFirst(), locale).size());
            assertEquals(1, pages(home.getStoreys().getFirst(), locale).size());
            assertEquals(1, pages(home.getCircuits().getFirst(), locale).size());
        }
    }

}
