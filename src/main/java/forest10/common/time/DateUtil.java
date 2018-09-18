package forest10.common.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author Forest10
 * @date 2018/4/22 下午6:18
 */
public class DateUtil {

	private static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter YYYYMMDDHHMMSS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private static final ZoneId ZONE = ZoneId.systemDefault();


	/**
	 * 转化时间字符串至 Date
	 *
	 * @param strDate
	 * @return
	 */
	public static Date convertStr2Date(String strDate) {
		if (strDate.contains(":")) {
			return LocalDateTimeToDate(convertStr2LocalDateTime(strDate));
		}
		return LocalDateToDate(convertStr2LocalDate(strDate));
	}

	/**
	 * 转化时间字符串至 LocalDate
	 *
	 * @param strDate
	 * @return
	 */
	public static LocalDate convertStr2LocalDate(String strDate) {
		return LocalDate.parse(strDate, YYYYMMDD);
	}

	/**
	 * 转化时间字符串至 LocalDateTime
	 *
	 * @param strDate
	 * @return
	 */
	public static LocalDateTime convertStr2LocalDateTime(String strDate) {
		return LocalDateTime.parse(strDate, YYYYMMDDHHMMSS);
	}


	/**
	 * java.util.Date --> java.time.LocalDateTime
	 */
	public static LocalDateTime dateToLocalDateTime(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZONE);
	}

	/**
	 * java.util.Date --> java.time.LocalDate
	 */
	public static LocalDate dateToLocalDate(Date date) {
		Instant instant = date.toInstant();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE);
		return localDateTime.toLocalDate();
	}

	/**
	 * java.util.Date --> java.time.LocalTime
	 */
	public static LocalTime dateToLocalTime(Date date) {
		Instant instant = date.toInstant();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE);
		return localDateTime.toLocalTime();
	}

	/**
	 * java.time.LocalDateTime --> java.util.Date
	 */
	public static Date LocalDateTimeToDate(LocalDateTime localDateTime) {
		Instant instant = localDateTime.atZone(ZONE).toInstant();
		return Date.from(instant);
	}

	/**
	 * java.time.LocalDate --> java.util.Date
	 */
	public static Date LocalDateToDate(LocalDate localDate) {
		Instant instant = localDate.atStartOfDay().atZone(ZONE).toInstant();
		return Date.from(instant);
	}

	/**
	 * 根据传入的时间格式返回系统当前的时间
	 *
	 * @param format string
	 * @return
	 */
	public static String nowByFormat(String format) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
		LocalDateTime now = LocalDateTime.now();
		return now.format(dateTimeFormatter);
	}

	/**
	 * 根据传入的时间格式返回系统当前的时间
	 *
	 * @param format string
	 * @return
	 */
	public static String nowByDateTimeFormatter(DateTimeFormatter format) {
		LocalDateTime now = LocalDateTime.now();
		return now.format(format);
	}


}
