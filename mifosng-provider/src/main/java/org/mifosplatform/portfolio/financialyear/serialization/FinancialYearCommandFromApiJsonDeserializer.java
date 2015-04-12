/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.financialyear.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.serialization.AbstractFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.financialyear.api.FinancialYearApiConstants.FINANCIAL_YEAR_JSON_INPUT_PARAMS;
import org.mifosplatform.portfolio.financialyear.command.FinancialYearCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link org.mifosplatform.infrastructure.core.serialization.FromApiJsonDeserializer} for
 * {@link org.mifosplatform.portfolio.financialyear.command.FinancialYearCommand}'s.
 */
@Component
public final class FinancialYearCommandFromApiJsonDeserializer extends AbstractFromApiJsonDeserializer<FinancialYearCommand> {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public FinancialYearCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    @Override
    public FinancialYearCommand commandFromApiJson(final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        final Set<String> supportedParameters = FINANCIAL_YEAR_JSON_INPUT_PARAMS.getAllValues();
        supportedParameters.add("locale");
        supportedParameters.add("dateFormat");
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Integer startYear = this.fromApiJsonHelper.extractIntegerNamed(FINANCIAL_YEAR_JSON_INPUT_PARAMS.START_YEAR.getValue(), element, locale);
        final Integer endYear = this.fromApiJsonHelper.extractIntegerNamed(FINANCIAL_YEAR_JSON_INPUT_PARAMS.END_YEAR.getValue(), element, locale);
        final LocalDate startDate = this.fromApiJsonHelper.extractLocalDateNamed(FINANCIAL_YEAR_JSON_INPUT_PARAMS.START_DATE.getValue(), element);
        final LocalDate endDate = this.fromApiJsonHelper.extractLocalDateNamed(FINANCIAL_YEAR_JSON_INPUT_PARAMS.END_DATE.getValue(), element);
        final Boolean current = this.fromApiJsonHelper.extractBooleanNamed(FINANCIAL_YEAR_JSON_INPUT_PARAMS.CURRENT.getValue(), element);
        final Boolean closed = this.fromApiJsonHelper.extractBooleanNamed(FINANCIAL_YEAR_JSON_INPUT_PARAMS.CLOSED.getValue(), element);

        return new FinancialYearCommand(startYear, endYear, startDate, endDate, current, closed);
    }
}