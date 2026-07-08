package com.gaquant.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/** 日期工具类 */
public class DateUtil {

    private static final DateTimeFormatter STD_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDate parse(String dateStr) {
        return LocalDate.parse(dateStr, STD_FMT);
    }

    public static String format(LocalDate date) {
        return date.format(STD_FMT);
    }

    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    public static LocalDate addMonths(LocalDate date, int months) {
        return date.plusMonths(months);
    }
}
