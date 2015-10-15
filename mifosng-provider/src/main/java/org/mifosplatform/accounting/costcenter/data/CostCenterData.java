package org.mifosplatform.accounting.costcenter.data;

import org.mifosplatform.accounting.glaccount.data.GLAccountData;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.staff.data.StaffData;

import java.util.Collection;
import java.util.List;

public class CostCenterData {

    private final StaffData staff;
    private final List<GLAccountData> glAccounts;

    // templates
    final List<GLAccountData> glAccountOptions;
    final List<StaffData> staffOptions;

    public CostCenterData(StaffData staff, List<GLAccountData> glAccounts) {
        this.staff = staff;
        this.glAccounts = glAccounts;
        this.glAccountOptions = null;
        this.staffOptions = null;
    }

    public CostCenterData(final CostCenterData costCenter, final List<GLAccountData> glAccountOptions,
                         final List<StaffData> staffOptions) {
        this.staff = costCenter.staff;
        this.glAccounts = costCenter.glAccounts;
        this.glAccountOptions = glAccountOptions;
        this.staffOptions = staffOptions;
    }

    public static CostCenterData sensibleDefaultsForNewCostCenterCreation() {
        final StaffData staff = null;
        final List<GLAccountData> glAccounts= null;
        return new CostCenterData(staff, glAccounts);
    }

}
