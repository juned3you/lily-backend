package com.lily.authorize.fitbit.loader;

import java.util.List;

import com.lily.exception.LoaderException;
import com.lily.loader.Loader;
import com.lily.mongo.models.SleepLog;
import com.lily.mongo.models.SleepTimeSeries;

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
		try {
			List<SleepTimeSeries> sleepTimeSeriesList = (List<SleepTimeSeries>) obj;

			SleepTimeSeries.insertAll(sleepTimeSeriesList);
		} catch (Throwable e) {
			throw new LoaderException(e);
		}
	}

	/**
	 * Delete records.
	 * 
	 * @param date
	 */
	private void deleteRecords(SleepLog sleepLog) {
		
	}
}