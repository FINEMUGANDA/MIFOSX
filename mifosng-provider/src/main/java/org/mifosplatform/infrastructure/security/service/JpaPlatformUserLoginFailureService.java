/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.security.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.mifosplatform.infrastructure.configuration.data.EmailCredentialsData;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationRepository;
import org.mifosplatform.infrastructure.configuration.service.ExternalServicesReadPlatformService;
import org.mifosplatform.useradministration.domain.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaPlatformUserLoginFailureService implements PlatformUserLoginFailureService {
    private static final Logger logger = LoggerFactory.getLogger(JpaPlatformUserLoginFailureService.class);

    private final AppUserRepository appUserRepository;

    private EmailCredentialsData emailCredentials;

    private final ExternalServicesReadPlatformService externalServicesReadPlatformService;

    private final GlobalConfigurationRepository globalConfigurationRepository;

    private String template = "You or someone has tried to login into the FINEM System with wrong credentials. After %s failed tries, your account has been locked.\n\nPlease Contact system administrator to reset your password.";
    private String subject = "FINEM Login Failure";

    @Autowired
    public JpaPlatformUserLoginFailureService(final AppUserRepository appUserRepository, final ExternalServicesReadPlatformService externalServicesReadPlatformService, final GlobalConfigurationRepository globalConfigurationRepository) {
        this.appUserRepository = appUserRepository;
        this.externalServicesReadPlatformService = externalServicesReadPlatformService;
        this.globalConfigurationRepository = globalConfigurationRepository;
    }

    @Transactional
    public Integer increment(String username) {
        appUserRepository.incrementLoginFailures(username);

        Integer failures = appUserRepository.getLoginFailuresByUsername(username);

        notify(username, failures);

        return failures;
    }

    @Transactional
    public void reset(String username) {
        appUserRepository.resetLoginFailures(username);
    }

    protected EmailCredentialsData getCredentials() {
        if(emailCredentials==null) {
            emailCredentials = externalServicesReadPlatformService.getEmailCredentials();
        }

        return emailCredentials;
    }

    private void notify(String username, Integer failures) {
        GlobalConfigurationProperty property = globalConfigurationRepository.findOneByName("login-failure-limit");

        Long limit = 3l;

        if(property!=null && property.isEnabled() && property.getValue()!=null) {
            limit = property.getValue();
        }

        // NOTE: only send the email once
        if(failures==limit.intValue()) {
            lock(username);
            try {
                StringBuilder message = new StringBuilder();
                message.append(String.format(template, limit));

                final Email email = new SimpleEmail();
                EmailCredentialsData credentials = getCredentials();
                email.setAuthenticator(new DefaultAuthenticator(credentials.getAuthUsername(), credentials.getAuthPassword()));
                email.setDebug(credentials.isDebug());
                email.setHostName(credentials.getHost());
				email.setSmtpPort(credentials.getSmtpPort());
				email.setStartTLSEnabled(credentials.isStartTls());
                //email.getMailSession().getProperties().put("mail.smtp.starttls.enable", credentials.isStartTls());
				//email.getMailSession().getProperties().put("mail.smtp.auth", true);
				//email.setSSLOnConnect(true);
                email.setFrom(credentials.getAuthUsername(), credentials.getSenderName());
                email.setSubject(subject);
                email.setMsg(message.toString());
                email.addTo(appUserRepository.getEmailByUsername(username));
                email.send();
            } catch (Exception e) {
                logger.warn(e.toString(), e);
            }

            throw new LockedException("User " + username + " has been locked after " + limit + " failed login attempts.");
        }
    }

    @Transactional
    private void lock(String username) {
        appUserRepository.lockUser(username);
    }
}