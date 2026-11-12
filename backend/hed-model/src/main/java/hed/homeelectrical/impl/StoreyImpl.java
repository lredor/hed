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

import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.Room;
import hed.homeelectrical.Storey;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Storey</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.impl.StoreyImpl#getLevelNumber <em>Level Number</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.StoreyImpl#getRooms <em>Rooms</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.StoreyImpl#getPlanImageId <em>Plan Image Id</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.StoreyImpl#getPlanWidth <em>Plan Width</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.StoreyImpl#getPlanHeight <em>Plan Height</em>}</li>
 *   <li>{@link hed.homeelectrical.impl.StoreyImpl#getPlanAspectRatio <em>Plan Aspect Ratio</em>}</li>
 * </ul>
 *
 * @generated
 */
public class StoreyImpl extends NamedElementImpl implements Storey {
    /**
     * The default value of the '{@link #getLevelNumber() <em>Level Number</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLevelNumber()
     * @generated
     * @ordered
     */
    protected static final Integer LEVEL_NUMBER_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLevelNumber() <em>Level Number</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLevelNumber()
     * @generated
     * @ordered
     */
    protected Integer levelNumber = LEVEL_NUMBER_EDEFAULT;

    /**
     * The cached value of the '{@link #getRooms() <em>Rooms</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRooms()
     * @generated
     * @ordered
     */
    protected EList<Room> rooms;

    /**
     * The default value of the '{@link #getPlanImageId() <em>Plan Image Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPlanImageId()
     * @generated
     * @ordered
     */
    protected static final String PLAN_IMAGE_ID_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPlanImageId() <em>Plan Image Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPlanImageId()
     * @generated
     * @ordered
     */
    protected String planImageId = PLAN_IMAGE_ID_EDEFAULT;

    /**
     * The default value of the '{@link #getPlanWidth() <em>Plan Width</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPlanWidth()
     * @generated
     * @ordered
     */
    protected static final int PLAN_WIDTH_EDEFAULT = 1200;

    /**
     * The cached value of the '{@link #getPlanWidth() <em>Plan Width</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPlanWidth()
     * @generated
     * @ordered
     */
    protected int planWidth = PLAN_WIDTH_EDEFAULT;

    /**
     * The default value of the '{@link #getPlanHeight() <em>Plan Height</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPlanHeight()
     * @generated
     * @ordered
     */
    protected static final int PLAN_HEIGHT_EDEFAULT = 800;

    /**
     * The cached value of the '{@link #getPlanHeight() <em>Plan Height</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPlanHeight()
     * @generated
     * @ordered
     */
    protected int planHeight = PLAN_HEIGHT_EDEFAULT;

    /**
     * The default value of the '{@link #getPlanAspectRatio() <em>Plan Aspect Ratio</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPlanAspectRatio()
     * @generated
     * @ordered
     */
    protected static final double PLAN_ASPECT_RATIO_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getPlanAspectRatio() <em>Plan Aspect Ratio</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPlanAspectRatio()
     * @generated
     * @ordered
     */
    protected double planAspectRatio = PLAN_ASPECT_RATIO_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected StoreyImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.STOREY;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Integer getLevelNumber() {
        return levelNumber;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setLevelNumber(Integer newLevelNumber) {
        Integer oldLevelNumber = levelNumber;
        levelNumber = newLevelNumber;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.STOREY__LEVEL_NUMBER, oldLevelNumber, levelNumber));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<Room> getRooms() {
        if (rooms == null) {
            rooms = new EObjectContainmentEList<Room>(Room.class, this, HomeElectricalPackage.STOREY__ROOMS);
        }
        return rooms;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getPlanImageId() {
        return planImageId;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setPlanImageId(String newPlanImageId) {
        String oldPlanImageId = planImageId;
        planImageId = newPlanImageId;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.STOREY__PLAN_IMAGE_ID, oldPlanImageId, planImageId));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int getPlanWidth() {
        return planWidth;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setPlanWidth(int newPlanWidth) {
        int oldPlanWidth = planWidth;
        planWidth = newPlanWidth;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.STOREY__PLAN_WIDTH, oldPlanWidth, planWidth));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int getPlanHeight() {
        return planHeight;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setPlanHeight(int newPlanHeight) {
        int oldPlanHeight = planHeight;
        planHeight = newPlanHeight;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.STOREY__PLAN_HEIGHT, oldPlanHeight, planHeight));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public double getPlanAspectRatio() {
        return planAspectRatio;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setPlanAspectRatio(double newPlanAspectRatio) {
        double oldPlanAspectRatio = planAspectRatio;
        planAspectRatio = newPlanAspectRatio;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, HomeElectricalPackage.STOREY__PLAN_ASPECT_RATIO, oldPlanAspectRatio, planAspectRatio));
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case HomeElectricalPackage.STOREY__ROOMS:
                return ((InternalEList<?>) getRooms()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case HomeElectricalPackage.STOREY__LEVEL_NUMBER:
                return getLevelNumber();
            case HomeElectricalPackage.STOREY__ROOMS:
                return getRooms();
            case HomeElectricalPackage.STOREY__PLAN_IMAGE_ID:
                return getPlanImageId();
            case HomeElectricalPackage.STOREY__PLAN_WIDTH:
                return getPlanWidth();
            case HomeElectricalPackage.STOREY__PLAN_HEIGHT:
                return getPlanHeight();
            case HomeElectricalPackage.STOREY__PLAN_ASPECT_RATIO:
                return getPlanAspectRatio();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case HomeElectricalPackage.STOREY__LEVEL_NUMBER:
                setLevelNumber((Integer) newValue);
                return;
            case HomeElectricalPackage.STOREY__ROOMS:
                getRooms().clear();
                getRooms().addAll((Collection<? extends Room>) newValue);
                return;
            case HomeElectricalPackage.STOREY__PLAN_IMAGE_ID:
                setPlanImageId((String) newValue);
                return;
            case HomeElectricalPackage.STOREY__PLAN_WIDTH:
                setPlanWidth((Integer) newValue);
                return;
            case HomeElectricalPackage.STOREY__PLAN_HEIGHT:
                setPlanHeight((Integer) newValue);
                return;
            case HomeElectricalPackage.STOREY__PLAN_ASPECT_RATIO:
                setPlanAspectRatio((Double) newValue);
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
            case HomeElectricalPackage.STOREY__LEVEL_NUMBER:
                setLevelNumber(LEVEL_NUMBER_EDEFAULT);
                return;
            case HomeElectricalPackage.STOREY__ROOMS:
                getRooms().clear();
                return;
            case HomeElectricalPackage.STOREY__PLAN_IMAGE_ID:
                setPlanImageId(PLAN_IMAGE_ID_EDEFAULT);
                return;
            case HomeElectricalPackage.STOREY__PLAN_WIDTH:
                setPlanWidth(PLAN_WIDTH_EDEFAULT);
                return;
            case HomeElectricalPackage.STOREY__PLAN_HEIGHT:
                setPlanHeight(PLAN_HEIGHT_EDEFAULT);
                return;
            case HomeElectricalPackage.STOREY__PLAN_ASPECT_RATIO:
                setPlanAspectRatio(PLAN_ASPECT_RATIO_EDEFAULT);
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
            case HomeElectricalPackage.STOREY__LEVEL_NUMBER:
                return LEVEL_NUMBER_EDEFAULT == null ? levelNumber != null : !LEVEL_NUMBER_EDEFAULT.equals(levelNumber);
            case HomeElectricalPackage.STOREY__ROOMS:
                return rooms != null && !rooms.isEmpty();
            case HomeElectricalPackage.STOREY__PLAN_IMAGE_ID:
                return PLAN_IMAGE_ID_EDEFAULT == null ? planImageId != null : !PLAN_IMAGE_ID_EDEFAULT.equals(planImageId);
            case HomeElectricalPackage.STOREY__PLAN_WIDTH:
                return planWidth != PLAN_WIDTH_EDEFAULT;
            case HomeElectricalPackage.STOREY__PLAN_HEIGHT:
                return planHeight != PLAN_HEIGHT_EDEFAULT;
            case HomeElectricalPackage.STOREY__PLAN_ASPECT_RATIO:
                return planAspectRatio != PLAN_ASPECT_RATIO_EDEFAULT;
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
        result.append(" (levelNumber: ");
        result.append(levelNumber);
        result.append(", planImageId: ");
        result.append(planImageId);
        result.append(", planWidth: ");
        result.append(planWidth);
        result.append(", planHeight: ");
        result.append(planHeight);
        result.append(", planAspectRatio: ");
        result.append(planAspectRatio);
        result.append(')');
        return result.toString();
    }

} //StoreyImpl
