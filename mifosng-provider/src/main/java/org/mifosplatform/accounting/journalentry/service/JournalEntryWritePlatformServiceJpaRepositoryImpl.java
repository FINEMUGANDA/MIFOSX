/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.journalentry.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.accounting.closure.domain.GLClosure;
import org.mifosplatform.accounting.closure.domain.GLClosureRepository;
import org.mifosplatform.accounting.glaccount.data.GLAccountDataForLookup;
import org.mifosplatform.accounting.glaccount.domain.GLAccount;
import org.mifosplatform.accounting.glaccount.domain.GLAccountRepository;
import org.mifosplatform.accounting.glaccount.exception.GLAccountNotFoundException;
import org.mifosplatform.accounting.glaccount.service.GLAccountReadPlatformService;
import org.mifosplatform.accounting.journalentry.api.JournalEntryJsonInputParams;
import org.mifosplatform.accounting.journalentry.command.JournalEntryCommand;
import org.mifosplatform.accounting.journalentry.command.SingleDebitOrCreditEntryCommand;
import org.mifosplatform.accounting.journalentry.data.LoanDTO;
import org.mifosplatform.accounting.journalentry.data.SavingsDTO;
import org.mifosplatform.accounting.journalentry.domain.JournalEntry;
import org.mifosplatform.accounting.journalentry.domain.JournalEntryRepository;
import org.mifosplatform.accounting.journalentry.domain.JournalEntryType;
import org.mifosplatform.accounting.journalentry.exception.JournalEntriesNotFoundException;
import org.mifosplatform.accounting.journalentry.exception.JournalEntryInvalidException;
import org.mifosplatform.accounting.journalentry.exception.JournalEntryInvalidException.GL_JOURNAL_ENTRY_INVALID_REASON;
import org.mifosplatform.accounting.journalentry.serialization.JournalEntryCommandFromApiJsonDeserializer;
import org.mifosplatform.accounting.rule.domain.AccountingRule;
import org.mifosplatform.accounting.rule.domain.AccountingRuleRepository;
import org.mifosplatform.accounting.rule.exception.AccountingRuleNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.domain.*;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.portfolio.financialyear.domain.FinancialYearRepository;
import org.mifosplatform.portfolio.paymentdetail.domain.PaymentDetail;
import org.mifosplatform.portfolio.paymentdetail.service.PaymentDetailWritePlatformService;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class JournalEntryWritePlatformServiceJpaRepositoryImpl implements JournalEntryWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(JournalEntryWritePlatformServiceJpaRepositoryImpl.class);

    private final GLClosureRepository glClosureRepository;
    private final GLAccountRepository glAccountRepository;
    private final JournalEntryRepository glJournalEntryRepository;
    private final OfficeRepository officeRepository;
    private final FinancialYearRepository financialYearRepository;
    private final AccountingProcessorForLoanFactory accountingProcessorForLoanFactory;
    private final AccountingProcessorForSavingsFactory accountingProcessorForSavingsFactory;
    private final AccountingProcessorHelper helper;
    private final JournalEntryCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final AccountingRuleRepository accountingRuleRepository;
    private final GLAccountReadPlatformService glAccountReadPlatformService;
    private final OrganisationCurrencyRepository organisationCurrencyRepo;
    private final OrganisationCurrencyRepositoryWrapper organisationCurrencyRepository;
    private final PlatformSecurityContext context;
    private final PaymentDetailWritePlatformService paymentDetailWritePlatformService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JournalEntryWritePlatformServiceJpaRepositoryImpl(final GLClosureRepository glClosureRepository,
            final JournalEntryRepository glJournalEntryRepository, final OfficeRepository officeRepository,
            final GLAccountRepository glAccountRepository, final FinancialYearRepository financialYearRepository, final JournalEntryCommandFromApiJsonDeserializer fromApiJsonDeserializer,
            final AccountingProcessorHelper accountingProcessorHelper, final AccountingRuleRepository accountingRuleRepository,
            final AccountingProcessorForLoanFactory accountingProcessorForLoanFactory,
            final AccountingProcessorForSavingsFactory accountingProcessorForSavingsFactory,
            final GLAccountReadPlatformService glAccountReadPlatformService,
            final OrganisationCurrencyRepository organisationCurrencyRepo,
            final OrganisationCurrencyRepositoryWrapper organisationCurrencyRepository, final PlatformSecurityContext context,
            final PaymentDetailWritePlatformService paymentDetailWritePlatformService, final RoutingDataSource dataSource) {
        this.glClosureRepository = glClosureRepository;
        this.officeRepository = officeRepository;
        this.glJournalEntryRepository = glJournalEntryRepository;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.glAccountRepository = glAccountRepository;
        this.financialYearRepository = financialYearRepository;
        this.accountingProcessorForLoanFactory = accountingProcessorForLoanFactory;
        this.accountingProcessorForSavingsFactory = accountingProcessorForSavingsFactory;
        this.helper = accountingProcessorHelper;
        this.accountingRuleRepository = accountingRuleRepository;
        this.glAccountReadPlatformService = glAccountReadPlatformService;
        this.organisationCurrencyRepo = organisationCurrencyRepo;
        this.organisationCurrencyRepository = organisationCurrencyRepository;
        this.context = context;
        this.paymentDetailWritePlatformService = paymentDetailWritePlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    @Override
    public CommandProcessingResult createJournalEntry(final JsonCommand command) {
        try {
            final JournalEntryCommand journalEntryCommand = this.fromApiJsonDeserializer.commandFromApiJson(command.json());
            journalEntryCommand.validateForCreate();

            // check office is valid
            final Long officeId = command.longValueOfParameterNamed(JournalEntryJsonInputParams.OFFICE_ID.getValue());
            final Office office = this.officeRepository.findOne(officeId);
            if (office == null) { throw new OfficeNotFoundException(officeId); }

            final Long accountRuleId = command.longValueOfParameterNamed(JournalEntryJsonInputParams.ACCOUNTING_RULE.getValue());

            //final Boolean opening = command.booleanObjectValueOfParameterNamed(JournalEntryJsonInputParams.OPENING.getValue());

            validateBusinessRulesForJournalEntries(journalEntryCommand);

            /** Capture payment details **/
            final Map<String, Object> changes = new LinkedHashMap<>();
            final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);

            /** Set a transaction Id and save these Journal entries **/
            final Date transactionDate = command.DateValueOfParameterNamed(JournalEntryJsonInputParams.TRANSACTION_DATE.getValue());
            final String transactionId = generateTransactionId(officeId);
            final String referenceNumber = command.stringValueOfParameterNamed(JournalEntryJsonInputParams.REFERENCE_NUMBER.getValue());

            BigDecimal creditExchangeRate = BigDecimal.ONE;
            BigDecimal debitExchangeRate = BigDecimal.ONE;

            BigDecimal[] exchangeRates = calculateExchangeRate(journalEntryCommand.getCredits(), journalEntryCommand.getDebits());
            creditExchangeRate = exchangeRates[0];
            debitExchangeRate = exchangeRates[1];

            if (accountRuleId != null) {

                final AccountingRule accountingRule = this.accountingRuleRepository.findOne(accountRuleId);
                if (accountingRule == null) { throw new AccountingRuleNotFoundException(accountRuleId); }

                if (accountingRule.getAccountToCredit() == null) {
                    if (journalEntryCommand.getCredits() == null) {
                        throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.NO_DEBITS_OR_CREDITS, null, null, null);
                    }
                    if (journalEntryCommand.getDebits() != null) {
                        checkDebitOrCreditAccountsAreValid(accountingRule, journalEntryCommand.getCredits(), journalEntryCommand.getDebits());
                        checkDebitAndCreditAmounts(journalEntryCommand.getCredits(), journalEntryCommand.getDebits());
                    }

                    saveAllDebitOrCreditEntries(journalEntryCommand, office, paymentDetail, creditExchangeRate, transactionDate, journalEntryCommand.getCredits(), transactionId, JournalEntryType.CREDIT, referenceNumber);
                } else {
                    final GLAccount creditAccountHead = accountingRule.getAccountToCredit();
                    validateGLAccountForTransaction(creditAccountHead);
                    validateDebitOrCreditArrayForExistingGLAccount(creditAccountHead, journalEntryCommand.getCredits());
                    saveAllDebitOrCreditEntries(journalEntryCommand, office, paymentDetail, creditExchangeRate, transactionDate, journalEntryCommand.getCredits(), transactionId, JournalEntryType.CREDIT, referenceNumber);
                }

                if (accountingRule.getAccountToDebit() == null) {
                    if (journalEntryCommand.getDebits() == null) {
                        throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.NO_DEBITS_OR_CREDITS, null, null, null);
                    }
                    if (journalEntryCommand.getCredits() != null) {
                        checkDebitOrCreditAccountsAreValid(accountingRule, journalEntryCommand.getCredits(), journalEntryCommand.getDebits());
                        checkDebitAndCreditAmounts(journalEntryCommand.getCredits(), journalEntryCommand.getDebits());
                    }

                    saveAllDebitOrCreditEntries(journalEntryCommand, office, paymentDetail, debitExchangeRate, transactionDate, journalEntryCommand.getDebits(), transactionId, JournalEntryType.DEBIT, referenceNumber);
                } else {
                    final GLAccount debitAccountHead = accountingRule.getAccountToDebit();
                    validateGLAccountForTransaction(debitAccountHead);
                    validateDebitOrCreditArrayForExistingGLAccount(debitAccountHead, journalEntryCommand.getDebits());
                    saveAllDebitOrCreditEntries(journalEntryCommand, office, paymentDetail, debitExchangeRate, transactionDate, journalEntryCommand.getDebits(), transactionId, JournalEntryType.DEBIT, referenceNumber);
                }
            } else {
                saveAllDebitOrCreditEntries(journalEntryCommand, office, paymentDetail, debitExchangeRate, transactionDate, journalEntryCommand.getDebits(), transactionId, JournalEntryType.DEBIT, referenceNumber);
                saveAllDebitOrCreditEntries(journalEntryCommand, office, paymentDetail, creditExchangeRate, transactionDate, journalEntryCommand.getCredits(), transactionId, JournalEntryType.CREDIT, referenceNumber);
            }

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withOfficeId(officeId)
                    .withTransactionId(transactionId).build();
        } catch (final DataIntegrityViolationException dve) {
            handleJournalEntryDataIntegrityIssues(dve);
            return null;
        }
    }

    private BigDecimal getCurrentExchangeRate(String base, String target) {
        try {
            BigDecimal result = jdbcTemplate.queryForObject("SELECT rate FROM exchange_rate WHERE base_code=? and target_code=? ORDER BY rate_year DESC, rate_month DESC LIMIT 1", new Object[]{base, target}, BigDecimal.class);
            return result;
        } catch (DataAccessException e) {
            throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.EXCHANGE_RATE_MISSING, null, null, null);
        }
    }

    private void validateDebitOrCreditArrayForExistingGLAccount(final GLAccount glaccount,
            final SingleDebitOrCreditEntryCommand[] creditOrDebits) {
        /**
         * If a glaccount is assigned for a rule the credits or debits array
         * should have only one entry and it must be same as existing account
         */
        if (creditOrDebits.length != 1) { throw new JournalEntryInvalidException(
                GL_JOURNAL_ENTRY_INVALID_REASON.INVALID_DEBIT_OR_CREDIT_ACCOUNTS, null, null, null); }
        for (final SingleDebitOrCreditEntryCommand creditOrDebit : creditOrDebits) {
            if (!glaccount.getId().equals(creditOrDebit.getGlAccountId())) { throw new JournalEntryInvalidException(
                    GL_JOURNAL_ENTRY_INVALID_REASON.INVALID_DEBIT_OR_CREDIT_ACCOUNTS, null, null, null); }
        }
    }

    @SuppressWarnings("null")
    private void checkDebitOrCreditAccountsAreValid(final AccountingRule accountingRule, final SingleDebitOrCreditEntryCommand[] credits,
            final SingleDebitOrCreditEntryCommand[] debits) {
        // Validate the debit and credit arrays are appropriate accounts
        List<GLAccountDataForLookup> allowedCreditGLAccounts = new ArrayList<>();
        List<GLAccountDataForLookup> allowedDebitGLAccounts = new ArrayList<>();
        final SingleDebitOrCreditEntryCommand[] validCredits = new SingleDebitOrCreditEntryCommand[credits.length];
        final SingleDebitOrCreditEntryCommand[] validDebits = new SingleDebitOrCreditEntryCommand[debits.length];

        if (credits != null && credits.length > 0) {
            allowedCreditGLAccounts = this.glAccountReadPlatformService.retrieveAccountsByTagId(accountingRule.getId(),
                    JournalEntryType.CREDIT.getValue());
            for (final GLAccountDataForLookup accountDataForLookup : allowedCreditGLAccounts) {
                for (int i = 0; i < credits.length; i++) {
                    final SingleDebitOrCreditEntryCommand credit = credits[i];
                    if (credit.getGlAccountId().equals(accountDataForLookup.getId())) {
                        validCredits[i] = credit;
                    }
                }
            }
            if (credits.length != validCredits.length) { throw new RuntimeException("Invalid credits"); }
        }

        if (debits != null && debits.length > 0) {
            allowedDebitGLAccounts = this.glAccountReadPlatformService.retrieveAccountsByTagId(accountingRule.getId(),
                    JournalEntryType.DEBIT.getValue());
            for (final GLAccountDataForLookup accountDataForLookup : allowedDebitGLAccounts) {
                for (int i = 0; i < debits.length; i++) {
                    final SingleDebitOrCreditEntryCommand debit = debits[i];
                    if (debit.getGlAccountId().equals(accountDataForLookup.getId())) {
                        validDebits[i] = debit;
                    }
                }
            }
            if (debits.length != validDebits.length) { throw new RuntimeException("Invalid debits"); }
        }
    }

    private BigDecimal[] calculateExchangeRate(final SingleDebitOrCreditEntryCommand[] credits, final SingleDebitOrCreditEntryCommand[] debits) {
        BigDecimal creditsSum = BigDecimal.ZERO;
        BigDecimal debitsSum = BigDecimal.ZERO;

        BigDecimal creditExchangeRate = null;
        BigDecimal debitExchangeRate = null;

        OrganisationCurrency baseCurrency = organisationCurrencyRepo.findFirstByBase(true);

        boolean creditIsHome = false;
        boolean debitIsHome = false;

        GLAccount creditGlAccount = this.glAccountRepository.findOne(credits[0].getGlAccountId());

        if(baseCurrency.getCode().equals(creditGlAccount.getCurrencyCode())) {
            creditIsHome = true;
            creditExchangeRate = BigDecimal.ONE;
        }

        for (final SingleDebitOrCreditEntryCommand creditEntryCommand : credits) {
            if (creditEntryCommand.getAmount() == null || creditEntryCommand.getGlAccountId() == null) {
                break;
            }

            creditsSum = creditsSum.add(creditEntryCommand.getAmount());
        }

        GLAccount debitGlAccount = this.glAccountRepository.findOne(debits[0].getGlAccountId());

        if(baseCurrency.getCode().equals(debitGlAccount.getCurrencyCode())) {
            debitIsHome = true;
            debitExchangeRate = BigDecimal.ONE;
        }

        for (final SingleDebitOrCreditEntryCommand debitEntryCommand : debits) {
            if (debitEntryCommand.getAmount() == null || debitEntryCommand.getGlAccountId() == null) {
                break;
            }

            debitsSum = debitsSum.add(debitEntryCommand.getAmount());
        }

        if(creditIsHome) {
            debitExchangeRate = creditsSum.divide(debitsSum, JournalEntry.EXCHANGE_RATE_SCALE, BigDecimal.ROUND_HALF_EVEN);
            //debitExchangeRate = getCurrentExchangeRate(creditGlAccount.getCurrencyCode(), debitGlAccount.getCurrencyCode());
        } else if(debitIsHome) {
            creditExchangeRate = debitsSum.divide(creditsSum, JournalEntry.EXCHANGE_RATE_SCALE, BigDecimal.ROUND_HALF_EVEN);
            //creditExchangeRate = getCurrentExchangeRate(debitGlAccount.getCurrencyCode(), creditGlAccount.getCurrencyCode());
        } else {
            //creditExchangeRate = debitsSum.divide(creditsSum, JournalEntry.EXCHANGE_RATE_SCALE, BigDecimal.ROUND_HALF_EVEN);
            //debitExchangeRate = creditsSum.divide(debitsSum, JournalEntry.EXCHANGE_RATE_SCALE, BigDecimal.ROUND_HALF_EVEN);
            OrganisationCurrency currency = organisationCurrencyRepo.findFirstByBase(true);
            creditExchangeRate = getCurrentExchangeRate(currency.getCode(), creditGlAccount.getCurrencyCode());
            debitExchangeRate = getCurrentExchangeRate(currency.getCode(), debitGlAccount.getCurrencyCode());
        }

        return new BigDecimal[]{creditExchangeRate, debitExchangeRate};
    }

    private void checkDebitAndCreditAmounts(final SingleDebitOrCreditEntryCommand[] credits, final SingleDebitOrCreditEntryCommand[] debits) {
        // sum of all debits must be = sum of all credits
        BigDecimal creditsSum = BigDecimal.ZERO;
        BigDecimal debitsSum = BigDecimal.ZERO;

        Set<String> currencies = new HashSet<>();
        Set<String> currenciesDebit = new HashSet<>();
        Set<String> currenciesCredit = new HashSet<>();

        Map<Long, GLAccount> accounts = new HashMap<>();

        for (final SingleDebitOrCreditEntryCommand creditEntryCommand : credits) {
            if (creditEntryCommand.getAmount() == null || creditEntryCommand.getGlAccountId() == null) {
                throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.DEBIT_CREDIT_ACCOUNT_OR_AMOUNT_EMPTY, null, null, null);
            }
            creditsSum = creditsSum.add(creditEntryCommand.getAmount());

            if(!accounts.containsKey(creditEntryCommand.getGlAccountId())) {
                accounts.put(creditEntryCommand.getGlAccountId(), this.glAccountRepository.findOne(creditEntryCommand.getGlAccountId()));
            }

            GLAccount glAccount = accounts.get(creditEntryCommand.getGlAccountId());

            if(!currenciesCredit.contains(glAccount.getCurrencyCode())) {
                currenciesCredit.add(glAccount.getCurrencyCode());
            }
            if(!currencies.contains(glAccount.getCurrencyCode())) {
                currencies.add(glAccount.getCurrencyCode());
            }
            if(currenciesCredit.size()>1) {
                throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.TOO_MANY_CREDIT_CURRENCIES, null, null, null);
            }
        }
        for (final SingleDebitOrCreditEntryCommand debitEntryCommand : debits) {
            if (debitEntryCommand.getAmount() == null || debitEntryCommand.getGlAccountId() == null) {
                throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.DEBIT_CREDIT_ACCOUNT_OR_AMOUNT_EMPTY, null, null, null);
            }
            debitsSum = debitsSum.add(debitEntryCommand.getAmount());

            if(!accounts.containsKey(debitEntryCommand.getGlAccountId())) {
                accounts.put(debitEntryCommand.getGlAccountId(), this.glAccountRepository.findOne(debitEntryCommand.getGlAccountId()));
            }

            GLAccount glAccount = accounts.get(debitEntryCommand.getGlAccountId());

            if(!currenciesDebit.contains(glAccount.getCurrencyCode())) {
                currenciesDebit.add(glAccount.getCurrencyCode());
            }
            if(!currencies.contains(glAccount.getCurrencyCode())) {
                currencies.add(glAccount.getCurrencyCode());
            }
            if(currenciesDebit.size()>1) {
                throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.TOO_MANY_DEBIT_CURRENCIES, null, null, null);
            }
        }

        if(currencies.size()>2) {
            throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.TOO_MANY_DEBIT_OR_CREDIT_CURRENCIES, null, null, null);
        }

        // NOTE: check for home currency only if more than 1 currencies involved in transaction; use case: transfer USD to USD account
        if(currencies.size()>1) {
            OrganisationCurrency baseCurrency = organisationCurrencyRepo.findFirstByBase(true);

            if(baseCurrency==null ) {
                throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.HOME_CURRENCY_MISSING, null, null, null);
            } else {
                for(String currency : currencies) {
                    if(!baseCurrency.getCode().equals(currency)) {
                        BigDecimal rate = getCurrentExchangeRate(baseCurrency.getCode(), currency);
                        if(rate==null || BigDecimal.ZERO.equals(rate)) {
                            throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.EXCHANGE_RATE_MISSING, null, null, null);
                        }
                    }
                }
            }
            /**
             if(baseCurrency==null || (!currenciesCredit.contains(baseCurrency.getCode()) && !currenciesDebit.contains(baseCurrency.getCode())) ) {
                 throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.HOME_CURRENCY_MISSING, null, null, null);
             }
             */
        }

        if (currencies.size()==1) {
            if (creditsSum.compareTo(debitsSum) != 0) {
                throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.DEBIT_CREDIT_SUM_MISMATCH, null, null, null);
            }
        }
    }

    private void validateGLAccountForTransaction(final GLAccount creditOrDebitAccountHead) {
        /***
         * validate that the account allows manual adjustments and is not
         * disabled
         **/
        if (creditOrDebitAccountHead.isDisabled()) {
            throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.GL_ACCOUNT_DISABLED, null,
                    creditOrDebitAccountHead.getName(), creditOrDebitAccountHead.getGlCode());
        } else if (!creditOrDebitAccountHead.isManualEntriesAllowed()) { throw new JournalEntryInvalidException(
                GL_JOURNAL_ENTRY_INVALID_REASON.GL_ACCOUNT_MANUAL_ENTRIES_NOT_PERMITTED, null, creditOrDebitAccountHead.getName(),
                creditOrDebitAccountHead.getGlCode()); }
    }

    @Transactional
    @Override
    public CommandProcessingResult revertJournalEntry(final JsonCommand command) {
        // is the transaction Id valid
        final List<JournalEntry> journalEntries = this.glJournalEntryRepository.findUnReversedManualJournalEntriesByTransactionId(command
                .getTransactionId());

        if (journalEntries.size() <= 1) { throw new JournalEntriesNotFoundException(command.getTransactionId()); }
        final Long officeId = journalEntries.get(0).getOffice().getId();
        final String reversalTransactionId = generateTransactionId(officeId);
        final boolean manualEntry = true;
        String reversalComment = command.stringValueOfParameterNamed("comments");
        final boolean useDefaultComment = StringUtils.isBlank(reversalComment);

        validateCommentForReversal(reversalComment);

        // check financial year
        for (final JournalEntry journalEntry : journalEntries) {
            validateFinancialYear(journalEntry.getTransactionDate());
        }

        for (final JournalEntry journalEntry : journalEntries) {
            JournalEntry reversalJournalEntry;
            if (useDefaultComment) {
                reversalComment = "Reversal entry for Journal Entry with Entry Id  :" + journalEntry.getId()
                        + " and transaction Id " + command.getTransactionId();
            }
            if (journalEntry.isDebitEntry()) {
                reversalJournalEntry = JournalEntry.createNew(journalEntry.getOffice(), journalEntry.getPaymentDetails(),
                        journalEntry.getGlAccount(), journalEntry.getCurrencyCode(), reversalTransactionId, manualEntry,
                        journalEntry.getTransactionDate(), JournalEntryType.CREDIT, journalEntry.getAmount(), journalEntry.getExchangeRate(), reversalComment, null, null,
                        journalEntry.getReferenceNumber(), journalEntry.getLoanTransaction(), journalEntry.getSavingsTransaction());
            } else {
                reversalJournalEntry = JournalEntry.createNew(journalEntry.getOffice(), journalEntry.getPaymentDetails(),
                        journalEntry.getGlAccount(), journalEntry.getCurrencyCode(), reversalTransactionId, manualEntry,
                        journalEntry.getTransactionDate(), JournalEntryType.DEBIT, journalEntry.getAmount(), journalEntry.getExchangeRate(), reversalComment, null, null,
                        journalEntry.getReferenceNumber(), journalEntry.getLoanTransaction(), journalEntry.getSavingsTransaction());
            }
            // save the reversal entry
            this.glJournalEntryRepository.saveAndFlush(reversalJournalEntry);
            journalEntry.setReversed(true);
            journalEntry.setReversalJournalEntry(reversalJournalEntry);
            // save the updated journal entry
            this.glJournalEntryRepository.saveAndFlush(journalEntry);
        }
        return new CommandProcessingResultBuilder().withTransactionId(reversalTransactionId).build();
    }

    private void validateCommentForReversal(final String reversalComment) {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("GLJournalEntry");

        baseDataValidator.reset().parameter("comments").value(reversalComment).notExceedingLengthOf(500);

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    @Transactional
    @Override
    public void createJournalEntriesForLoan(final Map<String, Object> accountingBridgeData) {

        final boolean cashBasedAccountingEnabled = (Boolean) accountingBridgeData.get("cashBasedAccountingEnabled");
        final boolean upfrontAccrualBasedAccountingEnabled = (Boolean) accountingBridgeData.get("upfrontAccrualBasedAccountingEnabled");
        final boolean periodicAccrualBasedAccountingEnabled = (Boolean) accountingBridgeData.get("periodicAccrualBasedAccountingEnabled");

        if (cashBasedAccountingEnabled || upfrontAccrualBasedAccountingEnabled || periodicAccrualBasedAccountingEnabled) {
            final LoanDTO loanDTO = this.helper.populateLoanDtoFromMap(accountingBridgeData, cashBasedAccountingEnabled,
                    upfrontAccrualBasedAccountingEnabled, periodicAccrualBasedAccountingEnabled);
            final AccountingProcessorForLoan accountingProcessorForLoan = this.accountingProcessorForLoanFactory
                    .determineProcessor(loanDTO);
            accountingProcessorForLoan.createJournalEntriesForLoan(loanDTO);
        }
    }

    @Transactional
    @Override
    public void createJournalEntriesForSavings(final Map<String, Object> accountingBridgeData) {

        final boolean cashBasedAccountingEnabled = (Boolean) accountingBridgeData.get("cashBasedAccountingEnabled");
        final boolean accrualBasedAccountingEnabled = (Boolean) accountingBridgeData.get("accrualBasedAccountingEnabled");

        if (cashBasedAccountingEnabled || accrualBasedAccountingEnabled) {
            final SavingsDTO savingsDTO = this.helper.populateSavingsDtoFromMap(accountingBridgeData, cashBasedAccountingEnabled,
                    accrualBasedAccountingEnabled);
            final AccountingProcessorForSavings accountingProcessorForSavings = this.accountingProcessorForSavingsFactory
                    .determineProcessor(savingsDTO);
            accountingProcessorForSavings.createJournalEntriesForSavings(savingsDTO);
        }

    }

    private void validateBusinessRulesForJournalEntries(final JournalEntryCommand command) {
        /** check if date of Journal entry is valid ***/
        final LocalDate entryLocalDate = command.getTransactionDate();
        final Date transactionDate = entryLocalDate.toDateTimeAtStartOfDay().toDate();
        // shouldn't be in the future
        final Date todaysDate = new Date();
        if (transactionDate.after(todaysDate)) { throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.FUTURE_DATE,
                transactionDate, null, null); }
        // shouldn't be before an accounting closure
        final GLClosure latestGLClosure = this.glClosureRepository.getLatestGLClosureByBranch(command.getOfficeId());
        if (latestGLClosure != null) {
            if (latestGLClosure.getClosingDate().after(transactionDate) || latestGLClosure.getClosingDate().equals(transactionDate)) { throw new JournalEntryInvalidException(
                    GL_JOURNAL_ENTRY_INVALID_REASON.ACCOUNTING_CLOSED, latestGLClosure.getClosingDate(), null, null); }
        }

        // check financial year
        validateFinancialYear(transactionDate);

        /*** check if credits and debits are valid **/
        final SingleDebitOrCreditEntryCommand[] credits = command.getCredits();
        final SingleDebitOrCreditEntryCommand[] debits = command.getDebits();

        // atleast one debit or credit must be present
        //if ((credits == null || credits.length <= 0 || debits == null || debits.length <= 0) && !Boolean.TRUE.equals(command.isOpening())) {
        if (credits == null || credits.length <= 0 || debits == null || debits.length <= 0) {
            throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.NO_DEBITS_OR_CREDITS, null, null, null); }

        checkDebitAndCreditAmounts(credits, debits);
    }

    private void validateFinancialYear(Date date) {
        final AppUser user = this.context.authenticatedUser();
        if(user.hasNotPermissionForAnyOf("ALL_FUNCTIONS", "ALL_FUNCTIONS_READ", "BACKDATE_JOURNALENTRY") && financialYearRepository.countActiveFinancialYearFor(date)==0) {
            throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.OUTSIDE_FINANCIALYEAR, date, null, null);
        } else {
            Boolean closed = financialYearRepository.isFinancialYearClosed(date);

            if(Boolean.TRUE.equals(closed) || closed==null) {
                throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.FINANCIALYEAR_CLOSED, date, null, null);
            }
        }
    }

    private void saveAllDebitOrCreditEntries(final JournalEntryCommand command, final Office office, final PaymentDetail paymentDetail,
            final BigDecimal exchangeRate, final Date transactionDate,
            final SingleDebitOrCreditEntryCommand[] singleDebitOrCreditEntryCommands, final String transactionId,
            final JournalEntryType type, final String referenceNumber) {

        final boolean manualEntry = true;
        final boolean unidentifiedEntry = Boolean.TRUE.equals(command.isUnidentifiedEntry());

        for (final SingleDebitOrCreditEntryCommand singleDebitOrCreditEntryCommand : singleDebitOrCreditEntryCommands) {
            final GLAccount glAccount = this.glAccountRepository.findOne(singleDebitOrCreditEntryCommand.getGlAccountId());
            if (glAccount == null) { throw new GLAccountNotFoundException(singleDebitOrCreditEntryCommand.getGlAccountId()); }

            validateGLAccountForTransaction(glAccount);

            String comments = command.getComments();
            if (!StringUtils.isBlank(singleDebitOrCreditEntryCommand.getComments())) {
                comments = singleDebitOrCreditEntryCommand.getComments();
            }

            /** Validate current code is appropriate **/
            this.organisationCurrencyRepository.findOneWithNotFoundDetection(glAccount.getCurrencyCode());

            final JournalEntry glJournalEntry = JournalEntry.createNew(office, paymentDetail, glAccount, glAccount.getCurrencyCode(), transactionId,
                    manualEntry, transactionDate, type, singleDebitOrCreditEntryCommand.getAmount(), exchangeRate, comments, null, null, referenceNumber,
                    null, null, unidentifiedEntry);
            this.glJournalEntryRepository.saveAndFlush(glJournalEntry);
        }
    }

    /**
     * TODO: Need a better implementation with guaranteed uniqueness (but not a
     * long UUID)...maybe something tied to system clock..
     */
    private String generateTransactionId(final Long officeId) {
        final AppUser user = this.context.authenticatedUser();
        final Long time = System.currentTimeMillis();
        final String uniqueVal = String.valueOf(time) + user.getId() + officeId;
        final String transactionId = Long.toHexString(Long.parseLong(uniqueVal));
        return transactionId;
    }

    private void handleJournalEntryDataIntegrityIssues(final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();
        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.glJournalEntry.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource Journal Entry: " + realCause.getMessage());
    }

}
