package com.rhcloud.cervex_jsoftware95.beans;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Util {

	public static final String FULL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	public static final ZoneId ADMIN_ZONE = ZoneId.of("GMT+1");

	public static Date getCurrentTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FULL_DATE_TIME_FORMAT);
		SimpleDateFormat simpleFormatter = new SimpleDateFormat(FULL_DATE_TIME_FORMAT);
		try {
			return simpleFormatter.parse(ZonedDateTime.now(ADMIN_ZONE).format(formatter));
		} catch (Exception e) {
			return new Date();
		}
	}
}
