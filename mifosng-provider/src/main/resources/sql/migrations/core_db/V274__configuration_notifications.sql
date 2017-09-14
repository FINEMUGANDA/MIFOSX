INSERT INTO `mifostenant-default`.c_configuration (`name`, `value`, enabled, description)
  SELECT * FROM (SELECT 'notification-lpi-payment-reminder-days' as `name`, 1 as `value`, true as `enabled`, 'In how many days after Maturity Date should we notify the clients.' as `description`) AS tmp
  WHERE NOT EXISTS (
      SELECT `name` FROM `mifostenant-default`.c_configuration WHERE `name` = 'notification-lpi-payment-reminder-days'
  ) LIMIT 1;