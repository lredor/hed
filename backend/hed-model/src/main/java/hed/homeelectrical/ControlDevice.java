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
 * A representation of the model object '<em><b>Control Device</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.ControlDevice#getChannels <em>Channels</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getControlDevice()
 * @model
 * @generated
 */
public interface ControlDevice extends FieldEquipment {
    /**
     * Returns the value of the '<em><b>Channels</b></em>' containment reference list.
     * The list contents are of type {@link hed.homeelectrical.ControlChannel}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Channels</em>' containment reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getControlDevice_Channels()
     * @model containment="true" required="true"
     * @generated
     */
    EList<ControlChannel> getChannels();

} // ControlDevice
