INSERT INTO `mifostenant-default`.c_configuration (name, value, enabled, description)
  SELECT * FROM (SELECT 'login-failure-limit', 3, 1, 'How many times can a login fail before user is informed.') AS tmp
  WHERE NOT EXISTS (
      SELECT name FROM `mifostenant-default`.c_configuration WHERE name = 'login-failure-limit'
  ) LIMIT 1;
