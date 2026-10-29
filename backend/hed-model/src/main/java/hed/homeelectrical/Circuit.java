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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Circuit</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.Circuit#getPhase <em>Phase</em>}</li>
 *   <li>{@link hed.homeelectrical.Circuit#getConductorSectionMm2 <em>Conductor Section Mm2</em>}</li>
 *   <li>{@link hed.homeelectrical.Circuit#getSourceBoard <em>Source Board</em>}</li>
 *   <li>{@link hed.homeelectrical.Circuit#getProtectiveDevice <em>Protective Device</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getCircuit()
 * @model abstract="true"
 * @generated
 */
public interface Circuit extends NamedElement {
    /**
     * Returns the value of the '<em><b>Phase</b></em>' attribute.
     * The literals are from the enumeration {@link hed.homeelectrical.CircuitPhase}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Phase</em>' attribute.
     * @see hed.homeelectrical.CircuitPhase
     * @see #setPhase(CircuitPhase)
     * @see hed.homeelectrical.HomeElectricalPackage#getCircuit_Phase()
     * @model
     * @generated
     */
    CircuitPhase getPhase();

    /**
     * Sets the value of the '{@link hed.homeelectrical.Circuit#getPhase <em>Phase</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Phase</em>' attribute.
     * @see hed.homeelectrical.CircuitPhase
     * @see #getPhase()
     * @generated
     */
    void setPhase(CircuitPhase value);

    /**
     * Returns the value of the '<em><b>Conductor Section Mm2</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Conductor Section Mm2</em>' attribute.
     * @see #setConductorSectionMm2(Double)
     * @see hed.homeelectrical.HomeElectricalPackage#getCircuit_ConductorSectionMm2()
     * @model
     * @generated
     */
    Double getConductorSectionMm2();

    /**
     * Sets the value of the '{@link hed.homeelectrical.Circuit#getConductorSectionMm2 <em>Conductor Section Mm2</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Conductor Section Mm2</em>' attribute.
     * @see #getConductorSectionMm2()
     * @generated
     */
    void setConductorSectionMm2(Double value);

    /**
     * Returns the value of the '<em><b>Source Board</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Source Board</em>' reference.
     * @see #setSourceBoard(DistributionBoard)
     * @see hed.homeelectrical.HomeElectricalPackage#getCircuit_SourceBoard()
     * @model
     * @generated
     */
    DistributionBoard getSourceBoard();

    /**
     * Sets the value of the '{@link hed.homeelectrical.Circuit#getSourceBoard <em>Source Board</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Source Board</em>' reference.
     * @see #getSourceBoard()
     * @generated
     */
    void setSourceBoard(DistributionBoard value);

    /**
     * Returns the value of the '<em><b>Protective Device</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Protective Device</em>' reference.
     * @see #setProtectiveDevice(ModularDevice)
     * @see hed.homeelectrical.HomeElectricalPackage#getCircuit_ProtectiveDevice()
     * @model
     * @generated
     */
    ModularDevice getProtectiveDevice();

    /**
     * Sets the value of the '{@link hed.homeelectrical.Circuit#getProtectiveDevice <em>Protective Device</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Protective Device</em>' reference.
     * @see #getProtectiveDevice()
     * @generated
     */
    void setProtectiveDevice(ModularDevice value);

} // Circuit
