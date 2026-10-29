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

import hed.homeelectrical.DINRail;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.ModularDevice;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>DIN Rail</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.impl.DINRailImpl#getCapacityModules <em>Capacity Modules</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.DINRailImpl#getDevices <em>Devices</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DINRailImpl extends NamedElementImpl implements DINRail {
    /**
     * The default value of the '{@link #getCapacityModules() <em>Capacity Modules</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCapacityModules()
     * @generated
     * @ordered
     */
    protected static final int CAPACITY_MODULES_EDEFAULT = 13;

    /**
     * The cached value of the '{@link #getCapacityModules() <em>Capacity Modules</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCapacityModules()
     * @generated
     * @ordered
     */
    protected int capacityModules = CAPACITY_MODULES_EDEFAULT;

    /**
     * The cached value of the '{@link #getDevices() <em>Devices</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDevices()
     * @generated
     * @ordered
     */
    protected EList<ModularDevice> devices;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DINRailImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.DIN_RAIL;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int getCapacityModules() {
        return capacityModules;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setCapacityModules(int newCapacityModules) {
        int oldCapacityModules = capacityModules;
        capacityModules = newCapacityModules;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.DIN_RAIL__CAPACITY_MODULES, oldCapacityModules, capacityModules));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<ModularDevice> getDevices() {
        if (devices == null) {
            devices = new EObjectWithInverseResolvingEList<ModularDevice>(ModularDevice.class, this, HomeElectricalPackage.DIN_RAIL__DEVICES, HomeElectricalPackage.MODULAR_DEVICE__RAIL);
        }
        return devices;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case HomeElectricalPackage.DIN_RAIL__DEVICES:
                return ((InternalEList<InternalEObject>) (InternalEList<?>) getDevices()).basicAdd(otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case HomeElectricalPackage.DIN_RAIL__DEVICES:
                return ((InternalEList<?>) getDevices()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case HomeElectricalPackage.DIN_RAIL__CAPACITY_MODULES:
                return getCapacityModules();
            case HomeElectricalPackage.DIN_RAIL__DEVICES:
                return getDevices();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case HomeElectricalPackage.DIN_RAIL__CAPACITY_MODULES:
                setCapacityModules((Integer) newValue);
                return;
            case HomeElectricalPackage.DIN_RAIL__DEVICES:
                getDevices().clear();
                getDevices().addAll((Collection<? extends ModularDevice>) newValue);
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
            case HomeElectricalPackage.DIN_RAIL__CAPACITY_MODULES:
                setCapacityModules(CAPACITY_MODULES_EDEFAULT);
                return;
            case HomeElectricalPackage.DIN_RAIL__DEVICES:
                getDevices().clear();
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
            case HomeElectricalPackage.DIN_RAIL__CAPACITY_MODULES:
                return capacityModules != CAPACITY_MODULES_EDEFAULT;
            case HomeElectricalPackage.DIN_RAIL__DEVICES:
                return devices != null && !devices.isEmpty();
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) {
            return super.toString();
        }

        StringBuilder result = new StringBuilder(super.toString());
        result.append(" (capacityModules: ");
        result.append(capacityModules);
        result.append(')');
        return result.toString();
    }

} //DINRailImpl
