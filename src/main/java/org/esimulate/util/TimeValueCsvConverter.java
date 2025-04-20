package org.esimulate.util;

import lombok.extern.slf4j.Slf4j;
import org.esimulate.core.pojo.common.TimeValue;
import org.jetbrains.annotations.TestOnly;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
public class TimeValueCsvConverter {

    public static <T extends TimeValue> List<T> convertByCsvContent(List<String> lines, BiFunction<LocalDateTime, BigDecimal, T> factory) {
        return lines.stream()
                .map(line -> convertByCsvLine(line, factory))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(TimeValue::getTime))
                .collect(Collectors.toList());
    }

    @TestOnly
    public static <T> T convertByCsvLineForTest(String line, BiFunction<LocalDateTime, BigDecimal, T> factory) {
        return convertByCsvLine(line, factory);
    }

    private static <T> T convertByCsvLine(String line, BiFunction<LocalDateTime, BigDecimal, T> factory) {
        String[] split = line.split(",");

        if (split.length != 2) {
            return null;
        }

        if (split[0] == null || split[1] == null) {
            log.error("这行不是2列，内容:{}", line);
            return null;
        }
        if (DateTimeUtil.isNotValidDateTime(split[0])) {
            log.error("这行日期不合格，内容:{}", line);
            return null;
        }
        if (!split[1].matches("-?\\d+(\\.\\d+)?")) {
            log.error("这行数值不合格，内容:{}", line);
            return null;
        }

        try {
            LocalDateTime time = DateTimeUtil.parse(split[0]);
            BigDecimal value = new BigDecimal(split[1]);
            return factory.apply(time, value);
        } catch (Exception e) {
            log.error("convertByCsvLine error, line content:{}", line, e);
            return null;
        }
    }

    public static String toLine(TimeValue timeValue) {
        return String.format("%s,%s\n", DateTimeUtil.format(timeValue.getTime()), timeValue.getValue().setScale(2, RoundingMode.HALF_UP));
    }
}
