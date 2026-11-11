# Circuit Diagram

**Experimental representation.** The Circuit Diagram is expected to evolve substantially. Future changes may invalidate existing saved circuit diagrams, causing their views and layouts to be lost and requiring them to be recreated. Limit its use for now and avoid spending time arranging diagrams you need to retain.

On the **Dwelling** in Model Explorer, choose **New representation → Circuit Diagram** (French: **Logement → Nouvelle représentation → Schéma des circuits**).

This is a standard Sirius Web diagram, using native containers, image nodes, border nodes and Manhattan edges. It shows every storey and its rooms, terminal equipment inside the assigned room, and distribution boards containing their devices and circuits. The service connection contains its meter and service circuit-breaker. Equipment without a room and circuits without a source board remain visible at diagram level, so an incomplete draft does not disappear.

The first arrangement is automatic. Move storeys, rooms, equipment and boards freely, and resize their containers. Once the user moves elements, Sirius retains that manual arrangement rather than rearranging the complete diagram after every edit. Layout and routing are stored per representation, independently of the imported floor-plan views and physical DIN-rail order. No category containers for outlets, lighting or shutters are created.

Existing Material Design Icons are reused. Equipment pictograms are 24 × 24 drawing units, with names outside them. A control device exposes its independent channels as small border pictograms: the two channels of a double switch are distinct connection endpoints.

## Connections

Solid grey arrows show recorded supply and protection relationships:

- The meter supplies the service circuit-breaker within its service connection.
- The service circuit-breaker supplies devices through their explicit `upstreamServiceBreaker` references; an RCCB supplies the devices referencing it through `upstreamRccb`.
- A circuit's protective device connects to that circuit. Circuit nodes show the name, conductor section when provided, and phase only with a three-phase general supply.
- Final circuits connect to their branches, then each branch connects to its assigned equipment. Branch nodes appear inside the source board, or at diagram level when the circuit has no source board. Equipment stays in its room and appears only once. Direct circuit links are used only by circuits without branches, including control devices when their circuit is explicitly assigned.
- Distribution circuits connect to their target distribution board.

Dashed purple arrows show functional controls and carry the control function's name. A direct control connects each participating channel to each controlled load or outlet. If the function declares an actuator, the diagram instead shows **channels → actuator → controlled equipment**. Several commands for the same light, double switches, switched outlets and shutters use the existing model relationships without inferring an additional electrical connection.

Click a node or edge to edit its underlying object through Properties. For example, selecting a supply edge to an outlet exposes that equipment's circuit selector; selecting a command edge exposes the control function's channels, targets and actuator. Changes refresh the diagram. Creation/deletion and room assignment remain available through the existing Model Explorer and Properties operations. Graphical positions inside a room do not change the equipment's room, circuit or DIN-rail membership.

## Model coverage

Create branches below final circuits in Model Explorer and associate existing devices by dragging them onto a branch or editing **Branch** in Properties. A branch exposes its name, parent supply circuit and associated equipment. The Equipment Table also shows the branch. Associations update the diagram immediately without changing room assignments or functional control links. See [branches in Model Explorer](model-explorer.md#circuit-branches-and-associated-devices).

Branches are optional: Details lets you edit the circuit's **Associated equipment** directly while it has no branches. The first branch receives these existing devices. With branches, the same circuit field displays their combined equipment in read-only mode. Deleting the last branch restores editable direct assignments. The diagram shows direct supply links without branches and supply paths through a branch for devices assigned to one.

The current metamodel already supplies the required logical information: `Circuit.sourceBoard`, `protectiveDevice`, `phase`, `conductorSectionMm2`, `FinalCircuit.branches`, `CircuitBranch.circuit`/`equipment`, `DistributionCircuit.targetBoard`, `FieldEquipment.room`/`circuit`/`branch`, `ControlDevice.channels`, `ControlFunction.channels`/`targets`/`actuator`, and the two upstream protection references on modular devices. No metamodel or existing-data migration is required for this representation.

This logical view does not record the physical sequence of outlets along a cable, cable routes, conductor-by-conductor terminals or junction boxes. In particular, outlets on one circuit are shown connected to the same circuit node; no daisy-chain order is invented from their graphical position. Existing missing-assignment warnings remain accessible in Properties. Display in this schematic does not count as placement on a storey's Floor Plan for the explorer's red-label indicator.

## Implementation and verification

`CircuitDiagram` is appended to the View contribution, preserving existing representation-description identifiers. `CircuitIconStyleProvider` resolves shared MDI assets into the platform's native `ImageNodeStyle`; no custom frontend node renderer or layout implementation is introduced. Containers use free-form layout and the diagram uses `AUTO_UNTIL_MANUAL`.

Java tests evaluate the actual AQL definitions against the sample model and check that all relevant objects occur exactly once, every expected protection/circuit/control connection exists, actuators replace direct command edges, and incomplete assignments remain visible. The browser scenario in `frontend/e2e/circuit-diagram.spec.ts` checks native nodes/edges, room containment, icons, manual placement persistence and live edits of circuit and room assignments.
