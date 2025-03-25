package org.esimulate.core.pojo.load;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.load.electric.ElectricLoadValue;
import org.esimulate.core.pojo.common.TimeValue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ElectricLoadValueDto implements TimeValue {

    private LocalDateTime time;

    private BigDecimal value;

    public ElectricLoadValueDto(ElectricLoadValue electricLoadValue) {
        this.time = electricLoadValue.getDatetime();
        this.value = electricLoadValue.getLoadValue();
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
