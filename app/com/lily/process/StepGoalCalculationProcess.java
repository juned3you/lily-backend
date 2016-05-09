package com.lily.process;

import java.util.Date;
import java.util.List;

import javax.inject.Singleton;

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
	public Float calculate(FitbitUser fitbitUser, final DurationInterval interval)
			throws Throwable {
		try {
			Integer monthlyStepGoal = getMonthlyStepGoal();
			Float totalStepPoints = getStepTotalPoints(fitbitUser.encodedId, interval,
					monthlyStepGoal);
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
			final DurationInterval interval, Integer monthlyStepGoal)
			throws Throwable {
		Float results = 0f;

		// Sleep config.
		List<GoalConfiguration> goalConfigList = GoalConfiguration
				.getGoalConfiguration(LilyConstants.GoalConfiguration.STEPS);
		Date[] dateRange = DateUtils.getDateRange(interval);

		List<DailyActivity> dailyActivities = DailyActivity.find().filter("userId", userId)
				.filter("date >=", dateRange[0])
				.filter("date <=", dateRange[1]).asList();

		// Calculating results for sleep daily basis.
		for (DailyActivity da : dailyActivities) {
			if (da.activities == null || da.activities.size() == 0)
				continue;

			Integer sleepDayValue = da.activities.stream().mapToInt(i -> i.steps).sum();
			Float percentageValue = GoalConfiguration.getRelatedPercentage(
					goalConfigList, sleepDayValue);
			
			results = results
					+ ((monthlyStepGoal * (percentageValue / 100)) / LilyConstants.ConstantClass
							.getDays(interval));			
		}
		return results;
	}
}