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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Control Mode</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see hed.homeelectrical.HomeElectricalPackage#getControlMode()
 * @model
 * @generated
 */
public enum ControlMode implements Enumerator {
    /**
     * The '<em><b>SWITCHING</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SWITCHING_VALUE
     * @generated
     * @ordered
     */
    SWITCHING(0, "SWITCHING", "SWITCHING"),

    /**
     * The '<em><b>DIMMING</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DIMMING_VALUE
     * @generated
     * @ordered
     */
    DIMMING(1, "DIMMING", "DIMMING"),

    /**
     * The '<em><b>SHUTTER</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SHUTTER_VALUE
     * @generated
     * @ordered
     */
    SHUTTER(2, "SHUTTER", "SHUTTER");

    /**
     * The '<em><b>SWITCHING</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SWITCHING
     * @model
     * @generated
     * @ordered
     */
    public static final int SWITCHING_VALUE = 0;

    /**
     * The '<em><b>DIMMING</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DIMMING
     * @model
     * @generated
     * @ordered
     */
    public static final int DIMMING_VALUE = 1;

    /**
     * The '<em><b>SHUTTER</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SHUTTER
     * @model
     * @generated
     * @ordered
     */
    public static final int SHUTTER_VALUE = 2;

    /**
     * An array of all the '<em><b>Control Mode</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final ControlMode[] VALUES_ARRAY = new ControlMode[] { SWITCHING, DIMMING, SHUTTER, };

    /**
     * A public read-only list of all the '<em><b>Control Mode</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List<ControlMode> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Control Mode</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param literal the literal.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static ControlMode get(String literal) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            ControlMode result = VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Control Mode</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param name the name.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static ControlMode getByName(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            ControlMode result = VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Control Mode</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the integer value.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static ControlMode get(int value) {
        switch (value) {
            case SWITCHING_VALUE:
                return SWITCHING;
            case DIMMING_VALUE:
                return DIMMING;
            case SHUTTER_VALUE:
                return SHUTTER;
        }
        return null;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final int value;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String name;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String literal;

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private ControlMode(int value, String name, String literal) {
        this.value = value;
        this.name = name;
        this.literal = literal;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int getValue() {
        return value;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getLiteral() {
        return literal;
    }

    /**
     * Returns the literal value of the enumerator, which is its string representation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        return literal;
    }

} //ControlMode
