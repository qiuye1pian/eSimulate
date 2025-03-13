package org.esimulate.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String format(LocalDateTime time) {
        return FORMATTER.format(time);
    }

    public static LocalDateTime parse(String timeString) {
        return LocalDateTime.parse(timeString, FORMATTER);
    }
}
