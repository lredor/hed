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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.eclipse.sirius.components.collaborative.trees.dto.DropTreeItemInput;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.representations.Success;
import org.eclipse.sirius.components.representations.Failure;
import org.junit.jupiter.api.Test;
import hed.domain.HedDomain;
import hed.domain.HedSamples;
import hed.homeelectrical.DeviceKind;
import hed.starter.representations.CatalogDropService;
import hed.starter.representations.modelexplorer.CatalogTreeDropHandler;
import hed.starter.representations.modelexplorer.HedJavaServices;

/** Verifies peer gap drops and parent appends for semantic lists and virtual explorer subgroups. */
class ExplorerListsDropTest {
    @Test
    void eachSupportedGroupAcceptsPeerDropsParentAppendsAndVerticalMoveActions() {
        for (var locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            var home = HedSamples.example(locale);
            var identity = new BootstrapIntegrationTest.Identity();
            var objects = new HashMap<String, Object>();
            HedDomain.contents(home).forEach(object -> objects.put(identity.getId(object), object));
            objects.put(identity.getId(home), home);
            IObjectSearchService search = (context, id) -> Optional.ofNullable(objects.get(id));
            var feedback = new BootstrapIntegrationTest.Feedback();
            var explorer = new HedJavaServices(identity, search, feedback, locale, dwelling -> dwelling.getName());
            var handler = new CatalogTreeDropHandler(search, null, new CatalogDropService(search, feedback, locale), explorer, locale);
            for (var first : List.of(home.getStoreys().getFirst(), home.getStoreys().getFirst().getRooms().getFirst(), home.getEquipment().getFirst(), home.getControlFunctions().getFirst(),
                    HedDomain.type(home, DeviceKind.OUTLET), HedDomain.type(home, DeviceKind.MCB))) {
                var peers = HedDomain.orderingPeers(first);
                var last = peers.getLast();
                var lastId = identity.getId(last);
                assertInstanceOf(Success.class, handler.handle(null, null, new DropTreeItemInput(UUID.randomUUID(), "context", "tree", List.of(lastId), identity.getId(first), 0)));
                assertSame(last, HedDomain.orderingPeers(first).getFirst());
                var parent = explorer.getHedParent(first);
                assertInstanceOf(Success.class, handler.handle(null, null, new DropTreeItemInput(UUID.randomUUID(), "context", "tree", List.of(lastId), explorer.getHedId(parent), -1)));
                assertSame(last, HedDomain.orderingPeers(first).getLast());
                assertSame(first, explorer.reorderHedObject(first, 1));
                assertSame(first, HedDomain.orderingPeers(first).get(1));
                assertEquals(lastId, identity.getId(last));
                assertEquals(HedDomain.orderingPeers(first).size(), HedDomain.orderingPeers(first).stream().distinct().count());
            }
            var before = List.copyOf(home.getEquipment());
            assertInstanceOf(Failure.class, handler.handle(null, null,
                    new DropTreeItemInput(UUID.randomUUID(), "context", "tree", List.of(identity.getId(home.getEquipment().getFirst()), "missing"), identity.getId(home.getEquipment().get(1)), 0)));
            assertEquals(before, home.getEquipment());
        }
    }
}
