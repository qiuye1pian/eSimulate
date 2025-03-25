package org.esimulate.core.pojo.environment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.environment.sunlight.SunlightIrradianceValue;
import org.esimulate.core.pojo.common.TimeValue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SunlightIrradianceValueDto implements TimeValue {

    private LocalDateTime time;

    private BigDecimal value;

    public SunlightIrradianceValueDto(SunlightIrradianceValue sunlightIrradianceValue) {
        this.time = sunlightIrradianceValue.getDatetime();
        this.value = sunlightIrradianceValue.getValue();
    }

    public SunlightIrradianceValue toSunlightIrradianceValue() {
        SunlightIrradianceValue sunlightIrradianceValue = new SunlightIrradianceValue();
        sunlightIrradianceValue.setDatetime(time);
        sunlightIrradianceValue.setIrradiance(value);
        sunlightIrradianceValue.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return sunlightIrradianceValue;
    }

}
