/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.diagrams.circuits;

import org.eclipse.sirius.components.collaborative.diagrams.api.ICustomNodeStyleDeserializer;
import org.eclipse.sirius.components.diagrams.INodeStyle;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.node.ObjectNode;

/** Restores circuit junction styles when reopening persisted circuit diagrams. */
@Service
public class CircuitJunctionStyleDeserializer implements ICustomNodeStyleDeserializer {
    @Override
    public boolean canHandle(String type) {
        return CircuitJunctionStyleProvider.TYPE.equals(type);
    }

    @Override
    public INodeStyle handle(ObjectNode root, JsonParser parser, DeserializationContext context) throws JacksonException {
        return context.readTreeAsValue(root, CircuitJunctionNodeStyle.class);
    }
}
