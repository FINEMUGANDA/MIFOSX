/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.costcenter.service;

import org.mifosplatform.accounting.costcenter.command.CostCenterCommand;
import org.mifosplatform.accounting.costcenter.exception.CostCenterGLAccountAlreadyUsedException;
import org.mifosplatform.accounting.costcenter.serialization.CostCenterCommandFromApiJsonDeserializer;
import org.mifosplatform.accounting.glaccount.domain.GLAccount;
import org.mifosplatform.accounting.glaccount.domain.GLAccountRepository;
import org.mifosplatform.accounting.journalentry.domain.JournalEntryRepository;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.mifosplatform.organisation.staff.domain.StaffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class CostCenterWritePlatformServiceJpaRepositoryImpl implements CostCenterWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(CostCenterWritePlatformServiceJpaRepositoryImpl.class);

    private final GLAccountRepository glAccountRepository;
    private final JournalEntryRepository glJournalEntryRepository;
    private final CostCenterCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final StaffRepository staffRepository;

    private final CodeValueRepositoryWrapper codeValueRepositoryWrapper;

    @Autowired
    public CostCenterWritePlatformServiceJpaRepositoryImpl(final GLAccountRepository glAccountRepository,
                                                           final JournalEntryRepository glJournalEntryRepository,
                                                           final CostCenterCommandFromApiJsonDeserializer fromApiJsonDeserializer,
                                                           final StaffRepository staffRepository,
                                                           final CodeValueRepositoryWrapper codeValueRepositoryWrapper) {
        this.glAccountRepository = glAccountRepository;
        this.glJournalEntryRepository = glJournalEntryRepository;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.staffRepository = staffRepository;
        this.codeValueRepositoryWrapper = codeValueRepositoryWrapper;
    }

    @Transactional
    @Override
    public CommandProcessingResult createCostCenter(final JsonCommand command) {
        try {
            final CostCenterCommand costCenterCommand = this.fromApiJsonDeserializer.commandFromApiJson(command.json());
            costCenterCommand.validateForCreate();

            Staff staff = this.staffRepository.findOne(costCenterCommand.getStaffId());

            List<GLAccount> glAccounts = new LinkedList<>();

            for (Long glAccountId : costCenterCommand.getGlAccounts()) {
                GLAccount glAccount = this.glAccountRepository.findOne(glAccountId);
                if (!glAccount.isAffectsLoan()) {
                    List<Staff> glAccountStaffs = glAccount.getStaffs();
                    if (glAccountStaffs != null && glAccountStaffs.size() > 0 && !glAccountStaffs.contains(staff)) {
                        throw new CostCenterGLAccountAlreadyUsedException(glAccount.getGlCode());
                    }
                }
                glAccounts.add(glAccount);
            }
            staff.setGlAccounts(glAccounts);

            this.staffRepository.saveAndFlush(staff);

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(staff.getId()).build();
        } catch (final DataIntegrityViolationException dve) {
            handleCostCenterDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateCostCenter(final Long staffId, final JsonCommand command) {
        try {
            final CostCenterCommand costCenterCommand = this.fromApiJsonDeserializer.commandFromApiJson(command.json());
            costCenterCommand.validateForUpdate();

            Staff oldStaff = this.staffRepository.findOne(staffId);
            Staff staff = this.staffRepository.findOne(costCenterCommand.getStaffId());

            List<GLAccount> glAccounts = new ArrayList<>();

            for (Long glAccountId : costCenterCommand.getGlAccounts()) {
                GLAccount glAccount = this.glAccountRepository.findOne(glAccountId);
                if (!glAccount.isAffectsLoan()) {
                    List<Staff> glAccountStaffs = glAccount.getStaffs();
                    if (glAccountStaffs != null && glAccountStaffs.size() > 0 && !glAccountStaffs.contains(staff) && !glAccountStaffs.contains(oldStaff)) {
                        throw new CostCenterGLAccountAlreadyUsedException(glAccount.getGlCode());
                    }
                }
                glAccounts.add(glAccount);
            }
            staff.setGlAccounts(glAccounts);
            if (staffId != costCenterCommand.getStaffId()) {
                oldStaff.setGlAccounts(new LinkedList<GLAccount>());
                this.staffRepository.saveAndFlush(oldStaff);
            }

            this.staffRepository.saveAndFlush(staff);

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(staff.getId()).build();
        } catch (final DataIntegrityViolationException dve) {
            handleCostCenterDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteCostCenter(final Long staffId) {

        Staff staff = this.staffRepository.findOne(staffId);

        List<GLAccount> glAccounts = new ArrayList<>();

        staff.setGlAccounts(glAccounts);

        this.staffRepository.saveAndFlush(staff);

        return new CommandProcessingResultBuilder().withEntityId(staffId).build();
    }

    /**
     * @param command
     * @param dve
     */
    private void handleCostCenterDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();
//        if (realCause.getMessage().contains("acc_gl_code")) {
//            final String glCode = command.stringValueOfParameterNamed(GLAccountJsonInputParams.GL_CODE.getValue());
//            throw new GLAccountDuplicateException(glCode);
//        }

        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.glAccount.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource GL Account: " + realCause.getMessage());
    }

}
