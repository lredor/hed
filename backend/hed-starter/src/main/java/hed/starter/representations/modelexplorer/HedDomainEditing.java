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
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.core.api.ChildCreationDescription;
import org.eclipse.sirius.components.core.api.IEditServiceDelegate;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IFeedbackMessageService;
import org.eclipse.sirius.components.core.api.IIdentityService;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.components.representations.Message;
import org.eclipse.sirius.components.representations.MessageLevel;
import org.springframework.stereotype.Service;
import hed.domain.HedDomain;
import hed.domain.Messages;
import hed.domain.HedSamples;
import hed.homeelectrical.DINRail;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.Room;

@Service
public class HedDomainEditing extends IEditServiceDelegate.NoOp {
    private final HedJavaServices services;

    private final IIdentityService identity;

    private final Locale locale;

    private final IFeedbackMessageService feedback;

    public HedDomainEditing(HedJavaServices services, IIdentityService identity, Locale locale, IFeedbackMessageService feedback) {
        this.services = services;
        this.identity = identity;
        this.locale = locale;
        this.feedback = feedback;
    }

    @Override
    public boolean canHandle(Object object) {
        return object instanceof EObject e && e.eClass().getEPackage() == HomeElectricalPackage.eINSTANCE;
    }

    @Override
    public boolean canHandle(IEditingContext context) {
        return context instanceof IEMFEditingContext;
    }

    @Override
    public List<ChildCreationDescription> getRootCreationDescriptions(IEditingContext context, String domainId, boolean suggested, String referenceKind) {
        return List.of(new ChildCreationDescription("hed-dwelling", Messages.text(locale, "class.Dwelling"), List.of("/icons/svg/Default.svg")));
    }

    @Override
    public List<ChildCreationDescription> getChildCreationDescriptions(IEditingContext context, String containerId, String referenceKind) {
        var object = services.getHedObject(containerId, context);
        if (!(object instanceof EObject owner)) {
            return List.of();
        }
        var descriptions = new ArrayList<ChildCreationDescription>();
        for (var name : List.of("Storey", "Room", "DistributionBoard", "DINRail", "FinalCircuit", "CircuitBranch", "DistributionCircuit", "ControlFunction", "ControlChannel", "ServiceConnection",
                "EnergyMeter", "ServiceCircuitBreaker", "DeviceType")) {
            if (services.canCreateHedObject(owner, name)) {
                descriptions.add(new ChildCreationDescription("hed-class:" + name, Messages.text(locale, "class." + name), List.of("/icons/svg/Default.svg")));
            }
        }
        var home = HedDomain.dwelling(owner);
        if (home != null && home.getCatalog() != null) {
            for (var type : home.getCatalog().getDeviceTypes()) {
                var k = type.getKind();
                if (HedDomain.modular(k) ? owner instanceof DINRail rail && HedDomain.canAddToRail(rail, type)
                        : (owner instanceof Room || owner instanceof Dwelling) && k != DeviceKind.ENERGY_METER && k != DeviceKind.SERVICE_BREAKER) {
                    descriptions.add(new ChildCreationDescription("hed-type:" + identity.getId(type), HedDomain.label(type, locale), List.of("/icons/svg/Default.svg")));
                }
            }
        }
        return descriptions;
    }

    @Override
    public Optional<Object> createChild(IEditingContext context, Object object, String description) {
        if (!(object instanceof EObject parent)) {
            return Optional.empty();
        }
        try {
            if (description.startsWith("hed-class:")) {
                return services.canCreateHedObject(parent, description.substring(10)) ? Optional.ofNullable(services.createHedObject(parent, description.substring(10))) : Optional.empty();
            }
            if (description.startsWith("hed-type:")) {
                var target = services.getHedObject(description.substring(9), context);
                if (!(target instanceof DeviceType type) || HedDomain.dwelling(type) != HedDomain.dwelling(parent)) {
                    return Optional.empty();
                }
                if (parent instanceof DINRail rail && HedDomain.modular(type.getKind())) {
                    return Optional.of(HedDomain.addModularDevice(rail, type, locale));
                }
                if (parent instanceof Room || parent instanceof Dwelling) {
                    if (HedDomain.modular(type.getKind()) || type.getKind() == DeviceKind.ENERGY_METER || type.getKind() == DeviceKind.SERVICE_BREAKER) {
                        return Optional.empty();
                    }
                    var equipment = HedDomain.addEquipment(HedDomain.dwelling(parent), type, locale);
                    if (parent instanceof Room room) {
                        equipment.setRoom(room);
                    }
                    return Optional.of(equipment);
                }
            }
            return Optional.empty();
        } catch (IllegalArgumentException ex) {
            feedback.addFeedbackMessage(new Message(Messages.text(locale, ex.getMessage()), MessageLevel.ERROR));
            return Optional.empty();
        }
    }

    @Override
    public Optional<Object> createRootObject(IEditingContext context, UUID documentId, String domainId, String description) {
        if (!description.equals("hed-dwelling") || !(context instanceof IEMFEditingContext emf)) {
            return Optional.empty();
        }
        var resource = emf.getDomain().getResourceSet().getResources().stream().filter(r -> r.getURI().toString().contains(documentId.toString())).findFirst();
        return resource.filter(r -> r.getContents().isEmpty()).map(r -> {
            var home = HedSamples.empty(Messages.text(locale, "new.Dwelling"), locale);
            r.getContents().add(home);
            return home;
        });
    }

    @Override
    public void delete(Object object) {
        try {
            HedDomain.delete((EObject) object);
        } catch (IllegalArgumentException ex) {
            feedback.addFeedbackMessage(new Message(Messages.text(locale, ex.getMessage()), MessageLevel.ERROR));
        }
    }
}
