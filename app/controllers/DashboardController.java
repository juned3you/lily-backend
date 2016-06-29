package controllers;

import javax.inject.Inject;

import play.libs.Json;
import play.mvc.Result;

import com.lily.exception.AuthorizationException;
import com.lily.models.FitbitUser;
import com.lily.mongo.models.Friend;
import com.lily.mongo.models.GoalCompletion;
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

		GoalCompletion response = goalCompletionProcess
				.getGoalCompletion(fitbitUser, DurationInterval.MONTHLY);

		setResponseHeaders();
		return ok(Json.toJson(response));
	}
	
	/**
	 * Compose dynamic route
	 * 
	 * @return
	 * @throws Throwable
	 * @throws AuthorizationException
	 */
	public Result getFriendsMonthlyGoalCompletion(String userId) throws Throwable {
		
		if (userId == null)
			return badRequest("No userId found in request !!");

		FitbitUser fitbitUser = fitbitService.getFitbitUser(userId);
		if (fitbitUser == null)
			return badRequest("Fitbit user doesn't exists..");

		Friend user = Friend.find().filter("userId", userId)
				.get();
		
		if(user == null)
			return badRequest("No Friends found...");
		
		GoalCompletion response = goalCompletionProcess
				.getGoalCompletion(fitbitUser, DurationInterval.MONTHLY);

		setResponseHeaders();
		return ok(Json.toJson(response));
	}

}