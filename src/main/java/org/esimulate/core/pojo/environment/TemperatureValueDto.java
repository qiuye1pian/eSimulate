package org.esimulate.core.pojo.environment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.environment.temperature.TemperatureValue;
import org.esimulate.core.pojo.common.TimeValue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemperatureValueDto  implements TimeValue {

    private LocalDateTime time;

    private BigDecimal value;

    public TemperatureValueDto(TemperatureValue temperatureValue) {
        this.time = temperatureValue.getDatetime();
        this.value = temperatureValue.getValue();
    }

    public TemperatureValue toTemperatureValue() {
        TemperatureValue  temperatureValue = new TemperatureValue();
        temperatureValue.setDatetime(this.time);
        temperatureValue.setTemperature(this.value);
        return temperatureValue;
    }

}
