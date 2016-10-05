UPDATE m_permission SET `grouping` = 'fin_cost_center' WHERE entity_name="COSTCENTER";

UPDATE m_permission SET `grouping` = 'fin_loan_transaction', `action_name` = 'FROMUNIDENTIFIED' WHERE `code` = 'FROM_UNIDENTIFIED_LOAN';
UPDATE m_permission SET `grouping` = 'fin_loan_transaction', `action_name` = 'MOVETOPROFIT' WHERE `code` = 'MOVE_TO_PROFIT_LOAN';
UPDATE m_permission SET `grouping` = 'fin_loan_transaction', `action_name` = 'MOVEOVERPAID' WHERE `code` = 'MOVE_OVERPAID_LOAN';
UPDATE m_permission SET `action_name` = 'MOVETOPROFIT' WHERE `code` = 'MOVETOPROFIT_JOURNALENTRY';
