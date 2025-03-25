package org.esimulate.core.pojo.load;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.load.heat.ThermalLoadValue;
import org.esimulate.core.pojo.common.TimeValue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThermalLoadValueDto implements TimeValue {

    private LocalDateTime time;

    private BigDecimal value;

    public ThermalLoadValueDto(ThermalLoadValue thermalLoadValue) {
        this.time = thermalLoadValue.getDatetime();
        this.value = thermalLoadValue.getLoadValue();
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
