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
}
