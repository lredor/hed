/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.homeelectrical.impl;

import hed.homeelectrical.DeviceType;
import hed.homeelectrical.Equipment;
import hed.homeelectrical.HomeElectricalPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Equipment</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.impl.EquipmentImpl#getDeviceType <em>Device Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class EquipmentImpl extends NamedElementImpl implements Equipment {
    /**
     * The cached value of the '{@link #getDeviceType() <em>Device Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDeviceType()
     * @generated
     * @ordered
     */
    protected DeviceType deviceType;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EquipmentImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.EQUIPMENT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public DeviceType getDeviceType() {
        if (deviceType != null && deviceType.eIsProxy()) {
            InternalEObject oldDeviceType = (InternalEObject) deviceType;
            deviceType = (DeviceType) eResolveProxy(oldDeviceType);
            if (deviceType != oldDeviceType) {
                if (eNotificationRequired()) {
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, HomeElectricalPackage.EQUIPMENT__DEVICE_TYPE, oldDeviceType, deviceType));
                }
            }
        }
        return deviceType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DeviceType basicGetDeviceType() {
        return deviceType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setDeviceType(DeviceType newDeviceType) {
        DeviceType oldDeviceType = deviceType;
        deviceType = newDeviceType;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.EQUIPMENT__DEVICE_TYPE, oldDeviceType, deviceType));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case HomeElectricalPackage.EQUIPMENT__DEVICE_TYPE:
                if (resolve) {
                    return getDeviceType();
                }
                return basicGetDeviceType();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case HomeElectricalPackage.EQUIPMENT__DEVICE_TYPE:
                setDeviceType((DeviceType) newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case HomeElectricalPackage.EQUIPMENT__DEVICE_TYPE:
                setDeviceType((DeviceType) null);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case HomeElectricalPackage.EQUIPMENT__DEVICE_TYPE:
                return deviceType != null;
        }
        return super.eIsSet(featureID);
    }

} //EquipmentImpl
