package org.esimulate.core.model.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.result.energy.ElectricEnergy;
import org.esimulate.core.pojo.model.ThermalPowerUnitModelDto;
import org.esimulate.core.pojo.simulate.result.StackedChartData;
import org.esimulate.core.pso.simulator.facade.Adjustable;
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
public class ThermalPowerUnitModel extends Device implements Producer, Adjustable, ElectricDevice {

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

    // 当前运行状态：true 表示运行中，false 表示停机
    @Column(nullable = false)
    private Boolean runningStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Transient
    // 每小时火电站基础出力列表 (单位: kW)
    private List<ElectricEnergy> electricEnergyList = new ArrayList<>();

    @Transient
    // 每小时火电站可调出力列表 (单位: kW)
    private List<ElectricEnergy> adjustElectricEnergyList = new ArrayList<>();

    // 状态持续时长
    @Transient
    private Integer stateDurationHours = 0;

    public ThermalPowerUnitModel(ThermalPowerUnitModelDto thermalPowerModelDto) {
        this.modelName = thermalPowerModelDto.getModelName();
        this.maxPower = thermalPowerModelDto.getMaxPower();
        this.minPower = thermalPowerModelDto.getMinPower();
        this.startupCost = thermalPowerModelDto.getStartupCost();
        this.a = thermalPowerModelDto.getA();
        this.b = thermalPowerModelDto.getB();
        this.c = thermalPowerModelDto.getC();
        this.auxiliaryRate = thermalPowerModelDto.getAuxiliaryRate();
        this.emissionRate = thermalPowerModelDto.getEmissionRate();
        this.minStartupTime = thermalPowerModelDto.getMinStartupTime();
        this.minShutdownTime = thermalPowerModelDto.getMinShutdownTime();
        this.runningStatus = thermalPowerModelDto.getRunningStatus();
        this.carbonEmissionFactor = thermalPowerModelDto.getCarbonEmissionFactor();
        this.cost = thermalPowerModelDto.getCost();
        this.purchaseCost = thermalPowerModelDto.getPurchaseCost();
    }

    @Override
    public Energy produce(List<EnvironmentValue> environmentValueList) {
        ElectricEnergy electricEnergy = this.runningStatus == false ?
                new ElectricEnergy(BigDecimal.ZERO) :
                new ElectricEnergy(minPower);
        this.electricEnergyList.add(electricEnergy);
        return electricEnergy;
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
        List<BigDecimal> collect = this.electricEnergyList.stream().map(ElectricEnergy::getValue).collect(Collectors.toList());
        StackedChartData stackedChartData = new StackedChartData(this.modelName, collect, 200);
        return Collections.singletonList(stackedChartData);
    }

    @Override
    public Energy adjustable(List<Energy> afterStorageEnergyList) {
        BigDecimal electricEnergyDifference = afterStorageEnergyList.stream()
                .filter(x -> x instanceof ElectricEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
        if (electricEnergyDifference.compareTo(BigDecimal.ZERO) > 0) {
            // 如果有能量缺口
        }

        return null;
    }

    @Override
    public BigDecimal getAdjustTotalEnergy() {
        return null;
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
