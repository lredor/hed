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
import org.eclipse.sirius.components.forms.description.TextfieldDescription;
import org.eclipse.sirius.components.representations.Failure;
import org.eclipse.sirius.components.representations.Success;
import org.eclipse.sirius.components.representations.VariableManager;
import org.junit.jupiter.api.Test;
import hed.domain.HedSamples;
import hed.domain.Messages;

/** Verifies localized, editable width controls and calculated read-only height controls on storeys. */
class FloorPlanPropertiesTest {
    @Test
    void widthAndHeightAreVisibleInBothLanguagesWhileImageMetadataRemainsHidden() {
        for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            var storey = HedSamples.example().getStoreys().getFirst();
            var variables = new VariableManager();
            variables.put(VariableManager.SELF, storey);
            var pages = new ArrayList<PageDescription>();
            BootstrapIntegrationTest.properties(new BootstrapIntegrationTest.Identity(), locale).addPropertiesDescriptions(pages::add);
            var controls = pages.stream().filter(page -> page.getCanCreatePredicate().test(variables)).flatMap(page -> page.getGroupDescriptions().stream())
                    .flatMap(group -> group.getControlDescriptions().stream()).filter(IfDescription.class::isInstance).map(IfDescription.class::cast)
                    .filter(condition -> condition.getPredicate().apply(variables)).flatMap(condition -> condition.getControlDescriptions().stream()).filter(TextfieldDescription.class::isInstance)
                    .map(TextfieldDescription.class::cast).toList();
            var width = controls.stream().filter(control -> control.getIdProvider().apply(variables).endsWith(".planWidth")).findFirst().orElseThrow();
            var height = controls.stream().filter(control -> control.getIdProvider().apply(variables).endsWith(".planHeight")).findFirst().orElseThrow();
            assertEquals(Messages.text(locale, "feature.planWidth"), width.getLabelProvider().apply(variables));
            assertEquals(Messages.text(locale, "feature.planHeight"), height.getLabelProvider().apply(variables));
            assertFalse(width.getIsReadOnlyProvider().apply(variables));
            assertTrue(height.getIsReadOnlyProvider().apply(variables));
            assertTrue(
                    controls.stream().noneMatch(control -> control.getIdProvider().apply(variables).endsWith(".planImageId") || control.getIdProvider().apply(variables).endsWith(".planAspectRatio")));
            assertInstanceOf(Success.class, width.getNewValueHandler().apply(variables, "2400"));
            assertEquals("2400", width.getValueProvider().apply(variables));
            assertEquals("1600", height.getValueProvider().apply(variables));
            assertInstanceOf(Failure.class, height.getNewValueHandler().apply(variables, "999"));
            for (var invalid : List.of("0", "-1", "", "text", "123.4", "2147483648")) {
                assertInstanceOf(Failure.class, width.getNewValueHandler().apply(variables, invalid));
                assertEquals(2400, storey.getPlanWidth());
                assertEquals(1600, storey.getPlanHeight());
            }
        }
    }
}
