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

import hed.homeelectrical.Circuit;
import hed.homeelectrical.CircuitPhase;
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.ModularDevice;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Circuit</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.impl.CircuitImpl#getPhase <em>Phase</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.CircuitImpl#getConductorSectionMm2 <em>Conductor Section Mm2</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.CircuitImpl#getSourceBoard <em>Source Board</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.CircuitImpl#getProtectiveDevice <em>Protective Device</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class CircuitImpl extends NamedElementImpl implements Circuit {
    /**
     * The default value of the '{@link #getPhase() <em>Phase</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPhase()
     * @generated
     * @ordered
     */
    protected static final CircuitPhase PHASE_EDEFAULT = CircuitPhase.UNKNOWN;

    /**
     * The cached value of the '{@link #getPhase() <em>Phase</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPhase()
     * @generated
     * @ordered
     */
    protected CircuitPhase phase = PHASE_EDEFAULT;

    /**
     * The default value of the '{@link #getConductorSectionMm2() <em>Conductor Section Mm2</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getConductorSectionMm2()
     * @generated
     * @ordered
     */
    protected static final Double CONDUCTOR_SECTION_MM2_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getConductorSectionMm2() <em>Conductor Section Mm2</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getConductorSectionMm2()
     * @generated
     * @ordered
     */
    protected Double conductorSectionMm2 = CONDUCTOR_SECTION_MM2_EDEFAULT;

    /**
     * The cached value of the '{@link #getSourceBoard() <em>Source Board</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSourceBoard()
     * @generated
     * @ordered
     */
    protected DistributionBoard sourceBoard;

    /**
     * The cached value of the '{@link #getProtectiveDevice() <em>Protective Device</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProtectiveDevice()
     * @generated
     * @ordered
     */
    protected ModularDevice protectiveDevice;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected CircuitImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.CIRCUIT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public CircuitPhase getPhase() {
        return phase;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setPhase(CircuitPhase newPhase) {
        CircuitPhase oldPhase = phase;
        phase = newPhase == null ? PHASE_EDEFAULT : newPhase;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.CIRCUIT__PHASE, oldPhase, phase));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Double getConductorSectionMm2() {
        return conductorSectionMm2;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setConductorSectionMm2(Double newConductorSectionMm2) {
        Double oldConductorSectionMm2 = conductorSectionMm2;
        conductorSectionMm2 = newConductorSectionMm2;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.CIRCUIT__CONDUCTOR_SECTION_MM2, oldConductorSectionMm2, conductorSectionMm2));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public DistributionBoard getSourceBoard() {
        if (sourceBoard != null && sourceBoard.eIsProxy()) {
            InternalEObject oldSourceBoard = (InternalEObject) sourceBoard;
            sourceBoard = (DistributionBoard) eResolveProxy(oldSourceBoard);
            if (sourceBoard != oldSourceBoard) {
                if (eNotificationRequired()) {
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, HomeElectricalPackage.CIRCUIT__SOURCE_BOARD, oldSourceBoard, sourceBoard));
                }
            }
        }
        return sourceBoard;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DistributionBoard basicGetSourceBoard() {
        return sourceBoard;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setSourceBoard(DistributionBoard newSourceBoard) {
        DistributionBoard oldSourceBoard = sourceBoard;
        sourceBoard = newSourceBoard;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.CIRCUIT__SOURCE_BOARD, oldSourceBoard, sourceBoard));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ModularDevice getProtectiveDevice() {
        if (protectiveDevice != null && protectiveDevice.eIsProxy()) {
            InternalEObject oldProtectiveDevice = (InternalEObject) protectiveDevice;
            protectiveDevice = (ModularDevice) eResolveProxy(oldProtectiveDevice);
            if (protectiveDevice != oldProtectiveDevice) {
                if (eNotificationRequired()) {
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, HomeElectricalPackage.CIRCUIT__PROTECTIVE_DEVICE, oldProtectiveDevice, protectiveDevice));
                }
            }
        }
        return protectiveDevice;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModularDevice basicGetProtectiveDevice() {
        return protectiveDevice;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setProtectiveDevice(ModularDevice newProtectiveDevice) {
        ModularDevice oldProtectiveDevice = protectiveDevice;
        protectiveDevice = newProtectiveDevice;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.CIRCUIT__PROTECTIVE_DEVICE, oldProtectiveDevice, protectiveDevice));
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
            case HomeElectricalPackage.CIRCUIT__PHASE:
                return getPhase();
            case HomeElectricalPackage.CIRCUIT__CONDUCTOR_SECTION_MM2:
                return getConductorSectionMm2();
            case HomeElectricalPackage.CIRCUIT__SOURCE_BOARD:
                if (resolve) {
                    return getSourceBoard();
                }
                return basicGetSourceBoard();
            case HomeElectricalPackage.CIRCUIT__PROTECTIVE_DEVICE:
                if (resolve) {
                    return getProtectiveDevice();
                }
                return basicGetProtectiveDevice();
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
            case HomeElectricalPackage.CIRCUIT__PHASE:
                setPhase((CircuitPhase) newValue);
                return;
            case HomeElectricalPackage.CIRCUIT__CONDUCTOR_SECTION_MM2:
                setConductorSectionMm2((Double) newValue);
                return;
            case HomeElectricalPackage.CIRCUIT__SOURCE_BOARD:
                setSourceBoard((DistributionBoard) newValue);
                return;
            case HomeElectricalPackage.CIRCUIT__PROTECTIVE_DEVICE:
                setProtectiveDevice((ModularDevice) newValue);
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
            case HomeElectricalPackage.CIRCUIT__PHASE:
                setPhase(PHASE_EDEFAULT);
                return;
            case HomeElectricalPackage.CIRCUIT__CONDUCTOR_SECTION_MM2:
                setConductorSectionMm2(CONDUCTOR_SECTION_MM2_EDEFAULT);
                return;
            case HomeElectricalPackage.CIRCUIT__SOURCE_BOARD:
                setSourceBoard((DistributionBoard) null);
                return;
            case HomeElectricalPackage.CIRCUIT__PROTECTIVE_DEVICE:
                setProtectiveDevice((ModularDevice) null);
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
            case HomeElectricalPackage.CIRCUIT__PHASE:
                return phase != PHASE_EDEFAULT;
            case HomeElectricalPackage.CIRCUIT__CONDUCTOR_SECTION_MM2:
                return CONDUCTOR_SECTION_MM2_EDEFAULT == null ? conductorSectionMm2 != null : !CONDUCTOR_SECTION_MM2_EDEFAULT.equals(conductorSectionMm2);
            case HomeElectricalPackage.CIRCUIT__SOURCE_BOARD:
                return sourceBoard != null;
            case HomeElectricalPackage.CIRCUIT__PROTECTIVE_DEVICE:
                return protectiveDevice != null;
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
        result.append(" (phase: ");
        result.append(phase);
        result.append(", conductorSectionMm2: ");
        result.append(conductorSectionMm2);
        result.append(')');
        return result.toString();
    }

} //CircuitImpl
