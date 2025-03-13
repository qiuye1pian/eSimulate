package org.esimulate.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.load.heat.ThermalLoadValue;
import org.esimulate.util.DateTimeUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThermalLoadValueDto {

    private LocalDateTime time;

    private BigDecimal value;

    public static List<ThermalLoadValueDto> convertByCsvContent(List<String> csvContent) {
        return csvContent.stream()
                .map(ThermalLoadValueDto::convertByCsvLine)
                .sorted(Comparator.comparing(ThermalLoadValueDto::getTime))
                .collect(Collectors.toList());
    }

    public static ThermalLoadValueDto convertByCsvLine(String csvLine) {
        String[] split = csvLine.split(",");
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


}
