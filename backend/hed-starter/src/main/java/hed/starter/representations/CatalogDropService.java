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
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.ModularDevice;

/** Shared creation semantics for catalog drops in Model Explorer and the panel diagram. */
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
