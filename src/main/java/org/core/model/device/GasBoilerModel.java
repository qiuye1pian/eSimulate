package org.core.model.device;

import org.core.model.result.energy.ThermalEnergy;
import org.core.pso.simulator.facade.Provider;
import org.core.pso.simulator.facade.result.energy.Energy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GasBoilerModel implements Provider {

    // 燃气锅炉的燃烧效率 (η_GB)
    private final BigDecimal etaGB;

    // 燃气锅炉出力 (kW)
    private final List<Energy> gasBoilerOutputList;

    /**
     * 构造函数：初始化燃气锅炉参数
     *
     * @param etaGB 燃气锅炉的燃烧效率 (0~1)
     */
    public GasBoilerModel(String etaGB) {
        this.etaGB = new BigDecimal(etaGB);
        this.gasBoilerOutputList = new ArrayList<>();
    }

    /**
     * 计算天然气消耗成本 F^GB。
     *
     * @param C_CH4     单位天然气价格 (元/kWh)
     * @param H_GB_list 燃气锅炉的供热功率列表 (kW)
     * @return 天然气消耗总成本
     */
    public BigDecimal calculateGasCost(BigDecimal C_CH4, List<BigDecimal> H_GB_list) {

        // 计算天然气消耗量
        return H_GB_list.stream()
                .map(H_GB -> C_CH4.multiply(H_GB.divide(etaGB, 10, RoundingMode.HALF_UP)))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 获取燃气锅炉出力 (kW)
     *
     * @return thermalPowerList
     */
    public List<BigDecimal> gasBoilerOutputList() {
        return gasBoilerOutputList.stream().map(Energy::getValue).collect(Collectors.toList());
    }


    @Override
    public Energy provide(List<Energy> afterStorageEnergyList) {

        //能量缺口
        BigDecimal afterStorageThermalEnergy = afterStorageEnergyList.stream()
                .filter(x -> x instanceof ThermalEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        // 如果热能还有缺口
        if (afterStorageThermalEnergy.compareTo(BigDecimal.ZERO) > 0) {
            return new ThermalEnergy(afterStorageThermalEnergy);
        }

        ThermalEnergy thermalEnergy = new ThermalEnergy(BigDecimal.ZERO);

        gasBoilerOutputList.add(thermalEnergy);

        return thermalEnergy;
    }

    @Override
    public BigDecimal getTotalEnergy() {
        return gasBoilerOutputList.stream().map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal calculateCarbonEmissions() {
        //TODO:计算碳排放
        return getTotalEnergy().multiply(new BigDecimal("0.222"));
    }
}
