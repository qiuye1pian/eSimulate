package org.esimulate.core.model.result.energy;

import lombok.Getter;
import org.esimulate.core.pso.simulator.facade.result.energy.Thermal;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class ThermalEnergy implements Thermal {

    BigDecimal value;

    final String energyType = "Thermal";

    final String energyTypeName = "热能";

    public ThermalEnergy(BigDecimal value) {
        this.value = value.setScale(2, RoundingMode.HALF_UP);
    }

    public ThermalEnergy subtract(BigDecimal param) {
        return new ThermalEnergy( this.value.subtract(param));
    }

    public ThermalEnergy subtract(ThermalEnergy param) {
        return subtract(param.getValue());
    }

    public ThermalEnergy multiply(BigDecimal param) {
        return new ThermalEnergy(this.value.multiply(param));
    }

    public ThermalEnergy multiply(ThermalEnergy param) {
        return multiply(param.getValue());
    }

    public ThermalEnergy add(BigDecimal param) {
        return new ThermalEnergy(this.value.add(param));
    }

    public ThermalEnergy add(ThermalEnergy param) {
        return add(param.getValue());
    }

    public ThermalEnergy divide(BigDecimal param) {
        return new ThermalEnergy(this.value.divide(param, 2, RoundingMode.HALF_UP));
    }

    public ThermalEnergy divide(ThermalEnergy param) {
        return divide(param.getValue());
    }


}
