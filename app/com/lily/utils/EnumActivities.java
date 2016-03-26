package com.lily.utils;

public enum EnumActivities {
	CALORIES("activities/calories"), CALORIES_BMR("activities/caloriesBMR"), STEPS(
			"activities/steps"), DISTANCE("activities/distance"), FLOORS(
			"activities/floors"), ELEVATION("activities/elevation"), MINUTES_SEDENTARY(
			"activities/minutesSedentary "), MINUTES_LIGHTLY_ACTIVE(
			"activities/minutesLightlyActive"), MINUTES_FAILY_ACTIVE(
			"activities/minutesFairlyActive"), MINUTES_VERY_ACTIVE(
			"activities/minutesVeryActive "), ACTIVITY_CALORIES(
			"activities/activityCalories");

	String uri;

	EnumActivities(String uri) {
		this.uri = uri;
	}
}