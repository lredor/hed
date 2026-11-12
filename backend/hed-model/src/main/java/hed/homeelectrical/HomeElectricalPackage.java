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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see hed.homeelectrical.HomeElectricalFactory
 * @model kind="package"
 * @generated
 */
public interface HomeElectricalPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "homeelectrical";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "https://lredor.github.io/home-electrical-designer/1.0";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "hed";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    HomeElectricalPackage eINSTANCE = hed.homeelectrical.impl.HomeElectricalPackageImpl.init();

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.NamedElementImpl <em>Named Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.NamedElementImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getNamedElement()
     * @generated
     */
    int NAMED_ELEMENT = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_ELEMENT__NAME = 0;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_ELEMENT__DESCRIPTION = 1;

    /**
     * The number of structural features of the '<em>Named Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_ELEMENT_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.TechnicalElementImpl <em>Technical Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.TechnicalElementImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getTechnicalElement()
     * @generated
     */
    int TECHNICAL_ELEMENT = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TECHNICAL_ELEMENT__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TECHNICAL_ELEMENT__DESCRIPTION = NAMED_ELEMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Rated Current A</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TECHNICAL_ELEMENT__RATED_CURRENT_A = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Rated Power W</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TECHNICAL_ELEMENT__RATED_POWER_W = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Width Modules</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TECHNICAL_ELEMENT__WIDTH_MODULES = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Residual Current Ma</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TECHNICAL_ELEMENT__RESIDUAL_CURRENT_MA = NAMED_ELEMENT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Residual Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TECHNICAL_ELEMENT__RESIDUAL_TYPE = NAMED_ELEMENT_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Trip Curve</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TECHNICAL_ELEMENT__TRIP_CURVE = NAMED_ELEMENT_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Pole Configuration</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TECHNICAL_ELEMENT__POLE_CONFIGURATION = NAMED_ELEMENT_FEATURE_COUNT + 6;

    /**
     * The number of structural features of the '<em>Technical Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TECHNICAL_ELEMENT_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 7;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.DwellingImpl <em>Dwelling</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.DwellingImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getDwelling()
     * @generated
     */
    int DWELLING = 2;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DWELLING__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DWELLING__DESCRIPTION = NAMED_ELEMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Catalog</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DWELLING__CATALOG = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Storeys</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DWELLING__STOREYS = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Equipment</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DWELLING__EQUIPMENT = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Boards</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DWELLING__BOARDS = NAMED_ELEMENT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Circuits</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DWELLING__CIRCUITS = NAMED_ELEMENT_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Control Functions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DWELLING__CONTROL_FUNCTIONS = NAMED_ELEMENT_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Service Connection</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DWELLING__SERVICE_CONNECTION = NAMED_ELEMENT_FEATURE_COUNT + 6;

    /**
     * The number of structural features of the '<em>Dwelling</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DWELLING_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 7;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.StoreyImpl <em>Storey</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.StoreyImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getStorey()
     * @generated
     */
    int STOREY = 3;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STOREY__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STOREY__DESCRIPTION = NAMED_ELEMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Level Number</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STOREY__LEVEL_NUMBER = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Rooms</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STOREY__ROOMS = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Plan Image Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STOREY__PLAN_IMAGE_ID = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Plan Width</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STOREY__PLAN_WIDTH = NAMED_ELEMENT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Plan Height</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STOREY__PLAN_HEIGHT = NAMED_ELEMENT_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Plan Aspect Ratio</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STOREY__PLAN_ASPECT_RATIO = NAMED_ELEMENT_FEATURE_COUNT + 5;

    /**
     * The number of structural features of the '<em>Storey</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STOREY_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 6;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.RoomImpl <em>Room</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.RoomImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getRoom()
     * @generated
     */
    int ROOM = 4;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROOM__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROOM__DESCRIPTION = NAMED_ELEMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Usage</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROOM__USAGE = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Room</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROOM_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.CatalogImpl <em>Catalog</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.CatalogImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getCatalog()
     * @generated
     */
    int CATALOG = 5;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CATALOG__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CATALOG__DESCRIPTION = NAMED_ELEMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Device Types</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CATALOG__DEVICE_TYPES = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Catalog</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CATALOG_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.DeviceTypeImpl <em>Device Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.DeviceTypeImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getDeviceType()
     * @generated
     */
    int DEVICE_TYPE = 6;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__NAME = TECHNICAL_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__DESCRIPTION = TECHNICAL_ELEMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Rated Current A</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__RATED_CURRENT_A = TECHNICAL_ELEMENT__RATED_CURRENT_A;

    /**
     * The feature id for the '<em><b>Rated Power W</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__RATED_POWER_W = TECHNICAL_ELEMENT__RATED_POWER_W;

    /**
     * The feature id for the '<em><b>Width Modules</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__WIDTH_MODULES = TECHNICAL_ELEMENT__WIDTH_MODULES;

    /**
     * The feature id for the '<em><b>Residual Current Ma</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__RESIDUAL_CURRENT_MA = TECHNICAL_ELEMENT__RESIDUAL_CURRENT_MA;

    /**
     * The feature id for the '<em><b>Residual Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__RESIDUAL_TYPE = TECHNICAL_ELEMENT__RESIDUAL_TYPE;

    /**
     * The feature id for the '<em><b>Trip Curve</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__TRIP_CURVE = TECHNICAL_ELEMENT__TRIP_CURVE;

    /**
     * The feature id for the '<em><b>Pole Configuration</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__POLE_CONFIGURATION = TECHNICAL_ELEMENT__POLE_CONFIGURATION;

    /**
     * The feature id for the '<em><b>Kind</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__KIND = TECHNICAL_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Manufacturer</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__MANUFACTURER = TECHNICAL_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Product Reference</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__PRODUCT_REFERENCE = TECHNICAL_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Channel Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__CHANNEL_COUNT = TECHNICAL_ELEMENT_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the '<em>Device Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE_FEATURE_COUNT = TECHNICAL_ELEMENT_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.EquipmentImpl <em>Equipment</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.EquipmentImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getEquipment()
     * @generated
     */
    int EQUIPMENT = 7;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EQUIPMENT__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EQUIPMENT__DESCRIPTION = NAMED_ELEMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Device Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EQUIPMENT__DEVICE_TYPE = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Equipment</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EQUIPMENT_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.FieldEquipmentImpl <em>Field Equipment</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.FieldEquipmentImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getFieldEquipment()
     * @generated
     */
    int FIELD_EQUIPMENT = 8;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FIELD_EQUIPMENT__NAME = EQUIPMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FIELD_EQUIPMENT__DESCRIPTION = EQUIPMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Device Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FIELD_EQUIPMENT__DEVICE_TYPE = EQUIPMENT__DEVICE_TYPE;

    /**
     * The feature id for the '<em><b>Rated Current A</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FIELD_EQUIPMENT__RATED_CURRENT_A = EQUIPMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Rated Power W</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FIELD_EQUIPMENT__RATED_POWER_W = EQUIPMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Width Modules</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FIELD_EQUIPMENT__WIDTH_MODULES = EQUIPMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Residual Current Ma</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FIELD_EQUIPMENT__RESIDUAL_CURRENT_MA = EQUIPMENT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Residual Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FIELD_EQUIPMENT__RESIDUAL_TYPE = EQUIPMENT_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Trip Curve</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FIELD_EQUIPMENT__TRIP_CURVE = EQUIPMENT_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Pole Configuration</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FIELD_EQUIPMENT__POLE_CONFIGURATION = EQUIPMENT_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Room</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FIELD_EQUIPMENT__ROOM = EQUIPMENT_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Circuit</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FIELD_EQUIPMENT__CIRCUIT = EQUIPMENT_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Branch</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FIELD_EQUIPMENT__BRANCH = EQUIPMENT_FEATURE_COUNT + 9;

    /**
     * The number of structural features of the '<em>Field Equipment</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FIELD_EQUIPMENT_FEATURE_COUNT = EQUIPMENT_FEATURE_COUNT + 10;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.OutletImpl <em>Outlet</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.OutletImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getOutlet()
     * @generated
     */
    int OUTLET = 9;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OUTLET__NAME = FIELD_EQUIPMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OUTLET__DESCRIPTION = FIELD_EQUIPMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Device Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OUTLET__DEVICE_TYPE = FIELD_EQUIPMENT__DEVICE_TYPE;

    /**
     * The feature id for the '<em><b>Rated Current A</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OUTLET__RATED_CURRENT_A = FIELD_EQUIPMENT__RATED_CURRENT_A;

    /**
     * The feature id for the '<em><b>Rated Power W</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OUTLET__RATED_POWER_W = FIELD_EQUIPMENT__RATED_POWER_W;

    /**
     * The feature id for the '<em><b>Width Modules</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OUTLET__WIDTH_MODULES = FIELD_EQUIPMENT__WIDTH_MODULES;

    /**
     * The feature id for the '<em><b>Residual Current Ma</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OUTLET__RESIDUAL_CURRENT_MA = FIELD_EQUIPMENT__RESIDUAL_CURRENT_MA;

    /**
     * The feature id for the '<em><b>Residual Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OUTLET__RESIDUAL_TYPE = FIELD_EQUIPMENT__RESIDUAL_TYPE;

    /**
     * The feature id for the '<em><b>Trip Curve</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OUTLET__TRIP_CURVE = FIELD_EQUIPMENT__TRIP_CURVE;

    /**
     * The feature id for the '<em><b>Pole Configuration</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OUTLET__POLE_CONFIGURATION = FIELD_EQUIPMENT__POLE_CONFIGURATION;

    /**
     * The feature id for the '<em><b>Room</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OUTLET__ROOM = FIELD_EQUIPMENT__ROOM;

    /**
     * The feature id for the '<em><b>Circuit</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OUTLET__CIRCUIT = FIELD_EQUIPMENT__CIRCUIT;

    /**
     * The feature id for the '<em><b>Branch</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OUTLET__BRANCH = FIELD_EQUIPMENT__BRANCH;

    /**
     * The number of structural features of the '<em>Outlet</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OUTLET_FEATURE_COUNT = FIELD_EQUIPMENT_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.ElectricalLoadImpl <em>Electrical Load</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.ElectricalLoadImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getElectricalLoad()
     * @generated
     */
    int ELECTRICAL_LOAD = 10;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELECTRICAL_LOAD__NAME = FIELD_EQUIPMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELECTRICAL_LOAD__DESCRIPTION = FIELD_EQUIPMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Device Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELECTRICAL_LOAD__DEVICE_TYPE = FIELD_EQUIPMENT__DEVICE_TYPE;

    /**
     * The feature id for the '<em><b>Rated Current A</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELECTRICAL_LOAD__RATED_CURRENT_A = FIELD_EQUIPMENT__RATED_CURRENT_A;

    /**
     * The feature id for the '<em><b>Rated Power W</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELECTRICAL_LOAD__RATED_POWER_W = FIELD_EQUIPMENT__RATED_POWER_W;

    /**
     * The feature id for the '<em><b>Width Modules</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELECTRICAL_LOAD__WIDTH_MODULES = FIELD_EQUIPMENT__WIDTH_MODULES;

    /**
     * The feature id for the '<em><b>Residual Current Ma</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELECTRICAL_LOAD__RESIDUAL_CURRENT_MA = FIELD_EQUIPMENT__RESIDUAL_CURRENT_MA;

    /**
     * The feature id for the '<em><b>Residual Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELECTRICAL_LOAD__RESIDUAL_TYPE = FIELD_EQUIPMENT__RESIDUAL_TYPE;

    /**
     * The feature id for the '<em><b>Trip Curve</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELECTRICAL_LOAD__TRIP_CURVE = FIELD_EQUIPMENT__TRIP_CURVE;

    /**
     * The feature id for the '<em><b>Pole Configuration</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELECTRICAL_LOAD__POLE_CONFIGURATION = FIELD_EQUIPMENT__POLE_CONFIGURATION;

    /**
     * The feature id for the '<em><b>Room</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELECTRICAL_LOAD__ROOM = FIELD_EQUIPMENT__ROOM;

    /**
     * The feature id for the '<em><b>Circuit</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELECTRICAL_LOAD__CIRCUIT = FIELD_EQUIPMENT__CIRCUIT;

    /**
     * The feature id for the '<em><b>Branch</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELECTRICAL_LOAD__BRANCH = FIELD_EQUIPMENT__BRANCH;

    /**
     * The number of structural features of the '<em>Electrical Load</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELECTRICAL_LOAD_FEATURE_COUNT = FIELD_EQUIPMENT_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.ControlDeviceImpl <em>Control Device</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.ControlDeviceImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getControlDevice()
     * @generated
     */
    int CONTROL_DEVICE = 11;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_DEVICE__NAME = FIELD_EQUIPMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_DEVICE__DESCRIPTION = FIELD_EQUIPMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Device Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_DEVICE__DEVICE_TYPE = FIELD_EQUIPMENT__DEVICE_TYPE;

    /**
     * The feature id for the '<em><b>Rated Current A</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_DEVICE__RATED_CURRENT_A = FIELD_EQUIPMENT__RATED_CURRENT_A;

    /**
     * The feature id for the '<em><b>Rated Power W</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_DEVICE__RATED_POWER_W = FIELD_EQUIPMENT__RATED_POWER_W;

    /**
     * The feature id for the '<em><b>Width Modules</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_DEVICE__WIDTH_MODULES = FIELD_EQUIPMENT__WIDTH_MODULES;

    /**
     * The feature id for the '<em><b>Residual Current Ma</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_DEVICE__RESIDUAL_CURRENT_MA = FIELD_EQUIPMENT__RESIDUAL_CURRENT_MA;

    /**
     * The feature id for the '<em><b>Residual Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_DEVICE__RESIDUAL_TYPE = FIELD_EQUIPMENT__RESIDUAL_TYPE;

    /**
     * The feature id for the '<em><b>Trip Curve</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_DEVICE__TRIP_CURVE = FIELD_EQUIPMENT__TRIP_CURVE;

    /**
     * The feature id for the '<em><b>Pole Configuration</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_DEVICE__POLE_CONFIGURATION = FIELD_EQUIPMENT__POLE_CONFIGURATION;

    /**
     * The feature id for the '<em><b>Room</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_DEVICE__ROOM = FIELD_EQUIPMENT__ROOM;

    /**
     * The feature id for the '<em><b>Circuit</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_DEVICE__CIRCUIT = FIELD_EQUIPMENT__CIRCUIT;

    /**
     * The feature id for the '<em><b>Branch</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_DEVICE__BRANCH = FIELD_EQUIPMENT__BRANCH;

    /**
     * The feature id for the '<em><b>Channels</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_DEVICE__CHANNELS = FIELD_EQUIPMENT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Control Device</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_DEVICE_FEATURE_COUNT = FIELD_EQUIPMENT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.ControlChannelImpl <em>Control Channel</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.ControlChannelImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getControlChannel()
     * @generated
     */
    int CONTROL_CHANNEL = 12;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_CHANNEL__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_CHANNEL__DESCRIPTION = NAMED_ELEMENT__DESCRIPTION;

    /**
     * The number of structural features of the '<em>Control Channel</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_CHANNEL_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.DistributionBoardImpl <em>Distribution Board</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.DistributionBoardImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getDistributionBoard()
     * @generated
     */
    int DISTRIBUTION_BOARD = 13;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DISTRIBUTION_BOARD__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DISTRIBUTION_BOARD__DESCRIPTION = NAMED_ELEMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Room</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DISTRIBUTION_BOARD__ROOM = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Rails</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DISTRIBUTION_BOARD__RAILS = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Devices</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DISTRIBUTION_BOARD__DEVICES = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the '<em>Distribution Board</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DISTRIBUTION_BOARD_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.DINRailImpl <em>DIN Rail</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.DINRailImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getDINRail()
     * @generated
     */
    int DIN_RAIL = 14;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIN_RAIL__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIN_RAIL__DESCRIPTION = NAMED_ELEMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Capacity Modules</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIN_RAIL__CAPACITY_MODULES = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Devices</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIN_RAIL__DEVICES = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>DIN Rail</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIN_RAIL_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.ModularDeviceImpl <em>Modular Device</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.ModularDeviceImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getModularDevice()
     * @generated
     */
    int MODULAR_DEVICE = 15;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODULAR_DEVICE__NAME = EQUIPMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODULAR_DEVICE__DESCRIPTION = EQUIPMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Device Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODULAR_DEVICE__DEVICE_TYPE = EQUIPMENT__DEVICE_TYPE;

    /**
     * The feature id for the '<em><b>Label Text</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODULAR_DEVICE__LABEL_TEXT = EQUIPMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Rail</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODULAR_DEVICE__RAIL = EQUIPMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Upstream Rccb</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODULAR_DEVICE__UPSTREAM_RCCB = EQUIPMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Upstream Service Breaker</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODULAR_DEVICE__UPSTREAM_SERVICE_BREAKER = EQUIPMENT_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the '<em>Modular Device</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODULAR_DEVICE_FEATURE_COUNT = EQUIPMENT_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.CircuitImpl <em>Circuit</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.CircuitImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getCircuit()
     * @generated
     */
    int CIRCUIT = 16;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CIRCUIT__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CIRCUIT__DESCRIPTION = NAMED_ELEMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Phase</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CIRCUIT__PHASE = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Conductor Section Mm2</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CIRCUIT__CONDUCTOR_SECTION_MM2 = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Source Board</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CIRCUIT__SOURCE_BOARD = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Protective Device</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CIRCUIT__PROTECTIVE_DEVICE = NAMED_ELEMENT_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the '<em>Circuit</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CIRCUIT_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.FinalCircuitImpl <em>Final Circuit</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.FinalCircuitImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getFinalCircuit()
     * @generated
     */
    int FINAL_CIRCUIT = 17;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_CIRCUIT__NAME = CIRCUIT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_CIRCUIT__DESCRIPTION = CIRCUIT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Phase</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_CIRCUIT__PHASE = CIRCUIT__PHASE;

    /**
     * The feature id for the '<em><b>Conductor Section Mm2</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_CIRCUIT__CONDUCTOR_SECTION_MM2 = CIRCUIT__CONDUCTOR_SECTION_MM2;

    /**
     * The feature id for the '<em><b>Source Board</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_CIRCUIT__SOURCE_BOARD = CIRCUIT__SOURCE_BOARD;

    /**
     * The feature id for the '<em><b>Protective Device</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_CIRCUIT__PROTECTIVE_DEVICE = CIRCUIT__PROTECTIVE_DEVICE;

    /**
     * The feature id for the '<em><b>Usage</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_CIRCUIT__USAGE = CIRCUIT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Branches</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_CIRCUIT__BRANCHES = CIRCUIT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Final Circuit</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_CIRCUIT_FEATURE_COUNT = CIRCUIT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.DistributionCircuitImpl <em>Distribution Circuit</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.DistributionCircuitImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getDistributionCircuit()
     * @generated
     */
    int DISTRIBUTION_CIRCUIT = 18;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DISTRIBUTION_CIRCUIT__NAME = CIRCUIT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DISTRIBUTION_CIRCUIT__DESCRIPTION = CIRCUIT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Phase</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DISTRIBUTION_CIRCUIT__PHASE = CIRCUIT__PHASE;

    /**
     * The feature id for the '<em><b>Conductor Section Mm2</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DISTRIBUTION_CIRCUIT__CONDUCTOR_SECTION_MM2 = CIRCUIT__CONDUCTOR_SECTION_MM2;

    /**
     * The feature id for the '<em><b>Source Board</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DISTRIBUTION_CIRCUIT__SOURCE_BOARD = CIRCUIT__SOURCE_BOARD;

    /**
     * The feature id for the '<em><b>Protective Device</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DISTRIBUTION_CIRCUIT__PROTECTIVE_DEVICE = CIRCUIT__PROTECTIVE_DEVICE;

    /**
     * The feature id for the '<em><b>Target Board</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DISTRIBUTION_CIRCUIT__TARGET_BOARD = CIRCUIT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Distribution Circuit</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DISTRIBUTION_CIRCUIT_FEATURE_COUNT = CIRCUIT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.ControlFunctionImpl <em>Control Function</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.ControlFunctionImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getControlFunction()
     * @generated
     */
    int CONTROL_FUNCTION = 19;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_FUNCTION__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_FUNCTION__DESCRIPTION = NAMED_ELEMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Mode</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_FUNCTION__MODE = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Channels</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_FUNCTION__CHANNELS = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Targets</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_FUNCTION__TARGETS = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Actuator</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_FUNCTION__ACTUATOR = NAMED_ELEMENT_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the '<em>Control Function</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_FUNCTION_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.ServiceConnectionImpl <em>Service Connection</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.ServiceConnectionImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getServiceConnection()
     * @generated
     */
    int SERVICE_CONNECTION = 20;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CONNECTION__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CONNECTION__DESCRIPTION = NAMED_ELEMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Phase</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CONNECTION__PHASE = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Subscribed Power Kva</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CONNECTION__SUBSCRIBED_POWER_KVA = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Meter</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CONNECTION__METER = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Main Breaker</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CONNECTION__MAIN_BREAKER = NAMED_ELEMENT_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the '<em>Service Connection</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CONNECTION_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.EnergyMeterImpl <em>Energy Meter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.EnergyMeterImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getEnergyMeter()
     * @generated
     */
    int ENERGY_METER = 21;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENERGY_METER__NAME = EQUIPMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENERGY_METER__DESCRIPTION = EQUIPMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Device Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENERGY_METER__DEVICE_TYPE = EQUIPMENT__DEVICE_TYPE;

    /**
     * The feature id for the '<em><b>Rated Current A</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENERGY_METER__RATED_CURRENT_A = EQUIPMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Rated Power W</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENERGY_METER__RATED_POWER_W = EQUIPMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Width Modules</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENERGY_METER__WIDTH_MODULES = EQUIPMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Residual Current Ma</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENERGY_METER__RESIDUAL_CURRENT_MA = EQUIPMENT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Residual Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENERGY_METER__RESIDUAL_TYPE = EQUIPMENT_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Trip Curve</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENERGY_METER__TRIP_CURVE = EQUIPMENT_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Pole Configuration</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENERGY_METER__POLE_CONFIGURATION = EQUIPMENT_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Serial Number</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENERGY_METER__SERIAL_NUMBER = EQUIPMENT_FEATURE_COUNT + 7;

    /**
     * The number of structural features of the '<em>Energy Meter</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENERGY_METER_FEATURE_COUNT = EQUIPMENT_FEATURE_COUNT + 8;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.ServiceCircuitBreakerImpl <em>Service Circuit Breaker</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.ServiceCircuitBreakerImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getServiceCircuitBreaker()
     * @generated
     */
    int SERVICE_CIRCUIT_BREAKER = 22;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CIRCUIT_BREAKER__NAME = EQUIPMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CIRCUIT_BREAKER__DESCRIPTION = EQUIPMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Device Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CIRCUIT_BREAKER__DEVICE_TYPE = EQUIPMENT__DEVICE_TYPE;

    /**
     * The feature id for the '<em><b>Rated Current A</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CIRCUIT_BREAKER__RATED_CURRENT_A = EQUIPMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Rated Power W</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CIRCUIT_BREAKER__RATED_POWER_W = EQUIPMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Width Modules</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CIRCUIT_BREAKER__WIDTH_MODULES = EQUIPMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Residual Current Ma</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CIRCUIT_BREAKER__RESIDUAL_CURRENT_MA = EQUIPMENT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Residual Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CIRCUIT_BREAKER__RESIDUAL_TYPE = EQUIPMENT_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Trip Curve</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CIRCUIT_BREAKER__TRIP_CURVE = EQUIPMENT_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Pole Configuration</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CIRCUIT_BREAKER__POLE_CONFIGURATION = EQUIPMENT_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Downstream Devices</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CIRCUIT_BREAKER__DOWNSTREAM_DEVICES = EQUIPMENT_FEATURE_COUNT + 7;

    /**
     * The number of structural features of the '<em>Service Circuit Breaker</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_CIRCUIT_BREAKER_FEATURE_COUNT = EQUIPMENT_FEATURE_COUNT + 8;

    /**
     * The meta object id for the '{@link hed.homeelectrical.impl.CircuitBranchImpl <em>Circuit Branch</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.impl.CircuitBranchImpl
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getCircuitBranch()
     * @generated
     */
    int CIRCUIT_BRANCH = 23;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CIRCUIT_BRANCH__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CIRCUIT_BRANCH__DESCRIPTION = NAMED_ELEMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Circuit</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CIRCUIT_BRANCH__CIRCUIT = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Equipment</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CIRCUIT_BRANCH__EQUIPMENT = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Circuit Branch</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CIRCUIT_BRANCH_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link hed.homeelectrical.DeviceKind <em>Device Kind</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.DeviceKind
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getDeviceKind()
     * @generated
     */
    int DEVICE_KIND = 24;

    /**
     * The meta object id for the '{@link hed.homeelectrical.SupplyPhase <em>Supply Phase</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.SupplyPhase
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getSupplyPhase()
     * @generated
     */
    int SUPPLY_PHASE = 25;

    /**
     * The meta object id for the '{@link hed.homeelectrical.CircuitPhase <em>Circuit Phase</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.CircuitPhase
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getCircuitPhase()
     * @generated
     */
    int CIRCUIT_PHASE = 26;

    /**
     * The meta object id for the '{@link hed.homeelectrical.CircuitUse <em>Circuit Use</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.CircuitUse
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getCircuitUse()
     * @generated
     */
    int CIRCUIT_USE = 27;

    /**
     * The meta object id for the '{@link hed.homeelectrical.ControlMode <em>Control Mode</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see hed.homeelectrical.ControlMode
     * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getControlMode()
     * @generated
     */
    int CONTROL_MODE = 28;

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.NamedElement <em>Named Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Named Element</em>'.
     * @see hed.homeelectrical.NamedElement
     * @generated
     */
    EClass getNamedElement();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.NamedElement#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see hed.homeelectrical.NamedElement#getName()
     * @see #getNamedElement()
     * @generated
     */
    EAttribute getNamedElement_Name();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.NamedElement#getDescription <em>Description</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Description</em>'.
     * @see hed.homeelectrical.NamedElement#getDescription()
     * @see #getNamedElement()
     * @generated
     */
    EAttribute getNamedElement_Description();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.TechnicalElement <em>Technical Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Technical Element</em>'.
     * @see hed.homeelectrical.TechnicalElement
     * @generated
     */
    EClass getTechnicalElement();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.TechnicalElement#getRatedCurrentA <em>Rated Current A</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Rated Current A</em>'.
     * @see hed.homeelectrical.TechnicalElement#getRatedCurrentA()
     * @see #getTechnicalElement()
     * @generated
     */
    EAttribute getTechnicalElement_RatedCurrentA();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.TechnicalElement#getRatedPowerW <em>Rated Power W</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Rated Power W</em>'.
     * @see hed.homeelectrical.TechnicalElement#getRatedPowerW()
     * @see #getTechnicalElement()
     * @generated
     */
    EAttribute getTechnicalElement_RatedPowerW();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.TechnicalElement#getWidthModules <em>Width Modules</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Width Modules</em>'.
     * @see hed.homeelectrical.TechnicalElement#getWidthModules()
     * @see #getTechnicalElement()
     * @generated
     */
    EAttribute getTechnicalElement_WidthModules();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.TechnicalElement#getResidualCurrentMa <em>Residual Current Ma</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Residual Current Ma</em>'.
     * @see hed.homeelectrical.TechnicalElement#getResidualCurrentMa()
     * @see #getTechnicalElement()
     * @generated
     */
    EAttribute getTechnicalElement_ResidualCurrentMa();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.TechnicalElement#getResidualType <em>Residual Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Residual Type</em>'.
     * @see hed.homeelectrical.TechnicalElement#getResidualType()
     * @see #getTechnicalElement()
     * @generated
     */
    EAttribute getTechnicalElement_ResidualType();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.TechnicalElement#getTripCurve <em>Trip Curve</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Trip Curve</em>'.
     * @see hed.homeelectrical.TechnicalElement#getTripCurve()
     * @see #getTechnicalElement()
     * @generated
     */
    EAttribute getTechnicalElement_TripCurve();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.TechnicalElement#getPoleConfiguration <em>Pole Configuration</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Pole Configuration</em>'.
     * @see hed.homeelectrical.TechnicalElement#getPoleConfiguration()
     * @see #getTechnicalElement()
     * @generated
     */
    EAttribute getTechnicalElement_PoleConfiguration();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.Dwelling <em>Dwelling</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Dwelling</em>'.
     * @see hed.homeelectrical.Dwelling
     * @generated
     */
    EClass getDwelling();

    /**
     * Returns the meta object for the containment reference '{@link hed.homeelectrical.Dwelling#getCatalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Catalog</em>'.
     * @see hed.homeelectrical.Dwelling#getCatalog()
     * @see #getDwelling()
     * @generated
     */
    EReference getDwelling_Catalog();

    /**
     * Returns the meta object for the containment reference list '{@link hed.homeelectrical.Dwelling#getStoreys <em>Storeys</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Storeys</em>'.
     * @see hed.homeelectrical.Dwelling#getStoreys()
     * @see #getDwelling()
     * @generated
     */
    EReference getDwelling_Storeys();

    /**
     * Returns the meta object for the containment reference list '{@link hed.homeelectrical.Dwelling#getEquipment <em>Equipment</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Equipment</em>'.
     * @see hed.homeelectrical.Dwelling#getEquipment()
     * @see #getDwelling()
     * @generated
     */
    EReference getDwelling_Equipment();

    /**
     * Returns the meta object for the containment reference list '{@link hed.homeelectrical.Dwelling#getBoards <em>Boards</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Boards</em>'.
     * @see hed.homeelectrical.Dwelling#getBoards()
     * @see #getDwelling()
     * @generated
     */
    EReference getDwelling_Boards();

    /**
     * Returns the meta object for the containment reference list '{@link hed.homeelectrical.Dwelling#getCircuits <em>Circuits</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Circuits</em>'.
     * @see hed.homeelectrical.Dwelling#getCircuits()
     * @see #getDwelling()
     * @generated
     */
    EReference getDwelling_Circuits();

    /**
     * Returns the meta object for the containment reference list '{@link hed.homeelectrical.Dwelling#getControlFunctions <em>Control Functions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Control Functions</em>'.
     * @see hed.homeelectrical.Dwelling#getControlFunctions()
     * @see #getDwelling()
     * @generated
     */
    EReference getDwelling_ControlFunctions();

    /**
     * Returns the meta object for the containment reference '{@link hed.homeelectrical.Dwelling#getServiceConnection <em>Service Connection</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Service Connection</em>'.
     * @see hed.homeelectrical.Dwelling#getServiceConnection()
     * @see #getDwelling()
     * @generated
     */
    EReference getDwelling_ServiceConnection();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.Storey <em>Storey</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Storey</em>'.
     * @see hed.homeelectrical.Storey
     * @generated
     */
    EClass getStorey();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.Storey#getLevelNumber <em>Level Number</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Level Number</em>'.
     * @see hed.homeelectrical.Storey#getLevelNumber()
     * @see #getStorey()
     * @generated
     */
    EAttribute getStorey_LevelNumber();

    /**
     * Returns the meta object for the containment reference list '{@link hed.homeelectrical.Storey#getRooms <em>Rooms</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Rooms</em>'.
     * @see hed.homeelectrical.Storey#getRooms()
     * @see #getStorey()
     * @generated
     */
    EReference getStorey_Rooms();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.Storey#getPlanImageId <em>Plan Image Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Plan Image Id</em>'.
     * @see hed.homeelectrical.Storey#getPlanImageId()
     * @see #getStorey()
     * @generated
     */
    EAttribute getStorey_PlanImageId();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.Storey#getPlanWidth <em>Plan Width</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Plan Width</em>'.
     * @see hed.homeelectrical.Storey#getPlanWidth()
     * @see #getStorey()
     * @generated
     */
    EAttribute getStorey_PlanWidth();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.Storey#getPlanHeight <em>Plan Height</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Plan Height</em>'.
     * @see hed.homeelectrical.Storey#getPlanHeight()
     * @see #getStorey()
     * @generated
     */
    EAttribute getStorey_PlanHeight();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.Storey#getPlanAspectRatio <em>Plan Aspect Ratio</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Plan Aspect Ratio</em>'.
     * @see hed.homeelectrical.Storey#getPlanAspectRatio()
     * @see #getStorey()
     * @generated
     */
    EAttribute getStorey_PlanAspectRatio();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.Room <em>Room</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Room</em>'.
     * @see hed.homeelectrical.Room
     * @generated
     */
    EClass getRoom();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.Room#getUsage <em>Usage</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Usage</em>'.
     * @see hed.homeelectrical.Room#getUsage()
     * @see #getRoom()
     * @generated
     */
    EAttribute getRoom_Usage();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.Catalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Catalog</em>'.
     * @see hed.homeelectrical.Catalog
     * @generated
     */
    EClass getCatalog();

    /**
     * Returns the meta object for the containment reference list '{@link hed.homeelectrical.Catalog#getDeviceTypes <em>Device Types</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Device Types</em>'.
     * @see hed.homeelectrical.Catalog#getDeviceTypes()
     * @see #getCatalog()
     * @generated
     */
    EReference getCatalog_DeviceTypes();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.DeviceType <em>Device Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Device Type</em>'.
     * @see hed.homeelectrical.DeviceType
     * @generated
     */
    EClass getDeviceType();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.DeviceType#getKind <em>Kind</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Kind</em>'.
     * @see hed.homeelectrical.DeviceType#getKind()
     * @see #getDeviceType()
     * @generated
     */
    EAttribute getDeviceType_Kind();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.DeviceType#getManufacturer <em>Manufacturer</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Manufacturer</em>'.
     * @see hed.homeelectrical.DeviceType#getManufacturer()
     * @see #getDeviceType()
     * @generated
     */
    EAttribute getDeviceType_Manufacturer();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.DeviceType#getProductReference <em>Product Reference</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Product Reference</em>'.
     * @see hed.homeelectrical.DeviceType#getProductReference()
     * @see #getDeviceType()
     * @generated
     */
    EAttribute getDeviceType_ProductReference();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.DeviceType#getChannelCount <em>Channel Count</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Channel Count</em>'.
     * @see hed.homeelectrical.DeviceType#getChannelCount()
     * @see #getDeviceType()
     * @generated
     */
    EAttribute getDeviceType_ChannelCount();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.Equipment <em>Equipment</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Equipment</em>'.
     * @see hed.homeelectrical.Equipment
     * @generated
     */
    EClass getEquipment();

    /**
     * Returns the meta object for the reference '{@link hed.homeelectrical.Equipment#getDeviceType <em>Device Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Device Type</em>'.
     * @see hed.homeelectrical.Equipment#getDeviceType()
     * @see #getEquipment()
     * @generated
     */
    EReference getEquipment_DeviceType();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.FieldEquipment <em>Field Equipment</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Field Equipment</em>'.
     * @see hed.homeelectrical.FieldEquipment
     * @generated
     */
    EClass getFieldEquipment();

    /**
     * Returns the meta object for the reference '{@link hed.homeelectrical.FieldEquipment#getRoom <em>Room</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Room</em>'.
     * @see hed.homeelectrical.FieldEquipment#getRoom()
     * @see #getFieldEquipment()
     * @generated
     */
    EReference getFieldEquipment_Room();

    /**
     * Returns the meta object for the reference '{@link hed.homeelectrical.FieldEquipment#getCircuit <em>Circuit</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Circuit</em>'.
     * @see hed.homeelectrical.FieldEquipment#getCircuit()
     * @see #getFieldEquipment()
     * @generated
     */
    EReference getFieldEquipment_Circuit();

    /**
     * Returns the meta object for the reference '{@link hed.homeelectrical.FieldEquipment#getBranch <em>Branch</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Branch</em>'.
     * @see hed.homeelectrical.FieldEquipment#getBranch()
     * @see #getFieldEquipment()
     * @generated
     */
    EReference getFieldEquipment_Branch();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.Outlet <em>Outlet</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Outlet</em>'.
     * @see hed.homeelectrical.Outlet
     * @generated
     */
    EClass getOutlet();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.ElectricalLoad <em>Electrical Load</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Electrical Load</em>'.
     * @see hed.homeelectrical.ElectricalLoad
     * @generated
     */
    EClass getElectricalLoad();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.ControlDevice <em>Control Device</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Control Device</em>'.
     * @see hed.homeelectrical.ControlDevice
     * @generated
     */
    EClass getControlDevice();

    /**
     * Returns the meta object for the containment reference list '{@link hed.homeelectrical.ControlDevice#getChannels <em>Channels</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Channels</em>'.
     * @see hed.homeelectrical.ControlDevice#getChannels()
     * @see #getControlDevice()
     * @generated
     */
    EReference getControlDevice_Channels();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.ControlChannel <em>Control Channel</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Control Channel</em>'.
     * @see hed.homeelectrical.ControlChannel
     * @generated
     */
    EClass getControlChannel();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.DistributionBoard <em>Distribution Board</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Distribution Board</em>'.
     * @see hed.homeelectrical.DistributionBoard
     * @generated
     */
    EClass getDistributionBoard();

    /**
     * Returns the meta object for the reference '{@link hed.homeelectrical.DistributionBoard#getRoom <em>Room</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Room</em>'.
     * @see hed.homeelectrical.DistributionBoard#getRoom()
     * @see #getDistributionBoard()
     * @generated
     */
    EReference getDistributionBoard_Room();

    /**
     * Returns the meta object for the containment reference list '{@link hed.homeelectrical.DistributionBoard#getRails <em>Rails</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Rails</em>'.
     * @see hed.homeelectrical.DistributionBoard#getRails()
     * @see #getDistributionBoard()
     * @generated
     */
    EReference getDistributionBoard_Rails();

    /**
     * Returns the meta object for the containment reference list '{@link hed.homeelectrical.DistributionBoard#getDevices <em>Devices</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Devices</em>'.
     * @see hed.homeelectrical.DistributionBoard#getDevices()
     * @see #getDistributionBoard()
     * @generated
     */
    EReference getDistributionBoard_Devices();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.DINRail <em>DIN Rail</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>DIN Rail</em>'.
     * @see hed.homeelectrical.DINRail
     * @generated
     */
    EClass getDINRail();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.DINRail#getCapacityModules <em>Capacity Modules</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Capacity Modules</em>'.
     * @see hed.homeelectrical.DINRail#getCapacityModules()
     * @see #getDINRail()
     * @generated
     */
    EAttribute getDINRail_CapacityModules();

    /**
     * Returns the meta object for the reference list '{@link hed.homeelectrical.DINRail#getDevices <em>Devices</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Devices</em>'.
     * @see hed.homeelectrical.DINRail#getDevices()
     * @see #getDINRail()
     * @generated
     */
    EReference getDINRail_Devices();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.ModularDevice <em>Modular Device</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Modular Device</em>'.
     * @see hed.homeelectrical.ModularDevice
     * @generated
     */
    EClass getModularDevice();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.ModularDevice#getLabelText <em>Label Text</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Label Text</em>'.
     * @see hed.homeelectrical.ModularDevice#getLabelText()
     * @see #getModularDevice()
     * @generated
     */
    EAttribute getModularDevice_LabelText();

    /**
     * Returns the meta object for the reference '{@link hed.homeelectrical.ModularDevice#getRail <em>Rail</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Rail</em>'.
     * @see hed.homeelectrical.ModularDevice#getRail()
     * @see #getModularDevice()
     * @generated
     */
    EReference getModularDevice_Rail();

    /**
     * Returns the meta object for the reference '{@link hed.homeelectrical.ModularDevice#getUpstreamRccb <em>Upstream Rccb</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Upstream Rccb</em>'.
     * @see hed.homeelectrical.ModularDevice#getUpstreamRccb()
     * @see #getModularDevice()
     * @generated
     */
    EReference getModularDevice_UpstreamRccb();

    /**
     * Returns the meta object for the reference '{@link hed.homeelectrical.ModularDevice#getUpstreamServiceBreaker <em>Upstream Service Breaker</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Upstream Service Breaker</em>'.
     * @see hed.homeelectrical.ModularDevice#getUpstreamServiceBreaker()
     * @see #getModularDevice()
     * @generated
     */
    EReference getModularDevice_UpstreamServiceBreaker();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.Circuit <em>Circuit</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Circuit</em>'.
     * @see hed.homeelectrical.Circuit
     * @generated
     */
    EClass getCircuit();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.Circuit#getPhase <em>Phase</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Phase</em>'.
     * @see hed.homeelectrical.Circuit#getPhase()
     * @see #getCircuit()
     * @generated
     */
    EAttribute getCircuit_Phase();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.Circuit#getConductorSectionMm2 <em>Conductor Section Mm2</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Conductor Section Mm2</em>'.
     * @see hed.homeelectrical.Circuit#getConductorSectionMm2()
     * @see #getCircuit()
     * @generated
     */
    EAttribute getCircuit_ConductorSectionMm2();

    /**
     * Returns the meta object for the reference '{@link hed.homeelectrical.Circuit#getSourceBoard <em>Source Board</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Source Board</em>'.
     * @see hed.homeelectrical.Circuit#getSourceBoard()
     * @see #getCircuit()
     * @generated
     */
    EReference getCircuit_SourceBoard();

    /**
     * Returns the meta object for the reference '{@link hed.homeelectrical.Circuit#getProtectiveDevice <em>Protective Device</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Protective Device</em>'.
     * @see hed.homeelectrical.Circuit#getProtectiveDevice()
     * @see #getCircuit()
     * @generated
     */
    EReference getCircuit_ProtectiveDevice();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.FinalCircuit <em>Final Circuit</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Final Circuit</em>'.
     * @see hed.homeelectrical.FinalCircuit
     * @generated
     */
    EClass getFinalCircuit();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.FinalCircuit#getUsage <em>Usage</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Usage</em>'.
     * @see hed.homeelectrical.FinalCircuit#getUsage()
     * @see #getFinalCircuit()
     * @generated
     */
    EAttribute getFinalCircuit_Usage();

    /**
     * Returns the meta object for the containment reference list '{@link hed.homeelectrical.FinalCircuit#getBranches <em>Branches</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Branches</em>'.
     * @see hed.homeelectrical.FinalCircuit#getBranches()
     * @see #getFinalCircuit()
     * @generated
     */
    EReference getFinalCircuit_Branches();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.DistributionCircuit <em>Distribution Circuit</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Distribution Circuit</em>'.
     * @see hed.homeelectrical.DistributionCircuit
     * @generated
     */
    EClass getDistributionCircuit();

    /**
     * Returns the meta object for the reference '{@link hed.homeelectrical.DistributionCircuit#getTargetBoard <em>Target Board</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Target Board</em>'.
     * @see hed.homeelectrical.DistributionCircuit#getTargetBoard()
     * @see #getDistributionCircuit()
     * @generated
     */
    EReference getDistributionCircuit_TargetBoard();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.ControlFunction <em>Control Function</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Control Function</em>'.
     * @see hed.homeelectrical.ControlFunction
     * @generated
     */
    EClass getControlFunction();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.ControlFunction#getMode <em>Mode</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Mode</em>'.
     * @see hed.homeelectrical.ControlFunction#getMode()
     * @see #getControlFunction()
     * @generated
     */
    EAttribute getControlFunction_Mode();

    /**
     * Returns the meta object for the reference list '{@link hed.homeelectrical.ControlFunction#getChannels <em>Channels</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Channels</em>'.
     * @see hed.homeelectrical.ControlFunction#getChannels()
     * @see #getControlFunction()
     * @generated
     */
    EReference getControlFunction_Channels();

    /**
     * Returns the meta object for the reference list '{@link hed.homeelectrical.ControlFunction#getTargets <em>Targets</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Targets</em>'.
     * @see hed.homeelectrical.ControlFunction#getTargets()
     * @see #getControlFunction()
     * @generated
     */
    EReference getControlFunction_Targets();

    /**
     * Returns the meta object for the reference '{@link hed.homeelectrical.ControlFunction#getActuator <em>Actuator</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Actuator</em>'.
     * @see hed.homeelectrical.ControlFunction#getActuator()
     * @see #getControlFunction()
     * @generated
     */
    EReference getControlFunction_Actuator();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.ServiceConnection <em>Service Connection</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Service Connection</em>'.
     * @see hed.homeelectrical.ServiceConnection
     * @generated
     */
    EClass getServiceConnection();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.ServiceConnection#getPhase <em>Phase</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Phase</em>'.
     * @see hed.homeelectrical.ServiceConnection#getPhase()
     * @see #getServiceConnection()
     * @generated
     */
    EAttribute getServiceConnection_Phase();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.ServiceConnection#getSubscribedPowerKva <em>Subscribed Power Kva</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Subscribed Power Kva</em>'.
     * @see hed.homeelectrical.ServiceConnection#getSubscribedPowerKva()
     * @see #getServiceConnection()
     * @generated
     */
    EAttribute getServiceConnection_SubscribedPowerKva();

    /**
     * Returns the meta object for the containment reference '{@link hed.homeelectrical.ServiceConnection#getMeter <em>Meter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Meter</em>'.
     * @see hed.homeelectrical.ServiceConnection#getMeter()
     * @see #getServiceConnection()
     * @generated
     */
    EReference getServiceConnection_Meter();

    /**
     * Returns the meta object for the containment reference '{@link hed.homeelectrical.ServiceConnection#getMainBreaker <em>Main Breaker</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Main Breaker</em>'.
     * @see hed.homeelectrical.ServiceConnection#getMainBreaker()
     * @see #getServiceConnection()
     * @generated
     */
    EReference getServiceConnection_MainBreaker();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.EnergyMeter <em>Energy Meter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Energy Meter</em>'.
     * @see hed.homeelectrical.EnergyMeter
     * @generated
     */
    EClass getEnergyMeter();

    /**
     * Returns the meta object for the attribute '{@link hed.homeelectrical.EnergyMeter#getSerialNumber <em>Serial Number</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Serial Number</em>'.
     * @see hed.homeelectrical.EnergyMeter#getSerialNumber()
     * @see #getEnergyMeter()
     * @generated
     */
    EAttribute getEnergyMeter_SerialNumber();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.ServiceCircuitBreaker <em>Service Circuit Breaker</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Service Circuit Breaker</em>'.
     * @see hed.homeelectrical.ServiceCircuitBreaker
     * @generated
     */
    EClass getServiceCircuitBreaker();

    /**
     * Returns the meta object for the reference list '{@link hed.homeelectrical.ServiceCircuitBreaker#getDownstreamDevices <em>Downstream Devices</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Downstream Devices</em>'.
     * @see hed.homeelectrical.ServiceCircuitBreaker#getDownstreamDevices()
     * @see #getServiceCircuitBreaker()
     * @generated
     */
    EReference getServiceCircuitBreaker_DownstreamDevices();

    /**
     * Returns the meta object for class '{@link hed.homeelectrical.CircuitBranch <em>Circuit Branch</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Circuit Branch</em>'.
     * @see hed.homeelectrical.CircuitBranch
     * @generated
     */
    EClass getCircuitBranch();

    /**
     * Returns the meta object for the container reference '{@link hed.homeelectrical.CircuitBranch#getCircuit <em>Circuit</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Circuit</em>'.
     * @see hed.homeelectrical.CircuitBranch#getCircuit()
     * @see #getCircuitBranch()
     * @generated
     */
    EReference getCircuitBranch_Circuit();

    /**
     * Returns the meta object for the reference list '{@link hed.homeelectrical.CircuitBranch#getEquipment <em>Equipment</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Equipment</em>'.
     * @see hed.homeelectrical.CircuitBranch#getEquipment()
     * @see #getCircuitBranch()
     * @generated
     */
    EReference getCircuitBranch_Equipment();

    /**
     * Returns the meta object for enum '{@link hed.homeelectrical.DeviceKind <em>Device Kind</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Device Kind</em>'.
     * @see hed.homeelectrical.DeviceKind
     * @generated
     */
    EEnum getDeviceKind();

    /**
     * Returns the meta object for enum '{@link hed.homeelectrical.SupplyPhase <em>Supply Phase</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Supply Phase</em>'.
     * @see hed.homeelectrical.SupplyPhase
     * @generated
     */
    EEnum getSupplyPhase();

    /**
     * Returns the meta object for enum '{@link hed.homeelectrical.CircuitPhase <em>Circuit Phase</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Circuit Phase</em>'.
     * @see hed.homeelectrical.CircuitPhase
     * @generated
     */
    EEnum getCircuitPhase();

    /**
     * Returns the meta object for enum '{@link hed.homeelectrical.CircuitUse <em>Circuit Use</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Circuit Use</em>'.
     * @see hed.homeelectrical.CircuitUse
     * @generated
     */
    EEnum getCircuitUse();

    /**
     * Returns the meta object for enum '{@link hed.homeelectrical.ControlMode <em>Control Mode</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Control Mode</em>'.
     * @see hed.homeelectrical.ControlMode
     * @generated
     */
    EEnum getControlMode();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    HomeElectricalFactory getHomeElectricalFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.NamedElementImpl <em>Named Element</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.NamedElementImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getNamedElement()
         * @generated
         */
        EClass NAMED_ELEMENT = eINSTANCE.getNamedElement();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute NAMED_ELEMENT__NAME = eINSTANCE.getNamedElement_Name();

        /**
         * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute NAMED_ELEMENT__DESCRIPTION = eINSTANCE.getNamedElement_Description();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.TechnicalElementImpl <em>Technical Element</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.TechnicalElementImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getTechnicalElement()
         * @generated
         */
        EClass TECHNICAL_ELEMENT = eINSTANCE.getTechnicalElement();

        /**
         * The meta object literal for the '<em><b>Rated Current A</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TECHNICAL_ELEMENT__RATED_CURRENT_A = eINSTANCE.getTechnicalElement_RatedCurrentA();

        /**
         * The meta object literal for the '<em><b>Rated Power W</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TECHNICAL_ELEMENT__RATED_POWER_W = eINSTANCE.getTechnicalElement_RatedPowerW();

        /**
         * The meta object literal for the '<em><b>Width Modules</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TECHNICAL_ELEMENT__WIDTH_MODULES = eINSTANCE.getTechnicalElement_WidthModules();

        /**
         * The meta object literal for the '<em><b>Residual Current Ma</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TECHNICAL_ELEMENT__RESIDUAL_CURRENT_MA = eINSTANCE.getTechnicalElement_ResidualCurrentMa();

        /**
         * The meta object literal for the '<em><b>Residual Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TECHNICAL_ELEMENT__RESIDUAL_TYPE = eINSTANCE.getTechnicalElement_ResidualType();

        /**
         * The meta object literal for the '<em><b>Trip Curve</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TECHNICAL_ELEMENT__TRIP_CURVE = eINSTANCE.getTechnicalElement_TripCurve();

        /**
         * The meta object literal for the '<em><b>Pole Configuration</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TECHNICAL_ELEMENT__POLE_CONFIGURATION = eINSTANCE.getTechnicalElement_PoleConfiguration();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.DwellingImpl <em>Dwelling</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.DwellingImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getDwelling()
         * @generated
         */
        EClass DWELLING = eINSTANCE.getDwelling();

        /**
         * The meta object literal for the '<em><b>Catalog</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DWELLING__CATALOG = eINSTANCE.getDwelling_Catalog();

        /**
         * The meta object literal for the '<em><b>Storeys</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DWELLING__STOREYS = eINSTANCE.getDwelling_Storeys();

        /**
         * The meta object literal for the '<em><b>Equipment</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DWELLING__EQUIPMENT = eINSTANCE.getDwelling_Equipment();

        /**
         * The meta object literal for the '<em><b>Boards</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DWELLING__BOARDS = eINSTANCE.getDwelling_Boards();

        /**
         * The meta object literal for the '<em><b>Circuits</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DWELLING__CIRCUITS = eINSTANCE.getDwelling_Circuits();

        /**
         * The meta object literal for the '<em><b>Control Functions</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DWELLING__CONTROL_FUNCTIONS = eINSTANCE.getDwelling_ControlFunctions();

        /**
         * The meta object literal for the '<em><b>Service Connection</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DWELLING__SERVICE_CONNECTION = eINSTANCE.getDwelling_ServiceConnection();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.StoreyImpl <em>Storey</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.StoreyImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getStorey()
         * @generated
         */
        EClass STOREY = eINSTANCE.getStorey();

        /**
         * The meta object literal for the '<em><b>Level Number</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STOREY__LEVEL_NUMBER = eINSTANCE.getStorey_LevelNumber();

        /**
         * The meta object literal for the '<em><b>Rooms</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference STOREY__ROOMS = eINSTANCE.getStorey_Rooms();

        /**
         * The meta object literal for the '<em><b>Plan Image Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STOREY__PLAN_IMAGE_ID = eINSTANCE.getStorey_PlanImageId();

        /**
         * The meta object literal for the '<em><b>Plan Width</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STOREY__PLAN_WIDTH = eINSTANCE.getStorey_PlanWidth();

        /**
         * The meta object literal for the '<em><b>Plan Height</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STOREY__PLAN_HEIGHT = eINSTANCE.getStorey_PlanHeight();

        /**
         * The meta object literal for the '<em><b>Plan Aspect Ratio</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STOREY__PLAN_ASPECT_RATIO = eINSTANCE.getStorey_PlanAspectRatio();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.RoomImpl <em>Room</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.RoomImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getRoom()
         * @generated
         */
        EClass ROOM = eINSTANCE.getRoom();

        /**
         * The meta object literal for the '<em><b>Usage</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ROOM__USAGE = eINSTANCE.getRoom_Usage();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.CatalogImpl <em>Catalog</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.CatalogImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getCatalog()
         * @generated
         */
        EClass CATALOG = eINSTANCE.getCatalog();

        /**
         * The meta object literal for the '<em><b>Device Types</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CATALOG__DEVICE_TYPES = eINSTANCE.getCatalog_DeviceTypes();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.DeviceTypeImpl <em>Device Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.DeviceTypeImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getDeviceType()
         * @generated
         */
        EClass DEVICE_TYPE = eINSTANCE.getDeviceType();

        /**
         * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DEVICE_TYPE__KIND = eINSTANCE.getDeviceType_Kind();

        /**
         * The meta object literal for the '<em><b>Manufacturer</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DEVICE_TYPE__MANUFACTURER = eINSTANCE.getDeviceType_Manufacturer();

        /**
         * The meta object literal for the '<em><b>Product Reference</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DEVICE_TYPE__PRODUCT_REFERENCE = eINSTANCE.getDeviceType_ProductReference();

        /**
         * The meta object literal for the '<em><b>Channel Count</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DEVICE_TYPE__CHANNEL_COUNT = eINSTANCE.getDeviceType_ChannelCount();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.EquipmentImpl <em>Equipment</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.EquipmentImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getEquipment()
         * @generated
         */
        EClass EQUIPMENT = eINSTANCE.getEquipment();

        /**
         * The meta object literal for the '<em><b>Device Type</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference EQUIPMENT__DEVICE_TYPE = eINSTANCE.getEquipment_DeviceType();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.FieldEquipmentImpl <em>Field Equipment</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.FieldEquipmentImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getFieldEquipment()
         * @generated
         */
        EClass FIELD_EQUIPMENT = eINSTANCE.getFieldEquipment();

        /**
         * The meta object literal for the '<em><b>Room</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference FIELD_EQUIPMENT__ROOM = eINSTANCE.getFieldEquipment_Room();

        /**
         * The meta object literal for the '<em><b>Circuit</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference FIELD_EQUIPMENT__CIRCUIT = eINSTANCE.getFieldEquipment_Circuit();

        /**
         * The meta object literal for the '<em><b>Branch</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference FIELD_EQUIPMENT__BRANCH = eINSTANCE.getFieldEquipment_Branch();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.OutletImpl <em>Outlet</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.OutletImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getOutlet()
         * @generated
         */
        EClass OUTLET = eINSTANCE.getOutlet();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.ElectricalLoadImpl <em>Electrical Load</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.ElectricalLoadImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getElectricalLoad()
         * @generated
         */
        EClass ELECTRICAL_LOAD = eINSTANCE.getElectricalLoad();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.ControlDeviceImpl <em>Control Device</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.ControlDeviceImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getControlDevice()
         * @generated
         */
        EClass CONTROL_DEVICE = eINSTANCE.getControlDevice();

        /**
         * The meta object literal for the '<em><b>Channels</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CONTROL_DEVICE__CHANNELS = eINSTANCE.getControlDevice_Channels();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.ControlChannelImpl <em>Control Channel</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.ControlChannelImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getControlChannel()
         * @generated
         */
        EClass CONTROL_CHANNEL = eINSTANCE.getControlChannel();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.DistributionBoardImpl <em>Distribution Board</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.DistributionBoardImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getDistributionBoard()
         * @generated
         */
        EClass DISTRIBUTION_BOARD = eINSTANCE.getDistributionBoard();

        /**
         * The meta object literal for the '<em><b>Room</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DISTRIBUTION_BOARD__ROOM = eINSTANCE.getDistributionBoard_Room();

        /**
         * The meta object literal for the '<em><b>Rails</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DISTRIBUTION_BOARD__RAILS = eINSTANCE.getDistributionBoard_Rails();

        /**
         * The meta object literal for the '<em><b>Devices</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DISTRIBUTION_BOARD__DEVICES = eINSTANCE.getDistributionBoard_Devices();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.DINRailImpl <em>DIN Rail</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.DINRailImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getDINRail()
         * @generated
         */
        EClass DIN_RAIL = eINSTANCE.getDINRail();

        /**
         * The meta object literal for the '<em><b>Capacity Modules</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DIN_RAIL__CAPACITY_MODULES = eINSTANCE.getDINRail_CapacityModules();

        /**
         * The meta object literal for the '<em><b>Devices</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DIN_RAIL__DEVICES = eINSTANCE.getDINRail_Devices();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.ModularDeviceImpl <em>Modular Device</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.ModularDeviceImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getModularDevice()
         * @generated
         */
        EClass MODULAR_DEVICE = eINSTANCE.getModularDevice();

        /**
         * The meta object literal for the '<em><b>Label Text</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODULAR_DEVICE__LABEL_TEXT = eINSTANCE.getModularDevice_LabelText();

        /**
         * The meta object literal for the '<em><b>Rail</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MODULAR_DEVICE__RAIL = eINSTANCE.getModularDevice_Rail();

        /**
         * The meta object literal for the '<em><b>Upstream Rccb</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MODULAR_DEVICE__UPSTREAM_RCCB = eINSTANCE.getModularDevice_UpstreamRccb();

        /**
         * The meta object literal for the '<em><b>Upstream Service Breaker</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MODULAR_DEVICE__UPSTREAM_SERVICE_BREAKER = eINSTANCE.getModularDevice_UpstreamServiceBreaker();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.CircuitImpl <em>Circuit</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.CircuitImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getCircuit()
         * @generated
         */
        EClass CIRCUIT = eINSTANCE.getCircuit();

        /**
         * The meta object literal for the '<em><b>Phase</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CIRCUIT__PHASE = eINSTANCE.getCircuit_Phase();

        /**
         * The meta object literal for the '<em><b>Conductor Section Mm2</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CIRCUIT__CONDUCTOR_SECTION_MM2 = eINSTANCE.getCircuit_ConductorSectionMm2();

        /**
         * The meta object literal for the '<em><b>Source Board</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CIRCUIT__SOURCE_BOARD = eINSTANCE.getCircuit_SourceBoard();

        /**
         * The meta object literal for the '<em><b>Protective Device</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CIRCUIT__PROTECTIVE_DEVICE = eINSTANCE.getCircuit_ProtectiveDevice();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.FinalCircuitImpl <em>Final Circuit</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.FinalCircuitImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getFinalCircuit()
         * @generated
         */
        EClass FINAL_CIRCUIT = eINSTANCE.getFinalCircuit();

        /**
         * The meta object literal for the '<em><b>Usage</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute FINAL_CIRCUIT__USAGE = eINSTANCE.getFinalCircuit_Usage();

        /**
         * The meta object literal for the '<em><b>Branches</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference FINAL_CIRCUIT__BRANCHES = eINSTANCE.getFinalCircuit_Branches();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.DistributionCircuitImpl <em>Distribution Circuit</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.DistributionCircuitImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getDistributionCircuit()
         * @generated
         */
        EClass DISTRIBUTION_CIRCUIT = eINSTANCE.getDistributionCircuit();

        /**
         * The meta object literal for the '<em><b>Target Board</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DISTRIBUTION_CIRCUIT__TARGET_BOARD = eINSTANCE.getDistributionCircuit_TargetBoard();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.ControlFunctionImpl <em>Control Function</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.ControlFunctionImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getControlFunction()
         * @generated
         */
        EClass CONTROL_FUNCTION = eINSTANCE.getControlFunction();

        /**
         * The meta object literal for the '<em><b>Mode</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONTROL_FUNCTION__MODE = eINSTANCE.getControlFunction_Mode();

        /**
         * The meta object literal for the '<em><b>Channels</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CONTROL_FUNCTION__CHANNELS = eINSTANCE.getControlFunction_Channels();

        /**
         * The meta object literal for the '<em><b>Targets</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CONTROL_FUNCTION__TARGETS = eINSTANCE.getControlFunction_Targets();

        /**
         * The meta object literal for the '<em><b>Actuator</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CONTROL_FUNCTION__ACTUATOR = eINSTANCE.getControlFunction_Actuator();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.ServiceConnectionImpl <em>Service Connection</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.ServiceConnectionImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getServiceConnection()
         * @generated
         */
        EClass SERVICE_CONNECTION = eINSTANCE.getServiceConnection();

        /**
         * The meta object literal for the '<em><b>Phase</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute SERVICE_CONNECTION__PHASE = eINSTANCE.getServiceConnection_Phase();

        /**
         * The meta object literal for the '<em><b>Subscribed Power Kva</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute SERVICE_CONNECTION__SUBSCRIBED_POWER_KVA = eINSTANCE.getServiceConnection_SubscribedPowerKva();

        /**
         * The meta object literal for the '<em><b>Meter</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference SERVICE_CONNECTION__METER = eINSTANCE.getServiceConnection_Meter();

        /**
         * The meta object literal for the '<em><b>Main Breaker</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference SERVICE_CONNECTION__MAIN_BREAKER = eINSTANCE.getServiceConnection_MainBreaker();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.EnergyMeterImpl <em>Energy Meter</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.EnergyMeterImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getEnergyMeter()
         * @generated
         */
        EClass ENERGY_METER = eINSTANCE.getEnergyMeter();

        /**
         * The meta object literal for the '<em><b>Serial Number</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ENERGY_METER__SERIAL_NUMBER = eINSTANCE.getEnergyMeter_SerialNumber();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.ServiceCircuitBreakerImpl <em>Service Circuit Breaker</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.ServiceCircuitBreakerImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getServiceCircuitBreaker()
         * @generated
         */
        EClass SERVICE_CIRCUIT_BREAKER = eINSTANCE.getServiceCircuitBreaker();

        /**
         * The meta object literal for the '<em><b>Downstream Devices</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference SERVICE_CIRCUIT_BREAKER__DOWNSTREAM_DEVICES = eINSTANCE.getServiceCircuitBreaker_DownstreamDevices();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.impl.CircuitBranchImpl <em>Circuit Branch</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.impl.CircuitBranchImpl
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getCircuitBranch()
         * @generated
         */
        EClass CIRCUIT_BRANCH = eINSTANCE.getCircuitBranch();

        /**
         * The meta object literal for the '<em><b>Circuit</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CIRCUIT_BRANCH__CIRCUIT = eINSTANCE.getCircuitBranch_Circuit();

        /**
         * The meta object literal for the '<em><b>Equipment</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CIRCUIT_BRANCH__EQUIPMENT = eINSTANCE.getCircuitBranch_Equipment();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.DeviceKind <em>Device Kind</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.DeviceKind
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getDeviceKind()
         * @generated
         */
        EEnum DEVICE_KIND = eINSTANCE.getDeviceKind();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.SupplyPhase <em>Supply Phase</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.SupplyPhase
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getSupplyPhase()
         * @generated
         */
        EEnum SUPPLY_PHASE = eINSTANCE.getSupplyPhase();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.CircuitPhase <em>Circuit Phase</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.CircuitPhase
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getCircuitPhase()
         * @generated
         */
        EEnum CIRCUIT_PHASE = eINSTANCE.getCircuitPhase();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.CircuitUse <em>Circuit Use</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.CircuitUse
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getCircuitUse()
         * @generated
         */
        EEnum CIRCUIT_USE = eINSTANCE.getCircuitUse();

        /**
         * The meta object literal for the '{@link hed.homeelectrical.ControlMode <em>Control Mode</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see hed.homeelectrical.ControlMode
         * @see hed.homeelectrical.impl.HomeElectricalPackageImpl#getControlMode()
         * @generated
         */
        EEnum CONTROL_MODE = eINSTANCE.getControlMode();

    }

} //HomeElectricalPackage
