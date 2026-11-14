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
import hed.homeelectrical.Outlet;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Outlet</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class OutletImpl extends FieldEquipmentImpl implements Outlet {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected OutletImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HomeElectricalPackage.Literals.OUTLET;
    }

    /** Inherits the physical block's circuit; contained outlets have no separate assignment.
     * @generated NOT
     */
    @Override
    public hed.homeelectrical.FinalCircuit getCircuit() {
        var block = hed.domain.HedOutletBlocks.block(this);
        return block == null ? super.getCircuit() : block.getCircuit();
    }

    /** Routes programmatic member assignments through their owning physical unit.
     * @generated NOT
     */
    @Override
    public void setCircuit(hed.homeelectrical.FinalCircuit circuit) {
        var block = hed.domain.HedOutletBlocks.block(this);
        if (block == null) {
            super.setCircuit(circuit);
        } else {
            block.setCircuit(circuit);
        }
    }

    /** Inherits the physical block's optional branch.
     * @generated NOT
     */
    @Override
    public hed.homeelectrical.CircuitBranch getBranch() {
        var block = hed.domain.HedOutletBlocks.block(this);
        return block == null ? super.getBranch() : block.getBranch();
    }

    /** Routes programmatic member assignments through their owning physical unit.
     * @generated NOT
     */
    @Override
    public void setBranch(hed.homeelectrical.CircuitBranch branch) {
        var block = hed.domain.HedOutletBlocks.block(this);
        if (block == null) {
            super.setBranch(branch);
        } else {
            block.setBranch(branch);
        }
    }
} //OutletImpl
