ALTER TABLE `acc_gl_journal_entry`
  ADD COLUMN `profit` BOOLEAN NOT NULL,
  ADD COLUMN `profit_transaction_id` VARCHAR(100) NULL DEFAULT NULL;

INSERT INTO m_permission (`grouping`, code, entity_name, action_name, can_maker_checker)
	VALUES ('fin_journal', 'MOVETOPROFIT_JOURNALENTRY', 'JOURNALENTRY', 'UPDATE', false);
