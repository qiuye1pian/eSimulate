package org.esimulate.core.model.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.esimulate.core.model.result.energy.ElectricEnergy;
import org.esimulate.core.model.result.energy.ThermalEnergy;
import org.esimulate.core.model.result.indication.calculator.NonRenewableEnergyDevice;
import org.esimulate.core.pojo.model.CogenerationModelDto;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "cogeneration_model")
@AllArgsConstructor
@NoArgsConstructor
public class CogenerationModel extends Device implements Producer, Adjustable,
        Dimension, ElectricDevice, ThermalDevice, NonRenewableEnergyDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String modelName;

    // 最小供热功率 PMin (kW)
    @Column(nullable = false)
    private BigDecimal PMin;

    // 最大供热功率 PMax (kW)
    @Column(nullable = false)
    private BigDecimal PMax;

    // 向上爬坡速率（单位：kW）
    @Column(nullable = false)
    private BigDecimal rampUpRate;

    // 向下爬坡速率（单位：kW）
    @Column(nullable = false)
    private BigDecimal rampDownRate;

    // 发电效率
    @Column(nullable = false)
    private BigDecimal etaElectric;

    // 散热损失率
    @Column(nullable = false)
    private BigDecimal etaLoss;

    // 溴冷机的制热系数
    @Column(nullable = false)
    private BigDecimal COP;

    // 烟气回收率
    @Column(nullable = false)
    private BigDecimal flueGasRecoveryRate;

    // 运行成本系数 a ["CNY"⋅("MW"⋅"h" )^(-1)]
    @Column(nullable = false)
    private BigDecimal a;

    // 运行成本系数 b ["CNY"⋅("MW"⋅"h" )^(-1)]
    @Column(nullable = false)
    private BigDecimal b;

    // 运行成本系数 c ("CNY"⋅"h" ^(-1))
    @Column(nullable = false)
    private BigDecimal c;

    // Cv
    @Column(nullable = false)
    private BigDecimal cv;

    // 碳排放因子
    @Column(nullable = false)
    private BigDecimal carbonEmissionFactor;

    // 单位成本
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
    Integer lowerBound;

    @Transient
    Integer upperBound;

    // 当前浮动工作功率
    @Transient
    private BigDecimal currentAdjustableThermalPower = BigDecimal.ZERO;

    public CogenerationModel(CogenerationModelDto cogenerationModelDto) {
        this.id = cogenerationModelDto.getId();
        this.modelName = cogenerationModelDto.getModelName();
        this.PMin = cogenerationModelDto.getPMin();
        this.PMax = cogenerationModelDto.getPMax();
        this.rampUpRate = cogenerationModelDto.getRampUpRate();
        this.rampDownRate = cogenerationModelDto.getRampDownRate();
        this.etaElectric = cogenerationModelDto.getEtaElectric();
        this.etaLoss = cogenerationModelDto.getEtaLoss();
        this.COP = cogenerationModelDto.getCOP();
        this.flueGasRecoveryRate = cogenerationModelDto.getFlueGasRecoveryRate();
        this.a = cogenerationModelDto.getA();
        this.b = cogenerationModelDto.getB();
        this.c = cogenerationModelDto.getC();
        this.cv = cogenerationModelDto.getCv();
        this.carbonEmissionFactor = cogenerationModelDto.getCarbonEmissionFactor();
        this.cost = cogenerationModelDto.getCost();
        this.purchaseCost = cogenerationModelDto.getPurchaseCost();
    }

    @Override
    public List<Energy> produce(List<EnvironmentValue> environmentValueList) {
        //按照最小产热值生产热能：制热量 = 最小产热值
        BigDecimal heatingPower = this.PMin;
        // 排气余热量
        BigDecimal exhaustHeat = calculateExhaustHeat(heatingPower);
        // 计算生成的电能
        BigDecimal electricPower = calculateElectricPower(exhaustHeat);

        //计算总数量下生产的能量
        ThermalEnergy thermalEnergy = new ThermalEnergy(heatingPower.multiply(quantity));
        ElectricEnergy electricEnergy = new ElectricEnergy(electricPower.multiply(quantity));

        log.debug("固定产热:{}", thermalEnergy.getValue());
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
    public List<Energy> adjustable(List<Energy> afterStorageEnergyList) {
        //热量缺口
        BigDecimal thermalEnergyDifference = afterStorageEnergyList.stream()
                .filter(x -> x instanceof ThermalEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
        log.debug("热缺口 in :{}", thermalEnergyDifference);

        //扩容
        this.PMax = this.PMax.multiply(quantity);
        this.PMin = this.PMin.multiply(quantity);
        this.rampUpRate = this.rampUpRate.multiply(quantity);
        this.rampDownRate = this.rampDownRate.multiply(quantity);
        this.currentAdjustableThermalPower = this.currentAdjustableThermalPower.multiply(quantity);

        //如果能量有冗余，向下爬坡
        if (thermalEnergyDifference.compareTo(BigDecimal.ZERO) >= 0) {
            log.debug("热冗余，向下爬坡");
            rampDown(thermalEnergyDifference);
        } else {
            // 如果能量有缺口，根据能量缺口和爬坡能力爬坡
            adjustPower(thermalEnergyDifference);
        }


        /*
          计算可调部分生产的电量
          先计算排气余热量 exhaustHeat
          根据排气余热量计算生产电量 currentAdjustableElectricPower
         */
        BigDecimal exhaustHeat = calculateExhaustHeat(this.currentAdjustableThermalPower);
        BigDecimal currentAdjustableElectricPower = calculateElectricPower(exhaustHeat);

        // 记录当前时刻可调部分的热能
        ThermalEnergy currentAdjustableThermalEnergy = new ThermalEnergy(this.currentAdjustableThermalPower);
        // 记录当前时刻可调部分的电能
        ElectricEnergy currentAdjustableElectricEnergy = new ElectricEnergy(currentAdjustableElectricPower);

        this.adjustThermalEnergyList.add(currentAdjustableThermalEnergy);
        this.adjustElectricEnergyList.add(currentAdjustableElectricEnergy);

        // 更新缺口/冗余里的热能
        afterStorageEnergyList.removeIf(x -> x instanceof ThermalEnergy);
        ThermalEnergy out = new ThermalEnergy(currentAdjustableThermalPower.add(thermalEnergyDifference));
        afterStorageEnergyList.add(out);

        // 取出缺口/冗余数据里的电能
        BigDecimal electricEnergyDifference = afterStorageEnergyList.stream()
                .filter(x -> x instanceof ElectricEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        // 更新缺口/冗余里的电能
        afterStorageEnergyList.removeIf(x -> x instanceof ElectricEnergy);
        afterStorageEnergyList.add(new ElectricEnergy(currentAdjustableElectricPower.add(electricEnergyDifference)));

        log.debug("当前产热 current :{}", currentAdjustableThermalPower);
        log.debug("热缺口 out :{}", out.getValue());

        //缩容
        this.PMax = this.PMax.divide(quantity,2,RoundingMode.HALF_UP);
        this.PMin = this.PMin.divide(quantity,2,RoundingMode.HALF_UP);
        this.rampUpRate = this.rampUpRate.divide(quantity,2,RoundingMode.HALF_UP);
        this.rampDownRate = this.rampDownRate.divide(quantity,2,RoundingMode.HALF_UP);
        this.currentAdjustableThermalPower = this.currentAdjustableThermalPower.divide(quantity,2,RoundingMode.HALF_UP);
        return afterStorageEnergyList;
    }

    /**
     * 根据能量缺口爬坡
     * @param thermalEnergyDifference 能量缺口，只会小于0
     */
    private void adjustPower(BigDecimal thermalEnergyDifference) {
        if (currentAdjustableThermalPower.compareTo(thermalEnergyDifference.abs()) < 0) {
            //向上爬坡
            log.debug("向上爬坡");
            rampUp(thermalEnergyDifference);
        } else {
            //向下爬坡
            log.debug("向下爬坡");
            rampDown(thermalEnergyDifference);
        }
    }

    /**
     * 向下爬坡
     * @param thermalEnergyDifference 能量缺口(-)，一定是负数或者0
     */
    private void rampDown(BigDecimal thermalEnergyDifference) {
        //计算爬坡后的数值
        log.debug("向下爬坡前功率:{}", currentAdjustableThermalPower);
        BigDecimal afterRampUpRate = currentAdjustableThermalPower.subtract(rampDownRate);

        if (afterRampUpRate.compareTo(BigDecimal.ZERO) <= 0) {
            log.debug("小于0，停止爬坡");
            afterRampUpRate = BigDecimal.ZERO;
        }

        //如果向下爬坡之后的产热值能够大于缺口
        if (afterRampUpRate.compareTo(thermalEnergyDifference.abs()) >= 0) {
            log.debug("向下爬坡后能满足需求，有限爬坡");
            currentAdjustableThermalPower = afterRampUpRate;
        } else {
            log.debug("向下爬坡后能满足需求，全力爬坡");
            currentAdjustableThermalPower = thermalEnergyDifference.abs();
        }
        log.debug("向下爬坡后功率:{}", currentAdjustableThermalPower);
    }

    /**
     * 向上爬坡
     *
     * @param thermalEnergyDifference 能量缺口(-)，一定是负数
     */
    private void rampUp(BigDecimal thermalEnergyDifference) {
        //计算爬坡后的数值
        log.debug("向上爬坡前功率:{}", currentAdjustableThermalPower);
        BigDecimal afterRampUpRate = currentAdjustableThermalPower.add(rampUpRate);
        BigDecimal pMax = PMax.subtract(PMin);
        if (afterRampUpRate.compareTo(pMax) >= 0) {
            log.debug("超出最大功率，停止爬坡");
            afterRampUpRate = pMax;
        }
        //如果爬上去之后能满足负荷
        if (afterRampUpRate
                .compareTo(thermalEnergyDifference.abs()) > 0) {
            log.debug("爬坡后能满足需求，有限爬坡");
            currentAdjustableThermalPower = thermalEnergyDifference.abs();
        } else {
            log.debug("爬坡后能满足需求，全力爬坡");
            currentAdjustableThermalPower = afterRampUpRate;
        }
        log.debug("向上爬坡后功率:{}", currentAdjustableThermalPower);
    }

    @Override
    public BigDecimal getTotalEnergy() {
        BigDecimal a = electricEnergyList.stream().map(ElectricEnergy::getValue)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal b = adjustElectricEnergyList.stream().map(ElectricEnergy::getValue)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal c = thermalEnergyList.stream().map(ThermalEnergy::getValue)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal d = adjustThermalEnergyList.stream().map(ThermalEnergy::getValue)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return a.add(b).add(c).add(d);
    }

    @Override
    public List<StackedChartData> getElectricStackedChartDataList() {

        List<BigDecimal> totalElectricEnergyList = IntStream.range(0, electricEnergyList.size())
                .mapToObj(i -> electricEnergyList.get(i).getValue().add(adjustElectricEnergyList.get(i).getValue()))
                .collect(Collectors.toList());

        return Collections.singletonList(new StackedChartData(this.modelName, totalElectricEnergyList, 600));
    }

    @Override
    public List<StackedChartData> getThermalStackedChartDataList() {
        List<BigDecimal> totalThermalEnergyList = IntStream.range(0, thermalEnergyList.size())
                .mapToObj(i -> thermalEnergyList.get(i).getValue().add(adjustThermalEnergyList.get(i).getValue()))
                .collect(Collectors.toList());
        return Collections.singletonList(new StackedChartData(this.modelName, totalThermalEnergyList, 600));
    }

    @Override
    public BigDecimal getTotalNonRenewableEnergy() {
        return getTotalEnergy().multiply(quantity);
    }

    @Override
    public BigDecimal calculateCarbonEmissions() {
        return getTotalEnergy().multiply(quantity).multiply(this.carbonEmissionFactor);
    }

    @Override
    public List<Energy> getAdjustTotalEnergy() {
        BigDecimal electricTotalEnergy = adjustElectricEnergyList.stream()
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        BigDecimal thermalTotalEnergy = adjustThermalEnergyList.stream()
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        return Arrays.asList(new ElectricEnergy(electricTotalEnergy), new ThermalEnergy(thermalTotalEnergy));
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
        return this.getTotalEnergy().multiply(this.cost);
    }

    @Override
    protected BigDecimal getCostOfGrid() {
        return BigDecimal.ZERO;
    }

    @Override
    protected BigDecimal getCostOfControl() {
        return IntStream.range(0, this.electricEnergyList.size())
                .mapToObj(this::calculateF2OfMoment)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(quantity)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateF2OfMoment(int i) {
        ElectricEnergy electricEnergy = electricEnergyList.get(i);
        ElectricEnergy adJustElectricEnergy = adjustElectricEnergyList.get(i);
        ThermalEnergy thermalEnergy = thermalEnergyList.get(i);
        ThermalEnergy adjustThermalEnergy = adjustThermalEnergyList.get(i);
        // 先计算某时刻的总能量 [P_(erl.i)^t+C_v (P_(h.i)^t+P_(cr.i)^t)]
        BigDecimal P_erl_i_t = electricEnergy.add(adJustElectricEnergy).getValue();
        BigDecimal P_h_i_t = thermalEnergy.add(adjustThermalEnergy).getValue();
        BigDecimal totalEnergy = P_erl_i_t.add((this.cv.multiply(P_h_i_t)));
        //F2 = a * totalEnergy^2 + b * totalEnergy + c
        //a b c的单位还是MW，要转换成kW
        BigDecimal fix_a = this.a.multiply(BigDecimal.valueOf(0.000001));
        BigDecimal fix_b = this.b.multiply(BigDecimal.valueOf(0.001));
        BigDecimal fix_c = this.c;
        return fix_a.multiply(totalEnergy.pow(2)).add(fix_b.multiply(totalEnergy)).add(fix_c);
    }

    @TestOnly
    public void rampDownForTest(BigDecimal electricEnergyDifference){
        rampDown(electricEnergyDifference);
    }

    @TestOnly
    public void rampUpForTest(BigDecimal electricEnergyDifference){
        rampUp(electricEnergyDifference);
    }

    @Override
    public CogenerationModel clone() {
        CogenerationModel clone = (CogenerationModel) super.clone();

        // 深拷贝 BigDecimal 字段
        clone.carbonEmissionFactor = new BigDecimal(this.carbonEmissionFactor.toString());
        clone.cost = new BigDecimal(this.cost.toString());
        clone.purchaseCost = new BigDecimal(this.purchaseCost.toString());

        // 深拷贝其他 BigDecimal 字段
        clone.PMin = new BigDecimal(this.PMin.toString());
        clone.PMax = new BigDecimal(this.PMax.toString());
        clone.rampUpRate = new BigDecimal(this.rampUpRate.toString());
        clone.rampDownRate = new BigDecimal(this.rampDownRate.toString());
        clone.etaElectric = new BigDecimal(this.etaElectric.toString());
        clone.etaLoss = new BigDecimal(this.etaLoss.toString());
        clone.COP = new BigDecimal(this.COP.toString());
        clone.flueGasRecoveryRate = new BigDecimal(this.flueGasRecoveryRate.toString());
        clone.a = new BigDecimal(this.a.toString());
        clone.b = new BigDecimal(this.b.toString());
        clone.c = new BigDecimal(this.c.toString());

        // 深拷贝 Timestamp
        clone.updatedAt = this.updatedAt == null ? null : new Timestamp(this.updatedAt.getTime());

        // 深拷贝可选边界值
        if (this.lowerBound != null) {
            clone.lowerBound = this.lowerBound;
        }
        if (this.upperBound != null) {
            clone.upperBound = this.upperBound;
        }

        // 字符串字段直接赋值（不可变类型）
        clone.modelName = this.modelName;

        // id 字段复制（如需排除可移除）
        clone.id = this.id;

        clone.electricEnergyList = new ArrayList<>();

        clone.adjustElectricEnergyList = new ArrayList<>();

        clone.thermalEnergyList = new ArrayList<>();

        clone.adjustThermalEnergyList = new ArrayList<>();

        return clone;
    }

}
