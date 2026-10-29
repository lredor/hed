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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class Messages {
    private Messages() {
    }

    public static String text(Locale locale, String key, Object... args) {
        var bundle = ResourceBundle.getBundle("i18n.hed-messages", locale == null ? Locale.ENGLISH : locale, ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
        return MessageFormat.format(bundle.containsKey(key) ? bundle.getString(key) : key, args);
    }
}
