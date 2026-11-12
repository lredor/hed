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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.eclipse.sirius.components.forms.description.IfDescription;
import org.eclipse.sirius.components.forms.description.MultiSelectDescription;
import org.eclipse.sirius.components.forms.description.PageDescription;
import org.eclipse.sirius.components.representations.Success;
import org.eclipse.sirius.components.representations.VariableManager;
import org.junit.jupiter.api.Test;
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.domain.Messages;
import hed.homeelectrical.ControlDevice;
import hed.homeelectrical.ControlFunction;
import hed.starter.representations.details.HedPropertiesDescriptionRegistryConfigurer;

/** Verifies distinguishable channel choices, stable selections and live device/channel renaming in Details. */
class ControlChannelPropertiesTest {
    private final BootstrapIntegrationTest.Identity identity = new BootstrapIntegrationTest.Identity();

    private VariableManager variables(ControlFunction function) {
        var variables = new VariableManager();
        variables.put(VariableManager.SELF, function);
        return variables;
    }

    private MultiSelectDescription channels(ControlFunction function, Locale locale) {
        var pages = new ArrayList<PageDescription>();
        new HedPropertiesDescriptionRegistryConfigurer(identity, locale).addPropertiesDescriptions(pages::add);
        var variables = variables(function);
        return pages.stream().filter(page -> page.getCanCreatePredicate().test(variables)).flatMap(page -> page.getGroupDescriptions().stream())
                .flatMap(group -> group.getControlDescriptions().stream()).filter(IfDescription.class::isInstance).map(IfDescription.class::cast)
                .flatMap(condition -> condition.getControlDescriptions().stream()).filter(MultiSelectDescription.class::isInstance).map(MultiSelectDescription.class::cast)
                .filter(widget -> widget.getIdProvider().apply(variables).equals("hed.ControlFunction.channels")).findFirst().orElseThrow();
    }

    @Test
    void identicalChannelNamesRemainDistinctAndSelectTheIntendedDeviceInBothLanguages() {
        for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            var home = HedSamples.example(locale);
            var devices = home.getEquipment().stream().filter(ControlDevice.class::isInstance).map(ControlDevice.class::cast).toList();
            var first = devices.getFirst();
            var second = devices.get(1);
            first.setName("Entrée");
            second.setName("Côté fenêtre");
            var channel = second.getChannels().getFirst();
            var function = home.getControlFunctions().getFirst();
            var widget = channels(function, locale);
            var variables = variables(function);
            var localName = Messages.text(locale, "channel.defaultName", 1);
            assertEquals(localName, first.getChannels().getFirst().getName());
            assertEquals(localName, channel.getName());
            assertTrue(widget.getOptionsProvider().apply(variables).containsAll(List.of(first.getChannels().getFirst(), channel)));
            variables.put("candidate", first.getChannels().getFirst());
            assertEquals("Entrée - " + localName, widget.getOptionLabelProvider().apply(variables));
            variables.put("candidate", channel);
            assertEquals("Côté fenêtre - " + localName, widget.getOptionLabelProvider().apply(variables));
            var channelId = widget.getOptionIdProvider().apply(variables);
            assertInstanceOf(Success.class, widget.getNewValuesHandler().apply(variables, List.of(channelId)));
            assertEquals(List.of(channel), function.getChannels());
            assertEquals(List.of(channelId), widget.getValuesProvider().apply(variables));
            second.setName("Palier");
            assertEquals("Palier - " + localName, widget.getOptionLabelProvider().apply(variables));
            channel.setName("Commande lumière");
            assertEquals("Palier - Commande lumière", widget.getOptionLabelProvider().apply(variables));
            assertEquals(channelId, widget.getOptionIdProvider().apply(variables));
            assertEquals(List.of(channelId), widget.getValuesProvider().apply(variables));
            assertSame(second, channel.eContainer());
            // Contextual labels must not expand the names shown beneath a device in the explorer or on its diagram ports.
            assertEquals("Commande lumière", HedDomain.label(channel, locale));
        }
    }

    @Test
    void unnamedDevicesUseLocalizedFallbacksAndDetachedChannelsKeepTheirOwnLabel() {
        for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            var home = HedSamples.example(locale);
            var device = home.getEquipment().stream().filter(ControlDevice.class::isInstance).map(ControlDevice.class::cast).findFirst().orElseThrow();
            device.setName(" ");
            var channel = device.getChannels().getFirst();
            var function = home.getControlFunctions().getFirst();
            var widget = channels(function, locale);
            var variables = variables(function);
            variables.put("candidate", channel);
            assertEquals(Messages.text(locale, "class.ControlDevice") + " - " + channel.getName(), widget.getOptionLabelProvider().apply(variables));
            var detached = HedDomain.named(HedDomain.F.createControlChannel(), "Commande isolée");
            variables.put("candidate", detached);
            assertEquals("Commande isolée", widget.getOptionLabelProvider().apply(variables));
        }
    }
}
