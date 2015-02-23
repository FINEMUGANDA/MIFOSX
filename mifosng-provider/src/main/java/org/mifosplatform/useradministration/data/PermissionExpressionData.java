/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.useradministration.data;

/**
 * Immutable representation of permissions
 */
public class PermissionExpressionData {

    @SuppressWarnings("unused")
    private final String code;
    @SuppressWarnings("unused")
    private final String entityName;
    @SuppressWarnings("unused")
    private final String actionName;
    @SuppressWarnings("unused")
    private final Boolean selected;
    @SuppressWarnings("unused")
    private final String expression;

    public static PermissionExpressionData from(final String permissionCode, final String permissionExpression, final boolean isSelected) {
        return new PermissionExpressionData(permissionCode, null, null, permissionExpression, isSelected);
    }

    public static PermissionExpressionData instance(final String code, final String entityName, final String actionName, final String expression, final Boolean selected) {
        return new PermissionExpressionData(code, entityName, actionName, expression, selected);
    }

    private PermissionExpressionData(final String code, final String entityName, final String actionName, final String expression, final Boolean selected) {
        this.code = code;
        this.entityName = entityName;
        this.actionName = actionName;
        this.expression = expression;
        this.selected = selected;
    }
}