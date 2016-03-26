package com.lily.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Seconds;

import com.typesafe.config.ConfigFactory;

/**
 * Date Utils
 * 
 * @author Mohammad
 */
public class DateUtils {

	private static DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
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
}