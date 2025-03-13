package org.esimulate.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DateTimeUtilTest {
    @Test
    public void testParse() {
        LocalDateTime parse = DateTimeUtil.parse("2022-01-01 00:00:00");
        assertNotNull(parse);
        assertEquals(2022, parse.getYear());
        assertEquals(1, parse.getMonthValue());
        assertEquals(1, parse.getDayOfMonth());
        assertEquals(0, parse.getHour());
        assertEquals(0, parse.getMinute());
        assertEquals(0, parse.getSecond());
    }

    @Test
    public void testFormat() {
        LocalDateTime localDateTime = DateTimeUtil.parse("2022-01-01 00:00:00").plusHours(1);
        assertNotNull(localDateTime);
        assertEquals("2022-01-01 01:00:00", DateTimeUtil.format(localDateTime));
    }
}