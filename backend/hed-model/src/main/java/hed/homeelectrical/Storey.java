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
 * A representation of the model object '<em><b>Storey</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link hed.homeelectrical.Storey#getLevelNumber <em>Level Number</em>}</li>
 *   <li>{@link hed.homeelectrical.Storey#getRooms <em>Rooms</em>}</li>
 *   <li>{@link hed.homeelectrical.Storey#getPlanImageId <em>Plan Image Id</em>}</li>
 *   <li>{@link hed.homeelectrical.Storey#getPlanWidth <em>Plan Width</em>}</li>
 *   <li>{@link hed.homeelectrical.Storey#getPlanHeight <em>Plan Height</em>}</li>
 *   <li>{@link hed.homeelectrical.Storey#getPlanAspectRatio <em>Plan Aspect Ratio</em>}</li>
 * </ul>
 *
 * @see hed.homeelectrical.HomeElectricalPackage#getStorey()
 * @model
 * @generated
 */
public interface Storey extends NamedElement {
    /**
     * Returns the value of the '<em><b>Level Number</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Level Number</em>' attribute.
     * @see #setLevelNumber(Integer)
     * @see hed.homeelectrical.HomeElectricalPackage#getStorey_LevelNumber()
     * @model
     * @generated
     */
    Integer getLevelNumber();

    /**
     * Sets the value of the '{@link hed.homeelectrical.Storey#getLevelNumber <em>Level Number</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Level Number</em>' attribute.
     * @see #getLevelNumber()
     * @generated
     */
    void setLevelNumber(Integer value);

    /**
     * Returns the value of the '<em><b>Rooms</b></em>' containment reference list.
     * The list contents are of type {@link hed.homeelectrical.Room}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Rooms</em>' containment reference list.
     * @see hed.homeelectrical.HomeElectricalPackage#getStorey_Rooms()
     * @model containment="true"
     * @generated
     */
    EList<Room> getRooms();

    /**
     * Returns the value of the '<em><b>Plan Image Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Identifier of this project's image used as the floor-plan background; absent until an image is imported.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Plan Image Id</em>' attribute.
     * @see #setPlanImageId(String)
     * @see hed.homeelectrical.HomeElectricalPackage#getStorey_PlanImageId()
     * @model
     * @generated
     */
    String getPlanImageId();

    /**
     * Sets the value of the '{@link hed.homeelectrical.Storey#getPlanImageId <em>Plan Image Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Plan Image Id</em>' attribute.
     * @see #getPlanImageId()
     * @generated
     */
    void setPlanImageId(String value);

    /**
     * Returns the value of the '<em><b>Plan Width</b></em>' attribute.
     * The default value is <code>"1200"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * User-adjustable floor-plan canvas width in diagram units, defaulting to 1200. Changing the width recalculates the height while preserving image proportions.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Plan Width</em>' attribute.
     * @see #setPlanWidth(int)
     * @see hed.homeelectrical.HomeElectricalPackage#getStorey_PlanWidth()
     * @model default="1200"
     * @generated
     */
    int getPlanWidth();

    /**
     * Sets the value of the '{@link hed.homeelectrical.Storey#getPlanWidth <em>Plan Width</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Plan Width</em>' attribute.
     * @see #getPlanWidth()
     * @generated
     */
    void setPlanWidth(int value);

    /**
     * Returns the value of the '<em><b>Plan Height</b></em>' attribute.
     * The default value is <code>"800"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Calculated image-area height in diagram units, excluding the toolbar. Read-only in Details; width edits and image imports preserve image proportions.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Plan Height</em>' attribute.
     * @see #setPlanHeight(int)
     * @see hed.homeelectrical.HomeElectricalPackage#getStorey_PlanHeight()
     * @model default="800"
     * @generated
     */
    int getPlanHeight();

    /**
     * Sets the value of the '{@link hed.homeelectrical.Storey#getPlanHeight <em>Plan Height</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Plan Height</em>' attribute.
     * @see #getPlanHeight()
     * @generated
     */
    void setPlanHeight(int value);

    /**
     * Returns the value of the '<em><b>Plan Aspect Ratio</b></em>' attribute.
     * The default value is <code>"0.0"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Reference image width divided by height, retained to avoid cumulative integer-rounding drift. Zero uses the current canvas dimensions until the next resize or image import. Managed internally and hidden from Details.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Plan Aspect Ratio</em>' attribute.
     * @see #setPlanAspectRatio(double)
     * @see hed.homeelectrical.HomeElectricalPackage#getStorey_PlanAspectRatio()
     * @model default="0.0"
     * @generated
     */
    double getPlanAspectRatio();

    /**
     * Sets the value of the '{@link hed.homeelectrical.Storey#getPlanAspectRatio <em>Plan Aspect Ratio</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Plan Aspect Ratio</em>' attribute.
     * @see #getPlanAspectRatio()
     * @generated
     */
    void setPlanAspectRatio(double value);

} // Storey
