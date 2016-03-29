package com.lily.authorize.fitbit.loader;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import com.lily.exception.LoaderException;
import com.lily.loader.Loader;
import com.lily.mongo.models.Activity;
import com.lily.mongo.models.ActivitySummary;
import com.lily.mongo.models.DailyActivity;

/**
 * Load model into Mongodb.
 * 
 * @author Mohammad
 *
 */
public class DailyActivitiesLoader implements Loader {

	@Override
	public void load(Object obj) throws LoaderException {
		DailyActivity dailyActivities = (DailyActivity) obj;
		try {
			deleteRecords(dailyActivities);

			DailyActivity.insertAll(dailyActivities.activities);

			dailyActivities.summary.insert();

			dailyActivities.insert();
		} catch (Throwable e) {
			deleteRecords(dailyActivities);
			throw new LoaderException(e);
		}
	}

	/**
	 * Delete records.
	 */
	private void deleteRecords(DailyActivity dailyActivities) {

		DailyActivity oldDailyActivities = DailyActivity.find()
				.filter("date", dailyActivities.date)
				.filter("userId", dailyActivities.userId).get();

		if (oldDailyActivities != null) {
			if (oldDailyActivities != null
					&& oldDailyActivities.activities.size() > 0) {
				List<ObjectId> ids = oldDailyActivities.activities.stream()
						.map(activity -> activity.id)
						.collect(Collectors.toList());

				Query<Activity> sleepQuery = Activity.q(Activity.class)
						.field("id").in(ids);
				Activity.deleteAll(sleepQuery);
			}

			if (oldDailyActivities.summary != null
					&& oldDailyActivities.summary.id != null) {
				ActivitySummary actSummary = ActivitySummary
						.q(ActivitySummary.class)
						.filter("id", oldDailyActivities.summary.id).get();
				actSummary.delete();
			}

			oldDailyActivities.delete();
		}
	}
}