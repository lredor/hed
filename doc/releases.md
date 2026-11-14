<!--
Copyright (c) 2026 Laurent Redor.
This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0
-->

# Preparing and publishing a release

HED releases have a stable tag such as `v0.1.0`, release notes, a version-pinned Docker Compose installer and a numbered public Docker image such as `ghcr.io/lredor/hed:0.1.0`.
The release promotes the exact image already built and tested on `master`; it does not rebuild the application. `latest` continues to follow tested `master` pushes and is not moved by the release workflow.

## 1. Merge the preparation PR

Prepare a branch and PR with the intended stable version in the root Maven POM, all five backend parent references and the separate `tools/codegen/pom.xml`.
Use the same version in `frontend/package.json` and both project version entries in `frontend/package-lock.json`. Dependencies and the Ecore namespace are independent of the application version and must not be changed just for a release.
Write the corresponding `## 0.1.0` section in [CHANGELOG.md](../CHANGELOG.md).

After reviewing and merging the PR, wait for **Build** and **CheckFormatting** on the resulting `master` commit.
All Build jobs must succeed, including both Playwright languages, **Container image** and **Publish Docker image**. A successful PR run alone is insufficient: it does not publish the source image.
The package `ghcr.io/lredor/hed` must already be public. The workflow uses the automatic `GITHUB_TOKEN`; no additional personal token is needed for release publication.

## 2. Tag that exact commit

Copy the full commit identifier from the successful master run. From an up-to-date local repository, use these commands, replacing `FULL_VALIDATED_COMMIT_SHA` with that identifier:

```sh
git fetch origin master --tags
git tag -a v0.1.0 FULL_VALIDATED_COMMIT_SHA -m "Home Electrical Designer 0.1.0"
git push origin v0.1.0
```

Do not move or replace an existing release tag. Tag creation alone does not publish an image or a release.

## 3. Prepare the draft in GitHub Actions

Open [Actions → Release](https://github.com/lredor/hed/actions/workflows/release.yml), choose **Run workflow**, keep the workflow branch on `master`, enter `v0.1.0` and run it.

The workflow verifies the tag, all project versions, ancestry on `master`, successful Build and CheckFormatting push runs on that exact commit, and every required Build job.
It downloads the image digest recorded by the publisher, then prepares a **draft** under [Releases](https://github.com/lredor/hed/releases) with these assets:

- `compose.run.yaml`: the installer defaults to `ghcr.io/lredor/hed:0.1.0`, while retaining the optional `HED_IMAGE` override.
- `release-manifest.json`: version, tag, source commit, immutable registry digest and original Build run URL.
- `SHA256SUMS.txt`: SHA-256 checksums of the installer and manifest.

The draft notes come from the matching changelog section and link to both installation guides.
This step does not publish the release or numbered image. You may rerun it to refresh an existing draft; it refuses to replace a published release.

## 4. Install and manually test the draft

Test the release candidate yourself before publishing the draft. The numbered image, such as `ghcr.io/lredor/hed:0.1.0`, does not exist yet: it is created only after publication.
Use the immutable `image` reference from the draft's `release-manifest.json` instead. It identifies the exact image that will receive the numbered tag, without rebuilding.

Download `compose.run.yaml` and `release-manifest.json` from the draft into a new, separate test folder. Inspect the manifest's version, source commit and original Build run URL.
Create `compose.test.yaml` in that folder with the following content:

```yaml
services:
  application:
    ports: !override
      - "127.0.0.1:8090:8080"
```

The override replaces the normal port mapping, so the test installation can run alongside HED on port 8080. Use a recent Docker Compose version that supports `!override`.
The distinct project name `hed-release-test` below gives this installation its own database volume; do not reuse your usual installation's project name.

Open a new PowerShell window in the test folder and run:

```powershell
$release = Get-Content .\release-manifest.json -Raw | ConvertFrom-Json
$env:HED_IMAGE = $release.image
$env:HED_LOCALE = "fr_FR"

docker compose -p hed-release-test -f compose.run.yaml -f compose.test.yaml up -d --pull always --wait --wait-timeout 180
```

`HED_IMAGE` overrides the installer's not-yet-published numbered tag with the manifest's `ghcr.io/lredor/hed@sha256:...` reference. No GitHub token or compilation is required.
Use `en_GB` instead of `fr_FR` to test in English. These environment settings apply only to this PowerShell window.

Open [http://localhost:8090](http://localhost:8090) and check the main workflows:

- Create a sample project, check its language, edit an object, return home and reopen the project to verify persistence.
- Exercise the explorer, catalog drag and drop, properties, tables, distribution-board layout and floor plans; open the bundled user guide.
- Import an exported copy of a representative existing project and check its data and representations. The experimental Circuit Diagram may require adaptation in future versions.

Test with copies of your projects; keep your normal installation and its database separate. If a problem is found, investigate it before publishing the candidate.
When finished, stop the test installation from the same PowerShell window:

```powershell
docker compose -p hed-release-test -f compose.run.yaml -f compose.test.yaml down
```

This preserves the test database volume for another session. Close this PowerShell window afterward so its test-only `HED_IMAGE` and language settings are not reused for another installation.
Once the manual checks pass, continue with publication: the tested digest will receive the numbered tag.

## 5. Review and publish

Inspect the draft notes and download its installer. Check that the version, source commit and original CI run are correct.
Then click **Publish release** in GitHub. Keep it a stable release, not a prerelease.
This user action triggers **Release / Publish numbered image**: the workflow verifies the published assets, promotes the tested digest to `ghcr.io/lredor/hed:0.1.0`, checks its digest and downloads it without registry credentials.

Wait for that workflow to succeed before announcing the installation link. The release page appears before the numbered image is available.
Assets are attached while the release is still a draft, which also supports repositories with immutable releases enabled. Publication does not try to edit published assets.

If promotion fails, read the failed step and rerun the failed jobs after fixing the cause. Reruns accept an existing version only when it already points to the expected digest; an existing numbered image with another digest is never intentionally overwritten.
Retain the version tag and GHCR digest. The tiny `hed-release-image-<commit SHA>` provenance artifact is retained for 90 days. Prepare and publish within this period; if it expires, rerun the original master Build run and prepare the draft again before publication. A rebuilt image may have a different digest, so an already published release must not be regenerated this way.

## Installing a numbered version

Download `compose.run.yaml` from the desired [release](https://github.com/lredor/hed/releases), save it in your permanent HED installation folder, and follow the [English](user/en/index.adoc#installation) or [French](user/fr/index.adoc#installation) guide.
No build tools or GitHub token are needed. Keep the same Compose project name and database volume when updating an existing installation, and back up projects first.
The release installer selects its numbered version by default. An existing `HED_IMAGE` entry in `.env` overrides this choice: remove it or set `HED_IMAGE=ghcr.io/lredor/hed:0.1.0` deliberately.

## Starting the next development cycle

After release 0.1.0 has been published and checked, make a **separate PR** that changes the seven Maven project versions to `0.2.0-SNAPSHOT` and the frontend project and lockfile versions to `0.2.0` (npm uses semantic versions, without Maven's SNAPSHOT convention).
Add an `Unreleased` section to the changelog. The source Dockerfile selects the application JAR independently of its version; `compose.yaml` labels locally built images `home-electrical-designer:dev`.
Keep the released `v0.1.0` tag, release assets and numbered Docker image unchanged. New validated master pushes update `latest` and their commit-specific tags.

The preparation PR for 0.1.0 does not create this later version bump, a tag or an actual release.
