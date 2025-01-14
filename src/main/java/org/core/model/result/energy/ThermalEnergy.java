package org.core.model.result.energy;

import lombok.Getter;
import org.core.pso.simulator.facade.result.energy.Thermal;

import java.math.BigDecimal;

@Getter
public class ThermalEnergy implements Thermal {

    BigDecimal value;

    public ThermalEnergy(BigDecimal value){
        this.value = value;
    }

}
