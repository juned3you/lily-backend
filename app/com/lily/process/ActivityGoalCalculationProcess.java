package com.lily.process;

import java.util.Date;
import java.util.List;

import javax.inject.Singleton;

import com.lily.models.GoalConfiguration;
import com.lily.mongo.models.SleepLog;
import com.lily.utils.DateUtils;
import com.lily.utils.LilyConstants;
import com.lily.utils.LilyConstants.DurationInterval;
import com.typesafe.config.ConfigFactory;

@Singleton
public class ActivityGoalCalculationProcess {

	/**
	 * Calculate Monthly Activity Goal.
	 */
	public Float calculate(String userId, final DurationInterval interval)
			throws Throwable {
		return 0f;
	}

	/**
	 * Get activity monthly goal.
	 * 
	 * @return
	 */
	public Integer getMonthlyActivityGoal() {
		return ConfigFactory.load().getInt("activity.goal");
	}

	/**
	 * Get calculated activity log.
	 * 
	 * @param userId
	 * @param interval
	 * @param monthlyActivityGoal
	 * @return
	 * @throws Throwable
	 */
	private Float getActivityTotalPoints(String userId,
			final DurationInterval interval, Integer monthlySleepGoal)
			throws Throwable {
		Float results = 0f;

		// Sleep config.
		List<GoalConfiguration> goalConfigList = GoalConfiguration
				.getGoalConfiguration(LilyConstants.GoalConfiguration.SLEEP);
		Date[] dateRange = DateUtils.getDateRange(interval);

		List<SleepLog> sleepLogs = SleepLog.find().filter("userId", userId)
				.filter("date >=", dateRange[0])
				.filter("date <=", dateRange[1]).asList();

		// Calculating results for sleep daily basis.
		for (SleepLog sl : sleepLogs) {
			if (sl.summary == null)
				continue;

			Integer sleepValue = sl.summary.totalTimeInBed;
			Float percentageValue = GoalConfiguration.getRelatedPercentage(
					goalConfigList, sleepValue);

			results = results
					+ ((monthlySleepGoal * (percentageValue / 100)) / LilyConstants.ConstantClass
							.getDays(interval));
		}
		return results;
	}
}