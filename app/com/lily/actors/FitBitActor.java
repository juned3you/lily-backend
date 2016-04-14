package com.lily.actors;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.beanutils.BeanUtils;

import play.Logger;
import play.db.jpa.JPA;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.lily.authorize.fitbit.extractor.FitbitExtractor;
import com.lily.authorize.fitbit.loader.ActivityTimeSeriesLoader;
import com.lily.authorize.fitbit.loader.DailyActivitiesLoader;
import com.lily.authorize.fitbit.loader.FatLogLoader;
import com.lily.authorize.fitbit.loader.HeartRateLoader;
import com.lily.authorize.fitbit.loader.SleepLogLoader;
import com.lily.authorize.fitbit.loader.SleepTimeSeriesLoader;
import com.lily.authorize.fitbit.loader.WeightLogLoader;
import com.lily.authorize.fitbit.transformer.ActivityTimeSeriesTransformer;
import com.lily.authorize.fitbit.transformer.DailyActivitiesTransformer;
import com.lily.authorize.fitbit.transformer.FatLogTransformer;
import com.lily.authorize.fitbit.transformer.HeartRateTransformer;
import com.lily.authorize.fitbit.transformer.SleepLogTransformer;
import com.lily.authorize.fitbit.transformer.SleepTimeSeriesTransformer;
import com.lily.authorize.fitbit.transformer.WeightLogTransformer;
import com.lily.extractor.ExtractorRequest;
import com.lily.extractor.ExtractorResponse;
import com.lily.models.FitbitUser;
import com.lily.services.FitbitService;
import com.lily.utils.DateUtils;
import com.lily.utils.EnumActivities;
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

		// Update User
		updateUser(fitbitUser);

		// Sleep Log
		loadSleepLog(fitbitUser);

		// Sleep time series
		loadSleepTimeSeries(fitbitUser);

		// Daily Log
		loadDailyActivities(fitbitUser);

		// Activity time series
		loadActivitiesTimeSeries(fitbitUser);

		// fatlog
		loadFatLog(fitbitUser);

		// weightLog
		loadWeightLog(fitbitUser);

		// heartRate
		loadHeartRate(fitbitUser);

		// updating sync flag.
		fitbitUser.isSync = true;
		JPA.withTransaction(() -> {
			JPA.em().merge(fitbitUser);
		});

		Logger.info("*********** Actor completed for fitbit user: "
				+ fitbitUser.encodedId);
		// sender().tell("Success", self());
		context().stop(self());
	}

	/**
	 * Update User.
	 * 
	 * @param persistFitbitUser
	 */
	private void updateUser(FitbitUser persistFitbitUser) {
		try {
			FitbitUser fitbitUserProfile = new FitbitService()
					.getUserFromServer(persistFitbitUser.encodedId);
			fitbitUserProfile.firstname = persistFitbitUser.firstname;
			fitbitUserProfile.lastname = persistFitbitUser.lastname;
			fitbitUserProfile.email = persistFitbitUser.email;
			fitbitUserProfile.password = persistFitbitUser.password;
			fitbitUserProfile.createdAt = persistFitbitUser.createdAt;
			fitbitUserProfile.id = persistFitbitUser.id;
			fitbitUserProfile.isSync = persistFitbitUser.isSync;

			// Copy new properties.
			BeanUtils.copyProperties(persistFitbitUser, fitbitUserProfile);

			persistFitbitUser.lastModified = new Date();
			JPA.withTransaction(() -> {
				JPA.em().merge(persistFitbitUser);
			});

		} catch (Throwable t) {
			Logger.error("Error updating user profile: "
					+ persistFitbitUser.encodedId + "-> " + t.getMessage());
		}

	}

	/**
	 * Load Sleep log into Mongo for last day date.
	 * 
	 * @param fitbitUser
	 */
	private void loadSleepLog(FitbitUser fitbitUser) {
		// Sleep Log
		try {
			Date startDate = DateUtils.formatDate(getStartDate(fitbitUser));
			Date endDate = new Date();

			GregorianCalendar gcal = new GregorianCalendar();
			gcal.setTime(startDate);
			while (!gcal.getTime().after(endDate)) {
				String dateString = DateUtils.formatDate(gcal.getTime());
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

				Logger.info("SleepLog for date " + dateString
						+ " has been inserted successfully for user: "
						+ fitbitUser.encodedId);
				gcal.add(Calendar.DATE, 1);
			}

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
	public void loadSleepTimeSeries(FitbitUser fitbitUser) {
		// Sleep Log
		String dateString = "";
		try {
			dateString = getStartDate(fitbitUser);

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

			Logger.info("SleepTimeSeries for date " + dateString
					+ " to today has been inserted successfully for user: "
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
	public void loadDailyActivities(FitbitUser fitbitUser) {
		// Sleep Log
		String dateString = "";
		try {
			Date startDate = DateUtils.formatDate(getStartDate(fitbitUser));
			Date endDate = new Date();

			GregorianCalendar gcal = new GregorianCalendar();
			gcal.setTime(startDate);
			while (!gcal.getTime().after(endDate)) {
				dateString = DateUtils.formatDate(gcal.getTime());

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
				Logger.info("DailyActivity for date " + dateString
						+ " has been inserted successfully for user: "
						+ fitbitUser.encodedId);

				gcal.add(Calendar.DATE, 1);
			}
		} catch (Throwable t) {
			Logger.error("Error updating Daily activities  for user: "
					+ fitbitUser.encodedId + "-> " + t.getMessage());
			t.printStackTrace();
		}
	}

	/**
	 * Load Activities log into Mongo for last day date.
	 * 
	 * @param fitbitUser
	 */
	public void loadActivitiesTimeSeries(FitbitUser fitbitUser) {
		// Sleep Log
		String dateString = "";
		try {
			dateString = getStartDate(fitbitUser);

			for (EnumActivities es : EnumActivities.values()) {
				ExtractorRequest sleepRequest = new ExtractorRequest(
						fitbitUser.encodedId, es.getUri() + "/date/"
								+ dateString + "/today");
				// Extract from fitbit
				ExtractorResponse response = new FitbitExtractor()
						.extract(sleepRequest);
				response.setUri(es.getUri());
				response.setUserId(fitbitUser.encodedId);

				// Transform into model.
				Object transformResponse = new ActivityTimeSeriesTransformer()
						.transform(response);

				// Load in mongo db.
				new ActivityTimeSeriesLoader().load(transformResponse);
			}

			Logger.info("ActivityTimeSeries for date " + dateString
					+ " to today has been inserted successfully for user: "
					+ fitbitUser.encodedId);

		} catch (Throwable t) {
			Logger.error("Error updating Sleep Time Series  for user: "
					+ fitbitUser.encodedId + "-> " + t.getMessage());
			t.printStackTrace();
		}
	}

	/**
	 * Load Fat log into Mongo for last day date.
	 * 
	 * @param fitbitUser
	 */
	public static void loadFatLog(FitbitUser fitbitUser) {
		// Sleep Log
		try {
			Date startDate = DateUtils.formatDate(getStartDate(fitbitUser));
			String todayDateString = DateUtils.formatDate(new Date());

			String dateString = DateUtils.formatDate(startDate);
			ExtractorRequest fatRequest = new ExtractorRequest(
					fitbitUser.encodedId, "body/log/fat/date/" + dateString
							+ "/" + todayDateString);

			// Extract from fitbit
			ExtractorResponse response = new FitbitExtractor()
					.extract(fatRequest);
			response.setDate(dateString);
			response.setUserId(fitbitUser.encodedId);

			// Transform into model.
			Object transformResponse = new FatLogTransformer()
					.transform(response);

			// Load in mongo db.
			new FatLogLoader().load(transformResponse);

			Logger.info("FatLog for date " + dateString + " - "
					+ todayDateString
					+ " has been inserted successfully for user: "
					+ fitbitUser.encodedId);

		} catch (Throwable t) {
			Logger.error("Error updating FatLog for user: "
					+ fitbitUser.encodedId + "-> " + t.getMessage());
			t.printStackTrace();
		}
	}

	/**
	 * Load Weight log into Mongo for last day date.
	 * 
	 * @param fitbitUser
	 */
	public static void loadWeightLog(FitbitUser fitbitUser) {
		// Sleep Log
		try {
			Date startDate = DateUtils.formatDate(getStartDate(fitbitUser));
			String todayDateString = DateUtils.formatDate(new Date());

			String dateString = DateUtils.formatDate(startDate);
			ExtractorRequest fatRequest = new ExtractorRequest(
					fitbitUser.encodedId, "body/log/weight/date/" + dateString
							+ "/" + todayDateString);

			// Extract from fitbit
			ExtractorResponse response = new FitbitExtractor()
					.extract(fatRequest);
			response.setDate(dateString);
			response.setUserId(fitbitUser.encodedId);

			// Transform into model.
			Object transformResponse = new WeightLogTransformer()
					.transform(response);

			// Load in mongo db.
			new WeightLogLoader().load(transformResponse);

			Logger.info("WeightLog for date " + dateString + " - "
					+ todayDateString
					+ " has been inserted successfully for user: "
					+ fitbitUser.encodedId);

		} catch (Throwable t) {
			Logger.error("Error updating WeightLog for user: "
					+ fitbitUser.encodedId + "-> " + t.getMessage());
			t.printStackTrace();
		}
	}

	/**
	 * Load HeartRate into Mongo for last day date.
	 * 
	 * @param fitbitUser
	 */
	public static void loadHeartRate(FitbitUser fitbitUser) {
		// Sleep Log
		try {
			Date startDate = DateUtils.formatDate(getStartDate(fitbitUser));
			String todayDateString = DateUtils.formatDate(new Date());

			String dateString = DateUtils.formatDate(startDate);
			ExtractorRequest fatRequest = new ExtractorRequest(
					fitbitUser.encodedId, "activities/heart/date/" + dateString
							+ "/" + todayDateString);

			// Extract from fitbit
			ExtractorResponse response = new FitbitExtractor()
					.extract(fatRequest);
			response.setDate(dateString);
			response.setUserId(fitbitUser.encodedId);

			// Transform into model.
			Object transformResponse = new HeartRateTransformer()
					.transform(response);

			// Load in mongo db.
			new HeartRateLoader().load(transformResponse);

			Logger.info("HeartRate for date " + dateString + " - "
					+ todayDateString
					+ " has been inserted successfully for user: "
					+ fitbitUser.encodedId);

		} catch (Throwable t) {
			Logger.error("Error updating HeartRate for user: "
					+ fitbitUser.encodedId + "-> " + t.getMessage());
			t.printStackTrace();
		}
	}

	/**
	 * Get Start date based on isSync flag.
	 * 
	 * @param fitbitUser
	 * @return
	 * @throws ParseException
	 */
	private static String getStartDate(FitbitUser fitbitUser)
			throws ParseException {
		if (fitbitUser.isSync == null || fitbitUser.isSync == false) {
			return fitbitUser.memberSince;
		}
		Date lastDayDate = DateUtils.getLastDayDate();
		String dateString = DateUtils.formatDate(lastDayDate);
		return dateString;
	}
}