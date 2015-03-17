DROP TABLE IF EXISTS `notification_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE notification_log
(
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  type VARCHAR(100),
  recipient VARCHAR(255),
  entity_name VARCHAR(255),
  entity_id BIGINT,
  sent_at DATE,
  sent TINYINT,
  sms_error VARCHAR(255),
  message_id VARCHAR(255) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT IGNORE INTO `mifostenant-default`.job (name, display_name, cron_expression, create_time, task_priority, group_name, previous_run_start_time, next_run_time, job_key, initializing_errorlog, is_active, currently_running, updates_allowed, scheduler_group, is_misfired) VALUES ('Follow up Email Notification', 'Follow up Email Notification', '0 0 6 1/1 * ? *', '2014-03-07 18:29:14', 5, null, '2015-03-01 16:30:00', '2015-03-05 16:30:00', 'Follow up Email NotificationJobDetail1 _ DEFAULT', null, 1, 0, 1, 0, 0);
INSERT IGNORE INTO `mifostenant-default`.job (name, display_name, cron_expression, create_time, task_priority, group_name, previous_run_start_time, next_run_time, job_key, initializing_errorlog, is_active, currently_running, updates_allowed, scheduler_group, is_misfired) VALUES ('Payment Reminder Email Notification', 'Payment Reminder Email Notification', '0 0 6 1/1 * ? *', '2014-03-07 18:29:14', 5, null, '2015-03-01 16:30:00', '2015-03-05 16:30:00', 'Payment Reminder Email NotificationJobDetail1 _ DEFAULT', null, 1, 0, 1, 0, 0);
INSERT IGNORE INTO `mifostenant-default`.job (name, display_name, cron_expression, create_time, task_priority, group_name, previous_run_start_time, next_run_time, job_key, initializing_errorlog, is_active, currently_running, updates_allowed, scheduler_group, is_misfired) VALUES ('Follow up SMS Notification', 'Follow up SMS Notification', '0 0 6 1/1 * ? *', '2014-03-07 18:29:14', 5, null, '2015-03-01 16:30:00', '2015-03-05 16:30:00', 'Follow up SMS NotificationJobDetail1 _ DEFAULT', null, 1, 0, 1, 0, 0);
INSERT IGNORE INTO `mifostenant-default`.job (name, display_name, cron_expression, create_time, task_priority, group_name, previous_run_start_time, next_run_time, job_key, initializing_errorlog, is_active, currently_running, updates_allowed, scheduler_group, is_misfired) VALUES ('Payment Reminder SMS Notification', 'Payment Reminder SMS Notification', '0 0 6 1/1 * ? *', '2014-03-07 18:29:14', 5, null, '2015-03-01 16:30:00', '2015-03-05 16:30:00', 'Payment Reminder SMS NotificationJobDetail1 _ DEFAULT', null, 1, 0, 1, 0, 0);

INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_host', 'smtp.gmail.com');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_auth_username', 'support@cloudmicrofinance.com');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_auth_password', '');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_starttls', 'true');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_sender_name', 'Support FINEM (U) LTD');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_subject', 'FINEM LTD - Notification');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_debug', 'false');

INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('sms_auth_username', 'FINEM');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('sms_auth_password', '');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('sms_outbound_max_per_day', '-1');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('sms_sender_name', 'FINEM');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('sms_sender_address', 'FINEM');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('sms_notify_url', null);
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('sms_debug', 'false');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('sms_debug_phone', '');

INSERT IGNORE INTO `mifostenant-default`.c_configuration (name, value, enabled, description) VALUES ('notification-payment-reminder-days-in-advance', 5, 0, 'How many days in advance should we notify the clients.');
