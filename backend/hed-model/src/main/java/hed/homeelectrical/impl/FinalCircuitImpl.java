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

import hed.homeelectrical.CircuitBranch;
import hed.homeelectrical.CircuitUse;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.HomeElectricalPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Final Circuit</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.impl.FinalCircuitImpl#getUsage <em>Usage</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.FinalCircuitImpl#getBranches <em>Branches</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FinalCircuitImpl extends CircuitImpl implements FinalCircuit {
    /**
     * The default value of the '{@link #getUsage() <em>Usage</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUsage()
     * @generated
     * @ordered
     */
    protected static final CircuitUse USAGE_EDEFAULT = CircuitUse.UNKNOWN;

    /**
     * The cached value of the '{@link #getUsage() <em>Usage</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUsage()
     * @generated
     * @ordered
     */
    protected CircuitUse usage = USAGE_EDEFAULT;

    /**
     * The cached value of the '{@link #getBranches() <em>Branches</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBranches()
     * @generated
     * @ordered
     */
    protected EList<CircuitBranch> branches;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected FinalCircuitImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.FINAL_CIRCUIT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public CircuitUse getUsage() {
        return usage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setUsage(CircuitUse newUsage) {
        CircuitUse oldUsage = usage;
        usage = newUsage == null ? USAGE_EDEFAULT : newUsage;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.FINAL_CIRCUIT__USAGE, oldUsage, usage));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<CircuitBranch> getBranches() {
        if (branches == null) {
            branches = new EObjectContainmentWithInverseEList<CircuitBranch>(CircuitBranch.class, this, HomeElectricalPackage.FINAL_CIRCUIT__BRANCHES, HomeElectricalPackage.CIRCUIT_BRANCH__CIRCUIT);
        }
        return branches;
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
            case HomeElectricalPackage.FINAL_CIRCUIT__BRANCHES:
                return ((InternalEList<InternalEObject>) (InternalEList<?>) getBranches()).basicAdd(otherEnd, msgs);
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
            case HomeElectricalPackage.FINAL_CIRCUIT__BRANCHES:
                return ((InternalEList<?>) getBranches()).basicRemove(otherEnd, msgs);
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
            case HomeElectricalPackage.FINAL_CIRCUIT__USAGE:
                return getUsage();
            case HomeElectricalPackage.FINAL_CIRCUIT__BRANCHES:
                return getBranches();
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
            case HomeElectricalPackage.FINAL_CIRCUIT__USAGE:
                setUsage((CircuitUse) newValue);
                return;
            case HomeElectricalPackage.FINAL_CIRCUIT__BRANCHES:
                getBranches().clear();
                getBranches().addAll((Collection<? extends CircuitBranch>) newValue);
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
            case HomeElectricalPackage.FINAL_CIRCUIT__USAGE:
                setUsage(USAGE_EDEFAULT);
                return;
            case HomeElectricalPackage.FINAL_CIRCUIT__BRANCHES:
                getBranches().clear();
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
            case HomeElectricalPackage.FINAL_CIRCUIT__USAGE:
                return usage != USAGE_EDEFAULT;
            case HomeElectricalPackage.FINAL_CIRCUIT__BRANCHES:
                return branches != null && !branches.isEmpty();
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
        result.append(" (usage: ");
        result.append(usage);
        result.append(')');
        return result.toString();
    }

} //FinalCircuitImpl
