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
import hed.homeelectrical.TechnicalElement;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Technical Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.impl.TechnicalElementImpl#getRatedCurrentA <em>Rated Current A</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.TechnicalElementImpl#getRatedPowerW <em>Rated Power W</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.TechnicalElementImpl#getWidthModules <em>Width Modules</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.TechnicalElementImpl#getResidualCurrentMa <em>Residual Current Ma</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.TechnicalElementImpl#getResidualType <em>Residual Type</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.TechnicalElementImpl#getTripCurve <em>Trip Curve</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.TechnicalElementImpl#getPoleConfiguration <em>Pole Configuration</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class TechnicalElementImpl extends NamedElementImpl implements TechnicalElement {
    /**
     * The default value of the '{@link #getRatedCurrentA() <em>Rated Current A</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRatedCurrentA()
     * @generated
     * @ordered
     */
    protected static final Double RATED_CURRENT_A_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getRatedCurrentA() <em>Rated Current A</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRatedCurrentA()
     * @generated
     * @ordered
     */
    protected Double ratedCurrentA = RATED_CURRENT_A_EDEFAULT;

    /**
     * The default value of the '{@link #getRatedPowerW() <em>Rated Power W</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRatedPowerW()
     * @generated
     * @ordered
     */
    protected static final Double RATED_POWER_W_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getRatedPowerW() <em>Rated Power W</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRatedPowerW()
     * @generated
     * @ordered
     */
    protected Double ratedPowerW = RATED_POWER_W_EDEFAULT;

    /**
     * The default value of the '{@link #getWidthModules() <em>Width Modules</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWidthModules()
     * @generated
     * @ordered
     */
    protected static final Double WIDTH_MODULES_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getWidthModules() <em>Width Modules</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWidthModules()
     * @generated
     * @ordered
     */
    protected Double widthModules = WIDTH_MODULES_EDEFAULT;

    /**
     * The default value of the '{@link #getResidualCurrentMa() <em>Residual Current Ma</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getResidualCurrentMa()
     * @generated
     * @ordered
     */
    protected static final Double RESIDUAL_CURRENT_MA_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getResidualCurrentMa() <em>Residual Current Ma</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getResidualCurrentMa()
     * @generated
     * @ordered
     */
    protected Double residualCurrentMa = RESIDUAL_CURRENT_MA_EDEFAULT;

    /**
     * The default value of the '{@link #getResidualType() <em>Residual Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getResidualType()
     * @generated
     * @ordered
     */
    protected static final String RESIDUAL_TYPE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getResidualType() <em>Residual Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getResidualType()
     * @generated
     * @ordered
     */
    protected String residualType = RESIDUAL_TYPE_EDEFAULT;

    /**
     * The default value of the '{@link #getTripCurve() <em>Trip Curve</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTripCurve()
     * @generated
     * @ordered
     */
    protected static final String TRIP_CURVE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getTripCurve() <em>Trip Curve</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTripCurve()
     * @generated
     * @ordered
     */
    protected String tripCurve = TRIP_CURVE_EDEFAULT;

    /**
     * The default value of the '{@link #getPoleConfiguration() <em>Pole Configuration</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPoleConfiguration()
     * @generated
     * @ordered
     */
    protected static final String POLE_CONFIGURATION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPoleConfiguration() <em>Pole Configuration</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPoleConfiguration()
     * @generated
     * @ordered
     */
    protected String poleConfiguration = POLE_CONFIGURATION_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TechnicalElementImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.TECHNICAL_ELEMENT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Double getRatedCurrentA() {
        return ratedCurrentA;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setRatedCurrentA(Double newRatedCurrentA) {
        Double oldRatedCurrentA = ratedCurrentA;
        ratedCurrentA = newRatedCurrentA;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.TECHNICAL_ELEMENT__RATED_CURRENT_A, oldRatedCurrentA, ratedCurrentA));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Double getRatedPowerW() {
        return ratedPowerW;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setRatedPowerW(Double newRatedPowerW) {
        Double oldRatedPowerW = ratedPowerW;
        ratedPowerW = newRatedPowerW;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.TECHNICAL_ELEMENT__RATED_POWER_W, oldRatedPowerW, ratedPowerW));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Double getWidthModules() {
        return widthModules;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setWidthModules(Double newWidthModules) {
        Double oldWidthModules = widthModules;
        widthModules = newWidthModules;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.TECHNICAL_ELEMENT__WIDTH_MODULES, oldWidthModules, widthModules));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Double getResidualCurrentMa() {
        return residualCurrentMa;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setResidualCurrentMa(Double newResidualCurrentMa) {
        Double oldResidualCurrentMa = residualCurrentMa;
        residualCurrentMa = newResidualCurrentMa;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.TECHNICAL_ELEMENT__RESIDUAL_CURRENT_MA, oldResidualCurrentMa, residualCurrentMa));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getResidualType() {
        return residualType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setResidualType(String newResidualType) {
        String oldResidualType = residualType;
        residualType = newResidualType;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.TECHNICAL_ELEMENT__RESIDUAL_TYPE, oldResidualType, residualType));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getTripCurve() {
        return tripCurve;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setTripCurve(String newTripCurve) {
        String oldTripCurve = tripCurve;
        tripCurve = newTripCurve;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.TECHNICAL_ELEMENT__TRIP_CURVE, oldTripCurve, tripCurve));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getPoleConfiguration() {
        return poleConfiguration;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setPoleConfiguration(String newPoleConfiguration) {
        String oldPoleConfiguration = poleConfiguration;
        poleConfiguration = newPoleConfiguration;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.TECHNICAL_ELEMENT__POLE_CONFIGURATION, oldPoleConfiguration, poleConfiguration));
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
            case HomeElectricalPackage.TECHNICAL_ELEMENT__RATED_CURRENT_A:
                return getRatedCurrentA();
            case HomeElectricalPackage.TECHNICAL_ELEMENT__RATED_POWER_W:
                return getRatedPowerW();
            case HomeElectricalPackage.TECHNICAL_ELEMENT__WIDTH_MODULES:
                return getWidthModules();
            case HomeElectricalPackage.TECHNICAL_ELEMENT__RESIDUAL_CURRENT_MA:
                return getResidualCurrentMa();
            case HomeElectricalPackage.TECHNICAL_ELEMENT__RESIDUAL_TYPE:
                return getResidualType();
            case HomeElectricalPackage.TECHNICAL_ELEMENT__TRIP_CURVE:
                return getTripCurve();
            case HomeElectricalPackage.TECHNICAL_ELEMENT__POLE_CONFIGURATION:
                return getPoleConfiguration();
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
            case HomeElectricalPackage.TECHNICAL_ELEMENT__RATED_CURRENT_A:
                setRatedCurrentA((Double) newValue);
                return;
            case HomeElectricalPackage.TECHNICAL_ELEMENT__RATED_POWER_W:
                setRatedPowerW((Double) newValue);
                return;
            case HomeElectricalPackage.TECHNICAL_ELEMENT__WIDTH_MODULES:
                setWidthModules((Double) newValue);
                return;
            case HomeElectricalPackage.TECHNICAL_ELEMENT__RESIDUAL_CURRENT_MA:
                setResidualCurrentMa((Double) newValue);
                return;
            case HomeElectricalPackage.TECHNICAL_ELEMENT__RESIDUAL_TYPE:
                setResidualType((String) newValue);
                return;
            case HomeElectricalPackage.TECHNICAL_ELEMENT__TRIP_CURVE:
                setTripCurve((String) newValue);
                return;
            case HomeElectricalPackage.TECHNICAL_ELEMENT__POLE_CONFIGURATION:
                setPoleConfiguration((String) newValue);
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
            case HomeElectricalPackage.TECHNICAL_ELEMENT__RATED_CURRENT_A:
                setRatedCurrentA(RATED_CURRENT_A_EDEFAULT);
                return;
            case HomeElectricalPackage.TECHNICAL_ELEMENT__RATED_POWER_W:
                setRatedPowerW(RATED_POWER_W_EDEFAULT);
                return;
            case HomeElectricalPackage.TECHNICAL_ELEMENT__WIDTH_MODULES:
                setWidthModules(WIDTH_MODULES_EDEFAULT);
                return;
            case HomeElectricalPackage.TECHNICAL_ELEMENT__RESIDUAL_CURRENT_MA:
                setResidualCurrentMa(RESIDUAL_CURRENT_MA_EDEFAULT);
                return;
            case HomeElectricalPackage.TECHNICAL_ELEMENT__RESIDUAL_TYPE:
                setResidualType(RESIDUAL_TYPE_EDEFAULT);
                return;
            case HomeElectricalPackage.TECHNICAL_ELEMENT__TRIP_CURVE:
                setTripCurve(TRIP_CURVE_EDEFAULT);
                return;
            case HomeElectricalPackage.TECHNICAL_ELEMENT__POLE_CONFIGURATION:
                setPoleConfiguration(POLE_CONFIGURATION_EDEFAULT);
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
            case HomeElectricalPackage.TECHNICAL_ELEMENT__RATED_CURRENT_A:
                return RATED_CURRENT_A_EDEFAULT == null ? ratedCurrentA != null : !RATED_CURRENT_A_EDEFAULT.equals(ratedCurrentA);
            case HomeElectricalPackage.TECHNICAL_ELEMENT__RATED_POWER_W:
                return RATED_POWER_W_EDEFAULT == null ? ratedPowerW != null : !RATED_POWER_W_EDEFAULT.equals(ratedPowerW);
            case HomeElectricalPackage.TECHNICAL_ELEMENT__WIDTH_MODULES:
                return WIDTH_MODULES_EDEFAULT == null ? widthModules != null : !WIDTH_MODULES_EDEFAULT.equals(widthModules);
            case HomeElectricalPackage.TECHNICAL_ELEMENT__RESIDUAL_CURRENT_MA:
                return RESIDUAL_CURRENT_MA_EDEFAULT == null ? residualCurrentMa != null : !RESIDUAL_CURRENT_MA_EDEFAULT.equals(residualCurrentMa);
            case HomeElectricalPackage.TECHNICAL_ELEMENT__RESIDUAL_TYPE:
                return RESIDUAL_TYPE_EDEFAULT == null ? residualType != null : !RESIDUAL_TYPE_EDEFAULT.equals(residualType);
            case HomeElectricalPackage.TECHNICAL_ELEMENT__TRIP_CURVE:
                return TRIP_CURVE_EDEFAULT == null ? tripCurve != null : !TRIP_CURVE_EDEFAULT.equals(tripCurve);
            case HomeElectricalPackage.TECHNICAL_ELEMENT__POLE_CONFIGURATION:
                return POLE_CONFIGURATION_EDEFAULT == null ? poleConfiguration != null : !POLE_CONFIGURATION_EDEFAULT.equals(poleConfiguration);
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
        result.append(" (ratedCurrentA: ");
        result.append(ratedCurrentA);
        result.append(", ratedPowerW: ");
        result.append(ratedPowerW);
        result.append(", widthModules: ");
        result.append(widthModules);
        result.append(", residualCurrentMa: ");
        result.append(residualCurrentMa);
        result.append(", residualType: ");
        result.append(residualType);
        result.append(", tripCurve: ");
        result.append(tripCurve);
        result.append(", poleConfiguration: ");
        result.append(poleConfiguration);
        result.append(')');
        return result.toString();
    }

} //TechnicalElementImpl
