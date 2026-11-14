# Copyright (c) 2026 Laurent Redor.
# This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v2.0
# which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0

"""Prepare release assets from an exact, fully verified master build."""

import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import subprocess
import tempfile
import xml.etree.ElementTree as ET

REPOSITORY = "lredor/hed"
IMAGE = "ghcr.io/lredor/hed"
POMS = (
    "pom.xml",
    "backend/hed-model/pom.xml",
    "backend/hed-model-edit/pom.xml",
    "backend/hed-starter/pom.xml",
    "backend/hed-application/pom.xml",
    "backend/hed-coverage/pom.xml",
    "tools/codegen/pom.xml",
)
BUILD_JOBS = {
    "Domain verification",
    "Full application build",
    "Container image",
    "Playwright (English)",
    "Playwright (French)",
    "Publish Docker image",
}
ASSETS = ("compose.run.yaml", "release-manifest.json", "SHA256SUMS.txt")


def source_version(root, tag):
    """Reject non-stable tags and inconsistent Maven or npm project versions."""
    if not re.fullmatch(r"v(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)", tag):
        raise ValueError("Expected a stable release tag such as v0.1.0")
    version = tag[1:]
    ns = {"m": "http://maven.apache.org/POM/4.0.0"}
    for name in POMS:
        pom = ET.parse(root / name).getroot()
        expression = "m:parent/m:version" if name.startswith("backend/") else "m:version"
        if pom.findtext(expression, namespaces=ns) != version:
            raise ValueError(f"{name}: expected project version {version}")
    package = json.loads((root / "frontend/package.json").read_text(encoding="utf-8"))
    lock = json.loads((root / "frontend/package-lock.json").read_text(encoding="utf-8"))
    if any(value != version for value in (
        package["version"], lock["version"], lock["packages"][""]["version"]
    )):
        raise ValueError(f"Frontend package and lockfile must both use version {version}")
    return version


def command(*args):
    """Run a required command without passing arguments through a shell."""
    return subprocess.run(args, check=True, text=True, capture_output=True).stdout.strip()


def api(endpoint):
    """Read all pages of a GitHub endpoint using the workflow's automatic token."""
    return json.loads(command("gh", "api", "--paginate", "--slurp", endpoint))


def verified_run(pages, commit, workflow):
    """Select the latest matching push run and require its successful completion."""
    runs = [run for page in pages for run in page["workflow_runs"] if (
        run["head_sha"] == commit and run["head_branch"] == "master"
        and run["event"] == "push" and run["path"] == f".github/workflows/{workflow}"
        and run["repository"]["full_name"] == REPOSITORY
    )]
    if not runs:
        raise ValueError(f"No master push run found for {workflow} at {commit}")
    run = max(runs, key=lambda item: (item["run_number"], item.get("run_attempt", 1)))
    if run["status"] != "completed" or run["conclusion"] != "success":
        raise ValueError(f"{workflow}: latest master run has not succeeded")
    return run["id"]


def verify_jobs(pages):
    """Reject skipped, missing or failed required jobs, including image publication."""
    jobs = {job["name"]: job for page in pages for job in page["jobs"]}
    for name in sorted(BUILD_JOBS):
        if name not in jobs or jobs[name]["conclusion"] != "success":
            raise ValueError(f"Required build job did not succeed: {name}")


def verified_image(metadata, version, commit):
    """Validate the immutable image identity recorded by the trusted publisher."""
    if metadata.get("version") != version or metadata.get("commit") != commit:
        raise ValueError("Published image metadata does not match the release version and commit")
    image = metadata.get("image", "")
    if not isinstance(image, str) or not re.fullmatch(re.escape(IMAGE) + r"@sha256:[0-9a-f]{64}", image):
        raise ValueError("Published image metadata must contain a valid HED registry digest")
    return image


def write_assets(root, destination, tag, commit, run_id, metadata):
    """Write a version-pinned installer, provenance manifest, checksums and notes."""
    version = source_version(root, tag)
    image = verified_image(metadata, version, commit)
    compose = (root / "compose.run.yaml").read_text(encoding="utf-8")
    placeholder = "${HED_IMAGE:-" + IMAGE + ":latest}"
    if compose.count(placeholder) != 1:
        raise ValueError("Expected exactly one default HED image in compose.run.yaml")
    changelog = (root / "CHANGELOG.md").read_text(encoding="utf-8")
    section = re.search(r"^## " + re.escape(version) + r"\s*\n(.*?)(?=^## |\Z)", changelog, re.M | re.S)
    if not section or not section[1].strip():
        raise ValueError(f"Missing release notes for {version} in CHANGELOG.md")
    destination.mkdir(parents=True, exist_ok=True)
    (destination / ASSETS[0]).write_text(compose.replace(
        placeholder, "${HED_IMAGE:-" + IMAGE + ":" + version + "}"
    ), encoding="utf-8", newline="\n")
    manifest = {
        "version": version, "tag": tag, "commit": commit, "image": image,
        "build_run_id": run_id,
        "build_url": f"https://github.com/{REPOSITORY}/actions/runs/{run_id}",
    }
    (destination / ASSETS[1]).write_text(
        json.dumps(manifest, indent=2) + "\n", encoding="utf-8", newline="\n"
    )
    checksums = "".join(
        f"{hashlib.sha256((destination / name).read_bytes()).hexdigest()}  {name}\n"
        for name in ASSETS[:2]
    )
    (destination / ASSETS[2]).write_text(checksums, encoding="utf-8", newline="\n")
    notes = section[1].strip() + (
        f"\n\nDownload **compose.run.yaml** below and follow the "
        f"[installation guide](https://github.com/{REPOSITORY}/blob/{tag}/doc/user/en/index.adoc#installation) "
        f"([français](https://github.com/{REPOSITORY}/blob/{tag}/doc/user/fr/index.adoc#installation)).\n\n"
        f"The installer uses `{IMAGE}:{version}` (Linux amd64). "
        "The numbered image is promoted after this release is published; wait for the "
        "**Release / Publish numbered image** job to succeed before installing.\n\n"
        "Back up existing projects before updating. The experimental Circuit Diagram "
        "may change and existing circuit diagrams may need to be recreated.\n"
    )
    (destination / "release-notes.md").write_text(notes, encoding="utf-8", newline="\n")
    return manifest


def verify_published_assets(expected, actual):
    """Require the published installer and provenance to match the verified draft."""
    for name in ASSETS:
        if not (actual / name).is_file() or (actual / name).read_bytes() != (expected / name).read_bytes():
            raise ValueError(f"Published release asset differs from the verified build: {name}")


def prepare(root, destination, tag, published=False):
    """Verify source, ancestry and CI before preparing or publishing a release."""
    version = source_version(root, tag)
    commit = command("git", "rev-parse", "HEAD")
    if not re.fullmatch(r"[0-9a-f]{40}", commit):
        raise ValueError("Expected a full commit SHA")
    if command("git", "rev-parse", f"refs/tags/{tag}^{{commit}}") != commit:
        raise ValueError("The checked-out commit differs from the requested tag")
    command("git", "merge-base", "--is-ancestor", commit, "origin/master")
    endpoint = f"repos/{REPOSITORY}/actions"
    run_ids = {}
    for workflow in ("build.yml", "format.yml"):
        pages = api(f"{endpoint}/workflows/{workflow}/runs?event=push&branch=master&head_sha={commit}&per_page=100")
        run_ids[workflow] = verified_run(pages, commit, workflow)
    run_id = run_ids["build.yml"]
    verify_jobs(api(f"{endpoint}/runs/{run_id}/jobs?filter=latest&per_page=100"))
    with tempfile.TemporaryDirectory() as temporary:
        temporary = Path(temporary)
        metadata_dir = temporary / "image"
        command("gh", "run", "download", str(run_id), "--repo", REPOSITORY,
                "--name", f"hed-release-image-{commit}", "--dir", str(metadata_dir))
        metadata = json.loads((metadata_dir / "image.json").read_text(encoding="utf-8"))
        manifest = write_assets(root, destination, tag, commit, run_id, metadata)
        if published:
            release = json.loads(command("gh", "release", "view", tag, "--repo", REPOSITORY,
                                         "--json", "isDraft,isPrerelease"))
            if release["isDraft"] or release["isPrerelease"]:
                raise ValueError("Only a published stable release can promote an image")
            actual = temporary / "published"
            for name in ASSETS:
                command("gh", "release", "download", tag, "--repo", REPOSITORY,
                        "--pattern", name, "--dir", str(actual))
            verify_published_assets(destination, actual)
    if os.environ.get("GITHUB_OUTPUT"):
        with open(os.environ["GITHUB_OUTPUT"], "a", encoding="utf-8", newline="\n") as output:
            for key in ("version", "commit", "image"):
                output.write(f"{key}={manifest[key]}\n")
    print(json.dumps(manifest, indent=2))


def main():
    """Expose the release preparation command used by GitHub Actions."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--tag", required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--published", action="store_true")
    args = parser.parse_args()
    try:
        prepare(Path.cwd(), args.output, args.tag, args.published)
    except (ValueError, KeyError, OSError, ET.ParseError, subprocess.CalledProcessError) as error:
        detail = error.stderr if isinstance(error, subprocess.CalledProcessError) else str(error)
        parser.exit(1, f"Release preparation failed: {detail}\n")


if __name__ == "__main__":
    main()
