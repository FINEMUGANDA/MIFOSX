DROP TABLE IF EXISTS `entity_audit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE entity_audit
(
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  entity_name VARCHAR(100),
  entity_id BIGINT,
  column_name VARCHAR(100),
  from_value VARCHAR(100),
  to_value VARCHAR(100),
  user_entity VARCHAR(100),
  changed_by BIGINT,
  changed_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TRIGGER IF EXISTS `on_client_trigger`;
DELIMITER $$
CREATE TRIGGER `on_client_trigger` AFTER UPDATE ON m_client
FOR EACH ROW
  BEGIN
    IF NEW.`status_enum` <> OLD.`status_enum` AND NEW.`status_enum` = 100 THEN
      -- pending
      INSERT INTO entity_audit SET entity_name = 'CLIENT', entity_id = NEW.id, column_name= 'status_enum', from_value = OLD.status_enum, to_value = NEW.status_enum, user_entity = 'CLIENT', changed_by = NEW.submittedon_userid, changed_at = NOW();
    ELSEIF NEW.`status_enum` <> OLD.`status_enum` AND NEW.`status_enum` = 300 THEN
      -- active
      INSERT INTO entity_audit SET entity_name = 'CLIENT', entity_id = NEW.id, column_name= 'status_enum', from_value = OLD.status_enum, to_value = NEW.status_enum, user_entity = 'CLIENT', changed_by = NEW.activatedon_userid, changed_at = NOW();
    ELSEIF NEW.`status_enum` <> OLD.`status_enum` AND NEW.`status_enum` = 600 THEN
      -- closed
      INSERT INTO entity_audit SET entity_name = 'CLIENT', entity_id = NEW.id, column_name= 'status_enum', from_value = OLD.status_enum, to_value = NEW.status_enum, user_entity = 'CLIENT', changed_by = NEW.closedon_userid, changed_at = NOW();
    ELSEIF NEW.`status_enum` <> OLD.`status_enum` AND NEW.`status_enum` = 700 THEN
      -- rejected
      INSERT INTO entity_audit SET entity_name = 'CLIENT', entity_id = NEW.id, column_name= 'status_enum', from_value = OLD.status_enum, to_value = NEW.status_enum, user_entity = 'CLIENT', changed_by = NEW.rejectedon_userid, changed_at = NOW();
    ELSEIF NEW.`status_enum` <> OLD.`status_enum` AND NEW.`status_enum` = 800 THEN
      -- withdrawn
      INSERT INTO entity_audit SET entity_name = 'CLIENT', entity_id = NEW.id, column_name= 'status_enum', from_value = OLD.status_enum, to_value = NEW.status_enum, user_entity = 'CLIENT', changed_by = NEW.withdraw_on_userid, changed_at = NOW();
    ELSE
      INSERT INTO entity_audit SET entity_name = 'CLIENT', entity_id = NEW.id, column_name= 'status_enum', from_value = OLD.status_enum, to_value = NEW.status_enum, user_entity = 'CLIENT', changed_by = NEW.updated_by, changed_at = NOW();
    END IF;
  END;$$
DELIMITER ;

DROP TRIGGER IF EXISTS `on_loan_trigger`;
DELIMITER $$
CREATE TRIGGER `on_loan_trigger` AFTER UPDATE ON m_loan
FOR EACH ROW
  BEGIN
    IF NEW.`loan_status_id` <> OLD.`loan_status_id` AND NEW.`loan_status_id` = 100 THEN
      -- submitted and pending approval
      INSERT INTO entity_audit SET entity_name = 'LOAN', entity_id = NEW.id, column_name= 'loan_status_id', from_value = OLD.loan_status_id, to_value = NEW.loan_status_id, user_entity = 'USER', changed_by = NEW.submittedon_userid, changed_at = NOW();
    ELSEIF NEW.`loan_status_id` <> OLD.`loan_status_id` AND NEW.`loan_status_id` = 200 THEN
      -- approved
      INSERT INTO entity_audit SET entity_name = 'LOAN', entity_id = NEW.id, column_name= 'loan_status_id', from_value = OLD.loan_status_id, to_value = NEW.loan_status_id, user_entity = 'USER', changed_by = NEW.approvedon_userid, changed_at = NOW();
    ELSEIF NEW.`loan_status_id` <> OLD.`loan_status_id` AND NEW.`loan_status_id` = 300 THEN
      -- active
      INSERT INTO entity_audit SET entity_name = 'LOAN', entity_id = NEW.id, column_name= 'loan_status_id', from_value = OLD.loan_status_id, to_value = NEW.loan_status_id, user_entity = 'USER', changed_by = NEW.approvedon_userid, changed_at = NOW();
    ELSEIF NEW.`loan_status_id` <> OLD.`loan_status_id` AND NEW.`loan_status_id` = 400 THEN
      -- withdrawn by client
      INSERT INTO entity_audit SET entity_name = 'LOAN', entity_id = NEW.id, column_name= 'loan_status_id', from_value = OLD.loan_status_id, to_value = NEW.loan_status_id, user_entity = 'CLIENT', changed_by = NEW.client_id, changed_at = NOW();
    ELSEIF NEW.`loan_status_id` <> OLD.`loan_status_id` AND NEW.`loan_status_id` = 500 THEN
      -- rejected
      INSERT INTO entity_audit SET entity_name = 'LOAN', entity_id = NEW.id, column_name= 'loan_status_id', from_value = OLD.loan_status_id, to_value = NEW.loan_status_id, user_entity = 'USER', changed_by = NEW.rejectedon_userid, changed_at = NOW();
    ELSEIF NEW.`loan_status_id` <> OLD.`loan_status_id` AND NEW.`loan_status_id` = 600 THEN
      -- closed obligations met
      INSERT INTO entity_audit SET entity_name = 'LOAN', entity_id = NEW.id, column_name= 'loan_status_id', from_value = OLD.loan_status_id, to_value = NEW.loan_status_id, user_entity = 'USER', changed_by = NEW.closedon_userid, changed_at = NOW();
    ELSEIF NEW.`loan_status_id` <> OLD.`loan_status_id` AND NEW.`loan_status_id` = 601 THEN
      -- closed written off
      INSERT INTO entity_audit SET entity_name = 'LOAN', entity_id = NEW.id, column_name= 'loan_status_id', from_value = OLD.loan_status_id, to_value = NEW.loan_status_id, user_entity = 'USER', changed_by = NEW.closedon_userid, changed_at = NOW();
    ELSEIF NEW.`loan_status_id` <> OLD.`loan_status_id` AND NEW.`loan_status_id` = 602 THEN
      -- closed reschedule
      INSERT INTO entity_audit SET entity_name = 'LOAN', entity_id = NEW.id, column_name= 'loan_status_id', from_value = OLD.loan_status_id, to_value = NEW.loan_status_id, user_entity = 'USER', changed_by = NEW.closedon_userid, changed_at = NOW();
    ELSEIF NEW.`loan_status_id` <> OLD.`loan_status_id` AND NEW.`loan_status_id` = 700 THEN
      -- overpaid
      INSERT INTO entity_audit SET entity_name = 'LOAN', entity_id = NEW.id, column_name= 'loan_status_id', from_value = OLD.loan_status_id, to_value = NEW.loan_status_id, user_entity = 'CLIENT', changed_by = NEW.client_id, changed_at = NOW();
    ELSEIF NEW.`loan_status_id` <> OLD.`loan_status_id` AND OLD.`loan_status_id` = 200 AND NEW.`loan_status_id` = 800 THEN
      -- active in good standing (from approved)
      INSERT INTO entity_audit SET entity_name = 'LOAN', entity_id = NEW.id, column_name= 'loan_status_id', from_value = OLD.loan_status_id, to_value = NEW.loan_status_id, user_entity = 'CLIENT', changed_by = NEW.disbursedon_userid, changed_at = NOW();
    ELSEIF NEW.`loan_status_id` <> OLD.`loan_status_id` AND NEW.`loan_status_id` = 800 THEN
      -- active in good standing
      INSERT INTO entity_audit SET entity_name = 'LOAN', entity_id = NEW.id, column_name= 'loan_status_id', from_value = OLD.loan_status_id, to_value = NEW.loan_status_id, user_entity = 'CLIENT', changed_by = NEW.client_id, changed_at = NOW();
    ELSEIF NEW.`loan_status_id` <> OLD.`loan_status_id` AND NEW.`loan_status_id` = 900 THEN
      -- active in bad standing
      INSERT INTO entity_audit SET entity_name = 'LOAN', entity_id = NEW.id, column_name= 'loan_status_id', from_value = OLD.loan_status_id, to_value = NEW.loan_status_id, user_entity = 'CLIENT', changed_by = NEW.client_id, changed_at = NOW();
    END IF;
  END;$$
DELIMITER ;
