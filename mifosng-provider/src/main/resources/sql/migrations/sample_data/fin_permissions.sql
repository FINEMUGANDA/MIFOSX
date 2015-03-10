UPDATE m_permission SET grouping = 'fin_loan' WHERE code IN ('CREATE_LOAN', 'UPDATE_LOAN', 'READ_LOAN', 'DELETE_LOAN', 'DISBURSE_LOAN', 'APPROVALUNDO_LOAN', 'REPAYMENT_LOAN', 'ADJUST_LOAN', 'WRITEOFF_LOAN', 'UNDOWRITEOFF_LOAN', 'CLOSE_LOAN', 'DISBURSALUNDO_LOAN', 'APPROVE_LOAN', 'REJECT_LOAN');
UPDATE m_permission SET grouping = 'fin_loan_reschedule' WHERE code IN ('CREATE_RESCHEDULELOAN', 'READ_RESCHEDULELOAN', 'APPROVE_RESCHEDULELOAN', 'REJECT_RESCHEDULELOAN');
UPDATE m_permission SET grouping = 'fin_loan_product' WHERE code IN ('CREATE_LOANPRODUCT', 'UPDATE_LOANPRODUCT', 'DELETE_LOANPRODUCT');
UPDATE m_permission SET grouping = 'fin_loan_assign' WHERE code IN ('BULKREASSIGN_LOAN');
UPDATE m_permission SET grouping = 'fin_client' WHERE code in ('CREATE_CLIENT', 'UPDATE_CLIENT', 'READ_CLIENT', 'DELETE_CLIENT', 'ACTIVATE_CLIENT', 'REACTIVATE_CLIENT', 'CLOSE_CLIENT');
UPDATE m_permission SET grouping = 'fin_account' WHERE code in ('CREATE_GLACCOUNT', 'UPDATE_GLACCOUNT', 'READ_GLACCOUNT', 'DELETE_GLACCOUNT');
UPDATE m_permission SET grouping = 'fin_journal' WHERE code in ('CREATE_JOURNALENTRY', 'READ_JOURNALENTRY', 'UPDATE_JOURNALENTRY', 'REVERSE_JOURNALENTRY');
UPDATE m_permission SET grouping = 'fin_charge' WHERE code in ('CREATE_CHARGE', 'UPDATE_CHARGE', 'READ_CHARGE', 'DELETE_CHARGE');
UPDATE m_permission SET grouping = 'fin_staff' WHERE code in ('CREATE_STAFF', 'UPDATE_STAFF', 'READ_STAFF', 'DELETE_STAFF');
UPDATE m_permission SET grouping = 'fin_user' WHERE code in ('CREATE_USER', 'UPDATE_USER', 'READ_USER', 'DELETE_USER');
UPDATE m_permission SET grouping = 'fin_role' WHERE code in ('CREATE_ROLE', 'UPDATE_ROLE', 'READ_ROLE', 'DELETE_ROLE');
-- UPDATE m_permission SET grouping = 'fin_permission' WHERE code in ('UPDATE_PERMISSION', 'READ_PERMISSION');
UPDATE m_permission SET grouping = 'fin_holiday' WHERE code in ('CREATE_HOLIDAY', 'UPDATE_HOLIDAY', 'READ_HOLIDAY', 'DELETE_HOLIDAY');
UPDATE m_permission SET grouping = 'fin_report' WHERE code in ('CREATE_REPORT', 'UPDATE_REPORT', 'DELETE_REPORT');
UPDATE m_permission SET grouping = 'fin_audit' WHERE code in ('READ_AUDIT');
-- UPDATE m_permission SET grouping = 'fin_dashboard_document' WHERE code in ('CREATE_DOCUMENT');
-- UPDATE m_permission SET grouping = 'fin_dashboard_survey' WHERE code in ('REGISTER_SURVEY');
UPDATE m_permission SET grouping = 'fin_job' WHERE code in ('READ_SCHEDULER', 'UPDATE_SCHEDULER', 'EXECUTEJOB_SCHEDULER');

INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('report', 'READ_JournalEntryList', 'JournalEntryList', 'READ', 0);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_journal', 'UPDATE_JOURNALENTRY', 'JOURNALENTRY', 'UPDATE', 0);

INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_dashboard_summary', 'TAC_UIDASHBOARD', 'UIDASHBOARD', 'TAC', 0);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_dashboard_summary', 'TB_UIDASHBOARD', 'UIDASHBOARD', 'TB', 0);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_dashboard_summary', 'LIBS_UIDASHBOARD', 'UIDASHBOARD', 'LIBS', 0);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_dashboard_summary', 'RDTW_UIDASHBOARD', 'UIDASHBOARD', 'RDTW', 0);

INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_dashboard_charts', 'ABPLO_UIDASHBOARD', 'UIDASHBOARD', 'ABPLO', 0);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_dashboard_charts', 'PPLO_UIDASHBOARD', 'UIDASHBOARD', 'PPLO', 0);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_dashboard_charts', 'CILPCM_UIDASHBOARD', 'UIDASHBOARD', 'CILPCM', 0);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_dashboard_charts', 'DVCPW_UIDASHBOARD', 'UIDASHBOARD', 'DVCPW', 0);

INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_dashboard_shortcuts', 'UPLOAD_UIDASHBOARD', 'UIDASHBOARD', 'UPLOAD', 0);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_dashboard_shortcuts', 'SURVEY_UIDASHBOARD', 'UIDASHBOARD', 'SURVEY', 0);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_dashboard_shortcuts', 'REPAYMENT_UIDASHBOARD', 'UIDASHBOARD', 'REPAYMENT', 0);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_dashboard_shortcuts', 'NOTE_UIDASHBOARD', 'UIDASHBOARD', 'NOTE', 0);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_dashboard_shortcuts', 'CLIENT_UIDASHBOARD', 'UIDASHBOARD', 'CLIENT', 0);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_dashboard_shortcuts', 'LOAN_UIDASHBOARD', 'UIDASHBOARD', 'LOAN', 0);

UPDATE m_permission SET grouping = 'report' WHERE code in ('READ_REPORT');
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_report', 'READ_UIREPORT', 'UIREPORT', 'READ', 0);

UPDATE m_permission SET grouping = 'portfolio' WHERE code in ('READ_LOANPRODUCT');
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_loan_product', 'READ_UILOANPRODUCT', 'UILOANPRODUCT', 'READ', 0);

INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('fin_notification', 'READ_UINOTIFICATION', 'UINOTIFICATION', 'READ', 0);

INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('datatable', 'CREATE_client_note_general', 'client_note_general', 'CREATE', 1);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('datatable', 'CREATE_client_note_general_CHECKER', 'client_note_general', 'CREATE', 0);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('datatable', 'READ_client_note_general', 'client_note_general', 'READ', 0);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('datatable', 'UPDATE_client_note_general', 'client_note_general', 'UPDATE', 1);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('datatable', 'UPDATE_client_note_general_CHECKER', 'client_note_general', 'UPDATE', 0);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('datatable', 'DELETE_client_note_general', 'client_note_general', 'DELETE', 1);
INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('datatable', 'DELETE_client_note_general_CHECKER', 'client_note_general', 'DELETE', 0);
