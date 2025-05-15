package org.esimulate.core.model.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.result.energy.ElectricEnergy;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private BigDecimal Pmax;

    // 上游水库最大储能容量（千瓦时）
    @Column(nullable = false)
    private BigDecimal Emax;

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

    @Transient
    private List<BigDecimal> chargingList = new ArrayList<>();

    @Transient
    private List<BigDecimal> chargingCostList = new ArrayList<>();

    @Transient
    private List<BigDecimal> disChargingList = new ArrayList<>();

    @Transient
    private BigDecimal lowerBound;

    @Transient
    private BigDecimal upperBound;

    //抽水蓄能，初始投资成本: 7000元人民币/千瓦，使用年限: 50年，折现率: 6%
    @Override
    protected BigDecimal getPurchaseCost() {
        return null;
    }

    @Override
    protected BigDecimal getDiscountRate() {
        return null;
    }

    @Override
    protected Integer getLifetimeYears() {
        return 0;
    }

    //抽水蓄能  单位运行维护成本：0.07元/kWh
    @Override
    protected BigDecimal getCostOfOperation() {
        return null;
    }

    @Override
    protected BigDecimal getCostOfGrid() {
        return null;
    }

    @Override
    protected BigDecimal getCostOfControl() {
        return null;
    }

    @Override
    public List<StackedChartData> getStackedChartDataList() {
        return Collections.emptyList();
    }

    /**
     * 储能或者释放
     * @param differenceList 能量冗余/缺口，正数为冗余
     * @return 剩余能量
     */
    @Override
    public Energy storage(List<Energy> differenceList) {
        BigDecimal electricEnergyDifference = differenceList.stream()
                .filter(x -> x instanceof ElectricEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        // 按数量扩容
        this.Pmax = this.Pmax.multiply(quantity);
        this.Emax = this.Emax.multiply(quantity);
        this.stateOfCharge = this.stateOfCharge.multiply(stateOfCharge);

        BigDecimal remainingDifference = updateElectricEnergy(electricEnergyDifference);

        // 按数量缩容
        this.Pmax = this.Pmax.divide(quantity, 2, RoundingMode.HALF_UP);
        this.Emax = this.Emax.divide(quantity, 2, RoundingMode.HALF_UP);
        this.stateOfCharge = this.stateOfCharge.divide(stateOfCharge, 2, RoundingMode.HALF_UP);

        return new ElectricEnergy(remainingDifference);
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
        //按照计划充电放电
        // 如果是周一到周五
        // 分时段进行充电或者放点
        // 如果是周末
        //

        return BigDecimal.ZERO;
    }

    // 蓄能
    private @NotNull BigDecimal charging(BigDecimal remainingDifference) {
        BigDecimal needToCharging = remainingDifference.compareTo(Pmax) > 0 ? Pmax : remainingDifference;
        // 如果超出了最大范围，则停止蓄能
        if (stateOfCharge.add(needToCharging).compareTo(this.Emax) > 0) {
            BigDecimal chargeValue = this.Emax.subtract(stateOfCharge).divide(etaCh, 2, RoundingMode.HALF_UP);
            this.stateOfCharge = this.Emax;
            this.chargingList.add(chargeValue);
            this.disChargingList.add(BigDecimal.ZERO);
            BigDecimal chargeCost = chargeValue.multiply(BigDecimal.valueOf(0.1));
            this.chargingCostList.add(chargeCost);
            return remainingDifference.subtract(chargeValue.add(chargeCost));
        }

        this.stateOfCharge = this.stateOfCharge.add(needToCharging);
        this.chargingList.add(remainingDifference);
        this.disChargingList.add(BigDecimal.ZERO);
        BigDecimal chargeCost = remainingDifference.multiply(BigDecimal.valueOf(0.1));
        this.chargingCostList.add(chargeCost);
        return chargeCost.negate();
    }

    /**
     * 放电
     * @param remainingDifference 剩余差额，只可能是负值
     * @return 经过放电之后的差额
     */
    private @NotNull BigDecimal disCharging(BigDecimal remainingDifference) {
        this.chargingCostList.add(BigDecimal.ZERO);

        BigDecimal needToDisCharging = remainingDifference.abs().compareTo(Pmax) > 0 ? Pmax.negate() : remainingDifference;
        if (this.stateOfCharge.add(needToDisCharging).compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal disChargeValue = (this.stateOfCharge.add(remainingDifference)).multiply(etaDis);
            this.stateOfCharge = this.stateOfCharge.subtract(disChargeValue);
            this.chargingList.add(BigDecimal.ZERO);
            this.disChargingList.add(disChargeValue);
            return remainingDifference.subtract(disChargeValue);
        }
        // 如果小于0，则有多少放多少
        BigDecimal disChargeValue = this.stateOfCharge.multiply(etaDis);
        this.chargingList.add(BigDecimal.ZERO);
        this.disChargingList.add(disChargeValue);
        return remainingDifference.subtract(disChargeValue);
    }

    @Override
    public BigDecimal calculateCarbonEmissions() {
        return null;
    }
}
