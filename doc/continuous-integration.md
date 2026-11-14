<!--
Copyright (c) 2026 Laurent Redor.
This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0
-->

# Continuous integration

GitHub Actions checks pushes to `master` and pull requests targeting `master`. Build and CheckFormatting can also be started from **Actions → Run workflow**, choosing a branch. A newer run cancels an obsolete run for the same workflow and pull request or branch. The separate Release workflow prepares drafts manually and publishes numbered images after a user publishes a release.

## Checks

| Workflow / job | What it verifies |
| --- | --- |
| **CheckFormatting / Source formatting** | The Sirius Java formatter and Prettier checks for TypeScript, TSX, CSS, frontend unit/browser tests, build scripts and HTML. This job does not need GitHub Packages credentials. |
| **Build / Domain verification** | `mvn -B -ntp verify`: Checkstyle, model/edit compilation and domain tests, using public dependencies. Also tests coverage summary reporters and release validation helpers with Python's standard library. This job runs for every contribution. |
| **Build / Full application build** | Locked frontend dependency installation, Prettier, TypeScript checking including unit tests, Vite assets, both user guides, `npm run test:coverage`, and `mvn -B -ntp -s .github/maven-settings.xml -Papplication,coverage clean verify`. Vitest reports targeted frontend coverage; JaCoCo aggregates Java-test coverage across modules. The application profile includes starter tests and packages the executable JAR. A final check verifies that it contains the frontend and both guides. |
| **Build / Playwright (English)** | All browser workflows against the packaged application started with `HED_LOCALE=en_GB`, a fresh PostgreSQL 17 service and an English Chrome locale. |
| **Build / Playwright (French)** | The same browser workflows with `HED_LOCALE=fr_FR`, a separate fresh database and a French Chrome locale. |
| **Build / Container image** | Builds `Dockerfile.runtime` from the same verified JAR, starts it with PostgreSQL through `compose.run.yaml` without package credentials, checks readiness and both guides, and uploads the tested image. Runs on the same trusted builds as the application, including repository PRs. |
| **Build / Publish Docker image** | Publishes that tested image only after domain, application, both browser jobs and container checks succeed, and only on pushes to `lredor/hed` master. |
| **Release / Verify release source and tested image** | Requires consistent stable project versions, an existing tag on master, successful Build and CheckFormatting push runs on that commit, all required Build jobs and the publisher's immutable image digest. |
| **Release / Prepare draft release** | On manual request, creates or updates a draft with changelog notes, a numbered-version installer, provenance manifest and checksums. |
| **Release / Publish numbered image** | After the user publishes the release, verifies its assets, promotes the tested digest without rebuilding, refuses conflicting version tags and checks an anonymous pull. |

Jobs run on Ubuntu 24.04 with Temurin Java 21 and Maven 3.9.9. The frontend uses Node 22.16.0. The shared Java/Maven action downloads Maven from Apache and checks a pinned SHA-512 checksum. External actions are pinned to commit hashes, with version comments for maintenance. Maven and npm dependency caches speed up subsequent runs; model, formatter and full-application Maven caches use separate dependency fingerprints.

Java verification and coverage run without starting the application or a database. Browser verification starts its own application and PostgreSQL service per language. The container check starts a separate isolated installation. Publication does not deploy or modify any running installation.

## Container image publication

`Dockerfile.runtime` packages the already tested JAR with Java 21, OCI source/license/revision/version labels, an unprivileged runtime user and a readiness health check. It does not download Maven/npm dependencies or receive their credentials. The first distribution targets **linux/amd64**.

The **Container image** job runs on trusted PR, master and manual builds. It uses the executable artifact from the same run and commit, builds the runtime once, checks it with `compose.run.yaml`, retains startup logs as **container-logs**, and uploads **hed-image-<commit>** as a compressed image archive for one day. Its test installation is removed after the check.

The **Publish Docker image** job waits for that check and both Playwright matrix entries as well as domain/application verification. It downloads and loads that exact image rather than rebuilding it, then pushes `ghcr.io/lredor/hed:latest` and `ghcr.io/lredor/hed:sha-<full-commit-id>`. It runs only for a push to `master` in `lredor/hed`; PRs, forks and manual Build runs never publish. Build does not create a release/version tag. The workflow's existing master concurrency cancels obsolete runs.

After pushing, it retains **hed-release-image-&lt;commit SHA&gt;** for 90 days: a tiny JSON file containing the source commit, project version and immutable registry digest. The separate Release workflow uses this provenance to promote the tested image independently of the one-day Docker archive and seven-day JAR artifact. It never moves `latest`. See [the release procedure](releases.md), including draft review, asset checks, retry behavior and the later separate development-version bump.

Only the publication job requests `packages: write`, using its automatic `GITHUB_TOKEN`. There is no new PAT or registry secret to configure. Compilation retains the existing optional upstream package-read credentials in the application job. The publisher has no checkout and receives neither those credentials nor source build scripts.

After the **first publication**, the repository owner must open **GitHub profile → Packages → hed → Package settings → Change visibility**, choose **Public** and confirm GitHub's prompts. Public repository visibility alone does not make the container public. If a package with that name already exists, check that it is linked to `lredor/hed` and allows its Actions workflows to write. See [GitHub's registry documentation](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry).

Verify an anonymous pull using an empty Docker credential directory, rather than relying on a logged-in account. For example, in a temporary Bash directory:

```sh
mkdir anonymous-docker
docker --config anonymous-docker pull ghcr.io/lredor/hed:latest
```

Until the first image exists and is public, the new user installation instructions are not yet usable. Publication success does not itself verify anonymous access; the visibility change and this pull check complete the initial setup. The publication summary reminds the owner of this action. No image is published during local validation of this change.

Only the `application` profile declares GitHub Packages repositories. Model/edit builds resolve public dependencies from Maven Central. Checkstyle examines Java production and test sources only: it skips resource directories, including the repository root used to package license files. Its Java-only rules therefore do not traverse npm dependencies or local caches.

## GitHub Packages configuration

The full build needs access to Sirius Web's Maven and npm packages. Its automatic `GITHUB_TOKEN` has only `contents: read` and `packages: read` permissions. The workflow uses it when no package token is configured. The automatic token works only if this repository can read the upstream packages; public visibility alone does not guarantee that access.

If dependency installation reports HTTP 401/403, configure a classic personal access token with **read:packages** and access to the required upstream packages:

1. Open this repository's **Settings → Secrets and variables → Actions**.
2. Under **Secrets**, create a repository secret named **GH_PACKAGES_TOKEN**, containing the token. The workflow will use it instead of the automatic token for both npm and Maven.
3. Under **Variables**, set **GH_PACKAGES_USERNAME** to the token owner's GitHub login if it differs from the repository owner. It defaults to the repository owner, currently `lredor`. This is an ordinary variable, not a secret.
4. Rerun the failed build from **Actions**.

The CI-specific Maven settings use these two variables. They do not reuse `GITHUB_ACTOR` as the package login, since a workflow can be triggered by someone other than the token owner. Local build and Docker credentials retain their existing configuration; see [GitHub Packages access](github-packages.md).

Only the dependency installation and Maven build steps receive the package token. Do not add tokens to workflow files, logs, reports or artifacts. See the official documentation for [Maven registry authentication](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry).

## Fork and Dependabot pull requests

Formatting and domain verification run without repository secrets. The full application job is explicitly skipped for pull requests from forks and Dependabot because repository package credentials are unavailable to them. Its dependent Playwright and container-image jobs are skipped as well. A summary explains this limitation; a skipped job does **not** establish that the application builds or its browser workflows pass.

Before merging such a contribution, a maintainer must review its code, copy the reviewed commits to a branch in this repository, and run **Build** manually on that branch. The full application build and both Playwright jobs must pass for the reviewed code that will be merged. Review build scripts and dependency changes before allowing them access to the package token.

Workflows use `pull_request`, never `pull_request_target`, to build contribution code. See GitHub's documentation on [secrets in fork workflows](https://docs.github.com/en/actions/how-tos/write-workflows/choose-what-workflows-do/use-secrets) and [secure workflow use](https://docs.github.com/en/actions/reference/security/secure-use).

## Bilingual browser verification

After the full application build succeeds, the two Playwright jobs run in parallel. They download the **same JAR artifact from this workflow run**, without rebuilding the application. Each job starts its own PostgreSQL 17 service and application process. Their databases and test projects are disposable and have no connection to local or deployed projects.

The backend's `HED_LOCALE`, the tests' `HED_TEST_LOCALE` and Chrome's browser locale agree: `en_GB` / `en-GB` for English and `fr_FR` / `fr-FR` for French. The job explicitly installs Google Chrome and its system dependencies because the existing Playwright configuration uses `channel: 'chrome'`.

The application must become ready at `/actuator/health/readiness` before testing starts. Startup failures and readiness timeouts fail the job and retain its log; the startup step is limited to five minutes. Tests use one worker per language, no automatic retries, and a 20-minute suite timeout within the 30-minute job limit so reporters can finish before job termination. An English failure does not cancel the French job, or conversely. Accidental `test.only` declarations fail CI.

The CI configuration writes console, HTML and JUnit reports. Screenshots and Playwright traces are retained on failure. Each language has separate output directories, which also allows local runs in both languages to keep their results independently.

To produce the same reports locally against an already-running isolated test instance, run from `frontend`:

```powershell
$env:CI = 'true'
$env:HED_TEST_LOCALE = 'fr_FR'
$env:HED_BASE_URL = 'http://localhost:8091'
npm test
npx playwright show-report ../playwright-report/fr_FR
```

The example expects the backend to be started in French. Use `en_GB` and the URL of an English test instance for the English run. Clear `CI` afterward to return to the regular local console reporter.

## Java coverage

The full application build runs [JaCoCo 0.8.16](https://github.com/jacoco/jacoco/releases/tag/v0.8.16) through the optional `coverage` Maven profile, together with `application`. The model, starter and application entry point are measured. Generated EMF classes and edit item providers under `hed/homeelectrical/**` are excluded from the report, while their tests continue to run. The edit module has no handwritten production Java after this exclusion. Tests, dependencies and the separate model generator/formatter are outside the measured production sources.

The `hed-coverage` reporting module depends on all application modules and runs last. Its [aggregate report](https://www.jacoco.org/jacoco/trunk/doc/report-aggregate-mojo.html) combines execution data from the entire reactor: a starter test that exercises a domain service contributes to that service's coverage. Measuring each module using only its own tests would miss these calls. The application entry point remains visible even if the Java tests do not exercise it.

Open **Actions → Build → a workflow run → Summary → Java coverage** to read overall and per-module percentages and covered/total counts. **Lines** measure source lines executed by Java tests. **Branches** measure outcomes of Java decisions such as `if` and `switch`; they do not refer to Git branches. A dash means there are no branches to measure. Percentages use combined counts, rather than averaging module percentages.

The **java-coverage** artifact contains the navigable HTML report and aggregate `jacoco.xml`. Extract it and open `index.html` to inspect packages, classes and highlighted source lines. Coverage is published after successful Java verification; a failed or incomplete Maven build may not reach the reporting module. A missing, malformed, incomplete or entirely unexercised report fails summary generation instead of publishing a misleading result. Coverage-summary regression checks run in Domain verification, including on fork pull requests.

Coverage currently establishes a baseline without a minimum coverage threshold or pull-request comments. Coverage indicates which code was executed, not whether all behavior was correctly asserted. The later Playwright jobs start the packaged JAR without a coverage agent and do not contribute to this Java-test report. The agent is attached only to the Java test process; it does not alter the packaged application.

For the same report locally, first build the frontend and guides as described in [development](development.md), configure the package credentials, then run from the repository root:

```sh
mvn -B -ntp -s .github/maven-settings.xml -Papplication,coverage clean verify
python tools/ci/jacoco_summary.py backend/hed-coverage/target/site/jacoco-aggregate/jacoco.xml
```

Use both profiles in the same reactor. `clean` removes old execution data and classes so the report describes the current test run. The HTML entry point is `backend/hed-coverage/target/site/jacoco-aggregate/index.html`. Without `coverage`, the regular domain and `application` builds retain their existing behavior and do not attach the agent or build the reporting module. To run the reporter tests locally, use `python -m unittest discover -s tools/ci -p 'test_*.py'`.

## Frontend tests and coverage

The full application job runs `npm run test:coverage` after building the frontend and guides, before Java verification. Vitest 5.1.0 and its matching V8 coverage provider are pinned alongside React Testing Library, user-event and jest-dom. jsdom 27.4.0 provides the DOM environment and was selected for compatibility with the workflow's Node 22.16.0. Unit tests are separate from Playwright: `npm test` still runs the existing browser workflows, while `npm run test:unit` performs a single local unit-test run and `npm run test:unit:watch` watches for edits.

The tests exercise HED's existing public representation contributions: GraphQL subscription transforms, semantic-to-graphical conversion, constrained rail layouts, fixed 24-pixel pictograms, relative positions after plan resizing, same-rail drop handling, persisted label preferences, image import errors and the view-only deletion prompt in English and French. They use the real HED translations. Sirius contexts/renderers, React Flow accessors, uploads and mutation calls are controlled test doubles; React, GraphQL parsing, Apollo document transforms, translation handling and HED's own code run normally. No application or database is started for these tests. The bilingual Playwright jobs remain the check of actual workbench/server integration.

Coverage includes **every TypeScript/TSX file under `frontend/src/representations/`**, including files not imported by the tests. It excludes `main.tsx` startup wiring, CSS, tests and dependencies such as Sirius Web. This is a targeted baseline, not a measurement of the entire web application. The HTML report shows uncovered source lines; totals include lines, branches, functions and statements. No minimum coverage threshold is enforced yet.

Open **Actions → Build → a workflow run → Summary** for **Frontend coverage** followed by **Frontend unit tests**, within the Full application build summary. The coverage section is written before the detailed test list so it is easier to find. The existing Java and bilingual Playwright sections remain separate. The summary reporter computes percentages from counts and rejects missing, malformed, unexercised or out-of-scope results. Its tests run in Domain verification without package credentials.

Reports are generated in `test-results/frontend-unit/results.xml` and `coverage/frontend/`, separate from Java and locale-specific browser reports. Available test and HTML coverage reports are retained even when Vitest fails. A setup/build failure may produce no reports; a successful test run must produce JUnit results and a valid coverage summary. Unit failures or invalid coverage reports fail the full build and prevent its dependent Playwright jobs from starting. As before, the full job is skipped on fork and Dependabot pull requests that cannot receive package credentials.

For a local run after `npm ci`, execute from `frontend`:

```sh
npm run test:coverage
```

Open `coverage/frontend/index.html` from the repository root, or summarize the same report without GitHub:

```sh
python tools/ci/frontend_coverage_summary.py coverage/frontend/coverage-summary.json
```

Vitest's configuration restricts test discovery to `frontend/tests/**/*.test.{ts,tsx}` and uses isolated jsdom environments with at most two workers. Playwright continues discovering only `frontend/e2e`. The frontend TypeScript and Prettier checks include unit tests; reports and dependency directories are ignored by Git and excluded from the Docker context. Test dependencies and fixtures are not imported by the application bundle.

## Reports and application artifact

Read test results directly in **Actions → Build → a workflow run → Summary**. The **Domain tests** and **Application tests** sections show totals, passed/failed/skipped counts, execution times, and individual tests grouped by suite. Failed tests include expandable error messages and stack traces. No download is needed to read the results.

The application results already include the domain tests: these two job summaries describe their respective executions and should not be added together to count unique tests. A failed or incomplete build explicitly warns that only available reports are shown; compilation and dependency failures can prevent tests from running.

The **Playwright (English)** and **Playwright (French)** sections show each browser workflow's result and duration directly on the same Summary page, with expandable failure details. These are functional browser checks, separate from the Java test counts. A setup failure explains that no browser tests could run.

Browser workflows explicitly select the applicable Details tab: installed-device ratings are checked read-only under **Advanced**, then rail assignments under **General**. Explorer operations locate tree occurrences and expand their owners before using contextual tools. The circuit-branch regression deliberately starts with the Circuits group collapsed, so the test also covers this state instead of relying on expansion left by an earlier action. Table visibility tests wait for the server's refresh before asserting the controlled switch state.

The circuit-completeness highlighting scenario prepares its objects with Model Explorer's native **Rename** action. Its assertions then depend on explorer state rather than on an unrelated Details subscription becoming editable. Details editing is covered by separate property workflows.

The summaries are generated even after test failures. The pinned [JUnit Report Action](https://github.com/mikepenz/action-junit-report/tree/v6.7.1) writes the native job summaries with annotations and pull-request comments disabled, so reporting needs no write permissions and also works on fork pull requests. Malformed reports and reported test failures fail the reporting step; a successful Maven run must produce test results.

Raw reports remain available as artifacts for detailed troubleshooting. Open the same workflow run to download them or the application:

- **domain-test-reports** contains the model's Surefire XML and text reports.
- **application-test-reports** contains all available Java test reports, including starter tests. Reports are uploaded even when verification fails; an earlier compilation or dependency failure may produce no reports.
- **java-coverage** contains aggregate JaCoCo HTML and XML after successful Java verification. Line and branch coverage can also be read directly on the native Summary page.
- **frontend-coverage** contains V8 HTML coverage (`index.html`), LCOV (`lcov.info`) and `coverage-summary.json` for HED representation code. Available reports are retained even after a unit-test failure; the native summary is published after successful unit tests.
- **frontend-test-reports** contains the Vitest JUnit report. Individual results and failure details also appear under **Frontend unit tests** on the native Summary page.
- **hed-application-&lt;commit SHA&gt;** contains the executable `hed-application-<project version>.jar` (`hed-application-0.1.0.jar` for the first release). It is uploaded only after the full build succeeds and includes both generated user guides. This is a build artifact, not a GitHub release.
- **playwright-en_GB** and **playwright-fr_FR** contain each language's HTML report, JUnit XML, available failure screenshots/traces and application log. The native Summary page provides the results without downloading these archives. To investigate a trace, extract the archive and run `npx playwright show-trace <path-to-trace.zip>` from `frontend`; use `npx playwright show-report <path-to-report-folder>` for the HTML report.

Artifacts expire after **7 days**. The JAR is reused by both browser jobs so the application is built once. Running it locally still requires a PostgreSQL database; see [building and running](development.md).

After the first successful GitHub runs, branch rules can require **Source formatting**, **Domain verification**, **Full application build**, **Playwright (English)** and **Playwright (French)**. Account for the explicit maintainer validation of external contributions when choosing those rules; skipped application and browser jobs are not sufficient validation.
