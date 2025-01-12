package org.core.model.result.energy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.core.pso.simulator.facade.result.energy.Electricity;

import java.math.BigDecimal;

@NoArgsConstructor
public class ElectricEnergy implements Electricity {

    @Getter
    BigDecimal value;

    public ElectricEnergy(BigDecimal value) {
        this.value = value;
    }
}
