/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.modelexplorer;

import java.util.Objects;
import org.eclipse.sirius.components.collaborative.trees.api.IInitialDirectEditTreeItemLabelProvider;
import org.eclipse.sirius.components.collaborative.trees.dto.InitialDirectEditElementLabelInput;
import org.eclipse.sirius.components.collaborative.trees.dto.InitialDirectEditElementLabelSuccessPayload;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.trees.Tree;
import org.eclipse.sirius.web.application.views.explorer.services.ExplorerInitialDirectEditTreeItemLabelProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import hed.homeelectrical.ModularDevice;

/** Supplies only the stored device name when editing a composite explorer label. */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HedInitialDirectEditTreeItemLabelProvider implements IInitialDirectEditTreeItemLabelProvider {
    private final IObjectSearchService search;

    private final ExplorerInitialDirectEditTreeItemLabelProvider defaultLabels;

    public HedInitialDirectEditTreeItemLabelProvider(IObjectSearchService search, ExplorerInitialDirectEditTreeItemLabelProvider defaultLabels) {
        this.search = search;
        this.defaultLabels = defaultLabels;
    }

    @Override
    public boolean canHandle(Tree tree) {
        return defaultLabels.canHandle(tree);
    }

    @Override
    public IPayload handle(IEditingContext context, Tree tree, InitialDirectEditElementLabelInput input) {
        return search.getObject(context, input.treeItemId()).filter(ModularDevice.class::isInstance).map(ModularDevice.class::cast)
                .<IPayload> map(device -> new InitialDirectEditElementLabelSuccessPayload(input.id(), Objects.toString(device.getName(), "")))
                .orElseGet(() -> defaultLabels.handle(context, tree, input));
    }
}
