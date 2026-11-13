<!--
Copyright (c) 2026 Laurent Redor.
This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0
-->

# Docker launch

For a first installation without development experience, use the [English installation walkthrough](user/en/index.adoc#installation) or [procédure en français](user/fr/index.adoc#installation). They explain Docker Desktop, downloading the standalone launcher, startup, backups and updates without build credentials.

## Ready-to-run public image

After the maintainer's first successful publication and change of package visibility to **Public**, users need only `compose.run.yaml`:

```sh
docker compose -p hed -f compose.run.yaml pull
docker compose -p hed -f compose.run.yaml up -d --wait
```

This independent Compose file has no `build` or `secrets` section. It downloads `ghcr.io/lredor/hed:latest` and PostgreSQL 17, binds only HTTP port 8080 to localhost and retains data in the `hed-postgres-data` volume for the selected project name. Its application image has a readiness health check and runs as user 10001. The image is Linux amd64; the launcher explicitly requests that platform, so ARM hosts require emulation.

Set `HED_LOCALE=fr_FR` in `.env` for French. `HED_IMAGE` can select an already published `sha-<full-commit-id>` tag or image digest. `latest` follows validated `master` commits, not numbered stable releases. Updating with `pull` then `up -d --wait` preserves the volume. Back up projects before updates.

For an existing source installation, retain its Compose project name (`-p`), database password and volume. Changing the project name would select a different database. The source and runtime launchers use the same volume key to support this transition. Do not replace the data volume or reset its password as part of switching launchers.

See [publication and the one-time public visibility setting](continuous-integration.md#container-image-publication).

## Developer source build

Docker must be running with Linux containers. Set GH_PACKAGES_TOKEN as described in [GitHub Packages access](github-packages.md).

Optionally copy .env.example to .env and adjust the database password or locale. Default configuration is for local development.

```sh
docker compose up --build -d
docker compose ps
```

Open http://localhost:8080 after the application is healthy.

The Dockerfile builds everything from source (including the frontend formatting check and backend Checkstyle rules): Node compiles the frontend, its static resources are copied into the backend, Maven builds/tests the Java modules, and the final image contains the executable application on Java 21. No prebuilt local JAR is needed.

The package token is passed as a BuildKit secret to the build stages. It is not copied into the final image. PostgreSQL 17 and the application have separate health checks. PostgreSQL data is stored in a named volume. Ports 8080 and 5437 are bound to localhost.

## Persistence check

1. Create the Sample dwelling project and rename an element.
2. Run `docker compose down`.
3. Run `docker compose up -d`.
4. Reopen the project and verify the change.

Normal `down` preserves the volume. Do not add `--volumes` unless deliberately deleting the database.

Set HED_LOCALE=fr_FR and recreate the application container to activate French. The database and user-entered model names are preserved.

The build's base images fix the toolchain major versions and Maven/Node versions. For a released deployment, resolve and record image digests alongside the release.

Official references: [Docker build secrets](https://docs.docker.com/build/building/secrets/) and [Compose volumes](https://docs.docker.com/reference/compose-file/volumes/).
