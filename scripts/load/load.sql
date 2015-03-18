CREATE TABLE doctor (
	id bigint primery key,
	gender integer,
	name varchar(80),
	email varchar(80),
	birthDate varchar(90),
	password varchar(80),
	discipline integer,
	link varchar(80)
);

CREATE TABLE patient (
	id bigint primery key,
	name varchar(80),
	password varchar(80),
	birthDate varchar(90),
	email varchar(80),
	gender integer
);