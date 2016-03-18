package com.lily.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * User model
 * 
 * @author Mohammad
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity {

	public User() {

	}

	public User(String firstname, String lastname, String email,
			String password, Date createdAt, Date lastModified) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.password = password;
		this.createdAt = createdAt;
		this.lastModified = lastModified;
	}

	@Column(name = "firstname")
	public String firstname;

	@Column(name = "lastname")
	public String lastname;

	@Column(name = "email")
	public String email;

	@JsonIgnore
	@Column(name = "password")
	public String password;
}