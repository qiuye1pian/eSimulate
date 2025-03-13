package org.esimulate.core.model.result.energy;

import lombok.Getter;
import org.esimulate.core.pso.simulator.facade.result.energy.Thermal;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class ThermalEnergy implements Thermal {

    BigDecimal value;

    public ThermalEnergy(BigDecimal value){
        this.value =  value.setScale(2, RoundingMode.HALF_UP);;
    }

}
