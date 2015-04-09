


DROP TABLE IF EXISTS `m_financial_year`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_financial_year` (
  `id` bigint(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `start_year` int(11) NOT NULL,
  `end_year` int(11) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `current` tinyint(4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `mifostenant-default`.m_permission (`grouping`, code, entity_name, action_name, can_maker_checker)
	VALUES ('configuration', 'CREATE_FINANCIALYEAR', 'FINANCIALYEAR', 'CREATE', true);
INSERT INTO `mifostenant-default`.m_permission (`grouping`, code, entity_name, action_name, can_maker_checker)
	VALUES ('configuration', 'UPDATE_FINANCIALYEAR', 'FINANCIALYEAR', 'UPDATE', true);
INSERT INTO `mifostenant-default`.m_permission (`grouping`, code, entity_name, action_name, can_maker_checker)
	VALUES ('configuration', 'READ_FINANCIALYEAR', 'FINANCIALYEAR', 'READ', true);
INSERT INTO `mifostenant-default`.m_permission (`grouping`, code, entity_name, action_name, can_maker_checker)
	VALUES ('configuration', 'DELETE_FINANCIALYEAR', 'FINANCIALYEAR', 'DELETE', true);