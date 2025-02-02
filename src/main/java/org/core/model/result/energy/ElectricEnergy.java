package org.core.model.result.energy;

import lombok.Getter;
import org.core.pso.simulator.facade.result.energy.Electricity;

import java.math.BigDecimal;

@Getter
public class ElectricEnergy implements Electricity {

    BigDecimal value;

    public ElectricEnergy(BigDecimal value) {
        this.value = value;
    }

    public ElectricEnergy subtract(BigDecimal param) {
        this.value = this.value.subtract(param);
        return new ElectricEnergy(this.value);
    }

    public ElectricEnergy multiply(BigDecimal param) {
        this.value = this.value.multiply(param);
        return new ElectricEnergy(this.value);
    }

    public ElectricEnergy add(ElectricEnergy param) {
        this.value = this.value.add(param);
        return new ElectricEnergy(this.value);
    }
}
