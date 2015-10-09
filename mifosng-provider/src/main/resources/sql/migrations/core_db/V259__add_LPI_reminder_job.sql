
INSERT INTO `job`
    (`name`, `display_name`, `cron_expression`, `create_time`, `task_priority`, `group_name`, `previous_run_start_time`, `next_run_time`, `job_key`, `initializing_errorlog`, `is_active`, `currently_running`, `updates_allowed`, `scheduler_group`, `is_misfired`)
    VALUES ('Expired Loans Payment Reminder SMS Notification', 'Expired Loans Payment Reminder SMS Notification', '0 0 12 1/1 * ? *', now(), 5, NULL, NULL, NULL, NULL, NULL, 1, 0, 1, 0, 0);

INSERT INTO `job`
    (`name`, `display_name`, `cron_expression`, `create_time`, `task_priority`, `group_name`, `previous_run_start_time`, `next_run_time`, `job_key`, `initializing_errorlog`, `is_active`, `currently_running`, `updates_allowed`, `scheduler_group`, `is_misfired`)
    VALUES ('Expired Loans Payment Reminder Email Notification', 'Expired Loans Payment Reminder Email Notification', '0 0 12 1/1 * ? *', now(), 5, NULL, NULL, NULL, NULL, NULL, 1, 0, 1, 0, 0);


