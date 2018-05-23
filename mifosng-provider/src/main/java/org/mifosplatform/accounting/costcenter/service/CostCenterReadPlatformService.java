/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.costcenter.service;

import org.mifosplatform.accounting.costcenter.data.CostCenterData;
import org.mifosplatform.accounting.glaccount.data.GLAccountData;
import org.mifosplatform.accounting.glaccount.data.GLAccountDataForLookup;
import org.mifosplatform.accounting.glaccount.domain.GLAccountType;
import org.mifosplatform.accounting.journalentry.data.JournalEntryAssociationParametersData;

import java.util.Collection;
import java.util.List;

public interface CostCenterReadPlatformService {

    CostCenterData retrieveNewCostCenterDetails();

    CostCenterData retrieveCostCenterDetails(Long staffId, String costCenterType);

    List<CostCenterData> retrieveAllCostCenters();
}