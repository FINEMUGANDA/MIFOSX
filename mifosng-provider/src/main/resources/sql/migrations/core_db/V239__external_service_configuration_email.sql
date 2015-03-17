INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_host', 'smtp.gmail.com');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_auth_username', 'support@cloudmicrofinance.com');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_auth_password', 'secret');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_starttls', 'true');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_sender_name', 'Support FINEM (U) LTD');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_subject', 'FINEM LTD - Notification');
INSERT IGNORE INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_debug', 'false');

INSERT IGNORE INTO `mifostenant-default`.c_configuration (name, value, enabled, description) VALUES ('notification-payment-reminder-days-in-advance', 5, 0, 'How many days in advance should we notify the clients.');
