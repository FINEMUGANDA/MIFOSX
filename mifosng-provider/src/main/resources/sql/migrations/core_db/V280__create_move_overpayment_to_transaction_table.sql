CREATE TABLE `overpayment_transaction_mapper` (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  `overpayment_transaction_id` bigint(20) NOT NULL,
  `repayment_transaction_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `overpayment_transaction_id` (`repayment_transaction_id`),
  CONSTRAINT `overpayment_transaction_mapper_m_loan_transaction_1` FOREIGN KEY (`overpayment_transaction_id`) REFERENCES `m_loan_transaction` (`id`),
  CONSTRAINT `overpayment_transaction_mapper_m_loan_transaction_2` FOREIGN KEY (`repayment_transaction_id`) REFERENCES `m_loan_transaction` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;