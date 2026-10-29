/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.domain;

import java.util.Locale;
import java.util.Map;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EObjectValidator;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.HomeElectricalPackage;

/** Exposes domain diagnostics through the standard EMF validation mechanism. */
public final class HedDomainEValidator extends EObjectValidator {
    private final Locale locale;

    public HedDomainEValidator(Locale locale) {
        this.locale = locale;
    }

    @Override
    protected EPackage getEPackage() {
        return HomeElectricalPackage.eINSTANCE;
    }

    @Override
    public boolean validate(EClass type, EObject object, DiagnosticChain diagnostics, Map<Object, Object> context) {
        boolean valid = super.validate(type, object, diagnostics, context);
        if (object instanceof Dwelling home) {
            for (var issue : new HedValidation().validate(home)) {
                boolean error = issue.severity() == HedValidation.Severity.ERROR;
                if (diagnostics != null) {
                    diagnostics.add(new BasicDiagnostic(error ? Diagnostic.ERROR : Diagnostic.WARNING, "hed", 0, Messages.text(locale, issue.code()), new Object[] { issue.target() }));
                }
                valid = valid && !error;
            }
        }
        return valid;
    }
}
