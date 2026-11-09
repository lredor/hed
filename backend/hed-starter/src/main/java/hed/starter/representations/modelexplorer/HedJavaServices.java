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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.nio.charset.StandardCharsets;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IFeedbackMessageService;
import org.eclipse.sirius.components.core.api.IIdentityService;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.components.representations.Message;
import org.eclipse.sirius.components.representations.MessageLevel;
import org.springframework.stereotype.Service;
import hed.domain.Messages;
import hed.homeelectrical.Catalog;
import hed.homeelectrical.Circuit;
import hed.homeelectrical.ControlChannel;
import hed.homeelectrical.ControlDevice;
import hed.homeelectrical.ControlFunction;
import hed.homeelectrical.DINRail;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.EnergyMeter;
import hed.homeelectrical.Equipment;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.ModularDevice;
import hed.homeelectrical.NamedElement;
import hed.homeelectrical.Room;
import hed.homeelectrical.ServiceConnection;
import hed.homeelectrical.Storey;
import hed.starter.HedDomainIcons;
import hed.starter.project.ProjectNameProvider;
import static hed.domain.HedDomain.F;
import static hed.domain.HedDomain.addEquipment;
import static hed.domain.HedDomain.addModularDevice;
import static hed.domain.HedDomain.canAddToRail;
import static hed.domain.HedDomain.delete;
import static hed.domain.HedDomain.dwelling;
import static hed.domain.HedDomain.get;
import static hed.domain.HedDomain.inUse;
import static hed.domain.HedDomain.initialize;
import static hed.domain.HedDomain.kind;
import static hed.domain.HedDomain.label;
import static hed.domain.HedDomain.modular;
import static hed.domain.HedDomain.type;

/** Services for the virtual model explorer. */
@Service
public class HedJavaServices {
    public record ProjectRoot(Dwelling home) {
    }

    public record Group(EObject owner, String feature) {
    }

    private final IIdentityService identity;

    private final IObjectSearchService search;

    private final IFeedbackMessageService feedback;

    private final Locale locale;

    private final ProjectNameProvider projectNames;

    public HedJavaServices(IIdentityService identity, IObjectSearchService search, IFeedbackMessageService feedback, Locale locale, ProjectNameProvider projectNames) {
        this.identity = identity;
        this.search = search;
        this.feedback = feedback;
        this.locale = locale;
        this.projectNames = projectNames;
    }

    public List<Object> getHedRoots(IEditingContext context) {
        if (context instanceof IEMFEditingContext emf) {
            return emf.getDomain().getResourceSet().getResources().stream().flatMap(r -> r.getContents().stream()).filter(Dwelling.class::isInstance).map(Dwelling.class::cast)
                    .map(home -> (Object) new ProjectRoot(home)).toList();
        }
        return List.of();
    }

    public List<Object> getHedChildren(Object self) {
        var result = new ArrayList<Object>();
        if (self instanceof ProjectRoot project) {
            result.add(project.home());
            result.add(new Group(project.home(), "gtl"));
            result.add(project.home().getCatalog());
        } else if (self instanceof Group group) {
            var owner = group.owner();
            var feature = group.feature();
            if (feature.equals("gtl")) {
                var home = (Dwelling) owner;
                result.add(home.getServiceConnection());
                result.addAll(home.getBoards());
                result.add(new Group(home, "circuits"));
            } else if (feature.equals("panelTypes") || feature.equals("terminalTypes")) {
                result.addAll(((Catalog) owner).getDeviceTypes().stream().filter(t -> panelType(t.getKind()) == feature.equals("panelTypes")).toList());
            } else if (feature.equals("unlocated")) {
                result.addAll(dwelling(owner).getEquipment().stream().filter(e -> e.getRoom() == null).toList());
            } else {
                result.addAll((Collection<?>) get(owner, feature));
            }
        } else if (self instanceof Dwelling home) {
            result.addAll(home.getStoreys());
            result.add(new Group(home, "unlocated"));
            result.add(new Group(home, "controlFunctions"));
        } else if (self instanceof Catalog catalog) {
            result.add(new Group(catalog, "panelTypes"));
            result.add(new Group(catalog, "terminalTypes"));
        } else if (self instanceof DistributionBoard board) {
            result.addAll(board.getRails());
        } else if (self instanceof Room room) {
            result.addAll(dwelling(room).getEquipment().stream().filter(e -> e.getRoom() == room).toList());
        } else if (self instanceof DINRail rail) {
            result.addAll(rail.getDevices());
        } else if (self instanceof EObject object) {
            result.addAll(object.eContents());
        }
        result.removeIf(Objects::isNull);
        return result;
    }

    public boolean hasHedChildren(Object self) {
        return !getHedChildren(self).isEmpty();
    }

    public String getHedId(Object self) {
        if (self instanceof ProjectRoot project) {
            return "hed-project:" + Base64.getUrlEncoder().withoutPadding().encodeToString(identity.getId(project.home()).getBytes(StandardCharsets.UTF_8));
        }
        if (self instanceof Group group) {
            return "hed-group:" + group.feature() + ":" + Base64.getUrlEncoder().withoutPadding().encodeToString(identity.getId(group.owner()).getBytes(StandardCharsets.UTF_8));
        }
        return identity.getId(self);
    }

    public Object getHedObject(String id, IEditingContext context) {
        if (id.startsWith("hed-project:")) {
            var homeId = new String(Base64.getUrlDecoder().decode(id.substring("hed-project:".length())), StandardCharsets.UTF_8);
            return search.getObject(context, homeId).filter(Dwelling.class::isInstance).map(Dwelling.class::cast).map(ProjectRoot::new).orElse(null);
        }
        if (id.startsWith("hed-group:")) {
            var parts = id.split(":", 3);
            var ownerId = new String(Base64.getUrlDecoder().decode(parts[2]), StandardCharsets.UTF_8);
            return search.getObject(context, ownerId).filter(EObject.class::isInstance).map(EObject.class::cast).map(owner -> new Group(owner, parts[1])).orElse(null);
        }
        return search.getObject(context, id).orElse(null);
    }

    public String getHedLabel(Object self) {
        if (self instanceof ProjectRoot project) {
            return projectNames.getName(project.home());
        }
        if (self instanceof Group group) {
            return Messages.text(locale, "explorer." + group.feature());
        }
        return self instanceof EObject object ? label(object, locale) : "";
    }

    public String getHedExplorerLabel(Object self) {
        return self instanceof Dwelling ? Messages.text(locale, "class.Dwelling") : getHedLabel(self);
    }

    public String getHedKind(Object self) {
        return self instanceof ProjectRoot ? "hed-project" : self instanceof Group ? "hed-group" : identity.getKind(self);
    }

    public String getHedIcon(Object self) {
        return HedDomainIcons.of(self);
    }

    public boolean isHedSelectable(Object self) {
        return self instanceof EObject;
    }

    public boolean isHedEditable(Object self) {
        return isHedSelectable(self) && !(self instanceof Dwelling);
    }

    public boolean isHedDeletable(Object self) {
        return self instanceof EObject && !(self instanceof Dwelling) && !(self instanceof Catalog) && !(self instanceof DINRail r && !r.getDevices().isEmpty())
                && !(self instanceof DeviceType t && inUse(t)) && !(self instanceof ControlChannel c && ((ControlDevice) c.eContainer()).getChannels().size() == 1);
    }

    public boolean canAddHedObject(Object self) {
        if (!(self instanceof EObject)) {
            return false;
        }
        return java.util.stream.Stream
                .concat(java.util.stream.Stream.of("Storey", "Room", "DistributionBoard", "DINRail", "FinalCircuit", "CircuitBranch", "DistributionCircuit", "ControlFunction", "ControlChannel",
                        "ServiceConnection", "EnergyMeter", "ServiceCircuitBreaker", "DeviceType"), Arrays.stream(DeviceKind.values()).map(DeviceKind::getName))
                .anyMatch(kind -> canCreateHedObject(self, kind));
    }

    public boolean canRepresentHedObject(Object self) {
        return false;
    }

    public Object getHedParent(Object self) {
        if (self instanceof Dwelling home) {
            return new ProjectRoot(home);
        }
        if (self instanceof Catalog catalog) {
            return new ProjectRoot(dwelling(catalog));
        }
        if (self instanceof Group group) {
            return switch (group.feature()) {
                case "gtl" -> new ProjectRoot((Dwelling) group.owner());
                case "circuits" -> new Group(group.owner(), "gtl");
                default -> group.owner();
            };
        }
        if (self instanceof FieldEquipment e) {
            return e.getRoom() != null ? e.getRoom() : new Group(dwelling(e), "unlocated");
        }
        if (self instanceof ModularDevice d) {
            return d.getRail();
        }
        if (self instanceof Storey s) {
            return dwelling(s);
        }
        if (self instanceof DistributionBoard || self instanceof ServiceConnection) {
            return new Group(dwelling((EObject) self), "gtl");
        }
        if (self instanceof DeviceType deviceType) {
            return new Group((Catalog) deviceType.eContainer(), panelType(deviceType.getKind()) ? "panelTypes" : "terminalTypes");
        }
        if (self instanceof Circuit c) {
            return new Group(dwelling(c), "circuits");
        }
        if (self instanceof ControlFunction c) {
            return new Group(dwelling(c), "controlFunctions");
        }
        return self instanceof EObject o ? o.eContainer() : null;
    }

    private boolean panelType(DeviceKind kind) {
        return modular(kind) || kind == DeviceKind.ENERGY_METER || kind == DeviceKind.SERVICE_BREAKER;
    }

    private EObject owner(Object self) {
        return self instanceof Group group ? group.owner() : (EObject) self;
    }

    private boolean hasType(EObject object, DeviceKind kind) {
        var home = dwelling(object);
        return home != null && home.getCatalog() != null && home.getCatalog().getDeviceTypes().stream().anyMatch(type -> type.getKind() == kind);
    }

    public boolean canCreateHedObject(Object self, String kind) {
        if (!(self instanceof EObject) && !(self instanceof Group)) {
            return false;
        }
        if (self instanceof Group group) {
            if (group.feature().equals("gtl")) {
                return kind.equals("DistributionBoard") || kind.equals("ServiceConnection") && ((Dwelling) group.owner()).getServiceConnection() == null;
            }
            if (group.feature().equals("panelTypes") || group.feature().equals("terminalTypes")) {
                return kind.equals("DeviceType");
            }
            boolean matches = switch (group.feature()) {
                case "circuits" -> Set.of("FinalCircuit", "DistributionCircuit").contains(kind);
                case "controlFunctions" -> kind.equals("ControlFunction");
                case "unlocated" -> Arrays.stream(DeviceKind.values()).anyMatch(k -> k.getName().equals(kind) && !panelType(k));
                default -> false;
            };
            if (!matches) {
                return false;
            }
        }
        var parent = owner(self);
        if (kind.equals("CircuitBranch")) {
            return parent instanceof FinalCircuit;
        }
        if (kind.equals("Storey") || kind.equals("ControlFunction")) {
            return parent instanceof Dwelling;
        }
        if (kind.equals("Room")) {
            return parent instanceof Storey;
        }
        if (kind.equals("DINRail")) {
            return parent instanceof DistributionBoard;
        }
        if (kind.equals("ControlChannel")) {
            return parent instanceof ControlDevice;
        }
        if (kind.equals("DeviceType")) {
            return parent instanceof Catalog;
        }
        if (kind.equals("FinalCircuit") || kind.equals("DistributionCircuit")) {
            return parent instanceof Dwelling || parent instanceof DistributionBoard;
        }
        if (kind.equals("DistributionBoard") || kind.equals("ServiceConnection")) {
            return false;
        }
        if (kind.equals("EnergyMeter")) {
            return parent instanceof ServiceConnection c && c.getMeter() == null && hasType(parent, DeviceKind.ENERGY_METER);
        }
        if (kind.equals("ServiceCircuitBreaker")) {
            return parent instanceof ServiceConnection c && c.getMainBreaker() == null && hasType(parent, DeviceKind.SERVICE_BREAKER);
        }
        try {
            var deviceKind = DeviceKind.valueOf(kind);
            var home = dwelling(parent);
            if (home == null || home.getCatalog() == null || home.getCatalog().getDeviceTypes().stream().noneMatch(t -> t.getKind() == deviceKind)) {
                return false;
            }
            if (modular(deviceKind)) {
                return parent instanceof DINRail rail && canAddToRail(rail, type(home, deviceKind));
            }
            return deviceKind != DeviceKind.ENERGY_METER && deviceKind != DeviceKind.SERVICE_BREAKER && (parent instanceof Room || parent instanceof Dwelling);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public Object createHedObject(Object self, String kind) {
        if (!canCreateHedObject(self, kind)) {
            return self;
        }
        var parent = owner(self);
        var home = dwelling(parent);
        try {
            var classifier = HomeElectricalPackage.eINSTANCE.getEClassifier(kind);
            if (classifier instanceof EClass type) {
                var created = F.create(type);
                if (created instanceof NamedElement n) {
                    n.setName(Messages.text(locale, "new." + kind));
                }
                if (created instanceof DeviceType deviceType && self instanceof Group group && group.feature().equals("panelTypes")) {
                    deviceType.setKind(DeviceKind.MCB);
                }
                if (created instanceof Equipment equipment) {
                    initialize(equipment, type(home, created instanceof EnergyMeter ? DeviceKind.ENERGY_METER : DeviceKind.SERVICE_BREAKER), locale);
                }
                var reference = parent.eClass().getEAllContainments().stream().filter(r -> r.getEReferenceType().isInstance(created)).findFirst().orElse(null);
                if (reference == null && created instanceof Circuit circuit) {
                    home.getCircuits().add(circuit);
                    if (parent instanceof DistributionBoard board) {
                        circuit.setSourceBoard(board);
                    }
                } else if (reference != null) {
                    if (reference.isMany()) {
                        ((Collection<Object>) parent.eGet(reference)).add(created);
                    } else {
                        parent.eSet(reference, created);
                    }
                } else {
                    return self;
                }
                return created;
            }
            var deviceKind = DeviceKind.valueOf(kind);
            var deviceType = type(home, deviceKind);
            if (modular(deviceKind)) {
                return addModularDevice((DINRail) parent, deviceType, locale);
            }
            var equipment = addEquipment(home, deviceType, locale);
            if (parent instanceof Room room) {
                equipment.setRoom(room);
            }
            return equipment;
        } catch (IllegalArgumentException ex) {
            feedback.addFeedbackMessage(new Message(Messages.text(locale, ex.getMessage()), MessageLevel.ERROR));
            return self;
        }
    }

    public Object deleteHedObject(EObject self) {
        try {
            delete(self);
        } catch (IllegalArgumentException ex) {
            feedback.addFeedbackMessage(new Message(Messages.text(locale, ex.getMessage()), MessageLevel.ERROR));
        }
        return self;
    }

    public String getHedText(Object self, String key) {
        return Messages.text(locale, key);
    }
}
