package com.lily.services;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.lily.http.DashboardResponse;
import com.lily.models.FitbitUser;
import com.lily.mongo.models.Friend;
import com.lily.mongo.models.User;
import com.lily.process.GoalCompletionProcess;
import com.lily.utils.LilyConstants.DurationInterval;

@Singleton
public class DashboardService {

	@Inject
	private GoalCompletionProcess goalCompletionProcess;

	/**
	 * Compose all data for dashboard.
	 * 
	 * @param fitbitUser
	 * @return
	 * @throws Throwable
	 */
	public DashboardResponse getDashboardData(final FitbitUser fitbitUser)
			throws Throwable {
		final DashboardResponse dashboardResponse = new DashboardResponse();
		final DurationInterval monthlyInterval = DurationInterval.MONTHLY;

		// Monthly goal completion.
		dashboardResponse.monthlyGoalCompletion = goalCompletionProcess
				.getGoalCompletion(fitbitUser, monthlyInterval);

		// Friends data.
		Friend user = Friend.find().filter("userId", fitbitUser.encodedId)
				.get();
		if (user != null && user.users != null && user.users.size() > 0) {
			User selfUser = User.find()
					.filter("encodedId", fitbitUser.encodedId).get();
			user.users.add(selfUser);

			dashboardResponse.friendsData = goalCompletionProcess
					.getGoalCompletionForFriends(monthlyInterval, user);
		}

		return dashboardResponse;
	}
}
