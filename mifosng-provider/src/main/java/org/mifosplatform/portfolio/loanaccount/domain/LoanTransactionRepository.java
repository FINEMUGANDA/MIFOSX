/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, Long>, JpaSpecificationExecutor<LoanTransaction> {
    @Query("FROM LoanTransaction lt WHERE lt.relatedTransactionId = :transactionId and lt.reversed = false")
    LoanTransaction findOneByRelatedTransactionIdAndNotReversed(@Param("transactionId") String transactionId);

	@Query("FROM LoanTransaction lt WHERE lt.loan.id = :loanId and lt.dateOf = :transactionDate and lt.typeOf = :typeOf and lt.id > :transactionId")
	LoanTransaction findAccrualTransactionBasedOn(@Param("loanId") Long loanId,
												  @Param("transactionDate") Date transactionDate,
												  @Param("typeOf") Integer typeOf,
												  @Param("transactionId") Long transactionId);
}