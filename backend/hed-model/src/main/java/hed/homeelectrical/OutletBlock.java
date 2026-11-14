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
 * A representation of the model object '<em><b>Outlet Block</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * A physically placed assembly of outlet rows. Individual outlets retain their electrical assignments; the block is moved as one graphical unit.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.OutletBlock#getRows <em>Rows</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getOutletBlock()
 * @model
 * @generated
 */
public interface OutletBlock extends FieldEquipment {
    /**
     * Returns the value of the '<em><b>Rows</b></em>' containment reference list.
     * The list contents are of type {@link hed.homeelectrical.OutletRow}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Rows</em>' containment reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getOutletBlock_Rows()
     * @model containment="true" required="true"
     * @generated
     */
    EList<OutletRow> getRows();

} // OutletBlock
