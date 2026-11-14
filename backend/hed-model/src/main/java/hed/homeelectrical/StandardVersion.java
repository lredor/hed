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
 * A representation of the literals of the enumeration '<em><b>Standard Version</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * Supported explicit profiles for version-aware installation checks; each profile implements a documented subset rather than a complete conformity assessment.
 * <!-- end-model-doc -->
 * @see hed.homeelectrical.HomeElectricalPackage#getStandardVersion()
 * @model
 * @generated
 */
public enum StandardVersion implements Enumerator {
    /**
     * The '<em><b>UNSPECIFIED</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #UNSPECIFIED_VALUE
     * @generated
     * @ordered
     */
    UNSPECIFIED(0, "UNSPECIFIED", "UNSPECIFIED"),

    /**
     * The '<em><b>NF C15 100 BEFORE A5</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #NF_C15_100_BEFORE_A5_VALUE
     * @generated
     * @ordered
     */
    NF_C15_100_BEFORE_A5(1, "NF_C_15_100_BEFORE_A5", "NF_C_15_100_BEFORE_A5"),

    /**
     * The '<em><b>NF C15 100 A5 2015</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #NF_C15_100_A5_2015_VALUE
     * @generated
     * @ordered
     */
    NF_C15_100_A5_2015(2, "NF_C_15_100_A5_2015", "NF_C_15_100_A5_2015");

    /**
     * The '<em><b>UNSPECIFIED</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #UNSPECIFIED
     * @model
     * @generated
     * @ordered
     */
    public static final int UNSPECIFIED_VALUE = 0;

    /**
     * The '<em><b>NF C15 100 BEFORE A5</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #NF_C15_100_BEFORE_A5
     * @model name="NF_C_15_100_BEFORE_A5"
     * @generated
     * @ordered
     */
    public static final int NF_C15_100_BEFORE_A5_VALUE = 1;

    /**
     * The '<em><b>NF C15 100 A5 2015</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #NF_C15_100_A5_2015
     * @model name="NF_C_15_100_A5_2015"
     * @generated
     * @ordered
     */
    public static final int NF_C15_100_A5_2015_VALUE = 2;

    /**
     * An array of all the '<em><b>Standard Version</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final StandardVersion[] VALUES_ARRAY = new StandardVersion[] { UNSPECIFIED, NF_C15_100_BEFORE_A5, NF_C15_100_A5_2015, };

    /**
     * A public read-only list of all the '<em><b>Standard Version</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List<StandardVersion> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Standard Version</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param literal the literal.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static StandardVersion get(String literal) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            StandardVersion result = VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Standard Version</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param name the name.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static StandardVersion getByName(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            StandardVersion result = VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Standard Version</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the integer value.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static StandardVersion get(int value) {
        switch (value) {
            case UNSPECIFIED_VALUE:
                return UNSPECIFIED;
            case NF_C15_100_BEFORE_A5_VALUE:
                return NF_C15_100_BEFORE_A5;
            case NF_C15_100_A5_2015_VALUE:
                return NF_C15_100_A5_2015;
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
    private StandardVersion(int value, String name, String literal) {
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

} //StandardVersion
