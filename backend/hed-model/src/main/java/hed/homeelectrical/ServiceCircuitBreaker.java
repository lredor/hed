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
 * A representation of the model object '<em><b>Service Circuit Breaker</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.ServiceCircuitBreaker#getDownstreamDevices <em>Downstream Devices</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getServiceCircuitBreaker()
 * @model
 * @generated
 */
public interface ServiceCircuitBreaker extends Equipment, TechnicalElement {
    /**
     * Returns the value of the '<em><b>Downstream Devices</b></em>' reference list.
     * The list contents are of type {@link hed.homeelectrical.ModularDevice}.
     * It is bidirectional and its opposite is '{@link hed.homeelectrical.ModularDevice#getUpstreamServiceBreaker <em>Upstream Service Breaker</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Downstream Devices</em>' reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getServiceCircuitBreaker_DownstreamDevices()
     * @see hed.homeelectrical.ModularDevice#getUpstreamServiceBreaker
     * @model opposite="upstreamServiceBreaker"
     * @generated
     */
    EList<ModularDevice> getDownstreamDevices();

} // ServiceCircuitBreaker
