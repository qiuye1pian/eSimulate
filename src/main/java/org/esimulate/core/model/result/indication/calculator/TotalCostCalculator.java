package org.esimulate.core.model.result.indication.calculator;

import org.esimulate.core.model.result.indication.TotalCost;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.Producer;
import org.esimulate.core.pso.simulator.facade.Provider;
import org.esimulate.core.pso.simulator.facade.Storage;
import org.esimulate.core.pso.simulator.facade.result.indication.Indication;

import java.util.ArrayList;
import java.util.List;

public class TotalCostCalculator {
    public static Indication calculate(List<Producer> producerList,
                                       List<Storage> storageList,
                                       List<Provider> providerList) {
        List<Device> combinedList = new ArrayList<>();
        combinedList.addAll(producerList);
        combinedList.addAll(storageList);
        combinedList.addAll(providerList);

//        BigDecimal purchaseCost = combinedList.stream()
//                .map(Device::getTotalCost)
//                .reduce(BigDecimal::add)
//                .orElse(BigDecimal.ZERO);
        return new TotalCost();
    }
}
