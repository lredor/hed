/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.homeelectrical.provider;

import hed.homeelectrical.Dwelling;
import hed.homeelectrical.HomeElectricalFactory;
import hed.homeelectrical.HomeElectricalPackage;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link hed.homeelectrical.Dwelling} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class DwellingItemProvider extends NamedElementItemProvider {
    /**
     * This constructs an instance from a factory and a notifier.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DwellingItemProvider(AdapterFactory adapterFactory) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

        }
        return itemPropertyDescriptors;
    }

    /**
     * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
     * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
     * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
        if (childrenFeatures == null) {
            super.getChildrenFeatures(object);
            childrenFeatures.add(HomeElectricalPackage.Literals.DWELLING__CATALOG);
            childrenFeatures.add(HomeElectricalPackage.Literals.DWELLING__STOREYS);
            childrenFeatures.add(HomeElectricalPackage.Literals.DWELLING__EQUIPMENT);
            childrenFeatures.add(HomeElectricalPackage.Literals.DWELLING__BOARDS);
            childrenFeatures.add(HomeElectricalPackage.Literals.DWELLING__CIRCUITS);
            childrenFeatures.add(HomeElectricalPackage.Literals.DWELLING__CONTROL_FUNCTIONS);
            childrenFeatures.add(HomeElectricalPackage.Literals.DWELLING__SERVICE_CONNECTION);
        }
        return childrenFeatures;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EStructuralFeature getChildFeature(Object object, Object child) {
        // Check the type of the specified child object and return the proper feature to use for
        // adding (see {@link AddCommand}) it as a child.

        return super.getChildFeature(object, child);
    }

    /**
     * This returns Dwelling.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object getImage(Object object) {
        return overlayImage(object, getResourceLocator().getImage("full/obj16/Dwelling"));
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getText(Object object) {
        String label = ((Dwelling) object).getName();
        return label == null || label.length() == 0 ? getString("_UI_Dwelling_type") : getString("_UI_Dwelling_type") + " " + label;
    }

    /**
     * This handles model notifications by calling {@link #updateChildren} to update any cached
     * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void notifyChanged(Notification notification) {
        updateChildren(notification);

        switch (notification.getFeatureID(Dwelling.class)) {
            case HomeElectricalPackage.DWELLING__CATALOG:
            case HomeElectricalPackage.DWELLING__STOREYS:
            case HomeElectricalPackage.DWELLING__EQUIPMENT:
            case HomeElectricalPackage.DWELLING__BOARDS:
            case HomeElectricalPackage.DWELLING__CIRCUITS:
            case HomeElectricalPackage.DWELLING__CONTROL_FUNCTIONS:
            case HomeElectricalPackage.DWELLING__SERVICE_CONNECTION:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
                return;
        }
        super.notifyChanged(notification);
    }

    /**
     * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
     * that can be created under this object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
        super.collectNewChildDescriptors(newChildDescriptors, object);

        newChildDescriptors.add(createChildParameter(HomeElectricalPackage.Literals.DWELLING__CATALOG, HomeElectricalFactory.eINSTANCE.createCatalog()));

        newChildDescriptors.add(createChildParameter(HomeElectricalPackage.Literals.DWELLING__STOREYS, HomeElectricalFactory.eINSTANCE.createStorey()));

        newChildDescriptors.add(createChildParameter(HomeElectricalPackage.Literals.DWELLING__EQUIPMENT, HomeElectricalFactory.eINSTANCE.createOutlet()));

        newChildDescriptors.add(createChildParameter(HomeElectricalPackage.Literals.DWELLING__EQUIPMENT, HomeElectricalFactory.eINSTANCE.createElectricalLoad()));

        newChildDescriptors.add(createChildParameter(HomeElectricalPackage.Literals.DWELLING__EQUIPMENT, HomeElectricalFactory.eINSTANCE.createControlDevice()));

        newChildDescriptors.add(createChildParameter(HomeElectricalPackage.Literals.DWELLING__BOARDS, HomeElectricalFactory.eINSTANCE.createDistributionBoard()));

        newChildDescriptors.add(createChildParameter(HomeElectricalPackage.Literals.DWELLING__CIRCUITS, HomeElectricalFactory.eINSTANCE.createFinalCircuit()));

        newChildDescriptors.add(createChildParameter(HomeElectricalPackage.Literals.DWELLING__CIRCUITS, HomeElectricalFactory.eINSTANCE.createDistributionCircuit()));

        newChildDescriptors.add(createChildParameter(HomeElectricalPackage.Literals.DWELLING__CONTROL_FUNCTIONS, HomeElectricalFactory.eINSTANCE.createControlFunction()));

        newChildDescriptors.add(createChildParameter(HomeElectricalPackage.Literals.DWELLING__SERVICE_CONNECTION, HomeElectricalFactory.eINSTANCE.createServiceConnection()));
    }

}
