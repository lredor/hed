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
import java.util.Locale;
import org.eclipse.sirius.components.forms.description.IfDescription;
import org.eclipse.sirius.components.forms.description.PageDescription;
import org.eclipse.sirius.components.forms.description.TextfieldDescription;
import org.eclipse.sirius.components.representations.Failure;
import org.eclipse.sirius.components.representations.VariableManager;
import org.junit.jupiter.api.Test;
import hed.domain.HedDomain;
import hed.domain.HedSamples;

class UpstreamProtectionIntegrationTest {
    @Test
    void rccbDownstreamPropertiesAreReadOnlyAndFollowProtectionEdits() {
        var home = HedSamples.example();
        var rccb = home.getBoards().get(0).getDevices().get(0);
        var breaker = home.getBoards().get(0).getDevices().get(1);
        var pages = new ArrayList<PageDescription>();
        BootstrapIntegrationTest.properties(new BootstrapIntegrationTest.Identity(), Locale.ENGLISH).addPropertiesDescriptions(pages::add);
        var vm = new VariableManager();
        vm.put(VariableManager.SELF, rccb);
        var page = pages.stream().filter(description -> description.getCanCreatePredicate().test(vm)).findFirst().orElseThrow();
        var condition = page.getGroupDescriptions().stream().flatMap(group -> group.getControlDescriptions().stream()).filter(IfDescription.class::isInstance).map(IfDescription.class::cast)
                .filter(description -> description.getId().equals("hed.visible.ModularDevice.downstreamDevices")).findFirst().orElseThrow();
        var field = assertInstanceOf(TextfieldDescription.class, condition.getControlDescriptions().get(0));
        assertTrue(condition.getPredicate().apply(vm));
        assertTrue(field.getIsReadOnlyProvider().apply(vm));
        assertEquals("Q1, Q2, Q3, Q5", field.getValueProvider().apply(vm));
        assertInstanceOf(Failure.class, field.getNewValueHandler().apply(vm, "Q4"));
        assertEquals("Q1, Q2, Q3, Q5", field.getValueProvider().apply(vm));
        HedDomain.set(breaker, "upstreamRccb", null);
        assertEquals("Q2, Q3, Q5", field.getValueProvider().apply(vm));
        HedDomain.set(breaker, "upstreamRccb", rccb);
        assertEquals("Q1, Q2, Q3, Q5", field.getValueProvider().apply(vm));
        vm.put(VariableManager.SELF, breaker);
        assertFalse(condition.getPredicate().apply(vm));
    }
}
