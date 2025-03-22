package org.esimulate.util;

import org.apache.commons.validator.GenericValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);

    public static String format(LocalDateTime time) {
        return FORMATTER.format(time);
    }

    public static LocalDateTime parse(String timeString) {
        return LocalDateTime.parse(timeString, FORMATTER);
    }

    public static boolean isNotValidDateTime(String timeString) {
        return !GenericValidator.isDate(timeString, YYYY_MM_DD_HH_MM_SS, true);
    }

}
