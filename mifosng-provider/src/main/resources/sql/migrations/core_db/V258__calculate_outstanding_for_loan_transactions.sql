DROP PROCEDURE IF EXISTS `calculateOutstanding`;
DROP PROCEDURE IF EXISTS `calculateOutstandingForLoan`;

DELIMITER //

CREATE PROCEDURE calculateOutstandingForLoan(
 IN loanId bigint(20)
)
BEGIN
  SET @outstanding = 0;
  UPDATE m_loan_transaction  t
    JOIN (
      select tt.id,
      @outstanding := if(transaction_type_enum = 1 or transaction_type_enum = 10, @outstanding + tt.amount, @outstanding - tt.amount) as tmp
      from m_loan_transaction tt
      where tt.loan_id = loanId
      and tt.is_reversed = false
      order by tt.transaction_date ASC, tt.id
      ) tt ON tt.id = t.id
    SET t.outstanding_loan_balance_derived = tmp
  where loan_id = loanId;
END;

//
DELIMITER;



DELIMITER //

CREATE PROCEDURE calculateOutstanding()
BEGIN
  DECLARE _id BIGINT UNSIGNED;
  DECLARE done BOOLEAN DEFAULT FALSE;
  DECLARE cur CURSOR FOR SELECT loan_id FROM m_loan_transaction GROUP BY loan_id;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done := TRUE;


OPEN cur;

  testLoop: LOOP
    FETCH cur INTO _id;
    IF done THEN
      LEAVE testLoop;
    END IF;
    CALL calculateOutstandingForLoan(_id);
  END LOOP testLoop;

  CLOSE cur;
END;

//
DELIMITER;


CALL calculateOutstanding();

DROP PROCEDURE IF EXISTS `calculateOutstanding`;
DROP PROCEDURE IF EXISTS `calculateOutstandingForLoan`;
