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

    public ThermalEnergy subtract(BigDecimal param) {
        this.value = this.value.subtract(param);
        return new ThermalEnergy(this.value);
    }

    public ThermalEnergy subtract(ThermalEnergy param) {
        return subtract(param.getValue());
    }

    public ThermalEnergy multiply(BigDecimal param) {
        this.value = this.value.multiply(param);
        return new ThermalEnergy(this.value);
    }

    public ThermalEnergy multiply(ThermalEnergy param) {
        return multiply(param.getValue());
    }

    public ThermalEnergy add(BigDecimal param) {
        this.value = this.value.add(param);
        return new ThermalEnergy(this.value);
    }

    public ThermalEnergy add(ThermalEnergy param) {
        return add(param.getValue());
    }

    public ThermalEnergy divide(BigDecimal param) {
        this.value = this.value.divide(param, 2, RoundingMode.HALF_UP);
        return new ThermalEnergy(this.value);
    }

    public ThermalEnergy divide(ThermalEnergy param) {
        return divide(param.getValue());
    }

    public Energy add(Energy energy) {
        this.value = this.value.add(energy.getValue());
        return new ThermalEnergy(this.value);
    }
}
