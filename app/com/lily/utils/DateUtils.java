package com.lily.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Seconds;

import com.lily.utils.LilyConstants.DurationInterval;
import com.typesafe.config.ConfigFactory;

/**
 * Date Utils
 * 
 * @author Mohammad
 */
public class DateUtils {

	private static DateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");

	/**
	 * Format date.
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date formatDate(String date) throws ParseException {
		return simpleDateFormat.parse(date);
	}

	/**
	 * Format date.
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String formatDate(Date date) throws ParseException {
		return simpleDateFormat.format(date);
	}

	/**
	 * Format date.
	 * 
	 * @param date
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static Date formatDate(String date, String format)
			throws ParseException {
		DateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.parse(date);
	}

	public static Date getLastDayDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	/**
	 * Calculate time in Seconds.
	 * 
	 * @param hour
	 * @param minute
	 * @param dateTime
	 * @return
	 */
	public static int nextExecutionInSeconds(int hour, int minute,
			DateTime dateTime) {
		return Seconds.secondsBetween(getCetDateTime(),
				nextExecution(hour, minute, dateTime)).getSeconds();
	}

	/**
	 * Calculate Date time
	 * 
	 * @param hour
	 * @param minute
	 * @param dateTime
	 * @return
	 */
	private static DateTime nextExecution(int hour, int minute,
			DateTime dateTime) {
		DateTime next = dateTime.withHourOfDay(hour).withMinuteOfHour(minute)
				.withSecondOfMinute(0).withMillisOfSecond(0);

		if (next.isBeforeNow())
			return next.plusHours(24);
		else
			return next;
	}

	/**
	 * Get Cet Datetime.
	 * 
	 * @return
	 */
	public static DateTime getCetDateTime() {
		DateTimeZone cetTimeZone = DateTimeZone.forTimeZone(TimeZone
				.getTimeZone(ConfigFactory.load().getString(
						"scheduler.timezone")));
		return new DateTime(cetTimeZone);
	}

	public static Date setStartTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	public static Date setTime(Date date, int hour, int mins, int secs) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, mins);
		cal.set(Calendar.SECOND, secs);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date setEndTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}

	/**
	 * Return date range based on interval.
	 * 
	 * @param interval
	 */
	public static Date[] getDateRange(DurationInterval interval, Date initialDate) {
		Date today = initialDate;
		Calendar gcal = new GregorianCalendar();
		gcal.setTime(today);

		Date startDate = today;
		Date endDate = today;

		switch (interval) {
		case YEARLY:
			gcal.set(Calendar.DAY_OF_YEAR,
					gcal.getActualMinimum(Calendar.DAY_OF_YEAR));
			startDate = gcal.getTime();

			gcal.set(Calendar.DAY_OF_YEAR,
					gcal.getActualMaximum(Calendar.DAY_OF_YEAR));
			endDate = gcal.getTime();
			break;

		case MONTHLY:
			gcal.set(Calendar.DAY_OF_MONTH,
					gcal.getActualMinimum(Calendar.DAY_OF_MONTH));
			startDate = gcal.getTime();

			gcal.set(Calendar.DAY_OF_MONTH,
					gcal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endDate = gcal.getTime();
			break;

		case WEEKLY:
			gcal.set(Calendar.DAY_OF_WEEK,
					gcal.getActualMinimum(Calendar.DAY_OF_WEEK));
			startDate = gcal.getTime();

			gcal.set(Calendar.DAY_OF_WEEK,
					gcal.getActualMaximum(Calendar.DAY_OF_WEEK));
			endDate = gcal.getTime();
			break;

		case DAILY:
			startDate = gcal.getTime();
			endDate = gcal.getTime();
			break;
		}

		startDate = setStartTime(startDate);
		endDate = setEndTime(endDate);
		return new Date[] { startDate, endDate };
	}
}