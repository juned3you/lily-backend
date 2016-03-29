package com.lily.actors;

import java.util.Date;

import play.Logger;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.lily.authorize.fitbit.extractor.FitbitExtractor;
import com.lily.authorize.fitbit.loader.DailyActivitiesLoader;
import com.lily.authorize.fitbit.loader.SleepLogLoader;
import com.lily.authorize.fitbit.loader.SleepTimeSeriesLoader;
import com.lily.authorize.fitbit.transformer.DailyActivitiesTransformer;
import com.lily.authorize.fitbit.transformer.SleepLogTransformer;
import com.lily.authorize.fitbit.transformer.SleepTimeSeriesTransformer;
import com.lily.extractor.ExtractorRequest;
import com.lily.extractor.ExtractorResponse;
import com.lily.models.FitbitUser;
import com.lily.utils.DateUtils;
import com.lily.utils.EnumSleep;

/**
 * Akka Fitbit actor.
 * 
 * @author Mohammad
 *
 */
public class FitBitActor extends UntypedActor {

	public static Props props = Props.create(FitBitActor.class);

	/**
	 * On receive
	 */
	public void onReceive(Object user) throws Exception {
		if (!(user instanceof FitbitUser))
			throw new Exception("Fitbituser param is null in FitBitActor");

		FitbitUser fitbitUser = (FitbitUser) user;
		Logger.info("*********** Actor invoke for fitbit user: "
				+ fitbitUser.encodedId);
		/*
		 * try { new FitbitService().createUpdateUser(fitbitUser.encodedId); }
		 * catch (Throwable t) { Logger.error("Error updating user: " +
		 * fitbitUser.encodedId + "-> " + t.getMessage()); throw new
		 * Exception(t); }
		 */

		// Sleep Log
		loadSleepLog(fitbitUser);

		// Sleep time series
		loadSleepTimeSeries(fitbitUser);
		
		//Daily Log
		loadDailyActivities(fitbitUser);

		Logger.info("*********** Actor completed for fitbit user: "
				+ fitbitUser.encodedId);
		// sender().tell("Success", self());
		context().stop(self());
	}

	/**
	 * Load Sleep log into Mongo for last day date.
	 * 
	 * @param fitbitUser
	 */
	private void loadSleepLog(FitbitUser fitbitUser) {
		// Sleep Log
		try {
			Date lastDayDate = DateUtils.getLastDayDate();
			String dateString = DateUtils.formatDate(lastDayDate);

			ExtractorRequest sleepRequest = new ExtractorRequest(
					fitbitUser.encodedId, "sleep/date/" + dateString);

			// Extract from fitbit
			ExtractorResponse response = new FitbitExtractor()
					.extract(sleepRequest);
			response.setDate(dateString);
			response.setUserId(fitbitUser.encodedId);

			// Transform into model.
			Object transformResponse = new SleepLogTransformer()
					.transform(response);

			// Load in mongo db.
			new SleepLogLoader().load(transformResponse);

			Logger.info("SleepLog for yesterday has been inserted successfully for user: "
					+ fitbitUser.encodedId);

		} catch (Throwable t) {
			Logger.error("Error updating Sleep log for user: "
					+ fitbitUser.encodedId + "-> " + t.getMessage());
			t.printStackTrace();
		}
	}

	/**
	 * Load Sleep log into Mongo for last day date.
	 * 
	 * @param fitbitUser
	 */
	public static void loadSleepTimeSeries(FitbitUser fitbitUser) {
		try {
			Date lastDayDate = DateUtils.getLastDayDate();
			String dateString = DateUtils.formatDate(lastDayDate);

			for (EnumSleep es : EnumSleep.values()) {
				ExtractorRequest sleepRequest = new ExtractorRequest(
						fitbitUser.encodedId, es.getUri() + "/date/"
								+ dateString + "/today");
				// Extract from fitbit
				ExtractorResponse response = new FitbitExtractor()
						.extract(sleepRequest);
				response.setUri(es.getUri());
				response.setUserId(fitbitUser.encodedId);

				// Transform into model.
				Object transformResponse = new SleepTimeSeriesTransformer()
						.transform(response);

				// Load in mongo db.
				new SleepTimeSeriesLoader().load(transformResponse);
			}

			Logger.info("SleepTimeSeries for yesterday has been inserted successfully for user: "
					+ fitbitUser.encodedId);

		} catch (Throwable t) {
			Logger.error("Error updating Sleep Time Series  for user: "
					+ fitbitUser.encodedId + "-> " + t.getMessage());
			t.printStackTrace();
		}
	}

	/**
	 * Load Daily activities into Mongo for last day date.
	 * 
	 * @param fitbitUser
	 */
	public static void loadDailyActivities(FitbitUser fitbitUser) {
		try {
			Date lastDayDate = DateUtils.getLastDayDate();
			String dateString = DateUtils.formatDate(lastDayDate);//"2016-03-02";

			ExtractorRequest sleepRequest = new ExtractorRequest(
					fitbitUser.encodedId, "activities/date/" + dateString);
			// Extract from fitbit
			ExtractorResponse response = new FitbitExtractor()
					.extract(sleepRequest);
			response.setUserId(fitbitUser.encodedId);
			response.setDate(dateString);

			// Transform into model.
			Object transformResponse = new DailyActivitiesTransformer()
					.transform(response);

			// Load in mongo db.
			new DailyActivitiesLoader().load(transformResponse);
			Logger.info("DailyActivity for yesterday has been inserted successfully for user: "
					+ fitbitUser.encodedId);

		} catch (Throwable t) {
			Logger.error("Error updating Daily activities  for user: "
					+ fitbitUser.encodedId + "-> " + t.getMessage());
			t.printStackTrace();
		}
	}
}