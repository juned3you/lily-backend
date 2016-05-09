package com.lily.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Singleton;

import com.google.common.collect.Lists;
import com.lily.models.FitbitUser;
import com.lily.models.GoalConfiguration;
import com.lily.mongo.models.CustomHeartRateZone;
import com.lily.mongo.models.HeartActivities;
import com.lily.utils.DateUtils;
import com.lily.utils.LilyConstants;
import com.lily.utils.LilyConstants.DurationInterval;
import com.typesafe.config.ConfigFactory;

@Singleton
public class ActivityGoalCalculationProcess {

	/**
	 * Calculate Monthly Activity Goal.
	 */
	public Float calculate(FitbitUser fitbitUser,
			final DurationInterval interval) throws Throwable {
		try {
			Integer monthlyGoal = getMonthlyActivityGoal();
			Float points = getActivityTotalPoints(fitbitUser.encodedId,
					interval, monthlyGoal);
			return points;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
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
			final DurationInterval interval, Integer monthlyActivityGoal)
			throws Throwable {
		Float results = 0f;

		// Sleep config.
		List<GoalConfiguration> bpmList = GoalConfiguration
				.getGoalConfiguration(LilyConstants.GoalConfiguration.BPM);

		List<GoalConfiguration> goalConfigList = GoalConfiguration
				.getGoalConfiguration(LilyConstants.GoalConfiguration.ACTIVE_MINUTES);

		Date[] dateRange = DateUtils.getDateRange(interval);

		List<HeartActivities> heartActivities = HeartActivities.find()
				.filter("userId", userId).filter("dateTime >=", dateRange[0])
				.filter("dateTime <=", dateRange[1]).asList();

		int days = LilyConstants.ConstantClass.getDays(DurationInterval.WEEKLY);

		List<List<HeartActivities>> partList = Lists.partition(heartActivities,
				days);	
		
		for (List<HeartActivities> heartActList : partList) {
			int innerResult = 0;
			
			for (HeartActivities ha : heartActList) {
				if (ha.value == null || ha.value.heartRateZones == null
						|| ha.value.heartRateZones.size() == 0)
					continue;

				for (CustomHeartRateZone cus : ha.value.heartRateZones)
					innerResult += (cus.minutes == null ? 0 : cus.minutes);
			}

			Float percentageValue = GoalConfiguration.getRelatedPercentage(
					goalConfigList, innerResult);

			results = results
					+ ((monthlyActivityGoal * (percentageValue / 100)) / days);
		}
		return results;
	}
}