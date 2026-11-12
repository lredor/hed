# Copyright (c) 2026 Laurent Redor.
# This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v2.0
# which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0

"""Render aggregate JaCoCo counters as a native GitHub Actions summary."""

import argparse
from pathlib import Path
import sys
import xml.etree.ElementTree as ET


def counter(element: ET.Element, kind: str) -> tuple[int, int]:
    """Return covered and total counts; a missing branch counter means no branches."""
    value = element.find(f"counter[@type='{kind}']")
    if value is None:
        if kind == "BRANCH":
            return 0, 0
        raise ValueError(f"Missing {kind} counter in {element.get('name', 'report')}")
    covered, missed = int(value.attrib["covered"]), int(value.attrib["missed"])
    if covered < 0 or missed < 0:
        raise ValueError("Coverage counters must be non-negative")
    return covered, covered + missed


def format_counter(counts: tuple[int, int]) -> str:
    """Show a weighted percentage and raw counts, or a dash for no code."""
    covered, total = counts
    return f"{100 * covered / total:.1f}% ({covered:,}/{total:,})" if total else "—"


def render(report: Path) -> str:
    """Read a complete application report, rejecting empty or uninstrumented results."""
    root = ET.parse(report).getroot()
    if root.tag != "report":
        raise ValueError("Expected a JaCoCo report element")
    required = {"hed-model", "hed-starter", "hed-application"}
    modules = {group.attrib["name"]: group for group in root.findall("group")}
    missing = required - modules.keys()
    if missing:
        raise ValueError(f"Missing application modules: {', '.join(sorted(missing))}")
    lines = counter(root, "LINE")
    if lines[0] == 0:
        raise ValueError("No Java lines were exercised; check the JaCoCo agent and test execution")
    rows = [
        "## Java coverage",
        "",
        "JaCoCo coverage from the Java tests. EMF-generated model and item providers are excluded.",
        "",
        "| Module | Lines | Branches |",
        "| --- | ---: | ---: |",
    ]
    for name in sorted(required):
        module = modules[name]
        rows.append(f"| {name} | {format_counter(counter(module, 'LINE'))} | {format_counter(counter(module, 'BRANCH'))} |")
    rows.extend([
        f"| **Total** | **{format_counter(lines)}** | **{format_counter(counter(root, 'BRANCH'))}** |",
        "",
        "Branches count outcomes of Java decisions, not Git branches. A dash means no branches to measure.",
        "",
        "The generated edit module has no handwritten Java to measure. The application entry point is included, "
        "even though the separate Playwright runs do not contribute to this Java-test coverage.",
        "",
        "No minimum coverage threshold is enforced yet. Download **java-coverage** and open **index.html** "
        "for source-level details; **jacoco.xml** is available for tooling.",
        "",
    ])
    return "\n".join(rows)


def main() -> int:
    """Write Markdown to stdout; fail clearly instead of publishing misleading results."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("report", type=Path, help="Aggregate jacoco.xml")
    args = parser.parse_args()
    try:
        summary = render(args.report)
    except (OSError, ET.ParseError, ValueError, KeyError) as error:
        print(f"Cannot summarize Java coverage from {args.report}: {error}", file=sys.stderr)
        return 1
    print(summary)
    return 0


if __name__ == "__main__":
    sys.exit(main())
