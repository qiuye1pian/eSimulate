package org.esimulate.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.load.electric.ElectricLoadValue;
import org.esimulate.util.DateTimeUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ElectricLoadValueDto {

    private LocalDateTime time;

    private BigDecimal value;

    public ElectricLoadValueDto(ElectricLoadValue electricLoadValue) {
        this.time = electricLoadValue.getDatetime();
        this.value = electricLoadValue.getLoadValue();
    }

    public static List<ElectricLoadValueDto> convertByCsvContent(List<String> csvContent) {
        return csvContent.stream()
                .map(ElectricLoadValueDto::convertByCsvLine)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ElectricLoadValueDto::getTime))
                .collect(Collectors.toList());
    }

    public static ElectricLoadValueDto convertByCsvLine(String csvLine) {
        String[] split = csvLine.split(",");
        if (split.length != 2) {
            return null;
        }

        // 如果split[0]不是日期，或者split[1]不是数字
        if (split[0] == null || split[1] == null
                || DateTimeUtil.isNotValidDateTime(split[0])
                || !split[1].matches("-?\\d+(\\.\\d+)?")) {
            return null;
        }

        return new ElectricLoadValueDto(DateTimeUtil.parse(split[0]), new BigDecimal(split[1]));
    }

    public ElectricLoadValue toElectricLoadValue() {
        ElectricLoadValue electricLoadValue = new ElectricLoadValue();
        electricLoadValue.setDatetime(time);
        electricLoadValue.setLoadValue(value);
        electricLoadValue.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        electricLoadValue.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return electricLoadValue;
    }
}
