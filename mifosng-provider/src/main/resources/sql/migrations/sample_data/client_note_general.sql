DROP TABLE client_note_general;
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
);
CREATE INDEX fk_client_id ON client_note_general (client_id);

INSERT INTO `mifostenant-default`.x_registered_table (registered_table_name, application_table_name, category) VALUES ('client_note_general', 'm_client', 100);

INSERT INTO `mifostenant-default`.m_code (code_name, is_system_defined) VALUES ('NoteSource', 0);

INSERT INTO `mifostenant-default`.m_code_value (code_id, code_value, code_description, order_position, code_score) VALUES ((SELECT id FROM m_code WHERE code_name='NoteSource'), 'Phone Call', null, 1, null);
INSERT INTO `mifostenant-default`.m_code_value (code_id, code_value, code_description, order_position, code_score) VALUES ((SELECT id FROM m_code WHERE code_name='NoteSource'), 'Visit at Office', null, 2, null);
INSERT INTO `mifostenant-default`.m_code_value (code_id, code_value, code_description, order_position, code_score) VALUES ((SELECT id FROM m_code WHERE code_name='NoteSource'), 'Visit at Business', null, 3, null);
INSERT INTO `mifostenant-default`.m_code_value (code_id, code_value, code_description, order_position, code_score) VALUES ((SELECT id FROM m_code WHERE code_name='NoteSource'), 'Visit at Residence', null, 4, null);
