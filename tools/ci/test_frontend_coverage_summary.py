# Copyright (c) 2026 Laurent Redor.
# This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v2.0
# which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0

"""Check weighted frontend summaries and diagnostics for missing or unusable data."""

import json
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest

from frontend_coverage_summary import render


class FrontendCoverageSummaryTest(unittest.TestCase):
    """Exercise real Istanbul counter shapes with Unix and Windows source paths."""

    def setUp(self):
        self.directory = tempfile.TemporaryDirectory()
        self.addCleanup(self.directory.cleanup)
        self.report = Path(self.directory.name) / "coverage-summary.json"
        def stats(covered, total, branches):
            result = {name: {"covered": covered, "total": total, "pct": 0}
                      for name in ("lines", "statements", "functions")}
            result["branches"] = {"covered": branches[0], "total": branches[1], "pct": 0}
            return result
        self.data = {
            "total": stats(83, 100, (16, 24)),
            "/home/runner/hed/frontend/src/representations/diagrams/panel/PanelDiagram.tsx": stats(80, 90, (16, 24)),
            "C:\\work\\hed\\frontend\\src\\representations\\diagrams\\floorplan\\PlanLabelFilter.ts": stats(3, 10, (0, 0)),
        }
        self.write()

    def write(self):
        self.report.write_text(json.dumps(self.data), encoding="utf-8")

    def test_uses_weighted_counts_and_relative_names_on_both_platforms(self):
        summary = render(self.report)
        self.assertIn("**83.0% (83/100)**", summary)
        self.assertIn("**66.7% (16/24)**", summary)
        self.assertIn("diagrams/floorplan/PlanLabelFilter.ts | 30.0% (3/10) | —", summary)
        self.assertNotIn("C:\\work", summary)
        self.assertNotIn("/home/runner", summary)

    def test_rejects_an_uninstrumented_run(self):
        self.data["total"]["lines"]["covered"] = 0
        self.write()
        with self.assertRaisesRegex(ValueError, "No frontend lines were exercised"):
            render(self.report)

    def test_rejects_missing_per_file_results(self):
        self.data = {"total": self.data["total"]}
        self.write()
        with self.assertRaisesRegex(ValueError, "Missing per-file"):
            render(self.report)

    def test_rejects_dependencies_in_the_measured_scope(self):
        self.data["/home/runner/hed/frontend/node_modules/react/index.js"] = self.data["total"]
        self.write()
        with self.assertRaisesRegex(ValueError, "outside src/representations"):
            render(self.report)

    def test_rejects_impossible_counts(self):
        self.data["total"]["lines"]["covered"] = 101
        self.write()
        with self.assertRaisesRegex(ValueError, "Invalid lines coverage counts"):
            render(self.report)

    def test_malformed_and_missing_reports_fail_without_a_partial_summary(self):
        for content in ("not JSON", None):
            if content is None:
                self.report.unlink()
            else:
                self.report.write_text(content, encoding="utf-8")
            result = subprocess.run(
                [sys.executable, str(Path(__file__).with_name("frontend_coverage_summary.py")), str(self.report)],
                capture_output=True, text=True, check=False,
            )
            self.assertEqual(1, result.returncode)
            self.assertEqual("", result.stdout)
            self.assertIn("Cannot summarize frontend coverage", result.stderr)


if __name__ == "__main__":
    unittest.main()
