/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.navigation;

import java.util.List;
import java.util.Map;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.FieldCoordinates;
import org.eclipse.sirius.components.core.api.IEditingContextSearchService;
import org.eclipse.sirius.components.core.api.IIdentityService;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.graphql.api.IDataFetcherWithFieldCoordinates;
import org.springframework.stereotype.Component;
import hed.homeelectrical.Equipment;

/** Resolves an installed device's current catalog type within the requested editing context, including branch occurrences. */
@Component
public class EditingContextHedDeviceTypeDataFetcher implements IDataFetcherWithFieldCoordinates<String> {
    private final IEditingContextSearchService contexts;

    private final IObjectSearchService objects;

    private final IIdentityService identity;

    /**
     * Creates the read-only navigation query.
     * @param contexts resolves the editing context already authorized by the platform
     * @param objects resolves semantic devices and virtual branch occurrences in that context
     * @param identity supplies the selected type's semantic identifier
     */
    public EditingContextHedDeviceTypeDataFetcher(IEditingContextSearchService contexts, IObjectSearchService objects, IIdentityService identity) {
        this.contexts = contexts;
        this.objects = objects;
        this.identity = identity;
    }

    @Override
    public List<FieldCoordinates> getFieldCoordinates() {
        return List.of(FieldCoordinates.coordinates("EditingContext", "hedDeviceTypeId"));
    }

    @Override
    public String get(DataFetchingEnvironment environment) {
        Map<?, ?> localContext = environment.getLocalContext();
        if (localContext == null || !(localContext.get("editingContextId") instanceof String contextId)) {
            return null;
        }
        String objectId = environment.getArgument("objectId");
        return contexts.findById(contextId).flatMap(context -> objects.getObject(context, objectId)).filter(Equipment.class::isInstance).map(Equipment.class::cast).map(Equipment::getDeviceType)
                .map(identity::getId).orElse(null);
    }
}
