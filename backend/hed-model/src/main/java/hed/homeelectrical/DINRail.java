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
 * A representation of the model object '<em><b>DIN Rail</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.DINRail#getCapacityModules <em>Capacity Modules</em>}</li>
 *   <li>{@link hed.homeelectrical.DINRail#getDevices <em>Devices</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getDINRail()
 * @model
 * @generated
 */
public interface DINRail extends NamedElement {
    /**
     * Returns the value of the '<em><b>Capacity Modules</b></em>' attribute.
     * The default value is <code>"13"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Capacity Modules</em>' attribute.
     * @see #setCapacityModules(int)
     * @see hed.homeelectrical.HomeElectricalPackage#getDINRail_CapacityModules()
     * @model default="13"
     * @generated
     */
    int getCapacityModules();

    /**
     * Sets the value of the '{@link hed.homeelectrical.DINRail#getCapacityModules <em>Capacity Modules</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Capacity Modules</em>' attribute.
     * @see #getCapacityModules()
     * @generated
     */
    void setCapacityModules(int value);

    /**
     * Returns the value of the '<em><b>Devices</b></em>' reference list.
     * The list contents are of type {@link hed.homeelectrical.ModularDevice}.
     * It is bidirectional and its opposite is '{@link hed.homeelectrical.ModularDevice#getRail <em>Rail</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Devices</em>' reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getDINRail_Devices()
     * @see hed.homeelectrical.ModularDevice#getRail
     * @model opposite="rail"
     * @generated
     */
    EList<ModularDevice> getDevices();

} // DINRail
