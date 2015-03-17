SET @s = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE table_name = 'acc_gl_account'
        AND table_schema = DATABASE()
        AND column_name = 'currency_code'
    ) > 0,
    "SELECT 1",
    "ALTER TABLE `mifostenant-default`.acc_gl_account ADD currency_code VARCHAR(3) NULL"
));

PREPARE stmt FROM @s;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;