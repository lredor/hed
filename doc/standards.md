<!--
Copyright (c) 2026 Laurent Redor.
This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
https://www.eclipse.org/legal/epl-2.0/

SPDX-License-Identifier: EPL-2.0
-->

# Version-aware electrical standard checks

Select the **Dwelling** in Model Explorer and edit **Applicable electrical standard** in Details. The choice is persisted in `Dwelling.standardVersion`; changing it recomputes validation and table anomalies without changing any wiring or catalog specifications.

New and existing dwellings default to **Unspecified (standard checks disabled)**. HED never infers a standard from a project date, its language or the current date. Structural/domain validation remains active with every selection.

The supported profiles cover selected design rules, not a complete conformity assessment. **Before A5 (2010 profile)** follows the historical Schneider guide below; **Amendment A5 (2015)** follows the A5 guide. The 2024 revision is not offered by this implementation. Selecting a historical profile does not determine which standard legally applies to work on an installation.

## General-purpose outlet circuit count

| Profile | 1.5 mm² copper | 2.5 mm² copper | Counting within one physical block |
| --- | --- | --- | --- |
| Before A5 | 5 counted outlets | 8 counted outlets | 1-2 outlets count as 1; 3-4 as 2; above 4 as 3 |
| A5 (2015) | 8 outlets | 12 outlets | Each individual outlet counts once |

The rule targets final circuits with **Usage = Socket outlets**. It expands each assigned block into all its individual outlets, including all branches and rows. A block has one common circuit and optional branch; moving it transfers every member together. Unassigned blocks do not contribute to a circuit count.

For example, seven double-outlet blocks and one standalone outlet on one 2.5 mm² circuit count as 8 before A5, and 15 under A5. The historical profile accepts that count; A5 reports a warning because 15 exceeds 12.

Missing or other conductor sections are explicitly marked **not assessed** by this count rule. Record the actual section instead of changing it merely to silence a warning. The checks assume copper conductors because conductor material is not modeled. This implementation does not infer specialized kitchen requirements, switched-outlet lighting equivalences, minimum room equipment, installation dates or regulatory applicability. These remain outside this rule's scope.

Warnings identify the affected circuit, selected profile, counted quantity and limit. They appear through standard EMF validation, the Dwelling/circuit validation fields in Details and circuit-table anomalies. They do not prevent saving an incomplete design. Rule dispatch and counting are in `hed.domain.standards`; messages retain stable keys and immutable parameters for English/French rendering.

## Sources and traceability

- [Schneider Electric, Guide de l'installation électrique 2010, chapter Q, pages Q12 and Q14](https://fr.electrical-installation.org/frw/images/4/42/Q-La-norme-NF-C-15-100-dans-l-habitat-2.pdf): historical conductor/protection table and per-box socket counting.
- [ABB, NF C 15-100 amendment 5, June 2016, slide 5](https://library.e.abb.com/public/5ce5a4dd0ee94d5792a57151d9fd7fd2/NF%20C%2015-100%20amendement%205.pdf): A5 circuit limits and individual socket counting.

These are manufacturer-authored technical guides used to trace the implemented subset. HED does not redistribute the normative text.
