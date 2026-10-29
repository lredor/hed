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

import hed.homeelectrical.EnergyMeter;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.ServiceCircuitBreaker;
import hed.homeelectrical.ServiceConnection;
import hed.homeelectrical.SupplyPhase;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Service Connection</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.impl.ServiceConnectionImpl#getPhase <em>Phase</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.ServiceConnectionImpl#getSubscribedPowerKva <em>Subscribed Power Kva</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.ServiceConnectionImpl#getMeter <em>Meter</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.ServiceConnectionImpl#getMainBreaker <em>Main Breaker</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ServiceConnectionImpl extends NamedElementImpl implements ServiceConnection {
    /**
     * The default value of the '{@link #getPhase() <em>Phase</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPhase()
     * @generated
     * @ordered
     */
    protected static final SupplyPhase PHASE_EDEFAULT = SupplyPhase.UNKNOWN;

    /**
     * The cached value of the '{@link #getPhase() <em>Phase</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPhase()
     * @generated
     * @ordered
     */
    protected SupplyPhase phase = PHASE_EDEFAULT;

    /**
     * The default value of the '{@link #getSubscribedPowerKva() <em>Subscribed Power Kva</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSubscribedPowerKva()
     * @generated
     * @ordered
     */
    protected static final Double SUBSCRIBED_POWER_KVA_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getSubscribedPowerKva() <em>Subscribed Power Kva</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSubscribedPowerKva()
     * @generated
     * @ordered
     */
    protected Double subscribedPowerKva = SUBSCRIBED_POWER_KVA_EDEFAULT;

    /**
     * The cached value of the '{@link #getMeter() <em>Meter</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMeter()
     * @generated
     * @ordered
     */
    protected EnergyMeter meter;

    /**
     * The cached value of the '{@link #getMainBreaker() <em>Main Breaker</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMainBreaker()
     * @generated
     * @ordered
     */
    protected ServiceCircuitBreaker mainBreaker;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ServiceConnectionImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.SERVICE_CONNECTION;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public SupplyPhase getPhase() {
        return phase;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setPhase(SupplyPhase newPhase) {
        SupplyPhase oldPhase = phase;
        phase = newPhase == null ? PHASE_EDEFAULT : newPhase;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.SERVICE_CONNECTION__PHASE, oldPhase, phase));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Double getSubscribedPowerKva() {
        return subscribedPowerKva;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setSubscribedPowerKva(Double newSubscribedPowerKva) {
        Double oldSubscribedPowerKva = subscribedPowerKva;
        subscribedPowerKva = newSubscribedPowerKva;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.SERVICE_CONNECTION__SUBSCRIBED_POWER_KVA, oldSubscribedPowerKva, subscribedPowerKva));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EnergyMeter getMeter() {
        return meter;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetMeter(EnergyMeter newMeter, NotificationChain msgs) {
        EnergyMeter oldMeter = meter;
        meter = newMeter;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.SERVICE_CONNECTION__METER, oldMeter, newMeter);
            if (msgs == null) {
                msgs = notification;
            } else {
                msgs.add(notification);
            }
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setMeter(EnergyMeter newMeter) {
        if (newMeter != meter) {
            NotificationChain msgs = null;
            if (meter != null) {
                msgs = ((InternalEObject) meter).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - HomeElectricalPackage.SERVICE_CONNECTION__METER, null, msgs);
            }
            if (newMeter != null) {
                msgs = ((InternalEObject) newMeter).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - HomeElectricalPackage.SERVICE_CONNECTION__METER, null, msgs);
            }
            msgs = basicSetMeter(newMeter, msgs);
            if (msgs != null) {
                msgs.dispatch();
            }
        } else if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.SERVICE_CONNECTION__METER, newMeter, newMeter));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ServiceCircuitBreaker getMainBreaker() {
        return mainBreaker;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetMainBreaker(ServiceCircuitBreaker newMainBreaker, NotificationChain msgs) {
        ServiceCircuitBreaker oldMainBreaker = mainBreaker;
        mainBreaker = newMainBreaker;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.SERVICE_CONNECTION__MAIN_BREAKER, oldMainBreaker, newMainBreaker);
            if (msgs == null) {
                msgs = notification;
            } else {
                msgs.add(notification);
            }
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setMainBreaker(ServiceCircuitBreaker newMainBreaker) {
        if (newMainBreaker != mainBreaker) {
            NotificationChain msgs = null;
            if (mainBreaker != null) {
                msgs = ((InternalEObject) mainBreaker).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - HomeElectricalPackage.SERVICE_CONNECTION__MAIN_BREAKER, null, msgs);
            }
            if (newMainBreaker != null) {
                msgs = ((InternalEObject) newMainBreaker).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - HomeElectricalPackage.SERVICE_CONNECTION__MAIN_BREAKER, null, msgs);
            }
            msgs = basicSetMainBreaker(newMainBreaker, msgs);
            if (msgs != null) {
                msgs.dispatch();
            }
        } else if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.SERVICE_CONNECTION__MAIN_BREAKER, newMainBreaker, newMainBreaker));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case HomeElectricalPackage.SERVICE_CONNECTION__METER:
                return basicSetMeter(null, msgs);
            case HomeElectricalPackage.SERVICE_CONNECTION__MAIN_BREAKER:
                return basicSetMainBreaker(null, msgs);
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
            case HomeElectricalPackage.SERVICE_CONNECTION__PHASE:
                return getPhase();
            case HomeElectricalPackage.SERVICE_CONNECTION__SUBSCRIBED_POWER_KVA:
                return getSubscribedPowerKva();
            case HomeElectricalPackage.SERVICE_CONNECTION__METER:
                return getMeter();
            case HomeElectricalPackage.SERVICE_CONNECTION__MAIN_BREAKER:
                return getMainBreaker();
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
            case HomeElectricalPackage.SERVICE_CONNECTION__PHASE:
                setPhase((SupplyPhase) newValue);
                return;
            case HomeElectricalPackage.SERVICE_CONNECTION__SUBSCRIBED_POWER_KVA:
                setSubscribedPowerKva((Double) newValue);
                return;
            case HomeElectricalPackage.SERVICE_CONNECTION__METER:
                setMeter((EnergyMeter) newValue);
                return;
            case HomeElectricalPackage.SERVICE_CONNECTION__MAIN_BREAKER:
                setMainBreaker((ServiceCircuitBreaker) newValue);
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
            case HomeElectricalPackage.SERVICE_CONNECTION__PHASE:
                setPhase(PHASE_EDEFAULT);
                return;
            case HomeElectricalPackage.SERVICE_CONNECTION__SUBSCRIBED_POWER_KVA:
                setSubscribedPowerKva(SUBSCRIBED_POWER_KVA_EDEFAULT);
                return;
            case HomeElectricalPackage.SERVICE_CONNECTION__METER:
                setMeter((EnergyMeter) null);
                return;
            case HomeElectricalPackage.SERVICE_CONNECTION__MAIN_BREAKER:
                setMainBreaker((ServiceCircuitBreaker) null);
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
            case HomeElectricalPackage.SERVICE_CONNECTION__PHASE:
                return phase != PHASE_EDEFAULT;
            case HomeElectricalPackage.SERVICE_CONNECTION__SUBSCRIBED_POWER_KVA:
                return SUBSCRIBED_POWER_KVA_EDEFAULT == null ? subscribedPowerKva != null : !SUBSCRIBED_POWER_KVA_EDEFAULT.equals(subscribedPowerKva);
            case HomeElectricalPackage.SERVICE_CONNECTION__METER:
                return meter != null;
            case HomeElectricalPackage.SERVICE_CONNECTION__MAIN_BREAKER:
                return mainBreaker != null;
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
        result.append(", subscribedPowerKva: ");
        result.append(subscribedPowerKva);
        result.append(')');
        return result.toString();
    }

} //ServiceConnectionImpl
