<!--
Copyright (c) 2026 Laurent Redor.
This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0
-->

# Tables

If another explorer is active, choose **Electrical installation** in the explorer selector to use the domain projection. Select an object in Model Explorer, open its context menu and choose **New representation**. The available tables depend on the selected object:

| Context | English title | French title | Editable cells |
| --- | --- | --- | --- |
| Dwelling | Equipment Table | Inventaire des équipements | Name, rated power |
| Dwelling | Circuit Table | Liste des circuits | Name, conductor cross-section |
| Distribution board | Distribution Board Table | Composition du tableau | Name, label text |
| Device catalog | Device Catalog Table | Catalogue des équipements | Name, current/power ratings, module width, residual-current rating, manufacturer, product reference |

Equipment and circuits use a single name. Catalog entries have one stored name seeded in the project's creation language. Neither legacy references nor a second translated catalog name appear in the tables.

The equipment table shows rooms, categories, catalog types, circuit assignments and functional controls. The circuit table shows the source board, use, phase, protective device, upstream residual-current protection, conductor cross-section and served equipment. The board table follows the order of rails and then the order of devices on each rail. Its read-only **Upstream protection** column shows the direct upstream RCCB or service circuit-breaker and participates in searching, filtering and sorting. Device ratings and module widths are read from the catalog type and are read-only on the board's instances. Edit those specifications in the catalog table or its Properties view; dependent values update in the other representations.

References and calculated relationships remain read-only in tables; change assignments in Properties. See the [protection relationship workflow](development.md#protection-relationships). Cells use the same domain operations and validation as Properties. An invalid edit retains the previous semantic value and reports the validation message.

Columns are resizable. Global search is case-insensitive. Filtering and numeric/text sorting are applied before pagination, so searches include rows beyond the current page. Table page sizes are 10, 25 and 50 rows.

Saved representations are shown as children of their semantic owner in Model Explorer, before its semantic children. Click a representation to open it. Native rename and delete actions are retained; deleting a representation preserves its model element.

Changes are saved by Sirius Web. Reopening the representation restores the edited data. The bilingual browser workflow verifies creation of all four tables, filtering, inline editing, reopening and propagation of a renamed catalog type to equipment rows.

## Implementation

`hed.starter.representations.tables.HedTableDescriptions` builds the four Sirius Web table descriptions. `ViewContribution` registers them in the same view as the domain explorer and assigns stable identifiers independently of translated titles. `HedJavaServices.getHedRows` prepares filtered/sorted rows before the standard Sirius pagination service; `getHedCell` and `editHedCell` supply values and domain edits. The shared icon mapping supplies the table and row pictograms. `ExplorerRepresentations` supplies native representation metadata to the tree once per render; tree services attach each saved representation to its semantic target.

Reference: [Sirius Web 2026.9.0 table descriptions](https://github.com/eclipse-sirius/sirius-web/blob/v2026.9.0/doc/content/modules/studio-maker-guide/pages/features/table-description.adoc).
