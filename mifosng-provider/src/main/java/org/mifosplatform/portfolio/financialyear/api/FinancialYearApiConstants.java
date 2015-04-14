/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.financialyear.api;

import java.util.HashSet;
import java.util.Set;

public class FinancialYearApiConstants {

    public static final String FINANCIAL_YEAR_CODE_NAME = "FinancialYear";

    /***
     * Enum of all parameters passed in while creating/updating a collateral
     ***/
    public static enum FINANCIAL_YEAR_JSON_INPUT_PARAMS {
        FINANCIAL_YEAR_ID("financialYearId"), START_YEAR("startYear"), END_YEAR("endYear"), START_DATE("startDate"), END_DATE("endDate"), CURRENT("current"), CLOSED("closed");

        private final String value;

        private FINANCIAL_YEAR_JSON_INPUT_PARAMS(final String value) {
            this.value = value;
        }

        private static final Set<String> values = new HashSet<>();
        static {
            for (final FINANCIAL_YEAR_JSON_INPUT_PARAMS type : FINANCIAL_YEAR_JSON_INPUT_PARAMS.values()) {
                values.add(type.value);
            }
        }

        public static Set<String> getAllValues() {
            return values;
        }

        @Override
        public String toString() {
            return name().toString().replaceAll("_", " ");
        }

        public String getValue() {
            return this.value;
        }
    }
}
