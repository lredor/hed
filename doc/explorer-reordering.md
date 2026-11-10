<!--
Copyright (c) 2026 Laurent Redor.
This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0
-->

# Reordering rails and devices

Model Explorer's context menu offers **Move up / Move down** on DIN rails and **Move left / Move right** on mounted modular devices. French labels are **Déplacer vers le haut / Déplacer vers le bas** and **Déplacer à gauche / Déplacer à droite**.

A command moves the selected element by one position within its board's rails or its current rail's devices. Only possible directions are offered: a first element cannot move backward and a last element cannot move forward. These commands preserve the selected object's identity and electrical assignments. They change the model's ordered references, so the Distribution Board Table uses the new order too.

Moving between rails uses the existing rail assignment in Properties. Diagram drag-and-drop is introduced with the panel representation in a separate increment.

The domain operation synchronizes the board's device containment order with rail order for native Sirius Web JSON persistence. Reopening a project restores both rail and device order. No metamodel change is required.

The starter integration test verifies boundary conditions, localized commands, retained circuit/protection assignments and a native JSON round trip. The bilingual `explorer-reordering.spec.ts` browser workflow checks menu availability, both kinds of move and persistence after reopening.
