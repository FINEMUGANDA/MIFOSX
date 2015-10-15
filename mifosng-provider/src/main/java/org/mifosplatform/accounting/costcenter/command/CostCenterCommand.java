/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.costcenter.command;

import org.mifosplatform.accounting.costcenter.api.CostCenterJsonInputParams;
import org.mifosplatform.accounting.glaccount.api.GLAccountJsonInputParams;
import org.mifosplatform.accounting.glaccount.domain.GLAccountType;
import org.mifosplatform.accounting.glaccount.domain.GLAccountUsage;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable command for adding a general Ledger Account
 */
public class CostCenterCommand {

    @SuppressWarnings("unused")
    private final Long staffId;
    private final List<Long> glAccounts;

    public CostCenterCommand(Long staffId, List<Long> glAccounts) {
        this.staffId = staffId;
        this.glAccounts = glAccounts;
    }

    public void validateForCreate() {

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("CostCenter");


        baseDataValidator.reset().parameter(CostCenterJsonInputParams.STAFF_ID.getValue()).value(this.staffId).notNull().longGreaterThanZero();

        baseDataValidator.reset().parameter(CostCenterJsonInputParams.GL_ACCOUNTS.getValue()).value(this.glAccounts.toArray()).notNull().arrayNotEmpty();

        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.", dataValidationErrors);
        }
    }

    public void validateForUpdate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("CostCenter");

        baseDataValidator.reset().parameter(CostCenterJsonInputParams.STAFF_ID.getValue()).value(this.staffId).notNull().longGreaterThanZero();

        baseDataValidator.reset().parameter(CostCenterJsonInputParams.GL_ACCOUNTS.getValue()).value(this.glAccounts.toArray()).notNull().arrayNotEmpty();

        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.", dataValidationErrors);
        }
    }

    public Long getStaffId() {
        return staffId;
    }

    public List<Long> getGlAccounts() {
        return glAccounts;
    }
}