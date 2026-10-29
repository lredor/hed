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
 * A representation of the model object '<em><b>Catalog</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.Catalog#getDeviceTypes <em>Device Types</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getCatalog()
 * @model
 * @generated
 */
public interface Catalog extends NamedElement {
    /**
     * Returns the value of the '<em><b>Device Types</b></em>' containment reference list.
     * The list contents are of type {@link hed.homeelectrical.DeviceType}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Device Types</em>' containment reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getCatalog_DeviceTypes()
     * @model containment="true"
     * @generated
     */
    EList<DeviceType> getDeviceTypes();

} // Catalog
