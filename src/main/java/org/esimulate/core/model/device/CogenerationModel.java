package org.esimulate.core.model.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.result.energy.ElectricEnergy;
import org.esimulate.core.model.result.energy.ThermalEnergy;
import org.esimulate.core.model.result.indication.calculator.NonRenewableEnergyDevice;
import org.esimulate.core.pojo.simulate.result.StackedChartData;
import org.esimulate.core.pso.particle.Dimension;
import org.esimulate.core.pso.simulator.facade.*;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentValue;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;
import org.jetbrains.annotations.TestOnly;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "hydro_power_plant_model")
@AllArgsConstructor
@NoArgsConstructor
public class CogenerationModel extends Device implements Producer, Adjustable,
        Dimension, ElectricDevice, ThermalDevice, NonRenewableEnergyDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String modelName;


    //最小供热功率 PMin (kW)
    @Column(nullable = false)
    private BigDecimal PMin;

    //最大供热功率 PMax (kW)
    @Column(nullable = false)
    private BigDecimal PMax;

    // 向上爬坡速率（单位：kW）
    @Column(nullable = false)
    private BigDecimal rampUpRate;

    // 向下爬坡速率（单位：kW）
    @Column(nullable = false)
    private BigDecimal rampDownRate;

    //发电效率
    @Column(nullable = false)
    private BigDecimal etaElectric;

    //散热损失率
    @Column(nullable = false)
    private BigDecimal etaLoss;

    //溴冷机的制热系数
    @Column(nullable = false)
    private BigDecimal COP;

    //烟气回收率
    @Column(nullable = false)
    private BigDecimal flueGasRecoveryRate;

    //天然气低热值 默认值取 9.7 kW·h/m3
    @Column(nullable = false)
    private BigDecimal gasLHV;

    //运行成本系数 a ["CNY"⋅("MW"⋅"h" )^(-1)]
    @Column(nullable = false)
    private BigDecimal a;

    //运行成本系数 b ["CNY"⋅("MW"⋅"h" )^(-1)]
    @Column(nullable = false)
    private BigDecimal b;

    //运行成本系数 c ("CNY"⋅"h" ^(-1))
    @Column(nullable = false)
    private BigDecimal c;

    // 碳排放因子
    @Column(nullable = false)
    private BigDecimal carbonEmissionFactor;

    // 天然气单价 默认值取2.5元/m3
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
    // 每小时电基础出力列表 (单位: kW)
    private List<ElectricEnergy> electricEnergyList = new ArrayList<>();

    @Transient
    // 每小时电可调出力列表 (单位: kW)
    private List<ElectricEnergy> adjustElectricEnergyList = new ArrayList<>();

    @Transient
    // 每小时热基础出力列表 (单位: kW)
    private List<ThermalEnergy> thermalEnergyList = new ArrayList<>();

    @Transient
    // 每小时热可调出力列表 (单位: kW)
    private List<ThermalEnergy> adjustThermalEnergyList = new ArrayList<>();

    @Transient
    BigDecimal lowerBound;

    @Transient
    BigDecimal upperBound;

    // 当前浮动工作功率
    @Transient
    private BigDecimal currentAdjustableThermalPower = BigDecimal.ZERO;

    @Override
    public List<Energy> produce(List<EnvironmentValue> environmentValueList) {
        //按照最小产热值生产热能：制热量 = 最小产热值
        BigDecimal heatingPower = this.PMin;
        // 排气余热量
        BigDecimal exhaustHeat = calculateExhaustHeat(heatingPower);
        // 计算生成的电能
        BigDecimal electricPower = calculateElectricPower(exhaustHeat);

        ThermalEnergy thermalEnergy = new ThermalEnergy(heatingPower);
        ElectricEnergy electricEnergy = new ElectricEnergy(electricPower);

        this.thermalEnergyList.add(thermalEnergy);
        this.electricEnergyList.add(electricEnergy);

        return Arrays.asList(thermalEnergy, electricEnergy);
    }

    /**
     * 计算排气余热量
     * 根据文档中的公式：制热量 = 排气余热量 * 制热系数 * 烟气回收率
     * 排气余热量 = 制热量 / (制热系数 * 烟气回收率）
     * @param heatingPower 制热量
     * @return 排气余热量
     */
    private BigDecimal calculateExhaustHeat(BigDecimal heatingPower) {
        return heatingPower.divide(this.COP.multiply(this.flueGasRecoveryRate), 2, RoundingMode.HALF_UP);
    }

    /**
     * 计算排气能发的电量
     *  根据公式 Q_Mt (t) = (P_Mt (t)(1-η_Mt (t)-η_L))/(η_Mt (t))
     *      电功率 * （1 - 发电效率 - 散热损失率）= 排气余热量 * 发电效率
     *      电功率 = （排气余热量 * 发电效率）/（1 - 发电效率 - 散热损失率）
     * @param exhaust 排气余热量
     * @return 发电量 即为电功率*1h
     */
    private BigDecimal calculateElectricPower(BigDecimal exhaust){
        return (exhaust.multiply(this.etaElectric))
                .divide(BigDecimal.ONE.subtract(this.etaElectric).subtract(etaLoss),2,RoundingMode.HALF_UP);
    }

    @Override
    public void adjustable(List<Energy> afterStorageEnergyList) {
        //热量缺口
        BigDecimal thermalEnergyDifference = afterStorageEnergyList.stream()
                .filter(x -> x instanceof ThermalEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        if (thermalEnergyDifference.compareTo(BigDecimal.ZERO) >= 0) {
            rampDown(BigDecimal.ZERO);
        }

        // 根据能量缺口爬坡
        adjustPower(thermalEnergyDifference);

        // 计算可调部分生产的电量
        BigDecimal exhaustHeat = calculateExhaustHeat(this.currentAdjustableThermalPower);
        BigDecimal currentAdjustableElectricPower = calculateElectricPower(exhaustHeat);

        // 记录当前时刻可调部分的热能
        ThermalEnergy currentAdjustableThermalEnergy = new ThermalEnergy(this.currentAdjustableThermalPower);
        // 记录当前时刻可调部分的电能
        ElectricEnergy currentAdjustableElectricEnergy = new ElectricEnergy(currentAdjustableElectricPower);

        this.thermalEnergyList.add(currentAdjustableThermalEnergy);
        this.electricEnergyList.add(currentAdjustableElectricEnergy);

        // 更新缺口/冗余里的热能
        afterStorageEnergyList.removeIf(x -> x instanceof ThermalEnergy);
        afterStorageEnergyList.add(new ThermalEnergy(currentAdjustableThermalPower.add(thermalEnergyDifference)));

        // 取出缺口/冗余数据里的电能
        BigDecimal electricEnergyDifference = afterStorageEnergyList.stream()
                .filter(x -> x instanceof ElectricEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        // 更新缺口/冗余里的电能
        afterStorageEnergyList.removeIf(x -> x instanceof ElectricEnergy);
        afterStorageEnergyList.add(new ElectricEnergy(electricEnergyDifference.add(currentAdjustableElectricPower)));

    }

    private void adjustPower(BigDecimal thermalEnergyDifference) {
        if (currentAdjustableThermalPower.compareTo(thermalEnergyDifference.abs()) < 0) {
            //向上爬坡
            rampUp(thermalEnergyDifference);
        } else {
            //向下爬坡
            rampDown(thermalEnergyDifference);
        }
    }

    /**
     * 向下爬坡
     * @param thermalEnergyDifference 能量缺口(-)，一定是负数或者0
     */
    private void rampDown(BigDecimal thermalEnergyDifference) {
        //计算爬坡后的数值
        BigDecimal afterRampUpRate = currentAdjustableThermalPower.subtract(rampDownRate);

        if (afterRampUpRate.compareTo(BigDecimal.ZERO) <= 0) {
            afterRampUpRate = BigDecimal.ZERO;
        }

        //如果向下爬坡之后的产热值能够大于缺口
        if (afterRampUpRate.multiply(quantity).compareTo(thermalEnergyDifference.abs()) >= 0) {
            currentAdjustableThermalPower = afterRampUpRate;
        } else {
            currentAdjustableThermalPower = thermalEnergyDifference.abs().divide(quantity, 2, RoundingMode.HALF_UP);
        }
    }

    /**
     * 向上爬坡
     *
     * @param thermalEnergyDifference 能量缺口(-)，一定是负数
     */
    private void rampUp(BigDecimal thermalEnergyDifference) {
        //计算爬坡后的数值
        BigDecimal afterRampUpRate = currentAdjustableThermalPower.add(rampUpRate);
        if (afterRampUpRate.compareTo(PMax) >= 0) {
            afterRampUpRate = PMax;
        }
        //如果爬上去之后能满足负荷
        if (afterRampUpRate.multiply(quantity)
                .compareTo(thermalEnergyDifference.abs()) > 0) {
            currentAdjustableThermalPower = thermalEnergyDifference.abs().divide(quantity, 2, RoundingMode.HALF_UP);
        } else {
            currentAdjustableThermalPower = afterRampUpRate;
        }
    }

    @Override
    public BigDecimal getTotalEnergy() {
        return null;
    }

    @Override
    public List<StackedChartData> getStackedChartDataList() {
        return Collections.emptyList();
    }


    @Override
    public BigDecimal getTotalNonRenewableEnergy() {
        return null;
    }

    @Override
    public BigDecimal calculateCarbonEmissions() {
        return null;
    }


    @Override
    public BigDecimal getAdjustTotalEnergy() {
        return null;
    }

    // 热电联产，初始投资成本: 8000元人民币/千瓦，使用年限: 30年，折现率: 6%
    @Override
    protected BigDecimal getDiscountRate() {
        return BigDecimal.valueOf(0.06);
    }

    @Override
    protected Integer getLifetimeYears() {
        return 30;
    }

    @Override
    protected BigDecimal getCostOfOperation() {
        return BigDecimal.ZERO;
    }

    @Override
    protected BigDecimal getCostOfGrid() {
        return BigDecimal.ZERO;
    }

    @Override
    protected BigDecimal getCostOfControl() {
        return null;
    }

    @TestOnly
    public void rampDownForTest(BigDecimal electricEnergyDifference){
        rampDown(electricEnergyDifference);
    }

    @TestOnly
    public void rampUpForTest(BigDecimal electricEnergyDifference){
        rampUp(electricEnergyDifference);
    }
}
