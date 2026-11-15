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
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.core.api.labels.StyledString;
import org.eclipse.sirius.components.representations.Failure;
import org.eclipse.sirius.components.representations.Success;
import org.eclipse.sirius.components.trees.TreeItem;
import hed.domain.HedDomain;
import hed.domain.HedOutletBlocks;
import hed.domain.HedSamples;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.OutletBlock;
import hed.starter.representations.CatalogDropService;
import hed.starter.representations.modelexplorer.RoomCatalogCreationTools;

/** Verifies per-entry discovery, stable variant invocation and rejected stale or invalid tools. */
class RoomCatalogCreationToolsTest {
    @Test
    void allVariantsAppearInCatalogOrderWithoutCategoryOrLabelDeduplication() {
        var home = HedSamples.example();
        var identity = new BootstrapIntegrationTest.Identity();
        var search = new IObjectSearchService.NoOp();
        var drops = new CatalogDropService(search, new BootstrapIntegrationTest.Feedback(), Locale.FRENCH);
        var tools = new RoomCatalogCreationTools(search, identity, drops, Locale.FRENCH);
        for (int index = 0; index < 40; index++) {
            var type = HedDomain.F.createDeviceType();
            type.setKind(DeviceKind.OUTLET_BLOCK);
            type.setName("Identical label");
            home.getCatalog().getDeviceTypes().add(type);
        }
        var room = home.getStoreys().getFirst().getRooms().getFirst();
        var types = home.getCatalog().getDeviceTypes().stream().filter(drops::isTerminalType).toList();
        var entries = tools.entries(room);
        assertEquals(types.size(), entries.size());
        assertEquals(40, entries.stream().filter(entry -> entry.label().equals("Créer Identical label")).count());
        assertEquals(entries.size(), entries.stream().map(entry -> entry.id()).distinct().count());
        var last = entries.getLast();
        home.getCatalog().getDeviceTypes().move(0, types.getLast());
        assertEquals(last.id(), tools.entries(room).getFirst().id());
    }

    @Test
    void retainedToolCreatesItsExactVariantAfterReorderingAndRejectsDeletedTypes() {
        for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            var home = HedSamples.example(locale);
            var identity = new BootstrapIntegrationTest.Identity();
            var search = new IObjectSearchService() {
                @Override
                public Optional<Object> getObject(IEditingContext context, String id) {
                    return HedDomain.contents(home).stream().filter(value -> identity.getId(value).equals(id)).map(value -> (Object) value).findFirst();
                }
            };
            var tools = new RoomCatalogCreationTools(search, identity, new CatalogDropService(search, new BootstrapIntegrationTest.Feedback(), locale), locale);
            var variant = HedDomain.F.createDeviceType();
            variant.setName("Block 2x3");
            variant.setKind(DeviceKind.OUTLET_BLOCK);
            variant.setNbRows(2);
            variant.setNbColumns(3);
            home.getCatalog().getDeviceTypes().add(variant);
            var room = home.getStoreys().getFirst().getRooms().getFirst();
            var item = TreeItem.newTreeItem(identity.getId(room)).kind("room").label(StyledString.of("Room")).iconURL(List.of()).children(List.of()).build();
            var action = tools.entries(room).getLast();
            home.getCatalog().getDeviceTypes().move(0, variant);
            assertInstanceOf(Success.class, tools.execute(null, null, null, item, action.id(), null));
            var created = assertInstanceOf(OutletBlock.class, home.getEquipment().getLast());
            assertSame(variant, created.getDeviceType());
            assertSame(room, created.getRoom());
            assertEquals(6, HedOutletBlocks.outlets(created).size());
            assertEquals(2, created.getRows().size());
            HedDomain.delete(created);
            home.getCatalog().getDeviceTypes().remove(variant);
            var count = home.getEquipment().size();
            assertInstanceOf(Failure.class, tools.execute(null, null, null, item, action.id(), null));
            assertEquals(count, home.getEquipment().size());
            assertTrue(tools.entries(room).stream().noneMatch(entry -> entry.id().equals(action.id())));
        }
    }
}
