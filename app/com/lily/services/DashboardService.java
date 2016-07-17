package com.lily.services;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.lily.http.ChartResponse;
import com.lily.http.DashboardResponse;
import com.lily.http.ProgressBarResponse;
import com.lily.http.Series;
import com.lily.models.FitbitUser;
import com.lily.mongo.models.DailyActivity;
import com.lily.mongo.models.Friend;
import com.lily.mongo.models.SleepLog;
import com.lily.mongo.models.User;
import com.lily.mongo.models.WeeklyGoalCompletion;
import com.lily.process.ActivityGoalCalculationProcess;
import com.lily.process.GoalCompletionProcess;
import com.lily.process.SleepGoalCalculationProcess;
import com.lily.process.StepGoalCalculationProcess;
import com.lily.utils.DateUtils;
import com.lily.utils.LilyConstants.DurationInterval;

@Singleton
public class DashboardService {

	@Inject
	private GoalCompletionProcess goalCompletionProcess;

	@Inject
	private SleepGoalCalculationProcess sleepProcess;

	@Inject
	private ActivityGoalCalculationProcess activityGoalCalculationProcess;

	@Inject
	private StepGoalCalculationProcess stepGoalCalculationProcess;

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
		dashboardResponse.progressBarData = getProgressbarData(fitbitUser);

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

	private ChartResponse getChartData(final FitbitUser fitbitUser)
			throws ParseException {
		final ChartResponse chartResponse = new ChartResponse();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -1);
		Date endDate = DateUtils.setEndTime(cal.getTime());

		cal.add(Calendar.DATE, -30);
		Date startDate = DateUtils.setStartTime(cal.getTime());

		GregorianCalendar gcal = new GregorianCalendar();
		gcal.setTime(startDate);

		Series sleepSeries = new Series();
		sleepSeries.name = "Sleep";
		chartResponse.legends.add(sleepSeries.name);

		Series sportsSeries = new Series();
		sportsSeries.name = "Sport";
		chartResponse.legends.add(sportsSeries.name);

		List<SleepLog> sleepLogs = SleepLog.find()
				.filter("userId", fitbitUser.encodedId)
				.filter("date >=", startDate).filter("date <=", endDate)
				.asList();

		List<DailyActivity> dailyActivities = DailyActivity.find()
				.filter("userId", fitbitUser.encodedId)
				.filter("date >=", startDate).filter("date <=", endDate)
				.asList();

		while (!gcal.getTime().after(endDate)) {
			Date filterDate = gcal.getTime();
			chartResponse.xAxisDataLabel.add("" + filterDate.getDate());
			SleepLog sleepLog = filterSleepLog(sleepLogs, filterDate);
			int sleepData = 0;
			if (sleepLog != null)
				sleepData = sleepLog.summary.totalTimeInBed;

			sleepSeries.data.add(sleepData);
			
			DailyActivity dailyAct = filterDailyActivity(dailyActivities, filterDate);
			int sportData = 0;
			if (dailyAct != null)
				sportData = dailyAct.summary.steps;
			
			sportsSeries.data.add(sportData);
			gcal.add(Calendar.DATE, 1);
		}

		chartResponse.seriesData.add(sleepSeries);
		chartResponse.seriesData.add(sportsSeries);
		return chartResponse;
	}

	/**
	 * Filter List by date
	 * @param sleepLogs
	 * @param filterDate
	 * @return
	 * @throws ParseException
	 */
	private SleepLog filterSleepLog(List<SleepLog> sleepLogs, Date filterDate)
			throws ParseException {
		String date = DateUtils.formatDate(filterDate);
		for (SleepLog sleepLog : sleepLogs) {
			String sleepDate = DateUtils.formatDate(sleepLog.date);
			if (date.equals(sleepDate))
				return sleepLog;
		}
		return null;
	}

	/**
	 * Filter List by date
	 * @param dailyActivities
	 * @param filterDate
	 * @return
	 * @throws ParseException
	 */
	private DailyActivity filterDailyActivity(
			List<DailyActivity> dailyActivities, Date filterDate)
			throws ParseException {
		String date = DateUtils.formatDate(filterDate);
		for (DailyActivity dailyAct : dailyActivities) {
			String actDate = DateUtils.formatDate(dailyAct.date);
			if (date.equals(actDate))
				return dailyAct;
		}
		return null;
	}

	/**
	 * Get progress bar data for last 7 days.
	 * 
	 * @param fitbitUser
	 * @return
	 * @throws Throwable
	 */
	private ProgressBarResponse getProgressbarData(final FitbitUser fitbitUser)
			throws Throwable {

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		Date endDate = cal.getTime();

		cal.add(Calendar.DATE, -7);
		Date startDate = cal.getTime();
		Date[] dateRange = new Date[] { startDate, endDate };

		final ProgressBarResponse progressBarResponse = new ProgressBarResponse();
		/*
		 * progressBarResponse.steps.progressValue = 56.20f;
		 * progressBarResponse.steps.pts = 120;
		 * progressBarResponse.steps.interval = "steps";
		 * progressBarResponse.steps.data = 5678;
		 */

		progressBarResponse.steps = stepGoalCalculationProcess
				.getStepTotalPointsLast7Days(fitbitUser.encodedId, dateRange);

		progressBarResponse.sleep = sleepProcess
				.getSleepTotalPointsForLast7Days(fitbitUser.encodedId,
						dateRange);
		/*
		 * progressBarResponse.sleep.progressValue = 25.0f;
		 * progressBarResponse.sleep.pts = 80;
		 * progressBarResponse.sleep.interval = "h";
		 * progressBarResponse.sleep.data = 7;
		 */

		progressBarResponse.activities = activityGoalCalculationProcess
				.getActivityTotalPointsForLast7Days(fitbitUser, dateRange);
		/*
		 * progressBarResponse.activities.progressValue = 30.0f;
		 * progressBarResponse.activities.pts = 180;
		 * progressBarResponse.activities.interval = "mins";
		 * progressBarResponse.activities.data = 15;
		 */

		progressBarResponse.activeTime = activityGoalCalculationProcess
				.getActiveMinutesTotalPointsLast7Days(fitbitUser.encodedId,
						dateRange);

		/*
		 * progressBarResponse.activeTime.progressValue = 70.0f;
		 * progressBarResponse.activeTime.pts = 35;
		 * progressBarResponse.activeTime.interval = "mins";
		 * progressBarResponse.activeTime.data = 20;
		 */

		return progressBarResponse;
	}
}
