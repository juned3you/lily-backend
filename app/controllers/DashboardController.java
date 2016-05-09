package controllers;

import javax.inject.Inject;

import play.libs.Json;
import play.mvc.Result;

import com.lily.exception.AuthorizationException;
import com.lily.http.GoalCompletionResponse;
import com.lily.models.FitbitUser;
import com.lily.process.GoalCompletionProcess;
import com.lily.services.FitbitService;
import com.lily.utils.LilyConstants.DurationInterval;

/**
 * Fitbit controller for Fitbit operations.
 * 
 * @author Mohammad
 *
 */
public class DashboardController extends BaseController {

	@Inject
	private GoalCompletionProcess goalCompletionProcess;

	@Inject
	private FitbitService fitbitService;

	/**
	 * Compose dynamic route
	 * 
	 * @return
	 * @throws Throwable
	 * @throws AuthorizationException
	 */
	public Result getMonthlyGoalCompletion(String userId) throws Throwable {
		if (userId == null)
			return badRequest("No userId found in request !!");

		FitbitUser fitbitUser = fitbitService.getFitbitUser(userId);
		if (fitbitUser == null)
			return badRequest("Fitbit user doesn't exists..");

		GoalCompletionResponse response = goalCompletionProcess
				.getGoalCompletion(fitbitUser, DurationInterval.MONTHLY);

		setResponseHeaders();
		return ok(Json.toJson(response));
	}

}