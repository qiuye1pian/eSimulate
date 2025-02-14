package org.core.model.result.indication.calculator;

import org.core.model.result.indication.RenewableEnergyShare;
import org.core.pso.simulator.facade.Producer;
import org.core.pso.simulator.facade.Provider;
import org.core.pso.simulator.facade.result.indication.Indication;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class RenewableEnergyShareCalculator {

    /**
     * 计算可再生能源占比
     * @param producerList 生产者(可再生能源)
     * @param providerList 供应商(非可再生能源)
     * @return 可再生能源占比
     */
    public static Indication calculate(List<Producer> producerList, List<Provider> providerList) {

        BigDecimal cleanEnergy = producerList.stream()
                .map(Producer::getTotalEnergy)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        BigDecimal nonCleanEnergy = providerList.stream()
                .map(Provider::getTotalEnergy)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        BigDecimal totalEnergy = cleanEnergy.add(nonCleanEnergy);

        BigDecimal share = cleanEnergy.divide(totalEnergy, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        return new RenewableEnergyShare(share);
    }
}
