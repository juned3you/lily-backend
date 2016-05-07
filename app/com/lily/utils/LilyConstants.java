package com.lily.utils;

import com.typesafe.config.ConfigFactory;

/**
 * Project constants
 * 
 * @author Mohammad
 *
 */
public interface LilyConstants {

	String AUTHORIZATION_CODE = "AUTHORIZATION_CODE";
	String EMAIL_SUFFIX = "@lily.com";
	String DEFAULT_PASSWORD = "password";
	String SESSION_KEY_PROFILE = "profile";
	String USER_TYPE = "userType";

	/**
	 * Fitbit constants
	 */
	public interface Fitbit {
		public String CLIENT_NAME = ConfigFactory.load().getString(
				"fitbit.client.name");

		public String USER_PROFILE_URI = "fitbit.api.user.profile.uri";
		public String DAILY_ACTIVITIES_URI = "fitbit.api.user.daily.activities.uri";
		public String SLEEP_URI = "fitbit.api.user.sleep.uri";
		public String SLEEP_GOAL_URI = "fitbit.api.user.sleep.goal.uri";
	}

	public enum DurationInterval {
		YEARLY, MONTHLY, WEEKLY, DAILY;
	}

	/**
	 * Static methods
	 * @author Mohammad
	 *
	 */
	public static class ConstantClass {
		public static Integer getDays(DurationInterval interval) {
			switch (interval) {
			case YEARLY:
				return 365;
			case MONTHLY:
				return 30;
			case WEEKLY:
				return 6;
			case DAILY:
				return 1;
			}
			return 30;
		}
	};

	public interface GoalConfiguration {
		public String MONTHLY_GROWTH = "MonthlyGrowth";
		public String SLEEP = "Sleep";
		public String STEPS = "Steps";
		public String BPM = "BPM";
		public String ACTIVE_MINUTES = "ActiveMinutes";
	}
}