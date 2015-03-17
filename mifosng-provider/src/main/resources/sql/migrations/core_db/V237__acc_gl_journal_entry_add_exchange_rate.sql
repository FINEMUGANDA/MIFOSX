SET @s = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE table_name = 'acc_gl_journal_entry'
        AND table_schema = DATABASE()
        AND column_name = 'exchange_rate'
    ) > 0,
    "SELECT 1",
    "ALTER TABLE `mifostenant-default`.acc_gl_journal_entry ADD exchange_rate DECIMAL(19,10) NULL"
));

PREPARE stmt FROM @s;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;