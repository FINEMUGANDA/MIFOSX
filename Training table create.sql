drop table training;
create table training(
id 					int auto_increment not null,
loan_product 		char(30) not null,
disbursed_amount	bigint not null,
paid_amount			bigint not null,
date_run			datetime not null,
primary key (id)
);