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
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.ModularDevice;
import hed.homeelectrical.Room;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Distribution Board</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.impl.DistributionBoardImpl#getRoom <em>Room</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.DistributionBoardImpl#getRails <em>Rails</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.DistributionBoardImpl#getDevices <em>Devices</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DistributionBoardImpl extends NamedElementImpl implements DistributionBoard {
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
     * The cached value of the '{@link #getRails() <em>Rails</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRails()
     * @generated
     * @ordered
     */
    protected EList<DINRail> rails;

    /**
     * The cached value of the '{@link #getDevices() <em>Devices</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDevices()
     * @generated
     * @ordered
     */
    protected EList<ModularDevice> devices;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DistributionBoardImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.DISTRIBUTION_BOARD;
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
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, HomeElectricalPackage.DISTRIBUTION_BOARD__ROOM, oldRoom, room));
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
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.DISTRIBUTION_BOARD__ROOM, oldRoom, room));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<DINRail> getRails() {
        if (rails == null) {
            rails = new EObjectContainmentEList<DINRail>(DINRail.class, this, HomeElectricalPackage.DISTRIBUTION_BOARD__RAILS);
        }
        return rails;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<ModularDevice> getDevices() {
        if (devices == null) {
            devices = new EObjectContainmentEList<ModularDevice>(ModularDevice.class, this, HomeElectricalPackage.DISTRIBUTION_BOARD__DEVICES);
        }
        return devices;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case HomeElectricalPackage.DISTRIBUTION_BOARD__RAILS:
                return ((InternalEList<?>) getRails()).basicRemove(otherEnd, msgs);
            case HomeElectricalPackage.DISTRIBUTION_BOARD__DEVICES:
                return ((InternalEList<?>) getDevices()).basicRemove(otherEnd, msgs);
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
            case HomeElectricalPackage.DISTRIBUTION_BOARD__ROOM:
                if (resolve) {
                    return getRoom();
                }
                return basicGetRoom();
            case HomeElectricalPackage.DISTRIBUTION_BOARD__RAILS:
                return getRails();
            case HomeElectricalPackage.DISTRIBUTION_BOARD__DEVICES:
                return getDevices();
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
            case HomeElectricalPackage.DISTRIBUTION_BOARD__ROOM:
                setRoom((Room) newValue);
                return;
            case HomeElectricalPackage.DISTRIBUTION_BOARD__RAILS:
                getRails().clear();
                getRails().addAll((Collection<? extends DINRail>) newValue);
                return;
            case HomeElectricalPackage.DISTRIBUTION_BOARD__DEVICES:
                getDevices().clear();
                getDevices().addAll((Collection<? extends ModularDevice>) newValue);
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
            case HomeElectricalPackage.DISTRIBUTION_BOARD__ROOM:
                setRoom((Room) null);
                return;
            case HomeElectricalPackage.DISTRIBUTION_BOARD__RAILS:
                getRails().clear();
                return;
            case HomeElectricalPackage.DISTRIBUTION_BOARD__DEVICES:
                getDevices().clear();
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
            case HomeElectricalPackage.DISTRIBUTION_BOARD__ROOM:
                return room != null;
            case HomeElectricalPackage.DISTRIBUTION_BOARD__RAILS:
                return rails != null && !rails.isEmpty();
            case HomeElectricalPackage.DISTRIBUTION_BOARD__DEVICES:
                return devices != null && !devices.isEmpty();
        }
        return super.eIsSet(featureID);
    }

} //DistributionBoardImpl
