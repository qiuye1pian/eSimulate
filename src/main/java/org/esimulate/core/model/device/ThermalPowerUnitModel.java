package org.esimulate.core.model.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.result.energy.ThermalEnergy;
import org.esimulate.core.pojo.model.ThermalPowerUnitModelDto;
import org.esimulate.core.pojo.simulate.result.StackedChartData;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.ElectricDevice;
import org.esimulate.core.pso.simulator.facade.Producer;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentValue;
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
@Table(name = "thermal_power_unit_model")
@AllArgsConstructor
@NoArgsConstructor
public class ThermalPowerUnitModel extends Device implements Producer, ElectricDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String modelName;

    // 最大出力 PMax
    @Column(nullable = false)
    private BigDecimal maxPower;

    // 最小出力 PMin
    @Column(nullable = false)
    private BigDecimal minPower;

    // 启动成本（元）
    @Column(nullable = false)
    private BigDecimal startupCost;

    // 成本函数系数 a
    @Column(nullable = false)
    private BigDecimal a;

    // 成本函数系数 b
    @Column(nullable = false)
    private BigDecimal b;

    // 成本函数系数 c
    @Column(nullable = false)
    private BigDecimal c;

    // 厂用电率 (%)
    @Column(nullable = false)
    private BigDecimal auxiliaryRate;

    // 碳排放系数（kg/kWh）
    @Column(nullable = false)
    private BigDecimal emissionRate;

    // 最小启动时间（小时）
    @Column(nullable = false)
    private int minStartupTime;

    // 最小停机时间（小时）
    @Column(nullable = false)
    private int minShutdownTime;

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

    @Transient
    // 每小时光热电站出力列表 (单位: kW)
    private List<ThermalEnergy> thermalEnergyList = new ArrayList<>();

    public ThermalPowerUnitModel(ThermalPowerUnitModelDto thermalPowerModelDto) {
        this.modelName = thermalPowerModelDto.getModelName();

        this.carbonEmissionFactor = thermalPowerModelDto.getCarbonEmissionFactor();
        this.cost = thermalPowerModelDto.getCost();
        this.purchaseCost = thermalPowerModelDto.getPurchaseCost();
    }

    @Override
    public Energy produce(List<EnvironmentValue> environmentValueList) {

        //todo:需要完善

        ThermalEnergy thermalEnergy = new ThermalEnergy(BigDecimal.ZERO);
        this.thermalEnergyList.add(thermalEnergy);
        return thermalEnergy;
    }

    @Override
    public BigDecimal getTotalEnergy() {
        return thermalEnergyList.stream()
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
        return 20;
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
        List<BigDecimal> collect = this.thermalEnergyList.stream().map(ThermalEnergy::getValue).collect(Collectors.toList());
        StackedChartData stackedChartData = new StackedChartData(this.modelName, collect, 200);
        return Collections.singletonList(stackedChartData);
    }

    @Override
    public ThermalPowerUnitModel clone() {
        ThermalPowerUnitModel clone = (ThermalPowerUnitModel) super.clone();

        // 深拷贝 BigDecimal 字段
        clone.maxPower = new BigDecimal(this.maxPower.toString());
        clone.minPower = new BigDecimal(this.minPower.toString());
        clone.startupCost = new BigDecimal(this.startupCost.toString());
        clone.a = new BigDecimal(this.a.toString());
        clone.b = new BigDecimal(this.b.toString());
        clone.c = new BigDecimal(this.c.toString());
        clone.auxiliaryRate = new BigDecimal(this.auxiliaryRate.toString());
        clone.emissionRate = new BigDecimal(this.emissionRate.toString());

        clone.carbonEmissionFactor = new BigDecimal(this.carbonEmissionFactor.toString());
        clone.cost = new BigDecimal(this.cost.toString());
        clone.purchaseCost = new BigDecimal(this.purchaseCost.toString());

        // int 类型字段直接赋值
        clone.minStartupTime = this.minStartupTime;
        clone.minShutdownTime = this.minShutdownTime;

        // 深拷贝 Timestamp
        clone.updatedAt = new Timestamp(this.updatedAt.getTime());

        // 字符串字段直接赋值（不可变类型）
        clone.modelName = this.modelName;

        // id 字段复制（如需排除可移除）
        clone.id = this.id;

        // thermalEnergyList 为 @Transient 字段，不拷贝

        return clone;
    }
}
