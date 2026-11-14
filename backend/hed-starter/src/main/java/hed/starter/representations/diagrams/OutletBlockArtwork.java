/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.diagrams;

import hed.domain.HedOutletBlocks;
import hed.homeelectrical.OutletBlock;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Renders catalog-shaped assemblies with the same local MDI outlet pictogram used elsewhere. */
@RestController
public final class OutletBlockArtwork {
    private final String outlet;

    /** Loads the bundled MIT-licensed MDI outlet artwork once for server-side composition. */
    public OutletBlockArtwork() {
        try (var stream = OutletBlockArtwork.class.getResourceAsStream("/hed/mdi/power-socket-fr.svg")) {
            if (stream == null) {
                throw new IllegalStateException("Missing outlet artwork");
            }
            var svg = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            this.outlet = svg.substring(svg.indexOf('>') + 1, svg.lastIndexOf("</svg>"));
        } catch (IOException error) {
            throw new IllegalStateException("Cannot read outlet artwork", error);
        }
    }

    /**
     * Resolves an assembly's persistent image URL from its actual contained structure.
     * @param block the physical assembly
     * @return an application-local image URL
     */
    public static String url(OutletBlock block) {
        return "/api/images" + imagePath(block);
    }

    /**
     * Resolves the image identifier expected by Sirius's native image pipeline.
     * Native diagram image URLs receive the /api/images prefix when sent to the browser.
     * @param block the physical assembly
     * @return the image path without the API prefix
     */
    public static String imagePath(OutletBlock block) {
        return "/hed/outlet-block.svg?rows=" + block.getRows().size() + "&columns=" + columns(block);
    }

    /**
     * Finds the widest row of an assembly.
     * @param block the physical assembly
     * @return at least one pictogram column
     */
    public static int columns(OutletBlock block) {
        return block.getRows().stream().mapToInt(row -> row.getOutlets().size()).max().orElse(1);
    }

    /**
     * Serves a complete grid as one image, so individual outlets have no draggable graphical views.
     * @param rows positive row count
     * @param columns positive column count
     * @return the composed SVG, or HTTP 400 for unsupported dimensions
     */
    @GetMapping(value = { "/api/images/hed/outlet-block.svg", "/api/hed/outlet-block.svg" }, produces = "image/svg+xml;charset=UTF-8")
    public ResponseEntity<String> image(@RequestParam("rows") int rows, @RequestParam("columns") int columns) {
        try {
            HedOutletBlocks.requireDimensions(rows, columns);
        } catch (IllegalArgumentException error) {
            return ResponseEntity.badRequest().build();
        }
        var svg = new StringBuilder("<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 ").append(columns * 24).append(' ').append(rows * 24).append("\">");
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                svg.append("<g transform=\"translate(").append(column * 24).append(' ').append(row * 24).append(")\">").append(this.outlet).append("</g>");
            }
        }
        svg.append("</svg>");
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.ofHours(1))).body(svg.toString());
    }
}
