package controllers;

import java.text.ParseException;

import javax.inject.Inject;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.lily.authorize.AuthorizationRequest;
import com.lily.authorize.fitbit.FitbitException;
import com.lily.exception.AuthorizationException;
import com.lily.models.FitbitUser;
import com.lily.services.FitbitService;
import com.lily.utils.DateUtils;
import com.lily.utils.JsonUtils;
import com.lily.utils.LilyConstants;
import com.typesafe.config.ConfigFactory;

/**
 * Fitbit controller for Fitbit operations.
 * 
 * @author Mohammad
 *
 */
public class FitbitController extends Controller {

	@Inject
	private FitbitService fitbitService;

	/**
	 * check Fitbit code in request and register user.
	 * 
	 * @return
	 * @throws AuthorizationException
	 * @throws FitbitException
	 */
	public Result oauthCallback() throws Exception {
		final String code = request().getQueryString("code");
		if (code == null)
			return badRequest("No authorization code found !!");

		AuthorizationRequest authRequest = new AuthorizationRequest();
		authRequest.authorizationCode = code;
		String userId = fitbitService.getAuthResponseByCode(code).userId;
		if (userId == null)
			throw new AuthorizationException(
					"User id not found in fitbit response!!");

		// Register to our db.
		// fitbitService.createUpdateUser(userId);

		String response = fitbitService.getDynamicData(userId, "profile");

		JsonNode jsValue = Json.parse(response);
		JsonNode userNode = jsValue.get("user");
		if (userNode == null)
			return badRequest("No user found in profile json response..");

		FitbitUser fitbitUser = JsonUtils.fromJson(userNode, FitbitUser.class);

		response().setCookie("firstname", fitbitUser.getFullName());
		response().setCookie("lastname", fitbitUser.getFullName());
		response().setCookie("userId", fitbitUser.getEncodedId());
		response().setCookie(LilyConstants.USER_TYPE,
				LilyConstants.Fitbit.CLIENT_NAME);

		return redirect(ConfigFactory.load().getString(
				"material.ui.callback.url"));
	}

	/**
	 * Get Fitbit user profile.
	 * 
	 * @return
	 * @throws AuthorizationException
	 */
	public Result getUserProfile(String userId) {
		if (userId == null)
			return badRequest("No userId found in request !!");
		String response = null;
		try {
			response = fitbitService.getUserProfile(userId);
		} catch (FitbitException e) {
			return badRequest(e.getMessage());
		}

		return ok(response);
	}

	/**
	 * Get Fitbit user activities.
	 * 
	 * @return
	 * @throws AuthorizationException
	 */
	public Result getUserActivities(String userId, String date) {
		if (userId == null || date == null)
			return badRequest("No userId or date found in request !!");
		try {
			DateUtils.formatDate(date);
		} catch (ParseException ex) {
			return badRequest("Invalid date format!!. Date should be yyyy-MM-dd format.");
		}

		String response = null;
		try {
			response = fitbitService.getDailyActivitySummary(userId, date);
		} catch (FitbitException e) {
			return badRequest(e.getMessage());
		}

		return ok(response);
	}

	/**
	 * Get Fitbit user sleep.
	 * 
	 * @return
	 * @throws AuthorizationException
	 */
	public Result getSleep(String userId, String date) {
		if (userId == null || date == null)
			return badRequest("No userId or date found in request !!");
		try {
			DateUtils.formatDate(date);
		} catch (ParseException ex) {
			return badRequest("Invalid date format!!. Date should be yyyy-MM-dd format.");
		}

		String response = null;
		try {
			response = fitbitService.getSleep(userId, date);
		} catch (FitbitException e) {
			return badRequest(e.getMessage());
		}

		return ok(response);
	}

	/**
	 * Get Fitbit user sleep goal.
	 * 
	 * @return
	 * @throws AuthorizationException
	 */
	public Result getSleepGoal(String userId) {
		if (userId == null)
			return badRequest("No userId found in request !!");

		String response = null;
		try {
			response = fitbitService.getSleepGoal(userId);
		} catch (FitbitException e) {
			return badRequest(e.getMessage());
		}

		return ok(response);
	}

	/**
	 * Compose dynamic route
	 * 
	 * @return
	 * @throws AuthorizationException
	 */
	public Result dynamicRouteWithUser(String userId, String uri) {
		if (userId == null || uri == null)
			return badRequest("No userId or uri found in request !!");

		String response = null;
		try {
			response = fitbitService.getDynamicData(userId, uri);
		} catch (FitbitException e) {
			return badRequest(e.getMessage());
		}

		return ok(response);
	}

}