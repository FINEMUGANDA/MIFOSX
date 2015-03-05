package org.mifosplatform.infrastructure.notification.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.mifosplatform.infrastructure.configuration.data.EmailCredentialsData;
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

@Service
public class EmailNotificationService extends AbstractNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

    // TODO: make these configurable
    private String host = "smtp.gmail.com";
    private boolean startTls = true;
    private String authUsername = "support@cloudmicrofinance.com";
    private String authPassword = "support80";
    private String senderName = "Support FINEM (U) LTD";
    private String template = "Hello, %s.\n\nPlease note that the following clients are due for follow up today, %s:\n\n";
    private String followUpSubject = "FINEM Follow Up Notification";
    private boolean debug = false;

    @Autowired
    public EmailNotificationService(final RoutingDataSource dataSource, final NotificationLogRepository notificationLogRepository, final ExternalServicesReadPlatformService externalServicesReadPlatformService) {
        super(dataSource, notificationLogRepository);
        /**
        EmailCredentialsData credentials = externalServicesReadPlatformService.getEmailCredentials();
        this.host = credentials.getHost();
        this.senderName = credentials.getAuthUsername();
        this.authPassword = credentials.getAuthPassword();
        //this.followUpSubject = credentials.getSubject();
        this.startTls = credentials.isStartTls();
        this.debug = credentials.isDebug();
         */
    }

    @Override
    @CronTarget(jobName = JobName.PAYMENT_REMINDER_EMAIL_NOTIFICATION)
    public void notifyPaymentReminders() {
        logger.warn("Email payment reminder notifications not yet implemented!");
    }

    @Override
    @CronTarget(jobName = JobName.FOLLOW_UP_EMAIL_NOTIFICATION)
    public void notifyFollowUps() {
        List<Map<String, Object>> officers = getFollowUpLoanOfficers();

        for(Map<String, Object> officer : officers) {
            boolean sent = false;

            String name = officer.get("firstname") + " " + officer.get("lastname");
            String email = officer.get("email").toString();

            StringBuilder message = new StringBuilder();
            message.append(String.format(template, name, df.format(new Date())));
            message.append(formatClients(getFollowUpClients(officer.get("username").toString())));

            try {
                send(email, name, followUpSubject, message.toString());
                sent = true;
            } catch (EmailException e) {
                logger.error(e.toString(), e);
            }

            NotificationLog log = notificationLogRepository.save(new NotificationLog(NotificationType.EMAIL, email, new Date(), sent));

            if(sent) {
                jdbcTemplate.update(updateNotes, log.getId(), officer.get("username"));
            }

            // TODO: remove this in production
            logger.info("############### Email notification sent: {}", sent);
        }
    }

    protected String formatClients(List<Map<String, Object>> clients) {
        StringBuilder builder = new StringBuilder();

        for(Map<String, Object> client : clients) {
            builder.append("- ").append(client.get("firstname")).append(" ").append(client.get("lastname")).append(" (").append(client.get("account_no")).append("): ").append(client.get("mobile_no")).append("\n");
        }

        return builder.toString();
    }

    protected void send(String to, String name, String subject, String message) throws EmailException {
        final Email email = new SimpleEmail();
        email.setAuthenticator(new DefaultAuthenticator(authUsername, authPassword));
        email.setDebug(debug);
        email.setHostName(host);
        email.getMailSession().getProperties().put("mail.smtp.starttls.enable", startTls);
        email.setFrom(senderName, authUsername);
        email.setSubject(subject);
        email.setMsg(message);
        email.addTo(to, name);
        email.send();
    }
}
