package org.esimulate.core.model.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.esimulate.core.model.result.energy.ElectricEnergy;
import org.esimulate.core.pojo.model.PumpedStorageModelDto;
import org.esimulate.core.pojo.simulate.result.StackedChartData;
import org.esimulate.core.pso.particle.Dimension;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.ElectricDevice;
import org.esimulate.core.pso.simulator.facade.Storage;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "pumped_storage_model")
@AllArgsConstructor
@NoArgsConstructor
public class PumpedStorageModel extends Device implements Storage, Dimension, ElectricDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String modelName;

    // 最大抽水（蓄能）或放水（发电）功率（千瓦）
    @Column(nullable = false)
    private BigDecimal PMax;

    // 上游水库最大储能容量（千瓦时）
    @Column(nullable = false)
    private BigDecimal EMax;

    // 抽水（蓄能）效率（0-1）
    @Column(nullable = false)
    private BigDecimal etaCh;

    // 放水（发电）效率（0-1）
    @Column(nullable = false)
    private BigDecimal etaDis;

    // 日/周能量平衡系数（λ）
    @Column(nullable = false)
    private BigDecimal lambda;

    // 当前水库储能（千瓦时）
    @Column(nullable = false)
    private BigDecimal stateOfCharge;

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
    private List<BigDecimal> chargingList = new ArrayList<>();

    @Transient
    private List<BigDecimal> chargingCostList = new ArrayList<>();

    @Transient
    private List<BigDecimal> disChargingList = new ArrayList<>();

    @Transient
    Integer lowerBound;

    @Transient
    Integer upperBound;

    public PumpedStorageModel(PumpedStorageModelDto pumpedStorageModelDto) {
        this.id = pumpedStorageModelDto.getId();
        this.modelName = pumpedStorageModelDto.getModelName();
        this.PMax = pumpedStorageModelDto.getPMax();
        this.EMax = pumpedStorageModelDto.getEMax();
        this.etaCh = pumpedStorageModelDto.getEtaCh();
        this.etaDis = pumpedStorageModelDto.getEtaDis();
        this.lambda = pumpedStorageModelDto.getLambda();
        this.stateOfCharge = pumpedStorageModelDto.getStateOfCharge();
        this.carbonEmissionFactor = pumpedStorageModelDto.getCarbonEmissionFactor();
        this.cost = pumpedStorageModelDto.getCost();
        this.purchaseCost = pumpedStorageModelDto.getPurchaseCost();
    }


    //抽水蓄能，初始投资成本: 7000元人民币/千瓦，使用年限: 50年，折现率: 6%
    @Override
    public BigDecimal getPurchaseCost() {
        return PMax.multiply(purchaseCost);
    }

    @Override
    protected BigDecimal getDiscountRate() {
        return BigDecimal.valueOf(0.06);
    }

    @Override
    protected Integer getLifetimeYears() {
        return 50;
    }

    //抽水蓄能  单位运行维护成本：0.07元/kWh
    @Override
    protected BigDecimal getCostOfOperation() {
        return this.chargingCostList.stream().reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO)
                .multiply(quantity)
                .multiply(cost);
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
    public List<StackedChartData> getElectricStackedChartDataList() {
        StackedChartData chargingList = new StackedChartData(String.format("%s 充电", this.modelName), this.chargingList, 601);
        StackedChartData disChargingList = new StackedChartData(String.format("%s 放电", this.modelName), this.disChargingList, 601);
        return Arrays.asList(chargingList, disChargingList);
    }

    /**
     * 储能或者释放
     * @param differenceList 能量冗余/缺口，正数为冗余
     * @return 剩余能量
     */
    @Override
    public List<Energy> storage(List<Energy> differenceList) {
        BigDecimal electricEnergyDifference = differenceList.stream()
                .filter(x -> x instanceof ElectricEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        // 按数量扩容
        this.PMax = this.PMax.multiply(quantity);
        this.EMax = this.EMax.multiply(quantity);
        this.stateOfCharge = this.stateOfCharge.multiply(quantity);


//        log.info("抽水蓄能电能========准备工作========剩余冗余/缺口:{}，电量:{}/{}", electricEnergyDifference, this.stateOfCharge, this.EMax);
        BigDecimal remainingDifference = updateElectricEnergy(electricEnergyDifference);
//        log.info("抽水蓄能电能========工作完成========剩余冗余/缺口:{}，电量:{}/{}\r\n==========:{}", remainingDifference, this.stateOfCharge, this.EMax, remainingDifference.subtract(electricEnergyDifference));

        // 按数量缩容
        this.PMax = this.PMax.divide(quantity, 2, RoundingMode.HALF_UP);
        this.EMax = this.EMax.divide(quantity, 2, RoundingMode.HALF_UP);
        this.stateOfCharge = this.stateOfCharge.divide(quantity, 2, RoundingMode.HALF_UP);

        differenceList.removeIf(x -> x instanceof ElectricEnergy);
        differenceList.add(new ElectricEnergy(remainingDifference));
        return differenceList;
    }

    private BigDecimal updateElectricEnergy(BigDecimal remainingDifference) {
        // 如果有冗余就充电
        if (remainingDifference.compareTo(BigDecimal.ZERO) > 0) {
            return charging(remainingDifference);
        }

        // 如果有缺口则放电
        if (remainingDifference.compareTo(BigDecimal.ZERO) < 0) {
            return disCharging(remainingDifference);
        }

        return BigDecimal.ZERO;
    }

    // 蓄能
    private @NotNull BigDecimal charging(BigDecimal remainingDifference) {
        BigDecimal needToCharging = remainingDifference.compareTo(PMax) > 0 ? PMax : remainingDifference;
//        log.info("充电>>>当前能量/最大容量:{}/{}，待充:{},剩余缺口:{}", stateOfCharge, EMax, needToCharging, remainingDifference);
        BigDecimal chargeValue;
        // 如果超出了最大范围，则停止蓄能
        if (stateOfCharge.add(needToCharging).compareTo(this.EMax) >= 0) {
            chargeValue = this.EMax.subtract(stateOfCharge);
//            log.info("可充空间不足-->充电:{}", chargeValue);
        } else {
            chargeValue = needToCharging;
//            log.info("可充空间充足-->充电:{}", chargeValue);
        }

        BigDecimal chargeCost = chargeValue.multiply(BigDecimal.valueOf(0.1));
        this.stateOfCharge = this.stateOfCharge.add(chargeValue);
        this.chargingList.add(chargeValue);
        this.disChargingList.add(BigDecimal.ZERO);
        this.chargingCostList.add(chargeCost);
//        log.info("没满-->充电:{}", chargeValue);

        return remainingDifference.subtract(chargeValue.divide(etaCh, 2, RoundingMode.HALF_UP));
    }

    /**
     * 放电
     * @param remainingDifference 剩余差额，只可能是负值
     * @return 经过放电之后的差额
     */
    private @NotNull BigDecimal disCharging(BigDecimal remainingDifference) {
        BigDecimal needToDisCharging = remainingDifference.abs().compareTo(this.PMax) > 0 ? PMax.negate() : remainingDifference;
//        log.info("放电<<<当前能量/最大容量:{}/{}, 待放电:{}, 剩余缺口:{}", stateOfCharge, EMax, needToDisCharging, remainingDifference);
        BigDecimal disChargeValue;
        if (this.stateOfCharge.add(needToDisCharging).compareTo(BigDecimal.ZERO) >= 0) {
            disChargeValue = needToDisCharging;
//            log.info("够放的-->放电:{}", disChargeValue);
        }else {
            // 如果小于0，不够放的，则全放掉
            disChargeValue = this.stateOfCharge.negate();
//            log.info("不够放的-->放电:{}", disChargeValue);
        }
        // disChargeValue一直是 大于等于0 的
        this.stateOfCharge = this.stateOfCharge.add(disChargeValue);
        this.chargingList.add(BigDecimal.ZERO);
        this.chargingCostList.add(BigDecimal.ZERO);
        this.disChargingList.add(disChargeValue);
        // remainingDifference是负数，加上 disChargeValue
        return remainingDifference.add(disChargeValue.multiply(etaDis));
    }

    @Override
    public BigDecimal calculateCarbonEmissions() {
        return this.chargingCostList.stream()
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO)
                .multiply(quantity)
                .multiply(this.carbonEmissionFactor);
    }

    @Override
    public PumpedStorageModel clone() {
        PumpedStorageModel clone = (PumpedStorageModel) super.clone();

        // 深拷贝 BigDecimal 字段
        clone.PMax = new BigDecimal(this.getPMax().toString());
        clone.carbonEmissionFactor = new BigDecimal(this.carbonEmissionFactor.toString());
        clone.cost = new BigDecimal(this.cost.toString());
        clone.purchaseCost = new BigDecimal(this.purchaseCost.toString());

        // 深拷贝其他 BigDecimal 字段
        clone.EMax = new BigDecimal(this.getEMax().toString());
        clone.etaCh = new BigDecimal(this.getEtaCh().toString());
        clone.etaDis = new BigDecimal(this.getEtaDis().toString());
        clone.lambda = new BigDecimal(this.getLambda().toString());
        clone.stateOfCharge = new BigDecimal(this.getStateOfCharge().toString());

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

        clone.chargingList = new ArrayList<>();

        clone.chargingCostList = new ArrayList<>();

        clone.disChargingList = new ArrayList<>();

        return clone;
    }
}
