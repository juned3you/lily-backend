package com.lily.authorize.fitbit.loader;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import com.lily.exception.LoaderException;
import com.lily.loader.Loader;
import com.lily.mongo.models.Sleep;
import com.lily.mongo.models.SleepLog;
import com.lily.mongo.utils.Model;

/**
 * Load model into Mongodb.
 * 
 * @author Mohammad
 *
 */
public class SleepLogLoader implements Loader {

	@Override
	public void load(Object obj) throws LoaderException {

		SleepLog sleepLog = (SleepLog) obj;
		deleteRecords(sleepLog);
		try {
			if (sleepLog.sleep != null) {
				Model.insertAll(sleepLog.sleep);
			}
			sleepLog.summary.insert();

			sleepLog.insert();
		} catch (Throwable e) {
			deleteRecords(sleepLog);
			throw new LoaderException(e);
		}
	}

	/**
	 * Delete records.
	 * 
	 * @param date
	 */
	private void deleteRecords(SleepLog sleepLog) {
		// deleting old records to avoid duplicates
		/*
		 * SleepLog oldsleepLog =
		 * Model.createQuery(SleepLog.class).field("date") .equal(date).get();
		 */

		SleepLog oldsleepLog = SleepLog.find().filter("date", sleepLog.date)
				.filter("userId", sleepLog.userId).get();

		if (oldsleepLog != null) {
			if (oldsleepLog.sleep != null) {
				List<ObjectId> ids = oldsleepLog.sleep.stream()
						.map(sl -> sl.id).collect(Collectors.toList());
				Query<Sleep> sleepQuery = Sleep.q(Sleep.class).field("id")
						.in(ids);
				Model.deleteAll(sleepQuery);
			}

			if (oldsleepLog.summary != null)
				oldsleepLog.summary.delete();

			oldsleepLog.delete();
		}
	}
}