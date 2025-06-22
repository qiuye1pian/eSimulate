package org.esimulate.core.model.result.indication.calculator;

import org.esimulate.core.model.result.indication.RenewableEnergyShare;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.result.indication.Indication;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class RenewableEnergyShareCalculator {

    /**
     * 计算可再生能源占比
     *
     * @param deviceList 设备列表
     * @return 可再生能源占比
     */
    public static Indication calculate(List<Device> deviceList) {

        BigDecimal cleanEnergy = deviceList.stream()
                .filter(x -> x instanceof RenewableEnergyDevice)
                .map(x -> (RenewableEnergyDevice) x)
                .map(RenewableEnergyDevice::getTotalRenewableEnergy)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        BigDecimal nonCleanEnergy = deviceList.stream()
                .filter(x -> x instanceof NonRenewableEnergyDevice)
                .map(x -> (NonRenewableEnergyDevice) x)
                .map(NonRenewableEnergyDevice::getTotalNonRenewableEnergy)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        BigDecimal totalEnergy = cleanEnergy.add(nonCleanEnergy);
        if (totalEnergy.compareTo(BigDecimal.ZERO) == 0) {
            return new RenewableEnergyShare(BigDecimal.ZERO);
        }

        BigDecimal share = cleanEnergy.divide(totalEnergy, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        return new RenewableEnergyShare(share);
    }
}
