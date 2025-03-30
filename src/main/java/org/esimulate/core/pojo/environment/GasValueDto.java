package org.esimulate.core.pojo.environment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.environment.gas.GasValue;
import org.esimulate.core.pojo.common.TimeValue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GasValueDto implements TimeValue {

    private LocalDateTime time;

    private BigDecimal value;

    public GasValueDto(GasValue gasValue) {
        this.time = gasValue.getDatetime();
        this.value = gasValue.getValue();
    }

    public GasValue toGasValue() {
        GasValue gasValue = new GasValue();
        gasValue.setDatetime(this.time);
        gasValue.setGasSupply(this.value);
        return gasValue;
    }
}
