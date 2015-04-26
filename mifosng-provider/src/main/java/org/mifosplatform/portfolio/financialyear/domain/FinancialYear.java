/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.financialyear.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.portfolio.financialyear.api.FinancialYearApiConstants.FINANCIAL_YEAR_JSON_INPUT_PARAMS;
import org.mifosplatform.portfolio.financialyear.exception.FinancialYearCannotBeUpdatedException;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "m_financial_year")
public class FinancialYear extends AbstractPersistable<Long> {

    @Column(name = "start_year", nullable = false)
    private Integer startYear;

    @Column(name = "end_year", nullable = false)
    private Integer endYear;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "current", nullable = false)
    private boolean current = false;

    @Column(name = "closed", nullable = false)
    private boolean closed = false;

    protected FinancialYear() {
        //
    }

    private FinancialYear(Integer startYear, Integer endYear, Date startDate, Date endDate, boolean current, boolean closed) {
        this.startYear = startYear;
        this.endYear = endYear;
        this.startDate = startDate;
        this.endDate = endDate;
        this.current = current;
        this.closed = closed;
    }

    public static FinancialYear from(Integer startYear, Integer endYear, Date startDate, Date endDate, boolean isCurrent, boolean closed) {
        return new FinancialYear(startYear, endYear, startDate, endDate, isCurrent, closed);
    }
    public static FinancialYear fromJson(final JsonCommand command) {
        Integer startYear = command.integerValueOfParameterNamed(FINANCIAL_YEAR_JSON_INPUT_PARAMS.START_YEAR.getValue());
        Integer endYear = command.integerValueOfParameterNamed(FINANCIAL_YEAR_JSON_INPUT_PARAMS.END_YEAR.getValue());
        LocalDate startDate = command.localDateValueOfParameterNamed(FINANCIAL_YEAR_JSON_INPUT_PARAMS.START_DATE.getValue());
        LocalDate endDate = command.localDateValueOfParameterNamed(FINANCIAL_YEAR_JSON_INPUT_PARAMS.END_DATE.getValue());
        boolean current = command.booleanPrimitiveValueOfParameterNamed(FINANCIAL_YEAR_JSON_INPUT_PARAMS.CURRENT.getValue());
        boolean closed = command.booleanPrimitiveValueOfParameterNamed(FINANCIAL_YEAR_JSON_INPUT_PARAMS.CLOSED.getValue());
        return new FinancialYear(startYear, endYear, startDate.toDate(), endDate.toDate(), current, closed);
    }

    public void assembleFrom(Integer startYear, Integer endYear, Date startDate, Date endDate, boolean isCurrent, boolean closed) {
        this.startYear = startYear;
        this.endYear = endYear;
        this.startDate = startDate;
        this.endDate = endDate;
        this.current = isCurrent;
        this.closed = closed;
    }

    public Map<String, Object> update(final JsonCommand command) {
        if(this.closed) {
            throw new FinancialYearCannotBeUpdatedException(FinancialYearCannotBeUpdatedException.FINANCIALYEAR_CANNOT_BE_UPDATED_REASON.FINANCIALYEAR_CLOSED, getId());
        }

        final Map<String, Object> actualChanges = new LinkedHashMap<>(5);

        final String startYearParamName = FINANCIAL_YEAR_JSON_INPUT_PARAMS.START_YEAR.getValue();
        if (command.isChangeInIntegerParameterNamed(startYearParamName, this.startYear)) {
            final Integer newValue = command.integerValueOfParameterNamed(startYearParamName);
            actualChanges.put(startYearParamName, newValue);
            this.startYear = newValue;
        }

        final String endYearParamName = FINANCIAL_YEAR_JSON_INPUT_PARAMS.END_YEAR.getValue();
        if (command.isChangeInIntegerParameterNamed(endYearParamName, this.endYear)) {
            final Integer newValue = command.integerValueOfParameterNamed(endYearParamName);
            actualChanges.put(endYearParamName, newValue);
            this.endYear = newValue;
        }

        final String startDateParamName = FINANCIAL_YEAR_JSON_INPUT_PARAMS.START_DATE.getValue();
        if (command.isChangeInLocalDateParameterNamed(startDateParamName, new LocalDate(this.startDate))) {
            final LocalDate newValue = command.localDateValueOfParameterNamed(startDateParamName);
            actualChanges.put(startDateParamName, newValue);
            this.startDate = newValue.toDate();
        }

        final String endDateParamName = FINANCIAL_YEAR_JSON_INPUT_PARAMS.END_DATE.getValue();
        if (command.isChangeInLocalDateParameterNamed(endDateParamName, new LocalDate(this.endDate))) {
            final LocalDate newValue = command.localDateValueOfParameterNamed(endDateParamName);
            actualChanges.put(endDateParamName, newValue);
            this.endDate = newValue.toDate();
        }

        final String currentParamName = FINANCIAL_YEAR_JSON_INPUT_PARAMS.CURRENT.getValue();
        if (command.isChangeInBooleanParameterNamed(currentParamName, this.current)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(currentParamName);
            actualChanges.put(currentParamName, newValue);
            this.current = newValue;
        }

        final String closedParamName = FINANCIAL_YEAR_JSON_INPUT_PARAMS.CLOSED.getValue();
        if (command.isChangeInBooleanParameterNamed(closedParamName, this.closed)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(closedParamName);
            actualChanges.put(closedParamName, newValue);
            this.closed = newValue;
        }

        return actualChanges;
    }

    public boolean isCurrent() {
        return current;
    }

    public boolean isClosed() {
        return closed;
    }

    public Integer getStartYear() {
        return startYear;
    }

    public Integer getEndYear() {
        return endYear;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        final FinancialYear rhs = (FinancialYear) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(getId(), rhs.getId())
                .append(this.startYear, rhs.startYear)
                .append(this.endYear, rhs.endYear)
                .append(this.startDate, rhs.startDate)
                .append(this.endDate, rhs.endDate)
                .append(this.current, rhs.current)
                .append(this.closed, rhs.closed)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(3, 5)
                .append(getId())
                .append(this.startYear)
                .append(this.endYear)
                .append(this.startDate)
                .append(this.endDate)
                .append(this.current)
                .append(this.closed)
                .toHashCode();
    }
}