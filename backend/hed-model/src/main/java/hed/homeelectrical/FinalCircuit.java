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
 * A representation of the model object '<em><b>Final Circuit</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.FinalCircuit#getUsage <em>Usage</em>}</li>
 *   <li>{@link hed.homeelectrical.FinalCircuit#getBranches <em>Branches</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getFinalCircuit()
 * @model
 * @generated
 */
public interface FinalCircuit extends Circuit {
    /**
     * Returns the value of the '<em><b>Usage</b></em>' attribute.
     * The literals are from the enumeration {@link hed.homeelectrical.CircuitUse}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Usage</em>' attribute.
     * @see hed.homeelectrical.CircuitUse
     * @see #setUsage(CircuitUse)
     * @see hed.homeelectrical.HomeElectricalPackage#getFinalCircuit_Usage()
     * @model
     * @generated
     */
    CircuitUse getUsage();

    /**
     * Sets the value of the '{@link hed.homeelectrical.FinalCircuit#getUsage <em>Usage</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Usage</em>' attribute.
     * @see hed.homeelectrical.CircuitUse
     * @see #getUsage()
     * @generated
     */
    void setUsage(CircuitUse value);

    /**
     * Returns the value of the '<em><b>Branches</b></em>' containment reference list.
     * The list contents are of type {@link hed.homeelectrical.CircuitBranch}.
     * It is bidirectional and its opposite is '{@link hed.homeelectrical.CircuitBranch#getCircuit <em>Circuit</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Branches</em>' containment reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getFinalCircuit_Branches()
     * @see hed.homeelectrical.CircuitBranch#getCircuit
     * @model opposite="circuit" containment="true"
     * @generated
     */
    EList<CircuitBranch> getBranches();

} // FinalCircuit
