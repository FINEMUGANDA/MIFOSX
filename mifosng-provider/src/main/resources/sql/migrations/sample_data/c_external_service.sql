INSERT INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_host', 'smtp.gmail.com');
INSERT INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_auth_username', 'support@cloudmicrofinance.com');
INSERT INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_auth_password', 'support80');
INSERT INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_starttls', 'true');
INSERT INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_sender_name', 'Support FINEM (U) LTD');
INSERT INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_subject', 'FINEM LTD - Notification');
INSERT INTO `mifostenant-default`.c_external_service (name, value) VALUES ('email_debug', 'false');

INSERT INTO `mifostenant-default`.c_external_service (name, value) VALUES ('sms_auth_username', 'FINEM');
INSERT INTO `mifostenant-default`.c_external_service (name, value) VALUES ('sms_auth_password', 'finem123');
INSERT INTO `mifostenant-default`.c_external_service (name, value) VALUES ('sms_outbound_max_per_day', '-1');
INSERT INTO `mifostenant-default`.c_external_service (name, value) VALUES ('sms_sender_name', 'FINEM');
INSERT INTO `mifostenant-default`.c_external_service (name, value) VALUES ('sms_sender_address', 'FINEM');
INSERT INTO `mifostenant-default`.c_external_service (name, value) VALUES ('sms_notify_url', null);
INSERT INTO `mifostenant-default`.c_external_service (name, value) VALUES ('sms_debug', 'false');
INSERT INTO `mifostenant-default`.c_external_service (name, value) VALUES ('sms_debug_phone', '351910306878');

INSERT INTO `mifostenant-default`.c_configuration (name, value, enabled, description) VALUES ('notification-payment-reminder-days-in-advance', 5, 0, 'How many days in advance should we notify the clients.');
