package com.lily.authorize;

import com.github.scribejava.core.model.OAuth2AccessToken;

/**
 * Auth result.
 * 
 * @author Mohammad
 *
 */
public class AuthorizationResponse {
	public String error = null;
	public String success = null;
	public Integer status = null;
	public OAuth2AccessToken oauth2accessToken = null;

	@Override
	public String toString() {
		return status + ": " +error+ " " + success;
	}
}