package org.esimulate.core.model.result.energy;

import lombok.Getter;
import org.esimulate.core.pso.simulator.facade.result.energy.Electricity;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;

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
        this.value = this.value.subtract(param);
        return new ElectricEnergy(this.value);
    }

    public ElectricEnergy subtract(ElectricEnergy param) {
        return subtract(param.getValue());
    }

    public ElectricEnergy multiply(BigDecimal param) {
        this.value = this.value.multiply(param);
        return new ElectricEnergy(this.value);
    }

    public ElectricEnergy multiply(ElectricEnergy param) {
        return multiply(param.getValue());
    }

    public ElectricEnergy add(BigDecimal param) {
        this.value = this.value.add(param);
        return new ElectricEnergy(this.value);
    }

    public ElectricEnergy add(ElectricEnergy param) {
        return add(param.getValue());
    }

    public ElectricEnergy divide(BigDecimal param) {
        this.value = this.value.divide(param, 2, RoundingMode.HALF_UP);
        return new ElectricEnergy(this.value);
    }

    public ElectricEnergy divide(ElectricEnergy param) {
        return divide(param.getValue());
    }

    @Override
    public Energy add(Energy energy) {
        this.value = this.value.add(energy.getValue());
        return new ElectricEnergy(this.value);
    }
}
