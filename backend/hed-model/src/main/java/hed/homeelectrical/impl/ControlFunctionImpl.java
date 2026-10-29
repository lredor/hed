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

import hed.homeelectrical.ControlChannel;
import hed.homeelectrical.ControlFunction;
import hed.homeelectrical.ControlMode;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.ModularDevice;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Control Function</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.impl.ControlFunctionImpl#getMode <em>Mode</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.ControlFunctionImpl#getChannels <em>Channels</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.ControlFunctionImpl#getTargets <em>Targets</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.ControlFunctionImpl#getActuator <em>Actuator</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ControlFunctionImpl extends NamedElementImpl implements ControlFunction {
    /**
     * The default value of the '{@link #getMode() <em>Mode</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMode()
     * @generated
     * @ordered
     */
    protected static final ControlMode MODE_EDEFAULT = ControlMode.SWITCHING;

    /**
     * The cached value of the '{@link #getMode() <em>Mode</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMode()
     * @generated
     * @ordered
     */
    protected ControlMode mode = MODE_EDEFAULT;

    /**
     * The cached value of the '{@link #getChannels() <em>Channels</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getChannels()
     * @generated
     * @ordered
     */
    protected EList<ControlChannel> channels;

    /**
     * The cached value of the '{@link #getTargets() <em>Targets</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTargets()
     * @generated
     * @ordered
     */
    protected EList<FieldEquipment> targets;

    /**
     * The cached value of the '{@link #getActuator() <em>Actuator</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getActuator()
     * @generated
     * @ordered
     */
    protected ModularDevice actuator;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ControlFunctionImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.CONTROL_FUNCTION;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ControlMode getMode() {
        return mode;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setMode(ControlMode newMode) {
        ControlMode oldMode = mode;
        mode = newMode == null ? MODE_EDEFAULT : newMode;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.CONTROL_FUNCTION__MODE, oldMode, mode));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<ControlChannel> getChannels() {
        if (channels == null) {
            channels = new EObjectResolvingEList<ControlChannel>(ControlChannel.class, this, HomeElectricalPackage.CONTROL_FUNCTION__CHANNELS);
        }
        return channels;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<FieldEquipment> getTargets() {
        if (targets == null) {
            targets = new EObjectResolvingEList<FieldEquipment>(FieldEquipment.class, this, HomeElectricalPackage.CONTROL_FUNCTION__TARGETS);
        }
        return targets;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ModularDevice getActuator() {
        if (actuator != null && actuator.eIsProxy()) {
            InternalEObject oldActuator = (InternalEObject) actuator;
            actuator = (ModularDevice) eResolveProxy(oldActuator);
            if (actuator != oldActuator) {
                if (eNotificationRequired()) {
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, HomeElectricalPackage.CONTROL_FUNCTION__ACTUATOR, oldActuator, actuator));
                }
            }
        }
        return actuator;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModularDevice basicGetActuator() {
        return actuator;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setActuator(ModularDevice newActuator) {
        ModularDevice oldActuator = actuator;
        actuator = newActuator;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.CONTROL_FUNCTION__ACTUATOR, oldActuator, actuator));
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
            case HomeElectricalPackage.CONTROL_FUNCTION__MODE:
                return getMode();
            case HomeElectricalPackage.CONTROL_FUNCTION__CHANNELS:
                return getChannels();
            case HomeElectricalPackage.CONTROL_FUNCTION__TARGETS:
                return getTargets();
            case HomeElectricalPackage.CONTROL_FUNCTION__ACTUATOR:
                if (resolve) {
                    return getActuator();
                }
                return basicGetActuator();
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
            case HomeElectricalPackage.CONTROL_FUNCTION__MODE:
                setMode((ControlMode) newValue);
                return;
            case HomeElectricalPackage.CONTROL_FUNCTION__CHANNELS:
                getChannels().clear();
                getChannels().addAll((Collection<? extends ControlChannel>) newValue);
                return;
            case HomeElectricalPackage.CONTROL_FUNCTION__TARGETS:
                getTargets().clear();
                getTargets().addAll((Collection<? extends FieldEquipment>) newValue);
                return;
            case HomeElectricalPackage.CONTROL_FUNCTION__ACTUATOR:
                setActuator((ModularDevice) newValue);
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
            case HomeElectricalPackage.CONTROL_FUNCTION__MODE:
                setMode(MODE_EDEFAULT);
                return;
            case HomeElectricalPackage.CONTROL_FUNCTION__CHANNELS:
                getChannels().clear();
                return;
            case HomeElectricalPackage.CONTROL_FUNCTION__TARGETS:
                getTargets().clear();
                return;
            case HomeElectricalPackage.CONTROL_FUNCTION__ACTUATOR:
                setActuator((ModularDevice) null);
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
            case HomeElectricalPackage.CONTROL_FUNCTION__MODE:
                return mode != MODE_EDEFAULT;
            case HomeElectricalPackage.CONTROL_FUNCTION__CHANNELS:
                return channels != null && !channels.isEmpty();
            case HomeElectricalPackage.CONTROL_FUNCTION__TARGETS:
                return targets != null && !targets.isEmpty();
            case HomeElectricalPackage.CONTROL_FUNCTION__ACTUATOR:
                return actuator != null;
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
        result.append(" (mode: ");
        result.append(mode);
        result.append(')');
        return result.toString();
    }

} //ControlFunctionImpl
