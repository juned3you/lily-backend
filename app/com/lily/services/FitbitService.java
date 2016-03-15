package com.lily.services;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.lily.authorize.Authorization;
import com.lily.authorize.AuthorizationRequest;
import com.lily.authorize.AuthorizationResponse;
import com.lily.authorize.fitbit.FitbitAuthorizationImpl;
import com.lily.authorize.fitbit.FitbitException;
import com.lily.authorize.fitbit.FitbitOAuth2Service;
import com.lily.exception.AuthorizationException;
import com.lily.models.Client;
import com.lily.utils.LilyConstants;
import com.typesafe.config.ConfigFactory;

/**
 * Firbit service for Fit bit operations.
 * 
 * @author Mohammad Juned
 *
 */
@Singleton
public class FitbitService {

	private Client fitbitClient;
	private OAuth20Service service;

	@Inject
	private Authorization authorization;

	public FitbitService() {
		fitbitClient = FitbitOAuth2Service.getFitbitClient();
		service = FitbitOAuth2Service
				.getFitbitOAuth2ServiceInstance(fitbitClient);
		
		if(authorization == null)
			authorization = new FitbitAuthorizationImpl();
	}

	/**
	 * Get User profile by userid
	 * 
	 * @param userId
	 * @return
	 */
	public String getUserProfile(String userId) throws FitbitException {
		try {
			String userEndpoint = ConfigFactory.load().getString(
					LilyConstants.Fitbit.USER_PROFILE_URI);
			Response response = getServerResponse(userId, Verb.GET,
					fitbitClient.endpoint + String.format(userEndpoint, userId));
			return response.getBody();
		} catch (Exception e) {
			throw new FitbitException(e);
		}
	}

	/**
	 * Get Daily activities.
	 * 
	 * @param userId
	 * * @param date
	 * @return
	 */
	public String getDailyActivitySummary(String userId, String date)
			throws FitbitException {
		try {
			String userEndpoint = ConfigFactory.load().getString(
					LilyConstants.Fitbit.DAILY_ACTIVITIES_URI);
			Response response = getServerResponse(
					userId,
					Verb.GET,
					fitbitClient.endpoint
							+ String.format(userEndpoint, userId, date));
			return response.getBody();
		} catch (Exception e) {
			throw new FitbitException(e);
		}
	}
	
	/**
	 * Get Sleep.
	 * 
	 * @param userId
	 * * @param date
	 * @return
	 */
	public String getSleep(String userId, String date)
			throws FitbitException {
		try {
			String userEndpoint = ConfigFactory.load().getString(
					LilyConstants.Fitbit.SLEEP_URI);
			Response response = getServerResponse(
					userId,
					Verb.GET,
					fitbitClient.endpoint
							+ String.format(userEndpoint, userId, date));
			return response.getBody();
		} catch (Exception e) {
			throw new FitbitException(e);
		}
	}
	
	/**
	 * Get Sleep.
	 * 
	 * @param userId
	 * * @param date
	 * @return
	 */
	public String getSleepGoal(String userId)
			throws FitbitException {
		try {
			String userEndpoint = ConfigFactory.load().getString(
					LilyConstants.Fitbit.SLEEP_GOAL_URI);
			Response response = getServerResponse(
					userId,
					Verb.GET,
					fitbitClient.endpoint
							+ String.format(userEndpoint, userId));
			return response.getBody();
		} catch (Exception e) {
			throw new FitbitException(e);
		}
	}
	
	/**
	 * Dynamic route
	 * 
	 * @param userId
	 * * @param date
	 * @return
	 */
	public String getDynamicData(String userId, String uri)
			throws FitbitException {
		try {			
			Response response = getServerResponse(
					userId,
					Verb.GET,
					fitbitClient.endpoint
							+ String.format("/user/%s/%s.json", userId, uri));
			return response.getBody();
		} catch (Exception e) {
			throw new FitbitException(e);
		}
	}

	/**
	 * Gets Auth response based on user id.
	 * 
	 * @param userId
	 * @return
	 * @throws AuthorizationException
	 */
	private AuthorizationResponse getAuthResponse(String userId)
			throws AuthorizationException {
		AuthorizationRequest authRequest = new AuthorizationRequest();
		authRequest.userId = userId;
		AuthorizationResponse authResponse = authorization
				.authorize(authRequest);
		return authResponse;
	}

	/**
	 * Gets Auth response based on user id.
	 * 
	 * @param code
	 * @return
	 * @throws AuthorizationException
	 */
	public AuthorizationResponse getAuthResponseByCode(String code)
			throws AuthorizationException {
		AuthorizationRequest authRequest = new AuthorizationRequest();
		authRequest.authorizationCode = code;
		AuthorizationResponse authResponse = authorization
				.authorize(authRequest);
		return authResponse;
	}

	/**
	 * Call Fitbit server based on url provided.
	 * 
	 * @param userId
	 * @param url
	 * @return
	 * @throws AuthorizationException
	 */
	private Response getServerResponse(String userId, Verb verb, String url)
			throws AuthorizationException {
		AuthorizationResponse authResponse = getAuthResponse(userId);
		OAuthRequest request = new OAuthRequest(verb, url, service);
		service.signRequest(authResponse.oauth2accessToken, request);
		Response response = request.send();
		return response;
	}
}
