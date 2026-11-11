/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IFeedbackMessageService;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.diagrams.Node;
import org.eclipse.sirius.components.representations.Message;
import org.eclipse.sirius.components.representations.MessageLevel;
import org.springframework.stereotype.Service;
import hed.domain.HedDomain;
import hed.domain.Messages;
import hed.homeelectrical.DINRail;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.ModularDevice;
import hed.homeelectrical.Room;
import hed.homeelectrical.Storey;

/** Creates devices from catalog types on DIN rails and in rooms; panel-diagram catalog drops target rails. */
@Service
public class CatalogDropService {
    private final IObjectSearchService search;

    private final IFeedbackMessageService feedback;

    private final Locale locale;

    /**
     * Creates the shared catalog-drop service.
     *
     * @param search resolves diagram targets in their editing context
     * @param feedback reports rejected diagram drops to the user
     * @param locale selects feedback and default-name translations
     */
    public CatalogDropService(IObjectSearchService search, IFeedbackMessageService feedback, Locale locale) {
        this.search = search;
        this.feedback = feedback;
        this.locale = locale;
    }

    /**
     * Appends one new modular device per selected type, preserving selection order and catalog ownership.
     * The entire selection and its combined module width are checked before creating any modular device.
     *
     * @param target the destination DIN rail
     * @param sources modular device types from the same dwelling's catalog
     * @return the new modular devices, in their rail order
     * @throws IllegalArgumentException if the target, selection, dwelling or available rail capacity is invalid
     */
    public List<ModularDevice> create(Object target, List<?> sources) {
        if (!(target instanceof DINRail rail) || !(rail.eContainer() instanceof DistributionBoard) || HedDomain.dwelling(rail) == null) {
            throw new IllegalArgumentException("drop.catalogRail");
        }
        if (sources.isEmpty() || sources.stream().anyMatch(source -> !(source instanceof DeviceType type) || !HedDomain.modular(type.getKind()))) {
            throw new IllegalArgumentException("drop.modularType");
        }
        var types = sources.stream().map(DeviceType.class::cast).toList();
        if (types.stream().anyMatch(type -> HedDomain.dwelling(type) != HedDomain.dwelling(rail))) {
            throw new IllegalArgumentException("foreignReference");
        }
        // Validate the entire explorer selection before creating any of its modular devices.
        HedDomain.requireRailCapacity(rail, types);
        return types.stream().map(type -> HedDomain.addModularDevice(rail, type, locale)).toList();
    }

    /**
     * Creates one terminal device per selected catalog type and assigns it to the destination room.
     * The dwelling retains semantic ownership of the devices; the source types remain in its catalog.
     *
     * @param room a room contained in a storey of the destination dwelling
     * @param sources terminal equipment types from that dwelling's catalog, in creation order
     * @return the newly created devices, initialized from their types in the application locale
     * @throws IllegalArgumentException if the room, selection, device category or source dwelling is invalid
     */
    public List<FieldEquipment> createInRoom(Room room, List<?> sources) {
        if (room == null || !(room.eContainer() instanceof Storey) || HedDomain.dwelling(room) == null) {
            throw new IllegalArgumentException("drop.catalogTarget");
        }
        if (sources.isEmpty() || sources.stream().anyMatch(source -> !(source instanceof DeviceType type) || !isTerminalType(type))) {
            throw new IllegalArgumentException("drop.terminalType");
        }
        var home = HedDomain.dwelling(room);
        var types = sources.stream().map(DeviceType.class::cast).toList();
        if (types.stream().anyMatch(type -> HedDomain.dwelling(type) != home)) {
            throw new IllegalArgumentException("foreignReference");
        }
        // Validate the whole batch first: a mixed or stale selection must not create partial results.
        return types.stream().map(type -> {
            var equipment = HedDomain.addEquipment(home, type, locale);
            equipment.setRoom(room);
            return equipment;
        }).toList();
    }

    private boolean isTerminalType(DeviceType type) {
        var kind = type.getKind();
        return !HedDomain.modular(kind) && kind != DeviceKind.ENERGY_METER && kind != DeviceKind.SERVICE_BREAKER;
    }

    /**
     * Creates a modular device for Sirius Web's native per-object diagram drop operation.
     * A drop onto a mounted modular device appends to its rail without changing that modular device.
     * Rejected drops produce localized feedback and leave the source type in the catalog.
     *
     * @param source the catalog type being dropped
     * @param selectedNode a rail or mounted modular device node, or {@code null} for the diagram background
     * @param context the editing context used to resolve the node's semantic target
     * @return the unchanged semantic source, keeping the View DSL operation's context
     */
    public EObject dropHedCatalogType(Object source, Node selectedNode, IEditingContext context) {
        var target = selectedNode == null ? null : search.getObject(context, selectedNode.getTargetObjectId()).orElse(null);
        if (target instanceof ModularDevice device) {
            // Modular devices cover their rail visually; both hit targets must create on the same physical rail.
            target = device.getRail();
        }
        try {
            create(target, Collections.singletonList(source));
        } catch (IllegalArgumentException ex) {
            feedback.addFeedbackMessage(new Message(Messages.text(locale, ex.getMessage()), MessageLevel.ERROR));
        }
        return source instanceof EObject object ? object : null;
    }
}
