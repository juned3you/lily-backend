package com.lily.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Auth code for a user.
 * 
 * @author Mohammad
 *
 */
@Entity
@Table(name = "auth_code")
public class AuthCode {

	@Id
	@Column(name = "authorization_code")
	public String authorizationCode;

	@ManyToOne
	@JoinColumn(name = "user_id")
	public String userId;

	@Column(name = "redirect_uri")
	public String redirectUri;

	@Column(name = "scope")
	public String scope;

	@Column(name = "created_at")
	public Date createdAt;

	@Column(name = "expires_in")
	public Integer expiresIn;

	@ManyToOne
	@JoinColumn(name = "client_id")
	public Client client;
}