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

The rule targets final circuits with **Usage = Socket outlets**. A circuit whose use is unspecified receives an explicit not-assessed warning: select the actual use in Details to enable the relevant checks. HED does not infer a circuit use from its name or connected devices. It expands each assigned block into all its individual outlets, including all branches and rows. A block has one common circuit and optional branch; moving it transfers every member together. Unassigned blocks do not contribute to a circuit count.

For example, seven double-outlet blocks and one standalone outlet on one 2.5 mm² circuit count as 8 before A5, and 15 under A5. The historical profile accepts that count; A5 reports a warning because 15 exceeds 12.

Missing or other conductor sections are explicitly marked **not assessed** by this count rule. Record the actual section instead of changing it merely to silence a warning. The checks assume copper conductors because conductor material is not modeled. This implementation does not infer specialized kitchen requirements, switched-outlet lighting equivalences, minimum room equipment, installation dates or regulatory applicability. These remain outside this rule's scope.

Warnings identify the affected circuit, selected profile, counted quantity and limit. They appear through standard EMF validation, the Dwelling/circuit validation fields in Details and circuit-table anomalies. They do not prevent saving an incomplete design. Rule dispatch and counting are in `hed.domain.standards`; messages retain stable keys and immutable parameters for English/French rendering.

## Copper section and protection rating

This rule targets final circuits whose use is **Socket outlets** or **Lighting**. It reads the protective device's current rating from its catalog type and assumes copper conductors.

| Use | Minimum section | Maximum MCB/RCBO rating | Historical fuse allowance before A5 |
| --- | --- | --- | --- |
| Lighting | 1.5 mm² | 16 A | 10 A maximum |
| General-purpose sockets below 2.5 mm² | 1.5 mm² | 16 A | Not permitted |
| General-purpose sockets from 2.5 mm² | 2.5 mm² | 20 A | 16 A maximum |

A5 rejects fuse protection for these supported circuit uses. Larger sections do not increase these usage-specific rating caps. Missing/nonfinite sizing values are marked not assessed, and missing protection remains a structural warning. Other circuit uses, cable installation methods, thermal derating and voltage drop are not evaluated by this matrix.
Warnings target the circuit and include the actual section/rating, applicable limit and profile. Catalog rating changes and profile changes recompute results without blocking draft edits.

## Outgoing circuits under an RCCB

Under **A5 (2015)**, a recorded final RCCB with sensitivity at most 30 mA may supply at most eight overcurrent-protected outgoing circuits. The rule counts explicitly modeled circuits through their distinct MCB, RCBO or fuse and recorded `upstreamRccb` links, including intermediate RCCBs. Spare breakers without a circuit assignment, room devices, circuit branches, relays, contactors and surge protectors are not extra departures. Unmodeled circuits cannot be inferred by this check.

Independent RCCBs are counted separately. Warnings target the overloaded RCCB and show the recorded count and limit. The historical profile does not inherit this A5 limit. A recorded sensitivity above 30 mA is outside this final-protection check; when the count exceeds eight but sensitivity is missing/invalid, the result is marked not assessed rather than assuming 30 mA.

This rule does not size an RCCB's assigned current, check phase balance or certify the installation's residual-current protection. See ABB's A5 guide, slide 9, for the eight-circuit limit.

## Sources and traceability

- [Schneider Electric, Guide de l'installation électrique 2010, chapter Q, pages Q12 and Q14](https://fr.electrical-installation.org/frw/images/4/42/Q-La-norme-NF-C-15-100-dans-l-habitat-2.pdf): historical conductor/protection table and per-box socket counting.
- [ABB, NF C 15-100 amendment 5, June 2016, slides 5 and 9](https://library.e.abb.com/public/5ce5a4dd0ee94d5792a57151d9fd7fd2/NF%20C%2015-100%20amendement%205.pdf): A5 circuit limits, individual socket counting and the eight-circuit limit per RCCB.

- [Promotelec, lighting circuit protection](https://www.promotelec.com/faq/professionnels/combien-de-points-lumineux-peut-on-mettre-sur-un-disjoncteur-16-a/): 1.5 mm² / 16 A and the A5 prohibition on fuses.

- [Legrand, NF C 15-100 — revision of 23 August 2024, key points and solutions](https://assets.legrand.com/editorial/legrandfr/outils/documentations-et-guides/legrand-guide-norme-nf-c-15-100.pdf): additional reference for future rules and the 2024 profile. This edition is not used to backdate requirements to the pre-A5 or A5 profiles currently supported.

These are manufacturer/industry-authored technical guides used to trace the implemented subset. HED does not redistribute the normative text.
