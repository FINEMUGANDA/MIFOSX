/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.financialyear.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.financialyear.domain.FinancialYear;
import org.mifosplatform.portfolio.financialyear.domain.FinancialYearRepository;
import org.mifosplatform.portfolio.financialyear.exception.FinancialYearNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class FinancialYearAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final FinancialYearRepository financialYearRepository;

    @Autowired
    public FinancialYearAssembler(final FromJsonHelper fromApiJsonHelper, final CodeValueRepositoryWrapper codeValueRepository,
                                  final FinancialYearRepository financialYearRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.codeValueRepository = codeValueRepository;
        this.financialYearRepository = financialYearRepository;
    }

    public Set<FinancialYear> fromParsedJson(final JsonElement element) {

        final Set<FinancialYear> financialYears = new HashSet<>();

        if (element.isJsonObject()) {
            final JsonObject topLevelJsonElement = element.getAsJsonObject();

            if (topLevelJsonElement.has("collateral") && topLevelJsonElement.get("collateral").isJsonArray()) {
                final JsonArray array = topLevelJsonElement.get("collateral").getAsJsonArray();
                final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
                for (int i = 0; i < array.size(); i++) {

                    final JsonObject financialYearItemElement = array.get(i).getAsJsonObject();

                    final Long id = this.fromApiJsonHelper.extractLongNamed("id", financialYearItemElement);
                    final Integer startYear = this.fromApiJsonHelper.extractIntegerNamed("startYear", financialYearItemElement, locale);
                    final Integer endYear = this.fromApiJsonHelper.extractIntegerNamed("endYear", financialYearItemElement, locale);
                    final LocalDate startDate = this.fromApiJsonHelper.extractLocalDateNamed("startDate", financialYearItemElement);
                    final LocalDate endDate = this.fromApiJsonHelper.extractLocalDateNamed("endDate", financialYearItemElement);
                    final boolean isCurrent = this.fromApiJsonHelper.extractBooleanNamed("isCurrent", financialYearItemElement);

                    if (id == null) {
                        financialYears.add(FinancialYear.from(startYear, endYear, startDate.toDate(), endDate.toDate(), isCurrent));
                    } else {
                        final FinancialYear financialYearItem = this.financialYearRepository.findOne(id);
                        if (financialYearItem == null) { throw new FinancialYearNotFoundException(id); }

                        financialYearItem.assembleFrom(startYear, endYear, startDate.toDate(), endDate.toDate(), isCurrent);

                        financialYears.add(financialYearItem);
                    }
                }
            } else {
                // no collaterals passed, use existing ones against loan
            }

        }

        return financialYears;
    }
}