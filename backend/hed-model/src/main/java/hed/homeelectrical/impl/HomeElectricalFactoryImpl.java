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
import hed.homeelectrical.CircuitBranch;
import hed.homeelectrical.CircuitPhase;
import hed.homeelectrical.CircuitUse;
import hed.homeelectrical.ControlChannel;
import hed.homeelectrical.ControlDevice;
import hed.homeelectrical.ControlFunction;
import hed.homeelectrical.ControlMode;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.DINRail;
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.DistributionCircuit;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.ElectricalLoad;
import hed.homeelectrical.EnergyMeter;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.HomeElectricalFactory;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.ModularDevice;
import hed.homeelectrical.Outlet;
import hed.homeelectrical.Room;
import hed.homeelectrical.ServiceCircuitBreaker;
import hed.homeelectrical.ServiceConnection;
import hed.homeelectrical.Storey;
import hed.homeelectrical.SupplyPhase;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class HomeElectricalFactoryImpl extends EFactoryImpl implements HomeElectricalFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static HomeElectricalFactory init() {
        try {
            HomeElectricalFactory theHomeElectricalFactory = (HomeElectricalFactory) EPackage.Registry.INSTANCE.getEFactory(HomeElectricalPackage.eNS_URI);
            if (theHomeElectricalFactory != null) {
                return theHomeElectricalFactory;
            }
        } catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new HomeElectricalFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public HomeElectricalFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case HomeElectricalPackage.DWELLING:
                return createDwelling();
            case HomeElectricalPackage.STOREY:
                return createStorey();
            case HomeElectricalPackage.ROOM:
                return createRoom();
            case HomeElectricalPackage.CATALOG:
                return createCatalog();
            case HomeElectricalPackage.DEVICE_TYPE:
                return createDeviceType();
            case HomeElectricalPackage.OUTLET:
                return createOutlet();
            case HomeElectricalPackage.ELECTRICAL_LOAD:
                return createElectricalLoad();
            case HomeElectricalPackage.CONTROL_DEVICE:
                return createControlDevice();
            case HomeElectricalPackage.CONTROL_CHANNEL:
                return createControlChannel();
            case HomeElectricalPackage.DISTRIBUTION_BOARD:
                return createDistributionBoard();
            case HomeElectricalPackage.DIN_RAIL:
                return createDINRail();
            case HomeElectricalPackage.MODULAR_DEVICE:
                return createModularDevice();
            case HomeElectricalPackage.FINAL_CIRCUIT:
                return createFinalCircuit();
            case HomeElectricalPackage.DISTRIBUTION_CIRCUIT:
                return createDistributionCircuit();
            case HomeElectricalPackage.CONTROL_FUNCTION:
                return createControlFunction();
            case HomeElectricalPackage.SERVICE_CONNECTION:
                return createServiceConnection();
            case HomeElectricalPackage.ENERGY_METER:
                return createEnergyMeter();
            case HomeElectricalPackage.SERVICE_CIRCUIT_BREAKER:
                return createServiceCircuitBreaker();
            case HomeElectricalPackage.CIRCUIT_BRANCH:
                return createCircuitBranch();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object createFromString(EDataType eDataType, String initialValue) {
        switch (eDataType.getClassifierID()) {
            case HomeElectricalPackage.DEVICE_KIND:
                return createDeviceKindFromString(eDataType, initialValue);
            case HomeElectricalPackage.SUPPLY_PHASE:
                return createSupplyPhaseFromString(eDataType, initialValue);
            case HomeElectricalPackage.CIRCUIT_PHASE:
                return createCircuitPhaseFromString(eDataType, initialValue);
            case HomeElectricalPackage.CIRCUIT_USE:
                return createCircuitUseFromString(eDataType, initialValue);
            case HomeElectricalPackage.CONTROL_MODE:
                return createControlModeFromString(eDataType, initialValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String convertToString(EDataType eDataType, Object instanceValue) {
        switch (eDataType.getClassifierID()) {
            case HomeElectricalPackage.DEVICE_KIND:
                return convertDeviceKindToString(eDataType, instanceValue);
            case HomeElectricalPackage.SUPPLY_PHASE:
                return convertSupplyPhaseToString(eDataType, instanceValue);
            case HomeElectricalPackage.CIRCUIT_PHASE:
                return convertCircuitPhaseToString(eDataType, instanceValue);
            case HomeElectricalPackage.CIRCUIT_USE:
                return convertCircuitUseToString(eDataType, instanceValue);
            case HomeElectricalPackage.CONTROL_MODE:
                return convertControlModeToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Dwelling createDwelling() {
        DwellingImpl dwelling = new DwellingImpl();
        return dwelling;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Storey createStorey() {
        StoreyImpl storey = new StoreyImpl();
        return storey;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Room createRoom() {
        RoomImpl room = new RoomImpl();
        return room;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Catalog createCatalog() {
        CatalogImpl catalog = new CatalogImpl();
        return catalog;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public DeviceType createDeviceType() {
        DeviceTypeImpl deviceType = new DeviceTypeImpl();
        return deviceType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Outlet createOutlet() {
        OutletImpl outlet = new OutletImpl();
        return outlet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ElectricalLoad createElectricalLoad() {
        ElectricalLoadImpl electricalLoad = new ElectricalLoadImpl();
        return electricalLoad;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ControlDevice createControlDevice() {
        ControlDeviceImpl controlDevice = new ControlDeviceImpl();
        return controlDevice;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ControlChannel createControlChannel() {
        ControlChannelImpl controlChannel = new ControlChannelImpl();
        return controlChannel;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public DistributionBoard createDistributionBoard() {
        DistributionBoardImpl distributionBoard = new DistributionBoardImpl();
        return distributionBoard;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public DINRail createDINRail() {
        DINRailImpl dinRail = new DINRailImpl();
        return dinRail;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ModularDevice createModularDevice() {
        ModularDeviceImpl modularDevice = new ModularDeviceImpl();
        return modularDevice;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public FinalCircuit createFinalCircuit() {
        FinalCircuitImpl finalCircuit = new FinalCircuitImpl();
        return finalCircuit;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public DistributionCircuit createDistributionCircuit() {
        DistributionCircuitImpl distributionCircuit = new DistributionCircuitImpl();
        return distributionCircuit;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ControlFunction createControlFunction() {
        ControlFunctionImpl controlFunction = new ControlFunctionImpl();
        return controlFunction;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ServiceConnection createServiceConnection() {
        ServiceConnectionImpl serviceConnection = new ServiceConnectionImpl();
        return serviceConnection;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EnergyMeter createEnergyMeter() {
        EnergyMeterImpl energyMeter = new EnergyMeterImpl();
        return energyMeter;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ServiceCircuitBreaker createServiceCircuitBreaker() {
        ServiceCircuitBreakerImpl serviceCircuitBreaker = new ServiceCircuitBreakerImpl();
        return serviceCircuitBreaker;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public CircuitBranch createCircuitBranch() {
        CircuitBranchImpl circuitBranch = new CircuitBranchImpl();
        return circuitBranch;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DeviceKind createDeviceKindFromString(EDataType eDataType, String initialValue) {
        DeviceKind result = DeviceKind.get(initialValue);
        if (result == null) {
            throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
        }
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertDeviceKindToString(EDataType eDataType, Object instanceValue) {
        return instanceValue == null ? null : instanceValue.toString();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SupplyPhase createSupplyPhaseFromString(EDataType eDataType, String initialValue) {
        SupplyPhase result = SupplyPhase.get(initialValue);
        if (result == null) {
            throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
        }
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertSupplyPhaseToString(EDataType eDataType, Object instanceValue) {
        return instanceValue == null ? null : instanceValue.toString();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CircuitPhase createCircuitPhaseFromString(EDataType eDataType, String initialValue) {
        CircuitPhase result = CircuitPhase.get(initialValue);
        if (result == null) {
            throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
        }
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertCircuitPhaseToString(EDataType eDataType, Object instanceValue) {
        return instanceValue == null ? null : instanceValue.toString();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CircuitUse createCircuitUseFromString(EDataType eDataType, String initialValue) {
        CircuitUse result = CircuitUse.get(initialValue);
        if (result == null) {
            throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
        }
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertCircuitUseToString(EDataType eDataType, Object instanceValue) {
        return instanceValue == null ? null : instanceValue.toString();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ControlMode createControlModeFromString(EDataType eDataType, String initialValue) {
        ControlMode result = ControlMode.get(initialValue);
        if (result == null) {
            throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
        }
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertControlModeToString(EDataType eDataType, Object instanceValue) {
        return instanceValue == null ? null : instanceValue.toString();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public HomeElectricalPackage getHomeElectricalPackage() {
        return (HomeElectricalPackage) getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static HomeElectricalPackage getPackage() {
        return HomeElectricalPackage.eINSTANCE;
    }

} //HomeElectricalFactoryImpl
