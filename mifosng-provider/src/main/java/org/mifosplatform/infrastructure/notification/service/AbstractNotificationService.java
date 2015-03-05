package org.mifosplatform.infrastructure.notification.service;

import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.notification.domain.NotificationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Service
public abstract class AbstractNotificationService implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractNotificationService.class);

    protected final DataSource dataSource;

    protected final JdbcTemplate jdbcTemplate;

    protected final NotificationLogRepository notificationLogRepository;

    protected final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    protected static final String queryFollowUpOfficers = "SELECT n.createdByUserName AS username,u.email, u.firstname, u.lastname FROM notes n, m_appuser u WHERE n.createdByUserName=u.username AND n.notification_id IS NULL AND n.followUpDate = CURRENT_DATE() GROUP BY email, firstname, lastname";
    protected static final String queryFollowUpClients = "SELECT l.client_id, c.firstname, c.lastname, c.account_no, c.mobile_no FROM notes n, m_loan l, m_client c WHERE n.loan_id=l.id AND l.client_id=c.id AND n.followUpDate = CURRENT_DATE() AND n.createdByUserName = ?";
    protected static final String updateNotes = "UPDATE notes SET notification_id=? WHERE followUpDate = CURRENT_DATE() AND createdByUserName = ?";
    protected static final String queryPaymentReminderClients = "SELECT lrs.id AS loan_repayment_schedule_id, c.firstname, c.lastname, c.mobile_no FROM m_loan_repayment_schedule lrs, m_loan l, m_client c WHERE lrs.loan_id = l.id AND l.client_id = c.id AND l.loan_status_id IN (800, 900) AND lrs.principal_amount > 0 AND (SELECT count(entity_id) FROM notification_log WHERE entity_name='m_loan_repayment_schedule' AND entity_id=lrs.id)=0 AND lrs.duedate = DATE_ADD(CURDATE(), INTERVAL ? DAY)";

    protected AbstractNotificationService(final RoutingDataSource dataSource, final NotificationLogRepository notificationLogRepository) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.notificationLogRepository = notificationLogRepository;
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
}
