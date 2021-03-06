package com.lily.process;

import java.util.Date;
import java.util.List;

import javax.inject.Singleton;

import com.lily.http.Value;
import com.lily.models.FitbitUser;
import com.lily.models.GoalConfiguration;
import com.lily.mongo.models.SleepLog;
import com.lily.utils.DateUtils;
import com.lily.utils.LilyConstants;
import com.lily.utils.LilyConstants.DurationInterval;
import com.typesafe.config.ConfigFactory;

@Singleton
public class SleepGoalCalculationProcess {

	/**
	 * Calculate Monthly Sleep Goal.
	 */
	public Float calculate(FitbitUser fitbitUser,
			final DurationInterval interval, Date currentDate) throws Throwable {
		try {
			Integer monthlySleepGoal = getMonthlySleepGoal();
			if (interval != DurationInterval.MONTHLY)
				monthlySleepGoal = monthlySleepGoal
						/ LilyConstants.ConstantClass.getDays(interval);

			Float totalSleepPoints = getSleepTotalPoints(fitbitUser.encodedId,
					interval, monthlySleepGoal, currentDate);
			return totalSleepPoints;
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	/**
	 * Get Sleep monthly goal.
	 * 
	 * @return
	 */
	public Integer getMonthlySleepGoal() {
		return ConfigFactory.load().getInt("sleep.goal");
	}

	/**
	 * Get calculated sleep log.
	 * 
	 * @param userId
	 * @param interval
	 * @param monthlySleepGoal
	 * @return
	 * @throws Throwable
	 */
	private Float getSleepTotalPoints(String userId,
			final DurationInterval interval, Integer monthlySleepGoal,
			Date currentDate) throws Throwable {
		Float results = 0f;

		// Sleep config.
		List<GoalConfiguration> goalConfigList = GoalConfiguration
				.getGoalConfiguration(LilyConstants.GoalConfiguration.SLEEP);
		Date[] dateRange = DateUtils.getDateRange(interval, currentDate);

		List<SleepLog> sleepLogs = SleepLog.find().filter("userId", userId)
				.filter("date >=", dateRange[0])
				.filter("date <=", dateRange[1]).asList();

		int days = LilyConstants.ConstantClass.getDays(interval);
		// Calculating results for sleep daily basis.
		for (SleepLog sl : sleepLogs) {
			if (sl.summary == null)
				continue;

			Integer sleepValue = convertToHour(sl.summary.totalTimeInBed);
			Float percentageValue = GoalConfiguration.getRelatedPercentage(
					goalConfigList, sleepValue);

			results = results
					+ ((monthlySleepGoal * (percentageValue / 100)) / days);
		}
		return results;
	}

	/**
	 * Get sleep for progress bar.
	 * 
	 * @param userId
	 * @param dateRange
	 * @return
	 * @throws Throwable
	 */
	public Value getSleepTotalPointsForLast7Days(String userId, Date[] dateRange)
			throws Throwable {
		Value results = new Value();
		results.interval = "h";
		int days = 7;
		Integer monthlySleepGoal = getMonthlySleepGoal();

		// Sleep config.
		List<GoalConfiguration> goalConfigList = GoalConfiguration
				.getGoalConfiguration(LilyConstants.GoalConfiguration.SLEEP);

		List<SleepLog> sleepLogs = SleepLog.find().filter("userId", userId)
				.filter("date >=", dateRange[0])
				.filter("date <=", dateRange[1]).asList();

		// Calculating results for sleep daily basis.
		for (SleepLog sl : sleepLogs) {
			if (sl.summary == null)
				continue;

			Integer sleepValue = convertToHour(sl.summary.totalTimeInBed);
			Float percentageValue = GoalConfiguration.getRelatedPercentage(
					goalConfigList, sleepValue);

			results.pts = results.pts
					+ new Float(
							((monthlySleepGoal * (percentageValue / 100)) / days))
							.intValue();

			results.data += sleepValue;
		}

		if (results.data > 0) {
			results.data = results.data / 60;
			results.progressValue = new Integer((results.data * 100)
					/ (days * 8)).floatValue();
		}

		return results;
	}

	private Integer convertToHour(Integer mins) {
		return mins / 60;
	}
}