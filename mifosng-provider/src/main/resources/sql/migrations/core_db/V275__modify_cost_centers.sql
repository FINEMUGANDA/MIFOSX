INSERT INTO m_code (`code_name`) VALUES ('Non Staff');

ALTER TABLE m_cost_center ADD COLUMN id BIGINT(20) PRIMARY KEY AUTO_INCREMENT;

ALTER TABLE m_cost_center DROP FOREIGN KEY m_cost_center_ibfk_1;

ALTER TABLE m_cost_center DROP FOREIGN KEY m_cost_center_ibfk_2;

ALTER TABLE m_cost_center DROP INDEX m_cost_center_unique;

ALTER TABLE m_cost_center ADD CONSTRAINT m_cost_center_ibfk_1 FOREIGN KEY (staff_id) REFERENCES m_staff(id)  ON DELETE CASCADE;

ALTER TABLE m_cost_center ADD CONSTRAINT m_cost_center_ibfk_2 FOREIGN KEY (acc_gl_account_id) REFERENCES acc_gl_account(id)  ON DELETE CASCADE;

ALTER TABLE m_cost_center MODIFY staff_id BIGINT(20) NULL;

ALTER TABLE m_cost_center ADD COLUMN non_staff_id INT;

ALTER TABLE m_cost_center ADD CONSTRAINT m_cost_center_ibfk_3 FOREIGN KEY (non_staff_id) REFERENCES m_code_value(id)  ON DELETE CASCADE;

ALTER TABLE m_cost_center ADD COLUMN cost_center_type VARCHAR(10) DEFAULT 'staff';