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
 * A representation of the model object '<em><b>Device Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.DeviceType#getKind <em>Kind</em>}</li>
 *   <li>{@link hed.homeelectrical.DeviceType#getManufacturer <em>Manufacturer</em>}</li>
 *   <li>{@link hed.homeelectrical.DeviceType#getProductReference <em>Product Reference</em>}</li>
 *   <li>{@link hed.homeelectrical.DeviceType#getChannelCount <em>Channel Count</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getDeviceType()
 * @model
 * @generated
 */
public interface DeviceType extends TechnicalElement {
    /**
     * Returns the value of the '<em><b>Kind</b></em>' attribute.
     * The literals are from the enumeration {@link hed.homeelectrical.DeviceKind}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Kind</em>' attribute.
     * @see hed.homeelectrical.DeviceKind
     * @see #setKind(DeviceKind)
     * @see hed.homeelectrical.HomeElectricalPackage#getDeviceType_Kind()
     * @model
     * @generated
     */
    DeviceKind getKind();

    /**
     * Sets the value of the '{@link hed.homeelectrical.DeviceType#getKind <em>Kind</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Kind</em>' attribute.
     * @see hed.homeelectrical.DeviceKind
     * @see #getKind()
     * @generated
     */
    void setKind(DeviceKind value);

    /**
     * Returns the value of the '<em><b>Manufacturer</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Manufacturer</em>' attribute.
     * @see #setManufacturer(String)
     * @see hed.homeelectrical.HomeElectricalPackage#getDeviceType_Manufacturer()
     * @model
     * @generated
     */
    String getManufacturer();

    /**
     * Sets the value of the '{@link hed.homeelectrical.DeviceType#getManufacturer <em>Manufacturer</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Manufacturer</em>' attribute.
     * @see #getManufacturer()
     * @generated
     */
    void setManufacturer(String value);

    /**
     * Returns the value of the '<em><b>Product Reference</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Product Reference</em>' attribute.
     * @see #setProductReference(String)
     * @see hed.homeelectrical.HomeElectricalPackage#getDeviceType_ProductReference()
     * @model
     * @generated
     */
    String getProductReference();

    /**
     * Sets the value of the '{@link hed.homeelectrical.DeviceType#getProductReference <em>Product Reference</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Product Reference</em>' attribute.
     * @see #getProductReference()
     * @generated
     */
    void setProductReference(String value);

    /**
     * Returns the value of the '<em><b>Channel Count</b></em>' attribute.
     * The default value is <code>"1"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Channel Count</em>' attribute.
     * @see #setChannelCount(int)
     * @see hed.homeelectrical.HomeElectricalPackage#getDeviceType_ChannelCount()
     * @model default="1"
     * @generated
     */
    int getChannelCount();

    /**
     * Sets the value of the '{@link hed.homeelectrical.DeviceType#getChannelCount <em>Channel Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Channel Count</em>' attribute.
     * @see #getChannelCount()
     * @generated
     */
    void setChannelCount(int value);

} // DeviceType
