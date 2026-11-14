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
 * A representation of the model object '<em><b>Outlet Row</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * An ordered row of individual outlets contained in an outlet block.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.OutletRow#getOutlets <em>Outlets</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getOutletRow()
 * @model
 * @generated
 */
public interface OutletRow extends NamedElement {
    /**
     * Returns the value of the '<em><b>Outlets</b></em>' containment reference list.
     * The list contents are of type {@link hed.homeelectrical.Outlet}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Outlets</em>' containment reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getOutletRow_Outlets()
     * @model containment="true" required="true"
     * @generated
     */
    EList<Outlet> getOutlets();

} // OutletRow
