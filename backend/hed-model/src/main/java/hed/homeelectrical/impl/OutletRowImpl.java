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

import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.Outlet;
import hed.homeelectrical.OutletRow;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Outlet Row</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.impl.OutletRowImpl#getOutlets <em>Outlets</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OutletRowImpl extends NamedElementImpl implements OutletRow {
    /**
     * The cached value of the '{@link #getOutlets() <em>Outlets</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOutlets()
     * @generated
     * @ordered
     */
    protected EList<Outlet> outlets;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected OutletRowImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.OUTLET_ROW;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<Outlet> getOutlets() {
        if (outlets == null) {
            outlets = new EObjectContainmentEList<Outlet>(Outlet.class, this, HomeElectricalPackage.OUTLET_ROW__OUTLETS);
        }
        return outlets;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case HomeElectricalPackage.OUTLET_ROW__OUTLETS:
                return ((InternalEList<?>) getOutlets()).basicRemove(otherEnd, msgs);
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
            case HomeElectricalPackage.OUTLET_ROW__OUTLETS:
                return getOutlets();
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
            case HomeElectricalPackage.OUTLET_ROW__OUTLETS:
                getOutlets().clear();
                getOutlets().addAll((Collection<? extends Outlet>) newValue);
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
            case HomeElectricalPackage.OUTLET_ROW__OUTLETS:
                getOutlets().clear();
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
            case HomeElectricalPackage.OUTLET_ROW__OUTLETS:
                return outlets != null && !outlets.isEmpty();
        }
        return super.eIsSet(featureID);
    }

} //OutletRowImpl
