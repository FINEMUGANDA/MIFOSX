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
);

INSERT INTO `mifostenant-default`.job (name, display_name, cron_expression, create_time, task_priority, group_name, previous_run_start_time, next_run_time, job_key, initializing_errorlog, is_active, currently_running, updates_allowed, scheduler_group, is_misfired) VALUES ('Follow up Email Notification', 'Follow up Email Notification', '0 0 6 1/1 * ? *', '2014-03-07 18:29:14', 5, null, '2015-03-01 16:30:00', '2015-03-05 16:30:00', 'Follow up Email NotificationJobDetail1 _ DEFAULT', null, 1, 0, 1, 0, 0);
INSERT INTO `mifostenant-default`.job (name, display_name, cron_expression, create_time, task_priority, group_name, previous_run_start_time, next_run_time, job_key, initializing_errorlog, is_active, currently_running, updates_allowed, scheduler_group, is_misfired) VALUES ('Payment Reminder Email Notification', 'Payment Reminder Email Notification', '0 0 6 1/1 * ? *', '2014-03-07 18:29:14', 5, null, '2015-03-01 16:30:00', '2015-03-05 16:30:00', 'Payment Reminder Email NotificationJobDetail1 _ DEFAULT', null, 1, 0, 1, 0, 0);
INSERT INTO `mifostenant-default`.job (name, display_name, cron_expression, create_time, task_priority, group_name, previous_run_start_time, next_run_time, job_key, initializing_errorlog, is_active, currently_running, updates_allowed, scheduler_group, is_misfired) VALUES ('Follow up SMS Notification', 'Follow up SMS Notification', '0 0 6 1/1 * ? *', '2014-03-07 18:29:14', 5, null, '2015-03-01 16:30:00', '2015-03-05 16:30:00', 'Follow up SMS NotificationJobDetail1 _ DEFAULT', null, 1, 0, 1, 0, 0);
INSERT INTO `mifostenant-default`.job (name, display_name, cron_expression, create_time, task_priority, group_name, previous_run_start_time, next_run_time, job_key, initializing_errorlog, is_active, currently_running, updates_allowed, scheduler_group, is_misfired) VALUES ('Payment Reminder SMS Notification', 'Payment Reminder SMS Notification', '0 0 6 1/1 * ? *', '2014-03-07 18:29:14', 5, null, '2015-03-01 16:30:00', '2015-03-05 16:30:00', 'Payment Reminder SMS NotificationJobDetail1 _ DEFAULT', null, 1, 0, 1, 0, 0);
