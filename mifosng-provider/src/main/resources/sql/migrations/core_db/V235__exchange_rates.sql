DROP TABLE IF EXISTS `exchange_rate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE exchange_rate
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    office_id BIGINT NOT NULL,
    base_code VARCHAR(100),
    target_code VARCHAR(100),
    rate_month INT NOT NULL,
    rate_year INT NOT NULL,
    rate decimal(19,6),
    FOREIGN KEY (office_id) REFERENCES m_office (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
CREATE INDEX fk_office_id ON exchange_rate (office_id);

INSERT IGNORE INTO `mifostenant-default`.x_registered_table (registered_table_name, application_table_name, category) VALUES ('exchange_rate', 'm_office', 100);
