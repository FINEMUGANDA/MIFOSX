package org.mifosplatform.infrastructure.notification.service;

import com.infobip.ApiClient;
import com.infobip.ApiException;
import com.infobip.api.SendSmsApi;
import com.infobip.model.SmsAdvancedTextualRequest;
import com.infobip.model.SmsDestination;
import com.infobip.model.SmsResponse;
import com.infobip.model.SmsResponseDetails;
import com.infobip.model.SmsTextualMessage;
import org.joda.time.DateTime;
import org.mifosplatform.infrastructure.configuration.data.SmsCredentialsData;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SmsNotificationService extends AbstractNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(SmsNotificationService.class);

    private final ExternalServicesReadPlatformService externalServicesReadPlatformService;
    private SendSmsApi sendSmsApi;
    private String senderName = null;
    private String notifyUrl = null;
    private boolean debug = false;
    private String debugPhone = null;

    private AtomicBoolean running = new AtomicBoolean(false);

    @Autowired
    public SmsNotificationService(final RoutingDataSource dataSource, final NotificationLogRepository notificationLogRepository, final ExternalServicesReadPlatformService externalServicesReadPlatformService, final GlobalConfigurationRepository globalConfigurationRepository) {
        super(dataSource, notificationLogRepository, globalConfigurationRepository);
        this.externalServicesReadPlatformService = externalServicesReadPlatformService;
        configure();
    }

    @Override
    @CronTarget(jobName = JobName.PAYMENT_REMINDER_SMS_NOTIFICATION)
    public void notifyPaymentReminders() {
        configure();
        if (!running.get()) {
            running.set(true);
            String template = "Dear %s. Your loan repayment is due on %s. Pls pay to FINEM (U) LTD,A/C:3100009566 Centenary Bank and deliver voucher to FINEM office.Tks";

            GlobalConfigurationProperty daysInAdvance = getGlobalConfiguration(CONFIG_NOTIFICATION_PAYMENT_REMINDER_DAYS_IN_ADVANCE);

            DateTime now = new DateTime();
            DateTime dueDate = now.plusDays(daysInAdvance.getValue().intValue());

            List<Map<String, Object>> clients = getPaymentReminderClients(daysInAdvance.getValue().intValue());

            logger.info("=============== SMS JOB - clients:{} - date:{} - id:{} - days:{}", clients, dueDate, daysInAdvance.getId(), daysInAdvance.getValue());

            for (Map<String, Object> client : clients) {
                boolean sent = false;
                SmsResponse result = null;

                String mobileNo = normalize(client.get("mobile_no").toString());
                Long loanRepaymentScheduleId = (Long) client.get("loan_repayment_schedule_id");

                StringBuilder message = new StringBuilder();
                message.append(String.format(template, client.get("firstname"), df.format(dueDate.toDate())));

                try {
                    logger.info("=============== SMS DESTINATION: {} - {} - {} - {}", mobileNo, dueDate, daysInAdvance.getId(), daysInAdvance.getValue());
                    result = send(mobileNo, message.toString());
                    sent = true;
                } catch (Exception e) {
                    logger.error(e.toString(), e);
                }

                if (result != null) {
                    SmsResponseDetails item = result.getMessages().get(0);

                    notificationLogRepository.save(new NotificationLog(NotificationType.SMS, mobileNo, new Date(), sent, "m_loan_repayment_schedule", loanRepaymentScheduleId, item.getStatus().getName(), item.getMessageId()));

                    logger.info("############### SMS notification sent: {}", sent);
                } else {
                    logger.warn("############### SMS notification not sent: {} ({})", mobileNo, sent);
                }
            }

            running.set(false);
        } else {
            logger.warn("############### SMS notification job is already running!");
        }
    }

    @Override
    @CronTarget(jobName = JobName.FOLLOW_UP_SMS_NOTIFICATION)
    public void notifyFollowUps() {
        logger.warn("SMS follow up notifications not yet implemented!");
    }

    private String normalize(String mobileNo) {
        if (debug) {
            return debugPhone;
        } else if (mobileNo.startsWith("0")) {
            return "256" + mobileNo.substring(1);
        } else if (mobileNo.startsWith("+")) {
            return mobileNo.substring(1);
        }

        return mobileNo;
    }

    @Override
    @CronTarget(jobName = JobName.EXPIRED_LOAN_PAYMENT_REMINDER_SMS_NOTIFICATION)
    public void notifyExpiredLoanPaymentReminders() {
        configure();
        if (!running.get()) {
            running.set(true);

            String template = "Dear %s, Please be reminded that your loan is overdue. We expect deposit of %s %s plus potential recovery cost on Finem (U) Ltd. Centenary Bank A/C: 3100009566";

            GlobalConfigurationProperty daysAfter = getGlobalConfiguration(CONFIG_NOTIFICATION_LPI_PAYMENT_REMINDER_DAYS);

            DateTime now = new DateTime();
            DateTime dueDate = now.plusDays(daysAfter.getValue().intValue());

            List<Map<String, Object>> clients = getExpiredLoanPaymentReminderClients(daysAfter.getValue().intValue());

            logger.info("=============== SMS JOB - clients:{} - date:{} - id:{} - days:{}", clients, dueDate, daysAfter.getId(), daysAfter.getValue());
//            Map<String, Object> client = clients.get(0);
            for (Map<String, Object> client : clients) {
                boolean sent = false;
                SmsResponse result = null;

                String mobileNo = normalize(client.get("mobile_no").toString());
                Long loanId = (Long) client.get("loan_id");

                StringBuilder message = new StringBuilder();
                BigDecimal amount = (BigDecimal) client.get("amount");
                amount = amount.setScale(2, BigDecimal.ROUND_DOWN);

                DecimalFormat df = new DecimalFormat("#,###.##", new DecimalFormatSymbols(Locale.US));

                df.setMaximumFractionDigits(2);

                df.setMinimumFractionDigits(0);

                df.setGroupingUsed(true);

                String amountStr = df.format(amount);

                message.append(String.format(template, client.get("firstname"), client.get("currencyDisplaySymbol"), amountStr));

                try {
                    logger.info("=============== SMS DESTINATION: {} - {} - {} - {}", mobileNo, dueDate, daysAfter.getId(), daysAfter.getValue());
                    result = send(mobileNo, message.toString());
                    sent = true;
                } catch (Exception e) {
                    logger.error(e.toString(), e);
                }

                if (result != null) {
                    SmsResponseDetails item = result.getMessages().get(0);

                    notificationLogRepository.save(new NotificationLog(NotificationType.SMS, mobileNo, new Date(), sent, "m_loan", loanId, item.getStatus().getName(), item.getMessageId()));

                    logger.info("############### SMS notification sent: {}", sent);
                } else {
                    logger.warn("############### SMS notification not sent: {} ({})", mobileNo, sent);
                }
            }

            running.set(false);
        } else {
            logger.warn("############### SMS notification job is already running!");
        }
    }

    protected SmsResponse send(String to, String message) {
        SmsTextualMessage smsMessage = new SmsTextualMessage()
                .from(senderName)
                .addDestinationsItem(new SmsDestination().to(to))
                .text(message);
        if (notifyUrl != null && !"".equals(notifyUrl.trim())) {
            smsMessage.setNotifyUrl(notifyUrl);
        }
        SmsAdvancedTextualRequest smsMessageRequest = new SmsAdvancedTextualRequest()
                .messages(Collections.singletonList(smsMessage));
        try {
            return sendSmsApi.sendSmsMessage(smsMessageRequest);
        } catch (ApiException e) {
            logger.error("HTTP status code: {}", e.getCode());
            logger.error("Response body: {}", e.getResponseBody());
        }
        return null;
    }

    protected void configure() {
        SmsCredentialsData credentials = externalServicesReadPlatformService.getSmsCredentials();
        this.senderName = credentials.getSenderName();
        this.notifyUrl = credentials.getNotifyUrl();
        this.debug = credentials.isDebug();
        this.debugPhone = credentials.getDebugPhone();

        ApiClient client = new ApiClient();
        client.setApiKeyPrefix("App");
        client.setApiKey(credentials.getApiKey());
        client.setBasePath(credentials.getBaseUrl());
        sendSmsApi = new SendSmsApi(client);
    }
}
