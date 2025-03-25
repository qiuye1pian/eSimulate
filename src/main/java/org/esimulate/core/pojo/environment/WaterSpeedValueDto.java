package org.esimulate.core.pojo.environment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.environment.water.WaterSpeedValue;
import org.esimulate.core.pojo.common.TimeValue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterSpeedValueDto implements TimeValue {

    private LocalDateTime time;

    private BigDecimal value;


    public WaterSpeedValueDto(WaterSpeedValue waterSpeedValue) {
        this.time = waterSpeedValue.getDatetime();
        this.value = waterSpeedValue.getValue();
    }

    public WaterSpeedValue toWaterSpeedValue() {
        WaterSpeedValue waterSpeedValue = new WaterSpeedValue();
        waterSpeedValue.setDatetime(this.time);
        waterSpeedValue.setWaterSpeed(this.value);
        waterSpeedValue.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return waterSpeedValue;
    }
}
