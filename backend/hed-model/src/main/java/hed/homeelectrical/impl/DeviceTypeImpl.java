/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.homeelectrical.impl;

import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.HomeElectricalPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Device Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.impl.DeviceTypeImpl#getKind <em>Kind</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.DeviceTypeImpl#getManufacturer <em>Manufacturer</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.DeviceTypeImpl#getProductReference <em>Product Reference</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.DeviceTypeImpl#getChannelCount <em>Channel Count</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DeviceTypeImpl extends TechnicalElementImpl implements DeviceType {
    /**
     * The default value of the '{@link #getKind() <em>Kind</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getKind()
     * @generated
     * @ordered
     */
    protected static final DeviceKind KIND_EDEFAULT = DeviceKind.OUTLET;

    /**
     * The cached value of the '{@link #getKind() <em>Kind</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getKind()
     * @generated
     * @ordered
     */
    protected DeviceKind kind = KIND_EDEFAULT;

    /**
     * The default value of the '{@link #getManufacturer() <em>Manufacturer</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getManufacturer()
     * @generated
     * @ordered
     */
    protected static final String MANUFACTURER_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getManufacturer() <em>Manufacturer</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getManufacturer()
     * @generated
     * @ordered
     */
    protected String manufacturer = MANUFACTURER_EDEFAULT;

    /**
     * The default value of the '{@link #getProductReference() <em>Product Reference</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProductReference()
     * @generated
     * @ordered
     */
    protected static final String PRODUCT_REFERENCE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getProductReference() <em>Product Reference</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProductReference()
     * @generated
     * @ordered
     */
    protected String productReference = PRODUCT_REFERENCE_EDEFAULT;

    /**
     * The default value of the '{@link #getChannelCount() <em>Channel Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getChannelCount()
     * @generated
     * @ordered
     */
    protected static final int CHANNEL_COUNT_EDEFAULT = 1;

    /**
     * The cached value of the '{@link #getChannelCount() <em>Channel Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getChannelCount()
     * @generated
     * @ordered
     */
    protected int channelCount = CHANNEL_COUNT_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DeviceTypeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.DEVICE_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public DeviceKind getKind() {
        return kind;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setKind(DeviceKind newKind) {
        DeviceKind oldKind = kind;
        kind = newKind == null ? KIND_EDEFAULT : newKind;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.DEVICE_TYPE__KIND, oldKind, kind));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setManufacturer(String newManufacturer) {
        String oldManufacturer = manufacturer;
        manufacturer = newManufacturer;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.DEVICE_TYPE__MANUFACTURER, oldManufacturer, manufacturer));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getProductReference() {
        return productReference;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setProductReference(String newProductReference) {
        String oldProductReference = productReference;
        productReference = newProductReference;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.DEVICE_TYPE__PRODUCT_REFERENCE, oldProductReference, productReference));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int getChannelCount() {
        return channelCount;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setChannelCount(int newChannelCount) {
        int oldChannelCount = channelCount;
        channelCount = newChannelCount;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.DEVICE_TYPE__CHANNEL_COUNT, oldChannelCount, channelCount));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case HomeElectricalPackage.DEVICE_TYPE__KIND:
                return getKind();
            case HomeElectricalPackage.DEVICE_TYPE__MANUFACTURER:
                return getManufacturer();
            case HomeElectricalPackage.DEVICE_TYPE__PRODUCT_REFERENCE:
                return getProductReference();
            case HomeElectricalPackage.DEVICE_TYPE__CHANNEL_COUNT:
                return getChannelCount();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case HomeElectricalPackage.DEVICE_TYPE__KIND:
                setKind((DeviceKind) newValue);
                return;
            case HomeElectricalPackage.DEVICE_TYPE__MANUFACTURER:
                setManufacturer((String) newValue);
                return;
            case HomeElectricalPackage.DEVICE_TYPE__PRODUCT_REFERENCE:
                setProductReference((String) newValue);
                return;
            case HomeElectricalPackage.DEVICE_TYPE__CHANNEL_COUNT:
                setChannelCount((Integer) newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case HomeElectricalPackage.DEVICE_TYPE__KIND:
                setKind(KIND_EDEFAULT);
                return;
            case HomeElectricalPackage.DEVICE_TYPE__MANUFACTURER:
                setManufacturer(MANUFACTURER_EDEFAULT);
                return;
            case HomeElectricalPackage.DEVICE_TYPE__PRODUCT_REFERENCE:
                setProductReference(PRODUCT_REFERENCE_EDEFAULT);
                return;
            case HomeElectricalPackage.DEVICE_TYPE__CHANNEL_COUNT:
                setChannelCount(CHANNEL_COUNT_EDEFAULT);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case HomeElectricalPackage.DEVICE_TYPE__KIND:
                return kind != KIND_EDEFAULT;
            case HomeElectricalPackage.DEVICE_TYPE__MANUFACTURER:
                return MANUFACTURER_EDEFAULT == null ? manufacturer != null : !MANUFACTURER_EDEFAULT.equals(manufacturer);
            case HomeElectricalPackage.DEVICE_TYPE__PRODUCT_REFERENCE:
                return PRODUCT_REFERENCE_EDEFAULT == null ? productReference != null : !PRODUCT_REFERENCE_EDEFAULT.equals(productReference);
            case HomeElectricalPackage.DEVICE_TYPE__CHANNEL_COUNT:
                return channelCount != CHANNEL_COUNT_EDEFAULT;
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) {
            return super.toString();
        }

        StringBuilder result = new StringBuilder(super.toString());
        result.append(" (kind: ");
        result.append(kind);
        result.append(", manufacturer: ");
        result.append(manufacturer);
        result.append(", productReference: ");
        result.append(productReference);
        result.append(", channelCount: ");
        result.append(channelCount);
        result.append(')');
        return result.toString();
    }

} //DeviceTypeImpl
