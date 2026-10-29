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
import hed.homeelectrical.ServiceCircuitBreaker;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Modular Device</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.impl.ModularDeviceImpl#getLabelText <em>Label Text</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.ModularDeviceImpl#getRail <em>Rail</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.ModularDeviceImpl#getUpstreamRccb <em>Upstream Rccb</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.ModularDeviceImpl#getUpstreamServiceBreaker <em>Upstream Service Breaker</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ModularDeviceImpl extends EquipmentImpl implements ModularDevice {
    /**
     * The default value of the '{@link #getLabelText() <em>Label Text</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLabelText()
     * @generated
     * @ordered
     */
    protected static final String LABEL_TEXT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLabelText() <em>Label Text</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLabelText()
     * @generated
     * @ordered
     */
    protected String labelText = LABEL_TEXT_EDEFAULT;

    /**
     * The cached value of the '{@link #getRail() <em>Rail</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRail()
     * @generated
     * @ordered
     */
    protected DINRail rail;

    /**
     * The cached value of the '{@link #getUpstreamRccb() <em>Upstream Rccb</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUpstreamRccb()
     * @generated
     * @ordered
     */
    protected ModularDevice upstreamRccb;

    /**
     * The cached value of the '{@link #getUpstreamServiceBreaker() <em>Upstream Service Breaker</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUpstreamServiceBreaker()
     * @generated
     * @ordered
     */
    protected ServiceCircuitBreaker upstreamServiceBreaker;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ModularDeviceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.MODULAR_DEVICE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getLabelText() {
        return labelText;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setLabelText(String newLabelText) {
        String oldLabelText = labelText;
        labelText = newLabelText;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.MODULAR_DEVICE__LABEL_TEXT, oldLabelText, labelText));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public DINRail getRail() {
        if (rail != null && rail.eIsProxy()) {
            InternalEObject oldRail = (InternalEObject) rail;
            rail = (DINRail) eResolveProxy(oldRail);
            if (rail != oldRail) {
                if (eNotificationRequired()) {
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, HomeElectricalPackage.MODULAR_DEVICE__RAIL, oldRail, rail));
                }
            }
        }
        return rail;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DINRail basicGetRail() {
        return rail;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetRail(DINRail newRail, NotificationChain msgs) {
        DINRail oldRail = rail;
        rail = newRail;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.MODULAR_DEVICE__RAIL, oldRail, newRail);
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
    public void setRail(DINRail newRail) {
        if (newRail != rail) {
            NotificationChain msgs = null;
            if (rail != null) {
                msgs = ((InternalEObject) rail).eInverseRemove(this, HomeElectricalPackage.DIN_RAIL__DEVICES, DINRail.class, msgs);
            }
            if (newRail != null) {
                msgs = ((InternalEObject) newRail).eInverseAdd(this, HomeElectricalPackage.DIN_RAIL__DEVICES, DINRail.class, msgs);
            }
            msgs = basicSetRail(newRail, msgs);
            if (msgs != null) {
                msgs.dispatch();
            }
        } else if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.MODULAR_DEVICE__RAIL, newRail, newRail));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ModularDevice getUpstreamRccb() {
        if (upstreamRccb != null && upstreamRccb.eIsProxy()) {
            InternalEObject oldUpstreamRccb = (InternalEObject) upstreamRccb;
            upstreamRccb = (ModularDevice) eResolveProxy(oldUpstreamRccb);
            if (upstreamRccb != oldUpstreamRccb) {
                if (eNotificationRequired()) {
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, HomeElectricalPackage.MODULAR_DEVICE__UPSTREAM_RCCB, oldUpstreamRccb, upstreamRccb));
                }
            }
        }
        return upstreamRccb;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModularDevice basicGetUpstreamRccb() {
        return upstreamRccb;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setUpstreamRccb(ModularDevice newUpstreamRccb) {
        ModularDevice oldUpstreamRccb = upstreamRccb;
        upstreamRccb = newUpstreamRccb;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.MODULAR_DEVICE__UPSTREAM_RCCB, oldUpstreamRccb, upstreamRccb));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ServiceCircuitBreaker getUpstreamServiceBreaker() {
        if (upstreamServiceBreaker != null && upstreamServiceBreaker.eIsProxy()) {
            InternalEObject oldUpstreamServiceBreaker = (InternalEObject) upstreamServiceBreaker;
            upstreamServiceBreaker = (ServiceCircuitBreaker) eResolveProxy(oldUpstreamServiceBreaker);
            if (upstreamServiceBreaker != oldUpstreamServiceBreaker) {
                if (eNotificationRequired()) {
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, HomeElectricalPackage.MODULAR_DEVICE__UPSTREAM_SERVICE_BREAKER, oldUpstreamServiceBreaker, upstreamServiceBreaker));
                }
            }
        }
        return upstreamServiceBreaker;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ServiceCircuitBreaker basicGetUpstreamServiceBreaker() {
        return upstreamServiceBreaker;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetUpstreamServiceBreaker(ServiceCircuitBreaker newUpstreamServiceBreaker, NotificationChain msgs) {
        ServiceCircuitBreaker oldUpstreamServiceBreaker = upstreamServiceBreaker;
        upstreamServiceBreaker = newUpstreamServiceBreaker;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.MODULAR_DEVICE__UPSTREAM_SERVICE_BREAKER, oldUpstreamServiceBreaker,
                    newUpstreamServiceBreaker);
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
    public void setUpstreamServiceBreaker(ServiceCircuitBreaker newUpstreamServiceBreaker) {
        if (newUpstreamServiceBreaker != upstreamServiceBreaker) {
            NotificationChain msgs = null;
            if (upstreamServiceBreaker != null) {
                msgs = ((InternalEObject) upstreamServiceBreaker).eInverseRemove(this, HomeElectricalPackage.SERVICE_CIRCUIT_BREAKER__DOWNSTREAM_DEVICES, ServiceCircuitBreaker.class, msgs);
            }
            if (newUpstreamServiceBreaker != null) {
                msgs = ((InternalEObject) newUpstreamServiceBreaker).eInverseAdd(this, HomeElectricalPackage.SERVICE_CIRCUIT_BREAKER__DOWNSTREAM_DEVICES, ServiceCircuitBreaker.class, msgs);
            }
            msgs = basicSetUpstreamServiceBreaker(newUpstreamServiceBreaker, msgs);
            if (msgs != null) {
                msgs.dispatch();
            }
        } else if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.MODULAR_DEVICE__UPSTREAM_SERVICE_BREAKER, newUpstreamServiceBreaker, newUpstreamServiceBreaker));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case HomeElectricalPackage.MODULAR_DEVICE__RAIL:
                if (rail != null) {
                    msgs = ((InternalEObject) rail).eInverseRemove(this, HomeElectricalPackage.DIN_RAIL__DEVICES, DINRail.class, msgs);
                }
                return basicSetRail((DINRail) otherEnd, msgs);
            case HomeElectricalPackage.MODULAR_DEVICE__UPSTREAM_SERVICE_BREAKER:
                if (upstreamServiceBreaker != null) {
                    msgs = ((InternalEObject) upstreamServiceBreaker).eInverseRemove(this, HomeElectricalPackage.SERVICE_CIRCUIT_BREAKER__DOWNSTREAM_DEVICES, ServiceCircuitBreaker.class, msgs);
                }
                return basicSetUpstreamServiceBreaker((ServiceCircuitBreaker) otherEnd, msgs);
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
            case HomeElectricalPackage.MODULAR_DEVICE__RAIL:
                return basicSetRail(null, msgs);
            case HomeElectricalPackage.MODULAR_DEVICE__UPSTREAM_SERVICE_BREAKER:
                return basicSetUpstreamServiceBreaker(null, msgs);
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
            case HomeElectricalPackage.MODULAR_DEVICE__LABEL_TEXT:
                return getLabelText();
            case HomeElectricalPackage.MODULAR_DEVICE__RAIL:
                if (resolve) {
                    return getRail();
                }
                return basicGetRail();
            case HomeElectricalPackage.MODULAR_DEVICE__UPSTREAM_RCCB:
                if (resolve) {
                    return getUpstreamRccb();
                }
                return basicGetUpstreamRccb();
            case HomeElectricalPackage.MODULAR_DEVICE__UPSTREAM_SERVICE_BREAKER:
                if (resolve) {
                    return getUpstreamServiceBreaker();
                }
                return basicGetUpstreamServiceBreaker();
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
            case HomeElectricalPackage.MODULAR_DEVICE__LABEL_TEXT:
                setLabelText((String) newValue);
                return;
            case HomeElectricalPackage.MODULAR_DEVICE__RAIL:
                setRail((DINRail) newValue);
                return;
            case HomeElectricalPackage.MODULAR_DEVICE__UPSTREAM_RCCB:
                setUpstreamRccb((ModularDevice) newValue);
                return;
            case HomeElectricalPackage.MODULAR_DEVICE__UPSTREAM_SERVICE_BREAKER:
                setUpstreamServiceBreaker((ServiceCircuitBreaker) newValue);
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
            case HomeElectricalPackage.MODULAR_DEVICE__LABEL_TEXT:
                setLabelText(LABEL_TEXT_EDEFAULT);
                return;
            case HomeElectricalPackage.MODULAR_DEVICE__RAIL:
                setRail((DINRail) null);
                return;
            case HomeElectricalPackage.MODULAR_DEVICE__UPSTREAM_RCCB:
                setUpstreamRccb((ModularDevice) null);
                return;
            case HomeElectricalPackage.MODULAR_DEVICE__UPSTREAM_SERVICE_BREAKER:
                setUpstreamServiceBreaker((ServiceCircuitBreaker) null);
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
            case HomeElectricalPackage.MODULAR_DEVICE__LABEL_TEXT:
                return LABEL_TEXT_EDEFAULT == null ? labelText != null : !LABEL_TEXT_EDEFAULT.equals(labelText);
            case HomeElectricalPackage.MODULAR_DEVICE__RAIL:
                return rail != null;
            case HomeElectricalPackage.MODULAR_DEVICE__UPSTREAM_RCCB:
                return upstreamRccb != null;
            case HomeElectricalPackage.MODULAR_DEVICE__UPSTREAM_SERVICE_BREAKER:
                return upstreamServiceBreaker != null;
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
        result.append(" (labelText: ");
        result.append(labelText);
        result.append(')');
        return result.toString();
    }

} //ModularDeviceImpl
