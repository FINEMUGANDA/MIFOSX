package org.mifosplatform.infrastructure.notification.service;

import oneapi.client.impl.SMSClient;
import oneapi.config.Configuration;
import oneapi.model.SMSRequest;
import oneapi.model.SendMessageResult;
import oneapi.model.SendMessageResultItem;
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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SmsNotificationService extends AbstractNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(SmsNotificationService.class);

    private final ExternalServicesReadPlatformService externalServicesReadPlatformService;
    private String senderName = "FINEM";
    private String senderAddress = "";
    private String notifyUrl = null;
    private Long outboundMaxPerDay;
    private String template = "Dear %s. Your loan repayment is due on %s. Pls pay to FINEM (U) LTD,A/C:2916200002 Centenary Bank and deliver voucher to FINEM office.Tks";
    private boolean debug = false;
    private String debugPhone = null;

    private SMSClient smsClient;

    private AtomicBoolean running = new AtomicBoolean(false);

    @Autowired
    public SmsNotificationService(final RoutingDataSource dataSource, final NotificationLogRepository notificationLogRepository, final ExternalServicesReadPlatformService externalServicesReadPlatformService, final GlobalConfigurationRepository globalConfigurationRepository) {
        super(dataSource, notificationLogRepository, globalConfigurationRepository);

        this.externalServicesReadPlatformService = externalServicesReadPlatformService;
    }

    @Override
    @CronTarget(jobName = JobName.PAYMENT_REMINDER_SMS_NOTIFICATION)
    public void notifyPaymentReminders() {
        if(!running.get()) {
            running.set(true);

            configure();

            GlobalConfigurationProperty daysInAdvance = getGlobalConfiguration(CONFIG_NOTIFICATION_PAYMENT_REMINDER_DAYS_IN_ADVANCE);

            DateTime now = new DateTime();
            DateTime dueDate = now.plusDays(daysInAdvance.getValue().intValue());

            List<Map<String, Object>> clients = getPaymentReminderClients(daysInAdvance.getValue().intValue());

            logger.info("=============== SMS JOB - clients:{} - date:{} - id:{} - days:{}", clients, dueDate, daysInAdvance.getId(), daysInAdvance.getValue());

            for(Map<String, Object> client : clients) {
                boolean sent = false;
                SendMessageResult result = null;

                String mobileNo = normalize(client.get("mobile_no").toString());
                Long loanRepaymentScheduleId = (Long)client.get("loan_repayment_schedule_id");

                StringBuilder message = new StringBuilder();
                message.append(String.format(template, client.get("firstname"), df.format(dueDate.toDate())));

                try {
                    logger.info("=============== SMS DESTINATION: {} - {} - {} - {}", mobileNo, dueDate, daysInAdvance.getId(), daysInAdvance.getValue());
                    result = send(mobileNo, message.toString());
                    sent = true;
                } catch (Exception e) {
                    logger.error(e.toString(), e);
                }

                if(result!=null) {
                    SendMessageResultItem item = result.getSendMessageResults()[0];

                    notificationLogRepository.save(new NotificationLog(NotificationType.SMS, mobileNo, new Date(), sent, "m_loan_repayment_schedule", loanRepaymentScheduleId, item.getMessageStatus(), item.getMessageId()));

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
        if(debug) {
            return debugPhone;
        } else if(mobileNo.startsWith("0")) {
            return "256" + mobileNo.substring(1);
        } else if(mobileNo.startsWith("+")) {
            return mobileNo.substring(1);
        }

        return mobileNo;
    }

    protected SendMessageResult send(String to, String message) {
        SMSRequest smsRequest = new SMSRequest(senderAddress, message, to);
        //smsRequest.setClientCorrelator(clientId);
        smsRequest.setSenderName(senderName);
        if(notifyUrl!=null && !"".equals(notifyUrl.trim())) {
            smsRequest.setNotifyURL(notifyUrl); // TODO: implement this
        }

        return smsClient.getSMSMessagingClient().sendSMS(smsRequest);
    }

    protected void configure() {
        if(smsClient==null) {
            SmsCredentialsData credentials = externalServicesReadPlatformService.getSmsCredentials();
            this.senderName = credentials.getSenderName();
            this.senderAddress = credentials.getSenderAddress();
            this.notifyUrl = credentials.getNotifyUrl();
            this.outboundMaxPerDay = credentials.getOutboundMaxPerDay();
            this.debug = credentials.isDebug();
            this.debugPhone = credentials.getDebugPhone();
            this.smsClient = new SMSClient(new Configuration(credentials.getAuthUsername(), credentials.getAuthPassword()));
        }
    }
}
