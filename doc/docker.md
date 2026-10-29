<!--
Copyright (c) 2026 Laurent Redor.
This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0
-->

# Docker launch

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
