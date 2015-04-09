/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.financialyear.service;

import org.mifosplatform.portfolio.financialyear.data.FinancialYearData;

import java.util.List;

public interface FinancialYearReadPlatformService {

    List<FinancialYearData> retrieveFinancialYears();

    FinancialYearData retrieveFinancialYear(Long financialYearId);

}
