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
 * A representation of the model object '<em><b>Energy Meter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.EnergyMeter#getSerialNumber <em>Serial Number</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getEnergyMeter()
 * @model
 * @generated
 */
public interface EnergyMeter extends Equipment, TechnicalElement {
    /**
     * Returns the value of the '<em><b>Serial Number</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Serial Number</em>' attribute.
     * @see #setSerialNumber(String)
     * @see hed.homeelectrical.HomeElectricalPackage#getEnergyMeter_SerialNumber()
     * @model
     * @generated
     */
    String getSerialNumber();

    /**
     * Sets the value of the '{@link hed.homeelectrical.EnergyMeter#getSerialNumber <em>Serial Number</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Serial Number</em>' attribute.
     * @see #getSerialNumber()
     * @generated
     */
    void setSerialNumber(String value);

} // EnergyMeter
