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
 * A representation of the model object '<em><b>Distribution Circuit</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.DistributionCircuit#getTargetBoard <em>Target Board</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getDistributionCircuit()
 * @model
 * @generated
 */
public interface DistributionCircuit extends Circuit {
    /**
     * Returns the value of the '<em><b>Target Board</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Target Board</em>' reference.
     * @see #setTargetBoard(DistributionBoard)
     * @see hed.homeelectrical.HomeElectricalPackage#getDistributionCircuit_TargetBoard()
     * @model
     * @generated
     */
    DistributionBoard getTargetBoard();

    /**
     * Sets the value of the '{@link hed.homeelectrical.DistributionCircuit#getTargetBoard <em>Target Board</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Target Board</em>' reference.
     * @see #getTargetBoard()
     * @generated
     */
    void setTargetBoard(DistributionBoard value);

} // DistributionCircuit
