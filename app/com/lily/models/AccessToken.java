package com.lily.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Access token table for storing tokens
 * @author Mohammad 
 */
@Entity
@Table(name = "access_token")
public class AccessToken{	

	public AccessToken() {
	}

	public AccessToken(String accessToken, String refreshToken,
			String userId, String scope, String tokenType, Date createdAt, Integer expiresIn,
			Client client) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.userId = userId;
		this.scope = scope;
		this.tokenType = tokenType;
		this.createdAt = createdAt;
		this.expiresIn = expiresIn;
		this.client = client;
	}

	@Id
	@Column(name = "access_token")
	public String accessToken;

	@Column(name = "refresh_token")
	public String refreshToken;

	@Column(name = "user_id")
	public String userId;

	@Column(name = "token_type")
	public String tokenType;
	
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