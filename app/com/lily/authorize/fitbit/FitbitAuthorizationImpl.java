package com.lily.authorize.fitbit;

import java.util.Calendar;
import java.util.Date;

import play.libs.Json;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.lily.authorize.Authorization;
import com.lily.authorize.AuthorizationRequest;
import com.lily.authorize.AuthorizationResponse;
import com.lily.models.AccessToken;
import com.lily.models.AuthCode;
import com.lily.models.Client;
import com.lily.utils.LilyConstants;
import com.typesafe.config.ConfigFactory;

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
		fitbitClient = Ebean.find(Client.class).where()
				.eq("name", LilyConstants.FITBIT_CLIENT_NAME).findUnique();
		// Create OAuth20Service for FitbitApi
		service = new ServiceBuilder().apiKey(fitbitClient.apiKey)
				.apiSecret(fitbitClient.apiSecret)
				.callback(fitbitClient.redirectUri).scope(fitbitClient.scope)
				.build(FitbitApi.instance(fitbitClient));
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
				AccessToken accessToken = getAccessToken(fitbitClient,
						authRequest.userId);

				// Check access token and it's expiration
				if (accessToken != null) {
					OAuth2AccessToken oauthToken = new OAuth2AccessToken(
							accessToken.accessToken, accessToken.tokenType,
							accessToken.expiresIn, accessToken.refreshToken,
							accessToken.scope, null);
					response.oauth2accessToken = oauthToken;
					return response;
				}

				AuthCode authCode = getAuthCode(fitbitClient,
						authRequest.userId);

				if (authCode != null) { // Check auth code and it's
										// expiration
					OAuth2AccessToken oauthToken = service
							.getAccessToken(new Verifier(
									authCode.authorizationCode));

					response.oauth2accessToken = oauthToken;
					return response;
				}
			} else if (authRequest.authorizationCode != null) {
				AuthCode authCode = getAuthCode(authRequest.authorizationCode);
				if (authCode != null) {
					Ebean.delete(Ebean.find(AccessToken.class).where()
							.eq("userId", authCode.userId)
							.eq("client", authCode.client).findUnique());
					Ebean.delete(authCode);
				}
			}

			OAuth2AccessToken newOauthaccessToken = service
					.getAccessToken(new Verifier(authRequest.authorizationCode));
			JsonNode jsResponse = Json.parse(newOauthaccessToken
					.getRawResponse());
			JsonNode userId = jsResponse.get("user_id");
			AccessToken newAccessToken = new AccessToken(
					newOauthaccessToken.getAccessToken(),
					newOauthaccessToken.getRefreshToken(), userId.textValue(),
					newOauthaccessToken.getScope(),
					newOauthaccessToken.getTokenType(), new Date(),
					newOauthaccessToken.getExpiresIn(), fitbitClient);
			Ebean.save(newAccessToken);

			AuthCode newAuthCode = new AuthCode(authRequest.authorizationCode,
					userId.textValue(), null, newOauthaccessToken.getScope(),
					new Date(), newOauthaccessToken.getExpiresIn(),
					fitbitClient);
			Ebean.save(newAuthCode);

			response.oauth2accessToken = newOauthaccessToken;

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
	private AccessToken getAccessToken(Client client, String userId) {
		AccessToken accessToken = Ebean.find(AccessToken.class).where()
				.eq("userId", userId).eq("client", client).findUnique();
		if (accessToken == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(accessToken.createdAt);
		cal.add(Calendar.SECOND, accessToken.expiresIn);

		if (new Date().after(cal.getTime())) {
			Ebean.delete(accessToken);
			return null;
		}

		return accessToken;
	}

	/**
	 * Get and check authCode expiry.
	 * 
	 * @param client
	 * @param userId
	 * @return
	 */
	private AuthCode getAuthCode(Client client, String userId) {
		AuthCode authCode = Ebean.find(AuthCode.class).where()
				.eq("userId", userId).eq("client", client).findUnique();
		if (authCode == null)
			return null;

		if (authCode.expiresIn != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(authCode.createdAt);
			cal.add(Calendar.SECOND, authCode.expiresIn);

			if (new Date().after(cal.getTime())) {
				Ebean.delete(authCode);
				return null;
			}
		}

		return authCode;
	}

	/**
	 * Get and check authCode expiry.
	 * 
	 * @param client
	 * @param userId
	 * @return
	 */
	private AuthCode getAuthCode(String code) {
		AuthCode authCode = Ebean.find(AuthCode.class).where()
				.eq("authorizationCode", code).findUnique();
		if (authCode == null)
			return null;

		if (authCode.expiresIn != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(authCode.createdAt);
			cal.add(Calendar.SECOND, authCode.expiresIn);

			if (new Date().after(cal.getTime())) {
				Ebean.delete(authCode);
				return null;
			}
		}

		return authCode;
	}

	@Override
	public String getAuthorizationUrl() throws AuthorizationException {
		return service.getAuthorizationUrl();
	}
}