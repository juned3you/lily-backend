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
	public AuthorizationResult authorize() throws AuthorizationException;
}