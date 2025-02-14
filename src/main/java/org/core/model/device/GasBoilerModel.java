package org.core.model.device;

import org.core.model.result.energy.ThermalEnergy;
import org.core.pso.simulator.facade.Provider;
import org.core.pso.simulator.facade.result.energy.Energy;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class GasBoilerModel implements Provider {

    // 燃气锅炉的燃烧效率 (η_GB)
    private final BigDecimal etaGB;

    // 碳排放因子 (kg CO₂ / m³)
    private final BigDecimal emissionFactor;

    // 燃气热值 (kWh/m³)
    private final BigDecimal gasEnergyDensity;

    // 燃气锅炉出力 (kW)
    private final List<Energy> gasBoilerOutputList;

    // 燃气消耗量(m³)
    private final List<BigDecimal> gasConsumptionList;


    /**
     * 构造函数：初始化燃气锅炉参数
     *
     * @param etaGB 燃气锅炉的燃烧效率 (0~1)
     */
    public GasBoilerModel(String etaGB, BigDecimal emissionFactor, BigDecimal gasEnergyDensity) {
        this.etaGB = new BigDecimal(etaGB);
        this.emissionFactor = emissionFactor;
        this.gasEnergyDensity = gasEnergyDensity;
        this.gasBoilerOutputList = new ArrayList<>();
        this.gasConsumptionList = new ArrayList<>();
    }

    // 该方法提供能量，根据存储后的能量列表计算热能缺口，如果热能有冗余则返回零热能；
    // 如果热能有缺口，则通过燃气补充能量，并计算相应的燃气消耗。
    @Override
    public Energy provide(List<Energy> afterStorageEnergyList) {
        //能量缺口
        BigDecimal afterStorageThermalEnergy = afterStorageEnergyList.stream()
                .filter(x -> x instanceof ThermalEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);


        return getThermalEnergy(afterStorageThermalEnergy);
    }

    /**
     *
     * @param afterStorageThermalEnergy
     * @return
     */
    private @NotNull ThermalEnergy getThermalEnergy(BigDecimal afterStorageThermalEnergy) {
        BigDecimal energyGapValue = getEnergyGapValue(afterStorageThermalEnergy);
        ThermalEnergy energyGap = new ThermalEnergy(energyGapValue);
        BigDecimal gasConsumption = calculateGasConsumption(energyGap.getValue());
        this.gasBoilerOutputList.add(energyGap);
        this.gasConsumptionList.add(gasConsumption);
        return energyGap;
    }

    private static @NotNull BigDecimal getEnergyGapValue(BigDecimal afterStorageThermalEnergy) {
        BigDecimal energyGapValue;
        if (afterStorageThermalEnergy.compareTo(BigDecimal.ZERO) >= 0) {
            // 如果热能有冗余则散逸掉
            energyGapValue = BigDecimal.ZERO;
        } else {
            // 如果热能有缺口，则烧燃气补充能量
            energyGapValue = afterStorageThermalEnergy.abs();
        }
        return energyGapValue;
    }

    /**
     * 计算锅炉为了弥补热能缺口，需要消耗多少燃气 (m³)
     *
     * @param heatDeficit 热能缺口 (kW)
     * @return 燃气消耗量 (m³)
     */
    private BigDecimal calculateGasConsumption(BigDecimal heatDeficit) {

        if (heatDeficit.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO; // 没有缺口，不需要燃气
        }

        // 计算所需燃气能量 (kWh)
        BigDecimal gasEnergyNeeded = heatDeficit.divide(this.etaGB, 10, RoundingMode.HALF_UP);

        // 转换为燃气体积 (m³)
        return gasEnergyNeeded.divide(gasEnergyDensity, 10, RoundingMode.HALF_UP);

    }

    @Override
    public BigDecimal getTotalEnergy() {
        return gasBoilerOutputList.stream().map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 计算锅炉的碳排放量
     *
     * @return 碳排放量 (kg CO₂)
     */
    @Override
    public BigDecimal calculateCarbonEmissions() {
        return gasConsumptionList.stream()
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO)
                .multiply(emissionFactor);
    }
}
