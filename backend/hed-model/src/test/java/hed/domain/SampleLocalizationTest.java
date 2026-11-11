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
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.Test;
import hed.homeelectrical.ControlDevice;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.NamedElement;

/** Checks localized creation of the whole sample and persistence of editable names after a language change. */
class SampleLocalizationTest {
    @Test
    void frenchCreationLocalizesPlacesCircuitsEquipmentChannelsAndLabelText() {
        var home = HedSamples.example(Locale.CANADA_FRENCH);
        assertEquals("Logement d’exemple", home.getName());
        assertEquals("Rez-de-chaussée", home.getStoreys().getFirst().getName());
        assertEquals("Premier étage", home.getStoreys().get(1).getName());
        assertEquals("Séjour", home.getStoreys().getFirst().getRooms().getFirst().getName());
        assertEquals("Tableau principal", home.getBoards().getFirst().getName());
        assertEquals("Rangée 1", home.getBoards().getFirst().getRails().getFirst().getName());
        assertEquals("Éclairage du rez-de-chaussée", home.getCircuits().getFirst().getName());
        assertEquals("Prises du séjour", home.getCircuits().get(1).getName());
        assertEquals("Plafonnier", home.getEquipment().getFirst().getName());
        assertEquals("Prise du canapé", home.getEquipment().get(1).getName());
        assertEquals("Canal 1", ((ControlDevice) home.getEquipment().get(4)).getChannels().getFirst().getName());
        assertEquals("Canal 2", ((ControlDevice) home.getEquipment().get(4)).getChannels().get(1).getName());
        assertEquals("Éclairage du séjour", home.getControlFunctions().getFirst().getName());
        assertEquals("Branchement", home.getServiceConnection().getName());
        assertEquals("Rez-de-chaussée", home.getBoards().getFirst().getDevices().getFirst().getLabelText());
        assertTrue(new HedValidation().validate(home).isEmpty());
    }

    @Test
    void defaultCreationStaysEnglishAndStoredFrenchNamesRemainEditableUserData() {
        var english = HedSamples.example();
        assertEquals("Sample home", english.getName());
        assertEquals("Ground floor", english.getStoreys().getFirst().getName());
        assertEquals("Ceiling light", english.getEquipment().getFirst().getName());
        var french = HedSamples.example(Locale.FRENCH);
        var room = french.getStoreys().getFirst().getRooms().getFirst();
        assertEquals("Séjour", HedDomain.label(room, Locale.ENGLISH));
        HedDomain.set(room, "name", "Mon salon");
        assertEquals("Mon salon", HedDomain.label(room, Locale.ENGLISH));
        assertEquals("Plafonnier", HedDomain.label(french.getEquipment().getFirst(), Locale.ENGLISH));
        assertEquals("Living room", english.getStoreys().getFirst().getRooms().getFirst().getName());
    }

    @Test
    void accentedSampleNamesSurviveSemanticSerialization() throws Exception {
        var home = HedSamples.example(Locale.FRENCH);
        var resources = new ResourceSetImpl();
        resources.getPackageRegistry().put(HomeElectricalPackage.eNS_URI, HomeElectricalPackage.eINSTANCE);
        resources.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
        var source = resources.createResource(URI.createURI("sample.xmi"));
        source.getContents().add(home);
        var bytes = new ByteArrayOutputStream();
        source.save(bytes, Map.of(XMLResource.OPTION_ENCODING, "UTF-8"));
        var copy = new XMIResourceFactoryImpl().createResource(URI.createURI("copy.xmi"));
        resources.getResources().add(copy);
        copy.load(new ByteArrayInputStream(bytes.toByteArray()), Map.of());
        var loaded = (Dwelling) copy.getContents().getFirst();
        assertEquals(HedDomain.contents(home).stream().filter(NamedElement.class::isInstance).map(NamedElement.class::cast).map(NamedElement::getName).toList(),
                HedDomain.contents(loaded).stream().filter(NamedElement.class::isInstance).map(NamedElement.class::cast).map(NamedElement::getName).toList());
        assertTrue(new HedValidation().validate(loaded).isEmpty());
    }
}
