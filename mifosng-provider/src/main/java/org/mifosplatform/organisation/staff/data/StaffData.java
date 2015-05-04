/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.staff.data;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.organisation.office.data.OfficeData;

import java.util.Collection;

/**
 * Immutable data object representing staff data.
 */
public class StaffData {

    private final Long id;
    private final String externalId;
    private final String firstname;
    private final String lastname;
    private final String displayName;
    private final String mobileNo;
    private final String mobileNo2;
    private final String email;
    private final String address;
    private final Integer children;
    private final String emergencyContactName;
    private final String emergencyContactMobileNo;
    private final CodeValueData gender;
    private final CodeValueData maritalStatus;
    private final CodeValueData emergencyContactRelation;
    private final Long officeId;
    private final String officeName;
    private final Boolean isLoanOfficer;
    private final Boolean isActive;
    private final LocalDate joiningDate;
    @SuppressWarnings("unused")
    private final Collection<OfficeData> allowedOffices;
    private final Collection<CodeValueData> genderOptions;
    private final Collection<CodeValueData> maritalStatusOptions;
    private final Collection<CodeValueData> emergencyContactRelationOptions;

    public static StaffData templateData(final Collection<OfficeData> allowedOffices, final Collection<CodeValueData> genderOptions, final Collection<CodeValueData> maritalStatusOptions, final Collection<CodeValueData> emergencyContactRelationOptions) {
        return new StaffData(null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, allowedOffices, null, null, genderOptions, maritalStatusOptions, emergencyContactRelationOptions);
    }

    public static StaffData templateData(final StaffData staff, final Collection<OfficeData> allowedOffices, final Collection<CodeValueData> genderOptions, final Collection<CodeValueData> maritalStatusOptions, final Collection<CodeValueData> emergencyContactRelationOptions) {
        return new StaffData(staff.id, staff.firstname, staff.lastname, staff.displayName, staff.officeId, staff.officeName,
                staff.isLoanOfficer, staff.externalId, staff.mobileNo, staff.mobileNo2, staff.email, staff.address, staff.children, staff.gender, staff.maritalStatus, staff.emergencyContactName, staff.emergencyContactMobileNo, staff.emergencyContactRelation, allowedOffices, staff.isActive, staff.joiningDate, genderOptions, maritalStatusOptions, emergencyContactRelationOptions);
    }

    public static StaffData lookup(final Long id, final String displayName) {
        return new StaffData(id, null, null, displayName, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public static StaffData instance(final Long id, final String firstname, final String lastname, final String displayName,
            final Long officeId, final String officeName, final Boolean isLoanOfficer, final String externalId, final String mobileNo,
            final boolean isActive, final LocalDate joiningDate, final CodeValueData gender, final CodeValueData maritalStatus, final CodeValueData emergencyContactRelation,
            final String mobileNo2, final String email, final String address, final Integer children, final String emergencyContactName, final String emergencyContactMobileNo) {
        return new StaffData(id, firstname, lastname, displayName, officeId, officeName, isLoanOfficer, externalId, mobileNo, mobileNo2, email, address, children, gender, maritalStatus, emergencyContactName, emergencyContactMobileNo, emergencyContactRelation, null,
                isActive, joiningDate, null, null, null);
    }

    private StaffData(final Long id, final String firstname, final String lastname, final String displayName, final Long officeId,
            final String officeName, final Boolean isLoanOfficer, final String externalId, final String mobileNo, final String mobileNo2, final String email, final String address, final Integer children, final CodeValueData gender, final CodeValueData maritalStatus, final String emergencyContactName, final String emergencyContactMobileNo, final CodeValueData emergencyContactRelation,
            final Collection<OfficeData> allowedOffices, final Boolean isActive, final LocalDate joiningDate, final Collection<CodeValueData> genderOptions, final Collection<CodeValueData> maritalStatusOptions, final Collection<CodeValueData> emergencyContactRelationOptions) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.displayName = displayName;
        this.officeName = officeName;
        this.isLoanOfficer = isLoanOfficer;
        this.externalId = externalId;
        this.officeId = officeId;
        this.mobileNo = mobileNo;
        this.mobileNo2 = mobileNo2;
        this.email = email;
        this.address = address;
        this.children = children;
        this.gender = gender;
        this.maritalStatus = maritalStatus;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactMobileNo = emergencyContactMobileNo;
        this.emergencyContactRelation = emergencyContactRelation;
        this.allowedOffices = allowedOffices;
        this.isActive = isActive;
        this.joiningDate = joiningDate;
        this.genderOptions = genderOptions;
        this.maritalStatusOptions = maritalStatusOptions;
        this.emergencyContactRelationOptions = emergencyContactRelationOptions;
    }

    public Long getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public String getOfficeName() {
        return this.officeName;
    }
    
    public LocalDate getJoiningDate() {
    	return this.joiningDate;
    }
}