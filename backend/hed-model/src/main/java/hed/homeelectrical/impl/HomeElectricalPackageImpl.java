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
import hed.homeelectrical.CircuitBranch;
import hed.homeelectrical.CircuitPhase;
import hed.homeelectrical.CircuitUse;
import hed.homeelectrical.ControlChannel;
import hed.homeelectrical.ControlDevice;
import hed.homeelectrical.ControlFunction;
import hed.homeelectrical.ControlMode;
import hed.homeelectrical.DINRail;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.DistributionCircuit;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.ElectricalLoad;
import hed.homeelectrical.EnergyMeter;
import hed.homeelectrical.Equipment;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.HomeElectricalFactory;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.ModularDevice;
import hed.homeelectrical.NamedElement;
import hed.homeelectrical.Outlet;
import hed.homeelectrical.Room;
import hed.homeelectrical.ServiceCircuitBreaker;
import hed.homeelectrical.ServiceConnection;
import hed.homeelectrical.Storey;
import hed.homeelectrical.SupplyPhase;
import hed.homeelectrical.TechnicalElement;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class HomeElectricalPackageImpl extends EPackageImpl implements HomeElectricalPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass namedElementEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass technicalElementEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass dwellingEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass storeyEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass roomEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass catalogEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass deviceTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass equipmentEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass fieldEquipmentEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass outletEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass electricalLoadEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass controlDeviceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass controlChannelEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass distributionBoardEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass dinRailEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass modularDeviceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass circuitEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass finalCircuitEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass distributionCircuitEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass controlFunctionEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass serviceConnectionEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass energyMeterEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass serviceCircuitBreakerEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass circuitBranchEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum deviceKindEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum supplyPhaseEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum circuitPhaseEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum circuitUseEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum controlModeEEnum = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see hed.homeelectrical.HomeElectricalPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private HomeElectricalPackageImpl() {
        super(eNS_URI, HomeElectricalFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     *
     * <p>This method is used to initialize {@link HomeElectricalPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static HomeElectricalPackage init() {
        if (isInited) {
            return (HomeElectricalPackage) EPackage.Registry.INSTANCE.getEPackage(HomeElectricalPackage.eNS_URI);
        }

        // Obtain or create and register package
        Object registeredHomeElectricalPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
        HomeElectricalPackageImpl theHomeElectricalPackage = registeredHomeElectricalPackage instanceof HomeElectricalPackageImpl ? (HomeElectricalPackageImpl) registeredHomeElectricalPackage
                : new HomeElectricalPackageImpl();

        isInited = true;

        // Create package meta-data objects
        theHomeElectricalPackage.createPackageContents();

        // Initialize created meta-data
        theHomeElectricalPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theHomeElectricalPackage.freeze();

        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(HomeElectricalPackage.eNS_URI, theHomeElectricalPackage);
        return theHomeElectricalPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getNamedElement() {
        return namedElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getNamedElement_Name() {
        return (EAttribute) namedElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getNamedElement_Description() {
        return (EAttribute) namedElementEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getTechnicalElement() {
        return technicalElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getTechnicalElement_RatedCurrentA() {
        return (EAttribute) technicalElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getTechnicalElement_RatedPowerW() {
        return (EAttribute) technicalElementEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getTechnicalElement_WidthModules() {
        return (EAttribute) technicalElementEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getTechnicalElement_ResidualCurrentMa() {
        return (EAttribute) technicalElementEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getTechnicalElement_ResidualType() {
        return (EAttribute) technicalElementEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getTechnicalElement_TripCurve() {
        return (EAttribute) technicalElementEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getTechnicalElement_PoleConfiguration() {
        return (EAttribute) technicalElementEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getDwelling() {
        return dwellingEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getDwelling_Catalog() {
        return (EReference) dwellingEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getDwelling_Storeys() {
        return (EReference) dwellingEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getDwelling_Equipment() {
        return (EReference) dwellingEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getDwelling_Boards() {
        return (EReference) dwellingEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getDwelling_Circuits() {
        return (EReference) dwellingEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getDwelling_ControlFunctions() {
        return (EReference) dwellingEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getDwelling_ServiceConnection() {
        return (EReference) dwellingEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getStorey() {
        return storeyEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getStorey_LevelNumber() {
        return (EAttribute) storeyEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getStorey_Rooms() {
        return (EReference) storeyEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getStorey_PlanImageId() {
        return (EAttribute) storeyEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getStorey_PlanWidth() {
        return (EAttribute) storeyEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getStorey_PlanHeight() {
        return (EAttribute) storeyEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getStorey_PlanAspectRatio() {
        return (EAttribute) storeyEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getRoom() {
        return roomEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getCatalog() {
        return catalogEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getCatalog_DeviceTypes() {
        return (EReference) catalogEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getDeviceType() {
        return deviceTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getDeviceType_Kind() {
        return (EAttribute) deviceTypeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getDeviceType_Manufacturer() {
        return (EAttribute) deviceTypeEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getDeviceType_ProductReference() {
        return (EAttribute) deviceTypeEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getDeviceType_ChannelCount() {
        return (EAttribute) deviceTypeEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getEquipment() {
        return equipmentEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getEquipment_DeviceType() {
        return (EReference) equipmentEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getFieldEquipment() {
        return fieldEquipmentEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getFieldEquipment_Room() {
        return (EReference) fieldEquipmentEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getFieldEquipment_Circuit() {
        return (EReference) fieldEquipmentEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getFieldEquipment_Branch() {
        return (EReference) fieldEquipmentEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getOutlet() {
        return outletEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getElectricalLoad() {
        return electricalLoadEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getControlDevice() {
        return controlDeviceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getControlDevice_Channels() {
        return (EReference) controlDeviceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getControlChannel() {
        return controlChannelEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getDistributionBoard() {
        return distributionBoardEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getDistributionBoard_Room() {
        return (EReference) distributionBoardEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getDistributionBoard_Rails() {
        return (EReference) distributionBoardEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getDistributionBoard_Devices() {
        return (EReference) distributionBoardEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getDINRail() {
        return dinRailEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getDINRail_CapacityModules() {
        return (EAttribute) dinRailEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getDINRail_Devices() {
        return (EReference) dinRailEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getModularDevice() {
        return modularDeviceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getModularDevice_LabelText() {
        return (EAttribute) modularDeviceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getModularDevice_Rail() {
        return (EReference) modularDeviceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getModularDevice_UpstreamRccb() {
        return (EReference) modularDeviceEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getModularDevice_UpstreamServiceBreaker() {
        return (EReference) modularDeviceEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getCircuit() {
        return circuitEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getCircuit_Phase() {
        return (EAttribute) circuitEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getCircuit_ConductorSectionMm2() {
        return (EAttribute) circuitEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getCircuit_SourceBoard() {
        return (EReference) circuitEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getCircuit_ProtectiveDevice() {
        return (EReference) circuitEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getFinalCircuit() {
        return finalCircuitEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getFinalCircuit_Usage() {
        return (EAttribute) finalCircuitEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getFinalCircuit_Equipment() {
        return (EReference) finalCircuitEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getFinalCircuit_Branches() {
        return (EReference) finalCircuitEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getDistributionCircuit() {
        return distributionCircuitEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getDistributionCircuit_TargetBoard() {
        return (EReference) distributionCircuitEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getControlFunction() {
        return controlFunctionEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getControlFunction_Mode() {
        return (EAttribute) controlFunctionEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getControlFunction_Channels() {
        return (EReference) controlFunctionEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getControlFunction_Targets() {
        return (EReference) controlFunctionEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getControlFunction_Actuator() {
        return (EReference) controlFunctionEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getServiceConnection() {
        return serviceConnectionEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getServiceConnection_Phase() {
        return (EAttribute) serviceConnectionEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getServiceConnection_SubscribedPowerKva() {
        return (EAttribute) serviceConnectionEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getServiceConnection_Meter() {
        return (EReference) serviceConnectionEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getServiceConnection_MainBreaker() {
        return (EReference) serviceConnectionEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getEnergyMeter() {
        return energyMeterEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getEnergyMeter_SerialNumber() {
        return (EAttribute) energyMeterEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getServiceCircuitBreaker() {
        return serviceCircuitBreakerEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getServiceCircuitBreaker_DownstreamDevices() {
        return (EReference) serviceCircuitBreakerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getCircuitBranch() {
        return circuitBranchEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getCircuitBranch_Circuit() {
        return (EReference) circuitBranchEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getCircuitBranch_Equipment() {
        return (EReference) circuitBranchEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EEnum getDeviceKind() {
        return deviceKindEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EEnum getSupplyPhase() {
        return supplyPhaseEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EEnum getCircuitPhase() {
        return circuitPhaseEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EEnum getCircuitUse() {
        return circuitUseEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EEnum getControlMode() {
        return controlModeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public HomeElectricalFactory getHomeElectricalFactory() {
        return (HomeElectricalFactory) getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) {
            return;
        }
        isCreated = true;

        // Create classes and their features
        namedElementEClass = createEClass(NAMED_ELEMENT);
        createEAttribute(namedElementEClass, NAMED_ELEMENT__NAME);
        createEAttribute(namedElementEClass, NAMED_ELEMENT__DESCRIPTION);

        technicalElementEClass = createEClass(TECHNICAL_ELEMENT);
        createEAttribute(technicalElementEClass, TECHNICAL_ELEMENT__RATED_CURRENT_A);
        createEAttribute(technicalElementEClass, TECHNICAL_ELEMENT__RATED_POWER_W);
        createEAttribute(technicalElementEClass, TECHNICAL_ELEMENT__WIDTH_MODULES);
        createEAttribute(technicalElementEClass, TECHNICAL_ELEMENT__RESIDUAL_CURRENT_MA);
        createEAttribute(technicalElementEClass, TECHNICAL_ELEMENT__RESIDUAL_TYPE);
        createEAttribute(technicalElementEClass, TECHNICAL_ELEMENT__TRIP_CURVE);
        createEAttribute(technicalElementEClass, TECHNICAL_ELEMENT__POLE_CONFIGURATION);

        dwellingEClass = createEClass(DWELLING);
        createEReference(dwellingEClass, DWELLING__CATALOG);
        createEReference(dwellingEClass, DWELLING__STOREYS);
        createEReference(dwellingEClass, DWELLING__EQUIPMENT);
        createEReference(dwellingEClass, DWELLING__BOARDS);
        createEReference(dwellingEClass, DWELLING__CIRCUITS);
        createEReference(dwellingEClass, DWELLING__CONTROL_FUNCTIONS);
        createEReference(dwellingEClass, DWELLING__SERVICE_CONNECTION);

        storeyEClass = createEClass(STOREY);
        createEAttribute(storeyEClass, STOREY__LEVEL_NUMBER);
        createEReference(storeyEClass, STOREY__ROOMS);
        createEAttribute(storeyEClass, STOREY__PLAN_IMAGE_ID);
        createEAttribute(storeyEClass, STOREY__PLAN_WIDTH);
        createEAttribute(storeyEClass, STOREY__PLAN_HEIGHT);
        createEAttribute(storeyEClass, STOREY__PLAN_ASPECT_RATIO);

        roomEClass = createEClass(ROOM);

        catalogEClass = createEClass(CATALOG);
        createEReference(catalogEClass, CATALOG__DEVICE_TYPES);

        deviceTypeEClass = createEClass(DEVICE_TYPE);
        createEAttribute(deviceTypeEClass, DEVICE_TYPE__KIND);
        createEAttribute(deviceTypeEClass, DEVICE_TYPE__MANUFACTURER);
        createEAttribute(deviceTypeEClass, DEVICE_TYPE__PRODUCT_REFERENCE);
        createEAttribute(deviceTypeEClass, DEVICE_TYPE__CHANNEL_COUNT);

        equipmentEClass = createEClass(EQUIPMENT);
        createEReference(equipmentEClass, EQUIPMENT__DEVICE_TYPE);

        fieldEquipmentEClass = createEClass(FIELD_EQUIPMENT);
        createEReference(fieldEquipmentEClass, FIELD_EQUIPMENT__ROOM);
        createEReference(fieldEquipmentEClass, FIELD_EQUIPMENT__CIRCUIT);
        createEReference(fieldEquipmentEClass, FIELD_EQUIPMENT__BRANCH);

        outletEClass = createEClass(OUTLET);

        electricalLoadEClass = createEClass(ELECTRICAL_LOAD);

        controlDeviceEClass = createEClass(CONTROL_DEVICE);
        createEReference(controlDeviceEClass, CONTROL_DEVICE__CHANNELS);

        controlChannelEClass = createEClass(CONTROL_CHANNEL);

        distributionBoardEClass = createEClass(DISTRIBUTION_BOARD);
        createEReference(distributionBoardEClass, DISTRIBUTION_BOARD__ROOM);
        createEReference(distributionBoardEClass, DISTRIBUTION_BOARD__RAILS);
        createEReference(distributionBoardEClass, DISTRIBUTION_BOARD__DEVICES);

        dinRailEClass = createEClass(DIN_RAIL);
        createEAttribute(dinRailEClass, DIN_RAIL__CAPACITY_MODULES);
        createEReference(dinRailEClass, DIN_RAIL__DEVICES);

        modularDeviceEClass = createEClass(MODULAR_DEVICE);
        createEAttribute(modularDeviceEClass, MODULAR_DEVICE__LABEL_TEXT);
        createEReference(modularDeviceEClass, MODULAR_DEVICE__RAIL);
        createEReference(modularDeviceEClass, MODULAR_DEVICE__UPSTREAM_RCCB);
        createEReference(modularDeviceEClass, MODULAR_DEVICE__UPSTREAM_SERVICE_BREAKER);

        circuitEClass = createEClass(CIRCUIT);
        createEAttribute(circuitEClass, CIRCUIT__PHASE);
        createEAttribute(circuitEClass, CIRCUIT__CONDUCTOR_SECTION_MM2);
        createEReference(circuitEClass, CIRCUIT__SOURCE_BOARD);
        createEReference(circuitEClass, CIRCUIT__PROTECTIVE_DEVICE);

        finalCircuitEClass = createEClass(FINAL_CIRCUIT);
        createEAttribute(finalCircuitEClass, FINAL_CIRCUIT__USAGE);
        createEReference(finalCircuitEClass, FINAL_CIRCUIT__EQUIPMENT);
        createEReference(finalCircuitEClass, FINAL_CIRCUIT__BRANCHES);

        distributionCircuitEClass = createEClass(DISTRIBUTION_CIRCUIT);
        createEReference(distributionCircuitEClass, DISTRIBUTION_CIRCUIT__TARGET_BOARD);

        controlFunctionEClass = createEClass(CONTROL_FUNCTION);
        createEAttribute(controlFunctionEClass, CONTROL_FUNCTION__MODE);
        createEReference(controlFunctionEClass, CONTROL_FUNCTION__CHANNELS);
        createEReference(controlFunctionEClass, CONTROL_FUNCTION__TARGETS);
        createEReference(controlFunctionEClass, CONTROL_FUNCTION__ACTUATOR);

        serviceConnectionEClass = createEClass(SERVICE_CONNECTION);
        createEAttribute(serviceConnectionEClass, SERVICE_CONNECTION__PHASE);
        createEAttribute(serviceConnectionEClass, SERVICE_CONNECTION__SUBSCRIBED_POWER_KVA);
        createEReference(serviceConnectionEClass, SERVICE_CONNECTION__METER);
        createEReference(serviceConnectionEClass, SERVICE_CONNECTION__MAIN_BREAKER);

        energyMeterEClass = createEClass(ENERGY_METER);
        createEAttribute(energyMeterEClass, ENERGY_METER__SERIAL_NUMBER);

        serviceCircuitBreakerEClass = createEClass(SERVICE_CIRCUIT_BREAKER);
        createEReference(serviceCircuitBreakerEClass, SERVICE_CIRCUIT_BREAKER__DOWNSTREAM_DEVICES);

        circuitBranchEClass = createEClass(CIRCUIT_BRANCH);
        createEReference(circuitBranchEClass, CIRCUIT_BRANCH__CIRCUIT);
        createEReference(circuitBranchEClass, CIRCUIT_BRANCH__EQUIPMENT);

        // Create enums
        deviceKindEEnum = createEEnum(DEVICE_KIND);
        supplyPhaseEEnum = createEEnum(SUPPLY_PHASE);
        circuitPhaseEEnum = createEEnum(CIRCUIT_PHASE);
        circuitUseEEnum = createEEnum(CIRCUIT_USE);
        controlModeEEnum = createEEnum(CONTROL_MODE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) {
            return;
        }
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes
        technicalElementEClass.getESuperTypes().add(this.getNamedElement());
        dwellingEClass.getESuperTypes().add(this.getNamedElement());
        storeyEClass.getESuperTypes().add(this.getNamedElement());
        roomEClass.getESuperTypes().add(this.getNamedElement());
        catalogEClass.getESuperTypes().add(this.getNamedElement());
        deviceTypeEClass.getESuperTypes().add(this.getTechnicalElement());
        equipmentEClass.getESuperTypes().add(this.getNamedElement());
        fieldEquipmentEClass.getESuperTypes().add(this.getEquipment());
        fieldEquipmentEClass.getESuperTypes().add(this.getTechnicalElement());
        outletEClass.getESuperTypes().add(this.getFieldEquipment());
        electricalLoadEClass.getESuperTypes().add(this.getFieldEquipment());
        controlDeviceEClass.getESuperTypes().add(this.getFieldEquipment());
        controlChannelEClass.getESuperTypes().add(this.getNamedElement());
        distributionBoardEClass.getESuperTypes().add(this.getNamedElement());
        dinRailEClass.getESuperTypes().add(this.getNamedElement());
        modularDeviceEClass.getESuperTypes().add(this.getEquipment());
        circuitEClass.getESuperTypes().add(this.getNamedElement());
        finalCircuitEClass.getESuperTypes().add(this.getCircuit());
        distributionCircuitEClass.getESuperTypes().add(this.getCircuit());
        controlFunctionEClass.getESuperTypes().add(this.getNamedElement());
        serviceConnectionEClass.getESuperTypes().add(this.getNamedElement());
        energyMeterEClass.getESuperTypes().add(this.getEquipment());
        energyMeterEClass.getESuperTypes().add(this.getTechnicalElement());
        serviceCircuitBreakerEClass.getESuperTypes().add(this.getEquipment());
        serviceCircuitBreakerEClass.getESuperTypes().add(this.getTechnicalElement());
        circuitBranchEClass.getESuperTypes().add(this.getNamedElement());

        // Initialize classes and features; add operations and parameters
        initEClass(namedElementEClass, NamedElement.class, "NamedElement", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getNamedElement_Name(), ecorePackage.getEString(), "name", null, 0, 1, NamedElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
                !IS_DERIVED, IS_ORDERED);
        initEAttribute(getNamedElement_Description(), ecorePackage.getEString(), "description", null, 0, 1, NamedElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
                IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(technicalElementEClass, TechnicalElement.class, "TechnicalElement", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getTechnicalElement_RatedCurrentA(), ecorePackage.getEDoubleObject(), "ratedCurrentA", null, 0, 1, TechnicalElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
                !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTechnicalElement_RatedPowerW(), ecorePackage.getEDoubleObject(), "ratedPowerW", null, 0, 1, TechnicalElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
                !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTechnicalElement_WidthModules(), ecorePackage.getEDoubleObject(), "widthModules", null, 0, 1, TechnicalElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
                !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTechnicalElement_ResidualCurrentMa(), ecorePackage.getEDoubleObject(), "residualCurrentMa", null, 0, 1, TechnicalElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
                !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTechnicalElement_ResidualType(), ecorePackage.getEString(), "residualType", null, 0, 1, TechnicalElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
                !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTechnicalElement_TripCurve(), ecorePackage.getEString(), "tripCurve", null, 0, 1, TechnicalElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
                IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTechnicalElement_PoleConfiguration(), ecorePackage.getEString(), "poleConfiguration", null, 0, 1, TechnicalElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
                !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(dwellingEClass, Dwelling.class, "Dwelling", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getDwelling_Catalog(), this.getCatalog(), null, "catalog", null, 1, 1, Dwelling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDwelling_Storeys(), this.getStorey(), null, "storeys", null, 0, -1, Dwelling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDwelling_Equipment(), this.getFieldEquipment(), null, "equipment", null, 0, -1, Dwelling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDwelling_Boards(), this.getDistributionBoard(), null, "boards", null, 0, -1, Dwelling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDwelling_Circuits(), this.getCircuit(), null, "circuits", null, 0, -1, Dwelling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDwelling_ControlFunctions(), this.getControlFunction(), null, "controlFunctions", null, 0, -1, Dwelling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
                !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDwelling_ServiceConnection(), this.getServiceConnection(), null, "serviceConnection", null, 0, 1, Dwelling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
                !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(storeyEClass, Storey.class, "Storey", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getStorey_LevelNumber(), ecorePackage.getEIntegerObject(), "levelNumber", null, 0, 1, Storey.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
                IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getStorey_Rooms(), this.getRoom(), null, "rooms", null, 0, -1, Storey.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
                IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStorey_PlanImageId(), ecorePackage.getEString(), "planImageId", null, 0, 1, Storey.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
                !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStorey_PlanWidth(), ecorePackage.getEInt(), "planWidth", "1200", 0, 1, Storey.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
                !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStorey_PlanHeight(), ecorePackage.getEInt(), "planHeight", "800", 0, 1, Storey.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
                !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStorey_PlanAspectRatio(), ecorePackage.getEDouble(), "planAspectRatio", "0.0", 0, 1, Storey.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
                IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(roomEClass, Room.class, "Room", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(catalogEClass, Catalog.class, "Catalog", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getCatalog_DeviceTypes(), this.getDeviceType(), null, "deviceTypes", null, 0, -1, Catalog.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(deviceTypeEClass, DeviceType.class, "DeviceType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getDeviceType_Kind(), this.getDeviceKind(), "kind", null, 0, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(getDeviceType_Manufacturer(), ecorePackage.getEString(), "manufacturer", null, 0, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
                IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getDeviceType_ProductReference(), ecorePackage.getEString(), "productReference", null, 0, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
                !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getDeviceType_ChannelCount(), ecorePackage.getEInt(), "channelCount", "1", 0, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
                !IS_DERIVED, IS_ORDERED);

        initEClass(equipmentEClass, Equipment.class, "Equipment", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getEquipment_DeviceType(), this.getDeviceType(), null, "deviceType", null, 1, 1, Equipment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(fieldEquipmentEClass, FieldEquipment.class, "FieldEquipment", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getFieldEquipment_Room(), this.getRoom(), null, "room", null, 0, 1, FieldEquipment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getFieldEquipment_Circuit(), this.getFinalCircuit(), this.getFinalCircuit_Equipment(), "circuit", null, 0, 1, FieldEquipment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
                !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getFieldEquipment_Branch(), this.getCircuitBranch(), this.getCircuitBranch_Equipment(), "branch", null, 0, 1, FieldEquipment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
                !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(outletEClass, Outlet.class, "Outlet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(electricalLoadEClass, ElectricalLoad.class, "ElectricalLoad", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(controlDeviceEClass, ControlDevice.class, "ControlDevice", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getControlDevice_Channels(), this.getControlChannel(), null, "channels", null, 1, -1, ControlDevice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
                !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(controlChannelEClass, ControlChannel.class, "ControlChannel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(distributionBoardEClass, DistributionBoard.class, "DistributionBoard", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getDistributionBoard_Room(), this.getRoom(), null, "room", null, 0, 1, DistributionBoard.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDistributionBoard_Rails(), this.getDINRail(), null, "rails", null, 0, -1, DistributionBoard.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
                !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDistributionBoard_Devices(), this.getModularDevice(), null, "devices", null, 0, -1, DistributionBoard.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
                !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(dinRailEClass, DINRail.class, "DINRail", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getDINRail_CapacityModules(), ecorePackage.getEInt(), "capacityModules", "13", 0, 1, DINRail.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
                IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDINRail_Devices(), this.getModularDevice(), this.getModularDevice_Rail(), "devices", null, 0, -1, DINRail.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
                IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(modularDeviceEClass, ModularDevice.class, "ModularDevice", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getModularDevice_LabelText(), ecorePackage.getEString(), "labelText", null, 0, 1, ModularDevice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
                IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getModularDevice_Rail(), this.getDINRail(), this.getDINRail_Devices(), "rail", null, 1, 1, ModularDevice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
                IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getModularDevice_UpstreamRccb(), this.getModularDevice(), null, "upstreamRccb", null, 0, 1, ModularDevice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
                IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getModularDevice_UpstreamServiceBreaker(), this.getServiceCircuitBreaker(), this.getServiceCircuitBreaker_DownstreamDevices(), "upstreamServiceBreaker", null, 0, 1,
                ModularDevice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(circuitEClass, Circuit.class, "Circuit", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getCircuit_Phase(), this.getCircuitPhase(), "phase", null, 0, 1, Circuit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(getCircuit_ConductorSectionMm2(), ecorePackage.getEDoubleObject(), "conductorSectionMm2", null, 0, 1, Circuit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
                !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getCircuit_SourceBoard(), this.getDistributionBoard(), null, "sourceBoard", null, 0, 1, Circuit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
                IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getCircuit_ProtectiveDevice(), this.getModularDevice(), null, "protectiveDevice", null, 0, 1, Circuit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
                IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(finalCircuitEClass, FinalCircuit.class, "FinalCircuit", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getFinalCircuit_Usage(), this.getCircuitUse(), "usage", null, 0, 1, FinalCircuit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
                !IS_DERIVED, IS_ORDERED);
        initEReference(getFinalCircuit_Equipment(), this.getFieldEquipment(), this.getFieldEquipment_Circuit(), "equipment", null, 0, -1, FinalCircuit.class, !IS_TRANSIENT, !IS_VOLATILE,
                IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getFinalCircuit_Branches(), this.getCircuitBranch(), this.getCircuitBranch_Circuit(), "branches", null, 0, -1, FinalCircuit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
                IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(distributionCircuitEClass, DistributionCircuit.class, "DistributionCircuit", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getDistributionCircuit_TargetBoard(), this.getDistributionBoard(), null, "targetBoard", null, 0, 1, DistributionCircuit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
                !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(controlFunctionEClass, ControlFunction.class, "ControlFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getControlFunction_Mode(), this.getControlMode(), "mode", null, 0, 1, ControlFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
                !IS_DERIVED, IS_ORDERED);
        initEReference(getControlFunction_Channels(), this.getControlChannel(), null, "channels", null, 0, -1, ControlFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
                IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getControlFunction_Targets(), this.getFieldEquipment(), null, "targets", null, 0, -1, ControlFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
                IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getControlFunction_Actuator(), this.getModularDevice(), null, "actuator", null, 0, 1, ControlFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
                IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(serviceConnectionEClass, ServiceConnection.class, "ServiceConnection", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getServiceConnection_Phase(), this.getSupplyPhase(), "phase", null, 0, 1, ServiceConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
                !IS_DERIVED, IS_ORDERED);
        initEAttribute(getServiceConnection_SubscribedPowerKva(), ecorePackage.getEDoubleObject(), "subscribedPowerKva", null, 0, 1, ServiceConnection.class, !IS_TRANSIENT, !IS_VOLATILE,
                IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getServiceConnection_Meter(), this.getEnergyMeter(), null, "meter", null, 0, 1, ServiceConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
                !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getServiceConnection_MainBreaker(), this.getServiceCircuitBreaker(), null, "mainBreaker", null, 0, 1, ServiceConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
                IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(energyMeterEClass, EnergyMeter.class, "EnergyMeter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getEnergyMeter_SerialNumber(), ecorePackage.getEString(), "serialNumber", null, 0, 1, EnergyMeter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
                IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(serviceCircuitBreakerEClass, ServiceCircuitBreaker.class, "ServiceCircuitBreaker", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getServiceCircuitBreaker_DownstreamDevices(), this.getModularDevice(), this.getModularDevice_UpstreamServiceBreaker(), "downstreamDevices", null, 0, -1,
                ServiceCircuitBreaker.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(circuitBranchEClass, CircuitBranch.class, "CircuitBranch", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getCircuitBranch_Circuit(), this.getFinalCircuit(), this.getFinalCircuit_Branches(), "circuit", null, 1, 1, CircuitBranch.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
                !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getCircuitBranch_Equipment(), this.getFieldEquipment(), this.getFieldEquipment_Branch(), "equipment", null, 0, -1, CircuitBranch.class, !IS_TRANSIENT, !IS_VOLATILE,
                IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Initialize enums and add enum literals
        initEEnum(deviceKindEEnum, DeviceKind.class, "DeviceKind");
        addEEnumLiteral(deviceKindEEnum, DeviceKind.OUTLET);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.SWITCH);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.PUSH_BUTTON);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.DIMMER);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.SHUTTER_CONTROL);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.LUMINAIRE);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.SHUTTER_MOTOR);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.HEATER);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.WATER_HEATER);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.VENTILATION);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.COOKER);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.FIXED_APPLIANCE);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.MCB);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.RCCB);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.RCBO);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.FUSE);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.ISOLATOR);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.SPD);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.IMPULSE_RELAY);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.CONTACTOR);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.ENERGY_METER);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.SERVICE_BREAKER);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.OTHER_FIELD);
        addEEnumLiteral(deviceKindEEnum, DeviceKind.OTHER_MODULAR);

        initEEnum(supplyPhaseEEnum, SupplyPhase.class, "SupplyPhase");
        addEEnumLiteral(supplyPhaseEEnum, SupplyPhase.UNKNOWN);
        addEEnumLiteral(supplyPhaseEEnum, SupplyPhase.SINGLE_PHASE);
        addEEnumLiteral(supplyPhaseEEnum, SupplyPhase.THREE_PHASE);

        initEEnum(circuitPhaseEEnum, CircuitPhase.class, "CircuitPhase");
        addEEnumLiteral(circuitPhaseEEnum, CircuitPhase.UNKNOWN);
        addEEnumLiteral(circuitPhaseEEnum, CircuitPhase.L1);
        addEEnumLiteral(circuitPhaseEEnum, CircuitPhase.L2);
        addEEnumLiteral(circuitPhaseEEnum, CircuitPhase.L3);
        addEEnumLiteral(circuitPhaseEEnum, CircuitPhase.THREE_PHASE);

        initEEnum(circuitUseEEnum, CircuitUse.class, "CircuitUse");
        addEEnumLiteral(circuitUseEEnum, CircuitUse.UNKNOWN);
        addEEnumLiteral(circuitUseEEnum, CircuitUse.LIGHTING);
        addEEnumLiteral(circuitUseEEnum, CircuitUse.SOCKET_OUTLETS);
        addEEnumLiteral(circuitUseEEnum, CircuitUse.SHUTTERS);
        addEEnumLiteral(circuitUseEEnum, CircuitUse.HEATING);
        addEEnumLiteral(circuitUseEEnum, CircuitUse.WATER_HEATING);
        addEEnumLiteral(circuitUseEEnum, CircuitUse.VENTILATION);
        addEEnumLiteral(circuitUseEEnum, CircuitUse.COOKING);
        addEEnumLiteral(circuitUseEEnum, CircuitUse.DEDICATED_APPLIANCE);
        addEEnumLiteral(circuitUseEEnum, CircuitUse.OTHER);

        initEEnum(controlModeEEnum, ControlMode.class, "ControlMode");
        addEEnumLiteral(controlModeEEnum, ControlMode.SWITCHING);
        addEEnumLiteral(controlModeEEnum, ControlMode.DIMMING);
        addEEnumLiteral(controlModeEEnum, ControlMode.SHUTTER);

        // Create resource
        createResource(eNS_URI);
    }

} //HomeElectricalPackageImpl
