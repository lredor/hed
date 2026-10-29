/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.codegen;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.Document;

public final class SourceFormatting {
    public static final String COPYRIGHT_TEXT = """
            Copyright (c) 2026 Laurent Redor.
            This program and the accompanying materials
            are made available under the terms of the Eclipse Public License v2.0
            which accompanies this distribution, and is available at
            https://www.eclipse.org/legal/epl-2.0/

            SPDX-License-Identifier: EPL-2.0
            """.stripTrailing();

    private static final String JAVA_HEADER = """
            /*******************************************************************************
             * Copyright (c) 2026 Laurent Redor.
             * This program and the accompanying materials
             * are made available under the terms of the Eclipse Public License v2.0
             * which accompanies this distribution, and is available at
             * https://www.eclipse.org/legal/epl-2.0/
             *
             * SPDX-License-Identifier: EPL-2.0
             *******************************************************************************/
            """;

    private static List<String> tokens(String source, boolean ignoreBraces) throws Exception {
        IScanner scanner = ToolFactory.createScanner(false, false, false, "21");
        scanner.setSource(source.toCharArray());
        List<String> tokens = new ArrayList<>();
        int token;
        while ((token = scanner.getNextToken()) != ITerminalSymbols.TokenNameEOF) {
            String text = new String(scanner.getCurrentTokenSource());
            if (!ignoreBraces || !(text.equals("{") || text.equals("}"))) {
                tokens.add(text);
            }
        }
        return tokens;
    }

    private static String addBraces(Path path, String source, boolean checkOnly) throws Exception {
        var diagnostics = new DiagnosticCollector<JavaFileObject>();
        var file = new SimpleJavaFileObject(path.toUri(), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }
        };
        JavacTask task = (JavacTask) ToolProvider.getSystemJavaCompiler().getTask(null, null, diagnostics, List.of("--release", "21", "-proc:none"), null, List.of(file));
        var unit = task.parse().iterator().next();
        for (var declaration : unit.getImports()) {
            if (declaration.getQualifiedIdentifier().toString().endsWith(".*")) {
                throw new IllegalArgumentException("Use explicit imports in " + path);
            }
        }
        if (diagnostics.getDiagnostics().stream().anyMatch(d -> d.getKind() == Diagnostic.Kind.ERROR)) {
            throw new IllegalStateException(path + ": " + diagnostics.getDiagnostics());
        }
        var positions = Trees.instance(task).getSourcePositions();
        var insertions = new TreeMap<Integer, StringBuilder>(Comparator.reverseOrder());
        new TreeScanner<Void, Void>() {
            private void wrap(StatementTree body) {
                if (body != null && body.getKind() != Tree.Kind.BLOCK) {
                    if (checkOnly) {
                        throw new IllegalStateException("Missing braces: " + path + ": " + body);
                    }
                    int start = (int) positions.getStartPosition(unit, body);
                    int end = (int) positions.getEndPosition(unit, body);
                    if (start < 0 || end <= start) {
                        throw new IllegalStateException("Invalid body span");
                    }
                    insertions.computeIfAbsent(start, key -> new StringBuilder()).append("{\n");
                    insertions.computeIfAbsent(end, key -> new StringBuilder()).append("\n}");
                }
            }

            @Override
            public Void visitIf(IfTree node, Void p) {
                wrap(node.getThenStatement());
                if (!(node.getElseStatement() instanceof IfTree)) {
                    wrap(node.getElseStatement());
                }
                return super.visitIf(node, p);
            }

            @Override
            public Void visitForLoop(ForLoopTree node, Void p) {
                wrap(node.getStatement());
                return super.visitForLoop(node, p);
            }

            @Override
            public Void visitEnhancedForLoop(EnhancedForLoopTree node, Void p) {
                wrap(node.getStatement());
                return super.visitEnhancedForLoop(node, p);
            }

            @Override
            public Void visitWhileLoop(WhileLoopTree node, Void p) {
                wrap(node.getStatement());
                return super.visitWhileLoop(node, p);
            }

            @Override
            public Void visitDoWhileLoop(DoWhileLoopTree node, Void p) {
                wrap(node.getStatement());
                return super.visitDoWhileLoop(node, p);
            }
        }.scan(unit, null);
        var result = new StringBuilder(source);
        insertions.forEach((position, value) -> result.insert(position.intValue(), value));
        return result.toString();
    }

    private SourceFormatting() {
    }

    public static Map<String, String> formatterOptions(Path path) throws Exception {
        Map<String, String> options = new HashMap<>(DefaultCodeFormatterConstants.getEclipseDefaultSettings());
        Path profile = projectRoot(path).resolve("tools/style/JavaFormatter.xml");
        var document = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(profile.toFile());
        var profiles = document.getElementsByTagName("profile");
        boolean found = false;
        for (int i = 0; i < profiles.getLength(); i++) {
            var selected = (org.w3c.dom.Element) profiles.item(i);
            if ("Sirius".equals(selected.getAttribute("name"))) {
                var settings = selected.getElementsByTagName("setting");
                for (int j = 0; j < settings.getLength(); j++) {
                    var setting = (org.w3c.dom.Element) settings.item(j);
                    options.put(setting.getAttribute("id"), setting.getAttribute("value"));
                }
                found = true;
            }
        }
        if (!found) {
            throw new IllegalStateException("Missing Sirius Java formatter profile");
        }
        options.put("org.eclipse.jdt.core.compiler.source", "21");
        options.put("org.eclipse.jdt.core.compiler.compliance", "21");
        options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "21");
        return options;
    }

    private static Path projectRoot(Path path) {
        Path directory = Files.isDirectory(path) ? path : path.getParent();
        while (directory != null && !Files.isRegularFile(directory.resolve("tools/style/JavaFormatter.xml"))) {
            directory = directory.getParent();
        }
        if (directory == null) {
            throw new IllegalArgumentException("Cannot locate project style configuration from " + path);
        }
        return directory;
    }

    private static String explicitModelImports(Path path, String source) throws Exception {
        var wildcard = java.util.regex.Pattern.compile("(?m)^import hed\\.homeelectrical\\.\\*;[ \\t]*$");
        if (!wildcard.matcher(source).find()) {
            return source;
        }
        Path modelSources = projectRoot(path).resolve("backend/hed-model/src/main/java/hed/homeelectrical");
        var imports = new ArrayList<String>();
        try (var files = Files.list(modelSources)) {
            for (Path model : files.filter(file -> file.toString().endsWith(".java")).sorted().toList()) {
                String name = model.getFileName().toString().replace(".java", "");
                String declaration = "import hed.homeelectrical." + name + ";";
                boolean alreadyImported = java.util.regex.Pattern.compile("(?m)^" + java.util.regex.Pattern.quote(declaration) + "[ \\t]*$").matcher(source).find();
                if (!alreadyImported && java.util.regex.Pattern.compile("\\b" + name + "\\b").matcher(source).find()) {
                    imports.add(declaration);
                }
            }
        }
        return wildcard.matcher(source).replaceAll(java.util.regex.Matcher.quoteReplacement(String.join("\n", imports)));
    }

    private static String formattedJava(Path path) throws Exception {
        CodeFormatter formatter = ToolFactory.createCodeFormatter(formatterOptions(path));
        String original = Files.readString(path, java.nio.charset.StandardCharsets.UTF_8);
        String expanded = explicitModelImports(path, original);
        String braced = addBraces(path, expanded, false);
        if (!tokens(expanded, true).equals(tokens(braced, true))) {
            throw new IllegalStateException("Non-brace change: " + path);
        }
        var edit = formatter.format(CodeFormatter.K_COMPILATION_UNIT, braced, 0, braced.length(), 0, "\n");
        if (edit == null) {
            throw new IllegalStateException("Cannot format " + path);
        }
        var document = new Document(braced);
        edit.apply(document);
        String formatted = document.get().replaceAll("(?m)[\\t ]+$", "").stripTrailing() + "\n";
        if (!tokens(braced, false).equals(tokens(formatted, false))) {
            throw new IllegalStateException("Token change: " + path);
        }
        addBraces(path, formatted, true);
        formatted = replaceHeader(formatted);
        return formatted;
    }

    public static void formatJava(Path path) throws Exception {
        String formatted = formattedJava(path);
        if (!Files.readString(path, java.nio.charset.StandardCharsets.UTF_8).equals(formatted)) {
            Files.writeString(path, formatted, java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    private static String replaceHeader(String source) {
        String content = source.stripLeading();
        while (content.startsWith("/*")) {
            int end = content.indexOf("*/");
            if (end < 0 || !content.substring(0, end).contains("Copyright (c) 2026 Laurent Redor.")) {
                break;
            }
            content = content.substring(end + 2).stripLeading();
        }
        return JAVA_HEADER + content;
    }

    public static void formatXml(Path path) throws Exception {
        var factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        var document = factory.newDocumentBuilder().parse(path.toFile());
        removeIndentation(document);
        org.w3c.dom.Comment license = null;
        for (var child = document.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof org.w3c.dom.Comment comment && comment.getData().contains("Copyright (c) 2026 Laurent Redor.")) {
                license = comment;
                break;
            }
        }
        if (license == null) {
            license = document.createComment("");
            document.insertBefore(license, document.getDocumentElement());
        }
        license.setData("\n" + COPYRIGHT_TEXT + "\n");
        var transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        var result = new java.io.StringWriter();
        transformer.transform(new javax.xml.transform.dom.DOMSource(document), new javax.xml.transform.stream.StreamResult(result));
        var output = new StringBuilder();
        var tagPattern = java.util.regex.Pattern.compile("^(\\s*)<([\\w:.-]+)(\\s+[^<>]+?)(/?)>$");
        var attributePattern = java.util.regex.Pattern.compile("\\s+([\\w:.-]+)=(\"[^\"]*\")");
        for (String line : result.toString().replace("\r\n", "\n").replace("--><", "-->\n<").split("\n")) {
            var tag = tagPattern.matcher(line);
            if (line.length() > 120 && tag.matches()) {
                output.append(tag.group(1)).append('<').append(tag.group(2)).append('\n');
                var attributes = attributePattern.matcher(tag.group(3));
                var values = new ArrayList<String>();
                while (attributes.find()) {
                    values.add(attributes.group(1) + "=" + attributes.group(2));
                }
                for (int i = 0; i < values.size(); i++) {
                    output.append(tag.group(1)).append("    ").append(values.get(i));
                    if (i == values.size() - 1) {
                        output.append(tag.group(4)).append('>');
                    }
                    output.append('\n');
                }
            } else {
                output.append(line).append('\n');
            }
        }
        String formatted = output.toString().stripTrailing() + "\n";
        if (!Files.readString(path, java.nio.charset.StandardCharsets.UTF_8).equals(formatted)) {
            Files.writeString(path, formatted, java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    private static void removeIndentation(org.w3c.dom.Node node) {
        for (var child = node.getFirstChild(); child != null;) {
            var next = child.getNextSibling();
            if (child.getNodeType() == org.w3c.dom.Node.TEXT_NODE && child.getNodeValue().isBlank()) {
                node.removeChild(child);
            } else {
                removeIndentation(child);
            }
            child = next;
        }
    }

    public static void main(String[] args) throws Exception {
        boolean check = args.length > 0 && "--check".equals(args[0]);
        var files = new ArrayList<Path>();
        for (int index = check ? 1 : 0; index < args.length; index++) {
            Path path = Path.of(args[index]).toAbsolutePath();
            if (Files.isDirectory(path)) {
                for (String directory : List.of("backend", "tools/codegen/src")) {
                    Path sourceRoot = path.resolve(directory);
                    if (!Files.isDirectory(sourceRoot)) {
                        continue;
                    }
                    try (var sources = Files.walk(sourceRoot)) {
                        files.addAll(sources.filter(file -> file.toString().endsWith(".java")).filter(file -> !sourceRoot.relativize(file).toString().replace('\\', '/').contains("target/")).sorted()
                                .toList());
                    }
                }
            } else {
                files.add(path);
            }
        }
        var differences = new ArrayList<Path>();
        for (Path path : files) {
            if (check) {
                if (!path.toString().endsWith(".java")) {
                    throw new IllegalArgumentException("Formatting checks support Java sources: " + path);
                }
                if (!Files.readString(path, java.nio.charset.StandardCharsets.UTF_8).equals(formattedJava(path))) {
                    differences.add(path);
                }
            } else if (path.toString().endsWith(".java")) {
                formatJava(path);
            } else {
                formatXml(path);
            }
        }
        if (!differences.isEmpty()) {
            throw new IllegalStateException("Apply the Sirius formatter to: " + differences);
        }
    }
}
