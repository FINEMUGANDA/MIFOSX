package org.mifosplatform.infrastructure.security.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.security.exception.NoAuthorizationException;
import org.mifosplatform.useradministration.domain.AppUser;
import org.mifosplatform.useradministration.domain.PermissionExpression;
import org.mifosplatform.useradministration.domain.Role;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PermissionExpressionService {
    private static final Logger logger = LoggerFactory.getLogger(PermissionExpressionService.class);

    private JsonParser parser = new JsonParser();

    private final DataSource dataSource;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PermissionExpressionService(final RoutingDataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public boolean validate(final AppUser user, final CommandWrapper command) {
        List<PermissionExpression> expressions = new ArrayList<>();

        for (final Role role : user.getRoles()) {
            PermissionExpression expression = role.getPermissionExpression(command.getTaskPermissionName());
            if (expression!=null) {
                expressions.add(expression);
            }
        }

        if(!expressions.isEmpty()) {
            JsonElement json = null;

            if(command.getJson()!=null) {
                json = parser.parse(command.getJson());
                logger.debug("############ VALIDATE COMMAND JSON: {}", command.getJson());
            }

            Map<String, Object> vars = new HashMap<>();
            vars.put("command", command);
            vars.put("appUser", user);

            // mvel
            Boolean result = false;

            try {
                for(PermissionExpression expression : expressions) {
                    if(expression.getExpression().contains("json.")) {
                        vars.put("json", json);
                    }
                    if(expression.getExpression().contains("resource.")) {
                        Long resourceId = command.resourceId();

                        if(resourceId==null) {
                            if("LOAN".equals(command.entityName())) {
                                resourceId = command.getLoanId();
                            } else if("LOANPRODUCT".equals(command.entityName())) {
                                resourceId = command.getProductId();
                            } else if("CLIENT".equals(command.entityName())) {
                                resourceId = command.getClientId();
                            }
                        }

                        logger.debug("############ VALIDATE COMMAND RES: {}", resourceId);
                        logger.debug("############ VALIDATE COMMAND ENT: {}", command.entityName());
                        Map<String, Object> resource = jdbcTemplate.query("SELECT * FROM m_" + command.entityName().toLowerCase() + " WHERE id=?", new Object[]{resourceId}, new ColumnMapRowMapper()).get(0);
                        vars.put("resource", resource);
                        // TODO: remove this
                        logger.debug("############ VALIDATE COMMAND VARS: {}", vars);
                    }
                    if(expression.getExpression().contains("jdbc.")) {
                        vars.put("jdbc", jdbcTemplate);
                    }
                    result = (Boolean) MVEL.eval(expression.getExpression(), vars);
                    if(result) {
                        // as soon as we find a true expression we can stop
                        break;
                    }
                }
            } catch (Exception e) {
                logger.error(e.toString(), e);
            }

            // TODO: remove this
            logger.debug("############ VALIDATE COMMAND RESULT: {}", result);

            if(!result) {
                final String authorizationMessage = "User has no authority for permission expression: " + command.getTaskPermissionName();
                throw new NoAuthorizationException(authorizationMessage);
            }
        }

        // value means: true=we've found expressions and checked them, false=no expressions were found
        return !expressions.isEmpty();
    }
}
