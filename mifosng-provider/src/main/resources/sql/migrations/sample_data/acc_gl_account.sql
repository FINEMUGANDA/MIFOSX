ALTER TABLE `mifostenant-default`.acc_gl_account ADD currency_code VARCHAR(3) NULL;
ALTER TABLE `mifostenant-default`.acc_gl_journal_entry ADD exchange_rate DECIMAL(19,10) NULL;
