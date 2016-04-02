package com.lily.authorize.fitbit.loader;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.mongodb.morphia.query.Query;

import com.lily.exception.LoaderException;
import com.lily.loader.Loader;
import com.lily.mongo.models.WeightLog;

/**
 * Load model into Mongodb.
 * 
 * @author Mohammad
 *
 */
public class WeightLogLoader implements Loader {

	@SuppressWarnings("unchecked")
	@Override
	public void load(Object obj) throws LoaderException {
		List<WeightLog> weightLogList = (List<WeightLog>) obj;
		try {
			deleteRecords(weightLogList);
			WeightLog.insertAll(weightLogList);
		} catch (Throwable e) {
			deleteRecords(weightLogList);
			throw new LoaderException(e);
		}
	}

	/**
	 * Delete records.
	 */
	private void deleteRecords(List<WeightLog> weightLogList) {

		WeightLog weightLog = null;
		if (weightLogList != null && weightLogList.size() > 0) {
			weightLog = weightLogList.get(0);

			List<Date> datesList = weightLogList.stream().map(slp -> slp.date)
					.collect(Collectors.toList());

			List<Time> timeList = weightLogList.stream().map(slp -> slp.time)
					.collect(Collectors.toList());

			Query<WeightLog> oldActivityTimeSeries = WeightLog.find()
					.filter("date in ", datesList).filter("time in ", timeList)
					.filter("userId", weightLog.userId);

			WeightLog.deleteAll(oldActivityTimeSeries);
		}
	}
}