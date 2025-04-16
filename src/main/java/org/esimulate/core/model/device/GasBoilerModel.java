package org.esimulate.core.model.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.result.energy.ThermalEnergy;
import org.esimulate.core.pojo.model.GasBoilerModelDto;
import org.esimulate.core.pojo.simulate.result.StackedChartData;
import org.esimulate.core.pso.particle.Dimension;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.Provider;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "gas_boiler_model")
@AllArgsConstructor
@NoArgsConstructor
public class GasBoilerModel extends Device implements Provider, Dimension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String modelName;

    // 燃气锅炉的燃烧效率 (η_GB)
    @Column(nullable = false)
    private BigDecimal etaGB;

    // 燃气热值 (kWh/m³)
    @Column(nullable = false)
    private BigDecimal gasEnergyDensity;

    // 碳排放因子 (kg CO₂ / m³)
    @Column(nullable = false)
    private BigDecimal carbonEmissionFactor;

    // 单位运行维护成本
    @Column(nullable = false)
    private BigDecimal cost;

    // 建设成本
    @Column(nullable = false)
    private BigDecimal purchaseCost;

    @Transient
    // 燃气锅炉出力 (kW)
    private List<Energy> gasBoilerOutputList = new ArrayList<>();

    @Transient
    // 燃气消耗量(m³)
    private List<BigDecimal> gasConsumptionList = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Transient
    BigDecimal lowerBound;

    @Transient
    BigDecimal upperBound;

    public GasBoilerModel(GasBoilerModelDto gasBoilerModelDto) {
        this.modelName = gasBoilerModelDto.getModelName();
        this.etaGB = gasBoilerModelDto.getEtaGB();
        this.gasEnergyDensity = gasBoilerModelDto.getGasEnergyDensity();
        this.carbonEmissionFactor = gasBoilerModelDto.getCarbonEmissionFactor();
        this.cost = gasBoilerModelDto.getCost();
        this.purchaseCost = gasBoilerModelDto.getPurchaseCost();
    }

    // 该方法提供能量，根据存储后的能量列表计算热能缺口，如果热能有冗余则返回；
    // 如果热能有缺口，则通过燃气补充能量，并计算相应的燃气消耗。
    @Override
    public Energy provide(List<Energy> afterStorageEnergyList) {
        //能量缺口
        BigDecimal afterStorageThermalEnergy = afterStorageEnergyList.stream()
                .filter(x -> x instanceof ThermalEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        // 没有缺口，不需要燃气
        if (afterStorageThermalEnergy.compareTo(BigDecimal.ZERO) >= 0) {
            this.gasBoilerOutputList.add(new ThermalEnergy(BigDecimal.ZERO));
            this.gasConsumptionList.add(BigDecimal.ZERO);
            return new ThermalEnergy(afterStorageThermalEnergy) ;
        }

        // 如果热能有缺口，则烧燃气补充能量
        ThermalEnergy energyGap = new ThermalEnergy(afterStorageThermalEnergy.abs());
        BigDecimal gasConsumption = calculateGasConsumption(energyGap.getValue());
        this.gasBoilerOutputList.add(energyGap);
        this.gasConsumptionList.add(gasConsumption);
        return new ThermalEnergy(BigDecimal.ZERO);
    }

    /**
     * 计算锅炉为了弥补热能缺口，需要消耗多少燃气 (m³)
     *
     * @param heatDeficit 热能缺口 (kW)
     * @return 燃气消耗量 (m³)
     */
    private BigDecimal calculateGasConsumption(BigDecimal heatDeficit) {

        // 计算所需燃气能量 (kWh)
        BigDecimal gasEnergyNeeded = heatDeficit.divide(this.etaGB, 2, RoundingMode.HALF_UP);

        // 转换为燃气体积 (m³)
        return gasEnergyNeeded.divide(gasEnergyDensity, 2, RoundingMode.HALF_UP);

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
                .multiply(this.carbonEmissionFactor);
    }


    @Override
    protected BigDecimal getDiscountRate() {
        return BigDecimal.valueOf(0.08);
    }

    @Override
    protected Integer getLifetimeYears() {
        return 20;
    }

    /**
     * 计算年度运行维护费用
     * @return 年度运行维护费用
     */
    @Override
    protected BigDecimal getCostOfOperation() {
        // 总产出值，求和，乘以设备数量，乘以单位运行维护成本
        return getTotalEnergy()
                .multiply(quantity)
                .multiply(this.cost)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 与公共电网交互费用
     * @return 燃气消耗费用
     */
    @Override
    protected BigDecimal getCostOfGrid() {
        return this.gasConsumptionList.stream()
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO)
                .multiply(BigDecimal.valueOf(2.5))
                .setScale(2, RoundingMode.HALF_UP);

    }

    @Override
    protected BigDecimal getCostOfControl() {
        return BigDecimal.ZERO;
    }

    @Override
    public List<StackedChartData> getStackedChartDataList() {
        List<BigDecimal> collect = this.gasBoilerOutputList.stream().map(Energy::getValue).collect(Collectors.toList());
        StackedChartData stackedChartData = new StackedChartData(this.modelName,collect,300);
        return Collections.singletonList(stackedChartData);
    }

    @Override
    public GasBoilerModel clone() {
        GasBoilerModel clone = (GasBoilerModel) super.clone();

        // 深拷贝可变对象字段
        clone.updatedAt = new Timestamp(this.updatedAt.getTime());
        clone.etaGB = new BigDecimal(this.etaGB.toString());
        clone.gasEnergyDensity = new BigDecimal(this.gasEnergyDensity.toString());
        clone.carbonEmissionFactor = new BigDecimal(this.carbonEmissionFactor.toString());
        clone.cost = new BigDecimal(this.cost.toString());
        clone.purchaseCost = new BigDecimal(this.purchaseCost.toString());
        clone.modelName = this.modelName;

        return clone;
    }
}
