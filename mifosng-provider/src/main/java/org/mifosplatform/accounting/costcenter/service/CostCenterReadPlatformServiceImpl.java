/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.costcenter.service;

import org.mifosplatform.accounting.costcenter.data.CostCenterData;
import org.mifosplatform.accounting.glaccount.data.GLAccountData;
import org.mifosplatform.accounting.glaccount.service.GLAccountReadPlatformService;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.organisation.staff.data.StaffData;
import org.mifosplatform.organisation.staff.exception.StaffNotFoundException;
import org.mifosplatform.organisation.staff.service.StaffReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class CostCenterReadPlatformServiceImpl implements CostCenterReadPlatformService {

	private final StaffReadPlatformService staffReadPlatformService;
	private final GLAccountReadPlatformService glAccountReadPlatformService;
	private final CodeValueReadPlatformService codeValueReadPlatformService;

	@Autowired
	public CostCenterReadPlatformServiceImpl(
			final StaffReadPlatformService staffReadPlatformService,
			final GLAccountReadPlatformService glAccountReadPlatformService,
			final CodeValueReadPlatformService codeValueReadPlatformService) {
		this.staffReadPlatformService = staffReadPlatformService;
		this.glAccountReadPlatformService = glAccountReadPlatformService;
		this.codeValueReadPlatformService = codeValueReadPlatformService;
	}


	@Override
	public CostCenterData retrieveNewCostCenterDetails() {
		return CostCenterData.sensibleDefaultsForNewCostCenterCreation();
	}

	@Override
	public CostCenterData retrieveCostCenterDetails(Long staffId, String costCenterType) {
		StaffData staffData = null;
		CodeValueData nonStaffData = null;
		List<GLAccountData> glAccountDataCollection;
		if (costCenterType.equals("staff")) {
			staffData = this.staffReadPlatformService.retrieveStaff(staffId);
			glAccountDataCollection = this.glAccountReadPlatformService.retrieveAllRelatedToStaff(staffId);
		} else {
			nonStaffData = this.codeValueReadPlatformService.retrieveCodeValue(staffId);
			glAccountDataCollection = this.glAccountReadPlatformService.retrieveAllRelatedToNonStaff(staffId);
		}
		return new CostCenterData(staffData, nonStaffData, glAccountDataCollection);
	}

	@Override
	public List<CostCenterData> retrieveAllCostCenters() {
		List<CostCenterData> result = new LinkedList<>();
		Collection<StaffData> staffs = this.staffReadPlatformService.retrieveAllStaffWithAssignedGlAccounts();
		for (StaffData staffData : staffs) {
			result.add(new CostCenterData(staffData, null, null));
		}
		Collection<CodeValueData> nonStaffs = this.codeValueReadPlatformService.retrieveAllNonStaffWithAssignedGlAccounts();
		for (CodeValueData nonStaff : nonStaffs) {
			result.add(new CostCenterData(null, nonStaff, null));
		}
		return result;
	}
}