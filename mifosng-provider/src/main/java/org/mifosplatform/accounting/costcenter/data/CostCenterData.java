package org.mifosplatform.accounting.costcenter.data;

import org.mifosplatform.accounting.glaccount.data.GLAccountData;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.staff.data.StaffData;

import java.util.Collection;
import java.util.List;

public class CostCenterData {

    private final StaffData staff;
	private final String costCenterType;
	private final CodeValueData nonStaff;
    private final List<GLAccountData> glAccounts;

    // templates
    final List<GLAccountData> glAccountOptions;
    final List<StaffData> staffOptions;
	final Collection<CodeValueData> nonStaffOptions;

    public CostCenterData(StaffData staff, CodeValueData nonStaff, List<GLAccountData> glAccounts) {
        this.staff = staff;
		this.nonStaff = nonStaff;
        this.glAccounts = glAccounts;
        this.glAccountOptions = null;
        this.staffOptions = null;
		this.nonStaffOptions = null;
		this.costCenterType = staff != null ? "staff" : "nonstaff";
    }

    public CostCenterData(final CostCenterData costCenter, final List<GLAccountData> glAccountOptions,
                         final List<StaffData> staffOptions, Collection<CodeValueData> nonStaffOptions) {
        this.staff = costCenter.staff;
		this.nonStaff = costCenter.nonStaff;
        this.glAccounts = costCenter.glAccounts;
        this.glAccountOptions = glAccountOptions;
        this.staffOptions = staffOptions;
		this.nonStaffOptions = nonStaffOptions;
		this.costCenterType = staff != null ? "staff" : "nonstaff";
    }

    public static CostCenterData sensibleDefaultsForNewCostCenterCreation() {
        final StaffData staff = null;
		final CodeValueData nonStaff = null;
        final List<GLAccountData> glAccounts= null;
        return new CostCenterData(staff, nonStaff, glAccounts);
    }

}
