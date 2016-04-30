package com.lily.process;

import java.util.Date;
import java.util.List;

import com.lily.mongo.models.DailyActivity;
import com.lily.utils.DateUtils;
import com.lily.utils.LilyConstants.DurationInterval;

/**
 * Goal Completion based on interval
 * 
 * @author Mohammad
 *
 */
public class GoalCompletionProcess {

	/**
	 * Get Goal completion based on interval.
	 * 
	 * @param interval
	 */
	public final void getGoalCompletion(String userId,
			final DurationInterval interval) {
		Date[] dateRange = DateUtils.getDateRange(interval);
		List<DailyActivity> dailyActivities = DailyActivity.find()
				.filter("userId", userId).filter("date >=", dateRange[0])
				.filter("date <", dateRange[1]).asList();

		for (DailyActivity da : dailyActivities) {
			System.out.println("Date: " + da.date);
		}

	}
}
