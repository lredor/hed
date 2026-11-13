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
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.Map;
import java.util.Optional;
import graphql.schema.DataFetchingEnvironmentImpl;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IEditingContextSearchService;
import org.junit.jupiter.api.Test;
import hed.domain.HedSamples;
import hed.starter.representations.navigation.EditingContextHedDeviceTypeDataFetcher;

/** Checks project-scoped, live type lookup without modifying catalog or instance data. */
class DeviceTypeNavigationTest {
    @Test
    void typeLookupUsesTheRequestedContextAndReflectsChangedAndMissingTypes() {
        var home = HedSamples.example();
        var device = home.getEquipment().getFirst();
        var identity = new BootstrapIntegrationTest.Identity();
        IEditingContext context = () -> "context";
        IEditingContextSearchService contexts = new IEditingContextSearchService() {
            @Override
            public boolean existsById(String id) {
                return id.equals(context.getId());
            }

            @Override
            public Optional<IEditingContext> findById(String id) {
                return existsById(id) ? Optional.of(context) : Optional.empty();
            }
        };
        var fetcher = new EditingContextHedDeviceTypeDataFetcher(contexts, (requested, id) -> requested == context ? switch (id) {
            case "device", "branch-occurrence" -> Optional.of(device);
            case "room" -> Optional.of(device.getRoom());
            default -> Optional.empty();
        } : Optional.empty(), identity);
        for (var id : java.util.List.of("device", "branch-occurrence")) {
            var environment = DataFetchingEnvironmentImpl.newDataFetchingEnvironment().localContext(Map.of("editingContextId", "context")).arguments(Map.of("objectId", id)).build();
            assertEquals(identity.getId(device.getDeviceType()), fetcher.get(environment));
            device.setDeviceType(home.getEquipment().get(1).getDeviceType());
            assertEquals(identity.getId(device.getDeviceType()), fetcher.get(environment));
        }
        for (var id : java.util.List.of("room", "deleted-device")) {
            assertNull(fetcher.get(DataFetchingEnvironmentImpl.newDataFetchingEnvironment().localContext(Map.of("editingContextId", "context")).arguments(Map.of("objectId", id)).build()));
        }
        device.setDeviceType(null);
        assertNull(fetcher.get(DataFetchingEnvironmentImpl.newDataFetchingEnvironment().localContext(Map.of("editingContextId", "context")).arguments(Map.of("objectId", "device")).build()));
        assertNull(fetcher.get(DataFetchingEnvironmentImpl.newDataFetchingEnvironment().localContext(Map.of("editingContextId", "another-context")).arguments(Map.of("objectId", "device")).build()));
        assertNull(fetcher.get(DataFetchingEnvironmentImpl.newDataFetchingEnvironment().arguments(Map.of("objectId", "device")).build()));
    }
}
