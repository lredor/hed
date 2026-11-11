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

import java.util.Map;
import java.util.Locale;
import org.eclipse.sirius.components.collaborative.api.ChangeKind;
import org.eclipse.sirius.components.collaborative.trees.api.IRenameTreeItemHandler;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IIdentityService;
import org.eclipse.sirius.components.representations.IStatus;
import org.eclipse.sirius.components.representations.Success;
import org.eclipse.sirius.components.representations.Failure;
import org.eclipse.sirius.components.trees.Tree;
import org.eclipse.sirius.components.trees.TreeItem;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import hed.domain.HedDomain;
import hed.domain.Messages;

/** A tree link renames the semantic equipment, just like its room occurrence. */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BranchEquipmentRenameHandler implements IRenameTreeItemHandler {
    private final IIdentityService identity;

    private final Locale locale;

    /**
     * Creates a native rename handler for the branch occurrence of an existing device.
     * @param identity resolves deterministic occurrence identifiers
     * @param locale selects validation feedback
     */
    public BranchEquipmentRenameHandler(IIdentityService identity, Locale locale) {
        this.identity = identity;
        this.locale = locale;
    }

    @Override
    public boolean canHandle(IEditingContext context, TreeItem item, String label) {
        return BranchEquipmentLinks.find(context, item.getId(), identity).isPresent();
    }

    @Override
    public IStatus handle(IEditingContext context, TreeItem item, String label, Tree tree) {
        var equipment = BranchEquipmentLinks.find(context, item.getId(), identity).orElseThrow().equipment();
        try {
            HedDomain.set(equipment, "name", label);
            return new Success(ChangeKind.SEMANTIC_CHANGE, Map.of());
        } catch (IllegalArgumentException ex) {
            return new Failure(Messages.text(locale, ex.getMessage()));
        }
    }
}
