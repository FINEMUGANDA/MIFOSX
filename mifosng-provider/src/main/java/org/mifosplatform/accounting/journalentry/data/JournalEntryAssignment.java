package org.mifosplatform.accounting.journalentry.data;

public class JournalEntryAssignment {

	private Long loanId;
	private Long journalId;
	private String clientName;
	private String loanStatus;
	private String clientFileNumber;
	private String loanAccountNumber;

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	public Long getJournalId() {
		return journalId;
	}

	public void setJournalId(Long journalId) {
		this.journalId = journalId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getLoanStatus() {
		return loanStatus;
	}

	public void setLoanStatus(String loanStatus) {
		this.loanStatus = loanStatus;
	}

	public String getClientFileNumber() {
		return clientFileNumber;
	}

	public void setClientFileNumber(String clientFileNumber) {
		this.clientFileNumber = clientFileNumber;
	}

	public String getLoanAccountNumber() {
		return loanAccountNumber;
	}

	public void setLoanAccountNumber(String loanAccountNumber) {
		this.loanAccountNumber = loanAccountNumber;
	}
}
