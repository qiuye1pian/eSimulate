package org.esimulate.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.load.heat.ThermalLoadValue;
import org.esimulate.util.DateTimeUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThermalLoadValueDto {

    private LocalDateTime time;

    private BigDecimal value;

    public ThermalLoadValueDto(ThermalLoadValue thermalLoadValue) {
        this.time = thermalLoadValue.getDatetime();
        this.value = thermalLoadValue.getLoadValue();
    }

    public static List<ThermalLoadValueDto> convertByCsvContent(List<String> csvContent) {
        return csvContent.stream()
                .map(ThermalLoadValueDto::convertByCsvLine)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ThermalLoadValueDto::getTime))
                .collect(Collectors.toList());
    }

    public static ThermalLoadValueDto convertByCsvLine(String csvLine) {
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

        return new ThermalLoadValueDto(DateTimeUtil.parse(split[0]), new BigDecimal(split[1]));
    }

    public ThermalLoadValue toThermalLoadValue() {
        ThermalLoadValue thermalLoadValue = new ThermalLoadValue();
        thermalLoadValue.setDatetime(time);
        thermalLoadValue.setLoadValue(value);
        thermalLoadValue.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        thermalLoadValue.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return thermalLoadValue;
    }

    public String toLine() {
        return String.format("%s,%s\n", DateTimeUtil.format(this.time), this.value.setScale(2, RoundingMode.HALF_UP));
    }

}
