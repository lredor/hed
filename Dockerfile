# syntax=docker/dockerfile:1.7

# Copyright (c) 2026 Laurent Redor.
# This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v2.0
# which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0

FROM node:22.16.0-bookworm-slim AS frontend
WORKDIR /workspace
COPY .editorconfig ./
COPY frontend/ frontend/
WORKDIR /workspace/frontend
RUN --mount=type=cache,target=/root/.npm \
    --mount=type=secret,id=github_packages_token,required=true \
    export GH_PACKAGES_TOKEN="$(cat /run/secrets/github_packages_token)" && \
    npm ci && npm run build

FROM maven:3.9.9-eclipse-temurin-21 AS backend
WORKDIR /workspace
COPY pom.xml settings.xml.example LICENSE NOTICE .editorconfig ./
COPY backend/ backend/
COPY tools/style/ tools/style/
COPY --from=frontend /workspace/backend/hed-application/src/main/resources/static/ backend/hed-application/src/main/resources/static/
ARG GITHUB_ACTOR=lredor
RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=secret,id=github_packages_token,required=true \
    export GH_PACKAGES_TOKEN="$(cat /run/secrets/github_packages_token)" GITHUB_ACTOR="$GITHUB_ACTOR" && \
    mvn -B -s settings.xml.example -Papplication verify

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -g 10001 hed && adduser -D -u 10001 -G hed hed
WORKDIR /app
COPY --from=backend /workspace/backend/hed-application/target/hed-application-0.1.0-SNAPSHOT.jar app.jar
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
