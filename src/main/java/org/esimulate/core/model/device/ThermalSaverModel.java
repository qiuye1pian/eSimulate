package org.esimulate.core.model.device;

import lombok.Data;
import org.esimulate.core.model.result.energy.ThermalEnergy;
import org.esimulate.core.pso.simulator.facade.Storage;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ThermalSaverModel implements Storage, Device {

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
