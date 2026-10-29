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

import java.util.List;
import java.util.Locale;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.core.api.labels.StyledString;
import org.eclipse.sirius.components.emf.services.api.IEMFLabelServiceDelegate;
import org.springframework.stereotype.Service;
import hed.domain.HedDomain;
import hed.homeelectrical.HomeElectricalPackage;
import org.eclipse.sirius.components.emf.services.api.IDefaultEMFLabelService;

@Service
public class HedDomainLabels implements IEMFLabelServiceDelegate {
    private final Locale locale;

    private final IDefaultEMFLabelService defaultLabels;

    public HedDomainLabels(Locale locale, IDefaultEMFLabelService defaultLabels) {
        this.locale = locale;
        this.defaultLabels = defaultLabels;
    }

    @Override
    public boolean canHandle(EObject self) {
        return self.eClass().getEPackage() == HomeElectricalPackage.eINSTANCE;
    }

    @Override
    public StyledString getStyledLabel(EObject self) {
        return StyledString.of(HedDomain.label(self, locale));
    }

    @Override
    public List<String> getImagePaths(EObject self) {
        return this.defaultLabels.getImagePaths(self);
    }
}
