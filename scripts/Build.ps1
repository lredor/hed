# Copyright (c) 2026 Laurent Redor.
# This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v2.0
# which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0

param([switch]$SkipFrontend)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
if (-not $env:GH_PACKAGES_TOKEN) {
    $env:GH_PACKAGES_TOKEN = [Environment]::GetEnvironmentVariable('GH_PACKAGES_TOKEN', 'User')
}
if (-not $env:GH_PACKAGES_TOKEN) {
    throw 'Configure GH_PACKAGES_TOKEN with a GitHub classic token having read:packages. See doc/github-packages.md.'
}
if (-not $env:GITHUB_ACTOR) {
    $env:GITHUB_ACTOR = 'lredor'
}
Push-Location $projectRoot
try {
    if (-not $SkipFrontend) {
        Push-Location frontend
        try {
            npm.cmd ci
            if ($LASTEXITCODE -ne 0) {
                throw 'Frontend dependency installation failed.'
            }
            npm.cmd run build
            if ($LASTEXITCODE -ne 0) {
                throw 'Frontend build failed.'
            }
        } finally {
            Pop-Location
        }
    }
    mvn.cmd -B -s settings.xml.example -Papplication verify
    if ($LASTEXITCODE -ne 0) {
        throw 'Application build failed.'
    }
} finally {
    Pop-Location
}
