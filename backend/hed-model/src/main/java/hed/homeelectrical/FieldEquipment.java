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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Field Equipment</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.FieldEquipment#getRoom <em>Room</em>}</li>
 *   <li>{@link hed.homeelectrical.FieldEquipment#getCircuit <em>Circuit</em>}</li>
 *   <li>{@link hed.homeelectrical.FieldEquipment#getBranch <em>Branch</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getFieldEquipment()
 * @model abstract="true"
 * @generated
 */
public interface FieldEquipment extends Equipment, TechnicalElement {
    /**
     * Returns the value of the '<em><b>Room</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Room</em>' reference.
     * @see #setRoom(Room)
     * @see hed.homeelectrical.HomeElectricalPackage#getFieldEquipment_Room()
     * @model
     * @generated
     */
    Room getRoom();

    /**
     * Sets the value of the '{@link hed.homeelectrical.FieldEquipment#getRoom <em>Room</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Room</em>' reference.
     * @see #getRoom()
     * @generated
     */
    void setRoom(Room value);

    /**
     * Returns the value of the '<em><b>Circuit</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Circuit</em>' reference.
     * @see #setCircuit(FinalCircuit)
     * @see hed.homeelectrical.HomeElectricalPackage#getFieldEquipment_Circuit()
     * @model
     * @generated
     */
    FinalCircuit getCircuit();

    /**
     * Sets the value of the '{@link hed.homeelectrical.FieldEquipment#getCircuit <em>Circuit</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Circuit</em>' reference.
     * @see #getCircuit()
     * @generated
     */
    void setCircuit(FinalCircuit value);

    /**
     * Returns the value of the '<em><b>Branch</b></em>' reference.
     * It is bidirectional and its opposite is '{@link hed.homeelectrical.CircuitBranch#getEquipment <em>Equipment</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Branch</em>' reference.
     * @see #setBranch(CircuitBranch)
     * @see hed.homeelectrical.HomeElectricalPackage#getFieldEquipment_Branch()
     * @see hed.homeelectrical.CircuitBranch#getEquipment
     * @model opposite="equipment"
     * @generated
     */
    CircuitBranch getBranch();

    /**
     * Sets the value of the '{@link hed.homeelectrical.FieldEquipment#getBranch <em>Branch</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Branch</em>' reference.
     * @see #getBranch()
     * @generated
     */
    void setBranch(CircuitBranch value);

} // FieldEquipment
