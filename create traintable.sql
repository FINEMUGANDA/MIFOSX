CREATE TABLE traintable(
  id				int(11) NOT NULL AUTO_INCREMENT,
  loan_product		char(50) NOT NULL,
  disbursed_amount	bigint(50) NOT NULL,
  paid_amount		bigint(50) NOT NULL,
  date_run			datetime NOT NULL,
  PRIMARY KEY (id)
);
