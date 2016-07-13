package com.lily.services;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.lily.http.ChartResponse;
import com.lily.http.DashboardResponse;
import com.lily.http.Series;
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
		dashboardResponse.chartData = getChartData(fitbitUser);

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

		cal.add(Calendar.DATE, -7);
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

	private ChartResponse getChartData(final FitbitUser fitbitUser) {
		final ChartResponse chartResponse = new ChartResponse();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -1);
		Date endDate = cal.getTime();

		cal.add(Calendar.DATE, -30);
		Date startDate = cal.getTime();

		GregorianCalendar gcal = new GregorianCalendar();
		gcal.setTime(startDate);

		Series sleepSeries = new Series();
		sleepSeries.name = "Sleep";
		chartResponse.legends.add(sleepSeries.name);

		Series sportsSeries = new Series();
		sportsSeries.name = "Sport";
		chartResponse.legends.add(sportsSeries.name);

		Random random = new Random();
		while (!gcal.getTime().after(endDate)) {
			chartResponse.xAxisDataLabel.add("" + gcal.getTime().getDate());
			int sleepHour = random.nextInt(24);
			sleepSeries.data.add(sleepHour);
			sportsSeries.data.add(24 - sleepHour);
			gcal.add(Calendar.DATE, 1);
		}

		chartResponse.seriesData.add(sleepSeries);
		chartResponse.seriesData.add(sportsSeries);
		return chartResponse;
	}
}
