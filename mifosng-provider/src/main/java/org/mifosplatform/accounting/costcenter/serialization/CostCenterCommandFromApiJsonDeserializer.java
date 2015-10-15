/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.costcenter.serialization;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.mifosplatform.accounting.costcenter.api.CostCenterJsonInputParams;
import org.mifosplatform.accounting.costcenter.command.CostCenterCommand;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.serialization.AbstractFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link org.mifosplatform.infrastructure.core.serialization.FromApiJsonDeserializer} for
 * {@link org.mifosplatform.portfolio.loanaccount.guarantor.command.GuarantorCommand}'s.
 */
@Component
public final class CostCenterCommandFromApiJsonDeserializer extends AbstractFromApiJsonDeserializer<CostCenterCommand> {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public CostCenterCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonfromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonfromApiJsonHelper;
    }

    @Override
    public CostCenterCommand commandFromApiJson(final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        final Set<String> supportedParameters = CostCenterJsonInputParams.getAllValues();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final Long staffId = this.fromApiJsonHelper.extractLongNamed(CostCenterJsonInputParams.STAFF_ID.getValue(), element);
//        final Long[] glAccounts = this.fromApiJsonHelper.extractArrayNamed(CostCenterJsonInputParams.GL_ACCOUNTS.getValue(), element);

        final String[] glAccountIds = this.fromApiJsonHelper.extractArrayNamed(CostCenterJsonInputParams.GL_ACCOUNTS.getValue(), element);

        List<Long> glAccounts = new ArrayList<>();
        if (glAccountIds != null) {
            for (final String glAccountId : glAccountIds) {
                glAccounts.add(Long.valueOf(glAccountId));
            }
        }

        return new CostCenterCommand(staffId, glAccounts);
    }
}