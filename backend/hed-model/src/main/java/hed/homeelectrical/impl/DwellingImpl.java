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

import hed.homeelectrical.Catalog;
import hed.homeelectrical.Circuit;
import hed.homeelectrical.ControlFunction;
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.ServiceConnection;
import hed.homeelectrical.Storey;

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
 * An implementation of the model object '<em><b>Dwelling</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.impl.DwellingImpl#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.DwellingImpl#getStoreys <em>Storeys</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.DwellingImpl#getEquipment <em>Equipment</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.DwellingImpl#getBoards <em>Boards</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.DwellingImpl#getCircuits <em>Circuits</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.DwellingImpl#getControlFunctions <em>Control Functions</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.DwellingImpl#getServiceConnection <em>Service Connection</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DwellingImpl extends NamedElementImpl implements Dwelling {
    /**
     * The cached value of the '{@link #getCatalog() <em>Catalog</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCatalog()
     * @generated
     * @ordered
     */
    protected Catalog catalog;

    /**
     * The cached value of the '{@link #getStoreys() <em>Storeys</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStoreys()
     * @generated
     * @ordered
     */
    protected EList<Storey> storeys;

    /**
     * The cached value of the '{@link #getEquipment() <em>Equipment</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEquipment()
     * @generated
     * @ordered
     */
    protected EList<FieldEquipment> equipment;

    /**
     * The cached value of the '{@link #getBoards() <em>Boards</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBoards()
     * @generated
     * @ordered
     */
    protected EList<DistributionBoard> boards;

    /**
     * The cached value of the '{@link #getCircuits() <em>Circuits</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCircuits()
     * @generated
     * @ordered
     */
    protected EList<Circuit> circuits;

    /**
     * The cached value of the '{@link #getControlFunctions() <em>Control Functions</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getControlFunctions()
     * @generated
     * @ordered
     */
    protected EList<ControlFunction> controlFunctions;

    /**
     * The cached value of the '{@link #getServiceConnection() <em>Service Connection</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getServiceConnection()
     * @generated
     * @ordered
     */
    protected ServiceConnection serviceConnection;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DwellingImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.DWELLING;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Catalog getCatalog() {
        return catalog;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetCatalog(Catalog newCatalog, NotificationChain msgs) {
        Catalog oldCatalog = catalog;
        catalog = newCatalog;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.DWELLING__CATALOG, oldCatalog, newCatalog);
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
    public void setCatalog(Catalog newCatalog) {
        if (newCatalog != catalog) {
            NotificationChain msgs = null;
            if (catalog != null) {
                msgs = ((InternalEObject) catalog).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - HomeElectricalPackage.DWELLING__CATALOG, null, msgs);
            }
            if (newCatalog != null) {
                msgs = ((InternalEObject) newCatalog).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - HomeElectricalPackage.DWELLING__CATALOG, null, msgs);
            }
            msgs = basicSetCatalog(newCatalog, msgs);
            if (msgs != null) {
                msgs.dispatch();
            }
        } else if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.DWELLING__CATALOG, newCatalog, newCatalog));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<Storey> getStoreys() {
        if (storeys == null) {
            storeys = new EObjectContainmentEList<Storey>(Storey.class, this, HomeElectricalPackage.DWELLING__STOREYS);
        }
        return storeys;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<FieldEquipment> getEquipment() {
        if (equipment == null) {
            equipment = new EObjectContainmentEList<FieldEquipment>(FieldEquipment.class, this, HomeElectricalPackage.DWELLING__EQUIPMENT);
        }
        return equipment;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<DistributionBoard> getBoards() {
        if (boards == null) {
            boards = new EObjectContainmentEList<DistributionBoard>(DistributionBoard.class, this, HomeElectricalPackage.DWELLING__BOARDS);
        }
        return boards;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<Circuit> getCircuits() {
        if (circuits == null) {
            circuits = new EObjectContainmentEList<Circuit>(Circuit.class, this, HomeElectricalPackage.DWELLING__CIRCUITS);
        }
        return circuits;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<ControlFunction> getControlFunctions() {
        if (controlFunctions == null) {
            controlFunctions = new EObjectContainmentEList<ControlFunction>(ControlFunction.class, this, HomeElectricalPackage.DWELLING__CONTROL_FUNCTIONS);
        }
        return controlFunctions;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetServiceConnection(ServiceConnection newServiceConnection, NotificationChain msgs) {
        ServiceConnection oldServiceConnection = serviceConnection;
        serviceConnection = newServiceConnection;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.DWELLING__SERVICE_CONNECTION, oldServiceConnection, newServiceConnection);
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
    public void setServiceConnection(ServiceConnection newServiceConnection) {
        if (newServiceConnection != serviceConnection) {
            NotificationChain msgs = null;
            if (serviceConnection != null) {
                msgs = ((InternalEObject) serviceConnection).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - HomeElectricalPackage.DWELLING__SERVICE_CONNECTION, null, msgs);
            }
            if (newServiceConnection != null) {
                msgs = ((InternalEObject) newServiceConnection).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - HomeElectricalPackage.DWELLING__SERVICE_CONNECTION, null, msgs);
            }
            msgs = basicSetServiceConnection(newServiceConnection, msgs);
            if (msgs != null) {
                msgs.dispatch();
            }
        } else if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.DWELLING__SERVICE_CONNECTION, newServiceConnection, newServiceConnection));
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
            case HomeElectricalPackage.DWELLING__CATALOG:
                return basicSetCatalog(null, msgs);
            case HomeElectricalPackage.DWELLING__STOREYS:
                return ((InternalEList<?>) getStoreys()).basicRemove(otherEnd, msgs);
            case HomeElectricalPackage.DWELLING__EQUIPMENT:
                return ((InternalEList<?>) getEquipment()).basicRemove(otherEnd, msgs);
            case HomeElectricalPackage.DWELLING__BOARDS:
                return ((InternalEList<?>) getBoards()).basicRemove(otherEnd, msgs);
            case HomeElectricalPackage.DWELLING__CIRCUITS:
                return ((InternalEList<?>) getCircuits()).basicRemove(otherEnd, msgs);
            case HomeElectricalPackage.DWELLING__CONTROL_FUNCTIONS:
                return ((InternalEList<?>) getControlFunctions()).basicRemove(otherEnd, msgs);
            case HomeElectricalPackage.DWELLING__SERVICE_CONNECTION:
                return basicSetServiceConnection(null, msgs);
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
            case HomeElectricalPackage.DWELLING__CATALOG:
                return getCatalog();
            case HomeElectricalPackage.DWELLING__STOREYS:
                return getStoreys();
            case HomeElectricalPackage.DWELLING__EQUIPMENT:
                return getEquipment();
            case HomeElectricalPackage.DWELLING__BOARDS:
                return getBoards();
            case HomeElectricalPackage.DWELLING__CIRCUITS:
                return getCircuits();
            case HomeElectricalPackage.DWELLING__CONTROL_FUNCTIONS:
                return getControlFunctions();
            case HomeElectricalPackage.DWELLING__SERVICE_CONNECTION:
                return getServiceConnection();
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
            case HomeElectricalPackage.DWELLING__CATALOG:
                setCatalog((Catalog) newValue);
                return;
            case HomeElectricalPackage.DWELLING__STOREYS:
                getStoreys().clear();
                getStoreys().addAll((Collection<? extends Storey>) newValue);
                return;
            case HomeElectricalPackage.DWELLING__EQUIPMENT:
                getEquipment().clear();
                getEquipment().addAll((Collection<? extends FieldEquipment>) newValue);
                return;
            case HomeElectricalPackage.DWELLING__BOARDS:
                getBoards().clear();
                getBoards().addAll((Collection<? extends DistributionBoard>) newValue);
                return;
            case HomeElectricalPackage.DWELLING__CIRCUITS:
                getCircuits().clear();
                getCircuits().addAll((Collection<? extends Circuit>) newValue);
                return;
            case HomeElectricalPackage.DWELLING__CONTROL_FUNCTIONS:
                getControlFunctions().clear();
                getControlFunctions().addAll((Collection<? extends ControlFunction>) newValue);
                return;
            case HomeElectricalPackage.DWELLING__SERVICE_CONNECTION:
                setServiceConnection((ServiceConnection) newValue);
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
            case HomeElectricalPackage.DWELLING__CATALOG:
                setCatalog((Catalog) null);
                return;
            case HomeElectricalPackage.DWELLING__STOREYS:
                getStoreys().clear();
                return;
            case HomeElectricalPackage.DWELLING__EQUIPMENT:
                getEquipment().clear();
                return;
            case HomeElectricalPackage.DWELLING__BOARDS:
                getBoards().clear();
                return;
            case HomeElectricalPackage.DWELLING__CIRCUITS:
                getCircuits().clear();
                return;
            case HomeElectricalPackage.DWELLING__CONTROL_FUNCTIONS:
                getControlFunctions().clear();
                return;
            case HomeElectricalPackage.DWELLING__SERVICE_CONNECTION:
                setServiceConnection((ServiceConnection) null);
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
            case HomeElectricalPackage.DWELLING__CATALOG:
                return catalog != null;
            case HomeElectricalPackage.DWELLING__STOREYS:
                return storeys != null && !storeys.isEmpty();
            case HomeElectricalPackage.DWELLING__EQUIPMENT:
                return equipment != null && !equipment.isEmpty();
            case HomeElectricalPackage.DWELLING__BOARDS:
                return boards != null && !boards.isEmpty();
            case HomeElectricalPackage.DWELLING__CIRCUITS:
                return circuits != null && !circuits.isEmpty();
            case HomeElectricalPackage.DWELLING__CONTROL_FUNCTIONS:
                return controlFunctions != null && !controlFunctions.isEmpty();
            case HomeElectricalPackage.DWELLING__SERVICE_CONNECTION:
                return serviceConnection != null;
        }
        return super.eIsSet(featureID);
    }

} //DwellingImpl
