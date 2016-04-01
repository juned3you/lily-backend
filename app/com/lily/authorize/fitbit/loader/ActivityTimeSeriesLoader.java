package com.lily.authorize.fitbit.loader;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.mongodb.morphia.query.Query;

import com.lily.exception.LoaderException;
import com.lily.loader.Loader;
import com.lily.mongo.models.ActivityTimeSeries;

/**
 * Load model into Mongodb.
 * 
 * @author Mohammad
 *
 */
public class ActivityTimeSeriesLoader implements Loader {

	@SuppressWarnings("unchecked")
	@Override
	public void load(Object obj) throws LoaderException {
		List<ActivityTimeSeries> activityTimeSeriesList = (List<ActivityTimeSeries>) obj;
		try {
			deleteRecords(activityTimeSeriesList);
			ActivityTimeSeries.insertAll(activityTimeSeriesList);
		} catch (Throwable e) {
			deleteRecords(activityTimeSeriesList);
			throw new LoaderException(e);
		}
	}

	/**
	 * Delete records.
	 */
	private void deleteRecords(List<ActivityTimeSeries> activityTimeSeriesList) {

		ActivityTimeSeries activityTimeSeries = null;
		if (activityTimeSeriesList != null && activityTimeSeriesList.size() > 0) {
			activityTimeSeries = activityTimeSeriesList.get(0);

			List<Date> datesList = activityTimeSeriesList.stream()
					.map(slp -> slp.dateTime).collect(Collectors.toList());

			Query<ActivityTimeSeries> oldActivityTimeSeries = ActivityTimeSeries
					.find().filter("dateTime in ", datesList)
					.filter("userId", activityTimeSeries.userId)
					.filter("resourcePath", activityTimeSeries.resourcePath);

			ActivityTimeSeries.deleteAll(oldActivityTimeSeries);
		}
	}
}