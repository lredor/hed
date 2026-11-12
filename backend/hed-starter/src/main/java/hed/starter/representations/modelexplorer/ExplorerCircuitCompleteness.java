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

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.components.representations.IRepresentationDescription;
import org.eclipse.sirius.components.representations.IRepresentationRenderVariableCustomizer;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.trees.description.TreeDescription;
import org.eclipse.sirius.components.trees.renderer.TreeRenderer;
import org.springframework.stereotype.Service;
import hed.domain.HedDomain;
import hed.domain.Messages;
import hed.homeelectrical.Circuit;
import hed.homeelectrical.ControlDevice;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.ModularDevice;
import hed.homeelectrical.Storey;

/** Computes an immutable, render-local set of incomplete circuit elements and their visible explorer parents. */
@Service
public class ExplorerCircuitCompleteness implements IRepresentationRenderVariableCustomizer {
    public static final String ENABLED_VARIABLE = "hedHighlightIncompleteCircuits";

    public static final String ELEMENTS_VARIABLE = "hedIncompleteCircuitElements";

    private final Locale locale;

    /**
     * Creates a semantic-only highlight contribution; it does not load representations or retain user preferences.
     * @param locale identifies the localized HED explorer description
     */
    public ExplorerCircuitCompleteness(Locale locale) {
        this.locale = locale;
    }

    @Override
    public VariableManager customize(IRepresentationDescription description, VariableManager variables) {
        if (!(description instanceof TreeDescription tree) || !Messages.text(this.locale, "explorer.title").equals(tree.getLabel())) {
            return variables;
        }
        boolean enabled = variables.get(TreeRenderer.ACTIVE_FILTER_IDS, List.class).map(filters -> filters.contains(CircuitCompletenessFilterProvider.FILTER_ID)).orElse(false);
        var child = variables.createChild();
        child.put(ENABLED_VARIABLE, enabled);
        var elements = new HashSet<Object>();
        if (enabled && variables.get(IEditingContext.EDITING_CONTEXT, IEditingContext.class).orElse(null) instanceof IEMFEditingContext context) {
            context.getDomain().getResourceSet().getResources().stream().flatMap(resource -> resource.getContents().stream()).filter(Dwelling.class::isInstance).map(Dwelling.class::cast)
                    .forEach(home -> elements.addAll(incompleteElements(home)));
        }
        child.put(ELEMENTS_VARIABLE, Set.copyOf(elements));
        return child;
    }

    /**
     * Builds the highlight set for one dwelling, including indirect protection and actuator usage.
     * @param home the dwelling being inspected
     * @return immutable semantic elements and virtual parent groups to display in red
     */
    public Set<Object> incompleteElements(Dwelling home) {
        var result = new HashSet<Object>();
        var used = new HashSet<ModularDevice>();
        for (var circuit : home.getCircuits()) {
            if (circuit.getProtectiveDevice() == null) {
                markCircuit(circuit, home, result);
            } else {
                markUsed(circuit.getProtectiveDevice(), home, used);
            }
        }
        for (var control : home.getControlFunctions()) {
            boolean connected = control.getTargets().stream().anyMatch(target -> target.getCircuit() != null)
                    || control.getChannels().stream().anyMatch(channel -> channel.eContainer() instanceof ControlDevice device && device.getCircuit() != null);
            if (connected) {
                markUsed(control.getActuator(), home, used);
            }
        }
        for (var equipment : home.getEquipment()) {
            if (equipment.getCircuit() == null) {
                markEquipment(equipment, home, result);
            }
        }
        for (var board : home.getBoards()) {
            for (var device : board.getDevices()) {
                if (!used.contains(device)) {
                    result.add(device);
                    if (device.getRail() != null) {
                        result.add(device.getRail());
                    }
                    result.add(board);
                    result.add(new HedJavaServices.Group(home, "gtl"));
                }
            }
        }
        return Set.copyOf(result);
    }

    /**
     * Tests a tree occurrence against this render's snapshot, retaining the same highlight on branch aliases.
     * @param source the semantic element or virtual explorer occurrence
     * @param elements immutable incomplete elements for this render
     * @param enabled whether this explorer has selected the completeness filter
     * @return whether this occurrence's label must be red
     */
    public boolean isHedCircuitIncomplete(Object source, Set<Object> elements, boolean enabled) {
        return enabled && source != null && elements.contains(source instanceof BranchEquipmentLinks.EquipmentLink link ? link.equipment() : source);
    }

    private void markUsed(ModularDevice device, Dwelling home, Set<ModularDevice> used) {
        // Each module has at most one upstream RCCB; visited nodes also stop malformed cyclic chains.
        while (device != null && HedDomain.dwelling(device) == home && used.add(device)) {
            device = device.getUpstreamRccb();
        }
    }

    private void markCircuit(Circuit circuit, Dwelling home, Set<Object> result) {
        result.add(circuit);
        result.add(new HedJavaServices.Group(home, "circuits"));
        result.add(new HedJavaServices.Group(home, "gtl"));
    }

    private void markEquipment(FieldEquipment equipment, Dwelling home, Set<Object> result) {
        result.add(equipment);
        result.add(home);
        if (equipment.getRoom() != null) {
            result.add(equipment.getRoom());
            if (equipment.getRoom().eContainer() instanceof Storey storey) {
                result.add(storey);
            }
        } else {
            result.add(new HedJavaServices.Group(home, "unlocated"));
        }
        if (equipment.getBranch() != null) {
            result.add(equipment.getBranch());
            if (equipment.getBranch().getCircuit() != null) {
                markCircuit(equipment.getBranch().getCircuit(), home, result);
            }
        }
    }
}
