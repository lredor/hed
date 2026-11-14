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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.eclipse.sirius.components.forms.description.IfDescription;
import org.eclipse.sirius.components.forms.description.PageDescription;
import org.eclipse.sirius.components.forms.description.SelectDescription;
import org.eclipse.sirius.components.representations.Success;
import org.eclipse.sirius.components.representations.VariableManager;
import org.junit.jupiter.api.Test;
import hed.domain.HedSamples;
import hed.domain.Messages;
import hed.homeelectrical.StandardVersion;

/** Exercises the native dwelling dropdown and its localized labels through the normal mutation handler. */
class StandardPropertiesTest {
    @Test
    void dwellingSelectorListsSupportedProfilesAndPersistsItsSelectionInBothLanguages() {
        for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            var home = HedSamples.empty("Home");
            var pages = new ArrayList<PageDescription>();
            BootstrapIntegrationTest.properties(new BootstrapIntegrationTest.Identity(), locale).addPropertiesDescriptions(pages::add);
            var variables = new VariableManager();
            variables.put(VariableManager.SELF, home);
            var select = pages.stream().filter(page -> page.getCanCreatePredicate().test(variables)).flatMap(page -> page.getGroupDescriptions().stream())
                    .flatMap(group -> group.getControlDescriptions().stream()).filter(IfDescription.class::isInstance).map(IfDescription.class::cast)
                    .flatMap(condition -> condition.getControlDescriptions().stream()).filter(SelectDescription.class::isInstance).map(SelectDescription.class::cast).findFirst().orElseThrow();
            assertEquals(Messages.text(locale, "feature.standardVersion"), select.getLabelProvider().apply(variables));
            assertEquals(List.of(StandardVersion.values()), select.getOptionsProvider().apply(variables));
            assertEquals("UNSPECIFIED", select.getValueProvider().apply(variables));
            for (var version : StandardVersion.values()) {
                var candidate = new VariableManager();
                candidate.put("candidate", version);
                var label = select.getOptionLabelProvider().apply(candidate);
                assertFalse(label.startsWith("enum."));
                assertTrue(label.contains(version == StandardVersion.UNSPECIFIED ? locale.getLanguage().equals("fr") ? "spécifiée" : "Unspecified" : "NF C 15-100"));
                assertInstanceOf(Success.class, select.getNewValueHandler().apply(variables, version.getLiteral()));
                assertEquals(version, home.getStandardVersion());
            }
        }
    }
}
