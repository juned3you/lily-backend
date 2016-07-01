package com.lily.services;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.lily.http.DashboardResponse;
import com.lily.models.FitbitUser;
import com.lily.mongo.models.Friend;
import com.lily.mongo.models.User;
import com.lily.mongo.models.WeeklyGoalCompletion;
import com.lily.process.GoalCompletionProcess;
import com.lily.utils.DateUtils;
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

		// weekly Goal Completion
		dashboardResponse.weeklyGoalCompletionPercentage = getWeeklyGoalCompletion(fitbitUser);

		return dashboardResponse;
	}

	private float getWeeklyGoalCompletion(final FitbitUser fitbitUser)
			throws Throwable {
		Calendar cal = Calendar.getInstance();

		Date[] currentDateRange = DateUtils.getDateRange(
				DurationInterval.WEEKLY, cal.getTime());

		WeeklyGoalCompletion currentWeekGoalCompletion = goalCompletionProcess
				.getWeeklyGoalCompletion(fitbitUser, DurationInterval.WEEKLY,
						currentDateRange);

		cal.add(Calendar.DATE, cal.get(Calendar.DATE) - 7);
		Date[] dateRange = DateUtils.getDateRange(DurationInterval.WEEKLY,
				cal.getTime());

		WeeklyGoalCompletion previousWeekGoalCompletion = goalCompletionProcess
				.getWeeklyGoalCompletion(fitbitUser, DurationInterval.WEEKLY,
						dateRange);

		float diff = currentWeekGoalCompletion.weeklyGoalCompletionPoints
				- previousWeekGoalCompletion.weeklyGoalCompletionPoints;

		diff = (diff * 100)
				/ previousWeekGoalCompletion.weeklyGoalCompletionPoints;

		if (diff < 0
				|| previousWeekGoalCompletion.weeklyGoalCompletionPoints <= 0)
			return 0.0f;

		return diff;
	}
}
