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
import hed.homeelectrical.Room;
import hed.homeelectrical.TechnicalElement;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Field Equipment</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.impl.FieldEquipmentImpl#getRatedCurrentA <em>Rated Current A</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.FieldEquipmentImpl#getRatedPowerW <em>Rated Power W</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.FieldEquipmentImpl#getWidthModules <em>Width Modules</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.FieldEquipmentImpl#getResidualCurrentMa <em>Residual Current Ma</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.FieldEquipmentImpl#getResidualType <em>Residual Type</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.FieldEquipmentImpl#getTripCurve <em>Trip Curve</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.FieldEquipmentImpl#getPoleConfiguration <em>Pole Configuration</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.FieldEquipmentImpl#getRoom <em>Room</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.FieldEquipmentImpl#getCircuit <em>Circuit</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.FieldEquipmentImpl#getBranch <em>Branch</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class FieldEquipmentImpl extends EquipmentImpl implements FieldEquipment {
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
     * The cached value of the '{@link #getRoom() <em>Room</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRoom()
     * @generated
     * @ordered
     */
    protected Room room;

    /**
     * The cached value of the '{@link #getCircuit() <em>Circuit</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCircuit()
     * @generated
     * @ordered
     */
    protected FinalCircuit circuit;

    /**
     * The cached value of the '{@link #getBranch() <em>Branch</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBranch()
     * @generated
     * @ordered
     */
    protected CircuitBranch branch;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected FieldEquipmentImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.FIELD_EQUIPMENT;
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
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.FIELD_EQUIPMENT__RATED_CURRENT_A, oldRatedCurrentA, ratedCurrentA));
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
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.FIELD_EQUIPMENT__RATED_POWER_W, oldRatedPowerW, ratedPowerW));
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
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.FIELD_EQUIPMENT__WIDTH_MODULES, oldWidthModules, widthModules));
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
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.FIELD_EQUIPMENT__RESIDUAL_CURRENT_MA, oldResidualCurrentMa, residualCurrentMa));
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
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.FIELD_EQUIPMENT__RESIDUAL_TYPE, oldResidualType, residualType));
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
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.FIELD_EQUIPMENT__TRIP_CURVE, oldTripCurve, tripCurve));
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
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.FIELD_EQUIPMENT__POLE_CONFIGURATION, oldPoleConfiguration, poleConfiguration));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Room getRoom() {
        if (room != null && room.eIsProxy()) {
            InternalEObject oldRoom = (InternalEObject) room;
            room = (Room) eResolveProxy(oldRoom);
            if (room != oldRoom) {
                if (eNotificationRequired()) {
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, HomeElectricalPackage.FIELD_EQUIPMENT__ROOM, oldRoom, room));
                }
            }
        }
        return room;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Room basicGetRoom() {
        return room;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setRoom(Room newRoom) {
        Room oldRoom = room;
        room = newRoom;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.FIELD_EQUIPMENT__ROOM, oldRoom, room));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public FinalCircuit getCircuit() {
        if (circuit != null && circuit.eIsProxy()) {
            InternalEObject oldCircuit = (InternalEObject) circuit;
            circuit = (FinalCircuit) eResolveProxy(oldCircuit);
            if (circuit != oldCircuit) {
                if (eNotificationRequired()) {
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, HomeElectricalPackage.FIELD_EQUIPMENT__CIRCUIT, oldCircuit, circuit));
                }
            }
        }
        return circuit;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public FinalCircuit basicGetCircuit() {
        return circuit;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetCircuit(FinalCircuit newCircuit, NotificationChain msgs) {
        FinalCircuit oldCircuit = circuit;
        circuit = newCircuit;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.FIELD_EQUIPMENT__CIRCUIT, oldCircuit, newCircuit);
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
    public void setCircuit(FinalCircuit newCircuit) {
        if (newCircuit != circuit) {
            NotificationChain msgs = null;
            if (circuit != null) {
                msgs = ((InternalEObject) circuit).eInverseRemove(this, HomeElectricalPackage.FINAL_CIRCUIT__EQUIPMENT, FinalCircuit.class, msgs);
            }
            if (newCircuit != null) {
                msgs = ((InternalEObject) newCircuit).eInverseAdd(this, HomeElectricalPackage.FINAL_CIRCUIT__EQUIPMENT, FinalCircuit.class, msgs);
            }
            msgs = basicSetCircuit(newCircuit, msgs);
            if (msgs != null) {
                msgs.dispatch();
            }
        } else if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.FIELD_EQUIPMENT__CIRCUIT, newCircuit, newCircuit));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public CircuitBranch getBranch() {
        if (branch != null && branch.eIsProxy()) {
            InternalEObject oldBranch = (InternalEObject) branch;
            branch = (CircuitBranch) eResolveProxy(oldBranch);
            if (branch != oldBranch) {
                if (eNotificationRequired()) {
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, HomeElectricalPackage.FIELD_EQUIPMENT__BRANCH, oldBranch, branch));
                }
            }
        }
        return branch;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CircuitBranch basicGetBranch() {
        return branch;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetBranch(CircuitBranch newBranch, NotificationChain msgs) {
        CircuitBranch oldBranch = branch;
        branch = newBranch;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.FIELD_EQUIPMENT__BRANCH, oldBranch, newBranch);
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
    public void setBranch(CircuitBranch newBranch) {
        if (newBranch != branch) {
            NotificationChain msgs = null;
            if (branch != null) {
                msgs = ((InternalEObject) branch).eInverseRemove(this, HomeElectricalPackage.CIRCUIT_BRANCH__EQUIPMENT, CircuitBranch.class, msgs);
            }
            if (newBranch != null) {
                msgs = ((InternalEObject) newBranch).eInverseAdd(this, HomeElectricalPackage.CIRCUIT_BRANCH__EQUIPMENT, CircuitBranch.class, msgs);
            }
            msgs = basicSetBranch(newBranch, msgs);
            if (msgs != null) {
                msgs.dispatch();
            }
        } else if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.FIELD_EQUIPMENT__BRANCH, newBranch, newBranch));
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
            case HomeElectricalPackage.FIELD_EQUIPMENT__CIRCUIT:
                if (circuit != null) {
                    msgs = ((InternalEObject) circuit).eInverseRemove(this, HomeElectricalPackage.FINAL_CIRCUIT__EQUIPMENT, FinalCircuit.class, msgs);
                }
                return basicSetCircuit((FinalCircuit) otherEnd, msgs);
            case HomeElectricalPackage.FIELD_EQUIPMENT__BRANCH:
                if (branch != null) {
                    msgs = ((InternalEObject) branch).eInverseRemove(this, HomeElectricalPackage.CIRCUIT_BRANCH__EQUIPMENT, CircuitBranch.class, msgs);
                }
                return basicSetBranch((CircuitBranch) otherEnd, msgs);
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
            case HomeElectricalPackage.FIELD_EQUIPMENT__CIRCUIT:
                return basicSetCircuit(null, msgs);
            case HomeElectricalPackage.FIELD_EQUIPMENT__BRANCH:
                return basicSetBranch(null, msgs);
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
            case HomeElectricalPackage.FIELD_EQUIPMENT__RATED_CURRENT_A:
                return getRatedCurrentA();
            case HomeElectricalPackage.FIELD_EQUIPMENT__RATED_POWER_W:
                return getRatedPowerW();
            case HomeElectricalPackage.FIELD_EQUIPMENT__WIDTH_MODULES:
                return getWidthModules();
            case HomeElectricalPackage.FIELD_EQUIPMENT__RESIDUAL_CURRENT_MA:
                return getResidualCurrentMa();
            case HomeElectricalPackage.FIELD_EQUIPMENT__RESIDUAL_TYPE:
                return getResidualType();
            case HomeElectricalPackage.FIELD_EQUIPMENT__TRIP_CURVE:
                return getTripCurve();
            case HomeElectricalPackage.FIELD_EQUIPMENT__POLE_CONFIGURATION:
                return getPoleConfiguration();
            case HomeElectricalPackage.FIELD_EQUIPMENT__ROOM:
                if (resolve) {
                    return getRoom();
                }
                return basicGetRoom();
            case HomeElectricalPackage.FIELD_EQUIPMENT__CIRCUIT:
                if (resolve) {
                    return getCircuit();
                }
                return basicGetCircuit();
            case HomeElectricalPackage.FIELD_EQUIPMENT__BRANCH:
                if (resolve) {
                    return getBranch();
                }
                return basicGetBranch();
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
            case HomeElectricalPackage.FIELD_EQUIPMENT__RATED_CURRENT_A:
                setRatedCurrentA((Double) newValue);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__RATED_POWER_W:
                setRatedPowerW((Double) newValue);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__WIDTH_MODULES:
                setWidthModules((Double) newValue);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__RESIDUAL_CURRENT_MA:
                setResidualCurrentMa((Double) newValue);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__RESIDUAL_TYPE:
                setResidualType((String) newValue);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__TRIP_CURVE:
                setTripCurve((String) newValue);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__POLE_CONFIGURATION:
                setPoleConfiguration((String) newValue);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__ROOM:
                setRoom((Room) newValue);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__CIRCUIT:
                setCircuit((FinalCircuit) newValue);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__BRANCH:
                setBranch((CircuitBranch) newValue);
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
            case HomeElectricalPackage.FIELD_EQUIPMENT__RATED_CURRENT_A:
                setRatedCurrentA(RATED_CURRENT_A_EDEFAULT);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__RATED_POWER_W:
                setRatedPowerW(RATED_POWER_W_EDEFAULT);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__WIDTH_MODULES:
                setWidthModules(WIDTH_MODULES_EDEFAULT);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__RESIDUAL_CURRENT_MA:
                setResidualCurrentMa(RESIDUAL_CURRENT_MA_EDEFAULT);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__RESIDUAL_TYPE:
                setResidualType(RESIDUAL_TYPE_EDEFAULT);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__TRIP_CURVE:
                setTripCurve(TRIP_CURVE_EDEFAULT);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__POLE_CONFIGURATION:
                setPoleConfiguration(POLE_CONFIGURATION_EDEFAULT);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__ROOM:
                setRoom((Room) null);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__CIRCUIT:
                setCircuit((FinalCircuit) null);
                return;
            case HomeElectricalPackage.FIELD_EQUIPMENT__BRANCH:
                setBranch((CircuitBranch) null);
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
            case HomeElectricalPackage.FIELD_EQUIPMENT__RATED_CURRENT_A:
                return RATED_CURRENT_A_EDEFAULT == null ? ratedCurrentA != null : !RATED_CURRENT_A_EDEFAULT.equals(ratedCurrentA);
            case HomeElectricalPackage.FIELD_EQUIPMENT__RATED_POWER_W:
                return RATED_POWER_W_EDEFAULT == null ? ratedPowerW != null : !RATED_POWER_W_EDEFAULT.equals(ratedPowerW);
            case HomeElectricalPackage.FIELD_EQUIPMENT__WIDTH_MODULES:
                return WIDTH_MODULES_EDEFAULT == null ? widthModules != null : !WIDTH_MODULES_EDEFAULT.equals(widthModules);
            case HomeElectricalPackage.FIELD_EQUIPMENT__RESIDUAL_CURRENT_MA:
                return RESIDUAL_CURRENT_MA_EDEFAULT == null ? residualCurrentMa != null : !RESIDUAL_CURRENT_MA_EDEFAULT.equals(residualCurrentMa);
            case HomeElectricalPackage.FIELD_EQUIPMENT__RESIDUAL_TYPE:
                return RESIDUAL_TYPE_EDEFAULT == null ? residualType != null : !RESIDUAL_TYPE_EDEFAULT.equals(residualType);
            case HomeElectricalPackage.FIELD_EQUIPMENT__TRIP_CURVE:
                return TRIP_CURVE_EDEFAULT == null ? tripCurve != null : !TRIP_CURVE_EDEFAULT.equals(tripCurve);
            case HomeElectricalPackage.FIELD_EQUIPMENT__POLE_CONFIGURATION:
                return POLE_CONFIGURATION_EDEFAULT == null ? poleConfiguration != null : !POLE_CONFIGURATION_EDEFAULT.equals(poleConfiguration);
            case HomeElectricalPackage.FIELD_EQUIPMENT__ROOM:
                return room != null;
            case HomeElectricalPackage.FIELD_EQUIPMENT__CIRCUIT:
                return circuit != null;
            case HomeElectricalPackage.FIELD_EQUIPMENT__BRANCH:
                return branch != null;
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
        if (baseClass == TechnicalElement.class) {
            switch (derivedFeatureID) {
                case HomeElectricalPackage.FIELD_EQUIPMENT__RATED_CURRENT_A:
                    return HomeElectricalPackage.TECHNICAL_ELEMENT__RATED_CURRENT_A;
                case HomeElectricalPackage.FIELD_EQUIPMENT__RATED_POWER_W:
                    return HomeElectricalPackage.TECHNICAL_ELEMENT__RATED_POWER_W;
                case HomeElectricalPackage.FIELD_EQUIPMENT__WIDTH_MODULES:
                    return HomeElectricalPackage.TECHNICAL_ELEMENT__WIDTH_MODULES;
                case HomeElectricalPackage.FIELD_EQUIPMENT__RESIDUAL_CURRENT_MA:
                    return HomeElectricalPackage.TECHNICAL_ELEMENT__RESIDUAL_CURRENT_MA;
                case HomeElectricalPackage.FIELD_EQUIPMENT__RESIDUAL_TYPE:
                    return HomeElectricalPackage.TECHNICAL_ELEMENT__RESIDUAL_TYPE;
                case HomeElectricalPackage.FIELD_EQUIPMENT__TRIP_CURVE:
                    return HomeElectricalPackage.TECHNICAL_ELEMENT__TRIP_CURVE;
                case HomeElectricalPackage.FIELD_EQUIPMENT__POLE_CONFIGURATION:
                    return HomeElectricalPackage.TECHNICAL_ELEMENT__POLE_CONFIGURATION;
                default:
                    return -1;
            }
        }
        return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
        if (baseClass == TechnicalElement.class) {
            switch (baseFeatureID) {
                case HomeElectricalPackage.TECHNICAL_ELEMENT__RATED_CURRENT_A:
                    return HomeElectricalPackage.FIELD_EQUIPMENT__RATED_CURRENT_A;
                case HomeElectricalPackage.TECHNICAL_ELEMENT__RATED_POWER_W:
                    return HomeElectricalPackage.FIELD_EQUIPMENT__RATED_POWER_W;
                case HomeElectricalPackage.TECHNICAL_ELEMENT__WIDTH_MODULES:
                    return HomeElectricalPackage.FIELD_EQUIPMENT__WIDTH_MODULES;
                case HomeElectricalPackage.TECHNICAL_ELEMENT__RESIDUAL_CURRENT_MA:
                    return HomeElectricalPackage.FIELD_EQUIPMENT__RESIDUAL_CURRENT_MA;
                case HomeElectricalPackage.TECHNICAL_ELEMENT__RESIDUAL_TYPE:
                    return HomeElectricalPackage.FIELD_EQUIPMENT__RESIDUAL_TYPE;
                case HomeElectricalPackage.TECHNICAL_ELEMENT__TRIP_CURVE:
                    return HomeElectricalPackage.FIELD_EQUIPMENT__TRIP_CURVE;
                case HomeElectricalPackage.TECHNICAL_ELEMENT__POLE_CONFIGURATION:
                    return HomeElectricalPackage.FIELD_EQUIPMENT__POLE_CONFIGURATION;
                default:
                    return -1;
            }
        }
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
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

} //FieldEquipmentImpl
