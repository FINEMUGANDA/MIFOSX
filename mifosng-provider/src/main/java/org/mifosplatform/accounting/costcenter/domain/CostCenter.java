package org.mifosplatform.accounting.costcenter.domain;

import org.mifosplatform.accounting.glaccount.domain.GLAccount;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by frank on 22/05/18.
 */
@Entity
@Table(name = "m_cost_center")
public class CostCenter extends AbstractPersistable<Long> {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "staff_id")
	private Staff staff;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "acc_gl_account_id")
	private GLAccount glAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "non_staff_id")
	private CodeValue nonStaff;

	@Column(name = "cost_center_type")
	private String costCenterType;

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public GLAccount getGlAccount() {
		return glAccount;
	}

	public void setGlAccount(GLAccount glAccount) {
		this.glAccount = glAccount;
	}

	public CodeValue getNonStaff() {
		return nonStaff;
	}

	public void setNonStaff(CodeValue nonStaff) {
		this.nonStaff = nonStaff;
	}

	public String getCostCenterType() {
		return costCenterType;
	}

	public void setCostCenterType(String costCenterType) {
		this.costCenterType = costCenterType;
	}

	public Long getGLAccountId() {
		return this.glAccount.getId();
	}
}
