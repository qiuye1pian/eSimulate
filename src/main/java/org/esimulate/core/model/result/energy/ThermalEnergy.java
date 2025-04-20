package org.esimulate.core.model.result.energy;

import lombok.Getter;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;
import org.esimulate.core.pso.simulator.facade.result.energy.Thermal;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class ThermalEnergy implements Thermal {

    BigDecimal value;

    final String energyType = "Thermal";

    final String energyTypeName = "热能";

    public ThermalEnergy(BigDecimal value){
        this.value =  value.setScale(2, RoundingMode.HALF_UP);;
    }

    public ThermalEnergy add(BigDecimal param) {
        return new ThermalEnergy(this.value.add(param));
    }

    public Energy add(Energy energy) {
        return this.add(energy.getValue());
    }
}
