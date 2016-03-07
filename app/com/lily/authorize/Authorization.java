package com.lily.authorize;

import com.google.inject.ImplementedBy;
import com.lily.authorize.fitbit.FitbitAuthorizationImpl;
import com.lily.exception.AuthorizationException;

/**
 * Base authorization.
 * @author Mohammad 
 */
@ImplementedBy(FitbitAuthorizationImpl.class)
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