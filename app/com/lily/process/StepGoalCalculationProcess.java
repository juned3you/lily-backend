package com.lily.process;

import java.util.Date;
import java.util.List;

import javax.inject.Singleton;

import com.lily.http.Value;
import com.lily.models.FitbitUser;
import com.lily.models.GoalConfiguration;
import com.lily.mongo.models.DailyActivity;
import com.lily.utils.DateUtils;
import com.lily.utils.LilyConstants;
import com.lily.utils.LilyConstants.DurationInterval;
import com.typesafe.config.ConfigFactory;

@Singleton
public class StepGoalCalculationProcess {

	/**
	 * Calculate Monthly Step Goal.
	 */
	public Float calculate(FitbitUser fitbitUser,
			final DurationInterval interval, Date currentDate) throws Throwable {
		try {
			Integer monthlyStepGoal = getMonthlyStepGoal();
			if (interval != DurationInterval.MONTHLY)
				monthlyStepGoal = monthlyStepGoal
						/ LilyConstants.ConstantClass.getDays(interval);

			Float totalStepPoints = getStepTotalPoints(fitbitUser.encodedId,
					interval, monthlyStepGoal, currentDate);
			return totalStepPoints;
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	/**
	 * Get Step monthly goal.
	 * 
	 * @return
	 */
	public Integer getMonthlyStepGoal() {
		return ConfigFactory.load().getInt("step.goal");
	}

	/**
	 * Get calculated sleep log.
	 * 
	 * @param userId
	 * @param interval
	 * @param monthlyStepGoal
	 * @return
	 * @throws Throwable
	 */
	private Float getStepTotalPoints(String userId,
			final DurationInterval interval, Integer monthlyStepGoal,
			Date currentDate) throws Throwable {
		Float results = 0f;

		// Sleep config.
		List<GoalConfiguration> goalConfigList = GoalConfiguration
				.getGoalConfiguration(LilyConstants.GoalConfiguration.STEPS);
		Date[] dateRange = DateUtils.getDateRange(interval, currentDate);

		List<DailyActivity> dailyActivities = DailyActivity.find()
				.filter("userId", userId).filter("date >=", dateRange[0])
				.filter("date <=", dateRange[1]).asList();

		int days = LilyConstants.ConstantClass.getDays(interval);
		// Calculating results for sleep daily basis.
		for (DailyActivity da : dailyActivities) {
			if (da.activities == null || da.activities.size() == 0)
				continue;

			Integer stepDayValue = da.activities.stream()
					.mapToInt(i -> i.steps).sum();
			Float percentageValue = GoalConfiguration.getRelatedPercentage(
					goalConfigList, stepDayValue);

			results = results
					+ ((monthlyStepGoal * (percentageValue / 100)) / days);
		}
		return results;
	}

	/**
	 * Calculate steps for last 7 days.
	 * 
	 * @param userId
	 * @param dateRange
	 * @return
	 * @throws Throwable
	 */
	public Value getStepTotalPointsLast7Days(String userId, Date[] dateRange)
			throws Throwable {
		Value results = new Value();
		results.interval = "steps";
		int days = 7;

		Integer monthlyStepGoal = getMonthlyStepGoal();

		// Sleep config.
		List<GoalConfiguration> goalConfigList = GoalConfiguration
				.getGoalConfiguration(LilyConstants.GoalConfiguration.STEPS);

		List<DailyActivity> dailyActivities = DailyActivity.find()
				.filter("userId", userId).filter("date >=", dateRange[0])
				.filter("date <=", dateRange[1]).asList();

		Integer goal = 0;
		// Calculating results for sleep daily basis.
		for (DailyActivity da : dailyActivities) {
			
			goal += da.goals.steps;
			
			if (da.activities == null || da.activities.size() == 0)
				continue;

			Integer stepDayValue = da.activities.stream()
					.mapToInt(i -> i.steps).sum();

			Float percentageValue = GoalConfiguration.getRelatedPercentage(
					goalConfigList, stepDayValue);

			results.pts = results.pts
					+ new Float(
							((monthlyStepGoal * (percentageValue / 100)) / days))
							.intValue();

			results.data += stepDayValue;			
		}
		int res = 0;

		if (goal > 0)
			res = ((results.data * 100) / goal);
		results.progressValue = new Integer(res).floatValue();

		return results;
	}
}