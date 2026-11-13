# Circuit Diagram

**Experimental representation.** The Circuit Diagram is expected to evolve substantially. Future changes may invalidate existing saved circuit diagrams, causing their views and layouts to be lost and requiring them to be recreated. Limit its use for now and avoid spending time arranging diagrams you need to retain.

On the **Dwelling** in Model Explorer, choose **New representation → Circuit Diagram** (French: **Logement → Nouvelle représentation → Schéma des circuits**).

This Sirius Web diagram shows every storey and its rooms, terminal equipment inside the assigned room, and distribution boards containing their DIN rails and modular devices. The service connection contains its meter and service circuit-breaker. Equipment without a room remains visible at diagram level. Circuits and branches are represented by named Manhattan edges, with one small junction for each branched circuit.

The first arrangement is automatic. Move storeys, rooms, terminal equipment, junctions and boards freely; resize storey, room and service-connection containers. Once the user moves elements, Sirius retains that manual arrangement rather than rearranging the complete diagram after every edit. Layout and routing are stored per representation, independently of imported floor-plan views. No category containers for outlets, lighting or shutters are created.

Existing Material Design Icons are reused. Terminal equipment pictograms are 48 × 48 drawing units, with names outside them, leaving more room to adjust edge endpoints. This size applies to the Circuit Diagram; Floor Plan icons remain 24 × 24. A control device with several channels exposes them as small border pictograms: the two channels of a double switch remain distinct connection endpoints. With a single channel, the functional control edge starts directly from the parent device; no channel border node is shown. Adding or removing channels updates these endpoints automatically.

## Distribution boards and rails

Boards stack their rails vertically in model order. All rails fill the board's inner width, including shorter and empty rails. Each rail aligns its modular devices horizontally in the order shown in Model Explorer. Devices have a rectangular body, a 24 × 24 MDI pictogram and their stored name; their height is 112 drawing units and their width follows the catalog's module width at 64 units per module (minimum 64). Hover over a device to read its full label when its name is truncated.

Board width follows its widest rail. Rails initially reserve 260 units vertically, with the devices 80 units below the rail's top edge to provide routing space above and below them. Select a rail by clicking its caption, then drag its bottom border downward to increase the space before the next row of devices. Its height is saved with this diagram and can be reduced back to the minimum; the following rails move down automatically and the board grows accordingly. Resizing changes no catalog dimensions or semantic rail capacity.

Rails and modular devices cannot be freely moved, and device bodies retain their catalog-derived width. Reorder them through Model Explorer; the physical Distribution Board Layout and this schematic then show the same semantic order. Moving the whole board preserves its internal arrangement, including the saved rail heights.

## Connections

Solid grey arrows show recorded supply and protection relationships:

- The meter supplies the service circuit-breaker within its service connection.
- The service circuit-breaker supplies devices through their explicit `upstreamServiceBreaker` references; an RCCB supplies the devices referencing it through `upstreamRccb`.
- Without branches, a final circuit connects its protective device to its first associated device, then that device to the second, and so on. The first segment carries the circuit's stored name; continuation labels repeat only when crossing rooms or when either room is unknown. There is no circuit node.
- With branches, a final circuit connects its protective device to a **black 8 × 8 junction**, labeled only on the incoming edge with the circuit's name. Each nonempty branch starts at this junction and connects to its first device, then continues from device to device. The first branch segment carries the branch's stored name; continuation labels repeat only when crossing rooms or when either room is unknown. No branch nodes are created. Equipment stays in its room and appears only once.
- Direct-circuit chains follow the circuit's ordered associated-equipment list. Branch chains follow each branch's associated-equipment list. In Details, use the list's **…** button and drag the grips in the native transfer dialog's right-hand list to reorder equipment. This order is saved independently of room/explorer order. Moving pictograms does not change these orders.
- Distribution circuits connect their protective device directly to their target board; the edge carries the distribution circuit's name.

Conductor sections and applicable phases remain editable in Details, without being appended to edge names. An empty branch has no outgoing edge until a device is assigned. A circuit without equipment and without branches has no supply edge; it remains available in Model Explorer and the Circuit Table. Missing protection produces no invented source endpoint. A branched circuit's junction remains visible even without protection or a source board; existing member-to-member segments and functional controls remain visible.

Use **Route protection links above devices** beside a rail's caption to route its protection links in an inverted U above the devices. RCCB outgoing anchors are spread across its width to make them easier to select. This action replaces the protection routes on that rail and saves the resulting layout; other circuits and functional-control paths retain their routes. Select a link to use Sirius Web's endpoint and Manhattan-segment handles to adjust it further. Increase a rail's height when more room is needed for circuit edges leaving the devices. Existing routes remain unchanged until an editing action is used.

Dashed purple arrows show functional controls and carry the control function's name. A direct control connects each participating channel to each controlled load or outlet. If the function declares an actuator, the diagram instead shows **channels → actuator → controlled equipment**. Several commands for the same light, double switches, switched outlets and shutters use the existing model relationships without inferring an additional electrical connection.

When a receiver and at least one of its command devices belong to the same nonempty circuit, the receiver is skipped in that circuit's solid chain (including branch chains). The surviving members connect directly to each other. The receiver stays visible and is reached through its dashed control edge. This applies to lighting, switched outlets and shutter motors. Its circuit and branch assignments remain unchanged, and remain listed in Details and tables. Receivers controlled from a different circuit, or without a command assigned to their circuit, keep their solid supply path.

Click a node or edge to edit its underlying object through Details. The first supply edge represents its circuit or branch; a continuation edge represents its target equipment. The junction represents its final circuit. A command edge exposes the control function's channels, targets and actuator. Changes refresh the diagram. Creation/deletion and room assignment remain available through Model Explorer and Details. Graphical positions inside a room do not change the equipment's room or circuit.

## Model coverage

Create branches below final circuits in Model Explorer and associate existing devices by dragging them onto a branch or editing **Branch** in Properties. A branch exposes its name, parent supply circuit and associated equipment. The Equipment Table also shows the branch. Associations update the diagram immediately without changing room assignments or functional control links. See [branches in Model Explorer](model-explorer.md#circuit-branches-and-associated-devices).

Branches are optional: Details lets you edit the circuit's **Associated equipment** directly while it has no branches. The first branch receives these existing devices. With branches, the same circuit field displays their combined equipment in read-only mode. Deleting the last branch restores editable direct assignments. The diagram switches between direct chains and chains starting at the junction. Direct assignments are not projected as a bypass when branches exist.

The current metamodel already supplies the required logical information: `Circuit.sourceBoard`, `protectiveDevice`, `phase`, `conductorSectionMm2`, `FinalCircuit.branches`, `CircuitBranch.circuit`/`equipment`, `DistributionCircuit.targetBoard`, `FieldEquipment.room`/`circuit`/`branch`, `ControlDevice.channels`, `ControlFunction.channels`/`targets`/`actuator`, and the two upstream protection references on modular devices. No metamodel or existing-data migration is required for this representation.

The displayed chains express association order, not a physical series connection or a measured cable route. This logical view does not record conductor-by-conductor terminals or physical junction boxes. Its black junction is a graphical projection, not an additional semantic device. Existing missing-assignment warnings remain accessible in Details. Display in this schematic does not count as placement on a storey's Floor Plan for the explorer's red-label indicator.

## Moving junctions

A junction has an **8 × 8 black square with a white halo**, centred within a **24 × 24 transparent hit area**. Hover over it to reveal the manipulation frame and the circuit name. Grab the square or the transparent margin to move the junction. The hit area takes priority over crossing/selected edges; edges remain selectable outside it. Selection keeps the frame visible. Pinned junctions and read-only diagrams cannot be dragged.

All incident edges connect to the symbol's centre and follow its movement. Position is saved with the representation. Existing three-unit junctions retain their identities and centre when opened with the larger manipulation area; no circuit/branch assignments change.

## Implementation and verification

`CircuitDiagram` keeps its position in the View contribution, preserving the other representation-description identifiers. `CircuitServices` projects the ordered chains and resolves single/multiple-channel endpoints. Terminal pictograms, room/storey containers and edges use native rendering. `CircuitJunctionNodeStyle` and the scoped `CircuitJunction` contribution render the small junction symbol with a larger hit area, centred handles and native selection/layout persistence. Boards use native vertical lists, which remain fixed during automatic arrangement. `CircuitBoardNodeStyle` and the scoped `CircuitBoard` frontend contribution constrain their horizontal rails and modular rectangles, preserving native connection handles. The diagram uses `AUTO_UNTIL_MANUAL`.

Java tests evaluate the actual AQL definitions against complete and incomplete models, checking unique equipment views, ordered paths, dimensions, edge names, optional branches, unchanged protection/control relationships and single/multiple-channel endpoints. Frontend unit tests check semantic rail ordering, shared board widths, saved rail heights, live resizing and scoped protection routing without changing other edges. The bilingual browser scenarios check actual geometry, inverted-U protection paths, rail resizing and persistence, edge labels, branching, icons, saved custom styles and live circuit/room edits.
