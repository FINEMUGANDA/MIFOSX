/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.costcenter.service;

import org.mifosplatform.accounting.costcenter.data.CostCenterData;
import org.mifosplatform.accounting.glaccount.data.GLAccountData;
import org.mifosplatform.accounting.glaccount.domain.GLAccount;
import org.mifosplatform.accounting.glaccount.domain.GLAccountRepositoryWrapper;
import org.mifosplatform.accounting.glaccount.service.GLAccountReadPlatformService;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.organisation.staff.data.StaffData;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.mifosplatform.organisation.staff.domain.StaffRepositoryWrapper;
import org.mifosplatform.organisation.staff.service.StaffReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class CostCenterReadPlatformServiceImpl implements CostCenterReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final StaffRepositoryWrapper staffRepositoryWrapper;
    private final GLAccountRepositoryWrapper glAccountRepositoryWrapper;
    private final StaffReadPlatformService staffReadPlatformService;
    private final GLAccountReadPlatformService glAccountReadPlatformService;

    @Autowired
    public CostCenterReadPlatformServiceImpl(final RoutingDataSource dataSource,
                                             final StaffRepositoryWrapper staffRepositoryWrapper,
                                             final GLAccountRepositoryWrapper glAccountRepositoryWrapper,
                                             final StaffReadPlatformService staffReadPlatformService,
                                             final GLAccountReadPlatformService glAccountReadPlatformService) {
        this.staffRepositoryWrapper = staffRepositoryWrapper;
        this.glAccountRepositoryWrapper = glAccountRepositoryWrapper;
        this.staffReadPlatformService = staffReadPlatformService;
        this.glAccountReadPlatformService = glAccountReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public CostCenterData retrieveNewCostCenterDetails() {
        return CostCenterData.sensibleDefaultsForNewCostCenterCreation();
    }

    @Override
    public CostCenterData retrieveCostCenterDetails(Long staffId) {
        StaffData staffData = this.staffReadPlatformService.retrieveStaff(staffId);
        List<GLAccountData> glAccountDataCollection = this.glAccountReadPlatformService.retrieveAllRelatedToStaff(staffId);
//        Staff staff = this.staffRepositoryWrapper.findOneWithNotFoundDetection(staffId);

//        List<GLAccountData> glAccountDataCollection = new LinkedList<>();
//
//        for (GLAccount glAccount : staff.getGlAccounts()) {
//            glAccountDataCollection.add(glAccount.toData());
//        }

//        return new CostCenterData(staff.toData(), glAccountDataCollection);
        return new CostCenterData(staffData, glAccountDataCollection);
    }

    @Override
    public List<CostCenterData> retrieveAllCostCenters() {
        Collection<StaffData> staffs = this.staffReadPlatformService.retrieveAllStaffWithAssignedGlAccounts();
        List<CostCenterData> result = new LinkedList<>();
        for (StaffData staffData : staffs) {
            result.add(new CostCenterData(staffData, null));
        }
        return result;
    }
}