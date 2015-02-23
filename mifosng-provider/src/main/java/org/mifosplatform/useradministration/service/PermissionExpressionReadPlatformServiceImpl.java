/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.useradministration.service;

import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.useradministration.data.PermissionExpressionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Service
public class PermissionExpressionReadPlatformServiceImpl implements PermissionExpressionReadPlatformService {

    private final static Logger logger = LoggerFactory.getLogger(PermissionReadPlatformService.class);

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public PermissionExpressionReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Collection<PermissionExpressionData> retrieveAllRolePermissionExpressions(final Long roleId) {

        final PermissionExpressionUsageDataMapper mapper = new PermissionExpressionUsageDataMapper();
        final String sql = mapper.rolePermissionExpressionSchema();
        logger.info("retrieveAllRolePermissionExpressions: " + sql);

        return this.jdbcTemplate.query(sql, mapper, new Object[] { roleId });
    }

    private static final class PermissionExpressionUsageDataMapper implements RowMapper<PermissionExpressionData> {

        @Override
        public PermissionExpressionData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final String code = rs.getString("code");
            final String entityName = rs.getString("entityName");
            final String actionName = rs.getString("actionName");
            final String expression = rs.getString("expression");
            final Boolean selected = rs.getBoolean("selected");

            return PermissionExpressionData.instance(code, entityName, actionName, expression, selected);
        }

        public String rolePermissionExpressionSchema() {
            return "select p.code, p.entity_name as entityName, p.action_name as actionName, p.expression, true as selected "
                    + " from m_permission_expression p where"
                    + " p.role_id = ? "
                    + " order by ifnull(entity_name, ''), p.code";
        }
    }

}