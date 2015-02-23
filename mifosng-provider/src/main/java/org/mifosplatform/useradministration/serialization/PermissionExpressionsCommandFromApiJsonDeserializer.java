/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.useradministration.serialization;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.serialization.AbstractFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.useradministration.command.PermissionExpressionsCommand;
import org.mifosplatform.useradministration.command.PermissionsCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link org.mifosplatform.infrastructure.core.serialization.FromApiJsonDeserializer} for
 * {@link org.mifosplatform.useradministration.command.PermissionExpressionsCommand}'s.
 */
@Component
public final class PermissionExpressionsCommandFromApiJsonDeserializer extends AbstractFromApiJsonDeserializer<PermissionExpressionsCommand> {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<>(Arrays.asList("expressions"));
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public PermissionExpressionsCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    @Override
    public PermissionExpressionsCommand commandFromApiJson(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        return this.fromApiJsonHelper.fromJson(json, PermissionExpressionsCommand.class);
    }
}