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

	public AuthCode() {

	}

	public AuthCode(String authorizationCode, String userId,
			String redirectUri, String scope, Date createdAt,
			Integer expiresIn, Client client) {
		this.authorizationCode = authorizationCode;
		this.userId = userId;
		this.redirectUri = redirectUri;
		this.scope = scope;
		this.createdAt = createdAt;
		this.expiresIn = expiresIn;
		this.client = client;
	}

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