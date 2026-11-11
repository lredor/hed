/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.help;

import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Opens the bundled user guide in the same language as the application's domain contributions. */
@RestController
public class UserGuideController {
    private final Locale locale;

    /**
     * Creates the language-aware entry point for the native Help menu.
     * @param locale the configured application locale, with English as the fallback
     */
    public UserGuideController(Locale locale) {
        this.locale = locale;
    }

    /**
     * Redirects to the static guide packaged alongside the application, including its local images and stylesheet.
     * @return a redirect to the French guide for French locales, or to the default English guide
     */
    @GetMapping("/api/hed/help")
    public ResponseEntity<Void> userGuide() {
        var language = "fr".equals(this.locale.getLanguage()) ? "fr" : "en";
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/help/" + language + "/index.html")).build();
    }
}
