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

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.sirius.components.collaborative.api.ChangeKind;
import org.eclipse.sirius.components.collaborative.forms.services.api.IPropertiesDescriptionRegistry;
import org.eclipse.sirius.components.collaborative.forms.services.api.IPropertiesDescriptionRegistryConfigurer;
import org.eclipse.sirius.components.core.api.IIdentityService;
import org.eclipse.sirius.components.forms.description.AbstractControlDescription;
import org.eclipse.sirius.components.forms.description.GroupDescription;
import org.eclipse.sirius.components.forms.description.IfDescription;
import org.eclipse.sirius.components.forms.description.MultiSelectDescription;
import org.eclipse.sirius.components.forms.description.PageDescription;
import org.eclipse.sirius.components.forms.description.SelectDescription;
import org.eclipse.sirius.components.forms.description.TextfieldDescription;
import org.eclipse.sirius.components.forms.MultiSelectStyle;
import org.eclipse.sirius.components.forms.SelectStyle;
import org.eclipse.sirius.components.representations.Failure;
import org.eclipse.sirius.components.representations.IStatus;
import org.eclipse.sirius.components.representations.Success;
import org.eclipse.sirius.components.representations.VariableManager;
import org.springframework.stereotype.Service;
import hed.domain.HedDomain;
import hed.domain.Messages;
import hed.domain.HedValidation;
import hed.homeelectrical.Circuit;
import hed.homeelectrical.ControlDevice;
import hed.homeelectrical.ControlFunction;
import hed.homeelectrical.DINRail;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.DistributionCircuit;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.ElectricalLoad;
import hed.homeelectrical.Equipment;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.ModularDevice;
import hed.homeelectrical.ServiceCircuitBreaker;
import hed.starter.HedDomainIcons;

/** Builds localized properties pages with controls filtered by device kind and supply configuration. */
@Service
public class HedPropertiesDescriptionRegistryConfigurer implements IPropertiesDescriptionRegistryConfigurer {
    private final IIdentityService identity;

    private final Locale locale;

    /**
     * Creates the properties description contribution.
     *
     * @param identity supplies stable identifiers for semantic targets
     * @param locale selects property labels and validation messages
     */
    public HedPropertiesDescriptionRegistryConfigurer(IIdentityService identity, Locale locale) {
        this.identity = identity;
        this.locale = locale;
    }

    @Override
    public void addPropertiesDescriptions(IPropertiesDescriptionRegistry registry) {
        for (var classifier : HomeElectricalPackage.eINSTANCE.getEClassifiers()) {
            if (classifier instanceof EClass type && !type.isAbstract()) {
                var groups = new LinkedHashMap<String, List<AbstractControlDescription>>();
                for (var feature : type.getEAllStructuralFeatures()) {
                    if (type == HomeElectricalPackage.Literals.MODULAR_DEVICE && feature.getName().equals("description")) {
                        continue;
                    }
                    if (feature instanceof EReference reference && (reference.isContainment() || reference.isContainer() && type != HomeElectricalPackage.Literals.CIRCUIT_BRANCH)) {
                        continue;
                    }
                    var widget = control(type, feature);
                    groups.computeIfAbsent(group(feature), k -> new ArrayList<>()).add(IfDescription.newIfDescription("hed.visible." + type.getName() + "." + feature.getName())
                            .targetObjectIdProvider(vm -> identity.getId(self(vm))).predicate(vm -> applicable(self(vm), feature)).controlDescriptions(List.of(widget)).build());
                }
                if (type == HomeElectricalPackage.Literals.MODULAR_DEVICE) {
                    groups.computeIfAbsent("connections", k -> new ArrayList<>())
                            .add(IfDescription.newIfDescription("hed.visible.ModularDevice.downstreamDevices").targetObjectIdProvider(vm -> identity.getId(self(vm)))
                                    .predicate(vm -> HedDomain.kind((ModularDevice) self(vm)) == DeviceKind.RCCB).controlDescriptions(List.of(downstreamDevices())).build());
                    for (var feature : HomeElectricalPackage.Literals.TECHNICAL_ELEMENT.getEStructuralFeatures()) {
                        var specification = technicalSpecification(feature);
                        groups.computeIfAbsent("technical", k -> new ArrayList<>()).add(IfDescription.newIfDescription("hed.visible.ModularDevice." + feature.getName())
                                .targetObjectIdProvider(vm -> identity.getId(self(vm))).predicate(vm -> applicable(self(vm), feature)).controlDescriptions(List.of(specification)).build());
                    }
                }
                groups.computeIfAbsent("validation", k -> new ArrayList<>()).add(validation(type));
                var groupDescriptions = new ArrayList<GroupDescription>();
                groups.forEach((key, controls) -> groupDescriptions.add(GroupDescription.newGroupDescription("hed." + type.getName() + "." + key).idProvider(vm -> "hed." + type.getName() + "." + key)
                        .labelProvider(vm -> Messages.text(locale, "group." + key)).semanticElementsProvider(vm -> List.of(self(vm))).controlDescriptions(controls).build()));
                registry.add(PageDescription.newPageDescription("hed.properties." + type.getName()).idProvider(vm -> "hed.properties." + type.getName())
                        .labelProvider(vm -> Messages.text(locale, "properties")).semanticElementsProvider(vm -> List.of(self(vm)))
                        .canCreatePredicate(vm -> vm.get(VariableManager.SELF, EObject.class).map(o -> o.eClass() == type).orElse(false)).groupDescriptions(groupDescriptions).build());
            }
        }
    }

    private EObject self(VariableManager vm) {
        return vm.get(VariableManager.SELF, EObject.class).orElseThrow();
    }

    /** Evaluates control visibility without changing values stored on the selected model element. */
    private boolean applicable(EObject object, EStructuralFeature feature) {
        var kind = object instanceof DeviceType type ? type.getKind() : object instanceof Equipment e ? HedDomain.kind(e) : null;
        return switch (feature.getName()) {
            case "phase" -> !(object instanceof Circuit) || HedDomain.hasThreePhaseSupply(object);
            case "widthModules" -> object instanceof ModularDevice || HedDomain.modular(kind);
            case "residualCurrentMa", "residualType" -> EnumSet.of(DeviceKind.RCCB, DeviceKind.RCBO, DeviceKind.SERVICE_BREAKER).contains(kind);
            case "tripCurve" -> EnumSet.of(DeviceKind.MCB, DeviceKind.RCBO).contains(kind);
            case "ratedPowerW" -> object instanceof ElectricalLoad || object instanceof DeviceType && EnumSet.of(DeviceKind.LUMINAIRE, DeviceKind.SHUTTER_MOTOR, DeviceKind.HEATER,
                    DeviceKind.WATER_HEATER, DeviceKind.VENTILATION, DeviceKind.COOKER, DeviceKind.FIXED_APPLIANCE, DeviceKind.OTHER_FIELD).contains(kind);
            case "channelCount" -> EnumSet.of(DeviceKind.SWITCH, DeviceKind.PUSH_BUTTON, DeviceKind.DIMMER, DeviceKind.SHUTTER_CONTROL).contains(kind);
            default -> true;
        };
    }

    private String group(EStructuralFeature f) {
        if (List.of("name", "description", "labelText", "manufacturer", "productReference").contains(f.getName())) {
            return "general";
        }
        if (f instanceof EReference) {
            return "connections";
        }
        return "technical";
    }

    private String id(Object value) {
        return value == null ? "" : value instanceof EObject o ? identity.getId(o) : value instanceof Enumerator e ? e.getLiteral() : value.toString();
    }

    private String label(Object value) {
        return value instanceof EObject o ? HedDomain.label(o, locale)
                : value instanceof Enumerator e ? Messages.text(locale, "enum." + e.getClass().getSimpleName() + "." + e.getName()) : Messages.text(locale, "unassigned");
    }

    private List<String> icons(Object value) {
        var path = value instanceof EObject ? HedDomainIcons.of(value) : value instanceof DeviceKind kind ? HedDomainIcons.concept(kind.getName()) : null;
        return path == null ? List.of() : List.of(path);
    }

    private IStatus update(Runnable action) {
        try {
            action.run();
            return new Success(ChangeKind.SEMANTIC_CHANGE, Map.of());
        } catch (IllegalArgumentException ex) {
            var key = ex instanceof NumberFormatException ? "positiveValue" : ex.getMessage();
            return new Failure(Messages.text(locale, key == null ? "requiredValue" : key));
        }
    }

    private List<?> options(EObject owner, EStructuralFeature feature) {
        if (feature instanceof EAttribute attribute && attribute.getEAttributeType() instanceof EEnum enumeration) {
            return enumeration.getELiterals().stream().map(EEnumLiteral::getInstance).toList();
        }
        if (!(feature instanceof EReference reference)) {
            return List.of();
        }
        var home = HedDomain.dwelling(owner);
        if (home == null) {
            return List.of();
        }
        var result = new ArrayList<Object>();
        if (!feature.isMany() && feature.getLowerBound() == 0) {
            result.add("");
        }
        HedDomain.contents(home).stream().filter(reference.getEReferenceType()::isInstance).filter(candidate -> allowed(owner, feature.getName(), candidate)).forEach(result::add);
        return result;
    }

    private boolean allowed(EObject owner, String feature, EObject candidate) {
        if (owner instanceof Equipment e && feature.equals("deviceType")) {
            return HedDomain.compatible(e, (DeviceType) candidate);
        }
        if (owner instanceof ModularDevice d && feature.equals("rail")) {
            return HedDomain.canPlaceDevice(d, (DINRail) candidate);
        }
        if (owner instanceof DINRail rail && feature.equals("devices")) {
            return candidate.eContainer() == rail.eContainer();
        }
        if (owner instanceof DistributionCircuit circuit && feature.equals("targetBoard")) {
            return candidate != circuit.getSourceBoard();
        }
        if (owner instanceof ModularDevice d && feature.equals("upstreamRccb")) {
            return d.getUpstreamServiceBreaker() == null && candidate != d && candidate.eContainer() == d.eContainer() && HedDomain.kind((ModularDevice) candidate) == DeviceKind.RCCB;
        }
        if (owner instanceof ModularDevice d && feature.equals("upstreamServiceBreaker")) {
            return d.getUpstreamRccb() == null;
        }
        if (feature.equals("downstreamDevices") && candidate instanceof ModularDevice d) {
            if (owner instanceof ServiceCircuitBreaker) {
                return d.getUpstreamRccb() == null;
            }
        }
        if (owner instanceof Circuit c && feature.equals("protectiveDevice")) {
            return candidate.eContainer() == c.getSourceBoard() && EnumSet.of(DeviceKind.MCB, DeviceKind.RCBO, DeviceKind.FUSE).contains(HedDomain.kind((ModularDevice) candidate))
                    && HedDomain.dwelling(c).getCircuits().stream().noneMatch(other -> other != c && other.getProtectiveDevice() == candidate);
        }
        if (owner instanceof ControlFunction && feature.equals("targets")) {
            return !(candidate instanceof ControlDevice);
        }
        if (owner instanceof ControlFunction && feature.equals("actuator")) {
            return EnumSet.of(DeviceKind.IMPULSE_RELAY, DeviceKind.CONTACTOR, DeviceKind.OTHER_MODULAR).contains(HedDomain.kind((ModularDevice) candidate));
        }
        return true;
    }

    private Object selected(EObject owner, EStructuralFeature feature, String selectedId) {
        return options(owner, feature).stream().filter(v -> id(v).equals(selectedId)).findFirst().filter(v -> !v.equals("")).orElse(null);
    }

    private AbstractControlDescription control(EClass type, EStructuralFeature feature) {
        String name = feature.getName(), widgetId = "hed." + type.getName() + "." + name;
        Function<VariableManager, String> target = vm -> identity.getId(self(vm));
        Function<VariableManager, String> title = vm -> Messages.text(locale, "feature." + name);
        Function<VariableManager, List<?>> diagnostics = vm -> List.of();
        Function<Object, String> kind = issue -> "ERROR", message = Object::toString;
        if (feature instanceof EReference || feature instanceof EAttribute a && a.getEAttributeType() instanceof EEnum) {
            if (feature.isMany()) {
                return MultiSelectDescription.newMultiSelectDescription(widgetId).idProvider(vm -> widgetId).targetObjectIdProvider(target).labelProvider(title)
                        .optionsProvider(vm -> options(self(vm), feature)).optionIdProvider(vm -> id(vm.get("candidate", Object.class).orElse(null)))
                        .optionLabelProvider(vm -> label(vm.get("candidate", Object.class).orElse(null))).optionIconURLProvider(vm -> icons(vm.get("candidate", Object.class).orElse(null)))
                        .styleProvider(vm -> MultiSelectStyle.newMultiSelectStyle().fontSize(14).showIcon(true).build())
                        .valuesProvider(vm -> ((Collection<?>) self(vm).eGet(feature)).stream().map(this::id).toList())
                        .newValuesHandler((vm, ids) -> update(() -> HedDomain.set(self(vm), name, ids.stream().map(i -> selected(self(vm), feature, i)).filter(Objects::nonNull).toList())))
                        .diagnosticsProvider(diagnostics).kindProvider(kind).messageProvider(message).build();
            }
            return SelectDescription.newSelectDescription(widgetId).idProvider(vm -> widgetId).targetObjectIdProvider(target).labelProvider(title).optionsProvider(vm -> options(self(vm), feature))
                    .optionIdProvider(vm -> id(vm.get("candidate", Object.class).orElse(null))).optionLabelProvider(vm -> label(vm.get("candidate", Object.class).orElse(null)))
                    .optionIconURLProvider(vm -> icons(vm.get("candidate", Object.class).orElse(null))).styleProvider(vm -> SelectStyle.newSelectStyle().fontSize(14).showIcon(true).build())
                    .valueProvider(vm -> id(self(vm).eGet(feature))).isReadOnlyProvider(vm -> self(vm) instanceof DeviceType t && name.equals("kind") && HedDomain.inUse(t))
                    .newValueHandler((vm, value) -> update(() -> HedDomain.set(self(vm), name, selected(self(vm), feature, value)))).diagnosticsProvider(diagnostics).kindProvider(kind)
                    .messageProvider(message).build();
        }
        return TextfieldDescription.newTextfieldDescription(widgetId).idProvider(vm -> widgetId).targetObjectIdProvider(target).labelProvider(title)
                .valueProvider(vm -> Objects.toString(self(vm).eGet(feature), "")).newValueHandler((vm, value) -> update(() -> HedDomain.setText(self(vm), name, value)))
                .diagnosticsProvider(diagnostics).kindProvider(kind).messageProvider(message).build();
    }

    private TextfieldDescription validation(EClass type) {
        var id = "hed." + type.getName() + ".validation";
        return TextfieldDescription.newTextfieldDescription(id).idProvider(vm -> id).targetObjectIdProvider(vm -> identity.getId(self(vm))).labelProvider(vm -> Messages.text(locale, "validation"))
                .isReadOnlyProvider(vm -> true).valueProvider(vm -> {
                    var self = self(vm);
                    var home = HedDomain.dwelling(self);
                    return home == null ? ""
                            : new HedValidation().validate(home).stream().filter(i -> self instanceof Dwelling || i.target() == self)
                                    .map(i -> HedDomain.label(i.target(), locale) + ": " + Messages.text(locale, i.code())).reduce((a, b) -> a + "\n" + b).orElse(Messages.text(locale, "consistent"));
                }).newValueHandler((vm, value) -> new Failure("Read only")).diagnosticsProvider(vm -> List.of()).kindProvider(Object::toString).messageProvider(Object::toString).build();
    }

    private TextfieldDescription technicalSpecification(EStructuralFeature feature) {
        var id = "hed.ModularDevice." + feature.getName();
        return TextfieldDescription.newTextfieldDescription(id).idProvider(vm -> id).targetObjectIdProvider(vm -> identity.getId(self(vm)))
                .labelProvider(vm -> Messages.text(locale, "feature." + feature.getName())).isReadOnlyProvider(vm -> true)
                .valueProvider(vm -> Objects.toString(HedDomain.get(self(vm), feature.getName()), "")).newValueHandler((vm, value) -> new Failure(Messages.text(locale, "catalogSpecifications")))
                .diagnosticsProvider(vm -> List.of()).kindProvider(Object::toString).messageProvider(Object::toString).build();
    }

    private TextfieldDescription downstreamDevices() {
        var id = "hed.ModularDevice.downstreamDevices";
        return TextfieldDescription.newTextfieldDescription(id).idProvider(vm -> id).targetObjectIdProvider(vm -> identity.getId(self(vm)))
                .labelProvider(vm -> Messages.text(locale, "feature.downstreamDevices")).isReadOnlyProvider(vm -> true)
                .valueProvider(vm -> HedDomain.downstreamDevices((ModularDevice) self(vm)).stream().map(device -> HedDomain.label(device, locale)).reduce((a, b) -> a + ", " + b).orElse(""))
                .newValueHandler((vm, value) -> new Failure("Read only")).diagnosticsProvider(vm -> List.of()).kindProvider(Object::toString).messageProvider(Object::toString).build();
    }
}
