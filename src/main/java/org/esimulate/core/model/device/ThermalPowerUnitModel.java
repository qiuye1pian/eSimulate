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
import org.jetbrains.annotations.TestOnly;

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

    // 启停成本（元）
    @Column(nullable = false)
    private BigDecimal startStopCost;

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

    // 向上爬坡速率（单位：MW/h）
    @Column(nullable = false)
    private BigDecimal rampUpRate;

    // 向下爬坡速率（单位：MW/h）
    @Column(nullable = false)
    private BigDecimal rampDownRate;

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

    @Transient
    private List<Integer> startStopRecordList = new ArrayList<>();

    // 状态持续时长
    @Transient
    private Integer stateDurationHours = 0;

    // 当前浮动工作功率
    @Transient
    private BigDecimal currentAdjustablePower = BigDecimal.ZERO;

    public ThermalPowerUnitModel(ThermalPowerUnitModelDto thermalPowerModelDto) {
        this.modelName = thermalPowerModelDto.getModelName();
        this.maxPower = thermalPowerModelDto.getMaxPower();
        this.minPower = thermalPowerModelDto.getMinPower();
        this.startStopCost = thermalPowerModelDto.getStartStopCost();
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
        this.rampUpRate = thermalPowerModelDto.getRampUpRate();
        this.rampDownRate = thermalPowerModelDto.getRampDownRate();
    }

    @Override
    public List<Energy> produce(List<EnvironmentValue> environmentValueList) {
        ElectricEnergy electricEnergy = (this.runningStatus == false ?
                new ElectricEnergy(BigDecimal.ZERO) :
                new ElectricEnergy(minPower))
                .multiply(quantity);

        this.electricEnergyList.add(electricEnergy);
        return Collections.singletonList(electricEnergy);
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
        return getTotalEnergy().add(getAdjustTotalEnergy())
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
        Integer startStopTimes = this.startStopRecordList.stream().reduce(Integer::sum).orElse(0);
        return this.startStopCost.multiply(BigDecimal.valueOf(startStopTimes)).multiply(quantity);
    }

    @Override
    public List<StackedChartData> getStackedChartDataList() {
        //todo:需要重写
        List<BigDecimal> collect = this.electricEnergyList.stream().map(ElectricEnergy::getValue).collect(Collectors.toList());
        StackedChartData stackedChartData = new StackedChartData(this.modelName, collect, 200);
        return Collections.singletonList(stackedChartData);
    }

    /**
     *
     * @param afterStorageEnergyList 能量 冗余/缺口，如果是缺口，值为负数
     */
    @Override
    public void adjustable(List<Energy> afterStorageEnergyList) {
        /*
         * 计算电力能量缺口/冗余值 electricEnergyDifference
         */
        BigDecimal electricEnergyDifference = afterStorageEnergyList.stream()
                .filter(x -> x instanceof ElectricEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        BigDecimal realTimePower;

        if (this.runningStatus == false) {
            realTimePower = workWithPowerOff(electricEnergyDifference);
        } else {
            realTimePower = workWithPowerOn(electricEnergyDifference);
        }

        this.adjustElectricEnergyList.add(new ElectricEnergy(realTimePower));
        /*
         * 计算剩余缺口/冗余
         */
        BigDecimal finalElectricEnergyDifference = electricEnergyDifference.subtract(realTimePower);
        afterStorageEnergyList.removeIf(x -> x instanceof ElectricEnergy);
        afterStorageEnergyList.add(new ElectricEnergy(finalElectricEnergyDifference));
    }

    /**
     *  设备是开启状态
     *    如果能量有冗余，则进入尝试关机方法，方法返回实际输出功率值
     *    如果能量有缺口，则调整当前功率
     * @param electricEnergyDifference 能量冗余(+)/缺口(-)
     * @return 实际输出功率值
     */
    private BigDecimal workWithPowerOn(BigDecimal electricEnergyDifference) {
        if (electricEnergyDifference.compareTo(BigDecimal.ZERO) >= 0) {
            return tryToTurnOff(electricEnergyDifference);
        } else {
            return adjustPower(electricEnergyDifference);
        }
    }

    /**
     *  设备是关闭状态
     *    如果能量有冗余（electricEnergyDifference 大于等于0），则返回
     *    如果能量有缺口（electricEnergyDifference 小于0），则进入尝试开机方法，方法返回实际输出功率值
     * @param electricEnergyDifference 能量冗余/缺口
     * @return 实际输出功率值
     */
    private BigDecimal workWithPowerOff(BigDecimal electricEnergyDifference) {

        if (electricEnergyDifference.compareTo(BigDecimal.ZERO) >= 0) {
            return BigDecimal.ZERO;
        } else {
            return tryToTurnOn();
        }
    }

    /**
     * 根据当前功率currentAdjustablePower计算是否满足
     *   不能满足则调用向上爬坡方法，向上爬坡方法返回实际输出功率值。
     *   能满足则调用向下爬坡方法，向下爬坡方法返回实际输出功率值。
     * @param electricEnergyDifference 能量缺口(-) 一定是负数
     * @return 实际输出功率值
     */
    private BigDecimal adjustPower(BigDecimal electricEnergyDifference) {
        if (currentAdjustablePower.compareTo(electricEnergyDifference.abs()) < 0) {
            //向上爬坡
            rampUp(electricEnergyDifference);
        } else {
            //向下爬坡
            rampDown(electricEnergyDifference);
        }
        return currentAdjustablePower;
    }

    /**
     * 向下爬坡
     * @param electricEnergyDifference 能量缺口(-)，一定是负数
     * @return 当前浮动工作功率
     */
    private BigDecimal rampDown(BigDecimal electricEnergyDifference) {
        if (currentAdjustablePower.subtract(rampDownRate).multiply(quantity)
                .compareTo(electricEnergyDifference.abs()) >= 0) {
            currentAdjustablePower = currentAdjustablePower.subtract(rampDownRate)
                    .divide(quantity, 2, RoundingMode.HALF_UP);
        } else {
            currentAdjustablePower = electricEnergyDifference.abs().divide(quantity, 2, RoundingMode.HALF_UP);
        }
        return currentAdjustablePower;
    }

    /**
     * 向上爬坡
     *
     * @param electricEnergyDifference 能量缺口(-)，一定是负数
     * @return 当前浮动工作功率
     */
    private BigDecimal rampUp(BigDecimal electricEnergyDifference) {
        if (currentAdjustablePower.add(rampUpRate).multiply(quantity)
                .compareTo(electricEnergyDifference.abs()) > 0) {
            currentAdjustablePower = electricEnergyDifference.abs().divide(quantity, 2, RoundingMode.HALF_UP);
        } else {
            currentAdjustablePower = currentAdjustablePower.add(rampUpRate)
                    .divide(quantity, 2, RoundingMode.HALF_UP);
        }
        return currentAdjustablePower;
    }

    /**
     * 尝试开机
     *
     * @return 实际输出功率值，如果成功开机，则返回最小功率值
     */
    private BigDecimal tryToTurnOn() {
        if (stateDurationHours < minShutdownTime) {
            stateDurationHours++;
            startStopRecordList.add(0);
            return BigDecimal.ZERO;
        }
        this.runningStatus = true;
        this.stateDurationHours = 0;
        this.currentAdjustablePower = BigDecimal.ZERO;
        startStopRecordList.add(1);
        return this.minPower.multiply(quantity);
    }

    /**
     * 尝试关机
     * @param electricEnergyDifference 能量冗余，一定是正数或者0
     * @return 实际输出功率值，如果成功关机，则返回0，如果没有关机成功，向下爬坡
     */
    private BigDecimal tryToTurnOff(BigDecimal electricEnergyDifference){
        if(this.stateDurationHours < minStartupTime){
            stateDurationHours++;
            startStopRecordList.add(0);
            return rampDown(electricEnergyDifference);
        }
        this.runningStatus = false;
        this.stateDurationHours = 0;
        this.currentAdjustablePower = BigDecimal.ZERO;
        startStopRecordList.add(1);
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getAdjustTotalEnergy() {
        return adjustElectricEnergyList.stream()
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public ThermalPowerUnitModel clone() {
        ThermalPowerUnitModel clone = (ThermalPowerUnitModel) super.clone();

        // 深拷贝 BigDecimal 字段
        clone.maxPower = new BigDecimal(this.maxPower.toString());
        clone.minPower = new BigDecimal(this.minPower.toString());
        clone.startStopCost = new BigDecimal(this.startStopCost.toString());
        clone.a = new BigDecimal(this.a.toString());
        clone.b = new BigDecimal(this.b.toString());
        clone.c = new BigDecimal(this.c.toString());
        clone.auxiliaryRate = new BigDecimal(this.auxiliaryRate.toString());
        clone.emissionRate = new BigDecimal(this.emissionRate.toString());

        clone.carbonEmissionFactor = new BigDecimal(this.carbonEmissionFactor.toString());
        clone.cost = new BigDecimal(this.cost.toString());
        clone.purchaseCost = new BigDecimal(this.purchaseCost.toString());

        clone.rampUpRate = new BigDecimal(this.rampUpRate.toString());
        clone.rampDownRate = new BigDecimal(this.rampDownRate.toString());

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

    @TestOnly
    public BigDecimal rampDownForTest(BigDecimal electricEnergyDifference){
        return rampDown(electricEnergyDifference);
    }

    @TestOnly
    public BigDecimal rampUpForTest(BigDecimal electricEnergyDifference){
        return rampUp(electricEnergyDifference);
    }
}
