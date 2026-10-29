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
 * A representation of the model object '<em><b>Storey</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.Storey#getLevelNumber <em>Level Number</em>}</li>
 *   <li>{@link hed.homeelectrical.Storey#getRooms <em>Rooms</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getStorey()
 * @model
 * @generated
 */
public interface Storey extends NamedElement {
    /**
     * Returns the value of the '<em><b>Level Number</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Level Number</em>' attribute.
     * @see #setLevelNumber(Integer)
     * @see hed.homeelectrical.HomeElectricalPackage#getStorey_LevelNumber()
     * @model
     * @generated
     */
    Integer getLevelNumber();

    /**
     * Sets the value of the '{@link hed.homeelectrical.Storey#getLevelNumber <em>Level Number</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Level Number</em>' attribute.
     * @see #getLevelNumber()
     * @generated
     */
    void setLevelNumber(Integer value);

    /**
     * Returns the value of the '<em><b>Rooms</b></em>' containment reference list.
     * The list contents are of type {@link hed.homeelectrical.Room}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Rooms</em>' containment reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getStorey_Rooms()
     * @model containment="true"
     * @generated
     */
    EList<Room> getRooms();

} // Storey
