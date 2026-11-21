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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.core.api.labels.StyledString;
import org.eclipse.sirius.components.palette.dto.Palette;
import org.eclipse.sirius.components.representations.Failure;
import org.eclipse.sirius.components.representations.Success;
import org.eclipse.sirius.components.trees.TreeItem;
import org.eclipse.sirius.components.trees.description.TreeDescription;
import org.junit.jupiter.api.Test;
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.domain.Messages;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.Dwelling;
import hed.starter.representations.CatalogDropService;
import hed.starter.representations.modelexplorer.DINRailCatalogCreationTools;

/** Verifies ordered rail tools, exact variant creation and validation of retained actions. */
class DINRailCatalogCreationToolsTest {
    private final BootstrapIntegrationTest.Identity identity = new BootstrapIntegrationTest.Identity();

    private DINRailCatalogCreationTools tools(Dwelling home, Locale locale) {
        var search = new IObjectSearchService() {
            @Override
            public Optional<Object> getObject(IEditingContext context, String id) {
                return HedDomain.contents(home).stream().filter(value -> identity.getId(value).equals(id)).map(value -> (Object) value).findFirst();
            }
        };
        return new DINRailCatalogCreationTools(search, identity, new CatalogDropService(search, new BootstrapIntegrationTest.Feedback(), locale), locale);
    }

    private DeviceType variant(Dwelling home, double width) {
        var type = HedDomain.F.createDeviceType();
        type.setKind(DeviceKind.MCB);
        type.setName("Compact variant");
        type.setWidthModules(width);
        home.getCatalog().getDeviceTypes().add(type);
        return type;
    }

    private TreeItem item(Object object) {
        return TreeItem.newTreeItem(identity.getId(object)).kind("hed").label(StyledString.of("Selection")).iconURL(List.of()).children(List.of()).build();
    }

    private TreeDescription description(Locale locale) {
        return TreeDescription.newTreeDescription("hed-test").label(Messages.text(locale, "explorer.title")).idProvider(vm -> "explorer://test").treeItemIdProvider(vm -> "item")
                .kindProvider(vm -> "hed").labelProvider(vm -> StyledString.of("Explorer")).targetObjectIdProvider(vm -> "root").parentObjectProvider(vm -> null)
                .treeItemIconURLsProvider(vm -> List.of()).editableProvider(vm -> false).deletableProvider(vm -> false).selectableProvider(vm -> true).elementsProvider(vm -> List.of())
                .childrenProvider(vm -> List.of()).hasChildrenProvider(vm -> false).canCreatePredicate(vm -> false).deleteHandler(vm -> null).renameHandler((vm, name) -> null)
                .treeItemObjectProvider(vm -> null).treeItemLabelProvider(vm -> StyledString.of("Item")).iconURLsProvider(vm -> List.of()).build();
    }

    @Test
    void everyCompatibleVariantAppearsInCatalogOrderWithoutDeduplication() {
        for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            var home = HedSamples.example(locale);
            var rail = home.getBoards().getFirst().getRails().getFirst();
            rail.setCapacityModules(100);
            var tools = tools(home, locale);
            for (int index = 0; index < 40; index++) {
                variant(home, 0.5);
            }
            var types = home.getCatalog().getDeviceTypes().stream().filter(type -> HedDomain.canAddToRail(rail, type)).toList();
            var entries = tools.entries(rail);
            assertEquals(types.stream().map(type -> "hed-rail-create-type:" + identity.getId(type)).toList(), entries.stream().map(entry -> entry.id()).toList());
            assertEquals(40, entries.stream().filter(entry -> entry.label().equals(Messages.text(locale, "action.create") + " Compact variant")).count());
            assertEquals(entries.size(), entries.stream().map(entry -> entry.id()).distinct().count());
            var retained = entries.getLast();
            home.getCatalog().getDeviceTypes().move(0, types.getLast());
            types.getLast().setName("Renamed variant");
            assertEquals(retained.id(), tools.entries(rail).getFirst().id());
            assertEquals(Messages.text(locale, "action.create") + " Renamed variant", tools.entries(rail).getFirst().label());
            var description = description(locale);
            var item = item(rail);
            assertTrue(tools.canHandle(null, description, null, item));
            assertFalse(tools.canHandle(null, description, null, item(home.getStoreys().getFirst().getRooms().getFirst())));
            assertTrue(tools.canExecute(null, description, retained.id(), null));
            assertFalse(tools.canExecute(null, description, "hed-room-create-type:any", null));
            var palette = tools.customize(null, description, null, item, new Palette("palette", List.of(), List.of(retained)));
            assertEquals(tools.entries(rail), palette.paletteEntries().subList(0, entries.size()));
            assertSame(retained, palette.paletteEntries().getLast());
        }
    }

    @Test
    void retainedActionCreatesItsExactVariantAfterReorderingAndRenaming() {
        for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            var home = HedSamples.example(locale);
            var rail = home.getBoards().getFirst().getRails().getFirst();
            var type = variant(home, 0.5);
            type.setRatedCurrentA(25d);
            var tools = tools(home, locale);
            var action = tools.entries(rail).getLast();
            home.getCatalog().getDeviceTypes().move(0, type);
            type.setName("Renamed compact breaker");
            int before = rail.getDevices().size();
            assertInstanceOf(Success.class, tools.execute(null, null, null, item(rail), action.id(), null));
            assertEquals(before + 1, rail.getDevices().size());
            var created = rail.getDevices().getLast();
            assertSame(type, created.getDeviceType());
            assertSame(rail, created.getRail());
            assertSame(rail.eContainer(), created.eContainer());
            assertSame(home.getCatalog(), type.eContainer());
            assertEquals(type.getName(), created.getName());
            assertEquals(0.5, HedDomain.moduleWidth(created));
            assertEquals(25d, HedDomain.get(created, "ratedCurrentA"));
        }
    }

    @Test
    void compactVariantsFitIndependentlyAndCapacityIsRecheckedAtExecution() {
        var home = HedSamples.example();
        var rail = home.getBoards().getFirst().getRails().getFirst();
        var type = variant(home, 0.5);
        var tools = tools(home, Locale.ENGLISH);
        rail.setCapacityModules((int) HedDomain.usedModules(rail) + 1);
        new CatalogDropService(new IObjectSearchService.NoOp(), new BootstrapIntegrationTest.Feedback(), Locale.ENGLISH).create(rail, List.of(type));
        assertFalse(HedDomain.canAddToRail(rail, HedDomain.type(home, DeviceKind.MCB)));
        var entries = tools.entries(rail);
        assertEquals(List.of("hed-rail-create-type:" + identity.getId(type)), entries.stream().map(entry -> entry.id()).toList());
        var action = entries.getFirst();
        assertInstanceOf(Success.class, tools.execute(null, null, null, item(rail), action.id(), null));
        var before = List.copyOf(rail.getDevices());
        assertTrue(tools.entries(rail).isEmpty());
        var failure = assertInstanceOf(Failure.class, tools.execute(null, null, null, item(rail), action.id(), null));
        assertEquals(Messages.text(Locale.ENGLISH, "railCapacity"), failure.getMessages().getFirst().body());
        assertEquals(before, rail.getDevices());
    }

    @Test
    void invalidSelectionsAndStaleOrForeignTypesDoNotCreateDevices() {
        var home = HedSamples.example();
        var rail = home.getBoards().getFirst().getRails().getFirst();
        var type = variant(home, 0.5);
        var tools = tools(home, Locale.FRENCH);
        var action = tools.entries(rail).getLast();
        var before = List.copyOf(rail.getDevices());
        var room = home.getStoreys().getFirst().getRooms().getFirst();
        assertInstanceOf(Failure.class, tools.execute(null, null, null, item(room), action.id(), null));
        assertInstanceOf(Failure.class, tools.execute(null, null, null, item(rail), "hed-room-create-type:any", null));
        type.setKind(DeviceKind.OUTLET);
        assertTrue(tools.entries(rail).stream().noneMatch(entry -> entry.id().equals(action.id())));
        assertInstanceOf(Failure.class, tools.execute(null, null, null, item(rail), action.id(), null));
        type.setKind(DeviceKind.MCB);
        HedSamples.example().getCatalog().getDeviceTypes().add(type);
        assertInstanceOf(Failure.class, tools.execute(null, null, null, item(rail), action.id(), null));
        home.getCatalog().getDeviceTypes().add(type);
        home.getCatalog().getDeviceTypes().remove(type);
        assertInstanceOf(Failure.class, tools.execute(null, null, null, item(rail), action.id(), null));
        assertEquals(before, rail.getDevices());
        assertTrue(tools.entries(HedDomain.F.createDINRail()).isEmpty());
    }
}
