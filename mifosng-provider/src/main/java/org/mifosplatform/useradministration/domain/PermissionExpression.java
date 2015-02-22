/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.useradministration.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;

@Entity
@Table(name = "m_permission_expression")
public class PermissionExpression extends AbstractPersistable<Long> {

    @Column(name = "code", nullable = false, length = 100)
    private final String code;

    @Column(name = "entity_name", nullable = true, length = 100)
    private final String entityName;

    @Column(name = "action_name", nullable = true, length = 100)
    private final String actionName;

    @Column(name = "can_maker_checker", nullable = false)
    private boolean canMakerChecker;

    @Column(name = "expression", nullable = true, length = 255)
    private String expression;

    @Column(name = "role_id", nullable = false)
    private final Long roleId;

    public PermissionExpression(final Long roleId, final String entityName, final String actionName, final String expression) {
        this.roleId = roleId;
        this.entityName = entityName;
        this.actionName = actionName;
        this.code = actionName + "_" + entityName;
        this.canMakerChecker = false;
        this.expression = expression;
    }

    protected PermissionExpression() {
        this.roleId = null;
        this.entityName = null;
        this.actionName = null;
        this.code = null;
        this.canMakerChecker = false;
        this.expression = null;
    }

    public boolean hasCode(final String checkCode) {
        return this.code.equalsIgnoreCase(checkCode);
    }

    public String getCode() {
        return this.code;
    }

    public boolean hasMakerCheckerEnabled() {
        return this.canMakerChecker;
    }

    public boolean enableMakerChecker(final boolean canMakerChecker) {
        final boolean isUpdatedValueSame = this.canMakerChecker == canMakerChecker;
        this.canMakerChecker = canMakerChecker;

        return !isUpdatedValueSame;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "PermissionExpression{" +
                "code='" + code + '\'' +
                ", roleId='" + roleId + '\'' +
                ", entityName='" + entityName + '\'' +
                ", actionName='" + actionName + '\'' +
                ", canMakerChecker=" + canMakerChecker +
                ", expression='" + expression + '\'' +
                '}';
    }

    /**
    public static class PermissionExpressionPk implements Serializable {
        protected String code;
        protected Long roleId;

        public PermissionExpressionPk() {
        }

        public PermissionExpressionPk(String code, Long roleId) {
            this.code = code;
            this.roleId = roleId;
        }
    }
     */
}