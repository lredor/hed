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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Circuit Branch</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.CircuitBranch#getCircuit <em>Circuit</em>}</li>
 *   <li>{@link hed.homeelectrical.CircuitBranch#getEquipment <em>Equipment</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getCircuitBranch()
 * @model
 * @generated
 */
public interface CircuitBranch extends NamedElement {
    /**
     * Returns the value of the '<em><b>Circuit</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link hed.homeelectrical.FinalCircuit#getBranches <em>Branches</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Circuit</em>' container reference.
     * @see #setCircuit(FinalCircuit)
     * @see hed.homeelectrical.HomeElectricalPackage#getCircuitBranch_Circuit()
     * @see hed.homeelectrical.FinalCircuit#getBranches
     * @model opposite="branches" required="true" transient="false"
     * @generated
     */
    FinalCircuit getCircuit();

    /**
     * Sets the value of the '{@link hed.homeelectrical.CircuitBranch#getCircuit <em>Circuit</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Circuit</em>' container reference.
     * @see #getCircuit()
     * @generated
     */
    void setCircuit(FinalCircuit value);

    /**
     * Returns the value of the '<em><b>Equipment</b></em>' reference list.
     * The list contents are of type {@link hed.homeelectrical.FieldEquipment}.
     * It is bidirectional and its opposite is '{@link hed.homeelectrical.FieldEquipment#getBranch <em>Branch</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Equipment</em>' reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getCircuitBranch_Equipment()
     * @see hed.homeelectrical.FieldEquipment#getBranch
     * @model opposite="branch"
     * @generated
     */
    EList<FieldEquipment> getEquipment();

} // CircuitBranch
