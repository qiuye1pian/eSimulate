package org.core.model.device;

import org.core.model.result.energy.ThermalEnergy;
import org.core.pso.simulator.facade.Storage;
import org.core.pso.simulator.facade.result.energy.Energy;

import java.math.BigDecimal;
import java.util.List;

public class ThermalSaverModel implements Storage {

    @Override
    public Energy storage(List<Energy> differenceList) {

        BigDecimal thermalEnergyDifference = differenceList.stream()
                .filter(x -> x instanceof ThermalEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        //目前没有设计储热模型
        return new ThermalEnergy(thermalEnergyDifference);
    }

    @Override
    public BigDecimal calculateCarbonEmissions() {
        return BigDecimal.ZERO;
    }
}
