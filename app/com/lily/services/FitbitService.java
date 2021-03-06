package com.lily.services;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import org.apache.commons.beanutils.BeanUtils;

import play.Logger;
import play.db.jpa.JPA;
import play.libs.Json;

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
import com.lily.utils.JpaUtils;
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
	 * @throws Exception 
	 */
	public String createUpdateUser(String userId) throws Exception {
		FitbitUser fitbitUser = getUserFromServer(userId);
		try {

			JPA.withTransaction(() -> {
				String email = userId + LilyConstants.EMAIL_SUFFIX;
				EntityManager em = JPA.em();

				FitbitUser persistFitbitUser = JpaUtils
						.getSingleResultOrElseNull(
								em.createQuery("FROM User where email = '"
										+ email + "'"), FitbitUser.class);

				// New creation
				if (persistFitbitUser == null) {
					persistFitbitUser = new FitbitUser();
					BeanUtils.copyProperties(persistFitbitUser, fitbitUser);
					persistFitbitUser.firstname = fitbitUser.fullName;
					persistFitbitUser.lastname = fitbitUser.fullName;
					persistFitbitUser.email = email;
					String hashedPassword = PasswordHasher
							.hash(LilyConstants.DEFAULT_PASSWORD);
					persistFitbitUser.password = hashedPassword;
					persistFitbitUser.createdAt = new Date();

					em.persist(persistFitbitUser);
					Logger.info("Created new User profile: " + email);
				} else { // Update.

					fitbitUser.firstname = persistFitbitUser.firstname;
					fitbitUser.lastname = persistFitbitUser.lastname;
					fitbitUser.email = persistFitbitUser.email;
					fitbitUser.password = persistFitbitUser.password;
					fitbitUser.createdAt = persistFitbitUser.createdAt;
					fitbitUser.id = persistFitbitUser.id;

					// Copy new properties.
					BeanUtils.copyProperties(persistFitbitUser, fitbitUser);

					persistFitbitUser.lastModified = new Date();
					em.merge(persistFitbitUser);
					Logger.info("Update User profile: " + email);
				}
			});
		} catch (Exception e) {
			throw new FitbitException(e);
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
	 * Get fitbit data for dynamic url.
	 * 
	 * @param userId
	 *            * @param date
	 * @return
	 */
	public String getFitbitData(String userId, String uri)
			throws FitbitException {
		try {
			Response response = getServerResponse(userId, Verb.GET,
					fitbitClient.endpoint + String.format("/%s.json", uri));
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
		//System.out.println(url);
		AuthorizationResponse authResponse = getAuthResponse(userId);
		OAuthRequest request = new OAuthRequest(verb, url, service);
		service.signRequest(authResponse.oauth2accessToken, request);
		Response response = request.send();
		return response;
	}

	/**
	 * Return all fitbit users from DB.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<FitbitUser> getFitbitUsers() {
		try {
			return JPA.withTransaction(() -> {
				return (List<FitbitUser>) JPA.em()
						.createQuery("From FitbitUser").getResultList();
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public FitbitUser getFitbitUser(String userId) throws Throwable {
		return JPA.withTransaction(() -> {
			return JpaUtils.getSingleResultOrElseNull(
					JPA.em().createQuery(
							"FROM FitbitUser where encodedId = '" + userId
									+ "'"), FitbitUser.class);
		});
	}
	
	public FitbitUser getFitbitUserByEmail(String email) throws Throwable {
		return JPA.withTransaction(() -> {
			return JpaUtils.getSingleResultOrElseNull(
					JPA.em().createQuery(
							"FROM FitbitUser where email = '" + email
									+ "'"), FitbitUser.class);
		});
	}

	/**
	 * Call Fitbit server and get User Profile.
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public FitbitUser getUserFromServer(String userId) throws Exception {
		String response = getDynamicData(userId, "profile");

		JsonNode jsValue = Json.parse(response);
		JsonNode userNode = jsValue.get("user");
		if (userNode == null)
			throw new Exception("No user found in profile json response..");

		return JsonUtils.fromJson(userNode, FitbitUser.class);
	}
}
