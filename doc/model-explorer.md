<!--
Copyright (c) 2026 Laurent Redor.
This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0
-->

# Model Explorer

Select **Electrical installation** in the explorer selector to use the domain tree. Model Explorer provides contextual creation, rename and delete actions.

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

The project root, GTL and category groups are navigation objects. They do not add semantic classes or duplicate the model. The displayed **Dwelling** label is localized; select it to edit the dwelling's stored name and properties. Inline Rename is disabled for that fixed navigation label.

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

## Implementation and verification

`ProjectNames` in `hed.starter.project` resolves the project through Sirius Web's editing-context and project services. Virtual nodes have stable IDs derived from their semantic owner, so expansion and parent lookup do not depend on translated labels. `HedJavaServices.getHedExplorerLabel` supplies the localized Dwelling navigation label while `getHedLabel` retains the model name in other contexts.

Integration tests cover project/group identity, displayed parent/child consistency, catalog classification and contextual creation. The bilingual `explorer-hierarchy.spec.ts` scenario checks the hierarchy, category changes, board/rail creation, occupied-rail deletion protection and persistence.
