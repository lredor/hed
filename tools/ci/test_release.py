# Copyright (c) 2026 Laurent Redor.
# This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v2.0
# which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0

"""Regression checks for release identity, CI gates and reproducible assets."""

import hashlib
import json
import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest
from unittest.mock import patch

from release import (
    ASSETS, BUILD_JOBS, IMAGE, POMS, REPOSITORY, prepare, source_version,
    verified_image, verified_run, verify_jobs, verify_published_assets, write_assets,
)


class ReleaseTest(unittest.TestCase):
    """Exercise publication prerequisites without creating a tag or a release."""

    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory()
        self.addCleanup(self.temporary.cleanup)
        self.root = Path(self.temporary.name)
        for name in POMS:
            path = self.root / name
            path.parent.mkdir(parents=True, exist_ok=True)
            version = "<version>0.1.0</version>"
            if name.startswith("backend/"):
                version = f"<parent>{version}</parent>"
            path.write_text(f'<project xmlns="http://maven.apache.org/POM/4.0.0">{version}</project>', encoding="utf-8")
        (self.root / "frontend").mkdir()
        (self.root / "frontend/package.json").write_text('{"version":"0.1.0"}', encoding="utf-8")
        (self.root / "frontend/package-lock.json").write_text(
            '{"version":"0.1.0","packages":{"":{"version":"0.1.0"}}}', encoding="utf-8"
        )
        (self.root / "compose.run.yaml").write_text(
            f"services:\n  application:\n    image: ${{HED_IMAGE:-{IMAGE}:latest}}\n", encoding="utf-8"
        )
        (self.root / "CHANGELOG.md").write_text(
            "# Changelog\n\n## 0.1.0\n\nInitial release. Éclairage.\n\n## 0.0.1\nOld release.\n", encoding="utf-8"
        )
        self.commit = "a" * 40
        self.metadata = {"commit": self.commit, "version": "0.1.0", "image": IMAGE + "@sha256:" + "b" * 64}
        self.destination = self.root / "assets"

    def run_fixture(self, **changes):
        run = {
            "id": 123, "run_number": 10, "run_attempt": 1, "head_sha": self.commit,
            "head_branch": "master", "event": "push", "path": ".github/workflows/build.yml",
            "repository": {"full_name": REPOSITORY}, "status": "completed", "conclusion": "success",
        }
        run.update(changes)
        return run

    def job_fixture(self):
        return [{"jobs": [{"name": name, "conclusion": "success"} for name in sorted(BUILD_JOBS)]}]

    def test_release_versions_and_stable_tag(self):
        self.assertEqual("0.1.0", source_version(self.root, "v0.1.0"))
        for tag in ("0.1.0", "v0.1.0-SNAPSHOT", "v0.1.0-rc1", "v00.1.0", "v0.1.0\n", "../master"):
            with self.subTest(tag=tag), self.assertRaises(ValueError):
                source_version(self.root, tag)

    def test_every_maven_project_including_codegen_must_match(self):
        for name in POMS:
            path = self.root / name
            original = path.read_text(encoding="utf-8")
            path.write_text(original.replace("0.1.0", "0.1.0-SNAPSHOT"), encoding="utf-8")
            with self.subTest(pom=name), self.assertRaisesRegex(ValueError, "expected project version"):
                source_version(self.root, "v0.1.0")
            path.write_text(original, encoding="utf-8")

    def test_package_and_both_lock_versions_must_match(self):
        for name, field in (("package.json", "version"), ("package-lock.json", "version"), ("package-lock.json", "root")):
            path = self.root / "frontend" / name
            original = path.read_text(encoding="utf-8")
            data = json.loads(original)
            target = data["packages"][""] if field == "root" else data
            target["version"] = "0.2.0"
            path.write_text(json.dumps(data), encoding="utf-8")
            with self.subTest(name=name, field=field), self.assertRaisesRegex(ValueError, "Frontend"):
                source_version(self.root, "v0.1.0")
            path.write_text(original, encoding="utf-8")

    def test_paginated_master_ci_and_latest_attempt(self):
        pages = [{"workflow_runs": [self.run_fixture(run_number=9)]},
                 {"workflow_runs": [self.run_fixture(id=124, run_attempt=2)]}]
        self.assertEqual(124, verified_run(pages, self.commit, "build.yml"))
        pages[1]["workflow_runs"][0]["conclusion"] = "failure"
        with self.assertRaisesRegex(ValueError, "latest master run"):
            verified_run(pages, self.commit, "build.yml")

    def test_ci_from_another_context_is_rejected(self):
        for changes in (
            {"event": "pull_request"}, {"head_branch": "feature"}, {"head_sha": "c" * 40},
            {"repository": {"full_name": "other/hed"}}, {"path": ".github/workflows/other.yml"},
        ):
            with self.subTest(changes=changes), self.assertRaisesRegex(ValueError, "No master push run"):
                verified_run([{"workflow_runs": [self.run_fixture(**changes)]}], self.commit, "build.yml")
        for changes in ({"status": "in_progress"}, {"conclusion": "cancelled"}):
            with self.subTest(changes=changes), self.assertRaises(ValueError):
                verified_run([{"workflow_runs": [self.run_fixture(**changes)]}], self.commit, "build.yml")

    def test_all_build_jobs_including_bilingual_tests_and_publication_are_required(self):
        verify_jobs(self.job_fixture())
        for name in BUILD_JOBS:
            for conclusion in ("skipped", "failure", None):
                pages = self.job_fixture()
                next(job for job in pages[0]["jobs"] if job["name"] == name)["conclusion"] = conclusion
                with self.subTest(name=name, conclusion=conclusion), self.assertRaisesRegex(ValueError, "Required build job"):
                    verify_jobs(pages)
        with self.assertRaises(ValueError):
            verify_jobs([{"jobs": []}])

    def test_image_identity_must_be_immutable_and_match_the_source(self):
        self.assertEqual(self.metadata["image"], verified_image(self.metadata, "0.1.0", self.commit))
        for changes in (
            {"version": "0.2.0"}, {"commit": "c" * 40}, {"image": IMAGE + ":latest"},
            {"image": "ghcr.io/other/hed@sha256:" + "b" * 64}, {"image": None},
        ):
            with self.subTest(changes=changes), self.assertRaises(ValueError):
                verified_image(self.metadata | changes, "0.1.0", self.commit)

    def test_installer_manifest_checksums_and_notes_are_consistent(self):
        original = (self.root / "compose.run.yaml").read_bytes()
        manifest = write_assets(self.root, self.destination, "v0.1.0", self.commit, 123, self.metadata)
        self.assertIn(IMAGE + ":0.1.0}", (self.destination / ASSETS[0]).read_text(encoding="utf-8"))
        self.assertEqual(original, (self.root / "compose.run.yaml").read_bytes())
        self.assertEqual(self.metadata["image"], manifest["image"])
        self.assertEqual(123, manifest["build_run_id"])
        for line in (self.destination / "SHA256SUMS.txt").read_text(encoding="utf-8").splitlines():
            digest, name = line.split("  ")
            self.assertEqual(digest, hashlib.sha256((self.destination / name).read_bytes()).hexdigest())
        notes = (self.destination / "release-notes.md").read_text(encoding="utf-8")
        self.assertIn("Éclairage", notes)
        self.assertNotIn("Old release", notes)
        self.assertIn("Publish numbered image", notes)
        snapshot = {name: (self.destination / name).read_bytes() for name in ASSETS}
        write_assets(self.root, self.destination, "v0.1.0", self.commit, 123, self.metadata)
        self.assertEqual(snapshot, {name: (self.destination / name).read_bytes() for name in ASSETS})

    def test_missing_notes_and_ambiguous_installer_are_rejected(self):
        (self.root / "CHANGELOG.md").write_text("# Changelog\n", encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "Missing release notes"):
            write_assets(self.root, self.destination, "v0.1.0", self.commit, 123, self.metadata)
        path = self.root / "compose.run.yaml"
        path.write_text(path.read_text(encoding="utf-8") * 2, encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "exactly one"):
            write_assets(self.root, self.destination, "v0.1.0", self.commit, 123, self.metadata)

    def test_published_assets_cannot_be_changed(self):
        write_assets(self.root, self.destination, "v0.1.0", self.commit, 123, self.metadata)
        actual = self.root / "published"
        actual.mkdir()
        for name in ASSETS:
            (actual / name).write_bytes((self.destination / name).read_bytes())
        verify_published_assets(self.destination, actual)
        (actual / "compose.run.yaml").write_text("different image", encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "Published release asset differs"):
            verify_published_assets(self.destination, actual)

    def test_preparation_verifies_both_workflows_and_downloads_recorded_digest(self):
        calls = []

        def fake_command(*args):
            calls.append(args)
            if args[:2] == ("git", "rev-parse"):
                return self.commit
            if args[:3] == ("gh", "run", "download"):
                directory = Path(args[args.index("--dir") + 1])
                directory.mkdir()
                (directory / "image.json").write_text(json.dumps(self.metadata), encoding="utf-8")
            return ""

        def fake_api(endpoint):
            if "/jobs?" in endpoint:
                return self.job_fixture()
            workflow = "format.yml" if "format.yml" in endpoint else "build.yml"
            return [{"workflow_runs": [self.run_fixture(path=f".github/workflows/{workflow}")]}]

        with patch("release.command", side_effect=fake_command), patch("release.api", side_effect=fake_api) as api_mock, patch("builtins.print"):
            with patch.dict("os.environ", {"GITHUB_OUTPUT": str(self.root / "outputs")}):
                prepare(self.root, self.destination, "v0.1.0")
        self.assertEqual(3, api_mock.call_count)
        self.assertIn(("git", "merge-base", "--is-ancestor", self.commit, "origin/master"), calls)
        self.assertIn("image=" + self.metadata["image"], (self.root / "outputs").read_text(encoding="utf-8"))
        self.assertTrue((self.destination / "release-manifest.json").exists())

    def test_published_preparation_requires_a_stable_release_and_unchanged_assets(self):
        write_assets(self.root, self.destination, "v0.1.0", self.commit, 123, self.metadata)
        state = {"isDraft": False, "isPrerelease": False}

        def fake_command(*args):
            if args[:2] == ("git", "rev-parse"):
                return self.commit
            if args[:3] == ("gh", "run", "download"):
                directory = Path(args[args.index("--dir") + 1])
                directory.mkdir()
                (directory / "image.json").write_text(json.dumps(self.metadata), encoding="utf-8")
            if args[:3] == ("gh", "release", "view"):
                return json.dumps(state)
            if args[:3] == ("gh", "release", "download"):
                directory = Path(args[args.index("--dir") + 1])
                directory.mkdir(exist_ok=True)
                name = args[args.index("--pattern") + 1]
                (directory / name).write_bytes((self.destination / name).read_bytes())
            return ""

        with patch("release.command", side_effect=fake_command), patch("release.api", return_value=[]), \
                patch("release.verified_run", return_value=123), patch("release.verify_jobs"), \
                patch("builtins.print"), patch.dict("os.environ", {"GITHUB_OUTPUT": ""}):
            prepare(self.root, self.destination, "v0.1.0", published=True)
            for flag in ("isDraft", "isPrerelease"):
                state[flag] = True
                with self.subTest(flag=flag), self.assertRaisesRegex(ValueError, "published stable release"):
                    prepare(self.root, self.destination, "v0.1.0", published=True)
                state[flag] = False

    def test_numbered_image_promotion_is_idempotent_and_never_replaces_another_digest(self):
        bash = shutil.which("bash")
        windows_bash = Path("C:/Program Files/Git/bin/bash.exe")
        if os.name == "nt" and windows_bash.exists():
            bash = str(windows_bash)
        if not bash:
            self.skipTest("Bash is required to exercise the GitHub Actions promotion step")
        workflow = Path(__file__).resolve().parents[2] / ".github/workflows/release.yml"
        script = workflow.read_text(encoding="utf-8").split("        run: |\n")[-1]
        script = "\n".join(line[10:] for line in script.splitlines())
        mock = r'''
docker() {
  echo "$*" >> "$RUNNER_TEMP/docker-calls"
  case "$*" in
    login*) cat > /dev/null ;;
    "buildx imagetools inspect"*)
      if [ -f "$RUNNER_TEMP/created" ] || [ "$MOCK_STATE" = same ]; then
        echo "${IMAGE##*@}"
      elif [ "$MOCK_STATE" = conflict ]; then
        echo sha256:another-image
      elif [ "$MOCK_STATE" = network ]; then
        echo 'connection timed out' >&2; return 1
      else
        echo 'manifest unknown' >&2; return 1
      fi ;;
    "buildx imagetools create"*) touch "$RUNNER_TEMP/created" ;;
  esac
}
'''
        for state in ("absent", "same", "conflict", "network"):
            with self.subTest(state=state):
                directory = self.root / state
                directory.mkdir()
                environment = os.environ | {
                    "RUNNER_TEMP": directory.as_posix(), "GITHUB_STEP_SUMMARY": (directory / "summary").as_posix(),
                    "MOCK_STATE": state, "PUBLISH_TOKEN": "test-only", "REGISTRY_USER": "test-only",
                    "IMAGE": self.metadata["image"], "VERSION": "0.1.0", "REVISION": self.commit,
                }
                result = subprocess.run(
                    [bash, "--noprofile", "--norc", "-euo", "pipefail", "-c", mock + script],
                    env=environment, capture_output=True, text=True,
                )
                self.assertEqual(0 if state in ("absent", "same") else 1, result.returncode, result.stderr)
                calls = (directory / "docker-calls").read_text(encoding="utf-8")
                self.assertEqual(state == "absent", "imagetools create" in calls)
                self.assertEqual(state in ("absent", "same"), "pull --platform linux/amd64" in calls)
                self.assertNotIn(":latest", calls)


if __name__ == "__main__":
    unittest.main()
