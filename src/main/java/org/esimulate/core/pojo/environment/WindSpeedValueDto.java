package org.esimulate.core.pojo.environment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.environment.wind.WindSpeedValue;
import org.esimulate.core.pojo.common.TimeValue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WindSpeedValueDto  implements TimeValue {

    private LocalDateTime time;

    private BigDecimal value;

    public WindSpeedValueDto(WindSpeedValue windSpeedValue) {
        this.time = windSpeedValue.getDatetime();
        this.value = windSpeedValue.getValue();
    }

    public WindSpeedValue toWindSpeedValue() {
        WindSpeedValue windSpeedValue = new WindSpeedValue();
        windSpeedValue.setDatetime(this.time);
        windSpeedValue.setWindSpeed(this.value);
        return windSpeedValue;
    }
}
