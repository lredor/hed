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
 * A representation of the literals of the enumeration '<em><b>Circuit Phase</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see hed.homeelectrical.HomeElectricalPackage#getCircuitPhase()
 * @model
 * @generated
 */
public enum CircuitPhase implements Enumerator {
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
     * The '<em><b>L1</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #L1_VALUE
     * @generated
     * @ordered
     */
    L1(1, "L1", "L1"),

    /**
     * The '<em><b>L2</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #L2_VALUE
     * @generated
     * @ordered
     */
    L2(2, "L2", "L2"),

    /**
     * The '<em><b>L3</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #L3_VALUE
     * @generated
     * @ordered
     */
    L3(3, "L3", "L3"),

    /**
     * The '<em><b>THREE PHASE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #THREE_PHASE_VALUE
     * @generated
     * @ordered
     */
    THREE_PHASE(4, "THREE_PHASE", "THREE_PHASE");

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
     * The '<em><b>L1</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #L1
     * @model
     * @generated
     * @ordered
     */
    public static final int L1_VALUE = 1;

    /**
     * The '<em><b>L2</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #L2
     * @model
     * @generated
     * @ordered
     */
    public static final int L2_VALUE = 2;

    /**
     * The '<em><b>L3</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #L3
     * @model
     * @generated
     * @ordered
     */
    public static final int L3_VALUE = 3;

    /**
     * The '<em><b>THREE PHASE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #THREE_PHASE
     * @model
     * @generated
     * @ordered
     */
    public static final int THREE_PHASE_VALUE = 4;

    /**
     * An array of all the '<em><b>Circuit Phase</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final CircuitPhase[] VALUES_ARRAY = new CircuitPhase[] { UNKNOWN, L1, L2, L3, THREE_PHASE, };

    /**
     * A public read-only list of all the '<em><b>Circuit Phase</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List<CircuitPhase> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Circuit Phase</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param literal the literal.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static CircuitPhase get(String literal) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            CircuitPhase result = VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Circuit Phase</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param name the name.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static CircuitPhase getByName(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            CircuitPhase result = VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Circuit Phase</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the integer value.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static CircuitPhase get(int value) {
        switch (value) {
            case UNKNOWN_VALUE:
                return UNKNOWN;
            case L1_VALUE:
                return L1;
            case L2_VALUE:
                return L2;
            case L3_VALUE:
                return L3;
            case THREE_PHASE_VALUE:
                return THREE_PHASE;
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
    private CircuitPhase(int value, String name, String literal) {
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

} //CircuitPhase
