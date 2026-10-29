/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
import java.nio.file.Path;
import java.nio.file.Files;
import hed.codegen.SourceFormatting;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapterFactory;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public final class GenerateModel {
    public static void main(String[] args) throws Exception {
        var root = Path.of(args[0]).toAbsolutePath().normalize();
        var rs = new ResourceSetImpl();
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("genmodel", new XMIResourceFactoryImpl());
        GenModelPackage.eINSTANCE.eClass();
        EcorePlugin.getPlatformResourceMap().put("hed-model", URI.createFileURI(root.resolve("backend/hed-model").toString() + "/"));
        EcorePlugin.getPlatformResourceMap().put("hed-model-edit", URI.createFileURI(root.resolve("backend/hed-model-edit").toString() + "/"));
        var uri = URI.createPlatformResourceURI("/hed-model/src/main/resources/model/homeelectrical.ecore", true);
        var pkg = (EPackage) rs.getResource(uri, true).getContents().get(0);
        var gen = GenModelFactory.eINSTANCE.createGenModel();
        gen.setModelName("HomeElectrical");
        gen.setModelPluginID("hed-model");
        gen.setEditPluginID("hed-model-edit");
        gen.setModelDirectory("/hed-model/src/main/java");
        gen.setEditDirectory("/hed-model-edit/src/main/java");
        gen.setComplianceLevel(GenJDKLevel.JDK170_LITERAL);
        gen.setRootExtendsClass("org.eclipse.emf.ecore.impl.MinimalEObjectImpl$Container");
        gen.initialize(java.util.List.of(pkg));
        gen.setCopyrightText(SourceFormatting.COPYRIGHT_TEXT);
        gen.setCodeFormatting(true);
        gen.setCodeFormatterOptions(SourceFormatting.formatterOptions(root));
        gen.setCopyrightFields(false);
        gen.getGenPackages().get(0).setBasePackage("hed");
        gen.getGenPackages().get(0).setPrefix("HomeElectrical");
        gen.setCanGenerate(true);
        var resource = rs.createResource(URI.createPlatformResourceURI("/hed-model/src/main/resources/model/homeelectrical.genmodel", true));
        resource.getContents().add(gen);
        resource.save(java.util.Map.of(XMLResource.OPTION_ENCODING, "UTF-8"));
        GeneratorAdapterFactory.Descriptor.Registry.INSTANCE.addDescriptor(GenModelPackage.eNS_URI, GenModelGeneratorAdapterFactory.DESCRIPTOR);
        var generator = new Generator();
        generator.setInput(gen);
        for (var projectType : java.util.List.of(GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, GenBaseGeneratorAdapter.EDIT_PROJECT_TYPE)) {
            var diagnostic = generator.generate(gen, projectType, new BasicMonitor.Printing(System.out));
            if (diagnostic.getSeverity() >= Diagnostic.ERROR) {
                throw new IllegalStateException(diagnostic.toString());
            }
        }
        for (var directory : java.util.List.of("backend/hed-model/src/main/java/hed/homeelectrical", "backend/hed-model-edit/src/main/java/hed/homeelectrical/provider")) {
            try (var sources = Files.walk(root.resolve(directory))) {
                for (var source : sources.filter(path -> path.toString().endsWith(".java")).sorted().toList()) {
                    SourceFormatting.formatJava(source);
                }
            }
        }
        for (var xml : java.util.List.of("backend/hed-model/src/main/resources/model/homeelectrical.genmodel", "backend/hed-model/plugin.xml", "backend/hed-model-edit/plugin.xml")) {
            SourceFormatting.formatXml(root.resolve(xml));
        }

    }
}
