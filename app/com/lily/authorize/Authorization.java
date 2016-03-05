package com.lily.authorize;

import con.lily.exception.AuthorizationException;

/**
 * Base authorization.
 * @author Mohammad 
 */
public interface Authorization {
	
	/**
	 * Authorize from API and return results.
	 * @return
	 * @throws AuthorizationException
	 */
	public AuthorizationResponse authorize(AuthorizationRequest request) throws AuthorizationException;
	
	/**
	 * Get Authorization url for api.
	 * @return
	 * @throws AuthorizationException
	 */
	public String getAuthorizationUrl() throws AuthorizationException;
}