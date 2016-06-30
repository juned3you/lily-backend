package controllers;

import javax.inject.Inject;

import play.libs.Json;
import play.mvc.Result;

import com.lily.exception.AuthorizationException;
import com.lily.http.DashboardResponse;
import com.lily.models.FitbitUser;
import com.lily.services.DashboardService;
import com.lily.services.FitbitService;

/**
 * Fitbit controller for Fitbit operations.
 * 
 * @author Mohammad
 *
 */
public class DashboardController extends BaseController {

	@Inject
	private FitbitService fitbitService;

	@Inject
	private DashboardService dashboardService;

	/**
	 * Get Dashboard data.
	 * 
	 * @return
	 * @throws Throwable
	 * @throws AuthorizationException
	 */
	public Result getDashboardData(String userId) throws Throwable {

		if (userId == null)
			return badRequest("No userId found in request !!");

		FitbitUser fitbitUser = fitbitService.getFitbitUser(userId);
		if (fitbitUser == null)
			return badRequest("Fitbit user doesn't exists..");

		DashboardResponse response = dashboardService
				.getDashboardData(fitbitUser);

		setResponseHeaders();
		return ok(Json.toJson(response));
	}
}