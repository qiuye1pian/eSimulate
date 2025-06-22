package org.esimulate.core.model.result.indication.calculator;

import org.esimulate.core.model.result.indication.TotalCost;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.result.indication.Indication;

import java.math.BigDecimal;
import java.util.List;

public class TotalCostCalculator {

    public static Indication calculate(List<Device> deviceList) {
        BigDecimal totalCost = deviceList.stream()
                .map(Device::getTotalCost)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        return new TotalCost(totalCost);
    }
}
