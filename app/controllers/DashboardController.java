package controllers;

import javax.inject.Inject;

import play.libs.Json;
import play.mvc.Result;

import com.lily.exception.AuthorizationException;
import com.lily.http.GoalCompletionResponse;
import com.lily.process.GoalCompletionProcess;
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

		GoalCompletionResponse response = goalCompletionProcess.getGoalCompletion(userId,
				DurationInterval.MONTHLY);

		setResponseHeaders();
		return ok(Json.toJson(response));
	}

}