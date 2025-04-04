package org.esimulate.core.model.result.indication.calculator;

import org.esimulate.core.model.result.indication.TotalCost;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.Producer;
import org.esimulate.core.pso.simulator.facade.Provider;
import org.esimulate.core.pso.simulator.facade.Storage;
import org.esimulate.core.pso.simulator.facade.result.indication.Indication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TotalCostCalculator {
    public static Indication calculate(List<Producer> producerList,
                                       List<Storage> storageList,
                                       List<Provider> providerList) {
        List<Device> combinedList = new ArrayList<>();
        combinedList.addAll(producerList.stream().map(x -> (Device) x).collect(Collectors.toList()));
        combinedList.addAll(storageList.stream().map(x -> (Device) x).collect(Collectors.toList()));
        combinedList.addAll(providerList.stream().map(x -> (Device) x).collect(Collectors.toList()));

        BigDecimal purchaseCost = combinedList.stream()
                .map(Device::getTotalCost)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        return new TotalCost(purchaseCost);
    }
}
