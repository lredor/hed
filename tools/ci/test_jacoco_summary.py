# Copyright (c) 2026 Laurent Redor.
# This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v2.0
# which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0

"""Regression checks for coverage totals and incomplete-report diagnostics."""

from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
import xml.etree.ElementTree as ET

from jacoco_summary import render


class CoverageSummaryTest(unittest.TestCase):
    """Exercise complete, branchless, malformed and missing application reports."""

    def setUp(self):
        self.directory = tempfile.TemporaryDirectory()
        self.addCleanup(self.directory.cleanup)
        self.report = Path(self.directory.name) / "jacoco.xml"
        self.report.write_text("""<?xml version="1.0"?>
<!DOCTYPE report PUBLIC "-//JACOCO//DTD Report 1.1//EN" "report.dtd">
<report name="HED Java coverage">
  <group name="hed-model">
    <counter type="LINE" missed="10" covered="80"/>
    <counter type="BRANCH" missed="5" covered="15"/>
  </group>
  <group name="hed-model-edit"/>
  <group name="hed-starter">
    <counter type="LINE" missed="5" covered="3"/>
    <counter type="BRANCH" missed="3" covered="1"/>
  </group>
  <group name="hed-application">
    <counter type="LINE" missed="2" covered="0"/>
  </group>
  <counter type="LINE" missed="17" covered="83"/>
  <counter type="BRANCH" missed="8" covered="16"/>
</report>
""", encoding="utf-8")

    def test_totals_are_weighted_and_branchless_entry_point_is_visible(self):
        summary = render(self.report)
        self.assertIn("**83.0% (83/100)**", summary)
        self.assertIn("**66.7% (16/24)**", summary)
        self.assertIn("| hed-application | 0.0% (0/2) | — |", summary)
        self.assertIn("| hed-model | 88.9% (80/90) | 75.0% (15/20) |", summary)
        self.assertIn("| hed-starter | 37.5% (3/8) | 25.0% (1/4) |", summary)

    def test_report_without_starter_is_rejected(self):
        tree = ET.parse(self.report)
        root = tree.getroot()
        root.remove(root.find("group[@name='hed-starter']"))
        tree.write(self.report)
        with self.assertRaisesRegex(ValueError, "Missing application modules: hed-starter"):
            render(self.report)

    def test_empty_report_is_rejected(self):
        self.report.write_text("<report/>", encoding="utf-8")
        with self.assertRaises(ValueError):
            render(self.report)

    def test_missing_module_line_counter_is_rejected(self):
        tree = ET.parse(self.report)
        group = tree.getroot().find("group[@name='hed-starter']")
        group.remove(group.find("counter[@type='LINE']"))
        tree.write(self.report)
        with self.assertRaisesRegex(ValueError, "Missing LINE counter in hed-starter"):
            render(self.report)

    def test_zero_exercised_lines_is_rejected(self):
        tree = ET.parse(self.report)
        tree.getroot().find("counter[@type='LINE']").set("covered", "0")
        tree.write(self.report)
        with self.assertRaisesRegex(ValueError, "No Java lines were exercised"):
            render(self.report)

    def test_malformed_report_fails_without_publishing_a_summary(self):
        self.report.write_text("<report>", encoding="utf-8")
        self.assert_cli_failure("Cannot summarize Java coverage")

    def test_missing_report_fails_without_publishing_a_summary(self):
        self.report.unlink()
        self.assert_cli_failure(str(self.report))

    def assert_cli_failure(self, expected_error):
        result = subprocess.run(
            [sys.executable, str(Path(__file__).with_name("jacoco_summary.py")), str(self.report)],
            capture_output=True, text=True, check=False,
        )
        self.assertEqual(1, result.returncode)
        self.assertEqual("", result.stdout)
        self.assertIn(expected_error, result.stderr)


if __name__ == "__main__":
    unittest.main()
