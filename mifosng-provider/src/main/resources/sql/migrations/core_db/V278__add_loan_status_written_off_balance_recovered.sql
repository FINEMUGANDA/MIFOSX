INSERT INTO `r_enum_value`
VALUES ('loan_status_id', 603, 'Closed:Written-Off:Balance-Recovered', 'Closed:Written-Off:Balance-Recovered', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('transaction_loan', 'RECOVER_WRITTEN_OFF_BALANCE_LOAN', 'LOAN', 'RECOVER_WRITTEN_OFF_BALANCE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('transaction_loan', 'UNDO_RECOVER_WRITTEN_OFF_BALANCE_LOAN', 'LOAN', 'UNDO_RECOVER_WRITTEN_OFF_BALANCE', 0);