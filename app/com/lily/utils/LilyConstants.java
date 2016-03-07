package com.lily.utils;

import com.typesafe.config.ConfigFactory;

/**
 * Project constants
 * @author Mohammad
 *
 */
public interface LilyConstants {

	String AUTHORIZATION_CODE = "AUTHORIZATION_CODE";

	/**
	 * Fitbit constants	 
	 */
	public interface Fitbit {
		public String CLIENT_NAME = ConfigFactory.load().getString(
				"fitbit.client.name");
		
		public String USER_PROFILE_URI = "fitbit.api.user.profile.uri";
		public String DAILY_ACTIVITIES_URI = "fitbit.api.user.daily.activities.uri";
	}
}