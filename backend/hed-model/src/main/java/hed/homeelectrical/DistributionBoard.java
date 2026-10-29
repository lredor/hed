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
 * A representation of the model object '<em><b>Distribution Board</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.DistributionBoard#getRoom <em>Room</em>}</li>
 *   <li>{@link hed.homeelectrical.DistributionBoard#getRails <em>Rails</em>}</li>
 *   <li>{@link hed.homeelectrical.DistributionBoard#getDevices <em>Devices</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getDistributionBoard()
 * @model
 * @generated
 */
public interface DistributionBoard extends NamedElement {
    /**
     * Returns the value of the '<em><b>Room</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Room</em>' reference.
     * @see #setRoom(Room)
     * @see hed.homeelectrical.HomeElectricalPackage#getDistributionBoard_Room()
     * @model
     * @generated
     */
    Room getRoom();

    /**
     * Sets the value of the '{@link hed.homeelectrical.DistributionBoard#getRoom <em>Room</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Room</em>' reference.
     * @see #getRoom()
     * @generated
     */
    void setRoom(Room value);

    /**
     * Returns the value of the '<em><b>Rails</b></em>' containment reference list.
     * The list contents are of type {@link hed.homeelectrical.DINRail}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Rails</em>' containment reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getDistributionBoard_Rails()
     * @model containment="true"
     * @generated
     */
    EList<DINRail> getRails();

    /**
     * Returns the value of the '<em><b>Devices</b></em>' containment reference list.
     * The list contents are of type {@link hed.homeelectrical.ModularDevice}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Devices</em>' containment reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getDistributionBoard_Devices()
     * @model containment="true"
     * @generated
     */
    EList<ModularDevice> getDevices();

} // DistributionBoard
