# --- !Ups

CREATE DATABASE lily_db;

CREATE TABLE user (
	id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	firstname VARCHAR(30) NOT NULL,
	lastname VARCHAR(30) NOT NULL,	
	email VARCHAR(50) NOT NULL,
	password VARCHAR(100),
	created TIMESTAMP NOT NULL,
	last_modified TIMESTAMP
); 

# --- !Downs