ALTER TABLE m_loan_transaction MODIFY related_transaction_id VARCHAR(100) NULL DEFAULT NULL;
UPDATE m_loan_transaction SET related_transaction_id = null where related_transaction_id = '';

