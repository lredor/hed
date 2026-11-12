<!--
Copyright (c) 2026 Laurent Redor.
This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0
-->

# GitHub Packages access

Sirius Web packages require a GitHub personal access token **classic**, with **read:packages**.

1. Open https://github.com/settings/tokens/new.
2. Name the token, set an expiration, select only read:packages, and generate it.
3. Store it locally; never paste it into chat or commit it.

On Windows, run in PowerShell:

```powershell
$jeton = Read-Host "GitHub token" -AsSecureString
[Environment]::SetEnvironmentVariable(
    "GH_PACKAGES_TOKEN",
    [System.Net.NetworkCredential]::new("", $jeton).Password,
    "User"
)
```

The build script reads this user variable directly, so an already-running Codex session does not need to restart.

From the repository root:

```powershell
.\scripts\Build.ps1
```

For Docker, load the variable into the current shell first:

```powershell
$env:GH_PACKAGES_TOKEN = [Environment]::GetEnvironmentVariable("GH_PACKAGES_TOKEN", "User")
docker compose up --build -d
```

A 401 or 403 response means the credentials, token scope, expiration, or package access need checking.

For GitHub Actions, configure the repository secret and optional username variable described in [continuous integration](continuous-integration.md#github-packages-configuration). The workflow can also use its automatic token when upstream package access permits it.

[Official npm registry authentication documentation](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-npm-registry)
