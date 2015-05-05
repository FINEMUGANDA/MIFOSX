ALTER TABLE `mifostenant-default`.m_staff ADD mobile_no2 VARCHAR(50) NULL;
ALTER TABLE `mifostenant-default`.m_staff ADD gender_cv_id INT NULL;
ALTER TABLE `mifostenant-default`.m_staff ADD marital_status_cv_id INT NULL;
ALTER TABLE `mifostenant-default`.m_staff ADD children INT NULL;
ALTER TABLE `mifostenant-default`.m_staff ADD address VARCHAR(255) NULL;
ALTER TABLE `mifostenant-default`.m_staff ADD email VARCHAR(255) NULL;
ALTER TABLE `mifostenant-default`.m_staff ADD emergency_contact_name VARCHAR(255) NULL;
ALTER TABLE `mifostenant-default`.m_staff ADD emergency_contact_mobile_no VARCHAR(50) NULL;
ALTER TABLE `mifostenant-default`.m_staff ADD emergency_contact_relation_cv_id INT NULL;

ALTER TABLE `mifostenant-default`.m_staff ADD FOREIGN KEY (gender_cv_id) REFERENCES m_code_value (id);
ALTER TABLE `mifostenant-default`.m_staff ADD FOREIGN KEY (marital_status_cv_id) REFERENCES m_code_value (id);
ALTER TABLE `mifostenant-default`.m_staff ADD FOREIGN KEY (emergency_contact_relation_cv_id) REFERENCES m_code_value (id);

CREATE INDEX FK_m_staff_gender_m_code_value ON m_staff (gender_cv_id);
CREATE INDEX FK_m_staff_marital_status_m_code_value ON m_staff (marital_status_cv_id);
CREATE INDEX FK_m_staff_emergency_contact_relation_m_code_value ON m_staff (emergency_contact_relation_cv_id);
