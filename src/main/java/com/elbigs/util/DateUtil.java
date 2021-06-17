package com.elbigs.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static String FORMAT_01 = "yyyy-MM-dd";
    public static String FORMAT_02 = "yyyyMMdd";
    public static String FORMAT_03 = "HH";

    public static String getCurrDateStr(String format) {
        return getCurrDateStr(format, null);
    }

    public static String getCurrDateStr(String format, java.util.Locale locale) {
        Date nowDate = new Date();
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, locale);
        return simpleDateFormat.format(nowDate);
    }

    /**
     * 현재 날짜에 일수 더하기
     *
     * @param addedDays
     * @param format
     * @param locale
     * @return
     */
    public static String getAddDate(int addedDays, String format, java.util.Locale locale) {
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
        cal.add(cal.DATE, addedDays);
        String date = sdf.format(cal.getTime());
        return date;
    }

    /**
     * 현재 날짜에 일수 더하기
     *
     * @param addedDays
     * @param format
     * @return
     */
    public static String getAddDate(int addedDays, String format) {

        return getAddDate(addedDays, format, null);
    }

    /**
     * 특정 날자에 일수 더하기
     *
     * @param addDays  더할 일수
     * @param fromDate 특정 날짜 ( yyyy-mm-dd )
     * @return
     */
    public static String getAddDateFromDate(int addDays, String fromDate) {

        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(fromDate.split("-")[0])
                , Integer.parseInt(fromDate.split("-")[1]) - 1
                , Integer.parseInt(fromDate.split("-")[2]));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        cal.add(cal.DATE, addDays);
        String date = sdf.format(cal.getTime());
        return date;
    }

    public static String getAddHour(int addHour, String format) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        cal.add(cal.HOUR, addHour);
        String date = sdf.format(cal.getTime());
        return date;
    }

    /**
     * LocalDateTime 을 String 형태로 변환
     *
     * @param date
     * @param FORMAT
     * @return
     */
    public static String convertLocalDateToStr(LocalDateTime date, String FORMAT) {
        if (date == null) {
            return null;
        } else {
            return date.format(DateTimeFormatter.ofPattern(FORMAT));
        }
    }

    /**
     * 날짜 사이 일수 구하기
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static long getDayDiff(String startDate, String endDate) {

        try {
            SimpleDateFormat format = new SimpleDateFormat(DateUtil.FORMAT_01);
            Date FirstDate = format.parse(endDate);
            Date SecondDate = format.parse(startDate);

            long calDate = FirstDate.getTime() - SecondDate.getTime();
            long calDateDays = calDate / (24 * 60 * 60 * 1000);

            calDateDays = Math.abs(calDateDays);
            return calDateDays;
        } catch (ParseException e) {
            // 예외 처리
        }
        return 0;
    }


}
