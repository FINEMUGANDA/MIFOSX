/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.jobs.service;

public enum JobName {

    UPDATE_LOAN_SUMMARY("Update loan Summary"), //
    UPDATE_LOAN_ARREARS_AGEING("Update Loan Arrears Ageing"), //
    UPDATE_LOAN_STATUS("Update Loan Status"),
    UPDATE_LOAN_PAID_IN_ADVANCE("Update Loan Paid In Advance"), //
    APPLY_ANNUAL_FEE_FOR_SAVINGS("Apply Annual Fee For Savings"), //
    APPLY_HOLIDAYS_TO_LOANS("Apply Holidays To Loans"), //
    POST_INTEREST_FOR_SAVINGS("Post Interest For Savings"), //
    TRANSFER_FEE_CHARGE_FOR_LOANS("Transfer Fee For Loans From Savings"), //
    ACCOUNTING_RUNNING_BALANCE_UPDATE("Update Accounting Running Balances"), //
    PAY_DUE_SAVINGS_CHARGES("Pay Due Savings Charges"), //
    APPLY_CHARGE_TO_OVERDUE_LOAN_INSTALLMENT("Apply penalty to overdue loans"),
    APPLY_CHARGE_TO_OVERDUE_MATURITY_DATE_LOAN("Apply penalty to overdue maturity date loans"),
    EXECUTE_STANDING_INSTRUCTIONS("Execute Standing Instruction"),
    ADD_ACCRUAL_ENTRIES("Add Accrual Transactions"),
    UPDATE_NPA("Update Non Performing Assets"),
    UPDATE_DEPOSITS_ACCOUNT_MATURITY_DETAILS("Update Deposit Accounts Maturity details"),
    TRANSFER_INTEREST_TO_SAVINGS("Transfer Interest To Savings"),
    ADD_PERIODIC_ACCRUAL_ENTRIES("Add Periodic Accrual Transactions"),
    RECALCULATE_INTEREST_FOR_LOAN("Recalculate Interest For Loans"),
    FOLLOW_UP_EMAIL_NOTIFICATION("Follow up Email Notification"),
    PAYMENT_REMINDER_EMAIL_NOTIFICATION("Payment Reminder Email Notification"),
    FOLLOW_UP_SMS_NOTIFICATION("Follow up SMS Notification"),
    PAYMENT_REMINDER_SMS_NOTIFICATION("Payment Reminder SMS Notification"),
    CREATE_NEW_FINANCIALYEAR("Create New Financial Year");

    private final String name;

    private JobName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}