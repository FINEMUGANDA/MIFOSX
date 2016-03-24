/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.journalentry.data;

public class JournalEntryAssociationParametersData {

    private final boolean transactionDetailsRequired;
    private final boolean runningBalanceRequired;
    private final boolean staffRelationRequired;
    private final boolean onlyUnidentifiedEntries;

    public JournalEntryAssociationParametersData() {
        this.transactionDetailsRequired = false;
        this.runningBalanceRequired = false;
        this.staffRelationRequired = false;
        this.onlyUnidentifiedEntries = false;
    }

    public JournalEntryAssociationParametersData(final boolean transactionDetailsRequired, final boolean runningBalanceRequired) {
        this(transactionDetailsRequired, runningBalanceRequired, false);
    }
    public JournalEntryAssociationParametersData(final boolean transactionDetailsRequired, final boolean runningBalanceRequired, final boolean staffRelationRequired) {
        this(transactionDetailsRequired, runningBalanceRequired, staffRelationRequired, false);
    }

    public JournalEntryAssociationParametersData(final boolean transactionDetailsRequired, final boolean runningBalanceRequired, final boolean staffRelationRequired, final boolean onlyUnidentifiedEntries) {
        this.transactionDetailsRequired = transactionDetailsRequired;
        this.runningBalanceRequired = runningBalanceRequired;
        this.staffRelationRequired = staffRelationRequired;
        this.onlyUnidentifiedEntries = onlyUnidentifiedEntries;
    }

    public boolean isTransactionDetailsRequired() {
        return this.transactionDetailsRequired;
    }

    public boolean isRunningBalanceRequired() {
        return this.runningBalanceRequired;
    }

    public boolean isStaffRelationRequired() {
        return staffRelationRequired;
    }

    public boolean isOnlyUnidentifiedEntries() {
        return this.onlyUnidentifiedEntries;
    }
}
