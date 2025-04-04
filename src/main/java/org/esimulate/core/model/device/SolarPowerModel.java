package org.esimulate.core.model.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.environment.sunlight.SunlightIrradianceValue;
import org.esimulate.core.model.environment.temperature.TemperatureValue;
import org.esimulate.core.model.result.energy.ElectricEnergy;
import org.esimulate.core.pojo.model.SolarPowerModelDto;
import org.esimulate.core.pso.simulator.facade.Producer;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentValue;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 光伏出力计算 (Java 版)
 */
@Data
@Entity
@Table(name = "solar_power_model")
@AllArgsConstructor
@NoArgsConstructor
public class SolarPowerModel implements Producer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String modelName;

    // 光伏系统额定功率 (kW)
    @Column(nullable = false)
    private BigDecimal P_pvN;

    // 光伏组件温度系数 (1/℃)，通常为负值
    @Column(nullable = false)
    private BigDecimal t_e;

    // 参考温度 (℃)
    @Column(nullable = false)
    private BigDecimal T_ref;

    // 参考辐照度 (W/m²)
    @Column(nullable = false)
    private BigDecimal G_ref;

    // 碳排放因子
    @Column(nullable = false)
    private BigDecimal carbonEmissionFactor;

    // 发电成本
    @Column(nullable = false)
    private BigDecimal cost;

    // 建设成本
    @Column(nullable = false)
    private BigDecimal purchaseCost;

    @Transient
    private BigDecimal quantity = BigDecimal.ONE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    // 每个时刻所发的电量 (kWh)
    @Transient
    private List<ElectricEnergy> electricEnergyList = new ArrayList<>();

    public SolarPowerModel(SolarPowerModelDto solarPowerModelDto) {
        this.modelName = solarPowerModelDto.getModelName();
        this.P_pvN = solarPowerModelDto.getPpvN();
        this.t_e = solarPowerModelDto.getTe();
        this.T_ref = solarPowerModelDto.getTref();
        this.G_ref = solarPowerModelDto.getGref();
        this.carbonEmissionFactor = solarPowerModelDto.getCarbonEmissionFactor();
        this.cost = solarPowerModelDto.getCost();
        this.purchaseCost = solarPowerModelDto.getPurchaseCost();
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
                .setScale(10, RoundingMode.HALF_UP)
                .multiply(this.quantity));
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
