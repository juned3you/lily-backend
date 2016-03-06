package com.lily.services;

import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.lily.authorize.Authorization;
import com.lily.authorize.AuthorizationRequest;
import com.lily.authorize.AuthorizationResponse;
import com.lily.authorize.fitbit.FitbitException;
import com.lily.authorize.fitbit.FitbitOAuth2Service;
import com.lily.factory.AuthorizationFactory;
import com.lily.utils.LilyConstants;

/**
 * Firbit service for Fit bit operations.
 * 
 * @author Mohammad Juned
 *
 */
public class FitbitService {

	/**
	 * Get User profile by userid
	 * 
	 * @param userId
	 * @return
	 */
	public String getUserProfile(String userId) throws FitbitException {

		try {
			AuthorizationRequest authRequest = new AuthorizationRequest();
			authRequest.userId = userId;
			Authorization auth = AuthorizationFactory
					.getAuthorizationImpl(LilyConstants.FITBIT_CLIENT_NAME);
			AuthorizationResponse authResponse = auth.authorize(authRequest);
			
			OAuth20Service service = FitbitOAuth2Service.getFitbitOAuth2ServiceInstance();
			OAuthRequest request = new OAuthRequest(Verb.GET,
					"https://api.fitbit.com/1/user/" + userId
							+ "/profile.json", service);
			service.signRequest(authResponse.oauth2accessToken, request);
			Response response = request.send();
			return response.getBody();			
		} catch (Exception e) {
			throw new FitbitException(e);
		}		
	}
}
