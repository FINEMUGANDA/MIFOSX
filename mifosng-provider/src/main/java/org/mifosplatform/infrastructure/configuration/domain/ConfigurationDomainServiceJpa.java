/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.configuration.domain;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.cache.domain.CacheType;
import org.mifosplatform.infrastructure.cache.domain.PlatformCache;
import org.mifosplatform.infrastructure.cache.domain.PlatformCacheRepository;
import org.mifosplatform.useradministration.domain.Permission;
import org.mifosplatform.useradministration.domain.PermissionRepository;
import org.mifosplatform.useradministration.exception.PermissionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigurationDomainServiceJpa implements ConfigurationDomainService {

    private final PermissionRepository permissionRepository;
    private final GlobalConfigurationRepositoryWrapper globalConfigurationRepository;
    private final PlatformCacheRepository cacheTypeRepository;

    @Autowired
    public ConfigurationDomainServiceJpa(final PermissionRepository permissionRepository,
            final GlobalConfigurationRepositoryWrapper globalConfigurationRepository, final PlatformCacheRepository cacheTypeRepository) {
        this.permissionRepository = permissionRepository;
        this.globalConfigurationRepository = globalConfigurationRepository;
        this.cacheTypeRepository = cacheTypeRepository;
    }

    @Override
    public boolean isMakerCheckerEnabledForTask(final String taskPermissionCode) {
        if (StringUtils.isBlank(taskPermissionCode)) { throw new PermissionNotFoundException(taskPermissionCode); }

        final Permission thisTask = this.permissionRepository.findOneByCode(taskPermissionCode);
        if (thisTask == null) { throw new PermissionNotFoundException(taskPermissionCode); }

        final String makerCheckerConfigurationProperty = "maker-checker";
        final GlobalConfigurationProperty property = this.globalConfigurationRepository
                .findOneByNameWithNotFoundDetection(makerCheckerConfigurationProperty);

        return thisTask.hasMakerCheckerEnabled() && property.isEnabled();
    }

    @Override
    public boolean isAmazonS3Enabled() {
		return this.isPropertyEnabled("amazon-S3");
    }

    @Override
    public boolean isRescheduleFutureRepaymentsEnabled() {
		return this.isPropertyEnabled("reschedule-future-repayments");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mifosplatform.infrastructure.configuration.domain.
     * ConfigurationDomainService#isHolidaysEnabled()
     */
    @Override
    public boolean isRescheduleRepaymentsOnHolidaysEnabled() {
		return this.isPropertyEnabled("reschedule-repayments-on-holidays");
    }

    @Override
    public boolean allowTransactionsOnHolidayEnabled() {
		return this.isPropertyEnabled("allow-transactions-on-holiday");
    }

    @Override
    public boolean allowTransactionsOnNonWorkingDayEnabled() {
		return this.isPropertyEnabled("allow-transactions-on-non_workingday");
    }

    @Override
    public boolean isConstraintApproachEnabledForDatatables() {
		return this.isPropertyEnabled("constraint_approach_for_datatables");
    }

    @Override
    public boolean isEhcacheEnabled() {
        return this.cacheTypeRepository.findOne(Long.valueOf(1)).isEhcacheEnabled();
    }

    @Transactional
    @Override
    public void updateCache(final CacheType cacheType) {
        final PlatformCache cache = this.cacheTypeRepository.findOne(Long.valueOf(1));
        cache.update(cacheType);
        this.cacheTypeRepository.save(cache);
    }

    @Override
    public Long retrievePenaltyWaitPeriod() {
		return this.getPropertyValue("penalty-wait-period");
    }

    @Override
    public Long retrieveGraceOnPenaltyPostingPeriod() {
		return this.getPropertyValue("grace-on-penalty-posting");
    }

    @Override
    public boolean isPasswordForcedResetEnable() {
		return this.isPropertyEnabled("force-password-reset-days");
    }

    @Override
    public Long retrievePasswordLiveTime() {
		return this.getPropertyValue("force-password-reset-days");
    }

    @Override
    public boolean isSavingsInterestPostingAtCurrentPeriodEnd() {
		return this.isPropertyEnabled("savings-interest-posting-current-period-end");
    }

    @Override
    public Integer retrieveFinancialYearBeginningMonth() {
		return this.getPropertyValueOrElse("financial-year-beginning-month", 1);
    }

    @Override
    public Integer retrieveMinAllowedClientsInGroup() {
		return this.getPropertyValueOrElse("min-clients-in-group", null);
    }

    @Override
    public Integer retrieveMaxAllowedClientsInGroup() {
		return this.getPropertyValueOrElse("max-clients-in-group", null);
    }

    @Override
    public boolean isMeetingMandatoryForJLGLoans() {
		return this.isPropertyEnabled("meetings-mandatory-for-jlg-loans");
    }

	@Override
	public boolean allowPostClosureRepayments() {
		return this.isPropertyEnabled("allow-post-closure-repayments");
	}

	private boolean isPropertyEnabled(String propertyName) {
		final GlobalConfigurationProperty property = this.globalConfigurationRepository.findOneByNameWithNotFoundDetection(propertyName);
		return property.isEnabled();
	}

	private Long getPropertyValue(String propertyName) {
		final GlobalConfigurationProperty property = this.globalConfigurationRepository.findOneByNameWithNotFoundDetection(propertyName);
		return property.getValue();
	}

	/**
	 * Gets the value of the property if enabled, otherwise returns default value
	 * @param propertyName
	 * @param defaultValue
	 * @return
	 */
	private Integer getPropertyValueOrElse(String propertyName, Integer defaultValue) {
		final GlobalConfigurationProperty property = this.globalConfigurationRepository.findOneByNameWithNotFoundDetection(propertyName);
		if (property.isEnabled()) {
			return property.getValue().intValue();
		}
		return defaultValue;
	}

}