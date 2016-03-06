package com.lily.authorize.fitbit;

import java.util.Calendar;
import java.util.Date;

import play.libs.Json;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.lily.authorize.Authorization;
import com.lily.authorize.AuthorizationRequest;
import com.lily.authorize.AuthorizationResponse;
import com.lily.models.AccessToken;
import com.lily.models.Client;

import con.lily.exception.AuthorizationException;

/**
 * Fitbit authorization steps.
 * 
 * @author Mohammad
 *
 */
public class FitbitAuthorizationImpl implements Authorization {

	private Client fitbitClient = null;
	private OAuth20Service service;

	public FitbitAuthorizationImpl() {
		fitbitClient = FitbitOAuth2Service.getFitbitClient();
		// Create OAuth20Service for FitbitApi
		service = FitbitOAuth2Service
				.getFitbitOAuth2ServiceInstance(fitbitClient);
	}

	/**
	 * Authenticate using auth code and generating access token for further
	 * call.
	 */
	@Override
	public AuthorizationResponse authorize(AuthorizationRequest authRequest)
			throws AuthorizationException {
		if (authRequest == null)
			throw new AuthorizationException(
					"AuthorizationRequest can't be null..");

		AuthorizationResponse response = new AuthorizationResponse();
		try {
			if (authRequest.userId != null) {
				AccessToken accessToken = Ebean.find(AccessToken.class).where()
						.eq("userId", authRequest.userId)
						.eq("client", fitbitClient).findUnique();

				// Check access token and it's expiration
				if (accessToken != null) {
					Boolean isExpired = isAccessTokenExpired(accessToken);
					if (isExpired == false) {
						OAuth2AccessToken oauthToken = toOauth2AccessToken(accessToken);
						response.oauth2accessToken = oauthToken;
						response.userId = authRequest.userId;
						return response;
					} else { // Renew it.
						OAuth2AccessToken oauthToken = service
								.refreshAccessToken(accessToken.refreshToken);
						saveAccessToken(oauthToken, authRequest.userId);					

						response.oauth2accessToken = oauthToken;
						response.userId = authRequest.userId;
						return response;
					}
				}
			}

			/**
			 * Create new Access token.
			 */
			OAuth2AccessToken newOauthaccessToken = service
					.getAccessToken(new Verifier(authRequest.authorizationCode));
			JsonNode jsResponse = Json.parse(newOauthaccessToken
					.getRawResponse());
			JsonNode userId = jsResponse.get("user_id");

			saveAccessToken(newOauthaccessToken, userId.textValue());

			response.oauth2accessToken = newOauthaccessToken;
			response.userId = userId.textValue();

		} catch (Throwable t) {
			t.printStackTrace();
			response.error = t.getMessage();
		}

		return response;
	}

	/**
	 * Get and check Access token expiry.
	 * 
	 * @param client
	 * @param userId
	 * @return
	 */
	private Boolean isAccessTokenExpired(AccessToken accessToken) {
		if (accessToken == null)
			return true;

		Calendar cal = Calendar.getInstance();
		cal.setTime(accessToken.createdAt);
		cal.add(Calendar.SECOND, accessToken.expiresIn);

		if (new Date().after(cal.getTime()))
			return true;

		return false;
	}

	@Override
	public String getAuthorizationUrl() throws AuthorizationException {
		return service.getAuthorizationUrl();
	}

	/**
	 * Delete and Save token in Db.
	 * 
	 * @param newOauthaccessToken
	 */
	private void saveAccessToken(OAuth2AccessToken newOauthaccessToken, String userId) {		

		// deleting old
		AccessToken oldAccessToken = Ebean.find(AccessToken.class).where()
				.eq("userId", userId).eq("client", fitbitClient)
				.findUnique();
		if (oldAccessToken != null)
			Ebean.delete(oldAccessToken);

		AccessToken newAccessToken = new AccessToken(
				newOauthaccessToken.getAccessToken(),
				newOauthaccessToken.getRefreshToken(), userId,
				newOauthaccessToken.getScope(),
				newOauthaccessToken.getTokenType(), new Date(),
				newOauthaccessToken.getExpiresIn(), fitbitClient);
		Ebean.save(newAccessToken);
	}

	private OAuth2AccessToken toOauth2AccessToken(AccessToken accessToken) {
		OAuth2AccessToken oauthToken = new OAuth2AccessToken(
				accessToken.accessToken, accessToken.tokenType,
				accessToken.expiresIn, accessToken.refreshToken,
				accessToken.scope, null);
		return oauthToken;
	}
}