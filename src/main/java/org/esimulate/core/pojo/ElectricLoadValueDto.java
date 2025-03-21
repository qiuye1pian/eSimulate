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
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ElectricLoadValueDto {

    private LocalDateTime time;

    private BigDecimal value;

    public static List<ElectricLoadValueDto> convertByCsvContent(List<String> csvContent) {
        return csvContent.stream()
                .map(ElectricLoadValueDto::convertByCsvLine)
                .sorted(Comparator.comparing(ElectricLoadValueDto::getTime))
                .collect(Collectors.toList());
    }

    public static ElectricLoadValueDto convertByCsvLine(String csvLine) {
        String[] split = csvLine.split(",");
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
