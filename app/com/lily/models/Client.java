package com.lily.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Client for API key and secret.
 * 
 * @author Mohammad
 *
 */
@Entity
@Table(name = "client")
public class Client {

	public Client() {
	}

	public Client(String apiKey, String name, String apiSecret,
			String redirectUri, String scope, String endpoint,
			String authorizationUrl, String accessTokenUrl,
			String refreshTokenUrl,
			Date createdAt) {
		this.apiKey = apiKey;
		this.name = name;
		this.apiSecret = apiSecret;
		this.redirectUri = redirectUri;
		this.scope = scope;

		this.endpoint = endpoint;
		this.authorizationUrl = authorizationUrl;
		this.accessTokenUrl = accessTokenUrl;
		this.refreshTokenUrl = refreshTokenUrl;
		this.createdAt = createdAt;
	}

	@Id
	public Long id;

	@Column(name = "api_key")
	public String apiKey;

	@Column(name = "name")
	public String name;

	@Column(name = "api_secret")
	public String apiSecret;

	@Column(name = "redirect_uri")
	public String redirectUri;

	@Column(name = "scope")
	public String scope;

	@Column(name = "endpoint")
	public String endpoint;

	@Column(name = "authorization_url")
	public String authorizationUrl;

	@Column(name = "access_token_url")
	public String accessTokenUrl;

	@Column(name = "refresh_token_url")
	public String refreshTokenUrl;
	
	@Column(name = "created_at")
	public Date createdAt;
}