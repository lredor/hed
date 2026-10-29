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
 * A representation of the model object '<em><b>Modular Device</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.ModularDevice#getLabelText <em>Label Text</em>}</li>
 *   <li>{@link hed.homeelectrical.ModularDevice#getRail <em>Rail</em>}</li>
 *   <li>{@link hed.homeelectrical.ModularDevice#getUpstreamRccb <em>Upstream Rccb</em>}</li>
 *   <li>{@link hed.homeelectrical.ModularDevice#getUpstreamServiceBreaker <em>Upstream Service Breaker</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getModularDevice()
 * @model
 * @generated
 */
public interface ModularDevice extends Equipment {
    /**
     * Returns the value of the '<em><b>Label Text</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Label Text</em>' attribute.
     * @see #setLabelText(String)
     * @see hed.homeelectrical.HomeElectricalPackage#getModularDevice_LabelText()
     * @model
     * @generated
     */
    String getLabelText();

    /**
     * Sets the value of the '{@link hed.homeelectrical.ModularDevice#getLabelText <em>Label Text</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Label Text</em>' attribute.
     * @see #getLabelText()
     * @generated
     */
    void setLabelText(String value);

    /**
     * Returns the value of the '<em><b>Rail</b></em>' reference.
     * It is bidirectional and its opposite is '{@link hed.homeelectrical.DINRail#getDevices <em>Devices</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Rail</em>' reference.
     * @see #setRail(DINRail)
     * @see hed.homeelectrical.HomeElectricalPackage#getModularDevice_Rail()
     * @see hed.homeelectrical.DINRail#getDevices
     * @model opposite="devices" required="true"
     * @generated
     */
    DINRail getRail();

    /**
     * Sets the value of the '{@link hed.homeelectrical.ModularDevice#getRail <em>Rail</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Rail</em>' reference.
     * @see #getRail()
     * @generated
     */
    void setRail(DINRail value);

    /**
     * Returns the value of the '<em><b>Upstream Rccb</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Upstream Rccb</em>' reference.
     * @see #setUpstreamRccb(ModularDevice)
     * @see hed.homeelectrical.HomeElectricalPackage#getModularDevice_UpstreamRccb()
     * @model
     * @generated
     */
    ModularDevice getUpstreamRccb();

    /**
     * Sets the value of the '{@link hed.homeelectrical.ModularDevice#getUpstreamRccb <em>Upstream Rccb</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Upstream Rccb</em>' reference.
     * @see #getUpstreamRccb()
     * @generated
     */
    void setUpstreamRccb(ModularDevice value);

    /**
     * Returns the value of the '<em><b>Upstream Service Breaker</b></em>' reference.
     * It is bidirectional and its opposite is '{@link hed.homeelectrical.ServiceCircuitBreaker#getDownstreamDevices <em>Downstream Devices</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Upstream Service Breaker</em>' reference.
     * @see #setUpstreamServiceBreaker(ServiceCircuitBreaker)
     * @see hed.homeelectrical.HomeElectricalPackage#getModularDevice_UpstreamServiceBreaker()
     * @see hed.homeelectrical.ServiceCircuitBreaker#getDownstreamDevices
     * @model opposite="downstreamDevices"
     * @generated
     */
    ServiceCircuitBreaker getUpstreamServiceBreaker();

    /**
     * Sets the value of the '{@link hed.homeelectrical.ModularDevice#getUpstreamServiceBreaker <em>Upstream Service Breaker</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Upstream Service Breaker</em>' reference.
     * @see #getUpstreamServiceBreaker()
     * @generated
     */
    void setUpstreamServiceBreaker(ServiceCircuitBreaker value);

} // ModularDevice
