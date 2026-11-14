/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.domain;

import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.FieldEquipment;
import hed.homeelectrical.Outlet;
import hed.homeelectrical.OutletBlock;
import hed.homeelectrical.OutletRow;
import hed.homeelectrical.Room;
import java.util.List;
import java.util.Locale;
import org.eclipse.emf.ecore.EObject;

/** Creates fixed outlet assemblies whose members share their physical unit and electrical assignment. */
public final class HedOutletBlocks {
    private HedOutletBlocks() {
    }

    /**
     * Finds the physical assembly enclosing an object.
     * @param object a block, row, outlet or unrelated object
     * @return the enclosing block, or null for a standalone object
     */
    public static OutletBlock block(EObject object) {
        for (var current = object; current != null; current = current.eContainer()) {
            if (current instanceof OutletBlock block) {
                return block;
            }
        }
        return null;
    }

    /**
     * Lists actual terminal equipment, including physical blocks and excluding their inherited member assignments.
     * @param home the owning dwelling
     * @return terminal devices in semantic traversal order
     */
    public static List<FieldEquipment> terminals(Dwelling home) {
        return home == null ? List.of()
                : HedDomain.contents(home).stream().filter(FieldEquipment.class::isInstance).map(FieldEquipment.class::cast).filter(device -> !(device.eContainer() instanceof OutletRow)).toList();
    }

    /**
     * Lists the outlets in row-major order without changing their circuit references.
     * @param block the physical assembly
     * @return its individual outlets
     */
    public static List<Outlet> outlets(OutletBlock block) {
        return block.getRows().stream().flatMap(row -> row.getOutlets().stream()).toList();
    }

    /**
     * Validates dimensions before allocation; the bounded grid avoids accidental unbounded creation.
     * @param rows number of rows
     * @param columns number of outlets per row
     * @throws IllegalArgumentException if dimensions are nonpositive or the assembly exceeds 256 outlets
     */
    public static void requireDimensions(int rows, int columns) {
        if (rows < 1 || columns < 1 || (long) rows * columns > 256) {
            throw new IllegalArgumentException("block.invalidDimensions");
        }
    }

    /**
     * Creates a catalog-shaped block with outlets sharing the block's circuit of the catalog's standalone outlet type.
     * @param home the dwelling owning the type and resulting block
     * @param type an outlet-block catalog entry
     * @param locale selects default row and outlet names
     * @return the new block attached to the dwelling
     */
    public static OutletBlock create(Dwelling home, DeviceType type, Locale locale) {
        requireDimensions(type.getNbRows(), type.getNbColumns());
        var outletType = home.getCatalog().getDeviceTypes().stream().filter(candidate -> candidate.getKind() == DeviceKind.OUTLET).findFirst().orElseGet(() -> {
            // A catalog may have removed its unused standalone type before its first block is created.
            var standalone = HedDomain.named(HedDomain.F.createDeviceType(), Messages.text(locale, "kind.OUTLET"));
            standalone.setKind(DeviceKind.OUTLET);
            home.getCatalog().getDeviceTypes().add(standalone);
            return standalone;
        });
        var block = HedDomain.F.createOutletBlock();
        HedDomain.initialize(block, type, locale);
        for (int rowIndex = 0; rowIndex < type.getNbRows(); rowIndex++) {
            var row = HedDomain.named(HedDomain.F.createOutletRow(), Messages.text(locale, "block.rowName", rowIndex + 1));
            block.getRows().add(row);
            for (int column = 0; column < type.getNbColumns(); column++) {
                var outlet = HedDomain.F.createOutlet();
                HedDomain.initialize(outlet, outletType, locale);
                outlet.setName(Messages.text(locale, "block.outletName", rowIndex + 1, column + 1));
                row.getOutlets().add(outlet);
            }
        }
        home.getEquipment().add(block);
        return block;
    }

    /**
     * Resolves an electrical device to its diagram endpoint.
     * @param device an individual outlet or standalone device
     * @return its block when grouped, otherwise the device itself
     */
    public static FieldEquipment endpoint(FieldEquipment device) {
        var block = block(device);
        return block == null ? device : block;
    }

    /**
     * Changes a physical unit's room and propagates location to its contained outlets.
     * @param device a standalone device or a complete block
     * @param room the target room, or null to unlocate the unit
     * @throws IllegalArgumentException if an individual contained outlet is moved independently
     */
    public static void locate(FieldEquipment device, Room room) {
        if (device.eContainer() instanceof OutletRow) {
            throw new IllegalArgumentException("block.moveWhole");
        }
        device.setRoom(room);
        if (device instanceof OutletBlock block) {
            outlets(block).forEach(outlet -> outlet.setRoom(room));
        }
    }
}
