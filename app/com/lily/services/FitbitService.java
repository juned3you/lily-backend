package com.lily.services;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.beanutils.BeanUtils;

import play.Logger;
import play.libs.Json;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
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
import com.lily.models.FitbitUser;
import com.lily.models.User;
import com.lily.utils.JsonUtils;
import com.lily.utils.LilyConstants;
import com.lily.utils.PasswordHasher;
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

		if (authorization == null)
			authorization = new FitbitAuthorizationImpl();
	}

	/**
	 * Register new user from Fitbit
	 * 
	 * @param userId
	 * @return
	 * @throws FitbitException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public String createUpdateUser(String userId) throws FitbitException,
			JsonParseException, JsonMappingException, IOException {

		String response = getDynamicData(userId, "profile");
		JsonNode jsValue = Json.parse(response);
		JsonNode userNode = jsValue.get("user");
		if (userNode == null)
			throw new FitbitException(
					"No user found in profile json response..");

		FitbitUser fitbitUser = JsonUtils.fromJson(userNode, FitbitUser.class);
		try {
			Ebean.beginTransaction();
			String email = userId + LilyConstants.EMAIL_SUFFIX;
			User user = Ebean.createQuery(User.class).where()
					.eq("email", email).findUnique();

			// New creation
			if (user == null) {
				user = new User();
				user.firstname = fitbitUser.fullName;
				user.lastname = fitbitUser.fullName;
				user.email = email;
				String hashedPassword = PasswordHasher
						.hash(LilyConstants.DEFAULT_PASSWORD);
				user.password = hashedPassword;
				user.createdAt = new Date();
				Ebean.save(user);
				fitbitUser.user = user;
				fitbitUser.createdAt = new Date();
				Ebean.save(fitbitUser);
				Logger.info("Created new User profile: " + email);
			} else { // Update.

				FitbitUser persistFitbitUser = Ebean
						.createQuery(FitbitUser.class).where().eq("user", user)
						.findUnique();
				Long id = persistFitbitUser.id;
				Date createdAt = persistFitbitUser.createdAt;
				
				//Copy new properties.
				BeanUtils.copyProperties(persistFitbitUser, fitbitUser);
				
				persistFitbitUser.id = id;
				persistFitbitUser.user = user;
				persistFitbitUser.createdAt = createdAt;
				fitbitUser.lastModified = new Date();
				Ebean.update(persistFitbitUser);
				Logger.info("Update User profile: " + email);
			}

			Ebean.commitTransaction();
		} catch (Exception e) {
			Ebean.rollbackTransaction();
			throw new FitbitException(e.getMessage());
		} finally {
			Ebean.endTransaction();
		}
		return userId;
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
	 *            * @param date
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
	 *            * @param date
	 * @return
	 */
	public String getSleep(String userId, String date) throws FitbitException {
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
	 *            * @param date
	 * @return
	 */
	public String getSleepGoal(String userId) throws FitbitException {
		try {
			String userEndpoint = ConfigFactory.load().getString(
					LilyConstants.Fitbit.SLEEP_GOAL_URI);
			Response response = getServerResponse(userId, Verb.GET,
					fitbitClient.endpoint + String.format(userEndpoint, userId));
			return response.getBody();
		} catch (Exception e) {
			throw new FitbitException(e);
		}
	}

	/**
	 * Dynamic route
	 * 
	 * @param userId
	 *            * @param date
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
