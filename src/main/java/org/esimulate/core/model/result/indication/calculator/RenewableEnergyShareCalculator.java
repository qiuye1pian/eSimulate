package org.esimulate.core.model.result.indication.calculator;

import org.esimulate.core.model.result.indication.RenewableEnergyShare;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.Producer;
import org.esimulate.core.pso.simulator.facade.Provider;
import org.esimulate.core.pso.simulator.facade.result.indication.Indication;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;

public class RenewableEnergyShareCalculator {

    /**
     * 计算可再生能源占比
     * @param producerList 生产者(可再生能源)
     * @param providerList 供应商(非可再生能源)
     * @return 可再生能源占比
     */
    @SuppressWarnings("unchecked")
    public static Indication calculate(List<Producer> producerList, List<Provider> providerList) {
        List<Device> deviceList = new java.util.ArrayList<>();
        deviceList.addAll((Collection<? extends Device>) producerList);
        deviceList.addAll((Collection<? extends Device>) providerList);

        BigDecimal cleanEnergy = deviceList.stream()
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
