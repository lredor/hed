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

import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.DistributionCircuit;
import hed.homeelectrical.HomeElectricalPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Distribution Circuit</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.impl.DistributionCircuitImpl#getTargetBoard <em>Target Board</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DistributionCircuitImpl extends CircuitImpl implements DistributionCircuit {
    /**
     * The cached value of the '{@link #getTargetBoard() <em>Target Board</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTargetBoard()
     * @generated
     * @ordered
     */
    protected DistributionBoard targetBoard;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DistributionCircuitImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.DISTRIBUTION_CIRCUIT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public DistributionBoard getTargetBoard() {
        if (targetBoard != null && targetBoard.eIsProxy()) {
            InternalEObject oldTargetBoard = (InternalEObject) targetBoard;
            targetBoard = (DistributionBoard) eResolveProxy(oldTargetBoard);
            if (targetBoard != oldTargetBoard) {
                if (eNotificationRequired()) {
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, HomeElectricalPackage.DISTRIBUTION_CIRCUIT__TARGET_BOARD, oldTargetBoard, targetBoard));
                }
            }
        }
        return targetBoard;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DistributionBoard basicGetTargetBoard() {
        return targetBoard;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setTargetBoard(DistributionBoard newTargetBoard) {
        DistributionBoard oldTargetBoard = targetBoard;
        targetBoard = newTargetBoard;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.DISTRIBUTION_CIRCUIT__TARGET_BOARD, oldTargetBoard, targetBoard));
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
            case HomeElectricalPackage.DISTRIBUTION_CIRCUIT__TARGET_BOARD:
                if (resolve) {
                    return getTargetBoard();
                }
                return basicGetTargetBoard();
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
            case HomeElectricalPackage.DISTRIBUTION_CIRCUIT__TARGET_BOARD:
                setTargetBoard((DistributionBoard) newValue);
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
            case HomeElectricalPackage.DISTRIBUTION_CIRCUIT__TARGET_BOARD:
                setTargetBoard((DistributionBoard) null);
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
            case HomeElectricalPackage.DISTRIBUTION_CIRCUIT__TARGET_BOARD:
                return targetBoard != null;
        }
        return super.eIsSet(featureID);
    }

} //DistributionCircuitImpl
