<!--
Copyright (c) 2026 Laurent Redor.
This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0
-->

# Continuous integration

GitHub Actions checks pushes to `master` and pull requests targeting `master`. Both workflows can also be started from **Actions → Run workflow**, choosing a branch. A newer run cancels an obsolete run for the same workflow and pull request or branch.

## Checks

| Workflow / job | What it verifies |
| --- | --- |
| **CheckFormatting / Source formatting** | The Sirius Java formatter and Prettier checks for TypeScript, TSX, CSS, frontend build scripts and HTML. This job does not need GitHub Packages credentials. |
| **Build / Domain verification** | `mvn -B -ntp verify`: Checkstyle, model/edit compilation and domain tests, using public dependencies. This job runs for every contribution. |
| **Build / Full application build** | Locked frontend dependency installation, Prettier, TypeScript checking, Vite assets, both user guides, and `mvn -B -ntp -s .github/maven-settings.xml -Papplication verify`. The application profile includes the starter tests and packages the executable Spring Boot JAR. A final check verifies that it contains the frontend and both guides. |
| **Build / Playwright (English)** | All browser workflows against the packaged application started with `HED_LOCALE=en_GB`, a fresh PostgreSQL 17 service and an English Chrome locale. |
| **Build / Playwright (French)** | The same browser workflows with `HED_LOCALE=fr_FR`, a separate fresh database and a French Chrome locale. |

Jobs run on Ubuntu 24.04 with Temurin Java 21 and Maven 3.9.9. The frontend uses Node 22.16.0. The shared Java/Maven action downloads Maven from Apache and checks a pinned SHA-512 checksum. External actions are pinned to commit hashes, with version comments for maintenance. Maven and npm dependency caches speed up subsequent runs; model, formatter and full-application Maven caches use separate dependency fingerprints.

Java verification runs without starting the application or a database. Browser verification starts its own application and PostgreSQL service per language. Code coverage, container publication and deployment are separate steps in the CI rollout.

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

Formatting and domain verification run without repository secrets. The full application job is explicitly skipped for pull requests from forks and Dependabot because repository package credentials are unavailable to them. Its dependent Playwright jobs are skipped as well. A summary explains this limitation; a skipped job does **not** establish that the application builds or its browser workflows pass.

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

## Reports and application artifact

Read test results directly in **Actions → Build → a workflow run → Summary**. The **Domain tests** and **Application tests** sections show totals, passed/failed/skipped counts, execution times, and individual tests grouped by suite. Failed tests include expandable error messages and stack traces. No download is needed to read the results.

The application results already include the domain tests: these two job summaries describe their respective executions and should not be added together to count unique tests. A failed or incomplete build explicitly warns that only available reports are shown; compilation and dependency failures can prevent tests from running.

The **Playwright (English)** and **Playwright (French)** sections show each browser workflow's result and duration directly on the same Summary page, with expandable failure details. These are functional browser checks, separate from the Java test counts. A setup failure explains that no browser tests could run.

The summaries are generated even after test failures. The pinned [JUnit Report Action](https://github.com/mikepenz/action-junit-report/tree/v6.7.1) writes the native job summaries with annotations and pull-request comments disabled, so reporting needs no write permissions and also works on fork pull requests. Malformed reports and reported test failures fail the reporting step; a successful Maven run must produce test results.

Raw reports remain available as artifacts for detailed troubleshooting. Open the same workflow run to download them or the application:

- **domain-test-reports** contains the model's Surefire XML and text reports.
- **application-test-reports** contains all available Java test reports, including starter tests. Reports are uploaded even when verification fails; an earlier compilation or dependency failure may produce no reports.
- **hed-application-&lt;commit SHA&gt;** contains the executable `hed-application-0.1.0-SNAPSHOT.jar`. It is uploaded only after the full build succeeds and includes both generated user guides. This is a build artifact, not a GitHub release.
- **playwright-en_GB** and **playwright-fr_FR** contain each language's HTML report, JUnit XML, available failure screenshots/traces and application log. The native Summary page provides the results without downloading these archives. To investigate a trace, extract the archive and run `npx playwright show-trace <path-to-trace.zip>` from `frontend`; use `npx playwright show-report <path-to-report-folder>` for the HTML report.

Artifacts expire after **7 days**. The JAR is reused by both browser jobs so the application is built once. Running it locally still requires a PostgreSQL database; see [building and running](development.md).

After the first successful GitHub runs, branch rules can require **Source formatting**, **Domain verification**, **Full application build**, **Playwright (English)** and **Playwright (French)**. Account for the explicit maintainer validation of external contributions when choosing those rules; skipped application and browser jobs are not sufficient validation.
