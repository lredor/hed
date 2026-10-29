/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.homeelectrical.util;

import hed.homeelectrical.Catalog;
import hed.homeelectrical.Circuit;
import hed.homeelectrical.CircuitBranch;
import hed.homeelectrical.ControlChannel;
import hed.homeelectrical.ControlDevice;
import hed.homeelectrical.ControlFunction;
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.DINRail;
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.DistributionCircuit;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.ElectricalLoad;
import hed.homeelectrical.EnergyMeter;
import hed.homeelectrical.Equipment;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.ModularDevice;
import hed.homeelectrical.NamedElement;
import hed.homeelectrical.Outlet;
import hed.homeelectrical.Room;
import hed.homeelectrical.ServiceCircuitBreaker;
import hed.homeelectrical.ServiceConnection;
import hed.homeelectrical.Storey;
import hed.homeelectrical.TechnicalElement;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see hed.homeelectrical.HomeElectricalPackage
 * @generated
 */
public class HomeElectricalAdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static HomeElectricalPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public HomeElectricalAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = HomeElectricalPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject) object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected HomeElectricalSwitch<Adapter> modelSwitch = new HomeElectricalSwitch<Adapter>() {
        @Override
        public Adapter caseNamedElement(NamedElement object) {
            return createNamedElementAdapter();
        }

        @Override
        public Adapter caseTechnicalElement(TechnicalElement object) {
            return createTechnicalElementAdapter();
        }

        @Override
        public Adapter caseDwelling(Dwelling object) {
            return createDwellingAdapter();
        }

        @Override
        public Adapter caseStorey(Storey object) {
            return createStoreyAdapter();
        }

        @Override
        public Adapter caseRoom(Room object) {
            return createRoomAdapter();
        }

        @Override
        public Adapter caseCatalog(Catalog object) {
            return createCatalogAdapter();
        }

        @Override
        public Adapter caseDeviceType(DeviceType object) {
            return createDeviceTypeAdapter();
        }

        @Override
        public Adapter caseEquipment(Equipment object) {
            return createEquipmentAdapter();
        }

        @Override
        public Adapter caseFieldEquipment(FieldEquipment object) {
            return createFieldEquipmentAdapter();
        }

        @Override
        public Adapter caseOutlet(Outlet object) {
            return createOutletAdapter();
        }

        @Override
        public Adapter caseElectricalLoad(ElectricalLoad object) {
            return createElectricalLoadAdapter();
        }

        @Override
        public Adapter caseControlDevice(ControlDevice object) {
            return createControlDeviceAdapter();
        }

        @Override
        public Adapter caseControlChannel(ControlChannel object) {
            return createControlChannelAdapter();
        }

        @Override
        public Adapter caseDistributionBoard(DistributionBoard object) {
            return createDistributionBoardAdapter();
        }

        @Override
        public Adapter caseDINRail(DINRail object) {
            return createDINRailAdapter();
        }

        @Override
        public Adapter caseModularDevice(ModularDevice object) {
            return createModularDeviceAdapter();
        }

        @Override
        public Adapter caseCircuit(Circuit object) {
            return createCircuitAdapter();
        }

        @Override
        public Adapter caseFinalCircuit(FinalCircuit object) {
            return createFinalCircuitAdapter();
        }

        @Override
        public Adapter caseDistributionCircuit(DistributionCircuit object) {
            return createDistributionCircuitAdapter();
        }

        @Override
        public Adapter caseControlFunction(ControlFunction object) {
            return createControlFunctionAdapter();
        }

        @Override
        public Adapter caseServiceConnection(ServiceConnection object) {
            return createServiceConnectionAdapter();
        }

        @Override
        public Adapter caseEnergyMeter(EnergyMeter object) {
            return createEnergyMeterAdapter();
        }

        @Override
        public Adapter caseServiceCircuitBreaker(ServiceCircuitBreaker object) {
            return createServiceCircuitBreakerAdapter();
        }

        @Override
        public Adapter caseCircuitBranch(CircuitBranch object) {
            return createCircuitBranchAdapter();
        }

        @Override
        public Adapter defaultCase(EObject object) {
            return createEObjectAdapter();
        }
    };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return modelSwitch.doSwitch((EObject) target);
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.NamedElement <em>Named Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.NamedElement
     * @generated
     */
    public Adapter createNamedElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.TechnicalElement <em>Technical Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.TechnicalElement
     * @generated
     */
    public Adapter createTechnicalElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.Dwelling <em>Dwelling</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.Dwelling
     * @generated
     */
    public Adapter createDwellingAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.Storey <em>Storey</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.Storey
     * @generated
     */
    public Adapter createStoreyAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.Room <em>Room</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.Room
     * @generated
     */
    public Adapter createRoomAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.Catalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.Catalog
     * @generated
     */
    public Adapter createCatalogAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.DeviceType <em>Device Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.DeviceType
     * @generated
     */
    public Adapter createDeviceTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.Equipment <em>Equipment</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.Equipment
     * @generated
     */
    public Adapter createEquipmentAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.FieldEquipment <em>Field Equipment</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.FieldEquipment
     * @generated
     */
    public Adapter createFieldEquipmentAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.Outlet <em>Outlet</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.Outlet
     * @generated
     */
    public Adapter createOutletAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.ElectricalLoad <em>Electrical Load</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.ElectricalLoad
     * @generated
     */
    public Adapter createElectricalLoadAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.ControlDevice <em>Control Device</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.ControlDevice
     * @generated
     */
    public Adapter createControlDeviceAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.ControlChannel <em>Control Channel</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.ControlChannel
     * @generated
     */
    public Adapter createControlChannelAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.DistributionBoard <em>Distribution Board</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.DistributionBoard
     * @generated
     */
    public Adapter createDistributionBoardAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.DINRail <em>DIN Rail</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.DINRail
     * @generated
     */
    public Adapter createDINRailAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.ModularDevice <em>Modular Device</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.ModularDevice
     * @generated
     */
    public Adapter createModularDeviceAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.Circuit <em>Circuit</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.Circuit
     * @generated
     */
    public Adapter createCircuitAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.FinalCircuit <em>Final Circuit</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.FinalCircuit
     * @generated
     */
    public Adapter createFinalCircuitAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.DistributionCircuit <em>Distribution Circuit</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.DistributionCircuit
     * @generated
     */
    public Adapter createDistributionCircuitAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.ControlFunction <em>Control Function</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.ControlFunction
     * @generated
     */
    public Adapter createControlFunctionAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.ServiceConnection <em>Service Connection</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.ServiceConnection
     * @generated
     */
    public Adapter createServiceConnectionAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.EnergyMeter <em>Energy Meter</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.EnergyMeter
     * @generated
     */
    public Adapter createEnergyMeterAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.ServiceCircuitBreaker <em>Service Circuit Breaker</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.ServiceCircuitBreaker
     * @generated
     */
    public Adapter createServiceCircuitBreakerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link hed.homeelectrical.CircuitBranch <em>Circuit Branch</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see hed.homeelectrical.CircuitBranch
     * @generated
     */
    public Adapter createCircuitBranchAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} //HomeElectricalAdapterFactory
