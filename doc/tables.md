<!--
Copyright (c) 2026 Laurent Redor.
This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0
-->

# Tables

The electrical-installation explorer opens automatically. Select an object in Model Explorer, open its context menu and choose **New representation**. The available tables depend on the selected object:

| Context | English title | French title | Editable cells |
| --- | --- | --- | --- |
| Dwelling, Storey or Room | Equipment Table | Inventaire des équipements | Name (when shown) |
| Dwelling | Circuit Table | Liste des circuits | Name (when shown), description, conductor section |
| Distribution board | Distribution Board Table | Composition du tableau | Name, label text |
| Device catalog | Device Catalog Table | Catalogue des équipements | Name, current/power ratings, module width, residual-current rating, manufacturer, product reference |

Equipment and circuits use a single name. Catalog entries have one stored name seeded in the project's creation language. Neither legacy references nor a second translated catalog name appear in the tables.

The board table follows the order of rails and then the order of devices on each rail. Its read-only **Upstream protection** column shows the direct upstream RCCB or service circuit-breaker and participates in searching, filtering and sorting. Device ratings and module widths are read from the catalog type and are read-only on the board's instances. Edit those specifications in the catalog table or its Properties view; dependent values update in the other representations.

## Equipment inventories

Create an Equipment Table on the **Dwelling**, a **Storey** or a **Room**. It displays the equipment currently assigned to that scope, in semantic order unless sorting is applied. The dwelling inventory also includes equipment without a room; storey and room inventories exclude it. Moving equipment to another room immediately updates all affected inventories without duplicating devices.

The visible columns are **Type**, **Circuit / Branch**, **Storey / Room**, **Control functions** and **Issues**, in that order. The circuit cell displays `Circuit name / Branch name` when a branch is assigned, otherwise the circuit name alone. The location cell displays `Storey name - Room name`; missing values do not add dangling separators. Control functions list both commands and receivers. The combined values participate in global search, column filtering and sorting.

**Name** is initially hidden on new inventories, since the row header already identifies the equipment. Use **Show/Hide columns** to display it for inline renaming or filtering; visibility is saved with the representation. Search still includes hidden names. Other cells are read-only: change types, circuit/branch assignments and locations in Details. Electrical ratings remain available in the Advanced tab of Details. Retired category, branch and power columns no longer appear; their saved filters and sorts are ignored when reopening existing inventories.

## Circuit summary

The Circuit Table shows **Description**, **Source board** (only with several boards), **RCCB** (French: **Inter diff**), **Protection**, **Section (mm²)**, **Equipment**, **Issues** and **Usage**, in that order. Hover over RCCB / Inter diff for its full translated term. An applicable **Phase / supply** column appears immediately after Protection. Description and conductor section remain editable. Summary column order follows this definition, including after optional columns appear; widths remain adjustable.

The row header already identifies the circuit, so **Name** is initially hidden on new tables. Use the standard **Show/Hide columns** menu to display it for inline renaming or column filtering; the choice is saved with the representation. Global search also searches names while the column is hidden. Existing tables retain their explicit visibility choices. The source-board column appears or disappears as boards are added or removed.

The Circuit Table shows its **Phase / supply** column only when the dwelling has a service connection explicitly configured as **Three phase**. The circuit phase selector in Properties follows the same rule, for both final and distribution circuits. With no service connection, unknown supply or single-phase supply, the column and selectors are hidden. Their stored values remain available when three-phase supply is selected again. The service connection’s own supply selector is always available.

References and calculated relationships remain read-only in tables; change assignments in Properties. See the [protection relationship workflow](development.md#protection-relationships). Cells use the same domain operations and validation as Properties. An invalid edit retains the previous semantic value and reports the validation message.

Columns are resizable. Global search is case-insensitive. Filtering and numeric/text sorting are applied before pagination, so searches include rows beyond the current page. Table page sizes are 10, 25 and 50 rows.

Saved representations are shown as children of their semantic owner in Model Explorer, before its semantic children. Click a representation to open it. Native rename and delete actions are retained; deleting a representation preserves its model element.

Changes are saved by Sirius Web. Reopening the representation restores the edited data. The bilingual browser workflow verifies creation of all four tables, filtering, inline editing, reopening and propagation of a renamed catalog type to equipment rows.

## Implementation

`hed.starter.representations.tables.HedTableDescriptions` builds the four Sirius Web table descriptions. Equipment inventories use `NamedElement` with a precondition restricted to dwelling, storey and room owners; the description retains its original position and stable identifier. `HedEquipmentTableScope` selects current assignments and composes circuit/branch and storey/room values. `HedTableCreationService` delegates to native creation and initializes the summaries' Name visibility once; the native renderer and visibility menu then preserve user choices. `ViewContribution` registers the tables in the same view as the domain explorer and assigns stable identifiers independently of translated titles. `HedJavaServices.getHedRows` prepares filtered/sorted rows before the standard Sirius pagination service; `getHedCell` and `editHedCell` supply values and domain edits. The shared icon mapping supplies the table and row pictograms. `ExplorerRepresentations` supplies native representation metadata to the tree once per render; tree services attach each saved representation to its semantic target.

Sirius Web 2026.9.0 exposes neither a header-tooltip setting nor a default position for columns introduced after creation. The guarded `hed-table-presentation-plugin` adds two narrow hooks to its pinned frontend module: HED summary order and an optional heading wrapper. `HedTablePresentation` supplies the ordered projection and the translated RCCB tooltip while retaining native columns, cells, identities and visibility preferences. Other tables keep native column reordering. Unit tests check the installed module boundaries; an incompatible dependency update fails the build explicitly.

Reference: [Sirius Web 2026.9.0 table descriptions](https://github.com/eclipse-sirius/sirius-web/blob/v2026.9.0/doc/content/modules/studio-maker-guide/pages/features/table-description.adoc).
