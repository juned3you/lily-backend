package com.lily.authorize.fitbit.loader;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import com.lily.exception.LoaderException;
import com.lily.loader.Loader;
import com.lily.mongo.models.Sleep;
import com.lily.mongo.models.SleepLog;
import com.lily.mongo.models.SleepTimeSeries;
import com.lily.mongo.utils.Model;

/**
 * Load model into Mongodb.
 * 
 * @author Mohammad
 *
 */
public class SleepTimeSeriesLoader implements Loader {

	@SuppressWarnings("unchecked")
	@Override
	public void load(Object obj) throws LoaderException {
		List<SleepTimeSeries> sleepTimeSeriesList = (List<SleepTimeSeries>) obj;
		try {
			deleteRecords(sleepTimeSeriesList);
			SleepTimeSeries.insertAll(sleepTimeSeriesList);
		} catch (Throwable e) {
			deleteRecords(sleepTimeSeriesList);
			throw new LoaderException(e);
		}
	}

	/**
	 * Delete records.
	 */
	private void deleteRecords(List<SleepTimeSeries> sleepTimeSeriesList) {

		SleepTimeSeries sleepTimeSeries = null;
		if (sleepTimeSeriesList != null && sleepTimeSeriesList.size() > 0) {
			sleepTimeSeries = sleepTimeSeriesList.get(0);

			List<Date> datesList = sleepTimeSeriesList.stream().map(slp -> slp.dateTime).collect(Collectors.toList());
			
			Query<SleepTimeSeries> oldsleepTimeSeries = SleepTimeSeries.find()
					.filter("dateTime in ", datesList)
					.filter("userId", sleepTimeSeries.userId)
					.filter("resourcePath", sleepTimeSeries.resourcePath);

			SleepTimeSeries.deleteAll(oldsleepTimeSeries);
		}
	}
}