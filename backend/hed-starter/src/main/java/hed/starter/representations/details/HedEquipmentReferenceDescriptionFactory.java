/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.details;

import static org.eclipse.sirius.components.widget.reference.ReferenceWidgetComponent.ITEM_VARIABLE;
import static org.eclipse.sirius.components.widget.reference.ReferenceWidgetComponent.MOVE_FROM_VARIABLE;
import static org.eclipse.sirius.components.widget.reference.ReferenceWidgetComponent.MOVE_TO_VARIABLE;
import static org.eclipse.sirius.components.widget.reference.ReferenceWidgetComponent.NEW_VALUE;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.core.api.IIdentityService;
import org.eclipse.sirius.components.emf.services.api.IEMFKindService;
import org.eclipse.sirius.components.representations.IStatus;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.widget.reference.ReferenceWidgetDescription;
import org.eclipse.sirius.components.widget.reference.ReferenceWidgetStyle;
import hed.domain.HedDomain;
import hed.domain.Messages;
import hed.homeelectrical.CircuitBranch;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.HomeElectricalPackage;
import hed.starter.HedDomainIcons;
import hed.starter.representations.CircuitEquipment;

/** Builds native reference editors whose transfer dialog retains each circuit or branch's equipment order. */
final class HedEquipmentReferenceDescriptionFactory {
    private final IIdentityService identity;

    private final IEMFKindService kinds;

    private final Locale locale;

    private final Function<Runnable, IStatus> update;

    /** Shares the properties registry's atomic-edit feedback and the platform's semantic-kind identifiers. */
    HedEquipmentReferenceDescriptionFactory(IIdentityService identity, IEMFKindService kinds, Locale locale, Function<Runnable, IStatus> update) {
        this.identity = identity;
        this.kinds = kinds;
        this.locale = locale;
        this.update = update;
    }

    /** Creates the same native widget contract for a circuit's direct/derived list and a branch's ordered list. */
    ReferenceWidgetDescription create(String type) {
        String widgetId = "hed." + type + ".equipment";
        return ReferenceWidgetDescription.newReferenceWidgetDescription(widgetId).idProvider(vm -> widgetId).targetObjectIdProvider(vm -> identity.getId(owner(vm)))
                .ownerIdProvider(vm -> identity.getId(owner(vm))).labelProvider(vm -> Messages.text(locale, "feature.equipment"))
                .helpTextProvider(vm -> Messages.text(locale, "properties.equipmentOrder")).isReadOnlyProvider(vm -> owner(vm) instanceof FinalCircuit circuit && !circuit.getBranches().isEmpty())
                .itemsProvider(vm -> members(owner(vm))).optionsProvider(vm -> {
                    var home = HedDomain.dwelling(owner(vm));
                    return home == null ? List.of() : List.copyOf(home.getEquipment());
                }).itemIdProvider(vm -> identity.getId(item(vm))).itemLabelProvider(vm -> HedDomain.label(item(vm), locale)).itemKindProvider(vm -> kinds.getKind(item(vm).eClass()))
                .itemIconURLProvider(vm -> List.of(HedDomainIcons.of(item(vm)))).ownerKindProvider(vm -> kinds.getKind(owner(vm).eClass()))
                .referenceKindProvider(vm -> kinds.getKind(HomeElectricalPackage.Literals.FIELD_EQUIPMENT)).isContainmentProvider(vm -> false).isManyProvider(vm -> true)
                .styleProvider(vm -> ReferenceWidgetStyle.newReferenceWidgetStyle().fontSize(14).build()).clearHandlerProvider(vm -> update.apply(() -> replace(owner(vm), List.of())))
                .itemRemoveHandlerProvider(vm -> update.apply(() -> {
                    var members = new ArrayList<>(members(owner(vm)));
                    members.remove(item(vm));
                    replace(owner(vm), members);
                })).addHandlerProvider(vm -> update.apply(() -> {
                    var members = new ArrayList<>(members(owner(vm)));
                    for (Object value : vm.get(NEW_VALUE, List.class).orElseThrow()) {
                        if (!(value instanceof FieldEquipment equipment) || HedDomain.dwelling(equipment) != HedDomain.dwelling(owner(vm))) {
                            throw new IllegalArgumentException("foreignReference");
                        }
                        if (!members.contains(equipment)) {
                            members.add(equipment);
                        }
                    }
                    replace(owner(vm), members);
                })).setHandlerProvider(vm -> update.apply(() -> {
                    var value = vm.get(NEW_VALUE, FieldEquipment.class).orElseThrow(() -> new IllegalArgumentException("foreignReference"));
                    replace(owner(vm), List.of(value));
                })).moveHandlerProvider(vm -> update.apply(() -> {
                    var members = new ArrayList<>(members(owner(vm)));
                    int from = vm.get(MOVE_FROM_VARIABLE, Integer.class).orElse(-1);
                    int to = vm.get(MOVE_TO_VARIABLE, Integer.class).orElse(-1);
                    if (from < 0 || from >= members.size() || to < 0 || to >= members.size() || members.get(from) != item(vm)) {
                        throw new IllegalArgumentException("equipmentOrderChanged");
                    }
                    members.add(to, members.remove(from));
                    replace(owner(vm), members);
                })).diagnosticsProvider(vm -> List.of()).kindProvider(Object::toString).messageProvider(Object::toString).build();
    }

    private EObject owner(VariableManager vm) {
        return vm.get(VariableManager.SELF, EObject.class).orElseThrow();
    }

    private EObject item(VariableManager vm) {
        return vm.get(ITEM_VARIABLE, EObject.class).orElseThrow();
    }

    private List<FieldEquipment> members(EObject owner) {
        return owner instanceof FinalCircuit circuit ? CircuitEquipment.getAssociatedEquipment(circuit) : List.copyOf(((CircuitBranch) owner).getEquipment());
    }

    private void replace(EObject owner, List<FieldEquipment> values) {
        if (owner instanceof FinalCircuit circuit) {
            CircuitEquipment.setAssociatedEquipment(circuit, values);
        } else {
            HedDomain.set(owner, "equipment", values);
        }
    }
}
