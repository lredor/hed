<!--
Copyright (c) 2026 Laurent Redor.
This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0
-->

# Model pictograms

The explorer, creation menus, native new-object dialog and semantic reference selectors share the icon selection in `HedDomainIcons`. Catalog types and equipment instances use their category icon. Virtual explorer groups use distinct pictograms; the explorer tab has a tree icon.

The 43 bundled SVGs are an unmodified subset of **Material Design Icons 7.4.47** (`@mdi/svg`), by [Pictogrammers](https://pictogrammers.com/library/mdi/). They are stored in the starter module at `backend/hed-starter/src/main/resources/hed/mdi/`. Their [Apache 2.0 license, upstream license notice and individual author credits](../backend/hed-starter/src/main/resources/hed/mdi/NOTICE.md) ship alongside the files. They retain their upstream licensing; the project's EPL-2.0 does not replace it.

The mapping gives a catalog type and every equipment instance using it the same category icon. Virtual explorer branches have distinct pictograms. Circuit branches are included because they are already part of the consolidated domain model. The application serves the bundled icons locally through Sirius at `/api/images/hed/mdi/`, without an external icon service. Project-root, GTL and representation-specific entries are reserved for the corresponding later UI increments.

These are interface pictograms, not a claim of IEC 60617 conformity. Physical cabinet and appliance illustrations belong to the separate panel diagram increment.

| Concept | MDI icon | Preview |
| --- | --- | --- |
| Project | `folder-home-outline` | ![folder-home-outline](../backend/hed-starter/src/main/resources/hed/mdi/folder-home-outline.svg) |
| Dwelling | `home-outline` | ![home-outline](../backend/hed-starter/src/main/resources/hed/mdi/home-outline.svg) |
| Electrical service area (GTL) | `home-lightning-bolt-outline` | ![home-lightning-bolt-outline](../backend/hed-starter/src/main/resources/hed/mdi/home-lightning-bolt-outline.svg) |
| Storey | `layers-outline` | ![layers-outline](../backend/hed-starter/src/main/resources/hed/mdi/layers-outline.svg) |
| Room | `floor-plan` | ![floor-plan](../backend/hed-starter/src/main/resources/hed/mdi/floor-plan.svg) |
| Distribution board / panel catalog branch | `view-dashboard-outline` | ![view-dashboard-outline](../backend/hed-starter/src/main/resources/hed/mdi/view-dashboard-outline.svg) |
| DIN rail / other modular device | `view-sequential-outline` | ![view-sequential-outline](../backend/hed-starter/src/main/resources/hed/mdi/view-sequential-outline.svg) |
| Catalog | `book-open-variant` | ![book-open-variant](../backend/hed-starter/src/main/resources/hed/mdi/book-open-variant.svg) |
| New device type | `tag-outline` | ![tag-outline](../backend/hed-starter/src/main/resources/hed/mdi/tag-outline.svg) |
| Terminal catalog branch / fixed appliance / other field equipment | `power-plug-outline` | ![power-plug-outline](../backend/hed-starter/src/main/resources/hed/mdi/power-plug-outline.svg) |
| Unlocated equipment | `map-marker-question-outline` | ![map-marker-question-outline](../backend/hed-starter/src/main/resources/hed/mdi/map-marker-question-outline.svg) |
| Final circuit | `vector-line` | ![vector-line](../backend/hed-starter/src/main/resources/hed/mdi/vector-line.svg) |
| Circuit branch | `source-branch` | ![source-branch](../backend/hed-starter/src/main/resources/hed/mdi/source-branch.svg) |
| Distribution circuit / circuits branch | `vector-polyline` | ![vector-polyline](../backend/hed-starter/src/main/resources/hed/mdi/vector-polyline.svg) |
| Functional control / controls branch | `gesture-tap` | ![gesture-tap](../backend/hed-starter/src/main/resources/hed/mdi/gesture-tap.svg) |
| Control channel | `tune-vertical-variant` | ![tune-vertical-variant](../backend/hed-starter/src/main/resources/hed/mdi/tune-vertical-variant.svg) |
| Service connection | `transmission-tower-import` | ![transmission-tower-import](../backend/hed-starter/src/main/resources/hed/mdi/transmission-tower-import.svg) |
| Energy meter | `meter-electric-outline` | ![meter-electric-outline](../backend/hed-starter/src/main/resources/hed/mdi/meter-electric-outline.svg) |
| Service circuit-breaker | `shield-home-outline` | ![shield-home-outline](../backend/hed-starter/src/main/resources/hed/mdi/shield-home-outline.svg) |
| Outlet | `power-socket-fr` | ![power-socket-fr](../backend/hed-starter/src/main/resources/hed/mdi/power-socket-fr.svg) |
| Switch | `toggle-switch-outline` | ![toggle-switch-outline](../backend/hed-starter/src/main/resources/hed/mdi/toggle-switch-outline.svg) |
| Push-button | `gesture-tap-button` | ![gesture-tap-button](../backend/hed-starter/src/main/resources/hed/mdi/gesture-tap-button.svg) |
| Dimmer | `brightness-6` | ![brightness-6](../backend/hed-starter/src/main/resources/hed/mdi/brightness-6.svg) |
| Shutter control | `window-shutter-settings` | ![window-shutter-settings](../backend/hed-starter/src/main/resources/hed/mdi/window-shutter-settings.svg) |
| Luminaire | `lightbulb-outline` | ![lightbulb-outline](../backend/hed-starter/src/main/resources/hed/mdi/lightbulb-outline.svg) |
| Shutter motor | `window-shutter` | ![window-shutter](../backend/hed-starter/src/main/resources/hed/mdi/window-shutter.svg) |
| Heater | `radiator` | ![radiator](../backend/hed-starter/src/main/resources/hed/mdi/radiator.svg) |
| Water heater | `water-boiler` | ![water-boiler](../backend/hed-starter/src/main/resources/hed/mdi/water-boiler.svg) |
| Ventilation | `fan` | ![fan](../backend/hed-starter/src/main/resources/hed/mdi/fan.svg) |
| Cooker | `stove` | ![stove](../backend/hed-starter/src/main/resources/hed/mdi/stove.svg) |
| MCB | `electric-switch` | ![electric-switch](../backend/hed-starter/src/main/resources/hed/mdi/electric-switch.svg) |
| RCCB | `shield-half-full` | ![shield-half-full](../backend/hed-starter/src/main/resources/hed/mdi/shield-half-full.svg) |
| RCBO | `shield-check-outline` | ![shield-check-outline](../backend/hed-starter/src/main/resources/hed/mdi/shield-check-outline.svg) |
| Fuse | `fuse` | ![fuse](../backend/hed-starter/src/main/resources/hed/mdi/fuse.svg) |
| Isolator / contactor | `electric-switch-closed` | ![electric-switch-closed](../backend/hed-starter/src/main/resources/hed/mdi/electric-switch-closed.svg) |
| Surge protective device | `lightning-bolt-outline` | ![lightning-bolt-outline](../backend/hed-starter/src/main/resources/hed/mdi/lightning-bolt-outline.svg) |
| Impulse relay | `toggle-switch-variant` | ![toggle-switch-variant](../backend/hed-starter/src/main/resources/hed/mdi/toggle-switch-variant.svg) |
| Electrical load without a type | `flash-outline` | ![flash-outline](../backend/hed-starter/src/main/resources/hed/mdi/flash-outline.svg) |
| Unclassified element | `shape-outline` | ![shape-outline](../backend/hed-starter/src/main/resources/hed/mdi/shape-outline.svg) |
| Explorer / schedule | `file-tree-outline` / `table-large` | ![file-tree-outline](../backend/hed-starter/src/main/resources/hed/mdi/file-tree-outline.svg) ![table-large](../backend/hed-starter/src/main/resources/hed/mdi/table-large.svg) |
| Move left / move right | `arrow-left` / `arrow-right` | ![arrow-left](../backend/hed-starter/src/main/resources/hed/mdi/arrow-left.svg) ![arrow-right](../backend/hed-starter/src/main/resources/hed/mdi/arrow-right.svg) |

To update the subset, select non-brand icons from a pinned `@mdi/svg` release, copy their SVGs without modification, and refresh the upstream license and author credits from the package's `meta.json`. Update this mapping and validate the SVG files and their credits. Also check the explorer, creation menus and reference selectors in the browser.
