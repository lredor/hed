/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import { parse, print } from 'graphql';
import { describe, expect, it } from 'vitest';
import { panelDocumentTransform } from '../src/representations/diagrams/panel/PanelDiagram';
import { floorPlanDocumentTransform } from '../src/representations/diagrams/floorplan/FloorPlanDiagram';

describe.each([
  ['PanelNodeStyle', panelDocumentTransform],
  ['FloorPlanNodeStyle', floorPlanDocumentTransform],
] as const)('%s subscription fields', (style, transform) => {
  it('adds custom fields to diagram subscriptions without duplicating them', () => {
    const input = parse(
      'subscription diagramEvent { diagramEvent { style { __typename ... on ImageNodeStyle { imageURL } } } }'
    );
    const output = transform.transformDocument(input);
    expect(print(output)).toContain(`... on ${style}`);
    expect(print(output)).toContain('imageURL');
    expect(print(output)).toContain('childrenLayoutStrategyFragment');
    const repeated = transform.transformDocument(output);
    expect(print(repeated).split(`... on ${style}`)).toHaveLength(2);
  });

  it('leaves other operations and non-image style selections intact', () => {
    const query = parse('query projects { projects { id } }');
    expect(transform.transformDocument(query)).toBe(query);
    const subscription = parse('subscription diagramEvent { diagramEvent { style { __typename } } }');
    expect(print(transform.transformDocument(subscription))).toBe(print(subscription));
    const unrelatedField = parse(
      'subscription diagramEvent { diagramEvent { other { ... on ImageNodeStyle { imageURL } } } }'
    );
    expect(print(transform.transformDocument(unrelatedField))).toBe(print(unrelatedField));
  });
});
