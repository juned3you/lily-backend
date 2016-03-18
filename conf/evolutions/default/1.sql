CREATE DATABASE lily_db;

CREATE TABLE users (
	id bigint IDENTITY(1,1),
	firstname VARCHAR(30) NOT NULL,
	lastname VARCHAR(30) NOT NULL,	
	email VARCHAR(50) NOT NULL,
	password VARCHAR(100),
	created TIMESTAMP NOT NULL,
	last_modified TIMESTAMP,
	PRIMARY KEY (id)
);