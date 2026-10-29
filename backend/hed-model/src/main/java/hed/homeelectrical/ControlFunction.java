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
 * A representation of the model object '<em><b>Control Function</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.ControlFunction#getMode <em>Mode</em>}</li>
 *   <li>{@link hed.homeelectrical.ControlFunction#getChannels <em>Channels</em>}</li>
 *   <li>{@link hed.homeelectrical.ControlFunction#getTargets <em>Targets</em>}</li>
 *   <li>{@link hed.homeelectrical.ControlFunction#getActuator <em>Actuator</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getControlFunction()
 * @model
 * @generated
 */
public interface ControlFunction extends NamedElement {
    /**
     * Returns the value of the '<em><b>Mode</b></em>' attribute.
     * The literals are from the enumeration {@link hed.homeelectrical.ControlMode}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Mode</em>' attribute.
     * @see hed.homeelectrical.ControlMode
     * @see #setMode(ControlMode)
     * @see hed.homeelectrical.HomeElectricalPackage#getControlFunction_Mode()
     * @model
     * @generated
     */
    ControlMode getMode();

    /**
     * Sets the value of the '{@link hed.homeelectrical.ControlFunction#getMode <em>Mode</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Mode</em>' attribute.
     * @see hed.homeelectrical.ControlMode
     * @see #getMode()
     * @generated
     */
    void setMode(ControlMode value);

    /**
     * Returns the value of the '<em><b>Channels</b></em>' reference list.
     * The list contents are of type {@link hed.homeelectrical.ControlChannel}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Channels</em>' reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getControlFunction_Channels()
     * @model
     * @generated
     */
    EList<ControlChannel> getChannels();

    /**
     * Returns the value of the '<em><b>Targets</b></em>' reference list.
     * The list contents are of type {@link hed.homeelectrical.FieldEquipment}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Targets</em>' reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getControlFunction_Targets()
     * @model
     * @generated
     */
    EList<FieldEquipment> getTargets();

    /**
     * Returns the value of the '<em><b>Actuator</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Actuator</em>' reference.
     * @see #setActuator(ModularDevice)
     * @see hed.homeelectrical.HomeElectricalPackage#getControlFunction_Actuator()
     * @model
     * @generated
     */
    ModularDevice getActuator();

    /**
     * Sets the value of the '{@link hed.homeelectrical.ControlFunction#getActuator <em>Actuator</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Actuator</em>' reference.
     * @see #getActuator()
     * @generated
     */
    void setActuator(ModularDevice value);

} // ControlFunction
