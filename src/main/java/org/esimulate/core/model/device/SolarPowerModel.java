package org.esimulate.core.model.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.environment.sunlight.SunlightIrradianceValue;
import org.esimulate.core.model.environment.temperature.TemperatureValue;
import org.esimulate.core.model.result.energy.ElectricEnergy;
import org.esimulate.core.pojo.model.SolarPowerModelDto;
import org.esimulate.core.pso.particle.Dimension;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.ElectricDevice;
import org.esimulate.core.pso.simulator.facade.Producer;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentValue;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;
import org.esimulate.core.pojo.simulate.result.StackedChartData;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 光伏出力计算 (Java 版)
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "solar_power_model")
@AllArgsConstructor
@NoArgsConstructor
public class SolarPowerModel extends Device implements Producer, Dimension, ElectricDevice {

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

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    // 每个时刻所发的电量 (kWh)
    @Transient
    private List<ElectricEnergy> electricEnergyList = new ArrayList<>();

    @Transient
    BigDecimal lowerBound;

    @Transient
    BigDecimal upperBound;

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
                .multiply(this.quantity)
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


    @Override
    protected BigDecimal getDiscountRate() {
        return BigDecimal.valueOf(0.07);
    }

    @Override
    protected Integer getLifetimeYears() {
        return 25;
    }

    @Override
    protected BigDecimal getCostOfOperation() {
        return getTotalEnergy()
                .multiply(quantity)
                .multiply(cost)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    protected BigDecimal getCostOfGrid() {
        return BigDecimal.ZERO;
    }

    @Override
    protected BigDecimal getCostOfControl() {
        return BigDecimal.ZERO;
    }

    @Override
    public List<StackedChartData> getStackedChartDataList() {
        List<BigDecimal> collect = this.electricEnergyList.stream().map(ElectricEnergy::getValue).collect(Collectors.toList());
        StackedChartData stackedChartData = new StackedChartData(this.modelName, collect, 500);
        return Collections.singletonList(stackedChartData);
    }

    @Override
    public SolarPowerModel clone() {
        SolarPowerModel clone = (SolarPowerModel) super.clone();

        // 深拷贝 BigDecimal 类型字段
        clone.P_pvN = new BigDecimal(this.P_pvN.toString());
        clone.t_e = new BigDecimal(this.t_e.toString());
        clone.T_ref = new BigDecimal(this.T_ref.toString());
        clone.G_ref = new BigDecimal(this.G_ref.toString());
        clone.carbonEmissionFactor = new BigDecimal(this.carbonEmissionFactor.toString());
        clone.cost = new BigDecimal(this.cost.toString());
        clone.purchaseCost = new BigDecimal(this.purchaseCost.toString());

        // 深拷贝 Timestamp
        clone.updatedAt = new Timestamp(this.updatedAt.getTime());

        // 字符串字段直接赋值（不可变类型）
        clone.modelName = this.modelName;

        // id 字段直接复制（若不希望保留可移除）
        clone.id = this.id;

        // electricEnergyList 不拷贝（@Transient）

        return clone;
    }
}
