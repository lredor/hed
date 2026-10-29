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
 * A representation of the model object '<em><b>Technical Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.TechnicalElement#getRatedCurrentA <em>Rated Current A</em>}</li>
 *   <li>{@link hed.homeelectrical.TechnicalElement#getRatedPowerW <em>Rated Power W</em>}</li>
 *   <li>{@link hed.homeelectrical.TechnicalElement#getWidthModules <em>Width Modules</em>}</li>
 *   <li>{@link hed.homeelectrical.TechnicalElement#getResidualCurrentMa <em>Residual Current Ma</em>}</li>
 *   <li>{@link hed.homeelectrical.TechnicalElement#getResidualType <em>Residual Type</em>}</li>
 *   <li>{@link hed.homeelectrical.TechnicalElement#getTripCurve <em>Trip Curve</em>}</li>
 *   <li>{@link hed.homeelectrical.TechnicalElement#getPoleConfiguration <em>Pole Configuration</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getTechnicalElement()
 * @model abstract="true"
 * @generated
 */
public interface TechnicalElement extends NamedElement {
    /**
     * Returns the value of the '<em><b>Rated Current A</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Rated Current A</em>' attribute.
     * @see #setRatedCurrentA(Double)
     * @see hed.homeelectrical.HomeElectricalPackage#getTechnicalElement_RatedCurrentA()
     * @model
     * @generated
     */
    Double getRatedCurrentA();

    /**
     * Sets the value of the '{@link hed.homeelectrical.TechnicalElement#getRatedCurrentA <em>Rated Current A</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Rated Current A</em>' attribute.
     * @see #getRatedCurrentA()
     * @generated
     */
    void setRatedCurrentA(Double value);

    /**
     * Returns the value of the '<em><b>Rated Power W</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Rated Power W</em>' attribute.
     * @see #setRatedPowerW(Double)
     * @see hed.homeelectrical.HomeElectricalPackage#getTechnicalElement_RatedPowerW()
     * @model
     * @generated
     */
    Double getRatedPowerW();

    /**
     * Sets the value of the '{@link hed.homeelectrical.TechnicalElement#getRatedPowerW <em>Rated Power W</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Rated Power W</em>' attribute.
     * @see #getRatedPowerW()
     * @generated
     */
    void setRatedPowerW(Double value);

    /**
     * Returns the value of the '<em><b>Width Modules</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Width Modules</em>' attribute.
     * @see #setWidthModules(Double)
     * @see hed.homeelectrical.HomeElectricalPackage#getTechnicalElement_WidthModules()
     * @model
     * @generated
     */
    Double getWidthModules();

    /**
     * Sets the value of the '{@link hed.homeelectrical.TechnicalElement#getWidthModules <em>Width Modules</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Width Modules</em>' attribute.
     * @see #getWidthModules()
     * @generated
     */
    void setWidthModules(Double value);

    /**
     * Returns the value of the '<em><b>Residual Current Ma</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Residual Current Ma</em>' attribute.
     * @see #setResidualCurrentMa(Double)
     * @see hed.homeelectrical.HomeElectricalPackage#getTechnicalElement_ResidualCurrentMa()
     * @model
     * @generated
     */
    Double getResidualCurrentMa();

    /**
     * Sets the value of the '{@link hed.homeelectrical.TechnicalElement#getResidualCurrentMa <em>Residual Current Ma</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Residual Current Ma</em>' attribute.
     * @see #getResidualCurrentMa()
     * @generated
     */
    void setResidualCurrentMa(Double value);

    /**
     * Returns the value of the '<em><b>Residual Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Residual Type</em>' attribute.
     * @see #setResidualType(String)
     * @see hed.homeelectrical.HomeElectricalPackage#getTechnicalElement_ResidualType()
     * @model
     * @generated
     */
    String getResidualType();

    /**
     * Sets the value of the '{@link hed.homeelectrical.TechnicalElement#getResidualType <em>Residual Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Residual Type</em>' attribute.
     * @see #getResidualType()
     * @generated
     */
    void setResidualType(String value);

    /**
     * Returns the value of the '<em><b>Trip Curve</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Trip Curve</em>' attribute.
     * @see #setTripCurve(String)
     * @see hed.homeelectrical.HomeElectricalPackage#getTechnicalElement_TripCurve()
     * @model
     * @generated
     */
    String getTripCurve();

    /**
     * Sets the value of the '{@link hed.homeelectrical.TechnicalElement#getTripCurve <em>Trip Curve</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Trip Curve</em>' attribute.
     * @see #getTripCurve()
     * @generated
     */
    void setTripCurve(String value);

    /**
     * Returns the value of the '<em><b>Pole Configuration</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Pole Configuration</em>' attribute.
     * @see #setPoleConfiguration(String)
     * @see hed.homeelectrical.HomeElectricalPackage#getTechnicalElement_PoleConfiguration()
     * @model
     * @generated
     */
    String getPoleConfiguration();

    /**
     * Sets the value of the '{@link hed.homeelectrical.TechnicalElement#getPoleConfiguration <em>Pole Configuration</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Pole Configuration</em>' attribute.
     * @see #getPoleConfiguration()
     * @generated
     */
    void setPoleConfiguration(String value);

} // TechnicalElement
