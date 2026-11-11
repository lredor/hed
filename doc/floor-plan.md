<!--
Copyright (c) 2026 Laurent Redor.
This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0
-->

# Storey floor plans

Create **Floor Plan** on a **Storey** using its Model Explorer menu, **New Representation**. The saved representation is listed under that storey and can be opened again from there.

## Import a background

Use **Import floor plan** to upload a PNG, JPEG or SVG image from your computer. Images must be at most 10 MB and have a width/height ratio between 1:5 and 10:1. The native Sirius Web project-image service stores the image. Its UUID, normalized canvas width (1200 units) and proportional height are recorded on the Storey; these fields are managed by the import tool and hidden from Properties.

Use **Replace floor plan** to choose another image or **Remove background** to return to an empty canvas. These actions retain equipment views and their positions. Different representations of the same storey share its background image; each has its own equipment views and layout. Images remain in the project's image library when the background is removed or replaced.

## Place existing equipment

Expand the storey's rooms in Model Explorer, then drag an existing outlet, control device or load onto the plan. Its room must belong to the selected storey. Catalog types, equipment without a room and equipment from another storey are rejected. Dropping an already displayed device does not create another semantic device or a duplicate view.

Drag the icon freely to position it along a wall or anywhere else on the image. Device nodes and their existing Material Design pictograms are both **24 × 24** diagram units, with no frame around the icon. Positions belong to the saved representation; moving a view never changes its room or circuit.

Labels are hidden by default. The header's **Show equipment labels** filter displays names just below the icons without changing node dimensions or positions. The preference is stored per diagram in this browser; a new representation starts with labels hidden. Hovering an icon shows its name even when labels are hidden.

The contextual **Remove from plan** action removes only the selected graphical view. The device remains in its room and can be placed again. Pressing **Delete** on plan equipment opens a **Remove from plan** confirmation explaining that the selected graphical elements will be removed while their devices and connections remain in the model. Cancelling retains the views. If a device is assigned to a room on another storey, its view disappears from this plan. Semantic editing continues through Properties and Model Explorer.

## Check placement from Model Explorer

Model Explorer keeps its usual labels by default. Enable **Highlight equipments not in floor plans** from its native **Filters** menu to color room equipment red when it has no view in any plan of its current storey. The red highlight also marks its room, storey and dwelling, including collapsed items, until all their room equipment is placed. The French label is **Surligner les équipements absents des plans**. Saved plans that are closed also count. This option is independent of the plan’s own outside-label checkbox; see [the placement highlight workflow](model-explorer.md#optional-floor-plan-placement-highlight).

## Implementation and validation

Backend contributions are in `hed.starter.representations.diagrams.floorplan`, and frontend rendering is in `frontend/src/representations/diagrams/floorplan`. The native View DSL uses an unsynchronized equipment node and Create View / Delete View operations. A custom style and React Flow converter draw the image and the compact pictograms, retaining native selection, drops and persisted positions. The canvas is fixed and nonselectable.

Background assignments run in the editing-context event queue. The handler checks the uploaded image's project and supported media type before changing the Storey. Invalid commands leave the previous background untouched; read-only diagrams disable import and removal controls.

`FloorPlanRemovalConfirmation` adapts the native confirmation context only while a floor-plan background is mounted. It preserves the original tool callback, dialog cancellation and user confirmation settings. Its representation factory delegates to the native diagram component, and the registry merge retains the other native representation factories. The Model Explorer's semantic deletion and the distribution-board diagram use their standard confirmation.

Java tests cover storey eligibility, model defaults and project-owned atomic background assignments. Bilingual browser workflows cover SVG/PNG/JPEG import, replacement, removal and malformed-image rejection; equipment placement and movement; duplicate and wrong-storey drops; outside-label filtering; view removal without semantic deletion; room reassignment; independent layouts and persistence after reopening.

The implementation follows the [Sirius Web 2026.9.0 diagram description guide](https://github.com/eclipse-sirius/sirius-web/blob/v2026.9.0/doc/content/modules/studio-maker-guide/pages/features/diagram-description.adoc) and the project's existing panel-style extension pattern.
