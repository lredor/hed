/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.diagrams.panel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Parameterized copies of the user's Canva SVGs; no model or filesystem mutation. */
@RestController
public class PanelAssetController {
    private final Map<String, String> templates;

    private final PanelCabinetSvg cabinet;

    public PanelAssetController() throws IOException {
        templates = Map.of("mcb", read("mcb"), "rccb", read("rccb"), "relay", read("relay"), "generic", read("generic"));
        cabinet = new PanelCabinetSvg();
    }

    @GetMapping(value = "/api/hed/panel/cabinet.svg", produces = "image/svg+xml;charset=UTF-8")
    public ResponseEntity<String> cabinet(@RequestParam(name = "rails", defaultValue = "3") int rails, @RequestParam(name = "pitch", defaultValue = "420") double pitch) {
        try {
            return ResponseEntity.ok().cacheControl(CacheControl.maxAge(java.time.Duration.ofHours(1))).body(cabinet.render(rails, pitch));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String read(String name) throws IOException {
        try (var stream = new ClassPathResource("hed/panel/" + name + ".svg").getInputStream()) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @GetMapping(value = "/api/hed/panel/device.svg", produces = "image/svg+xml;charset=UTF-8")
    public ResponseEntity<String> image(@RequestParam(name = "kind", defaultValue = "OTHER_MODULAR") String kind, @RequestParam(name = "current", defaultValue = "") String current,
            @RequestParam(name = "curve", defaultValue = "") String curve, @RequestParam(name = "residual", defaultValue = "") String residual,
            @RequestParam(name = "residualType", defaultValue = "") String residualType, @RequestParam(name = "manufacturer", defaultValue = "") String manufacturer) {
        String template = switch (kind) {
            case "RCCB" -> "rccb";
            case "IMPULSE_RELAY" -> "relay";
            case "MCB", "RCBO" -> "mcb";
            default -> "generic";
        };
        String svg = templates.get(template);
        svg = replace(svg, "Calibre en ampères" + (template.equals("rccb") ? "-texte" : ""), current.isBlank() ? "—" : current);
        svg = replace(svg, "Courbe", kind.equals("MCB") || kind.equals("RCBO") ? curve : "");
        svg = replace(svg, "Sensibilité différentielle-texte", residual.isBlank() ? "—" : residual);
        svg = replace(svg, "Type différentiel-texte", residualType.isBlank() ? "" : "Type " + residualType.strip());
        svg = replace(svg, "Marque" + (template.equals("rccb") ? "-texte" : ""), manufacturer);
        for (var id : java.util.List.of("Référence produit", "Gamme", "Pouvoir de coupure", "Classe")) {
            svg = replace(svg, id, "");
        }
        if (kind.equals("RCBO")) {
            svg = replace(svg, "Gamme", (residual.isBlank() ? "—" : residual) + " mA");
        }
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(java.time.Duration.ofHours(1))).body(svg);
    }

    static String replace(String svg, String id, String value) {
        var pattern = Pattern.compile("(<text\\b[^>]*\\bid=\"" + Pattern.quote(id) + "\"[^>]*?)(?:>[^<]*</text>|/>)");
        String escaped = value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
        var matcher = pattern.matcher(svg);
        return matcher.replaceAll(match -> Matcher.quoteReplacement(match.group(1) + ">" + escaped + "</text>"));
    }
}
