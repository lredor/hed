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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see hed.homeelectrical.HomeElectricalPackage
 * @generated
 */
public class HomeElectricalSwitch<T> extends Switch<T> {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static HomeElectricalPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public HomeElectricalSwitch() {
        if (modelPackage == null) {
            modelPackage = HomeElectricalPackage.eINSTANCE;
        }
    }

    /**
     * Checks whether this is a switch for the given package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param ePackage the package in question.
     * @return whether this is a switch for the given package.
     * @generated
     */
    @Override
    protected boolean isSwitchFor(EPackage ePackage) {
        return ePackage == modelPackage;
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    @Override
    protected T doSwitch(int classifierID, EObject theEObject) {
        switch (classifierID) {
            case HomeElectricalPackage.NAMED_ELEMENT: {
                NamedElement namedElement = (NamedElement) theEObject;
                T result = caseNamedElement(namedElement);
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.TECHNICAL_ELEMENT: {
                TechnicalElement technicalElement = (TechnicalElement) theEObject;
                T result = caseTechnicalElement(technicalElement);
                if (result == null) {
                    result = caseNamedElement(technicalElement);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.DWELLING: {
                Dwelling dwelling = (Dwelling) theEObject;
                T result = caseDwelling(dwelling);
                if (result == null) {
                    result = caseNamedElement(dwelling);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.STOREY: {
                Storey storey = (Storey) theEObject;
                T result = caseStorey(storey);
                if (result == null) {
                    result = caseNamedElement(storey);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.ROOM: {
                Room room = (Room) theEObject;
                T result = caseRoom(room);
                if (result == null) {
                    result = caseNamedElement(room);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.CATALOG: {
                Catalog catalog = (Catalog) theEObject;
                T result = caseCatalog(catalog);
                if (result == null) {
                    result = caseNamedElement(catalog);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.DEVICE_TYPE: {
                DeviceType deviceType = (DeviceType) theEObject;
                T result = caseDeviceType(deviceType);
                if (result == null) {
                    result = caseTechnicalElement(deviceType);
                }
                if (result == null) {
                    result = caseNamedElement(deviceType);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.EQUIPMENT: {
                Equipment equipment = (Equipment) theEObject;
                T result = caseEquipment(equipment);
                if (result == null) {
                    result = caseNamedElement(equipment);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.FIELD_EQUIPMENT: {
                FieldEquipment fieldEquipment = (FieldEquipment) theEObject;
                T result = caseFieldEquipment(fieldEquipment);
                if (result == null) {
                    result = caseEquipment(fieldEquipment);
                }
                if (result == null) {
                    result = caseTechnicalElement(fieldEquipment);
                }
                if (result == null) {
                    result = caseNamedElement(fieldEquipment);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.OUTLET: {
                Outlet outlet = (Outlet) theEObject;
                T result = caseOutlet(outlet);
                if (result == null) {
                    result = caseFieldEquipment(outlet);
                }
                if (result == null) {
                    result = caseEquipment(outlet);
                }
                if (result == null) {
                    result = caseTechnicalElement(outlet);
                }
                if (result == null) {
                    result = caseNamedElement(outlet);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.ELECTRICAL_LOAD: {
                ElectricalLoad electricalLoad = (ElectricalLoad) theEObject;
                T result = caseElectricalLoad(electricalLoad);
                if (result == null) {
                    result = caseFieldEquipment(electricalLoad);
                }
                if (result == null) {
                    result = caseEquipment(electricalLoad);
                }
                if (result == null) {
                    result = caseTechnicalElement(electricalLoad);
                }
                if (result == null) {
                    result = caseNamedElement(electricalLoad);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.CONTROL_DEVICE: {
                ControlDevice controlDevice = (ControlDevice) theEObject;
                T result = caseControlDevice(controlDevice);
                if (result == null) {
                    result = caseFieldEquipment(controlDevice);
                }
                if (result == null) {
                    result = caseEquipment(controlDevice);
                }
                if (result == null) {
                    result = caseTechnicalElement(controlDevice);
                }
                if (result == null) {
                    result = caseNamedElement(controlDevice);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.CONTROL_CHANNEL: {
                ControlChannel controlChannel = (ControlChannel) theEObject;
                T result = caseControlChannel(controlChannel);
                if (result == null) {
                    result = caseNamedElement(controlChannel);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.DISTRIBUTION_BOARD: {
                DistributionBoard distributionBoard = (DistributionBoard) theEObject;
                T result = caseDistributionBoard(distributionBoard);
                if (result == null) {
                    result = caseNamedElement(distributionBoard);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.DIN_RAIL: {
                DINRail dinRail = (DINRail) theEObject;
                T result = caseDINRail(dinRail);
                if (result == null) {
                    result = caseNamedElement(dinRail);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.MODULAR_DEVICE: {
                ModularDevice modularDevice = (ModularDevice) theEObject;
                T result = caseModularDevice(modularDevice);
                if (result == null) {
                    result = caseEquipment(modularDevice);
                }
                if (result == null) {
                    result = caseNamedElement(modularDevice);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.CIRCUIT: {
                Circuit circuit = (Circuit) theEObject;
                T result = caseCircuit(circuit);
                if (result == null) {
                    result = caseNamedElement(circuit);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.FINAL_CIRCUIT: {
                FinalCircuit finalCircuit = (FinalCircuit) theEObject;
                T result = caseFinalCircuit(finalCircuit);
                if (result == null) {
                    result = caseCircuit(finalCircuit);
                }
                if (result == null) {
                    result = caseNamedElement(finalCircuit);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.DISTRIBUTION_CIRCUIT: {
                DistributionCircuit distributionCircuit = (DistributionCircuit) theEObject;
                T result = caseDistributionCircuit(distributionCircuit);
                if (result == null) {
                    result = caseCircuit(distributionCircuit);
                }
                if (result == null) {
                    result = caseNamedElement(distributionCircuit);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.CONTROL_FUNCTION: {
                ControlFunction controlFunction = (ControlFunction) theEObject;
                T result = caseControlFunction(controlFunction);
                if (result == null) {
                    result = caseNamedElement(controlFunction);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.SERVICE_CONNECTION: {
                ServiceConnection serviceConnection = (ServiceConnection) theEObject;
                T result = caseServiceConnection(serviceConnection);
                if (result == null) {
                    result = caseNamedElement(serviceConnection);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.ENERGY_METER: {
                EnergyMeter energyMeter = (EnergyMeter) theEObject;
                T result = caseEnergyMeter(energyMeter);
                if (result == null) {
                    result = caseEquipment(energyMeter);
                }
                if (result == null) {
                    result = caseTechnicalElement(energyMeter);
                }
                if (result == null) {
                    result = caseNamedElement(energyMeter);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.SERVICE_CIRCUIT_BREAKER: {
                ServiceCircuitBreaker serviceCircuitBreaker = (ServiceCircuitBreaker) theEObject;
                T result = caseServiceCircuitBreaker(serviceCircuitBreaker);
                if (result == null) {
                    result = caseEquipment(serviceCircuitBreaker);
                }
                if (result == null) {
                    result = caseTechnicalElement(serviceCircuitBreaker);
                }
                if (result == null) {
                    result = caseNamedElement(serviceCircuitBreaker);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case HomeElectricalPackage.CIRCUIT_BRANCH: {
                CircuitBranch circuitBranch = (CircuitBranch) theEObject;
                T result = caseCircuitBranch(circuitBranch);
                if (result == null) {
                    result = caseNamedElement(circuitBranch);
                }
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            default:
                return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Named Element</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Named Element</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseNamedElement(NamedElement object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Technical Element</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Technical Element</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseTechnicalElement(TechnicalElement object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Dwelling</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Dwelling</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseDwelling(Dwelling object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Storey</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Storey</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseStorey(Storey object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Room</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Room</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseRoom(Room object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Catalog</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Catalog</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseCatalog(Catalog object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Device Type</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Device Type</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseDeviceType(DeviceType object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Equipment</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Equipment</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseEquipment(Equipment object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Field Equipment</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Field Equipment</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseFieldEquipment(FieldEquipment object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Outlet</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Outlet</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseOutlet(Outlet object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Electrical Load</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Electrical Load</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseElectricalLoad(ElectricalLoad object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Control Device</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Control Device</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseControlDevice(ControlDevice object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Control Channel</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Control Channel</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseControlChannel(ControlChannel object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Distribution Board</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Distribution Board</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseDistributionBoard(DistributionBoard object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>DIN Rail</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>DIN Rail</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseDINRail(DINRail object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Modular Device</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Modular Device</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseModularDevice(ModularDevice object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Circuit</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Circuit</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseCircuit(Circuit object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Final Circuit</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Final Circuit</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseFinalCircuit(FinalCircuit object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Distribution Circuit</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Distribution Circuit</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseDistributionCircuit(DistributionCircuit object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Control Function</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Control Function</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseControlFunction(ControlFunction object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Service Connection</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Service Connection</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseServiceConnection(ServiceConnection object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Energy Meter</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Energy Meter</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseEnergyMeter(EnergyMeter object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Service Circuit Breaker</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Service Circuit Breaker</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseServiceCircuitBreaker(ServiceCircuitBreaker object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Circuit Branch</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Circuit Branch</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseCircuitBranch(CircuitBranch object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    @Override
    public T defaultCase(EObject object) {
        return null;
    }

} //HomeElectricalSwitch
