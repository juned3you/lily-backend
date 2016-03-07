package controllers;

import java.text.ParseException;

import javax.inject.Inject;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.lily.authorize.Authorization;
import com.lily.authorize.AuthorizationRequest;
import com.lily.authorize.fitbit.FitbitException;
import com.lily.exception.AuthorizationException;
import com.lily.factory.AuthorizationFactory;
import com.lily.services.FitbitService;
import com.lily.utils.DateUtils;
import com.lily.utils.LilyConstants;

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
	 */
	public Result oauthCallback() throws AuthorizationException {
		final String code = request().getQueryString("code");
		if (code == null)
			return badRequest("No authorization code found !!");

		AuthorizationRequest authRequest = new AuthorizationRequest();
		authRequest.authorizationCode = code;
		return ok(Json.toJson(fitbitService.getAuthResponseByCode(code).userId));
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

}