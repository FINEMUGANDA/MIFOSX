INSERT INTO `mifostenant-default`.job (name, display_name, cron_expression, create_time, task_priority, group_name, previous_run_start_time, next_run_time, job_key, initializing_errorlog, is_active, currently_running, updates_allowed, scheduler_group, is_misfired)
  SELECT * FROM (SELECT 'Create New Financial Year', 'Create New Financial Year' as a, '0 0 0 1/1 * ? *', '2014-03-07 18:29:14', 5, null, '2015-03-01 16:30:00', '2015-03-05 16:30:00', 'Create New Financial Year1 _ DEFAULT', null as b, 1 as c, 0 as d, 1 as e, 0 as f, 0 as g) AS tmp
  WHERE NOT EXISTS (
      SELECT name FROM `mifostenant-default`.job WHERE name = 'Create New Financial Year'
  ) LIMIT 1;
