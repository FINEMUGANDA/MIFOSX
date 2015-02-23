/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.useradministration.command;

import java.util.Collections;
import java.util.Map;

/**
 * Immutable command for updating permission expressions (initially maker-checker).
 */
public class PermissionExpressionsCommand {

    private final Map<String, String> expressions;

    public PermissionExpressionsCommand(final Map<String, String> expressionsMap) {
        this.expressions = expressionsMap;
    }

    public Map<String, String> getExpressions() {
        return expressions;
    }
}