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
 * A representation of the literals of the enumeration '<em><b>Circuit Use</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see hed.homeelectrical.HomeElectricalPackage#getCircuitUse()
 * @model
 * @generated
 */
public enum CircuitUse implements Enumerator {
    /**
     * The '<em><b>UNKNOWN</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #UNKNOWN_VALUE
     * @generated
     * @ordered
     */
    UNKNOWN(0, "UNKNOWN", "UNKNOWN"),

    /**
     * The '<em><b>LIGHTING</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #LIGHTING_VALUE
     * @generated
     * @ordered
     */
    LIGHTING(1, "LIGHTING", "LIGHTING"),

    /**
     * The '<em><b>SOCKET OUTLETS</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SOCKET_OUTLETS_VALUE
     * @generated
     * @ordered
     */
    SOCKET_OUTLETS(2, "SOCKET_OUTLETS", "SOCKET_OUTLETS"),

    /**
     * The '<em><b>SHUTTERS</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SHUTTERS_VALUE
     * @generated
     * @ordered
     */
    SHUTTERS(3, "SHUTTERS", "SHUTTERS"),

    /**
     * The '<em><b>HEATING</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #HEATING_VALUE
     * @generated
     * @ordered
     */
    HEATING(4, "HEATING", "HEATING"),

    /**
     * The '<em><b>WATER HEATING</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #WATER_HEATING_VALUE
     * @generated
     * @ordered
     */
    WATER_HEATING(5, "WATER_HEATING", "WATER_HEATING"),

    /**
     * The '<em><b>VENTILATION</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #VENTILATION_VALUE
     * @generated
     * @ordered
     */
    VENTILATION(6, "VENTILATION", "VENTILATION"),

    /**
     * The '<em><b>COOKING</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #COOKING_VALUE
     * @generated
     * @ordered
     */
    COOKING(7, "COOKING", "COOKING"),

    /**
     * The '<em><b>DEDICATED APPLIANCE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DEDICATED_APPLIANCE_VALUE
     * @generated
     * @ordered
     */
    DEDICATED_APPLIANCE(8, "DEDICATED_APPLIANCE", "DEDICATED_APPLIANCE"),

    /**
     * The '<em><b>OTHER</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #OTHER_VALUE
     * @generated
     * @ordered
     */
    OTHER(9, "OTHER", "OTHER");

    /**
     * The '<em><b>UNKNOWN</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #UNKNOWN
     * @model
     * @generated
     * @ordered
     */
    public static final int UNKNOWN_VALUE = 0;

    /**
     * The '<em><b>LIGHTING</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #LIGHTING
     * @model
     * @generated
     * @ordered
     */
    public static final int LIGHTING_VALUE = 1;

    /**
     * The '<em><b>SOCKET OUTLETS</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SOCKET_OUTLETS
     * @model
     * @generated
     * @ordered
     */
    public static final int SOCKET_OUTLETS_VALUE = 2;

    /**
     * The '<em><b>SHUTTERS</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SHUTTERS
     * @model
     * @generated
     * @ordered
     */
    public static final int SHUTTERS_VALUE = 3;

    /**
     * The '<em><b>HEATING</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #HEATING
     * @model
     * @generated
     * @ordered
     */
    public static final int HEATING_VALUE = 4;

    /**
     * The '<em><b>WATER HEATING</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #WATER_HEATING
     * @model
     * @generated
     * @ordered
     */
    public static final int WATER_HEATING_VALUE = 5;

    /**
     * The '<em><b>VENTILATION</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #VENTILATION
     * @model
     * @generated
     * @ordered
     */
    public static final int VENTILATION_VALUE = 6;

    /**
     * The '<em><b>COOKING</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #COOKING
     * @model
     * @generated
     * @ordered
     */
    public static final int COOKING_VALUE = 7;

    /**
     * The '<em><b>DEDICATED APPLIANCE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DEDICATED_APPLIANCE
     * @model
     * @generated
     * @ordered
     */
    public static final int DEDICATED_APPLIANCE_VALUE = 8;

    /**
     * The '<em><b>OTHER</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #OTHER
     * @model
     * @generated
     * @ordered
     */
    public static final int OTHER_VALUE = 9;

    /**
     * An array of all the '<em><b>Circuit Use</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final CircuitUse[] VALUES_ARRAY = new CircuitUse[] { UNKNOWN, LIGHTING, SOCKET_OUTLETS, SHUTTERS, HEATING, WATER_HEATING, VENTILATION, COOKING, DEDICATED_APPLIANCE, OTHER, };

    /**
     * A public read-only list of all the '<em><b>Circuit Use</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List<CircuitUse> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Circuit Use</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param literal the literal.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static CircuitUse get(String literal) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            CircuitUse result = VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Circuit Use</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param name the name.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static CircuitUse getByName(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            CircuitUse result = VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Circuit Use</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the integer value.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static CircuitUse get(int value) {
        switch (value) {
            case UNKNOWN_VALUE:
                return UNKNOWN;
            case LIGHTING_VALUE:
                return LIGHTING;
            case SOCKET_OUTLETS_VALUE:
                return SOCKET_OUTLETS;
            case SHUTTERS_VALUE:
                return SHUTTERS;
            case HEATING_VALUE:
                return HEATING;
            case WATER_HEATING_VALUE:
                return WATER_HEATING;
            case VENTILATION_VALUE:
                return VENTILATION;
            case COOKING_VALUE:
                return COOKING;
            case DEDICATED_APPLIANCE_VALUE:
                return DEDICATED_APPLIANCE;
            case OTHER_VALUE:
                return OTHER;
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
    private CircuitUse(int value, String name, String literal) {
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

} //CircuitUse
