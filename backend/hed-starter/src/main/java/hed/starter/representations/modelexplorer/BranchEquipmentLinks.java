/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.modelexplorer;

import java.util.Optional;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IIdentityService;
import org.eclipse.sirius.components.core.api.IObjectSearchServiceDelegate;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.CircuitBranch;
import hed.homeelectrical.Dwelling;

/** Distinct tree occurrences, resolving to the original equipment for selection and editing. */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BranchEquipmentLinks implements IObjectSearchServiceDelegate {
    /** A second tree occurrence of an existing device, without adding a semantic object or changing its room. */
    public record EquipmentLink(FieldEquipment equipment) {
        /** @return the device's current branch, or null after its assignment has been removed */
        public CircuitBranch branch() {
            return equipment.getBranch();
        }
    }

    private final IIdentityService identity;

    /**
     * Creates the resolver for branch occurrences used by native semantic selection and editing.
     * @param identity supplies stable identifiers for the underlying devices
     */
    public BranchEquipmentLinks(IIdentityService identity) {
        this.identity = identity;
    }

    // Native tree renaming requires UUID item identifiers, including virtual occurrences.
    /**
     * Returns a deterministic native UUID distinct from the device's room occurrence.
     * @param link the explorer occurrence under a circuit branch
     * @param identity resolves the original device's identifier
     * @return a version-three UUID stable across renders, reopening and branch reassignment
     */
    public static String id(EquipmentLink link, IIdentityService identity) {
        return UUID.nameUUIDFromBytes(("hed-branch-equipment:" + identity.getId(link.equipment())).getBytes(StandardCharsets.UTF_8)).toString();
    }

    /**
     * Resolves an occurrence within the current editing context without accepting foreign-context devices.
     * A stale occurrence can still resolve its device after branch unassignment, until the tree refreshes.
     * @param context the current semantic editing context
     * @param id the prospective virtual occurrence identifier
     * @param identity supplies semantic device identifiers
     * @return the matching occurrence, or empty for an unrelated identifier or deleted device
     */
    public static Optional<EquipmentLink> find(IEditingContext context, String id, IIdentityService identity) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            if (UUID.fromString(id).version() != 3) {
                return Optional.empty();
            }
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
        if (context instanceof IEMFEditingContext emf) {
            for (var resource : emf.getDomain().getResourceSet().getResources()) {
                for (var root : resource.getContents()) {
                    if (root instanceof Dwelling home) {
                        for (var equipment : home.getEquipment()) {
                            var link = new EquipmentLink(equipment);
                            if (id(link, identity).equals(id)) {
                                return Optional.of(link);
                            }
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean canHandle(IEditingContext context, String id) {
        return find(context, id, identity).isPresent();
    }

    @Override
    public Optional<Object> getObject(IEditingContext context, String id) {
        return find(context, id, identity).map(EquipmentLink::equipment).map(item -> (Object) item);
    }

    /**
     * Unwraps explorer occurrences for semantic operations and shared presentation services.
     * @param object an explorer occurrence or an ordinary object
     * @return the original device for an occurrence; otherwise the unchanged object
     */
    public static Object semantic(Object object) {
        return object instanceof EquipmentLink link ? link.equipment() : object;
    }
}
