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

INSERT INTO `mifostenant-default`.job (name, display_name, cron_expression, create_time, task_priority, group_name, previous_run_start_time, next_run_time, job_key, initializing_errorlog, is_active, currently_running, updates_allowed, scheduler_group, is_misfired)
  SELECT * FROM (SELECT 'Follow up Email Notification', 'Follow up Email Notification' as a, '0 0 6 1/1 * ? *', '2014-03-07 18:29:14', 5, null, '2015-03-01 16:30:00', '2015-03-05 16:30:00', 'Follow up Email NotificationJobDetail1 _ DEFAULT', null as b, 1 as c, 0 as d, 1 as e, 0 as f, 0 as g) AS tmp
  WHERE NOT EXISTS (
      SELECT name FROM `mifostenant-default`.job WHERE name = 'Follow up Email Notification'
  ) LIMIT 1;
INSERT INTO `mifostenant-default`.job (name, display_name, cron_expression, create_time, task_priority, group_name, previous_run_start_time, next_run_time, job_key, initializing_errorlog, is_active, currently_running, updates_allowed, scheduler_group, is_misfired)
  SELECT * FROM (SELECT 'Payment Reminder Email Notification', 'Payment Reminder Email Notification' as a, '0 0 6 1/1 * ? *', '2014-03-07 18:29:14', 5, null, '2015-03-01 16:30:00', '2015-03-05 16:30:00', 'Payment Reminder Email NotificationJobDetail1 _ DEFAULT', null as b, 1 as c, 0 as d, 1 as e, 0 as f, 0 as g) AS tmp
  WHERE NOT EXISTS (
      SELECT name FROM `mifostenant-default`.job WHERE name = 'Payment Reminder Email Notification'
  ) LIMIT 1;
INSERT INTO `mifostenant-default`.job (name, display_name, cron_expression, create_time, task_priority, group_name, previous_run_start_time, next_run_time, job_key, initializing_errorlog, is_active, currently_running, updates_allowed, scheduler_group, is_misfired)
  SELECT * FROM (SELECT 'Follow up SMS Notification', 'Follow up SMS Notification' as a, '0 0 6 1/1 * ? *', '2014-03-07 18:29:14', 5, null, '2015-03-01 16:30:00', '2015-03-05 16:30:00', 'Follow up SMS NotificationJobDetail1 _ DEFAULT', null as b, 1 as c, 0 as d, 1 as e, 0 as f, 0 as g) AS tmp
  WHERE NOT EXISTS (
      SELECT name FROM `mifostenant-default`.job WHERE name = 'Follow up SMS Notification'
  ) LIMIT 1;
INSERT INTO `mifostenant-default`.job (name, display_name, cron_expression, create_time, task_priority, group_name, previous_run_start_time, next_run_time, job_key, initializing_errorlog, is_active, currently_running, updates_allowed, scheduler_group, is_misfired)
  SELECT * FROM (SELECT 'Payment Reminder SMS Notification', 'Payment Reminder SMS Notification' as a, '0 0 6 1/1 * ? *', '2014-03-07 18:29:14', 5, null, '2015-03-01 16:30:00', '2015-03-05 16:30:00', 'Payment Reminder SMS NotificationJobDetail1 _ DEFAULT', null as b, 1 as c, 0 as d, 1 as e, 0 as f, 0 as g) AS tmp
  WHERE NOT EXISTS (
      SELECT name FROM `mifostenant-default`.job WHERE name = 'Payment Reminder SMS Notification'
  ) LIMIT 1;
