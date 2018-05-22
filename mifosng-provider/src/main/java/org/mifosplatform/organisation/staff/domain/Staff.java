/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.staff.domain;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.accounting.glaccount.domain.GLAccount;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.staff.data.StaffData;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "m_staff", uniqueConstraints = {@UniqueConstraint(columnNames = {"display_name"}, name = "display_name"),
        @UniqueConstraint(columnNames = {"external_id"}, name = "external_id_UNIQUE"),
        @UniqueConstraint(columnNames = {"mobile_no"}, name = "mobile_no_UNIQUE")})
public class Staff extends AbstractPersistable<Long> {

    @Column(name = "firstname", length = 50)
    private String firstname;

    @Column(name = "lastname", length = 50)
    private String lastname;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "mobile_no", length = 50, nullable = false, unique = true)
    private String mobileNo;

    @Column(name = "mobile_no2", length = 50, nullable = false, unique = true)
    private String mobileNo2;

    @Column(name = "external_id", length = 100, nullable = true, unique = true)
    private String externalId;

    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @Column(name = "is_loan_officer ", nullable = false)
    private boolean loanOfficer;

    @Column(name = "organisational_role_enum", nullable = true)
    private Integer organisationalRoleType;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "joining_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date joiningDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_cv_id", nullable = true)
    private CodeValue gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marital_status_cv_id", nullable = true)
    private CodeValue maritalStatus;

    @Column(name = "children", nullable = true)
    private Integer children;

    @Column(name = "address", nullable = true)
    private String address;

    @Column(name = "email", nullable = true)
    private String email;

    @Column(name = "emergency_contact_name", nullable = true)
    private String emergencyContactName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emergency_contact_relation_cv_id", nullable = true)
    private CodeValue emergencyContactRelation;

    @Column(name = "emergency_contact_mobile_no", length = 50, nullable = true)
    private String emergencyContactMobileNo;

    @ManyToOne
    @JoinColumn(name = "organisational_role_parent_staff_id", nullable = true)
    private Staff organisationalRoleParentStaff;

    @ManyToMany
    @JoinTable(
            name = "m_cost_center",
            joinColumns = {@JoinColumn(name = "staff_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "acc_gl_account_id", referencedColumnName = "id")})
    private List<GLAccount> glAccounts;

    public static Staff fromJson(final Office staffOffice, final CodeValue gender, final CodeValue maritalStatus, final CodeValue emergencyContactRelation, final JsonCommand command) {

        final String firstnameParamName = "firstname";
        final String firstname = command.stringValueOfParameterNamed(firstnameParamName);

        final String lastnameParamName = "lastname";
        final String lastname = command.stringValueOfParameterNamed(lastnameParamName);

        final String externalIdParamName = "externalId";
        final String externalId = command.stringValueOfParameterNamedAllowingNull(externalIdParamName);

        final String mobileNoParamName = "mobileNo";
        final String mobileNo = command.stringValueOfParameterNamedAllowingNull(mobileNoParamName);

        final String mobileNo2ParamName = "mobileNo2";
        final String mobileNo2 = command.stringValueOfParameterNamedAllowingNull(mobileNo2ParamName);

        final String isLoanOfficerParamName = "isLoanOfficer";
        final boolean isLoanOfficer = command.booleanPrimitiveValueOfParameterNamed(isLoanOfficerParamName);

        final String isActiveParamName = "isActive";
        final Boolean isActive = command.booleanObjectValueOfParameterNamed(isActiveParamName);

        final String addressParamName = "address";
        final String address = command.stringValueOfParameterNamedAllowingNull(addressParamName);

        final String childrenParamName = "children";
        final Integer children = command.integerValueOfParameterNamedDefaultToNullIfZero(childrenParamName);

        final String emailParamName = "email";
        final String email = command.stringValueOfParameterNamedAllowingNull(emailParamName);

        final String emergencyContactNameParamName = "emergencyContactName";
        final String emergencyContactName = command.stringValueOfParameterNamedAllowingNull(emergencyContactNameParamName);

        final String emergencyContactMobileNoParamName = "emergencyContactMobileNo";
        final String emergencyContactMobileNo = command.stringValueOfParameterNamedAllowingNull(emergencyContactMobileNoParamName);

        LocalDate joiningDate = null;

        final String joiningDateParamName = "joiningDate";
        if (command.hasParameter(joiningDateParamName)) {
            joiningDate = command.localDateValueOfParameterNamed(joiningDateParamName);
        }

        return new Staff(staffOffice, firstname, lastname, externalId, mobileNo, mobileNo2, isLoanOfficer, isActive, joiningDate, gender, maritalStatus, address, children, email, emergencyContactName, emergencyContactMobileNo, emergencyContactRelation);
    }

    protected Staff() {
        //
    }

    private Staff(final Office staffOffice, final String firstname, final String lastname, final String externalId, final String mobileNo, final String mobileNo2,
                  final boolean isLoanOfficer, final Boolean isActive, final LocalDate joiningDate, final CodeValue gender, final CodeValue maritalStatus, final String address, final Integer children, final String email, final String emergencyContactName, final String emergencyContactMobileNo, final CodeValue emergencyContactRelation) {
        this.office = staffOffice;
        this.firstname = StringUtils.defaultIfEmpty(firstname, null);
        this.lastname = StringUtils.defaultIfEmpty(lastname, null);
        this.externalId = StringUtils.defaultIfEmpty(externalId, null);
        this.mobileNo = StringUtils.defaultIfEmpty(mobileNo, null);
        this.mobileNo2 = StringUtils.defaultIfEmpty(mobileNo2, null);
        this.gender = gender;
        this.maritalStatus = maritalStatus;
        this.address = StringUtils.defaultIfEmpty(address, null);
        this.children = children;
        this.email = StringUtils.defaultIfEmpty(email, null);
        this.emergencyContactName = StringUtils.defaultIfEmpty(emergencyContactName, null);
        this.emergencyContactMobileNo = StringUtils.defaultIfEmpty(emergencyContactMobileNo, null);
        this.emergencyContactRelation = emergencyContactRelation;
        this.loanOfficer = isLoanOfficer;
        this.active = (isActive == null) ? true : isActive;
        deriveDisplayName(firstname);
        if (joiningDate != null) {
            this.joiningDate = joiningDate.toDateTimeAtStartOfDay().toDate();
        }
    }

    public EnumOptionData organisationalRoleData() {
        EnumOptionData organisationalRole = null;
        if (this.organisationalRoleType != null) {
            organisationalRole = StaffEnumerations.organisationalRole(this.organisationalRoleType);
        }
        return organisationalRole;
    }

    public void changeOffice(final Office newOffice) {
        this.office = newOffice;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        final String officeIdParamName = "officeId";
        if (command.isChangeInLongParameterNamed(officeIdParamName, this.office.getId())) {
            final Long newValue = command.longValueOfParameterNamed(officeIdParamName);
            actualChanges.put(officeIdParamName, newValue);
        }

        boolean firstnameChanged = false;
        final String firstnameParamName = "firstname";
        if (command.isChangeInStringParameterNamed(firstnameParamName, this.firstname)) {
            final String newValue = command.stringValueOfParameterNamed(firstnameParamName);
            actualChanges.put(firstnameParamName, newValue);
            this.firstname = newValue;
            firstnameChanged = true;
        }

        boolean lastnameChanged = false;
        final String lastnameParamName = "lastname";
        if (command.isChangeInStringParameterNamed(lastnameParamName, this.lastname)) {
            final String newValue = command.stringValueOfParameterNamed(lastnameParamName);
            actualChanges.put(lastnameParamName, newValue);
            this.lastname = newValue;
            lastnameChanged = true;
        }

        if (firstnameChanged || lastnameChanged) {
            deriveDisplayName(this.firstname);
        }

        final String genderIdParamName = "genderId";
        if (command.isChangeInLongParameterNamed(genderIdParamName, genderId())) {
            final Long newValue = command.longValueOfParameterNamed(genderIdParamName);
            actualChanges.put(genderIdParamName, newValue);
        }

        final String maritalStatusIdParamName = "maritalStatusId";
        if (command.isChangeInLongParameterNamed(maritalStatusIdParamName, maritalStatusId())) {
            final Long newValue = command.longValueOfParameterNamed(maritalStatusIdParamName);
            actualChanges.put(maritalStatusIdParamName, newValue);
        }

        final String emergencyContactRelationIdParamName = "emergencyContactRelationId";
        if (command.isChangeInLongParameterNamed(emergencyContactRelationIdParamName, emergencyContactRelationId())) {
            final Long newValue = command.longValueOfParameterNamed(emergencyContactRelationIdParamName);
            actualChanges.put(emergencyContactRelationIdParamName, newValue);
        }

        final String emailParamName = "email";
        if (command.isChangeInStringParameterNamed(emailParamName, this.email)) {
            final String newValue = command.stringValueOfParameterNamed(emailParamName);
            actualChanges.put(emailParamName, newValue);
            this.email = newValue;
        }

        final String externalIdParamName = "externalId";
        if (command.isChangeInStringParameterNamed(externalIdParamName, this.externalId)) {
            final String newValue = command.stringValueOfParameterNamed(externalIdParamName);
            actualChanges.put(externalIdParamName, newValue);
            this.externalId = newValue;
        }

        final String childrenParamName = "children";
        if (command.isChangeInIntegerParameterNamed(childrenParamName, this.children)) {
            final Integer newValue = command.integerValueOfParameterNamed(childrenParamName);
            actualChanges.put(childrenParamName, newValue);
            this.children = newValue;
        }

        final String addressParamName = "address";
        if (command.isChangeInStringParameterNamed(addressParamName, this.address)) {
            final String newValue = command.stringValueOfParameterNamed(addressParamName);
            actualChanges.put(addressParamName, newValue);
            this.address = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String emergencyContactNameParamName = "emergencyContactName";
        if (command.isChangeInStringParameterNamed(emergencyContactNameParamName, this.emergencyContactName)) {
            final String newValue = command.stringValueOfParameterNamed(emergencyContactNameParamName);
            actualChanges.put(emergencyContactNameParamName, newValue);
            this.emergencyContactName = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String emergencyContactMobileNoParamName = "emergencyContactMobileNo";
        if (command.isChangeInStringParameterNamed(emergencyContactMobileNoParamName, this.emergencyContactMobileNo)) {
            final String newValue = command.stringValueOfParameterNamed(emergencyContactMobileNoParamName);
            actualChanges.put(emergencyContactMobileNoParamName, newValue);
            this.emergencyContactMobileNo = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String mobileNoParamName = "mobileNo";
        if (command.isChangeInStringParameterNamed(mobileNoParamName, this.mobileNo)) {
            final String newValue = command.stringValueOfParameterNamed(mobileNoParamName);
            actualChanges.put(mobileNoParamName, newValue);
            this.mobileNo = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String mobileNo2ParamName = "mobileNo2";
        if (command.isChangeInStringParameterNamed(mobileNo2ParamName, this.mobileNo2)) {
            final String newValue = command.stringValueOfParameterNamed(mobileNo2ParamName);
            actualChanges.put(mobileNo2ParamName, newValue);
            this.mobileNo2 = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String isLoanOfficerParamName = "isLoanOfficer";
        if (command.isChangeInBooleanParameterNamed(isLoanOfficerParamName, this.loanOfficer)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(isLoanOfficerParamName);
            actualChanges.put(isLoanOfficerParamName, newValue);
            this.loanOfficer = newValue;
        }

        final String isActiveParamName = "isActive";
        if (command.isChangeInBooleanParameterNamed(isActiveParamName, this.active)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(isActiveParamName);
            actualChanges.put(isActiveParamName, newValue);
            this.active = newValue;
        }

        final String joiningDateParamName = "joiningDate";
        if (command.isChangeInDateParameterNamed(joiningDateParamName, this.joiningDate)) {
            final String valueAsInput = command.stringValueOfParameterNamed(joiningDateParamName);
            actualChanges.put(joiningDateParamName, valueAsInput);
            final LocalDate newValue = command.localDateValueOfParameterNamed(joiningDateParamName);
            this.joiningDate = newValue.toDate();
        }

        return actualChanges;
    }

    public boolean isNotLoanOfficer() {
        return !isLoanOfficer();
    }

    public boolean isLoanOfficer() {
        return this.loanOfficer;
    }

    public boolean isNotActive() {
        return !isActive();
    }

    public boolean isActive() {
        return this.active;
    }

    private void deriveDisplayName(final String firstname) {
        if (!StringUtils.isBlank(firstname)) {
            this.displayName = this.lastname + ", " + this.firstname;
        } else {
            this.displayName = this.lastname;
        }
    }

    public boolean identifiedBy(final Staff staff) {
        return getId().equals(staff.getId());
    }

    public Long officeId() {
        return this.office.getId();
    }

    public String displayName() {
        return this.displayName;
    }

    public String mobileNo() {
        return this.mobileNo;
    }

    public Office office() {
        return this.office;
    }

    public void updateGender(CodeValue gender) {
        this.gender = gender;
    }

    public void updateMaritalStatus(CodeValue maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public void updateEmergencyContactRelation(CodeValue emergencyContactRelation) {
        this.emergencyContactRelation = emergencyContactRelation;
    }

    public Long genderId() {
        Long id = null;
        if (this.gender != null) {
            id = this.gender.getId();
        }
        return id;
    }

    public Long maritalStatusId() {
        Long id = null;
        if (this.maritalStatus != null) {
            id = this.maritalStatus.getId();
        }
        return id;
    }

    public Long emergencyContactRelationId() {
        Long id = null;
        if (this.emergencyContactRelation != null) {
            id = this.emergencyContactRelation.getId();
        }
        return id;
    }

    public StaffData toData() {
        return StaffData.instance(this.getId(), this.firstname, this.lastname, this.displayName,
                this.office.getId(), this.office.getName(), this.isLoanOfficer(), this.externalId, this.mobileNo,
                this.isActive(), LocalDate.fromDateFields(this.joiningDate), this.gender.toData(), this.maritalStatus.toData(),
                this.emergencyContactRelation.toData(), this.mobileNo2, this.email, this.address, this.children,
                this.emergencyContactName, this.emergencyContactMobileNo);
    }

    public List<GLAccount> getGlAccounts() {
        return glAccounts;
    }

    public void setGlAccounts(List<GLAccount> glAccounts) {
        this.glAccounts = glAccounts;
    }
}