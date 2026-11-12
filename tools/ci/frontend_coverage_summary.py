# Copyright (c) 2026 Laurent Redor.
# This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v2.0
# which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0

"""Render Vitest's Istanbul JSON summary in GitHub Actions without exposing local paths."""

import argparse
import json
from pathlib import Path
import sys

from jacoco_summary import format_counter

METRICS = ("lines", "branches", "functions", "statements")


def cells(stats: dict) -> list[str]:
    """Calculate percentages from execution counts instead of rounded report percentages."""
    result = []
    for name in METRICS:
        counts = stats[name]
        covered, total = counts["covered"], counts["total"]
        if type(covered) is not int or type(total) is not int or not 0 <= covered <= total:
            raise ValueError(f"Invalid {name} coverage counts")
        result.append(format_counter((covered, total)))
    return result


def render(report: Path) -> str:
    """Require exercised HED representation sources and render weighted totals per file."""
    data = json.loads(report.read_text(encoding="utf-8"))
    if not isinstance(data, dict) or "total" not in data:
        raise ValueError("Missing total coverage counters")
    total = cells(data["total"])
    if data["total"]["lines"]["covered"] == 0:
        raise ValueError("No frontend lines were exercised; check Vitest and the V8 provider")
    files = []
    for path, stats in data.items():
        if path == "total":
            continue
        normalized = path.replace("\\", "/")
        marker = "/src/representations/"
        if marker not in normalized:
            raise ValueError("Coverage includes a file outside src/representations")
        label = normalized.split(marker, 1)[1].replace("|", "\\|")
        files.append((label, cells(stats)))
    if not files:
        raise ValueError("Missing per-file representation coverage")
    rows = [
        "## Frontend coverage",
        "",
        "V8 coverage from Vitest tests of HED's custom representations, including sources not yet exercised.",
        "",
        "| Source | Lines | Branches | Functions | Statements |",
        "| --- | ---: | ---: | ---: | ---: |",
    ]
    rows.extend("| " + label + " | " + " | ".join(values) + " |" for label, values in sorted(files))
    rows.extend([
        "| **Total** | " + " | ".join(f"**{value}**" for value in total) + " |",
        "",
        "Only src/representations is measured. Startup wiring, CSS, tests, Sirius Web and other dependencies "
        "are outside this baseline. Platform services are mocked; the separate bilingual Playwright jobs "
        "verify the running application and do not contribute to these figures.",
        "",
        "No minimum coverage threshold is enforced yet. Download **frontend-coverage** and open **index.html** "
        "for source-level details. LCOV and coverage-summary.json are included for tooling.",
        "",
    ])
    return "\n".join(rows)


def main() -> int:
    """Write Markdown on success and a diagnostic on failure, without a partial summary."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("report", type=Path, help="Vitest coverage-summary.json")
    args = parser.parse_args()
    try:
        summary = render(args.report)
    except (OSError, ValueError, KeyError, TypeError) as error:
        print(f"Cannot summarize frontend coverage from {args.report}: {error}", file=sys.stderr)
        return 1
    print(summary)
    return 0


if __name__ == "__main__":
    sys.exit(main())
