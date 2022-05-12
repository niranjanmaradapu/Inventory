package com.otsi.retail.inventory.utils;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author vasavi
 *
 */
@Component
public class DateConverters {

	public long getGapBetweenTwoDays(LocalDate fromDate, LocalDate toDate) {
		return ChronoUnit.DAYS.between(fromDate, toDate);
	}
	
	
	// Local time
		public static Date toDate(LocalTime localTime) {
			Instant instant = localTime.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant();
			return toDate(instant);
		}

		public static Date toDate(Instant instant) {
			BigInteger milis = BigInteger.valueOf(instant.getEpochSecond()).multiply(BigInteger.valueOf(1000));
			milis = milis.add(BigInteger.valueOf(instant.getNano()).divide(BigInteger.valueOf(1_000_000)));
			return new Date(milis.longValue());
		}

		public static Long LocalDateToLongConverter(LocalDate localDate) {
			ZoneId zoneId = ZoneId.systemDefault();
			long localDdateInMilli = localDate.atStartOfDay(zoneId).toEpochSecond();
			return localDdateInMilli;

		}

		// converting ZonedDateTime to long
		public static long convertZonetoLOng(ZonedDateTime dtzone) {
			LocalDateTime time = dtzone.toLocalDateTime();
			Long longValue = (time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) / 1000;
			return longValue;

		}

		// Converting long to ZonedDateTime
		public static ZonedDateTime convertlongToZoneDateTime(long zoneDateTime) {
			LocalDateTime timePoint = getDateTimeFromTimestamp(zoneDateTime);
			ZoneId id = ZoneId.of("Asia/Calcutta");
			ZonedDateTime zoned = ZonedDateTime.of(timePoint, id);
			return zoned;
		}

		// Converting long to localdatetime
		public static LocalDateTime getDateTimeFromTimestamp(long timestamp) {
			if (timestamp == 0)
				return null;
			return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone.getDefault().toZoneId());
		}

		// Converting long to localdate time
		public static LocalDate getDateFromTimestamp(long timestamp) {
			LocalDateTime date = getDateTimeFromTimestamp(timestamp);
			return date == null ? null : date.toLocalDate();
		}

		public static List<LocalDate> getDateFromTimestamp(List<Long> timestamps) {

			return timestamps.stream().map(t -> getDateFromTimestamp(t)).collect(Collectors.toList());
		}

		// Converting long to LocalTime
		public static LocalTime convertLongToLocalTime(long timestamp) {
			LocalDateTime date = getDateTimeFromTimestamp(timestamp);
			return date == null ? null : date.toLocalTime();

		}

		// converting localTime to Long
		public static long convertLocalTimeToLong(LocalTime time) {

			Date date = toDate(time);
			LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
			long millis = (ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) / 1000;
			return millis;
		}

		// converting String to localdatetime
		public static LocalDateTime convertingStringToLocalDateTime(String localDateTime) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
			LocalDateTime dateTime = LocalDateTime.parse(localDateTime, formatter);
			return dateTime;
		}

		// converting localdate format to dd-mm-yyyy format.
		public static String convertLocalDateFormat(LocalDate date) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			return date.format(formatter);
		}

		// Date format validation for String
		public static String validateDateFormate(String date) {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate localDate = LocalDate.parse(date, formatter);
			return localDate.toString();
		}

		// Date format validation for String
		public static String validateDateFormated(String date) {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate localDate = LocalDate.parse(date, formatter);
			return localDate.toString();
		}

		public static String validateAndConvertString(String date) {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate localDate = LocalDate.parse(date, formatter);
			return localDate.toString();
		}

		public static String convertLocalDateFormat(LocalDateTime date) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			return date.format(formatter);
		}

		public static String convertHSRLocalDateFormat(LocalDateTime date) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			return date.format(formatter);
		}

		public static String convertCfstSyncLocalDateFormat(LocalDate localDate) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			return localDate.format(formatter);
		}

		public static String convertCfstSyncLocalDateTimeFormat(LocalDateTime localDate) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			return localDate.format(formatter);
		}

		/**
		 * 
		 * @param date
		 * @return date as localDate
		 */
		public static LocalDate convertStirngTOlocalDate(String date) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate localDate = LocalDate.parse(date, formatter);
			return localDate;
		}

		public static LocalDate convertStringTOlocalDate(String date) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate localDate = LocalDate.parse(date, formatter);
			return localDate;
		}

		public static LocalDate convertStirngTODate(String date) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate localDate = LocalDate.parse(date, formatter);
			return localDate;
		}

		public static String convertDateToString(LocalDate date) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			return date.format(formatter);
		}

		// Local Date to LocalDateTime Conversion 24 hrs
		public static List<LocalDateTime> convertDateToLocalDateTime(LocalDate fromDate, LocalDate toDate) {

			List<LocalDateTime> localDateTimeList = new ArrayList<>();

			String dateVal = fromDate + "T00:00:00.000Z";
			ZonedDateTime zdt = ZonedDateTime.parse(dateVal);
			LocalDateTime ldtFrom = zdt.toLocalDateTime();
			localDateTimeList.add(ldtFrom);

			dateVal = toDate + "T23:59:59.999Z";
			ZonedDateTime zdt1 = ZonedDateTime.parse(dateVal);
			LocalDateTime ldtTo = zdt1.toLocalDateTime();
			localDateTimeList.add(ldtTo);

			return localDateTimeList;

		}

		public static String replaceDefaults(String input) {

			if (StringUtils.isBlank(input) || input == null) {
				return StringUtils.EMPTY;
			}
			return input;
		}

		public static String covertDateToString(String format, String date) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
			LocalDate localDate = LocalDate.parse(date, formatter);
			return localDate.toString();
		}

		public static String replaceDefaults(Object input) {

			if (null == input) {
				return "0.0";
			}
			return input.toString();
		}

		public static boolean validateYearMonth(final String dateFormat) {

			LocalDate inputDate = LocalDate.parse(dateFormat);
			LocalDate currentDate = LocalDate.now();
			// \\d{4}-[01]\\d-[0-3]\\d \\d\\d-[0-3]\\d{4}-[01]
			if (dateFormat == null || !dateFormat.matches("\\d{4}-\\d{2}-\\d{2}") || !(inputDate.isBefore(currentDate)))
				return false;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setLenient(false);
			try {
				sdf.parse(dateFormat);
				return true;
			} catch (ParseException ex) {
				return false;
			}

		}

		public static LocalDateTime convertLocalDateToLocalDateTime(LocalDate fromDate) {

			String dateVal = fromDate + "T00:00:00.000Z";
			ZonedDateTime zdt = ZonedDateTime.parse(dateVal);
			LocalDateTime ldtFrom = zdt.toLocalDateTime();
			return ldtFrom;
		}
		
		public static LocalDateTime convertToLocalDateTimeMax(LocalDate fromDate) {

			String dateVal = fromDate + "T23:59:59.999Z";
			ZonedDateTime zdt = ZonedDateTime.parse(dateVal);
			LocalDateTime ldtFrom = zdt.toLocalDateTime();
			return ldtFrom;
		}

		// converting String to localdatetime
		public static LocalDateTime StringToLocalDateTime(String localDateTime) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			LocalDateTime dateTime = LocalDateTime.parse(localDateTime, formatter);
			return dateTime;
		}

		public static LocalDateTime StringToLocalDateTimeVahan(String localDateTime) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDateTime dateTime = LocalDateTime.parse(localDateTime, formatter);
			return dateTime;
		}

		public static LocalDate StringToLocalDate(String date) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			LocalDate localDate = LocalDate.parse(date, formatter);
			return localDate;
		}

		public static LocalDate StringToLocalDateVahan(String date) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDate localDate = LocalDate.parse(date, formatter);
			return localDate;
		}

		// converting String to localdatetime
		public static LocalDateTime StringLocalDateTime(String localDateTime) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDateTime dateTime = LocalDateTime.parse(localDateTime, formatter);
			return dateTime;
		}

		public static LocalDate StringLocalDate(String date) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate localDate = LocalDate.parse(date, formatter);
			return localDate;
		}
}
