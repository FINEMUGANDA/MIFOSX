ALTER TABLE `extra_guarantor_details` drop foreign key fk_extra_guarantor_details_loan_id;

ALTER TABLE `extra_guarantor_details` ADD CONSTRAINT `fk_extra_guarantor_details_loan_id` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`) on DELETE CASCADE;

ALTER TABLE `extra_loan_details` drop foreign key fk_extra_loan_details_loan_id;

ALTER TABLE `extra_loan_details` ADD CONSTRAINT `fk_extra_loan_details_loan_id` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`) on DELETE CASCADE;

