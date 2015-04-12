/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.financialyear.command;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.portfolio.financialyear.api.FinancialYearApiConstants.FINANCIAL_YEAR_JSON_INPUT_PARAMS;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable command for creating or updating details of a FinancialYear.
 */
public class FinancialYearCommand {

    private Integer startYear;
    private Integer endYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean current;
    private Boolean closed;

    public FinancialYearCommand(Integer startYear, Integer endYear, LocalDate startDate, LocalDate endDate, Boolean current, Boolean closed) {
        this.startYear = startYear;
        this.endYear = endYear;
        this.startDate = startDate;
        this.endDate = endDate;
        this.current = current;
        this.closed = closed;
    }

    public Integer getStartYear() {
        return startYear;
    }

    public Integer getEndYear() {
        return endYear;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Boolean isCurrent() {
        return current;
    }

    public Boolean isClosed() {
        return closed;
    }

    public void validateForCreate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("financial.year");
        final LocalDate currentDate = new LocalDate();

        baseDataValidator.reset().parameter(FINANCIAL_YEAR_JSON_INPUT_PARAMS.START_YEAR.getValue()).value(this.startYear).notNull().ignoreIfNull().integerGreaterThanZero().inMinMaxRange(currentDate.getYear()-10, currentDate.getYear() + 1);
        baseDataValidator.reset().parameter(FINANCIAL_YEAR_JSON_INPUT_PARAMS.END_YEAR.getValue()).value(this.endYear).notNull().ignoreIfNull().integerGreaterThanZero().inMinMaxRange(currentDate.getYear()-10, currentDate.getYear() + 1);
        if (this.startYear != null && this.endYear != null) {
            baseDataValidator.reset().parameter(FINANCIAL_YEAR_JSON_INPUT_PARAMS.END_YEAR.getValue()).value(this.endYear).integerGreaterThanNumber(this.startYear);

            final LocalDate startDate = new LocalDate(this.startYear, 1, 1);
            final LocalDate endDate = new LocalDate(this.endYear, 12, 31);
            baseDataValidator.reset().parameter(FINANCIAL_YEAR_JSON_INPUT_PARAMS.START_DATE.getValue()).value(this.startDate).notNull().ignoreIfNull().validateDateBetween(startDate, endDate);
            baseDataValidator.reset().parameter(FINANCIAL_YEAR_JSON_INPUT_PARAMS.END_DATE.getValue()).value(this.endDate).notNull().ignoreIfNull().validateDateAfter(startDate).validateDateBetween(startDate, endDate);
        }
        baseDataValidator.reset().parameter(FINANCIAL_YEAR_JSON_INPUT_PARAMS.CURRENT.getValue()).value(this.current).notNull().ignoreIfNull().validateForBooleanValue();
        baseDataValidator.reset().parameter(FINANCIAL_YEAR_JSON_INPUT_PARAMS.CLOSED.getValue()).value(this.closed).notNull().ignoreIfNull().validateForBooleanValue();

        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                    "Validation errors exist.", dataValidationErrors);
        }
    }

    public void validateForUpdate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("financial.year");
        final LocalDate currentDate = new LocalDate();

        baseDataValidator.reset().parameter(FINANCIAL_YEAR_JSON_INPUT_PARAMS.START_YEAR.getValue()).value(this.startYear).notNull().ignoreIfNull().integerGreaterThanZero().inMinMaxRange(currentDate.getYear() - 10, currentDate.getYear() + 1);
        baseDataValidator.reset().parameter(FINANCIAL_YEAR_JSON_INPUT_PARAMS.END_YEAR.getValue()).value(this.endYear).notNull().ignoreIfNull().integerGreaterThanZero().inMinMaxRange(currentDate.getYear() - 10, currentDate.getYear() + 1);
        if (this.startYear != null && this.endYear != null) {
            baseDataValidator.reset().parameter(FINANCIAL_YEAR_JSON_INPUT_PARAMS.END_YEAR.getValue()).value(this.endYear).integerGreaterThanNumber(this.startYear);

            final LocalDate startDate = new LocalDate(this.startYear, 1, 1);
            final LocalDate endDate = new LocalDate(this.endYear, 12, 31);
            baseDataValidator.reset().parameter(FINANCIAL_YEAR_JSON_INPUT_PARAMS.START_DATE.getValue()).value(this.startDate).notNull().ignoreIfNull().validateDateBetween(startDate, endDate);
            baseDataValidator.reset().parameter(FINANCIAL_YEAR_JSON_INPUT_PARAMS.END_DATE.getValue()).value(this.endDate).notNull().ignoreIfNull().validateDateAfter(startDate).validateDateBetween(startDate, endDate);
        }
        baseDataValidator.reset().parameter(FINANCIAL_YEAR_JSON_INPUT_PARAMS.CURRENT.getValue()).value(this.current).notNull().ignoreIfNull().validateForBooleanValue();
        baseDataValidator.reset().parameter(FINANCIAL_YEAR_JSON_INPUT_PARAMS.CLOSED.getValue()).value(this.closed).notNull().ignoreIfNull().validateForBooleanValue();

        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                    "Validation errors exist.", dataValidationErrors);
        }
    }
}