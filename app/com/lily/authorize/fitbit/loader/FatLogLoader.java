package com.lily.authorize.fitbit.loader;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.mongodb.morphia.query.Query;

import com.lily.exception.LoaderException;
import com.lily.loader.Loader;
import com.lily.mongo.models.FatLog;

/**
 * Load model into Mongodb.
 * 
 * @author Mohammad
 *
 */
public class FatLogLoader implements Loader {

	@SuppressWarnings("unchecked")
	@Override
	public void load(Object obj) throws LoaderException {
		List<FatLog> fatLogList = (List<FatLog>) obj;
		try {
			deleteRecords(fatLogList);
			FatLog.insertAll(fatLogList);
		} catch (Throwable e) {
			deleteRecords(fatLogList);
			throw new LoaderException(e);
		}
	}

	/**
	 * Delete records.
	 */
	private void deleteRecords(List<FatLog> fatLogList) {

		FatLog fatLog = null;
		if (fatLogList != null && fatLogList.size() > 0) {
			fatLog = fatLogList.get(0);

			List<Date> datesList = fatLogList.stream().map(slp -> slp.date)
					.collect(Collectors.toList());

			List<Time> timeList = fatLogList.stream().map(slp -> slp.time)
					.collect(Collectors.toList());

			Query<FatLog> oldActivityTimeSeries = FatLog.find()
					.filter("date in ", datesList).filter("time in ", timeList)
					.filter("userId", fatLog.userId);

			FatLog.deleteAll(oldActivityTimeSeries);
		}
	}
}