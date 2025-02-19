package org.core.model.device;

import lombok.Data;
import org.core.model.environment.sunlight.SunlightIrradianceValue;
import org.core.model.environment.temperature.TemperatureValue;
import org.core.model.result.energy.ElectricEnergy;
import org.core.pso.simulator.facade.Producer;
import org.core.pso.simulator.facade.environment.EnvironmentValue;
import org.core.pso.simulator.facade.result.energy.Energy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 光伏出力计算 (Java 版)
 */
@Data
public class SolarPowerModel implements Producer {

    // 光伏系统额定功率 (kW)
    private final BigDecimal P_pvN;

    // 光伏组件温度系数 (1/℃)，通常为负值
    private final BigDecimal t_e;

    // 参考温度 (℃)
    private final BigDecimal T_ref;

    // 参考辐照度 (W/m²)
    private final BigDecimal G_ref;

    // 每个时刻所法的电量 (kWh)
    private final List<ElectricEnergy> electricEnergyList;

    /**
     * 构造函数，初始化光伏系统参数
     *
     * @param P_pvN 光伏系统额定功率 (kW)
     * @param t_e   温度系数 (1/℃)
     * @param T_ref 参考温度 (℃)
     * @param G_ref 参考辐照度 (W/m²)
     */
    public SolarPowerModel(String P_pvN, String t_e, String T_ref, String G_ref) {
        this.P_pvN = new BigDecimal(P_pvN);
        this.t_e = new BigDecimal(t_e);
        this.T_ref = new BigDecimal(T_ref);
        this.G_ref = new BigDecimal(G_ref);
        this.electricEnergyList = new ArrayList<>();
    }

    /**
     * 计算 t 时刻光伏电站的出力 P_pv(t)
     *
     * @param currentTemperature 实际环境温度 (°C)
     * @param currentIrradiance  t 时刻的太阳辐照强度 (W/m²)
     * @return 计算得到的光伏输出功率 (kW)
     */
    private Energy calculatePower(BigDecimal currentTemperature, BigDecimal currentIrradiance) {

        // 计算温度影响部分: (1 + t_e * (T_e - T_ref))
        BigDecimal temperatureEffect = t_e.multiply(currentTemperature.subtract(T_ref));
        BigDecimal temperatureFactor = BigDecimal.ONE.add(temperatureEffect);

        // 计算辐照度比例: (G_T / G_ref)
        BigDecimal irradianceRatio = currentIrradiance.divide(G_ref, 10, RoundingMode.HALF_UP);

        // 计算最终光伏出力
        return new ElectricEnergy(P_pvN
                .multiply(temperatureFactor)
                .multiply(irradianceRatio)
                .setScale(10, RoundingMode.HALF_UP));
    }

    @Override
    public Energy produce(List<EnvironmentValue> environmentValueList) {
        BigDecimal sunlight = environmentValueList.stream()
                .filter(x -> x instanceof SunlightIrradianceValue)
                .map(EnvironmentValue::getValue)
                .findAny()
                .orElse(BigDecimal.ZERO);

        BigDecimal temperature = environmentValueList.stream()
                .filter(x -> x instanceof TemperatureValue)
                .map(EnvironmentValue::getValue)
                .findAny()
                .orElse(BigDecimal.ZERO);

        Energy energy = calculatePower(temperature, sunlight);
        this.electricEnergyList.add((ElectricEnergy) energy);
        return energy;
    }

    @Override
    public BigDecimal getTotalEnergy() {
        return electricEnergyList.stream()
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal calculateCarbonEmissions() {
        return BigDecimal.ZERO;
    }


}
