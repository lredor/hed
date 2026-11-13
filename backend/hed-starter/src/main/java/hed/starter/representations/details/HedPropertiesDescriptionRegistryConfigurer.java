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
import java.util.Comparator;
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
import org.eclipse.sirius.components.emf.services.api.IEMFKindService;
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
import hed.homeelectrical.ControlChannel;
import hed.homeelectrical.ControlDevice;
import hed.homeelectrical.ControlFunction;
import hed.homeelectrical.DINRail;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.DistributionCircuit;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.ElectricalLoad;
import hed.homeelectrical.Equipment;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.ModularDevice;
import hed.homeelectrical.ServiceCircuitBreaker;
import hed.homeelectrical.Storey;
import hed.starter.HedDomainIcons;

/** Builds localized properties pages with controls filtered by device kind and supply configuration. */
@Service
public class HedPropertiesDescriptionRegistryConfigurer implements IPropertiesDescriptionRegistryConfigurer {
    private final IIdentityService identity;

    private final Locale locale;

    private final HedEquipmentReferenceDescriptionFactory equipmentReferences;

    /**
     * Creates the properties description contribution.
     *
     * @param identity supplies stable identifiers for semantic targets
     * @param locale selects property labels and validation messages
     * @param kinds supplies EMF type identifiers to the native reference browser
     */
    public HedPropertiesDescriptionRegistryConfigurer(IIdentityService identity, Locale locale, IEMFKindService kinds) {
        this.identity = identity;
        this.locale = locale;
        this.equipmentReferences = new HedEquipmentReferenceDescriptionFactory(identity, kinds, locale, this::update);
    }

    @Override
    public void addPropertiesDescriptions(IPropertiesDescriptionRegistry registry) {
        for (var classifier : HomeElectricalPackage.eINSTANCE.getEClassifiers()) {
            if (classifier instanceof EClass type && !type.isAbstract()) {
                var groups = new LinkedHashMap<String, List<AbstractControlDescription>>();
                var features = new ArrayList<>(type.getEAllStructuralFeatures());
                if (HomeElectricalPackage.Literals.EQUIPMENT.isSuperTypeOf(type)) {
                    var generalOrder = List.of("name", "description", "labelText", "deviceType");
                    features.sort(Comparator.comparingInt(feature -> {
                        int index = generalOrder.indexOf(feature.getName());
                        return index < 0 ? Integer.MAX_VALUE : index;
                    }));
                }
                for (var feature : features) {
                    if (type == HomeElectricalPackage.Literals.FINAL_CIRCUIT && feature.getName().equals("equipment")) {
                        continue;
                    }
                    // Image identity and reference proportions belong to the image-import tool.
                    if (type == HomeElectricalPackage.Literals.STOREY && List.of("planImageId", "planAspectRatio").contains(feature.getName())) {
                        continue;
                    }
                    if (type == HomeElectricalPackage.Literals.MODULAR_DEVICE && feature.getName().equals("description")) {
                        continue;
                    }
                    if (feature instanceof EReference reference && (reference.isContainment() || reference.isContainer() && type != HomeElectricalPackage.Literals.CIRCUIT_BRANCH)) {
                        continue;
                    }
                    var widget = control(type, feature);
                    groups.computeIfAbsent(group(type, feature), k -> new ArrayList<>()).add(IfDescription.newIfDescription("hed.visible." + type.getName() + "." + feature.getName())
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
                if (type == HomeElectricalPackage.Literals.FINAL_CIRCUIT) {
                    groups.computeIfAbsent("connections", k -> new ArrayList<>()).add(equipmentReferences.create("FinalCircuit"));
                }
                groups.computeIfAbsent("validation", k -> new ArrayList<>()).add(validation(type));
                if (HomeElectricalPackage.Literals.EQUIPMENT.isSuperTypeOf(type)) {
                    addPage(registry, type, "", "properties.general", groups, List.of("general", "connections", "validation"));
                    addPage(registry, type, ".advanced", "properties.advanced", groups, List.of("technical"));
                } else {
                    addPage(registry, type, "", "properties", groups, List.copyOf(groups.keySet()));
                }
            }
        }
    }

    /** Registers ordered groups on a page, retaining the existing main-page identifier for saved Details state. */
    private void addPage(IPropertiesDescriptionRegistry registry, EClass type, String suffix, String labelKey, Map<String, List<AbstractControlDescription>> groups, List<String> order) {
        var descriptions = new ArrayList<GroupDescription>();
        for (var key : order) {
            var controls = groups.get(key);
            if (controls != null && !controls.isEmpty()) {
                var groupId = "hed." + type.getName() + "." + key;
                descriptions.add(GroupDescription.newGroupDescription(groupId).idProvider(vm -> groupId).labelProvider(vm -> Messages.text(locale, "group." + key))
                        .semanticElementsProvider(vm -> List.of(self(vm))).controlDescriptions(controls).build());
            }
        }
        if (!descriptions.isEmpty()) {
            var pageId = "hed.properties." + type.getName() + suffix;
            registry.add(PageDescription.newPageDescription(pageId).idProvider(vm -> pageId).labelProvider(vm -> Messages.text(locale, labelKey)).semanticElementsProvider(vm -> List.of(self(vm)))
                    .canCreatePredicate(vm -> vm.get(VariableManager.SELF, EObject.class).map(o -> o.eClass() == type).orElse(false)).groupDescriptions(descriptions).build());
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

    private String group(EClass type, EStructuralFeature f) {
        if (List.of("name", "description", "labelText", "manufacturer", "productReference").contains(f.getName())
                || f.getName().equals("deviceType") && HomeElectricalPackage.Literals.EQUIPMENT.isSuperTypeOf(type)) {
            return "general";
        }
        if (f instanceof EReference) {
            return "connections";
        }
        return "technical";
    }

    /** A selected circuit without branches cannot accept branch assignments; an unassigned device can still choose a branch. */
    private boolean circuitWithoutBranches(EObject object) {
        return object instanceof FieldEquipment equipment && equipment.getCircuit() != null && equipment.getCircuit().getBranches().isEmpty();
    }

    private String id(Object value) {
        return value == null ? "" : value instanceof EObject o ? identity.getId(o) : value instanceof Enumerator e ? e.getLiteral() : value.toString();
    }

    /** Qualifies channel reference choices with their control device while retaining the stored channel name. */
    private String label(Object value) {
        if (value instanceof ControlChannel channel && channel.eContainer() instanceof ControlDevice device) {
            return HedDomain.label(device, locale) + " - " + HedDomain.label(channel, locale);
        }
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
        if (feature.getName().equals("branch") && circuitWithoutBranches(owner)) {
            return List.of("");
        }
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
        if (owner instanceof hed.homeelectrical.FieldEquipment equipment && feature.equals("circuit") && candidate instanceof FinalCircuit circuit) {
            return circuit.getBranches().isEmpty() || equipment.getBranch() != null && equipment.getBranch().getCircuit() == circuit;
        }
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
        var selection = options(owner, feature).stream().filter(value -> id(value).equals(selectedId)).findFirst();
        if (selection.isEmpty()) {
            throw new IllegalArgumentException(owner instanceof hed.homeelectrical.FieldEquipment && feature.getName().equals("circuit") ? "requiredBranch" : "foreignReference");
        }
        return selection.get().equals("") ? null : selection.get();
    }

    private AbstractControlDescription control(EClass type, EStructuralFeature feature) {
        String name = feature.getName(), widgetId = "hed." + type.getName() + "." + name;
        if (type == HomeElectricalPackage.Literals.CIRCUIT_BRANCH && name.equals("equipment")) {
            return equipmentReferences.create("CircuitBranch");
        }
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
                    .optionIdProvider(vm -> id(vm.get("candidate", Object.class).orElse(null)))
                    .optionLabelProvider(
                            vm -> name.equals("branch") && circuitWithoutBranches(self(vm)) ? Messages.text(locale, "circuitWithoutBranches") : label(vm.get("candidate", Object.class).orElse(null)))
                    .optionIconURLProvider(vm -> icons(vm.get("candidate", Object.class).orElse(null))).styleProvider(vm -> SelectStyle.newSelectStyle().fontSize(14).showIcon(true).build())
                    .valueProvider(vm -> id(self(vm).eGet(feature)))
                    .isReadOnlyProvider(vm -> self(vm) instanceof DeviceType t && name.equals("kind") && HedDomain.inUse(t) || name.equals("branch") && circuitWithoutBranches(self(vm)))
                    .newValueHandler((vm, value) -> update(() -> {
                        if (name.equals("branch") && circuitWithoutBranches(self(vm))) {
                            throw new IllegalArgumentException("circuitWithoutBranches");
                        }
                        HedDomain.set(self(vm), name, selected(self(vm), feature, value));
                    })).diagnosticsProvider(diagnostics).kindProvider(kind).messageProvider(message).build();
        }
        return TextfieldDescription.newTextfieldDescription(widgetId).idProvider(vm -> widgetId).targetObjectIdProvider(target).labelProvider(title)
                .isReadOnlyProvider(vm -> self(vm) instanceof Storey && name.equals("planHeight")).valueProvider(vm -> Objects.toString(self(vm).eGet(feature), ""))
                .newValueHandler((vm, value) -> update(() -> HedDomain.setText(self(vm), name, value))).diagnosticsProvider(diagnostics).kindProvider(kind).messageProvider(message).build();
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
