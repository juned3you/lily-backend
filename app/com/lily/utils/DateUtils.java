package com.lily.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date Utils
 * 
 * @author Mohammad
 */
public class DateUtils {

	public static Date formatDate(String date) throws ParseException {
		DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return simpleDateFormat.parse(date);
	}

	public static Date formatDate(String date, String format)
			throws ParseException {
		DateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.parse(date);
	}
}
