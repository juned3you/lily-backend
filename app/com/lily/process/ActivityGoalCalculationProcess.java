package com.lily.process;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Singleton;

import com.google.common.collect.Lists;
import com.lily.http.Value;
import com.lily.models.FitbitUser;
import com.lily.models.GoalConfiguration;
import com.lily.mongo.models.CustomHeartRateZone;
import com.lily.mongo.models.DailyActivity;
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
			final DurationInterval interval, Date currentDate) throws Throwable {
		try {
			Integer monthlyGoal = getMonthlyActivityGoal();
			if (interval != DurationInterval.MONTHLY)
				monthlyGoal = monthlyGoal
						/ LilyConstants.ConstantClass.getDays(interval);

			Float points = getActivityTotalPoints(fitbitUser, interval,
					monthlyGoal, currentDate);
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
	 * user.active.bpm
	 * 
	 * @return
	 */
	public Double getUserActiveBPMPercentage() {
		return ConfigFactory.load().getDouble("user.active.bpm");
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
	private Float getActivityTotalPoints(FitbitUser fitbitUser,
			final DurationInterval interval, Integer monthlyActivityGoal,
			Date currentDate) throws Throwable {

		String userId = fitbitUser.encodedId;
		int userAge = getAge(fitbitUser);

		// BPM config.
		List<GoalConfiguration> bpmList = GoalConfiguration
				.getGoalConfiguration(LilyConstants.GoalConfiguration.BPM);

		// BPM Threashold
		Double bpmThreshold = (GoalConfiguration.getRelatedPercentage(bpmList,
				userAge) * getUserActiveBPMPercentage()) / 100;

		Float results = 0f;

		List<GoalConfiguration> goalConfigList = GoalConfiguration
				.getGoalConfiguration(LilyConstants.GoalConfiguration.ACTIVE_MINUTES);

		Date[] dateRange = DateUtils.getDateRange(interval, currentDate);

		List<HeartActivities> heartActivities = HeartActivities.find()
				.filter("userId", userId).filter("dateTime >=", dateRange[0])
				.filter("dateTime <=", dateRange[1]).asList();

		int days = LilyConstants.ConstantClass.getDays(DurationInterval.WEEKLY);

		List<List<HeartActivities>> partList = Lists.partition(heartActivities,
				days);

		for (List<HeartActivities> heartActList : partList) {
			int totalMinutes = 0;

			for (HeartActivities ha : heartActList) {
				if (ha.value == null || ha.value.heartRateZones == null
						|| ha.value.heartRateZones.size() == 0)
					continue;

				for (CustomHeartRateZone cus : ha.value.heartRateZones)
					totalMinutes += (cus.minutes == null ? 0 : cus.minutes);
			}

			Float percentageValue = GoalConfiguration.getRelatedPercentage(
					goalConfigList, totalMinutes);

			Float innerResults = ((monthlyActivityGoal * (percentageValue / 100)) / days);
			if (innerResults > bpmThreshold)
				results = results + innerResults;
		}
		return results;
	}

	private int getAge(FitbitUser fitbitUser) throws ParseException {
		Date dob = DateUtils.formatDate(fitbitUser.dateOfBirth);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dob);

		Month month = Month.of(cal.get(Calendar.MONTH) + 1);

		LocalDate today = LocalDate.now();
		LocalDate birthday = LocalDate.of(cal.get(Calendar.YEAR), month,
				cal.get(Calendar.DATE));

		Period p = Period.between(birthday, today);
		return p.getYears();
	}

	/**
	 * Activities in Last 7 days
	 * @param fitbitUser
	 * @param dateRange
	 * @return
	 * @throws Throwable
	 */
	public Value getActivityTotalPointsForLast7Days(FitbitUser fitbitUser,
			Date[] dateRange) throws Throwable {
		Value results = new Value();
		results.interval = "mins";

		int days = 7;
		Integer monthlyActivityGoal = getMonthlyActivityGoal() ;
		String userId = fitbitUser.encodedId;
		int userAge = getAge(fitbitUser);

		// BPM config.
		List<GoalConfiguration> bpmList = GoalConfiguration
				.getGoalConfiguration(LilyConstants.GoalConfiguration.BPM);

		// BPM Threashold
		Double bpmThreshold = (GoalConfiguration.getRelatedPercentage(bpmList,
				userAge) * getUserActiveBPMPercentage()) / 100;

		List<GoalConfiguration> goalConfigList = GoalConfiguration
				.getGoalConfiguration(LilyConstants.GoalConfiguration.ACTIVE_MINUTES);

		List<HeartActivities> heartActivities = HeartActivities.find()
				.filter("userId", userId).filter("dateTime >=", dateRange[0])
				.filter("dateTime <=", dateRange[1]).asList();

		List<List<HeartActivities>> partList = Lists.partition(heartActivities,
				days);

		Integer totalMins = 0;
		for (List<HeartActivities> heartActList : partList) {
			int totalMinutes = 0;

			for (HeartActivities ha : heartActList) {
				if (ha.value == null || ha.value.heartRateZones == null
						|| ha.value.heartRateZones.size() == 0)
					continue;

				for (CustomHeartRateZone cus : ha.value.heartRateZones)
					totalMinutes += (cus.minutes == null ? 0 : cus.minutes);
			}

			totalMins += totalMinutes;
			Float percentageValue = GoalConfiguration.getRelatedPercentage(
					goalConfigList, totalMinutes);

			Float innerResults = ((monthlyActivityGoal * (percentageValue / 100)) / days);
			if (innerResults > bpmThreshold)
				results.pts += innerResults.intValue();
		}

		results.data = totalMins;
		int totalMinsInWeek = (days * 24 * 60) - (days * 8 * 60); // less sleep
																	// 8 hours
		if (totalMins > 0)
			results.progressValue = new Integer((results.data * 100)
					/ totalMinsInWeek).floatValue();
		return results;
	}

	/**
	 * Active minutes in Last 7 days
	 * @param userId
	 * @param dateRange
	 * @return
	 * @throws Throwable
	 */
	public Value getActiveMinutesTotalPointsLast7Days(String userId,
			Date[] dateRange) throws Throwable {
		Value results = new Value();
		results.interval = "mins";
		int days = 7;

		Integer monthlyStepGoal = getMonthlyActivityGoal();

		// Active Minutes.
		List<GoalConfiguration> goalConfigList = GoalConfiguration
				.getGoalConfiguration(LilyConstants.GoalConfiguration.ACTIVE_MINUTES);

		List<DailyActivity> dailyActivities = DailyActivity.find()
				.filter("userId", userId).filter("date >=", dateRange[0])
				.filter("date <=", dateRange[1]).asList();

		// Calculating results
		for (DailyActivity da : dailyActivities) {

			if (da.summary == null)
				continue;

			Integer activeMinutes = da.summary.fairlyActiveMinutes
					+ da.summary.lightlyActiveMinutes
					+ da.summary.veryActiveMinutes;

			Float percentageValue = GoalConfiguration.getRelatedPercentage(
					goalConfigList, activeMinutes);

			results.pts = results.pts
					+ new Float(
							((monthlyStepGoal * (percentageValue / 100)) / days))
							.intValue();

			results.data += activeMinutes;
		}
		
		int totalMinsInWeek = (days * 24 * 60) - (days * 8 * 60); // less sleep
		int	res = (results.data * 100) / totalMinsInWeek;
		results.progressValue = new Integer(res).floatValue();

		return results;
	}
}