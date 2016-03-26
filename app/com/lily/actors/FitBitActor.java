package com.lily.actors;

import java.util.Date;

import play.Logger;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.lily.authorize.fitbit.extractor.FitbitExtractor;
import com.lily.authorize.fitbit.loader.SleepLogLoader;
import com.lily.authorize.fitbit.transformer.SleepLogTransformer;
import com.lily.extractor.ExtractorRequest;
import com.lily.extractor.ExtractorResponse;
import com.lily.models.FitbitUser;
import com.lily.utils.DateUtils;

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
		
		//Sleep Log
		loadSleepLog(fitbitUser);

		Logger.info("*********** Actor completed for fitbit user: "
				+ fitbitUser.encodedId);
		// sender().tell("Success", self());
		context().stop(self());
	}

	/**
	 * Load Sleep log into Mongo for last day date.
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

		} catch (Throwable t) {
			Logger.error("Error updating Sleep log for user: "
					+ fitbitUser.encodedId + "-> " + t.getMessage());
		}
	}
}