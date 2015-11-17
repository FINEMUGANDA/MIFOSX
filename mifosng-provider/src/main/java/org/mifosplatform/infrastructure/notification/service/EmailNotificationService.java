package org.mifosplatform.infrastructure.notification.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.mifosplatform.infrastructure.configuration.data.EmailCredentialsData;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationRepository;
import org.mifosplatform.infrastructure.configuration.service.ExternalServicesReadPlatformService;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.jobs.annotation.CronTarget;
import org.mifosplatform.infrastructure.jobs.service.JobName;
import org.mifosplatform.infrastructure.notification.domain.NotificationLog;
import org.mifosplatform.infrastructure.notification.domain.NotificationLogRepository;
import org.mifosplatform.infrastructure.notification.domain.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class EmailNotificationService extends AbstractNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

    private final ExternalServicesReadPlatformService externalServicesReadPlatformService;
    private String template = "Hello, %s.\n\nPlease note that the following clients are due for follow up today, %s:\n\n";
    private String followUpSubject = "FINEM Follow Up Notification";

    private EmailCredentialsData credentials;

    private AtomicBoolean running = new AtomicBoolean(false);

    @Autowired
    public EmailNotificationService(final RoutingDataSource dataSource, final NotificationLogRepository notificationLogRepository, final ExternalServicesReadPlatformService externalServicesReadPlatformService, final GlobalConfigurationRepository globalConfigurationRepository) {
        super(dataSource, notificationLogRepository, globalConfigurationRepository);

        this.externalServicesReadPlatformService = externalServicesReadPlatformService;
    }

    @Override
    @CronTarget(jobName = JobName.PAYMENT_REMINDER_EMAIL_NOTIFICATION)
    public void notifyPaymentReminders() {
        logger.warn("Email payment reminder notifications not yet implemented!");
    }

    @Override
    @CronTarget(jobName = JobName.EXPIRED_LOAN_PAYMENT_REMINDER_EMAIL_NOTIFICATION)
    public void notifyExpiredLoanPaymentReminders() {
        logger.warn("Email expired loan notifications not yet implemented!");
    }

    @Override
    @CronTarget(jobName = JobName.FOLLOW_UP_EMAIL_NOTIFICATION)
    public void notifyFollowUps() {
        if(!running.get()) {
            running.set(true);

            List<Map<String, Object>> officers = getFollowUpLoanOfficers();

            for(Map<String, Object> officer : officers) {
                boolean sent = false;

                String name = officer.get("firstname") + " " + officer.get("lastname");
                String email = officer.get("email").toString();
                String messageId = null;

                StringBuilder message = new StringBuilder();
                message.append(String.format(template, name, df.format(new Date())));
                message.append(formatClients(getFollowUpClients(officer.get("username").toString())));

                try {
                    messageId = send(email, name, followUpSubject, message.toString());
                    sent = true;
                } catch (EmailException e) {
                    logger.error(e.toString(), e);
                }

                NotificationLog log = notificationLogRepository.save(new NotificationLog(NotificationType.EMAIL, email, new Date(), sent, "m_note", (Long)officer.get("note_id"), "", messageId));

                if(sent) {
                    jdbcTemplate.update(updateNotes, log.getId(), officer.get("username"));
                }

                // TODO: remove this in production
                logger.info("############### Email notification sent: {}", sent);
            }

            running.set(false);
        } else {
            logger.warn("############### Email notification job is already running!");
        }
    }

    protected String formatClients(List<Map<String, Object>> clients) {
        StringBuilder builder = new StringBuilder();

        for(Map<String, Object> client : clients) {
            builder.append("- ").append(client.get("firstname")).append(" ").append(client.get("lastname")).append(" - File No. (").append(client.get("file_no")).append(") - Phone (").append(client.get("mobile_no")).append(")\n");
        }

        return builder.toString();
    }

    protected EmailCredentialsData getCredentials() {
        if(credentials==null) {
            credentials = externalServicesReadPlatformService.getEmailCredentials();
        }

        return credentials;
    }

    protected String send(String to, String name, String subject, String message) throws EmailException {
        final Email email = new SimpleEmail();
        EmailCredentialsData credentials = getCredentials();
        email.setAuthenticator(new DefaultAuthenticator(credentials.getAuthUsername(), credentials.getAuthPassword()));
        email.setDebug(credentials.isDebug());
        email.setHostName(credentials.getHost());
        email.setStartTLSEnabled(credentials.isStartTls());
        email.setSocketTimeout(300);// recommended socket timeout - 5 min https://support.google.com/mail/answer/13287?hl=en
        //email.getMailSession().getProperties().put(EmailConstants.MAIL_TRANSPORT_STARTTLS_ENABLE, credentials.isStartTls());
        email.setFrom(credentials.getAuthUsername(), credentials.getSenderName());
        email.setSubject(subject);
        email.setMsg(message);
        email.addTo(to, name);
        return email.send();
    }
}
