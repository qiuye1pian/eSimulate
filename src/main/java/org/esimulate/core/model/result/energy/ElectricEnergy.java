package org.esimulate.core.model.result.energy;

import lombok.Getter;
import org.esimulate.core.pso.simulator.facade.result.energy.Electricity;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class ElectricEnergy implements Electricity {

    BigDecimal value;

    final String energyType = "Electricity";

    final String energyTypeName = "电能";

    public ElectricEnergy(BigDecimal value) {
        this.value = value.setScale(2, RoundingMode.HALF_UP);
    }

    public ElectricEnergy subtract(BigDecimal param) {
        return new ElectricEnergy(this.value.subtract(param));
    }

    public ElectricEnergy subtract(ElectricEnergy param) {
        return subtract(param.getValue());
    }

    public ElectricEnergy multiply(BigDecimal param) {
        return new ElectricEnergy(this.value.multiply(param));
    }

    public ElectricEnergy multiply(ElectricEnergy param) {
        return multiply(param.getValue());
    }

    public ElectricEnergy add(BigDecimal param) {
        return new ElectricEnergy(this.value.add(param));
    }

    public ElectricEnergy add(ElectricEnergy param) {
        return add(param.getValue());
    }

    public ElectricEnergy divide(BigDecimal param) {
        return new ElectricEnergy(this.value.divide(param, 2, RoundingMode.HALF_UP));
    }

    public ElectricEnergy divide(ElectricEnergy param) {
        return divide(param.getValue());
    }

}
