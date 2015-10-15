
ALTER TABLE `acc_gl_account`
  ADD COLUMN `affects_loan` BOOLEAN NOT NULL;

CREATE TABLE m_cost_center (
-- `id` bigint(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
 `staff_id` bigint(20) NOT NULL,
 `acc_gl_account_id` bigint(20) NOT NULL,
 CONSTRAINT `m_cost_center_unique` UNIQUE INDEX (`staff_id`, `acc_gl_account_id`),
 FOREIGN KEY (`staff_id`) REFERENCES `m_staff` (`id`) ON DELETE CASCADE,
 FOREIGN KEY (`acc_gl_account_id`) REFERENCES `acc_gl_account` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('accounting', 'READ_COSTCENTER', 'COSTCENTER', 'READ', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('accounting', 'CREATE_COSTCENTER', 'COSTCENTER', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('accounting', 'UPDATE_COSTCENTER', 'COSTCENTER', 'UPDATE', 0);
