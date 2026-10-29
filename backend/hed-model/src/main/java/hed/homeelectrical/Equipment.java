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
 * A representation of the model object '<em><b>Equipment</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.Equipment#getDeviceType <em>Device Type</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getEquipment()
 * @model abstract="true"
 * @generated
 */
public interface Equipment extends NamedElement {
    /**
     * Returns the value of the '<em><b>Device Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Device Type</em>' reference.
     * @see #setDeviceType(DeviceType)
     * @see hed.homeelectrical.HomeElectricalPackage#getEquipment_DeviceType()
     * @model required="true"
     * @generated
     */
    DeviceType getDeviceType();

    /**
     * Sets the value of the '{@link hed.homeelectrical.Equipment#getDeviceType <em>Device Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Device Type</em>' reference.
     * @see #getDeviceType()
     * @generated
     */
    void setDeviceType(DeviceType value);

} // Equipment
