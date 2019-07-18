/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "overpayment_transaction_mapper")
public final class OverpaymentTransactionMapper extends AbstractPersistable<Long> {

	@ManyToOne(optional = false)
	@JoinColumn(name = "overpayment_transaction_id", nullable = false)
	private LoanTransaction overpaymentTransaction;

	@ManyToOne
	@JoinColumn(name = "repayment_transaction_id", nullable = false)
	private LoanTransaction repaymentTransaction;

	public LoanTransaction getOverpaymentTransaction() {
		return overpaymentTransaction;
	}

	public void setOverpaymentTransaction(LoanTransaction overpaymentTransaction) {
		this.overpaymentTransaction = overpaymentTransaction;
	}

	public LoanTransaction getRepaymentTransaction() {
		return repaymentTransaction;
	}

	public void setRepaymentTransaction(LoanTransaction repaymentTransaction) {
		this.repaymentTransaction = repaymentTransaction;
	}
}