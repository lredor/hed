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
 * A representation of the model object '<em><b>Service Connection</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.ServiceConnection#getPhase <em>Phase</em>}</li>
 *   <li>{@link hed.homeelectrical.ServiceConnection#getSubscribedPowerKva <em>Subscribed Power Kva</em>}</li>
 *   <li>{@link hed.homeelectrical.ServiceConnection#getMeter <em>Meter</em>}</li>
 *   <li>{@link hed.homeelectrical.ServiceConnection#getMainBreaker <em>Main Breaker</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getServiceConnection()
 * @model
 * @generated
 */
public interface ServiceConnection extends NamedElement {
    /**
     * Returns the value of the '<em><b>Phase</b></em>' attribute.
     * The literals are from the enumeration {@link hed.homeelectrical.SupplyPhase}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Phase</em>' attribute.
     * @see hed.homeelectrical.SupplyPhase
     * @see #setPhase(SupplyPhase)
     * @see hed.homeelectrical.HomeElectricalPackage#getServiceConnection_Phase()
     * @model
     * @generated
     */
    SupplyPhase getPhase();

    /**
     * Sets the value of the '{@link hed.homeelectrical.ServiceConnection#getPhase <em>Phase</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Phase</em>' attribute.
     * @see hed.homeelectrical.SupplyPhase
     * @see #getPhase()
     * @generated
     */
    void setPhase(SupplyPhase value);

    /**
     * Returns the value of the '<em><b>Subscribed Power Kva</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Subscribed Power Kva</em>' attribute.
     * @see #setSubscribedPowerKva(Double)
     * @see hed.homeelectrical.HomeElectricalPackage#getServiceConnection_SubscribedPowerKva()
     * @model
     * @generated
     */
    Double getSubscribedPowerKva();

    /**
     * Sets the value of the '{@link hed.homeelectrical.ServiceConnection#getSubscribedPowerKva <em>Subscribed Power Kva</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Subscribed Power Kva</em>' attribute.
     * @see #getSubscribedPowerKva()
     * @generated
     */
    void setSubscribedPowerKva(Double value);

    /**
     * Returns the value of the '<em><b>Meter</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Meter</em>' containment reference.
     * @see #setMeter(EnergyMeter)
     * @see hed.homeelectrical.HomeElectricalPackage#getServiceConnection_Meter()
     * @model containment="true"
     * @generated
     */
    EnergyMeter getMeter();

    /**
     * Sets the value of the '{@link hed.homeelectrical.ServiceConnection#getMeter <em>Meter</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Meter</em>' containment reference.
     * @see #getMeter()
     * @generated
     */
    void setMeter(EnergyMeter value);

    /**
     * Returns the value of the '<em><b>Main Breaker</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Main Breaker</em>' containment reference.
     * @see #setMainBreaker(ServiceCircuitBreaker)
     * @see hed.homeelectrical.HomeElectricalPackage#getServiceConnection_MainBreaker()
     * @model containment="true"
     * @generated
     */
    ServiceCircuitBreaker getMainBreaker();

    /**
     * Sets the value of the '{@link hed.homeelectrical.ServiceConnection#getMainBreaker <em>Main Breaker</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Main Breaker</em>' containment reference.
     * @see #getMainBreaker()
     * @generated
     */
    void setMainBreaker(ServiceCircuitBreaker value);

} // ServiceConnection
