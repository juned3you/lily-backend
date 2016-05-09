package com.lily.authorize.fitbit.loader;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.mongodb.morphia.query.Query;

import com.lily.exception.LoaderException;
import com.lily.loader.Loader;
import com.lily.mongo.models.HeartActivities;

/**
 * Load model into Mongodb.
 * 
 * @author Mohammad
 *
 */
public class HeartRateLoader implements Loader {

	@SuppressWarnings("unchecked")
	@Override
	public void load(Object obj) throws LoaderException {
		List<HeartActivities> heartActivitiesList = (List<HeartActivities>) obj;
		try {
			deleteRecords(heartActivitiesList);
			HeartActivities.insertAll(heartActivitiesList);
		} catch (Throwable e) {
			deleteRecords(heartActivitiesList);
			throw new LoaderException(e);
		}
	}

	/**
	 * Delete records.
	 */
	private void deleteRecords(List<HeartActivities> heartActivitiesList) {

		HeartActivities heartActivities = null;
		if (heartActivitiesList != null && heartActivitiesList.size() > 0) {
			heartActivities = heartActivitiesList.get(0);

			List<Date> datesList = heartActivitiesList.stream()
					.map(slp -> slp.dateTime).collect(Collectors.toList());

			Query<HeartActivities> oldActivityTimeSeries = HeartActivities.find()
					.filter("dateTime in ", datesList)
					.filter("userId", heartActivities.userId);

			HeartActivities.deleteAll(oldActivityTimeSeries);
		}
	}
}