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

Jobs run on Ubuntu 24.04 with Temurin Java 21 and Maven 3.9.9. The frontend uses Node 22.16.0. The shared Java/Maven action downloads Maven from Apache and checks a pinned SHA-512 checksum. External actions are pinned to commit hashes, with version comments for maintenance. Maven and npm dependency caches speed up subsequent runs; model, formatter and full-application Maven caches use separate dependency fingerprints.

The build does not require a database: these Java tests run without starting the application. Browser workflows, code coverage, container publication and deployment are separate steps in the CI rollout.

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

Formatting and domain verification run without repository secrets. The full application job is explicitly skipped for pull requests from forks and Dependabot because repository package credentials are unavailable to them. A summary explains this limitation; a skipped job does **not** establish that the application builds successfully.

Before merging such a contribution, a maintainer must review its code, copy the reviewed commits to a branch in this repository, and run **Build** manually on that branch. The full application build must pass for the reviewed code that will be merged. Review build scripts and dependency changes before allowing them access to the package token.

Workflows use `pull_request`, never `pull_request_target`, to build contribution code. See GitHub's documentation on [secrets in fork workflows](https://docs.github.com/en/actions/how-tos/write-workflows/choose-what-workflows-do/use-secrets) and [secure workflow use](https://docs.github.com/en/actions/reference/security/secure-use).

## Reports and application artifact

Open a workflow run under **Actions** to inspect each step and download its artifacts:

- **domain-test-reports** contains the model's Surefire XML and text reports.
- **application-test-reports** contains all available Java test reports, including starter tests. Reports are uploaded even when verification fails; an earlier compilation or dependency failure may produce no reports.
- **hed-application-&lt;commit SHA&gt;** contains the executable `hed-application-0.1.0-SNAPSHOT.jar`. It is uploaded only after the full build succeeds and includes both generated user guides. This is a build artifact, not a GitHub release.

Artifacts expire after **7 days**. The JAR can be reused by subsequent browser-test jobs so the application is built once. Running it locally still requires a PostgreSQL database; see [building and running](development.md).

After the first successful GitHub runs, branch rules can require **Source formatting**, **Domain verification** and **Full application build**. Account for the explicit maintainer validation of external contributions when choosing those rules; a skipped full build is not sufficient validation.
