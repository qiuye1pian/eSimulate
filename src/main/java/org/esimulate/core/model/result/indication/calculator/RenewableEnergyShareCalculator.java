package org.esimulate.core.model.result.indication.calculator;

import org.esimulate.core.model.result.indication.RenewableEnergyShare;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.Producer;
import org.esimulate.core.pso.simulator.facade.Provider;
import org.esimulate.core.pso.simulator.facade.result.indication.Indication;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class RenewableEnergyShareCalculator {

    /**
     * 计算可再生能源占比
     * @param producerList 生产者(可再生能源)
     * @param providerList 供应商(非可再生能源)
     * @return 可再生能源占比
     */
    public static Indication calculate(List<Producer> producerList, List<Provider> providerList) {
        List<Device> combinedList = new java.util.ArrayList<>();
        combinedList.addAll(producerList.stream().map(x -> (Device) x).collect(Collectors.toList()));
        combinedList.addAll(providerList.stream().map(x -> (Device) x).collect(Collectors.toList()));

        BigDecimal cleanEnergy = combinedList.stream()
                .filter(x -> x instanceof RenewableEnergyDevice)
                .map(x -> (RenewableEnergyDevice) x)
                .map(RenewableEnergyDevice::getTotalRenewableEnergy)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        BigDecimal nonCleanEnergy = providerList.stream()
                .filter(x->x instanceof NonRenewableEnergyDevice)
                .map(x->(NonRenewableEnergyDevice)x)
                .map(NonRenewableEnergyDevice::getTotalNonRenewableEnergy)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        BigDecimal totalEnergy = cleanEnergy.add(nonCleanEnergy);

        BigDecimal share = cleanEnergy.divide(totalEnergy, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        return new RenewableEnergyShare(share);
    }
}
