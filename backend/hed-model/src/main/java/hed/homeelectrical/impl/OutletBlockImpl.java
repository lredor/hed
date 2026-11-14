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
import hed.homeelectrical.OutletBlock;
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
 * An implementation of the model object '<em><b>Outlet Block</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.impl.OutletBlockImpl#getRows <em>Rows</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OutletBlockImpl extends FieldEquipmentImpl implements OutletBlock {
    /**
     * The cached value of the '{@link #getRows() <em>Rows</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRows()
     * @generated
     * @ordered
     */
    protected EList<OutletRow> rows;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected OutletBlockImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.OUTLET_BLOCK;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<OutletRow> getRows() {
        if (rows == null) {
            rows = new EObjectContainmentEList<OutletRow>(OutletRow.class, this, HomeElectricalPackage.OUTLET_BLOCK__ROWS);
        }
        return rows;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case HomeElectricalPackage.OUTLET_BLOCK__ROWS:
                return ((InternalEList<?>) getRows()).basicRemove(otherEnd, msgs);
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
            case HomeElectricalPackage.OUTLET_BLOCK__ROWS:
                return getRows();
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
            case HomeElectricalPackage.OUTLET_BLOCK__ROWS:
                getRows().clear();
                getRows().addAll((Collection<? extends OutletRow>) newValue);
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
            case HomeElectricalPackage.OUTLET_BLOCK__ROWS:
                getRows().clear();
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
            case HomeElectricalPackage.OUTLET_BLOCK__ROWS:
                return rows != null && !rows.isEmpty();
        }
        return super.eIsSet(featureID);
    }

} //OutletBlockImpl
