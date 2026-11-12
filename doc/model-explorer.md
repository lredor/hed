<!--
Copyright (c) 2026 Laurent Redor.
This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0
-->

# Model Explorer

Select **Electrical installation** in the explorer selector to use the domain tree. Model Explorer provides contextual creation, rename and delete actions, and lists saved representations beneath their model element.

## Hierarchy and creation contexts

The root displays the native Sirius Web project name. The tree organizes the model as follows:

```text
Project
├── Dwelling
│   ├── Storeys
│   │   └── Rooms
│   │       └── Terminal equipment assigned to each room
│   ├── Equipment without a room
│   └── Functional controls
├── Electrical service area (GTL)
│   ├── Service connection
│   │   ├── Energy meter
│   │   └── Service circuit-breaker
│   ├── Distribution boards
│   │   └── DIN rails
│   │       └── Modular devices
│   └── Circuits
│       └── Final circuits / Distribution circuits
│           └── Circuit branches (final circuits)
└── Device catalog
    ├── Distribution board devices
    └── Terminal equipment
```

The project root, GTL and category groups are navigation objects. They do not add semantic classes or duplicate the model. The displayed **Dwelling** label is localized; select it to edit the dwelling's stored name and properties. Inline Rename is disabled for that fixed navigation label. Saved representations remain beneath their real semantic owner: dwelling tables under Dwelling, panel tables/diagrams under their distribution board, and catalog tables under Device catalog.

| Context | Creation actions |
| --- | --- |
| Dwelling | Storeys, functional controls and unlocated terminal equipment |
| Storey / Room | Rooms / terminal equipment assigned to that room |
| GTL | Distribution boards and one service connection |
| Service connection | Energy meter and service circuit-breaker, when the catalog contains a compatible type |
| Distribution board / DIN rail | DIN rails / compatible modular devices within the rail capacity |
| Circuits / Final circuit | Final or distribution circuits / branches |
| Catalog category | A device type initialized in that category |

Use **New object** on a room or rail to choose a compatible catalog type, or a contextual **Create** action to use the default type for a category. A board cannot create an unmounted modular device, and an occupied rail cannot be deleted.

Distribution board catalog types include modular apparatus, energy meters and service circuit-breakers. All other types appear under Terminal equipment. Creating a type from the distribution board category initializes it as MCB; creating one from the terminal category initializes it as Outlet. Editing its **Category** immediately moves the same type to the corresponding navigation group, retaining its name, identity and semantic catalog ownership.

## Creation choices and rail capacity

On a DIN rail, **New object** stays available while at least one compatible catalog type fits in the remaining space. The dialog lists each fitting variant using its own module width. Contextual **Create** actions still use the default type for that category, so a generic action can disappear while a smaller custom variant remains available through **New object**.

For example, a one-module rail with a half-module device can accept another half-module catalog variant, even though a default one-module device cannot fit. When no compatible type fits, **New object** is hidden. Creation requests and catalog drops are checked again on the server, including choices made before the available capacity changed.

Properties only offers valid destination rails. Devices already on a full rail can still be reordered because their total occupied width does not change.

## Create modular devices from the catalog

Drag a modular **Device type** from the catalog onto a **DIN rail** in Model Explorer, or onto a rail in the **Distribution Board Layout**. In the diagram, dropping onto an existing modular device uses its rail. Each accepted type creates a new modular device at the end of the rail, preserving the modular devices already mounted there. Its initial name is the type's current stored name. The type remains in the catalog; repeated drops create distinct modular devices sharing that type's specifications.

Model Explorer accepts one or several selected modular types. It validates the entire selection, including its combined module width, before creating anything. Drop onto the rail itself; a gap between explorer rows is reserved for ordering and cannot create modular devices. Diagram drops use Sirius Web's native per-object tool and check each type before appending its modular device.

Only modular types from the same dwelling are accepted. Terminal equipment, energy meters and service circuit-breakers cannot be mounted by this tool. A full rail, a foreign type, a board or the diagram background is rejected with a translated message. Configure circuit and upstream protection assignments afterward in Properties. Reopening retains modular device identities, their catalog references and rail order.

## Create terminal equipment from the catalog

Drag a type from **Device catalog → Terminal equipment** onto a **Room** in Model Explorer. Each accepted type creates a new outlet, control device or electrical load assigned to that room. The device belongs to the dwelling and references the selected catalog type; the type stays in its catalog. Initial names and technical values come from the type's current stored data, and control channels are initialized in the application language. Repeated drops create distinct devices that can be renamed independently in Properties.

Model Explorer validates the complete selection before creating anything. Only terminal types from the same dwelling are accepted. A modular type, energy meter, service circuit-breaker, non-catalog object, unresolved source or foreign type causes the entire selection to be rejected. Drop directly onto the room: storeys, navigation groups and gaps between rows cannot receive this creation operation.

Created devices are immediately available under the room and can then be dragged onto a Floor Plan of that storey. The plan's own drop operation places an existing device; direct catalog drops onto a floor plan remain rejected. Reopening retains device identities, their room assignments and their type references.

## Move existing equipment between rooms

Drag an existing outlet, control device or load directly onto its destination **Room**. This changes its room reference and moves its explorer entry, preserving the same semantic device, name, catalog type, circuit, branch and functional-control connections. The destination can be on another storey of the same dwelling. Devices under **Unlocated equipment** can also be assigned this way; dropping a device onto its current room does not duplicate it.

Several existing devices can be moved together. The entire selection is validated before changing any room. Mixing devices with catalog types, unresolved sources, devices from another dwelling, detached rooms and drops between tree rows are rejected. Catalog types retain their separate behavior: dropping them onto a room creates new devices.

The room reference determines which storey's Floor Plans can represent the device. Moving between rooms on the same storey preserves its existing plan placement. After a move to another storey, place the device on that storey's plan; no placement is automatically created there.

## Circuit branches and associated devices

Create a **Branch** below a **Final circuit** using its creation menu or **New Object**. Each branch groups devices supplied by that same circuit. Distribution circuits do not have branches.

Branches are optional. On a final circuit without branches, **Associated equipment** in Details is an editable list of its directly supplied devices. Adding the first branch automatically assigns those existing devices to it, preserving their room, identity and supply circuit; subsequent branches start empty. Once at least one branch exists, the circuit's list becomes the read-only union of its branches' equipment. Edit each branch's list to distribute devices between branches. Removing the last branch retains direct supply assignments and makes the circuit's list editable again.

Drag an existing room device onto a branch, or set its **Branch** in Properties, to associate it. The operation retains its room and identity and sets its supply circuit to the branch's circuit. A selection containing anything other than existing terminal devices is rejected before any association changes. Catalog types can still be dropped onto rooms or rails; use the branch's creation menu to create a new device directly on that branch, then assign its room in Properties.

Associated devices remain visible in their rooms and also appear under their branch. These are two explorer occurrences of the same semantic device, with distinct stable tree identifiers. Selecting or renaming either occurrence edits that device. **Delete on either occurrence deletes the semantic device**, including its occurrence in the other part of the explorer. Removing a device from a branch clears its supply circuit. Deleting the branch itself retains its devices: they become unassigned if other branches remain; deleting the last branch keeps direct supply assignments. To reconnect a device to a circuit with branches, select a **Branch** or drag it onto one. The device's supply-circuit selector offers circuits without branches and its current branched circuit only. A stale form cannot bypass this rule. Moving a branch to another final circuit updates its members' supply circuit and groups existing direct equipment when it becomes that circuit's first branch.

The Equipment Table includes a read-only **Branch** column. The Circuit Diagram shows **circuit → branch → device** while the device stays in its room container. The optional placement highlight applies consistently to both explorer occurrences; room, storey and dwelling propagation continues to follow room assignments.

## Optional floor-plan placement highlight

Open the native **Filters** menu (the filter-list icon in the Model Explorer toolbar) and enable **Highlight equipments not in floor plans**. In French the checkbox is **Surligner les équipements absents des plans**. It is unchecked by default; reopening the page starts with the normal labels. The filter changes label colors and does not hide or reorder any items.

When enabled, equipment assigned to a room is shown in red until it has a graphical view in at least one **Floor Plan** targeting that room's storey. Saved plans that are not open also count. Placing a device restores its usual label; removing its last view or deleting its last containing plan makes it red again. A view in another plan of the same storey still counts. A view left on a former storey does not count after the device's room changes.

The red highlight propagates to each affected **Room**, **Storey** and **Dwelling**, even when these items are collapsed. It clears when all their room equipment has been placed. Empty rooms and storeys retain their usual labels. The project root, navigation groups, catalog types, control channels, modular devices and equipment without a room also retain their usual labels.

Disabling the filter restores all normal labels immediately. The preference belongs to the current explorer, so another tab can leave it disabled independently. It does not change the model, plan layout or the diagram's separate **Show equipment labels** option.

The two explorer highlights are mutually exclusive: enabling placement switches off circuit completeness, and conversely.

`FloorPlanPlacementFilterProvider` contributes the default-off checkbox through Sirius Web 2026.9.0's [native tree-filter API](https://github.com/eclipse-sirius/sirius-web/blob/v2026.9.0/packages/trees/backend/sirius-components-collaborative-trees/src/main/java/org/eclipse/sirius/components/collaborative/trees/api/ITreeFilterProvider.java). The View DSL has no filter declaration in this version; the platform passes active filter identifiers to the tree renderer. `ExplorerPlanPlacement` uses them to prepare an immutable snapshot for that render only when requested, with live diagrams taking precedence over saved versions. It reuses the explorer's metadata snapshot when available. Parent labels inspect semantic room assignments against that same snapshot, independently of the expanded tree items. The disabled filter performs no placement-specific diagram reads and preserves the original label fragments. No placement flag is stored in the semantic model.

## Optional circuit completeness highlight

Enable **Highlight incomplete circuit elements** in the same **Filters** menu. The French checkbox is **Surligner les éléments incomplets des circuits**. It starts unchecked and enabling it unchecks the placement highlight. The labels turn red for:

- Final or distribution circuits without a protective device.
- Terminal equipment without a circuit, including equipment without a room.
- Modular devices in distribution boards that are unused by every circuit.

Usage includes upstream RCCBs of circuit protection devices and actuators of functional controls with a circuit-connected target or source control device. This avoids marking a used upstream RCCB or impulse relay as spare. Energy meters, service circuit-breakers and catalog types are outside this check.

Red propagates through room, storey and dwelling for unconnected terminal equipment, and through rails, boards, the Circuits group and GTL for their incomplete descendants. Collapsing these parents retains the indication. This is an editing aid, not an electrical compliance check; it neither changes associations nor prevents saving. Each explorer keeps its own selection, and reopening starts with both highlights off.

`CircuitCompletenessFilterProvider` contributes the checkbox and `ExplorerCircuitCompleteness` computes an immutable semantic snapshot only when it is enabled. Sirius Web 2026.9.0 has no exclusive filter-group API: `hed-explorer-filters-plugin.ts` routes the application's public tree-component imports through `ExclusiveTreeToolBar`, which forwards the native toolbar and submits one normalized selection per click. The native menu and per-explorer state remain in use. Production builds fail if that public import boundary changes during a Sirius Web upgrade, so the adapter must then be reviewed. The server also gives circuit completeness priority if a client submits both filter identifiers.

## Modular device labels

A modular device mounted on a DIN rail is displayed as **Name - Device type name**, for example `Q1 - MCB C16 A`. Its name is the model's single local identifier. If the name is empty or contains only whitespace, the explorer displays the catalog type name alone. Without a catalog type, the usual object label is retained.

The type's name is stored user data, initialized in the project's creation language. The explorer does not translate it when the application language changes. Renaming a catalog type updates the suffix on all associated modular devices without changing their stored names.

This composite label is specific to Model Explorer. Properties, reference selections, table cells/row headers and panel captions retain their existing labels. A printed cabinet label remains in the separate `labelText` field. Terminal equipment and the other named elements keep their usual explorer labels, apart from the fixed localized Dwelling navigation label.

## Rename

Use **Rename** on a modular device to edit only its stored name. The inline editor starts with that name, including an empty value, rather than the composite label. A name containing a hyphen is preserved as entered. Confirming the edit uses Sirius Web's native rename action and updates the `name` attribute; it does not change the catalog type or cabinet label text.

The initial edit-label provider handles modular devices and delegates all other elements and saved representations to the standard Sirius Web provider. Renaming and moving an object preserve its identity and electrical references.

## Ordering and pictograms

See [reordering circuits, rails and modular devices](explorer-reordering.md) for contextual movement commands and circuit drag-and-drop, and [model pictograms](icons.md) for the shared icon selection.

## Implementation and verification

`HedJavaServices.getHedExplorerLabel` supplies the display label to the tree description. `getHedLabel` remains shared by the other representations. `HedInitialDirectEditTreeItemLabelProvider` customizes only the initial edit value through Sirius Web 2026.9.0's [native explorer label provider API](https://github.com/eclipse-sirius/sirius-web/blob/v2026.9.0/packages/sirius-web/backend/sirius-web-application/src/main/java/org/eclipse/sirius/web/application/views/explorer/services/ExplorerInitialDirectEditTreeItemLabelProvider.java); the native rename handler still writes the semantic name.

`ProjectNames` in `hed.starter.project` resolves the project through Sirius Web's editing-context and project services. Virtual nodes have stable IDs derived from their semantic owner, so expansion and parent lookup do not depend on translated labels. Integration tests cover project/group identity, displayed parent/child consistency, catalog classification, contextual creation and saved representations. The bilingual `explorer-hierarchy.spec.ts` scenario checks the hierarchy, category changes, board/rail creation, occupied-rail deletion protection and persistence.

Integration tests also cover composite/fallback labels, retained names in other contexts, raw edit values and delegation. The bilingual `explorer-labels.spec.ts` browser scenario checks renaming a modular device, propagation of a catalog type rename, persistence after reopening and editing an unnamed modular device.

`CatalogDropService` in `hed.starter.representations` shares catalog-based creation and validation for DIN rails and rooms. Diagram catalog drops continue to use the rail route. `CatalogTreeDropHandler` is restricted to the domain explorer through the native [tree drop API](https://github.com/eclipse-sirius/sirius-web/blob/v2026.9.0/packages/trees/backend/sirius-components-collaborative-trees/src/main/java/org/eclipse/sirius/components/collaborative/trees/api/IDropTreeItemHandler.java). The bilingual `catalog-drop.spec.ts` checks both routes, invalid targets/types, full-rail rejection, catalog-owned ratings and persistence. `CatalogDropTest` also checks every terminal category, whole-selection room rejection and localized control-channel names. The bilingual `terminal-catalog-drop.spec.ts` verifies room creation, repeated drops, names and properties, invalid targets/types, placement on a storey floor plan and persistence.
