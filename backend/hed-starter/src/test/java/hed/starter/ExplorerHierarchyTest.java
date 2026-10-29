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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.junit.jupiter.api.Test;
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.Dwelling;
import hed.starter.project.ProjectNames;
import hed.starter.representations.modelexplorer.HedJavaServices;
import hed.starter.representations.modelexplorer.HedJavaServices.Group;
import hed.starter.representations.modelexplorer.HedJavaServices.ProjectRoot;

class ExplorerHierarchyTest {
    private final BootstrapIntegrationTest.Identity identity = new BootstrapIntegrationTest.Identity();

    private final IObjectSearchService search = new IObjectSearchService() {
        @Override
        public Optional<Object> getObject(IEditingContext context, String id) {
            return identity.ids.entrySet().stream().filter(entry -> entry.getValue().equals(id)).map(Map.Entry::getKey).findFirst();
        }
    };

    private HedJavaServices services(Locale locale) {
        return new HedJavaServices(identity, search, new BootstrapIntegrationTest.Feedback(), locale, null, home -> "Native project name");
    }

    @Test
    void projectAndGroupsHaveStableResolvableIdentitiesAndLocalizedHousingLabels() {
        var home = HedSamples.example();
        var service = services(Locale.ENGLISH);
        var root = new ProjectRoot(home);
        var gtl = new Group(home, "gtl");
        assertEquals(List.of(home, gtl, home.getCatalog()), service.getHedChildren(root));
        assertEquals("Native project name", service.getHedExplorerLabel(root));
        assertEquals("hed-project", service.getHedKind(root));
        assertEquals(root, service.getHedObject(service.getHedId(root), null));
        assertEquals(gtl, service.getHedObject(service.getHedId(gtl), null));
        assertEquals(root, service.getHedParent(home));
        assertEquals(root, service.getHedParent(gtl));
        assertNull(service.getHedParent(root));
        assertFalse(service.isHedSelectable(root));
        assertFalse(service.isHedEditable(root));
        assertFalse(service.isHedDeletable(root));
        assertTrue(service.isHedSelectable(home));
        assertFalse(service.isHedEditable(home));
        assertEquals("Dwelling", service.getHedExplorerLabel(home));
        assertEquals("Logement", services(Locale.FRENCH).getHedExplorerLabel(home));
        assertEquals(home.getName(), service.getHedLabel(home));
        assertEquals(home.getName(), new ProjectNames(null, null).getName(home));
        assertSame(home.getStoreys().get(0), service.getHedChildren(home).get(0));
        var gtlChildren = service.getHedChildren(gtl);
        assertSame(home.getServiceConnection(), gtlChildren.get(0));
        assertEquals(home.getBoards(), gtlChildren.subList(1, gtlChildren.size() - 1));
        assertEquals(new Group(home, "circuits"), gtlChildren.getLast());
    }

    @Test
    void catalogClassificationFollowsTheCurrentKindWithoutChangingTypeIdentityOrNames() {
        var home = HedSamples.example();
        var service = services(Locale.FRENCH);
        var catalog = home.getCatalog();
        var panel = new Group(catalog, "panelTypes");
        var terminal = new Group(catalog, "terminalTypes");
        assertEquals(List.of(panel, terminal), service.getHedChildren(catalog));
        assertTrue(service.getHedChildren(panel).contains(HedDomain.type(home, DeviceKind.ENERGY_METER)));
        assertTrue(service.getHedChildren(panel).contains(HedDomain.type(home, DeviceKind.SERVICE_BREAKER)));
        assertTrue(service.getHedChildren(terminal).contains(HedDomain.type(home, DeviceKind.OUTLET)));
        assertEquals(catalog.getDeviceTypes().size(), service.getHedChildren(panel).size() + service.getHedChildren(terminal).size());
        var type = (DeviceType) service.createHedObject(panel, "DeviceType");
        assertEquals(DeviceKind.MCB, type.getKind());
        var id = service.getHedId(type);
        type.setName("Custom catalog name");
        type.setKind(DeviceKind.OUTLET);
        assertEquals(terminal, service.getHedParent(type));
        assertFalse(service.getHedChildren(panel).contains(type));
        assertTrue(service.getHedChildren(terminal).contains(type));
        assertEquals(id, service.getHedId(type));
        assertSame(catalog, type.eContainer());
        assertEquals("Custom catalog name", service.getHedExplorerLabel(type));
        assertEquals(DeviceKind.OUTLET, ((DeviceType) service.createHedObject(terminal, "DeviceType")).getKind());
    }

    @Test
    void creationMenusFollowTheHousingServiceAreaCatalogAndRailContexts() {
        Dwelling home = HedSamples.empty("House");
        var service = services(Locale.ENGLISH);
        var root = new ProjectRoot(home);
        var gtl = new Group(home, "gtl");
        assertFalse(service.canCreateHedObject(root, "Storey"));
        assertFalse(service.canCreateHedObject(home, "DistributionBoard"));
        assertFalse(service.canCreateHedObject(home, "ServiceConnection"));
        assertTrue(service.canCreateHedObject(home, "Storey"));
        assertTrue(service.canCreateHedObject(gtl, "ServiceConnection"));
        var connection = service.createHedObject(gtl, "ServiceConnection");
        assertSame(home.getServiceConnection(), connection);
        assertFalse(service.canCreateHedObject(gtl, "ServiceConnection"));
        assertSame(gtl, service.createHedObject(gtl, "ServiceConnection"));
        assertSame(connection, home.getServiceConnection());
        assertTrue(service.canCreateHedObject(gtl, "DistributionBoard"));
        var board = service.createHedObject(gtl, "DistributionBoard");
        assertEquals(gtl, service.getHedParent(board));
        assertTrue(service.canCreateHedObject(board, "DINRail"));
        assertFalse(service.canCreateHedObject(board, "MCB"));
        assertFalse(service.canCreateHedObject(gtl, "MCB"));
        assertTrue(service.canCreateHedObject(service.createHedObject(board, "DINRail"), "MCB"));
        assertTrue(service.canCreateHedObject(new Group(home, "circuits"), "FinalCircuit"));
        assertTrue(service.canCreateHedObject(new Group(home, "controlFunctions"), "ControlFunction"));
        assertFalse(service.canCreateHedObject(new Group(home, "unlocated"), "MCB"));
        assertTrue(service.canCreateHedObject(new Group(home, "unlocated"), "OUTLET"));
    }
}
