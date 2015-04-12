/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.financialyear.data;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 * Immutable data object for Collateral data.
 */
public class FinancialYearData {

    private final Long id;
    private final Integer startYear;
    private final Integer endYear;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Boolean current;
    private final Boolean closed;

    public static FinancialYearData instance(final Long id, final Integer startYear, final Integer endYear, final LocalDate startDate, final LocalDate endDate, final Boolean current, final Boolean closed) {
        return new FinancialYearData(id, startYear, endYear, startDate, endDate, current, closed);
    }

    public FinancialYearData(Long id, Integer startYear, Integer endYear, LocalDate startDate, LocalDate endDate, Boolean current, Boolean closed) {
        this.id = id;
        this.startYear = startYear;
        this.endYear = endYear;
        this.startDate = startDate;
        this.endDate = endDate;
        this.current = current;
        this.closed = closed;
    }

    public FinancialYearData template(final FinancialYearData financialYearData) {
        return new FinancialYearData(financialYearData.id, financialYearData.startYear, financialYearData.endYear, financialYearData.startDate,
                financialYearData.endDate, financialYearData.current, financialYearData.closed);
    }

}