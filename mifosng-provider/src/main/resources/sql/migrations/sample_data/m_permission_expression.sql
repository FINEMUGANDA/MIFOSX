--
-- Table structure for table `m_permission_expression`
--

DROP TABLE IF EXISTS `m_permission_expression`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `m_permission_expression` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `entity_name` varchar(100) DEFAULT NULL,
  `action_name` varchar(100) DEFAULT NULL,
  `can_maker_checker` tinyint(1) NOT NULL DEFAULT '1',
  `code` varchar(100) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  `expression` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `role_id` FOREIGN KEY (`role_id`) REFERENCES `m_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `mifostenant-default`.m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('authorisation', 'EXPRESSIONS_ROLE', 'ROLE', 'PERMISSION_EXPRESSIONS', 0);