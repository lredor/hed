/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter;

import java.util.Locale;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IEditingContextProcessor;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import hed.homeelectrical.HomeElectricalPackage;
import hed.homeelectrical.provider.HomeElectricalItemProviderAdapterFactory;
import hed.domain.HedDomainEValidator;
import org.eclipse.emf.ecore.EValidator;

@Configuration
public class HedDomainConfiguration {
    @Bean
    public ComposedAdapterFactory.Descriptor hedAdapterFactory() {
        return HomeElectricalItemProviderAdapterFactory::new;
    }

    @Bean
    public Locale hedLocale(@Value("${spring.mvc.locale:en_GB}") Locale locale) {
        return locale;
    }

    @Bean
    public IEditingContextProcessor hedPackageRegistration(Locale locale) {
        EPackage.Registry.INSTANCE.put(HomeElectricalPackage.eNS_URI, HomeElectricalPackage.eINSTANCE);
        EValidator.Registry.INSTANCE.put(HomeElectricalPackage.eINSTANCE, new HedDomainEValidator(locale));
        return new IEditingContextProcessor() {
            @Override
            public void preProcess(IEditingContext context) {
                if (context instanceof IEMFEditingContext emf) {
                    emf.getDomain().getResourceSet().getPackageRegistry().put(HomeElectricalPackage.eNS_URI, HomeElectricalPackage.eINSTANCE);
                }
            }
        };
    }
}
