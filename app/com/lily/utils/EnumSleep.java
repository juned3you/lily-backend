package com.lily.utils;

public enum EnumSleep {

	STARTTIME("sleep/startTime"), TIME_IN_BED("sleep/timeInBed"), MINUTES_AS_SLEEP(
			"sleep/minutesAsleep"), AWAKENINGS_COUNT("sleep/awakeningsCount"), MINUTES_AWAKE(
			"sleep/minutesAwake"), MINUTES_TO_FALL_AS_SLEEP(
			"sleep/minutesToFallAsleep"), MINUTES_AFTER_WAKE_UP(
			"sleep/minutesAfterWakeup"), EFFICIENCY("sleep/efficiency");

	String uri;

	EnumSleep(String uri) {
		this.uri = uri;
	}
	
	public String getUri() {
        return this.uri;
    }
}
