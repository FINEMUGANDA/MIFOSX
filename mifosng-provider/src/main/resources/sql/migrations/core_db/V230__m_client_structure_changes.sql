
ALTER TABLE `m_client` DROP index `mobile_no_UNIQUE`;

ALTER TABLE `client_additional_details` MODIFY `bank_account_number` VARCHAR(50);
