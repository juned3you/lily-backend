package com.lily.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "lilyuser")
public class LilyUser extends User {

	@Column(name = "testfield")
	public String testfield;
	
	public LilyUser(){
		
	}
	
	public LilyUser(String firstname, String lastname, String email,
			String password, Date createdAt, Date lastModified) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.password = password;
		this.createdAt = createdAt;
		this.lastModified = lastModified;
	}	
}
