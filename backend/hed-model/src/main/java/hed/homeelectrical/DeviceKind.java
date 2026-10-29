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
 * A representation of the literals of the enumeration '<em><b>Device Kind</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see hed.homeelectrical.HomeElectricalPackage#getDeviceKind()
 * @model
 * @generated
 */
public enum DeviceKind implements Enumerator {
    /**
     * The '<em><b>OUTLET</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #OUTLET_VALUE
     * @generated
     * @ordered
     */
    OUTLET(0, "OUTLET", "OUTLET"),

    /**
     * The '<em><b>SWITCH</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SWITCH_VALUE
     * @generated
     * @ordered
     */
    SWITCH(1, "SWITCH", "SWITCH"),

    /**
     * The '<em><b>PUSH BUTTON</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #PUSH_BUTTON_VALUE
     * @generated
     * @ordered
     */
    PUSH_BUTTON(2, "PUSH_BUTTON", "PUSH_BUTTON"),

    /**
     * The '<em><b>DIMMER</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DIMMER_VALUE
     * @generated
     * @ordered
     */
    DIMMER(3, "DIMMER", "DIMMER"),

    /**
     * The '<em><b>SHUTTER CONTROL</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SHUTTER_CONTROL_VALUE
     * @generated
     * @ordered
     */
    SHUTTER_CONTROL(4, "SHUTTER_CONTROL", "SHUTTER_CONTROL"),

    /**
     * The '<em><b>LUMINAIRE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #LUMINAIRE_VALUE
     * @generated
     * @ordered
     */
    LUMINAIRE(5, "LUMINAIRE", "LUMINAIRE"),

    /**
     * The '<em><b>SHUTTER MOTOR</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SHUTTER_MOTOR_VALUE
     * @generated
     * @ordered
     */
    SHUTTER_MOTOR(6, "SHUTTER_MOTOR", "SHUTTER_MOTOR"),

    /**
     * The '<em><b>HEATER</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #HEATER_VALUE
     * @generated
     * @ordered
     */
    HEATER(7, "HEATER", "HEATER"),

    /**
     * The '<em><b>WATER HEATER</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #WATER_HEATER_VALUE
     * @generated
     * @ordered
     */
    WATER_HEATER(8, "WATER_HEATER", "WATER_HEATER"),

    /**
     * The '<em><b>VENTILATION</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #VENTILATION_VALUE
     * @generated
     * @ordered
     */
    VENTILATION(9, "VENTILATION", "VENTILATION"),

    /**
     * The '<em><b>COOKER</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #COOKER_VALUE
     * @generated
     * @ordered
     */
    COOKER(10, "COOKER", "COOKER"),

    /**
     * The '<em><b>FIXED APPLIANCE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #FIXED_APPLIANCE_VALUE
     * @generated
     * @ordered
     */
    FIXED_APPLIANCE(11, "FIXED_APPLIANCE", "FIXED_APPLIANCE"),

    /**
     * The '<em><b>MCB</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #MCB_VALUE
     * @generated
     * @ordered
     */
    MCB(12, "MCB", "MCB"),

    /**
     * The '<em><b>RCCB</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #RCCB_VALUE
     * @generated
     * @ordered
     */
    RCCB(13, "RCCB", "RCCB"),

    /**
     * The '<em><b>RCBO</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #RCBO_VALUE
     * @generated
     * @ordered
     */
    RCBO(14, "RCBO", "RCBO"),

    /**
     * The '<em><b>FUSE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #FUSE_VALUE
     * @generated
     * @ordered
     */
    FUSE(15, "FUSE", "FUSE"),

    /**
     * The '<em><b>ISOLATOR</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #ISOLATOR_VALUE
     * @generated
     * @ordered
     */
    ISOLATOR(16, "ISOLATOR", "ISOLATOR"),

    /**
     * The '<em><b>SPD</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SPD_VALUE
     * @generated
     * @ordered
     */
    SPD(17, "SPD", "SPD"),

    /**
     * The '<em><b>IMPULSE RELAY</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #IMPULSE_RELAY_VALUE
     * @generated
     * @ordered
     */
    IMPULSE_RELAY(18, "IMPULSE_RELAY", "IMPULSE_RELAY"),

    /**
     * The '<em><b>CONTACTOR</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #CONTACTOR_VALUE
     * @generated
     * @ordered
     */
    CONTACTOR(19, "CONTACTOR", "CONTACTOR"),

    /**
     * The '<em><b>ENERGY METER</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #ENERGY_METER_VALUE
     * @generated
     * @ordered
     */
    ENERGY_METER(20, "ENERGY_METER", "ENERGY_METER"),

    /**
     * The '<em><b>SERVICE BREAKER</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SERVICE_BREAKER_VALUE
     * @generated
     * @ordered
     */
    SERVICE_BREAKER(21, "SERVICE_BREAKER", "SERVICE_BREAKER"),

    /**
     * The '<em><b>OTHER FIELD</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #OTHER_FIELD_VALUE
     * @generated
     * @ordered
     */
    OTHER_FIELD(22, "OTHER_FIELD", "OTHER_FIELD"),

    /**
     * The '<em><b>OTHER MODULAR</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #OTHER_MODULAR_VALUE
     * @generated
     * @ordered
     */
    OTHER_MODULAR(23, "OTHER_MODULAR", "OTHER_MODULAR");

    /**
     * The '<em><b>OUTLET</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #OUTLET
     * @model
     * @generated
     * @ordered
     */
    public static final int OUTLET_VALUE = 0;

    /**
     * The '<em><b>SWITCH</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SWITCH
     * @model
     * @generated
     * @ordered
     */
    public static final int SWITCH_VALUE = 1;

    /**
     * The '<em><b>PUSH BUTTON</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #PUSH_BUTTON
     * @model
     * @generated
     * @ordered
     */
    public static final int PUSH_BUTTON_VALUE = 2;

    /**
     * The '<em><b>DIMMER</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DIMMER
     * @model
     * @generated
     * @ordered
     */
    public static final int DIMMER_VALUE = 3;

    /**
     * The '<em><b>SHUTTER CONTROL</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SHUTTER_CONTROL
     * @model
     * @generated
     * @ordered
     */
    public static final int SHUTTER_CONTROL_VALUE = 4;

    /**
     * The '<em><b>LUMINAIRE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #LUMINAIRE
     * @model
     * @generated
     * @ordered
     */
    public static final int LUMINAIRE_VALUE = 5;

    /**
     * The '<em><b>SHUTTER MOTOR</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SHUTTER_MOTOR
     * @model
     * @generated
     * @ordered
     */
    public static final int SHUTTER_MOTOR_VALUE = 6;

    /**
     * The '<em><b>HEATER</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #HEATER
     * @model
     * @generated
     * @ordered
     */
    public static final int HEATER_VALUE = 7;

    /**
     * The '<em><b>WATER HEATER</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #WATER_HEATER
     * @model
     * @generated
     * @ordered
     */
    public static final int WATER_HEATER_VALUE = 8;

    /**
     * The '<em><b>VENTILATION</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #VENTILATION
     * @model
     * @generated
     * @ordered
     */
    public static final int VENTILATION_VALUE = 9;

    /**
     * The '<em><b>COOKER</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #COOKER
     * @model
     * @generated
     * @ordered
     */
    public static final int COOKER_VALUE = 10;

    /**
     * The '<em><b>FIXED APPLIANCE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #FIXED_APPLIANCE
     * @model
     * @generated
     * @ordered
     */
    public static final int FIXED_APPLIANCE_VALUE = 11;

    /**
     * The '<em><b>MCB</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #MCB
     * @model
     * @generated
     * @ordered
     */
    public static final int MCB_VALUE = 12;

    /**
     * The '<em><b>RCCB</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #RCCB
     * @model
     * @generated
     * @ordered
     */
    public static final int RCCB_VALUE = 13;

    /**
     * The '<em><b>RCBO</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #RCBO
     * @model
     * @generated
     * @ordered
     */
    public static final int RCBO_VALUE = 14;

    /**
     * The '<em><b>FUSE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #FUSE
     * @model
     * @generated
     * @ordered
     */
    public static final int FUSE_VALUE = 15;

    /**
     * The '<em><b>ISOLATOR</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #ISOLATOR
     * @model
     * @generated
     * @ordered
     */
    public static final int ISOLATOR_VALUE = 16;

    /**
     * The '<em><b>SPD</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SPD
     * @model
     * @generated
     * @ordered
     */
    public static final int SPD_VALUE = 17;

    /**
     * The '<em><b>IMPULSE RELAY</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #IMPULSE_RELAY
     * @model
     * @generated
     * @ordered
     */
    public static final int IMPULSE_RELAY_VALUE = 18;

    /**
     * The '<em><b>CONTACTOR</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #CONTACTOR
     * @model
     * @generated
     * @ordered
     */
    public static final int CONTACTOR_VALUE = 19;

    /**
     * The '<em><b>ENERGY METER</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #ENERGY_METER
     * @model
     * @generated
     * @ordered
     */
    public static final int ENERGY_METER_VALUE = 20;

    /**
     * The '<em><b>SERVICE BREAKER</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SERVICE_BREAKER
     * @model
     * @generated
     * @ordered
     */
    public static final int SERVICE_BREAKER_VALUE = 21;

    /**
     * The '<em><b>OTHER FIELD</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #OTHER_FIELD
     * @model
     * @generated
     * @ordered
     */
    public static final int OTHER_FIELD_VALUE = 22;

    /**
     * The '<em><b>OTHER MODULAR</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #OTHER_MODULAR
     * @model
     * @generated
     * @ordered
     */
    public static final int OTHER_MODULAR_VALUE = 23;

    /**
     * An array of all the '<em><b>Device Kind</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final DeviceKind[] VALUES_ARRAY = new DeviceKind[] { OUTLET, SWITCH, PUSH_BUTTON, DIMMER, SHUTTER_CONTROL, LUMINAIRE, SHUTTER_MOTOR, HEATER, WATER_HEATER, VENTILATION, COOKER,
            FIXED_APPLIANCE, MCB, RCCB, RCBO, FUSE, ISOLATOR, SPD, IMPULSE_RELAY, CONTACTOR, ENERGY_METER, SERVICE_BREAKER, OTHER_FIELD, OTHER_MODULAR, };

    /**
     * A public read-only list of all the '<em><b>Device Kind</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List<DeviceKind> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Device Kind</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param literal the literal.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static DeviceKind get(String literal) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            DeviceKind result = VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Device Kind</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param name the name.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static DeviceKind getByName(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            DeviceKind result = VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Device Kind</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the integer value.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static DeviceKind get(int value) {
        switch (value) {
            case OUTLET_VALUE:
                return OUTLET;
            case SWITCH_VALUE:
                return SWITCH;
            case PUSH_BUTTON_VALUE:
                return PUSH_BUTTON;
            case DIMMER_VALUE:
                return DIMMER;
            case SHUTTER_CONTROL_VALUE:
                return SHUTTER_CONTROL;
            case LUMINAIRE_VALUE:
                return LUMINAIRE;
            case SHUTTER_MOTOR_VALUE:
                return SHUTTER_MOTOR;
            case HEATER_VALUE:
                return HEATER;
            case WATER_HEATER_VALUE:
                return WATER_HEATER;
            case VENTILATION_VALUE:
                return VENTILATION;
            case COOKER_VALUE:
                return COOKER;
            case FIXED_APPLIANCE_VALUE:
                return FIXED_APPLIANCE;
            case MCB_VALUE:
                return MCB;
            case RCCB_VALUE:
                return RCCB;
            case RCBO_VALUE:
                return RCBO;
            case FUSE_VALUE:
                return FUSE;
            case ISOLATOR_VALUE:
                return ISOLATOR;
            case SPD_VALUE:
                return SPD;
            case IMPULSE_RELAY_VALUE:
                return IMPULSE_RELAY;
            case CONTACTOR_VALUE:
                return CONTACTOR;
            case ENERGY_METER_VALUE:
                return ENERGY_METER;
            case SERVICE_BREAKER_VALUE:
                return SERVICE_BREAKER;
            case OTHER_FIELD_VALUE:
                return OTHER_FIELD;
            case OTHER_MODULAR_VALUE:
                return OTHER_MODULAR;
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
    private DeviceKind(int value, String name, String literal) {
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

} //DeviceKind
