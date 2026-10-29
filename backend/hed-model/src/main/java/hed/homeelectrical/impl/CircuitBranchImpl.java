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
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.HomeElectricalPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Circuit Branch</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.impl.CircuitBranchImpl#getCircuit <em>Circuit</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.CircuitBranchImpl#getEquipment <em>Equipment</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CircuitBranchImpl extends NamedElementImpl implements CircuitBranch {
    /**
     * The cached value of the '{@link #getEquipment() <em>Equipment</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEquipment()
     * @generated
     * @ordered
     */
    protected EList<FieldEquipment> equipment;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected CircuitBranchImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.CIRCUIT_BRANCH;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public FinalCircuit getCircuit() {
        if (eContainerFeatureID() != HomeElectricalPackage.CIRCUIT_BRANCH__CIRCUIT) {
            return null;
        }
        return (FinalCircuit) eInternalContainer();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetCircuit(FinalCircuit newCircuit, NotificationChain msgs) {
        msgs = eBasicSetContainer((InternalEObject) newCircuit, HomeElectricalPackage.CIRCUIT_BRANCH__CIRCUIT, msgs);
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setCircuit(FinalCircuit newCircuit) {
        if (newCircuit != eInternalContainer() || (eContainerFeatureID() != HomeElectricalPackage.CIRCUIT_BRANCH__CIRCUIT && newCircuit != null)) {
            if (EcoreUtil.isAncestor(this, newCircuit)) {
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
            }
            NotificationChain msgs = null;
            if (eInternalContainer() != null) {
                msgs = eBasicRemoveFromContainer(msgs);
            }
            if (newCircuit != null) {
                msgs = ((InternalEObject) newCircuit).eInverseAdd(this, HomeElectricalPackage.FINAL_CIRCUIT__BRANCHES, FinalCircuit.class, msgs);
            }
            msgs = basicSetCircuit(newCircuit, msgs);
            if (msgs != null) {
                msgs.dispatch();
            }
        } else if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.CIRCUIT_BRANCH__CIRCUIT, newCircuit, newCircuit));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<FieldEquipment> getEquipment() {
        if (equipment == null) {
            equipment = new EObjectWithInverseResolvingEList<FieldEquipment>(FieldEquipment.class, this, HomeElectricalPackage.CIRCUIT_BRANCH__EQUIPMENT,
                    HomeElectricalPackage.FIELD_EQUIPMENT__BRANCH);
        }
        return equipment;
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
            case HomeElectricalPackage.CIRCUIT_BRANCH__CIRCUIT:
                if (eInternalContainer() != null) {
                    msgs = eBasicRemoveFromContainer(msgs);
                }
                return basicSetCircuit((FinalCircuit) otherEnd, msgs);
            case HomeElectricalPackage.CIRCUIT_BRANCH__EQUIPMENT:
                return ((InternalEList<InternalEObject>) (InternalEList<?>) getEquipment()).basicAdd(otherEnd, msgs);
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
            case HomeElectricalPackage.CIRCUIT_BRANCH__CIRCUIT:
                return basicSetCircuit(null, msgs);
            case HomeElectricalPackage.CIRCUIT_BRANCH__EQUIPMENT:
                return ((InternalEList<?>) getEquipment()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
        switch (eContainerFeatureID()) {
            case HomeElectricalPackage.CIRCUIT_BRANCH__CIRCUIT:
                return eInternalContainer().eInverseRemove(this, HomeElectricalPackage.FINAL_CIRCUIT__BRANCHES, FinalCircuit.class, msgs);
        }
        return super.eBasicRemoveFromContainerFeature(msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case HomeElectricalPackage.CIRCUIT_BRANCH__CIRCUIT:
                return getCircuit();
            case HomeElectricalPackage.CIRCUIT_BRANCH__EQUIPMENT:
                return getEquipment();
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
            case HomeElectricalPackage.CIRCUIT_BRANCH__CIRCUIT:
                setCircuit((FinalCircuit) newValue);
                return;
            case HomeElectricalPackage.CIRCUIT_BRANCH__EQUIPMENT:
                getEquipment().clear();
                getEquipment().addAll((Collection<? extends FieldEquipment>) newValue);
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
            case HomeElectricalPackage.CIRCUIT_BRANCH__CIRCUIT:
                setCircuit((FinalCircuit) null);
                return;
            case HomeElectricalPackage.CIRCUIT_BRANCH__EQUIPMENT:
                getEquipment().clear();
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
            case HomeElectricalPackage.CIRCUIT_BRANCH__CIRCUIT:
                return getCircuit() != null;
            case HomeElectricalPackage.CIRCUIT_BRANCH__EQUIPMENT:
                return equipment != null && !equipment.isEmpty();
        }
        return super.eIsSet(featureID);
    }

} //CircuitBranchImpl
