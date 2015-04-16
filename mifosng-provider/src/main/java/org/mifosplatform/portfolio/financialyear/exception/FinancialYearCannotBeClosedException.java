/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.financialyear.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class FinancialYearCannotBeClosedException extends AbstractPlatformDomainRuleException {

    /*** enum of reasons of why Loan Charge cannot be waived **/
    public static enum FINANCIALYEAR_CLOSE_REASON {
        HAS_NO_FINANCIAL_YEAR_CLOSE_PERMISSION;

        public String errorMessage() {
            if (name().toString().equalsIgnoreCase("HAS_NO_FINANCIAL_YEAR_CLOSE_PERMISSION")) { return "Not allowed to close the financial year"; }
            return name().toString();
        }

        public String errorCode() {
            if (name().toString().equalsIgnoreCase("HAS_NO_FINANCIAL_YEAR_CLOSE_PERMISSION")) { return "error.msg.financialyear.has.no.close.permission"; }
            return name().toString();
        }
    }

    public FinancialYearCannotBeClosedException(final FINANCIALYEAR_CLOSE_REASON reason, final Long financialYearId) {
        super(reason.errorCode(), reason.errorMessage(), financialYearId);
    }
}
