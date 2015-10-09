package org.mifosplatform.infrastructure.notification.service;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationRepository;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.notification.domain.NotificationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public abstract class AbstractNotificationService implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractNotificationService.class);

    protected final DataSource dataSource;

    protected final JdbcTemplate jdbcTemplate;

    protected final NotificationLogRepository notificationLogRepository;

    protected final GlobalConfigurationRepository globalConfigurationRepository;

    protected final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    protected static final String queryFollowUpOfficers = "SELECT MAX(n.id) AS note_id, n.createdByUserName AS username,u.email, u.firstname, u.lastname FROM notes n, m_appuser u WHERE n.createdByUserName=u.username AND (SELECT COUNT(id) FROM notification_log WHERE id=n.notification_id)=0 AND n.followUpDate = CURRENT_DATE() GROUP BY email, firstname, lastname";
    protected static final String queryFollowUpClients = "SELECT l.client_id, c.firstname, c.lastname, c.account_no, c.mobile_no FROM notes n, m_loan l, m_client c WHERE n.loan_id=l.id AND l.client_id=c.id AND n.followUpDate = CURRENT_DATE() AND n.createdByUserName = ?";
    protected static final String updateNotes = "UPDATE notes SET notification_id=? WHERE followUpDate = CURRENT_DATE() AND createdByUserName = ?";
    protected static final String queryPaymentReminderClients = "SELECT lrs.id AS loan_repayment_schedule_id, c.firstname, c.lastname, c.mobile_no FROM m_loan_repayment_schedule lrs, m_loan l, m_client c WHERE lrs.loan_id = l.id AND l.client_id = c.id AND l.loan_status_id IN (800, 900) AND lrs.principal_amount > 0 AND (SELECT count(entity_id) FROM notification_log WHERE entity_name='m_loan_repayment_schedule' AND entity_id=lrs.id)=0 AND lrs.duedate = DATE_ADD(CURDATE(), INTERVAL ? DAY)";
    protected static final String queryExpiredLoans = "SELECT " +
            "l.id AS loan_id, " +
            "c.firstname, " +
            "c.lastname, " +
            "c.mobile_no, " +
            "l.total_outstanding_derived as amount, " +
            "l.maturedon_date as maturityDate, " +
            "(SELECT MAX(sent_at) FROM notification_log WHERE entity_name = 'm_loan' AND entity_id = l.id) as lastMessageDate, " +
            "rc.display_symbol as currencyDisplaySymbol " +
            "FROM m_loan l " +
            "left join m_client c on l.client_id = c.id " +
            "left join m_currency rc on rc.`code` = l.currency_code " +
            "WHERE " +
            "l.loan_status_id IN (300, 800, 900) " +
            "AND l.maturedon_date < DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
            "AND l.total_outstanding_derived > 0";

    protected static final String CONFIG_NOTIFICATION_PAYMENT_REMINDER_DAYS_IN_ADVANCE = "notification-payment-reminder-days-in-advance";

    protected AbstractNotificationService(final RoutingDataSource dataSource, final NotificationLogRepository notificationLogRepository, final GlobalConfigurationRepository globalConfigurationRepository) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.notificationLogRepository = notificationLogRepository;
        this.globalConfigurationRepository = globalConfigurationRepository;
    }

    protected List<Map<String, Object>> getFollowUpLoanOfficers() {
        return jdbcTemplate.query(queryFollowUpOfficers, new ColumnMapRowMapper());
    }

    protected List<Map<String, Object>> getFollowUpClients(String loanOfficer) {
        return jdbcTemplate.query(queryFollowUpClients, new Object[]{loanOfficer}, new ColumnMapRowMapper());
    }

    protected List<Map<String, Object>> getPaymentReminderClients(Integer daysInAdvance) {
        return jdbcTemplate.query(queryPaymentReminderClients, new Object[]{daysInAdvance}, new ColumnMapRowMapper());
    }

    protected List<Map<String, Object>> getExpiredLoanPaymentReminderClients(Integer daysInAdvance) {
        List<Map<String, Object>> expiredLoans = jdbcTemplate.query(queryExpiredLoans, new Object[]{daysInAdvance}, new ColumnMapRowMapper());
        List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();

        for (Map<String, Object> expiredLoan : expiredLoans) {
            Date maturityDate = (Date) expiredLoan.get("maturityDate");
            Date lastMessageDate = (Date) expiredLoan.get("lastMessageDate");
            if (lastMessageDate == null) {
                result.add(expiredLoan);
            } else {
                LocalDate date = LocalDate.fromDateFields(maturityDate).minusDays(daysInAdvance);
                LocalDate lastScheduledDate = null;
                while (new LocalDate().isAfter(date)) {
                    lastScheduledDate = date;
                    date = date.plusMonths(1);
                }
                if (lastScheduledDate != null && lastScheduledDate.isAfter(LocalDate.fromDateFields(lastMessageDate))) {
                    result.add(expiredLoan);
                }
            }
        }

        return result;
    }

    protected GlobalConfigurationProperty getGlobalConfiguration(String name) {
        return globalConfigurationRepository.findOneByName(name);
    }
}
