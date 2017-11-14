/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.journalentry.service;

import org.mifosplatform.accounting.journalentry.data.JournalEntryAssignment;
import org.mifosplatform.accounting.journalentry.data.JournalEntryAssociationParametersData;
import org.mifosplatform.accounting.journalentry.data.JournalEntryData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.SearchParameters;

import java.util.Collection;
import java.util.Date;

public interface JournalEntryReadPlatformService {

	JournalEntryData retrieveGLJournalEntryById(long glJournalEntryId, JournalEntryAssociationParametersData associationParametersData);

	Page<JournalEntryData> retrieveAll(SearchParameters searchParameters, Long glAccountId, Boolean onlyManualEntries, Date fromDate,
	                                   Date toDate, String transactionId, Integer entityType, JournalEntryAssociationParametersData associationParametersData, Boolean onlyUnidentifiedEntries);

	Collection<JournalEntryAssignment> retrieveJournalEntryAssignments(Long journalEntryId);

}
