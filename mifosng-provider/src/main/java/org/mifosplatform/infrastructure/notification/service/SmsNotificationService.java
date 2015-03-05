package org.mifosplatform.infrastructure.notification.service;

import oneapi.client.impl.SMSClient;
import oneapi.config.Configuration;
import oneapi.model.SMSRequest;
import oneapi.model.SendMessageResult;
import org.joda.time.DateTime;
import org.mifosplatform.infrastructure.configuration.data.SmsCredentialsData;
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
public class SmsNotificationService extends AbstractNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(SmsNotificationService.class);

    private String sender = "FINEM";
    private String notifyUrl = null;
    private Long outboundMaxPerDay;
    private String template = "Dear %s. Your loan repayment is due on %s. Pls pay to FINEM (U) LTD,A/C:2916200002 Centenary Bank and deliver voucher to FINEM office.Tks";
    private boolean debug = false;
    // TODO: make these configurable
    private int daysInAdvance = 15;

    private Configuration configuration;
    private SMSClient smsClient;

    @Autowired
    public SmsNotificationService(final RoutingDataSource dataSource, final NotificationLogRepository notificationLogRepository, final ExternalServicesReadPlatformService externalServicesReadPlatformService) {
        super(dataSource, notificationLogRepository);
        // TODO: enable this
        /**
        SmsCredentialsData credentials = externalServicesReadPlatformService.getSmsCredentials();
        this.sender = credentials.getSender();
        this.notifyUrl = credentials.getNotifyUrl();
        this.outboundMaxPerDay = credentials.getOutboundMaxPerDay();
        this.debug = credentials.isDebug();
        this.configuration = new Configuration(credentials.getAuthUsername(), credentials.getAuthPassword());
         */
        this.configuration = new Configuration("", "");
        this.smsClient = new SMSClient(configuration);
    }

    @Override
    @CronTarget(jobName = JobName.PAYMENT_REMINDER_SMS_NOTIFICATION)
    public void notifyPaymentReminders() {
        DateTime now = new DateTime();
        DateTime dueDate = now.plusDays(daysInAdvance);

        List<Map<String, Object>> clients = getPaymentReminderClients(daysInAdvance);

        for(Map<String, Object> client : clients) {
            boolean sent = false;
            SendMessageResult result = null;

            String mobileNo = normalize(client.get("mobile_no").toString());
            Long loanRepaymentScheduleId = (Long)client.get("loan_repayment_schedule_id");

            StringBuilder message = new StringBuilder();
            message.append(String.format(template, client.get("firstname"), df.format(dueDate.toDate())));

            try {
                result = send(mobileNo, message.toString());
                sent = true;
            } catch (Exception e) {
                logger.error(e.toString(), e);
            }

            notificationLogRepository.save(new NotificationLog(NotificationType.SMS, mobileNo, new Date(), sent, "m_loan_repayment_schedule", loanRepaymentScheduleId, null));

            logger.info("############### SMS notification sent: {}", sent);
        }
    }

    @Override
    @CronTarget(jobName = JobName.FOLLOW_UP_SMS_NOTIFICATION)
    public void notifyFollowUps() {
        logger.warn("SMS follow up notifications not yet implemented!");
    }

    private String normalize(String mobileNo) {
        if(mobileNo.startsWith("0")) {
            return "256" + mobileNo.substring(1);
        }

        return mobileNo;
    }

    protected SendMessageResult send(String to, String message) {
        SMSRequest smsRequest = new SMSRequest(sender, message, to);
        //smsRequest.setClientCorrelator(clientId);
        //smsRequest.setSenderName(sender);
        if(notifyUrl!=null) {
            smsRequest.setNotifyURL(notifyUrl); // TODO: implement this
        }

        logger.debug("SMS request: {} - {} - {}" + smsRequest.getSenderName(), smsRequest.getAddress(), smsRequest.getMessage());

        // TODO: enable this
        //return smsClient.getSMSMessagingClient().sendSMS(smsRequest);
        return null;
    }
}
