/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.journalentry.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long>, JpaSpecificationExecutor<JournalEntry>,
        JournalEntryRepositoryCustom {

    @Query("from JournalEntry journalEntry where journalEntry.transactionId= :transactionId and journalEntry.reversed is false and journalEntry.manualEntry is true")
    List<JournalEntry> findUnReversedManualJournalEntriesByTransactionId(@Param("transactionId") String transactionId);

    @Query("from JournalEntry journalEntry where journalEntry.transactionId= :transactionId and journalEntry.reversed is false and journalEntry.manualEntry is true and journalEntry.profit=false")
    List<JournalEntry> findUnReversedManualNotProfitJournalEntriesByTransactionId(@Param("transactionId") String transactionId);

    @Query("from JournalEntry journalEntry where journalEntry.profitTransactionId= :profitTransactionId and journalEntry.reversed is false and journalEntry.manualEntry is true and journalEntry.profit=true")
    List<JournalEntry> findJournalEntriesByProfitTransactionId(@Param("profitTransactionId") String profitTransactionId);
}
