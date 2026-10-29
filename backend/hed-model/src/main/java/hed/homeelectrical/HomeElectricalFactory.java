/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.homeelectrical;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see hed.homeelectrical.HomeElectricalPackage
 * @generated
 */
public interface HomeElectricalFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    HomeElectricalFactory eINSTANCE = hed.homeelectrical.impl.HomeElectricalFactoryImpl.init();

    /**
     * Returns a new object of class '<em>Dwelling</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Dwelling</em>'.
     * @generated
     */
    Dwelling createDwelling();

    /**
     * Returns a new object of class '<em>Storey</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Storey</em>'.
     * @generated
     */
    Storey createStorey();

    /**
     * Returns a new object of class '<em>Room</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Room</em>'.
     * @generated
     */
    Room createRoom();

    /**
     * Returns a new object of class '<em>Catalog</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Catalog</em>'.
     * @generated
     */
    Catalog createCatalog();

    /**
     * Returns a new object of class '<em>Device Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Device Type</em>'.
     * @generated
     */
    DeviceType createDeviceType();

    /**
     * Returns a new object of class '<em>Outlet</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Outlet</em>'.
     * @generated
     */
    Outlet createOutlet();

    /**
     * Returns a new object of class '<em>Electrical Load</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Electrical Load</em>'.
     * @generated
     */
    ElectricalLoad createElectricalLoad();

    /**
     * Returns a new object of class '<em>Control Device</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Control Device</em>'.
     * @generated
     */
    ControlDevice createControlDevice();

    /**
     * Returns a new object of class '<em>Control Channel</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Control Channel</em>'.
     * @generated
     */
    ControlChannel createControlChannel();

    /**
     * Returns a new object of class '<em>Distribution Board</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Distribution Board</em>'.
     * @generated
     */
    DistributionBoard createDistributionBoard();

    /**
     * Returns a new object of class '<em>DIN Rail</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>DIN Rail</em>'.
     * @generated
     */
    DINRail createDINRail();

    /**
     * Returns a new object of class '<em>Modular Device</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Modular Device</em>'.
     * @generated
     */
    ModularDevice createModularDevice();

    /**
     * Returns a new object of class '<em>Final Circuit</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Final Circuit</em>'.
     * @generated
     */
    FinalCircuit createFinalCircuit();

    /**
     * Returns a new object of class '<em>Distribution Circuit</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Distribution Circuit</em>'.
     * @generated
     */
    DistributionCircuit createDistributionCircuit();

    /**
     * Returns a new object of class '<em>Control Function</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Control Function</em>'.
     * @generated
     */
    ControlFunction createControlFunction();

    /**
     * Returns a new object of class '<em>Service Connection</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Service Connection</em>'.
     * @generated
     */
    ServiceConnection createServiceConnection();

    /**
     * Returns a new object of class '<em>Energy Meter</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Energy Meter</em>'.
     * @generated
     */
    EnergyMeter createEnergyMeter();

    /**
     * Returns a new object of class '<em>Service Circuit Breaker</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Service Circuit Breaker</em>'.
     * @generated
     */
    ServiceCircuitBreaker createServiceCircuitBreaker();

    /**
     * Returns a new object of class '<em>Circuit Branch</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Circuit Branch</em>'.
     * @generated
     */
    CircuitBranch createCircuitBranch();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    HomeElectricalPackage getHomeElectricalPackage();

} //HomeElectricalFactory
