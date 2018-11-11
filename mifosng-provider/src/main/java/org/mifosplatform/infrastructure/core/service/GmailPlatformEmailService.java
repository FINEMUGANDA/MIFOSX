/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.core.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.mifosplatform.infrastructure.configuration.data.EmailCredentialsData;
import org.mifosplatform.infrastructure.configuration.service.ExternalServicesReadPlatformService;
import org.mifosplatform.infrastructure.core.domain.EmailDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GmailPlatformEmailService implements PlatformEmailService {
    private static final Logger logger = LoggerFactory.getLogger(GmailPlatformEmailService.class);

    private final ExternalServicesReadPlatformService externalServicesReadPlatformService;

    private final EmailCredentialsData credentials;

    @Autowired
    public GmailPlatformEmailService(ExternalServicesReadPlatformService externalServicesReadPlatformService) {
        this.externalServicesReadPlatformService = externalServicesReadPlatformService;
        this.credentials = externalServicesReadPlatformService.getEmailCredentials();
        logger.info("Production Email Service started!");
    }

    @Override
    public void sendToUserAccount(final EmailDetail emailDetail, final String unencodedPassword) {
        final Email email = new SimpleEmail();

        // Very Important, Don't use email.setAuthentication()
        email.setAuthenticator(new DefaultAuthenticator(credentials.getAuthUsername(), credentials.getAuthPassword()));
        email.setDebug(false); // true if you want to debug
        email.setHostName("smtp.gmail.com");
		email.setSmtpPort(credentials.getSmtpPort());
        try {
			email.setStartTLSEnabled(credentials.isStartTls());
            email.setFrom(credentials.getAuthUsername(), credentials.getAuthUsername());

            final StringBuilder subjectBuilder = new StringBuilder().append("FINEM U Ltd.: ").append(emailDetail.getContactName())
                    .append(" user account creation.");

            email.setSubject(subjectBuilder.toString());

            final String sendToEmail = emailDetail.getAddress();

            final StringBuilder messageBuilder = new StringBuilder().append("You are receiving this email as your email account: ")
                    .append(sendToEmail).append(" has being used to create a user account for an organisation named [")
                    .append(emailDetail.getOrganisationName()).append("].")
                    .append("You can login using the following credentials: username: ").append(emailDetail.getUsername())
                    .append(" password: ").append(unencodedPassword);

            email.setMsg(messageBuilder.toString());

            email.addTo(sendToEmail, emailDetail.getContactName());
            email.send();
        } catch (final EmailException e) {
            throw new PlatformEmailSendException(e);
        }
    }
}