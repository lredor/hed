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
 * A representation of the model object '<em><b>Room</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.Room#getUsage <em>Usage</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getRoom()
 * @model
 * @generated
 */
public interface Room extends NamedElement {
    /**
     * Returns the value of the '<em><b>Usage</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Usage</em>' attribute.
     * @see #setUsage(String)
     * @see hed.homeelectrical.HomeElectricalPackage#getRoom_Usage()
     * @model
     * @generated
     */
    String getUsage();

    /**
     * Sets the value of the '{@link hed.homeelectrical.Room#getUsage <em>Usage</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Usage</em>' attribute.
     * @see #getUsage()
     * @generated
     */
    void setUsage(String value);

} // Room
