select 1,'Richard','Nsanze','male',29222,'Kampala','O-level';
select 2,'Milton','Mido','male',21/3/2002,'Masaka','A-level';
select 3,'Stephen','Nkuru','male',30/2/1979,'Jinja','Primary';
select 4,'James','Koko','male',31/12/1986,'Mbarara','Other';

drop table examples;
create table examples(
id			int auto_increment not null,
name		char(60) not null,
surname		char(60) not null,
sex			char(60) not null,
dob			date not null,
address		char(60) not null,
education	char(60) not null,
primary key (id)
);