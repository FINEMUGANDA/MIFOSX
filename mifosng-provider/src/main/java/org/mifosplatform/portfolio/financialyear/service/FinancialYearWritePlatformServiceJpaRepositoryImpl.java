/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.financialyear.service;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.jobs.annotation.CronTarget;
import org.mifosplatform.infrastructure.jobs.service.JobName;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.financialyear.command.FinancialYearCommand;
import org.mifosplatform.portfolio.financialyear.domain.FinancialYear;
import org.mifosplatform.portfolio.financialyear.domain.FinancialYearRepository;
import org.mifosplatform.portfolio.financialyear.exception.FinancialYearNotFoundException;
import org.mifosplatform.portfolio.financialyear.serialization.FinancialYearCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class FinancialYearWritePlatformServiceJpaRepositoryImpl implements FinancialYearWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(FinancialYearWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final FinancialYearRepository financialYearRepository;
    private final FinancialYearCommandFromApiJsonDeserializer financialYearCommandFromApiJsonDeserializer;

    @Autowired
    public FinancialYearWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context, final FinancialYearRepository financialYearRepository,
                                                              final FinancialYearCommandFromApiJsonDeserializer financialYearCommandFromApiJsonDeserializer) {
        this.context = context;
        this.financialYearRepository = financialYearRepository;
        this.financialYearCommandFromApiJsonDeserializer = financialYearCommandFromApiJsonDeserializer;
    }

    @Transactional
    @Override
    public CommandProcessingResult addFinancialYear(final JsonCommand command) {

        this.context.authenticatedUser();
        final FinancialYearCommand financialYearCommand = this.financialYearCommandFromApiJsonDeserializer.commandFromApiJson(command.json());
        financialYearCommand.validateForCreate();

        try {

            final FinancialYear financialYear = FinancialYear.fromJson(command);

            if(financialYear.isCurrent()) {
                this.financialYearRepository.setAllCurrent(false);
            }
            this.financialYearRepository.save(financialYear);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(financialYear.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleFinancialYearDataIntegrityViolation(dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateFinancialYear(final Long financialYearId, final JsonCommand command) {

        this.context.authenticatedUser();
        final FinancialYearCommand financialYearCommand = this.financialYearCommandFromApiJsonDeserializer.commandFromApiJson(command.json());
        financialYearCommand.validateForUpdate();

        try {
            final FinancialYear financialYearForUpdate = this.financialYearRepository.findOne(financialYearId);
            if (financialYearForUpdate == null) {
                throw new FinancialYearNotFoundException(financialYearId);
            }

            final Map<String, Object> changes = financialYearForUpdate.update(command);

            if (!changes.isEmpty()) {
                if(financialYearCommand.isCurrent()) {
                    this.financialYearRepository.setAllCurrent(false, financialYearId);
                }
                this.financialYearRepository.save(financialYearForUpdate);
            }

            return new CommandProcessingResultBuilder()
                    .withCommandId(command.commandId())
                    .withEntityId(financialYearId)
                    .with(changes)
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleFinancialYearDataIntegrityViolation(dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteFinancialYear(final Long financialYearId, final Long commandId) {
        final FinancialYear financialYear = this.financialYearRepository.findById(financialYearId);
        if (financialYear == null) {
            throw new FinancialYearNotFoundException(financialYearId);
        }

        this.financialYearRepository.delete(financialYear);

        return new CommandProcessingResultBuilder().withCommandId(commandId).withEntityId(financialYearId).build();
    }

    private void handleFinancialYearDataIntegrityViolation(final DataIntegrityViolationException dve) {
        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.financial.year.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }


    @Transactional
    @Override
    @CronTarget(jobName = JobName.CREATE_NEW_FINANCIALYEAR)
    public void createNewFinancialYear() {
        try {
            List<FinancialYear> years = this.financialYearRepository.getLast(new LocalDate().toDate());

            if(years==null || years.isEmpty()) {
                List<FinancialYear> result = this.financialYearRepository.getLast();
                if(result!=null && !result.isEmpty()) {
                    this.financialYearRepository.setAllCurrent(false);
                    FinancialYear last = result.get(0);
                    FinancialYear fy = FinancialYear.from(last.getEndYear(), last.getEndYear() + 1, new LocalDate(last.getEndDate()).minusDays(1).toDate(), new LocalDate(last.getEndDate()).plusYears(1).toDate(), true, false);
                    this.financialYearRepository.saveAndFlush(fy);
                }
            }
        } catch (EmptyResultDataAccessException e) {
            logger.debug("No results found for updation of running balance ");
        }
    }
}