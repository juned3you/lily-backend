package com.lily.authorize.fitbit.loader;

import com.lily.exception.LoaderException;
import com.lily.loader.Loader;
import com.lily.mongo.models.SleepGoal;

/**
 * Load model into Mongodb.
 * 
 * @author Mohammad
 *
 */
public class SleepGoalLoader implements Loader {

	@Override
	public void load(Object obj) throws LoaderException {
		SleepGoal sleepGoal = (SleepGoal) obj;
		try {
			deleteRecords(sleepGoal);
			sleepGoal.insert();
		} catch (Throwable e) {
			throw new LoaderException(e);
		}
	}

	/**
	 * Delete records.
	 */
	private void deleteRecords(SleepGoal sleepGoal) {

		SleepGoal oldSleepGoal = SleepGoal.find()
				.filter("updatedOn", sleepGoal.updatedOn)
				.filter("userId", sleepGoal.userId).get();

		if (oldSleepGoal != null) {
			oldSleepGoal.delete();
		}
	}
}