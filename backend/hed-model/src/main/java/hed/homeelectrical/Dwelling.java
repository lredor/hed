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
 * A representation of the model object '<em><b>Dwelling</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.Dwelling#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link hed.homeelectrical.Dwelling#getStoreys <em>Storeys</em>}</li>
 *   <li>{@link hed.homeelectrical.Dwelling#getEquipment <em>Equipment</em>}</li>
 *   <li>{@link hed.homeelectrical.Dwelling#getBoards <em>Boards</em>}</li>
 *   <li>{@link hed.homeelectrical.Dwelling#getCircuits <em>Circuits</em>}</li>
 *   <li>{@link hed.homeelectrical.Dwelling#getControlFunctions <em>Control Functions</em>}</li>
 *   <li>{@link hed.homeelectrical.Dwelling#getServiceConnection <em>Service Connection</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getDwelling()
 * @model
 * @generated
 */
public interface Dwelling extends NamedElement {
    /**
     * Returns the value of the '<em><b>Catalog</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Catalog</em>' containment reference.
     * @see #setCatalog(Catalog)
     * @see hed.homeelectrical.HomeElectricalPackage#getDwelling_Catalog()
     * @model containment="true" required="true"
     * @generated
     */
    Catalog getCatalog();

    /**
     * Sets the value of the '{@link hed.homeelectrical.Dwelling#getCatalog <em>Catalog</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Catalog</em>' containment reference.
     * @see #getCatalog()
     * @generated
     */
    void setCatalog(Catalog value);

    /**
     * Returns the value of the '<em><b>Storeys</b></em>' containment reference list.
     * The list contents are of type {@link hed.homeelectrical.Storey}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Storeys</em>' containment reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getDwelling_Storeys()
     * @model containment="true"
     * @generated
     */
    EList<Storey> getStoreys();

    /**
     * Returns the value of the '<em><b>Equipment</b></em>' containment reference list.
     * The list contents are of type {@link hed.homeelectrical.FieldEquipment}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Equipment</em>' containment reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getDwelling_Equipment()
     * @model containment="true"
     * @generated
     */
    EList<FieldEquipment> getEquipment();

    /**
     * Returns the value of the '<em><b>Boards</b></em>' containment reference list.
     * The list contents are of type {@link hed.homeelectrical.DistributionBoard}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Boards</em>' containment reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getDwelling_Boards()
     * @model containment="true"
     * @generated
     */
    EList<DistributionBoard> getBoards();

    /**
     * Returns the value of the '<em><b>Circuits</b></em>' containment reference list.
     * The list contents are of type {@link hed.homeelectrical.Circuit}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Circuits</em>' containment reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getDwelling_Circuits()
     * @model containment="true"
     * @generated
     */
    EList<Circuit> getCircuits();

    /**
     * Returns the value of the '<em><b>Control Functions</b></em>' containment reference list.
     * The list contents are of type {@link hed.homeelectrical.ControlFunction}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Control Functions</em>' containment reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getDwelling_ControlFunctions()
     * @model containment="true"
     * @generated
     */
    EList<ControlFunction> getControlFunctions();

    /**
     * Returns the value of the '<em><b>Service Connection</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Service Connection</em>' containment reference.
     * @see #setServiceConnection(ServiceConnection)
     * @see hed.homeelectrical.HomeElectricalPackage#getDwelling_ServiceConnection()
     * @model containment="true"
     * @generated
     */
    ServiceConnection getServiceConnection();

    /**
     * Sets the value of the '{@link hed.homeelectrical.Dwelling#getServiceConnection <em>Service Connection</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Service Connection</em>' containment reference.
     * @see #getServiceConnection()
     * @generated
     */
    void setServiceConnection(ServiceConnection value);

} // Dwelling
