DROP TABLE IF EXISTS `client_note_general`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE client_note_general
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    client_id BIGINT NOT NULL,
    staff_username VARCHAR(100),
    notes LONGTEXT,
    created_at DATE,
    called_at DATE,
    visited_at DATE,
    phone VARCHAR(100),
    NoteSource_cd_source INT NOT NULL,
    FOREIGN KEY (client_id) REFERENCES m_client (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
CREATE INDEX fk_client_id ON client_note_general (client_id);

INSERT IGNORE INTO `mifostenant-default`.x_registered_table (registered_table_name, application_table_name, category) VALUES ('client_note_general', 'm_client', 100);
INSERT IGNORE INTO `mifostenant-default`.m_code (code_name, is_system_defined) VALUES ('NoteSource', 0);
INSERT IGNORE INTO `mifostenant-default`.m_code_value (code_id, code_value, code_description, order_position, code_score) VALUES ((SELECT id FROM m_code WHERE code_name='NoteSource'), 'Phone Call', null, 1, null);
INSERT IGNORE INTO `mifostenant-default`.m_code_value (code_id, code_value, code_description, order_position, code_score) VALUES ((SELECT id FROM m_code WHERE code_name='NoteSource'), 'Visit at Office', null, 2, null);
INSERT IGNORE INTO `mifostenant-default`.m_code_value (code_id, code_value, code_description, order_position, code_score) VALUES ((SELECT id FROM m_code WHERE code_name='NoteSource'), 'Visit at Business', null, 3, null);
INSERT IGNORE INTO `mifostenant-default`.m_code_value (code_id, code_value, code_description, order_position, code_score) VALUES ((SELECT id FROM m_code WHERE code_name='NoteSource'), 'Visit at Residence', null, 4, null);
