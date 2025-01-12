package org.core.model.result.energy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.core.pso.simulator.facade.result.energy.Thermal;

import java.math.BigDecimal;

@NoArgsConstructor
public class ThermalEnergy implements Thermal {

    @Getter
    BigDecimal value;

    public ThermalEnergy(BigDecimal value){
        this.value = value;
    }

}
