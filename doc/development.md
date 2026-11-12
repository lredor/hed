<!--
Copyright (c) 2026 Laurent Redor.
This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0
-->

# Building and running

## Prerequisites

- JDK 21+, Maven 3.9.9+, Node 22.16+ and npm 10+.
- An accessible PostgreSQL database, with a database and user prepared for this application.
- GitHub Packages credentials for Sirius Web and Sirius EMF JSON dependencies.

Set `GITHUB_ACTOR` to your GitHub login and `GH_PACKAGES_TOKEN` to a token with package read access in the shell used to build. Maven uses `settings.xml.example`; npm reads `frontend/.npmrc`. Both files contain variable references only.

The target Sirius Web version is **2026.9.0**. Ontology is the structural reference, not a dependency: its OWL/XLS/Entity-specific modules are not copied. The application uses the standard Sirius Web frontend, a domain-specific explorer and reflective properties based on the registered Ecore package.

## Build the application

From the repository root:

```sh
cd frontend
npm ci
npm run build
cd ..
mvn -s settings.xml.example -Papplication verify
```

The lockfile is committed. Direct dependencies and the Sirius Web version are pinned. TypeScript uses Node-style module resolution to consume the declaration files supplied by these Sirius Web packages.

The frontend build writes static resources into the application module. Build it before packaging the backend. The Spring Boot executable JAR is `backend/hed-application/target/hed-application-0.1.0-SNAPSHOT.jar`.

The default `mvn verify` builds and tests the model and edit modules. The `application` profile also builds the Sirius Web starter and executable application. The application can run directly against an existing PostgreSQL server or use the [source-built Docker stack](docker.md). The Windows [build helper](../scripts/Build.ps1) installs the locked frontend dependencies, builds its assets, and runs the Maven application verification.

## Run

Configure the database connection, then launch the JAR:

```sh
java -jar backend/hed-application/target/hed-application-0.1.0-SNAPSHOT.jar
```

| Variable | Default | Purpose |
| --- | --- | --- |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5437/hed` | PostgreSQL connection URL |
| `DATABASE_USER` | `hed` | Database user |
| `DATABASE_PASSWORD` | `hed` | Password for the configured database user |
| `HED_LOCALE` | `en_GB` | Application language |
| `HED_ALLOWED_ORIGINS` | `http://localhost:5173` | Allowed frontend development origin |

Set these values to match your database. Liquibase initializes the application schema at startup. The default HTTP port is 8080; it can be overridden with `--server.port=8081`. Readiness is exposed at `/actuator/health/readiness`.

For frontend development, run `npm run dev` in `frontend`. Vite proxies HTTP and WebSocket requests to localhost:8080. A packaged frontend uses the backend serving it as its HTTP/WebSocket origin unless `VITE_BACKEND_URL` was supplied at build time.

## Starter packages

The starter follows Ontology's separation of project setup and representation contributions:

| Package | Responsibilities |
| --- | --- |
| `hed.starter` | `HedDomainConfiguration` connects EMF, validation, locale and image paths to Sirius Web; `HedDomainIcons` supplies shared pictograms. |
| `hed.starter.project` | `ProjectTemplates` advertises the available project templates; `ProjectInitializer` creates their semantic content; `ProjectNames` resolves the native project name for the explorer root. |
| `hed.starter.representations` | `CatalogDropService` shares catalog-to-rail creation and validation between explorer and diagram drops. |
| `hed.starter.representations.modelexplorer` | `ViewContribution`, `HedJavaServices`, `ExplorerMetadata` and `ExplorerRepresentations` describe the explorer and its saved representations; `HedDomainEditing` and `HedDomainLabels` provide editing and label services; `HedInitialDirectEditTreeItemLabelProvider` supplies the stored name when renaming an appliance. |
| `hed.starter.representations.details` | `HedPropertiesDescriptionRegistryConfigurer` builds the properties pages. |
| `hed.starter.representations.tables` | `HedTableDescriptions` builds equipment, circuit, board and catalog tables. |
| `hed.starter.representations.diagrams.floorplan` | Storey floor plans, background-image assignment and existing-equipment placement. |

The domain configuration remains in the starter because it also registers Sirius Web editing-context services, image paths and application locale. The model and edit modules remain independent of Spring and Sirius Web.

## First modeling workflow

1. Open the application and create an **Empty dwelling** or **Sample dwelling** project.
2. Select an element in the explorer to edit its properties. Expand nodes individually or use **Expand all**.
3. Use contextual **Create** actions for the default catalog categories, or **New object** to choose a compatible catalog type, including a user-defined type.
4. Create a distribution board from **Electrical service area (GTL)**, a DIN rail within it, then modular appliances directly on that rail. Creation tools respect the remaining rail capacity. An occupied rail cannot be deleted.
5. Create terminal equipment under a room. In Properties, select a supply circuit without branches or choose a branch to derive its circuit. Final circuits contain their branch objects; equipment remains owned by the dwelling.
6. Configure circuit source boards, protective devices and functional controls through reference selectors. Clear an incompatible protection before changing a circuit's source board.
7. Rename through the Name property or the explorer, and delete through the explorer. Reopen the project to continue editing the persisted model.

The explorer root displays the native Sirius Web project name above Dwelling, Electrical service area (GTL) and Device catalog. Dwelling contains storeys/rooms, unlocated equipment and functional controls; the GTL contains the service connection, boards and circuits. The catalog groups distribution board device types separately from terminal equipment types. These navigation nodes do not change semantic ownership. Modular appliances appear under their rails. See [the hierarchy and creation contexts](model-explorer.md). Specialized tables and diagrams are introduced in separate increments.

Modular specifications are editable on catalog types and displayed read-only on instances. Each equipment or circuit has one Name; modular appliances also have Label text. The properties view exposes domain validation messages and the native validation service uses the same domain rules.

The [Material Design Icons](icons.md) are served locally and shared by project templates, the explorer, creation tools and semantic reference selectors. The navigation icon returns to the home page through a React `Link` to `/` and a translated tooltip.

## Source formatting

Use UTF-8, LF line endings and a final newline, as declared in `.editorconfig`. Java and XML files use four spaces per indentation level.

Format Java classes with one statement per line and the Sirius formatter profile (200-column code target). Always use braces for `if`/`else` and loop bodies, including single statements; retain conventional `else if` chains. Expand XML elements on separate lines with indentation that reflects their nesting, including Maven POMs and settings examples. Apply these conventions when introducing or modifying files in subsequent increments.

Name public AQL services with a leading verb and the `Hed` qualifier: `getHedChildren`, `hasHedChildren`, `isHedSelectable`, `canCreateHedObject` and `createHedObject`, for example. Keep Java callers and AQL expressions consistent with these names.

Document each new class, public constructor and public method that does not override or implement an inherited contract with Javadoc. Explain its purpose and document parameters, return values and rejection conditions where applicable. Add comments for non-obvious decisions or invariants. Apply this convention to new work; previously reviewed classes can be documented in a separate pass.

## Protection relationships

Select an RCCB in Model Explorer and use **Upstream service circuit-breaker** in Properties to connect it to the main service breaker. The service breaker's **Devices directly downstream** selection is the inverse relationship and updates automatically. An RCCB also displays its directly supplied modular devices as a read-only list, calculated from their **Upstream RCCB** assignments.

MCBs reference their upstream RCCB; circuits reference their overcurrent protective device. A modular device can have either an upstream RCCB or a direct service-breaker supply. Clear the current assignment before selecting the other supply. Domain validation rejects conflicting assignments and cycles.

## Circuit phase visibility

Circuit phase controls are available only when the dwelling's service connection explicitly uses a three-phase supply. The Circuit Table uses the same condition for its Phase / supply column. Missing, unknown and single-phase supply configurations hide both presentations while retaining the recorded circuit assignments. The service connection's own selector remains visible so the supply can always be changed.

The properties condition and the View DSL service share `HedDomain.hasThreePhaseSupply`. The table keeps its column description in place and returns no semantic candidates when the column is hidden, preserving the identifiers of existing representations.

## Storey floor plans

Create **Floor Plan** on a Storey from Model Explorer. The [floor-plan workflow](floor-plan.md) describes background-image import, 24 × 24 pictograms, optional outside labels and saved placement. The Java contribution lives in `representations.diagrams.floorplan`; its image action validates project ownership before mutation. Existing table and panel description positions are retained by appending the new description.

## Languages

English is the default. For example, in PowerShell, enable French before launching the backend:

```powershell
$env:HED_LOCALE = 'fr_FR'
java -jar backend/hed-application/target/hed-application-0.1.0-SNAPSHOT.jar
```

Domain labels and messages are in the model's `i18n/hed-messages*.properties`. Frontend additions use `i18n/{en,fr}/home-electrical-designer.json` through Sirius Web's namespace discovery.

A new project's default catalog names use its creation language. There is only one editable name per type. Stored catalog, equipment and circuit names remain unchanged when the application language changes. New equipment takes the current name of its selected catalog type. The sample also initializes its storeys, rooms, boards, circuits, equipment, channels, controls and label text in that language. Those names remain ordinary editable example data afterward.

## Validation

`mvn -s settings.xml.example -Papplication verify` runs the domain and starter integration tests. The starter suite checks native Sirius JSON serialization, catalog and protection references, branch assignments, explorer projection, room/rail creation, stale creation-dialog rejection, read-only catalog specifications, stable view identifiers across languages, local category icon resolution, table filtering/numeric sorting, rejected cell edits and catalog-owned board ratings.

Browser checks cover project creation, catalog editing, native object creation, circuit assignment, renaming, deletion, reopening and French/English behavior. They use a locally installed Chrome and create and delete only their own uniquely named test project. For a running French instance on port 8081:

```powershell
$env:HED_BASE_URL = 'http://localhost:8081'
$env:HED_TEST_LOCALE = 'fr_FR'
npm --prefix frontend test -- bootstrap.spec.ts tables.spec.ts explorer-representations.spec.ts upstream-protection.spec.ts explorer-reordering.spec.ts explorer-labels.spec.ts explorer-hierarchy.spec.ts catalog-drop.spec.ts terminal-catalog-drop.spec.ts rail-capacity.spec.ts circuit-phase.spec.ts floor-plan.spec.ts floor-plan-images.spec.ts floor-plan-highlight.spec.ts floor-plan-removal.spec.ts panel.spec.ts
```

The protection workflow checks service-breaker inverse links, the read-only RCCB downstream list and persistence after reopening.

`CircuitPhaseVisibilityTest` checks both circuit kinds and the always-visible supply selector. The bilingual `circuit-phase.spec.ts` workflow checks missing/unknown/single/three-phase supplies, live table-column updates and retention of hidden L2/L3 assignments across reopening.

`ExplorerPlanPlacementTest` checks the native filter’s default-off scope, closed/live plan accounting, disabled-query avoidance, independent render snapshots and propagation to rooms, storeys and the dwelling. The bilingual `floor-plan-highlight.spec.ts` checks collapsed parents, clearing their highlight after the last placement, toggling without hiding items, two explorer tabs, multiple plans, last-placement deletion, room reassignment and reset to the default after reopening.

The bilingual `floor-plan-removal.spec.ts` checks the view-only Delete confirmation, cancellation, retained devices and circuit/room relationships, placement again after removal, and the standard semantic deletion warning in Model Explorer. The adaptation uses the public confirmation context behind Sirius Web 2026.9.0's [native deletion-confirmation hook](https://github.com/eclipse-sirius/sirius-web/blob/v2026.9.0/packages/core/frontend/sirius-components-core/src/modals/confirmation/useConfirmationDialog.tsx), without replacing tool execution.

The bilingual `terminal-catalog-drop.spec.ts` workflow creates outlets, controls and loads by dropping their catalog types onto a room, checks localized names and control channels, rejects invalid drops and places the resulting device on a storey Floor Plan. `CatalogDropTest` checks whole-selection validation before any room equipment is created.

The bilingual `rail-capacity.spec.ts` workflow verifies that the native New Object dialog remains available for a compact catalog variant when generic types no longer fit, then hides creation once the rail is full. It also checks drop rejection and persisted fractional module widths. `RailCapacityTest` covers menu/dialog consistency, stale choices and missing or incompatible catalog types.

`CircuitDiagramTest` evaluates the circuit diagram's actual AQL descriptions against complete and incomplete models, including independent control channels, optional actuators, three-phase labels and circuit–branch–device supply paths. `BranchIntegrationTest` checks stable occurrence identifiers, semantic selection/rename/delete and atomic branch drops. The bilingual `circuit-branches.spec.ts` covers branch creation, associations, shared room/branch occurrences, circuit changes, deletion and persisted identities. The bilingual `circuit-diagram.spec.ts` checks native containment, supply/control links, existing pictograms, manual movement/resizing, saved layouts and live circuit/room edits. See the [circuit-diagram workflow](circuit-diagram.md).

`CircuitEquipmentPropertiesTest` checks the final circuit's native multi-select: editable direct assignments without branches, a live read-only union with branches, rejection of stale or foreign selections, and return to direct editing after the last branch is removed. `BranchIntegrationTest` also verifies transfer of direct members into the first explorer-created branch. The bilingual `optional-circuit-branches.spec.ts` exercises both modes, first-branch transfer, redistribution between branches, deletion, reopening and independent room/supply relationships.

`RoomEquipmentMoveTest` and `RoomEquipmentDropTest` check room reassignment without cloning, preserved ownership and circuit/branch/control links, multi-device validation before mutation, localized rejection and floor-plan storey eligibility. The bilingual `room-equipment-move.spec.ts` moves an outlet, a luminaire and a switch through the native explorer, checks stable identity and connections, retains same-storey placement and reopens the saved room assignments.

The [panel diagram workflow](distribution-board-layout.md) checks SVG alignment, module widths, drag-and-drop transfers and reopening of the constrained layout.

The [table workflow](tables.md) also checks all four representation contexts and persistence of table edits.

Use `HED_TEST_LOCALE=en_GB` for an English backend. Use a separate database when validating persistence across application restarts so these checks remain independent of existing projects.

The project is distributed under EPL-2.0. Model, edit, starter and application artifacts include the project license and notice; the MDI assets retain their upstream notices. Vite also emits `assets/third-party-licenses.md` alongside the frontend to preserve the license texts of bundled dependencies, including the Roboto font.

## Frontend unit tests

The targeted frontend suite uses [Vitest with V8 coverage](https://vitest.dev/guide/coverage.html) and [React Testing Library](https://testing-library.com/docs/react-testing-library/intro/). It tests HED's custom diagram behavior with controlled Sirius/React Flow/server boundaries; the separate bilingual Playwright suite verifies the running application.

After installing the locked frontend dependencies, run from `frontend`:

```sh
npm run test:unit
npm run test:coverage
npm run test:unit:watch
```

Use the first two commands for single runs and the last for interactive development. `npm test` continues to mean Playwright. The test fixtures and cases live in `frontend/tests/`; test discovery excludes `e2e/`. Both TypeScript and Prettier check the unit tests during `npm run build`.

The coverage report includes all source files in `src/representations/`, with unexecuted files counted as uncovered. Startup wiring in `main.tsx`, CSS, tests and external dependencies are outside the measured baseline. Open `coverage/frontend/index.html` at the repository root for the HTML report; JUnit results are under `test-results/frontend-unit/`. These reports are independent of Java and browser reports and do not require an application or database. See [CI reporting and scope](continuous-integration.md#frontend-tests-and-coverage).

## User documentation

The illustrated guides live in `doc/user/en/index.adoc` and `doc/user/fr/index.adoc`, and can be read directly in the repository. This follows [Ontology's repository user guide](https://github.com/ObeoNetwork/ontology/blob/main/doc/user.adoc). [SysON's documentation](https://github.com/eclipse-syson/syson/tree/main/doc) also uses AsciiDoc, organized into Antora user, installation and developer modules; HED currently keeps one guide per language for its smaller scope.

The pinned `@asciidoctor/core` build dependency converts both guides into HTML after Vite builds the frontend. Local screenshots, the stylesheet and its MIT license are packaged under `static/help/` in the executable application. Remote web fonts are disabled, and conversion errors fail the build. Run `npm --prefix frontend run build-docs` to regenerate the guides after a frontend build; `npm --prefix frontend run build` and the Docker source build include this step automatically.

The native `navigationBarMenuHelpURLExtensionPoint` points to `/api/hed/help`, outside Sirius Web's frontend-route forwarding. `hed.starter.help.UserGuideController` redirects to the French or English static guide using the same injected application locale as domain contributions. The development proxy forwards `/help` assets to the backend alongside its existing `/api` proxy. Each guide links to the other language without changing project data. The browser help test checks the actual native menu, locale redirect, all local screenshots and language switching.

## Automated formatting checks

The Java formatter and cleanup profiles come from Sirius Web 2026.9.0. Maven runs formatting-focused Checkstyle rules in `validate`. The code-generation tool can apply or check the exact Sirius Java profile as described in the repository README. Java imports must be explicit; static wildcard imports are rejected too.

The frontend uses Sirius Web's Prettier 2.7.1 configuration (two spaces, 120 columns). Run `npm run format` to apply it or `npm run format-lint` to check it. The frontend build starts with this check, including TSX, CSS, TypeScript configuration and browser tests. The formatting workflow checks both Java and frontend files without private package credentials.
