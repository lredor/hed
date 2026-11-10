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
| Distribution board / DIN rail | DIN rails / compatible modular appliances within the rail capacity |
| Circuits / Final circuit | Final or distribution circuits / branches |
| Catalog category | A device type initialized in that category |

Use **New object** on a room or rail to choose a compatible catalog type, or a contextual **Create** action to use the default type for a category. A board cannot create an unmounted appliance, and an occupied rail cannot be deleted.

Distribution board catalog types include modular apparatus, energy meters and service circuit-breakers. All other types appear under Terminal equipment. Creating a type from the distribution board category initializes it as MCB; creating one from the terminal category initializes it as Outlet. Editing its **Category** immediately moves the same type to the corresponding navigation group, retaining its name, identity and semantic catalog ownership.

## Modular device labels

An appliance mounted on a DIN rail is displayed as **Name - Device type name**, for example `Q1 - MCB C16 A`. Its name is the model's single local identifier. If the name is empty or contains only whitespace, the explorer displays the catalog type name alone. Without a catalog type, the usual object label is retained.

The type's name is stored user data, initialized in the project's creation language. The explorer does not translate it when the application language changes. Renaming a catalog type updates the suffix on all associated appliances without changing their stored names.

This composite label is specific to Model Explorer. Properties, reference selections, table cells/row headers and panel captions retain their existing labels. A printed cabinet label remains in the separate `labelText` field. Terminal equipment and the other named elements keep their usual explorer labels, apart from the fixed localized Dwelling navigation label.

## Rename

Use **Rename** on an appliance to edit only its stored name. The inline editor starts with that name, including an empty value, rather than the composite label. A name containing a hyphen is preserved as entered. Confirming the edit uses Sirius Web's native rename action and updates the `name` attribute; it does not change the catalog type or cabinet label text.

The initial edit-label provider handles modular devices and delegates all other elements and saved representations to the standard Sirius Web provider. Renaming and moving an object preserve its identity and electrical references.

## Ordering and pictograms

See [reordering rails and appliances](explorer-reordering.md) for the contextual movement commands and [model pictograms](icons.md) for the shared icon selection.

## Implementation and verification

`HedJavaServices.getHedExplorerLabel` supplies the display label to the tree description. `getHedLabel` remains shared by the other representations. `HedInitialDirectEditTreeItemLabelProvider` customizes only the initial edit value through Sirius Web 2026.9.0's [native explorer label provider API](https://github.com/eclipse-sirius/sirius-web/blob/v2026.9.0/packages/sirius-web/backend/sirius-web-application/src/main/java/org/eclipse/sirius/web/application/views/explorer/services/ExplorerInitialDirectEditTreeItemLabelProvider.java); the native rename handler still writes the semantic name.

`ProjectNames` in `hed.starter.project` resolves the project through Sirius Web's editing-context and project services. Virtual nodes have stable IDs derived from their semantic owner, so expansion and parent lookup do not depend on translated labels. Integration tests cover project/group identity, displayed parent/child consistency, catalog classification, contextual creation and saved representations. The bilingual `explorer-hierarchy.spec.ts` scenario checks the hierarchy, category changes, board/rail creation, occupied-rail deletion protection and persistence.

Integration tests also cover composite/fallback labels, retained names in other contexts, raw edit values and delegation. The bilingual `explorer-labels.spec.ts` browser scenario checks renaming an appliance, propagation of a catalog type rename, persistence after reopening and editing an unnamed appliance.
