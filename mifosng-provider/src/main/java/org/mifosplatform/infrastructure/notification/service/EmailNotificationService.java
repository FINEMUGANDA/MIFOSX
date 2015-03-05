package org.mifosplatform.infrastructure.notification.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.jobs.annotation.CronTarget;
import org.mifosplatform.infrastructure.jobs.service.JobName;
import org.mifosplatform.infrastructure.notification.domain.NotificationLog;
import org.mifosplatform.infrastructure.notification.domain.NotificationLogRepository;
import org.mifosplatform.infrastructure.notification.domain.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class EmailNotificationService implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

    // TODO: make these configurable
    private String hostname = "smtp.gmail.com";
    private boolean startTlsEnabled = true;
    private String authuserName = "support@cloudmicrofinance.com";
    private String authuser = "support@cloudmicrofinance.com";
    private String authpwd = "support80";
    private String template = "Hello, %s.\n\nPlease note that the following clients are due for follow up today, %s:\n\n";
    private String subject = "Mifos Notification";
    private boolean debug = false;

    private final String queryOfficers = "SELECT n.createdByUserName AS username,u.email, u.firstname, u.lastname FROM notes n, m_appuser u WHERE n.createdByUserName=u.username AND n.followUpDate = CURRENT_DATE() GROUP BY email, firstname, lastname";
    private final String queryClients = "SELECT l.client_id, c.firstname, c.lastname, c.account_no, c.mobile_no FROM notes n, m_loan l, m_client c WHERE n.loan_id=l.id AND l.client_id=c.id AND n.followUpDate = CURRENT_DATE() AND n.createdByUserName = ?";
    private final String updateNotes = "UPDATE notes SET notification_id=? WHERE followUpDate = CURRENT_DATE() AND createdByUserName = ?";

    private final DataSource dataSource;

    private final JdbcTemplate jdbcTemplate;

    private final NotificationLogRepository notificationLogRepository;

    private final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

    @Autowired
    public EmailNotificationService(final RoutingDataSource dataSource, final NotificationLogRepository notificationLogRepository) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.notificationLogRepository = notificationLogRepository;
    }

    @Override
    @CronTarget(jobName = JobName.FOLLOW_UP_EMAIL_NOTIFICATION)
    public void notifyFollowUps() {
        List<Map<String, Object>> officers = jdbcTemplate.query(queryOfficers, new ColumnMapRowMapper());

        for(Map<String, Object> officer : officers) {
            boolean sent = false;

            String name = officer.get("firstname") + " " + officer.get("lastname");

            StringBuilder message = new StringBuilder();
            message.append(String.format(template, name, df.format(new Date())));
            message.append(formatClients(jdbcTemplate.query(queryClients, new Object[]{officer.get("username")}, new ColumnMapRowMapper())));

            try {
                send(officer.get("email").toString(), name, subject, message.toString());
                sent = true;
            } catch (EmailException e) {
                logger.error(e.toString(), e);
            }

            NotificationLog log = notificationLogRepository.save(new NotificationLog(NotificationType.EMAIL, new Date(), sent));

            if(sent) {
                jdbcTemplate.update(updateNotes, log.getId(), officer.get("username"));
            }

            // TODO: remove this in production
            logger.info("############### Notification sent: {}", sent);
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
        email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
        email.setDebug(debug);
        email.setHostName(hostname);
        email.getMailSession().getProperties().put("mail.smtp.starttls.enable", startTlsEnabled);
        email.setFrom(authuser, authuserName);
        email.setSubject(subject);
        email.setMsg(message);
        email.addTo(to, name);
        email.send();
    }
}
