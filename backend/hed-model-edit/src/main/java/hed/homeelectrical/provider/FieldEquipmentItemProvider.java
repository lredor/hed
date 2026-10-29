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

import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.HomeElectricalPackage;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link hed.homeelectrical.FieldEquipment} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class FieldEquipmentItemProvider extends EquipmentItemProvider {
    /**
     * This constructs an instance from a factory and a notifier.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public FieldEquipmentItemProvider(AdapterFactory adapterFactory) {
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

            addRatedCurrentAPropertyDescriptor(object);
            addRatedPowerWPropertyDescriptor(object);
            addWidthModulesPropertyDescriptor(object);
            addResidualCurrentMaPropertyDescriptor(object);
            addResidualTypePropertyDescriptor(object);
            addTripCurvePropertyDescriptor(object);
            addPoleConfigurationPropertyDescriptor(object);
            addRoomPropertyDescriptor(object);
            addCircuitPropertyDescriptor(object);
            addBranchPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Rated Current A feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addRatedCurrentAPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
                getString("_UI_TechnicalElement_ratedCurrentA_feature"), getString("_UI_PropertyDescriptor_description", "_UI_TechnicalElement_ratedCurrentA_feature", "_UI_TechnicalElement_type"),
                HomeElectricalPackage.Literals.TECHNICAL_ELEMENT__RATED_CURRENT_A, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Rated Power W feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addRatedPowerWPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
                getString("_UI_TechnicalElement_ratedPowerW_feature"), getString("_UI_PropertyDescriptor_description", "_UI_TechnicalElement_ratedPowerW_feature", "_UI_TechnicalElement_type"),
                HomeElectricalPackage.Literals.TECHNICAL_ELEMENT__RATED_POWER_W, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Width Modules feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addWidthModulesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
                getString("_UI_TechnicalElement_widthModules_feature"), getString("_UI_PropertyDescriptor_description", "_UI_TechnicalElement_widthModules_feature", "_UI_TechnicalElement_type"),
                HomeElectricalPackage.Literals.TECHNICAL_ELEMENT__WIDTH_MODULES, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Residual Current Ma feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addResidualCurrentMaPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(
                createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString("_UI_TechnicalElement_residualCurrentMa_feature"),
                        getString("_UI_PropertyDescriptor_description", "_UI_TechnicalElement_residualCurrentMa_feature", "_UI_TechnicalElement_type"),
                        HomeElectricalPackage.Literals.TECHNICAL_ELEMENT__RESIDUAL_CURRENT_MA, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Residual Type feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addResidualTypePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
                getString("_UI_TechnicalElement_residualType_feature"), getString("_UI_PropertyDescriptor_description", "_UI_TechnicalElement_residualType_feature", "_UI_TechnicalElement_type"),
                HomeElectricalPackage.Literals.TECHNICAL_ELEMENT__RESIDUAL_TYPE, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Trip Curve feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addTripCurvePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
                getString("_UI_TechnicalElement_tripCurve_feature"), getString("_UI_PropertyDescriptor_description", "_UI_TechnicalElement_tripCurve_feature", "_UI_TechnicalElement_type"),
                HomeElectricalPackage.Literals.TECHNICAL_ELEMENT__TRIP_CURVE, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Pole Configuration feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addPoleConfigurationPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(
                createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString("_UI_TechnicalElement_poleConfiguration_feature"),
                        getString("_UI_PropertyDescriptor_description", "_UI_TechnicalElement_poleConfiguration_feature", "_UI_TechnicalElement_type"),
                        HomeElectricalPackage.Literals.TECHNICAL_ELEMENT__POLE_CONFIGURATION, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Room feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addRoomPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
                getString("_UI_FieldEquipment_room_feature"), getString("_UI_PropertyDescriptor_description", "_UI_FieldEquipment_room_feature", "_UI_FieldEquipment_type"),
                HomeElectricalPackage.Literals.FIELD_EQUIPMENT__ROOM, true, false, true, null, null, null));
    }

    /**
     * This adds a property descriptor for the Circuit feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addCircuitPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
                getString("_UI_FieldEquipment_circuit_feature"), getString("_UI_PropertyDescriptor_description", "_UI_FieldEquipment_circuit_feature", "_UI_FieldEquipment_type"),
                HomeElectricalPackage.Literals.FIELD_EQUIPMENT__CIRCUIT, true, false, true, null, null, null));
    }

    /**
     * This adds a property descriptor for the Branch feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addBranchPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
                getString("_UI_FieldEquipment_branch_feature"), getString("_UI_PropertyDescriptor_description", "_UI_FieldEquipment_branch_feature", "_UI_FieldEquipment_type"),
                HomeElectricalPackage.Literals.FIELD_EQUIPMENT__BRANCH, true, false, true, null, null, null));
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getText(Object object) {
        String label = ((FieldEquipment) object).getName();
        return label == null || label.length() == 0 ? getString("_UI_FieldEquipment_type") : getString("_UI_FieldEquipment_type") + " " + label;
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

        switch (notification.getFeatureID(FieldEquipment.class)) {
            case HomeElectricalPackage.FIELD_EQUIPMENT__RATED_CURRENT_A:
            case HomeElectricalPackage.FIELD_EQUIPMENT__RATED_POWER_W:
            case HomeElectricalPackage.FIELD_EQUIPMENT__WIDTH_MODULES:
            case HomeElectricalPackage.FIELD_EQUIPMENT__RESIDUAL_CURRENT_MA:
            case HomeElectricalPackage.FIELD_EQUIPMENT__RESIDUAL_TYPE:
            case HomeElectricalPackage.FIELD_EQUIPMENT__TRIP_CURVE:
            case HomeElectricalPackage.FIELD_EQUIPMENT__POLE_CONFIGURATION:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
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
    }

}
