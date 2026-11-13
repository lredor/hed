<!--
Copyright (c) 2026 Laurent Redor.
This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0
-->

# Reordering model elements

## Catalog, dwelling and functional controls

**Move up / Move down** and drag-and-drop ordering are available for catalog types within each category, storeys within the dwelling, rooms within a storey, terminal equipment within a room and functional controls. Unavailable directions are hidden at the ends of each visible list.

Drop before a peer to insert the selection there, or onto its current parent to append it. Selections retain their original model order and are validated before any mutation. A drop onto a different room still moves terminal equipment to that room; peer ordering does not reparent objects.

Catalog categories and room-equipment groups are projections of shared semantic lists. Reordering one group preserves the slots and relative order of the other groups. Equipment identity, circuit/branch/control associations and floor-plan positions are retained. Storey order is independent of its level number. Reopening restores the saved list order; existing diagram coordinates are not recalculated.

`ExplorerElementOrderTest` covers boundaries, subgroup isolation, unchanged connections and atomic rejection. `ExplorerListsDropTest` checks native gap destinations and append-to-parent drops, including virtual catalog/control groups. The bilingual `explorer-lists-ordering.spec.ts` exercises all six visible groups through commands and actual drag-and-drop, then reloads the project.

## Circuits

The context menu offers **Move up / Move down** on final and distribution circuits in the GTL's **Circuits** group. French labels are **Déplacer vers le haut / Déplacer vers le bas**. Each action moves the circuit by one position in the dwelling's ordered circuit list. Unavailable directions are hidden at the ends of the list.

Drag a circuit onto another circuit, or into the gap immediately above it, to insert it **before** that peer. Drop onto the **Circuits** group to append it at the end. Several selected circuits move together, retaining their existing relative model order. All selected circuits and the destination must belong to the same dwelling; mixed, foreign and stale selections are rejected before changing the list. Dropping onto a selected circuit leaves the order unchanged.

These operations retain circuit identity, protection, source board, branches and equipment assignments. The order is saved in the model and restored after reopening. It controls the explorer list; it does not reposition existing Circuit Diagram nodes or override a table's selected sort order. No metamodel change is required.

`CircuitOrderingTest` covers both circuit kinds, boundaries, native gap and virtual-group destinations, whole-selection rejection, localization and a Sirius JSON round trip retaining branches and assignments. The bilingual `circuit-ordering.spec.ts` checks context actions, actual gap drops in both directions, append-to-group drops, unchanged identity/assignments and persistence.

## Rails and modular devices

Model Explorer's context menu offers **Move up / Move down** on DIN rails and **Move left / Move right** on mounted modular devices. French labels are **Déplacer vers le haut / Déplacer vers le bas** and **Déplacer à gauche / Déplacer à droite**.

A command moves the selected element by one position within its board's rails or its current rail's devices. Only possible directions are offered: a first element cannot move backward and a last element cannot move forward. These commands preserve the selected object's identity and electrical assignments. They change the model's ordered references, so the Distribution Board Table uses the new order too.

Moving between rails uses the existing rail assignment in Properties. Diagram drag-and-drop is introduced with the panel representation in a separate increment.

The domain operation synchronizes the board's device containment order with rail order for native Sirius Web JSON persistence. Reopening a project restores both rail and device order. No metamodel change is required.

The starter integration test verifies boundary conditions, localized commands, retained circuit/protection assignments and a native JSON round trip. The bilingual `explorer-reordering.spec.ts` browser workflow checks menu availability, both kinds of move and persistence after reopening.
