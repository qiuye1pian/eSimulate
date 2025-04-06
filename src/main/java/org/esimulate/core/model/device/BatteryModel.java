package org.esimulate.core.model.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.result.energy.ElectricEnergy;
import org.esimulate.core.pojo.model.BatteryModelDto;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.Storage;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 蓄电池储能模型
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "battery_model")
@AllArgsConstructor
@NoArgsConstructor
public class BatteryModel extends Device implements Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String modelName;

    // 蓄电池总容量 (Wh)
    @Column(nullable = false)
    private BigDecimal C_t;

    // SOC 最小值 (0~1)
    @Column(nullable = false)
    private BigDecimal SOC_min;

    // SOC 最大值 (0~1)
    @Column(nullable = false)
    private BigDecimal SOC_max;

    // 自放电损失率 (无量纲)
    @Column(nullable = false)
    private BigDecimal mu;

    // 最大充电功率 (W)
    @Column(nullable = false)
    private BigDecimal maxChargePower;

    // 最大放电功率 (W)
    @Column(nullable = false)
    private BigDecimal maxDischargePower;

    // 充电效率 (0~1)
    @Column(nullable = false)
    private BigDecimal etaHch;

    // 放电效率 (0~1)
    @Column(nullable = false)
    private BigDecimal etaHdis;

    // 当前储电量 (Wh)
    @Column(nullable = false)
    private BigDecimal E_ESS_t;

    // 碳排放因子
    @Column(nullable = false)
    private BigDecimal carbonEmissionFactor;

    // 维护成本
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
    // 每个时刻电池的剩余电量 (Wh)
    private List<ElectricEnergy> E_ESS_LIST = new ArrayList<>();

    @Transient
    private List<BigDecimal> chargingList = new ArrayList<>();

    @Transient
    private List<BigDecimal> disChargingList = new ArrayList<>();

    public BatteryModel(BatteryModelDto batteryModelDto) {
        this.modelName = batteryModelDto.getModelName();
        this.C_t = batteryModelDto.getCt();
        this.SOC_min = batteryModelDto.getSOCMin();
        this.SOC_max = batteryModelDto.getSOCMax();
        this.mu = batteryModelDto.getMu();
        this.maxChargePower = batteryModelDto.getMaxChargePower();
        this.maxDischargePower = batteryModelDto.getMaxDischargePower();
        this.etaHch = batteryModelDto.getEtaHch();
        this.etaHdis = batteryModelDto.getEtaHDis();
        this.E_ESS_t = batteryModelDto.getEESSt();
        this.carbonEmissionFactor = batteryModelDto.getCarbonEmissionFactor();
        this.cost = batteryModelDto.getCost();
        this.purchaseCost = batteryModelDto.getPurchaseCost();
    }

    /**
     * 电池根据传入的能源 冗余/缺口 中的电力能源数据数据计算充放电
     * 该方法用于计算和更新电能存储系统的电量，处理充电和放电逻辑。
     * 首先计算输入的电能冗余或缺口，然后考虑自然放电损失并更新储电量。
     * 根据剩余的电能差值，决定是否进行充电或放电，并相应地更新储电量。最后返回剩余的电能差值。
     *
     * @param differenceList 能源 冗余/缺口 数据
     * @return 经过储能调整后的 冗余/缺口 数据
     */
    @Override
    public Energy storage(List<Energy> differenceList) {
        // 1. 计算输入的电能冗余/缺口
        BigDecimal electricEnergyDifference = differenceList.stream()
                .filter(x -> x instanceof ElectricEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        // 2. 计算自然放电并更新储电量
        // 自然放电损失
        BigDecimal naturalDischarge = this.E_ESS_t.multiply(this.mu);
        this.E_ESS_t = this.E_ESS_t.subtract(naturalDischarge);

        // 3. 充放电逻辑处理
        BigDecimal remainingDifference = updateElectricEnergy(electricEnergyDifference);

        this.E_ESS_LIST.add(new ElectricEnergy(E_ESS_t));

        // 4. 返回剩余的电能差值
        return new ElectricEnergy(remainingDifference);
    }

    /**
     * 用于更新电能储存系统的电能状态。
     * 首先计算可用的充电容量，并根据剩余差值和充电效率确定实际充电量，更新储电量。
     * 接着，如果剩余差值为负，则计算可用的放电容量，并确定实际放电量，更新储电量。
     * 。
     *
     * @param remainingDifference 剩余差额值
     * @return 最后返回剩余的电能差值
     */
    private @NotNull BigDecimal updateElectricEnergy(BigDecimal remainingDifference) {
        if (remainingDifference.compareTo(BigDecimal.ZERO) > 0) {
            this.disChargingList.add(BigDecimal.ZERO);

            // 3.1 充电逻辑
            // 计算最大可用充电容量 = 最大容量 * SOC_max - 剩余电量
            BigDecimal maxChargeCapacity = this.C_t.multiply(this.SOC_max).subtract(this.E_ESS_t);

            if (maxChargeCapacity.compareTo(BigDecimal.ZERO) < 0) {
                // 如果没有可充电量了
                this.chargingList.add(BigDecimal.ZERO);
                return remainingDifference;
            }

            // 取出 待充能量，最大可用充电容量，最大充电功率 中最小的值作为实际充电量
            BigDecimal actualCharge = remainingDifference.min(this.maxChargePower).min(maxChargeCapacity); // 实际充电量
            this.chargingList.add(actualCharge.setScale(2, RoundingMode.HALF_UP));

            // 更新储电量，剩余电量 += 实际充电量 * 充电效率
            this.E_ESS_t = this.E_ESS_t.add(actualCharge.multiply(this.etaHch).setScale(2, RoundingMode.HALF_UP));
            // 剩余冗余
            return remainingDifference.subtract(actualCharge);
        }
        if (remainingDifference.compareTo(BigDecimal.ZERO) < 0) {
            this.chargingList.add(BigDecimal.ZERO);
            // 3.2 放电逻辑
            // 计算最大可用放电容量 = 剩余电量 - 最大容量 * SOC_min
            BigDecimal maxDischargeCapacity = this.E_ESS_t.subtract(this.C_t.multiply(this.SOC_min));
            if (maxDischargeCapacity.compareTo(BigDecimal.ZERO) <= 0) {
                this.disChargingList.add(BigDecimal.ZERO);
                // 如果没有可放电量了
                return remainingDifference;
            }
            // 取出 待放能量，最大可用放电容量，最大放电功率 中最小的值作为实际放电量
            BigDecimal actualDischarge = remainingDifference.abs().min(this.maxDischargePower).min(maxDischargeCapacity);
            this.disChargingList.add(actualDischarge.setScale(2, RoundingMode.HALF_UP));
            // 更新储电量，剩余电量 -= 实际放电量 * 放电效率
            this.E_ESS_t = this.E_ESS_t.subtract(actualDischarge.multiply(this.etaHdis)).setScale(2, RoundingMode.HALF_UP);
            // 剩余缺口
            return remainingDifference.add(actualDischarge);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateCarbonEmissions() {
        return BigDecimal.ZERO;
    }

    @TestOnly
    public BigDecimal testUpdateElectricEnergy(BigDecimal remainingDifference) {
        return this.updateElectricEnergy(remainingDifference);
    }

    @Override
    protected BigDecimal getDiscountRate() {
        return BigDecimal.valueOf(0.07);
    }

    @Override
    protected Integer getLifetimeYears() {
        return 10;
    }

    @Override
    protected BigDecimal getCostOfOperation() {
        BigDecimal chargingTotal = this.chargingList.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal disChargingTotal = this.disChargingList.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        return chargingTotal.add(disChargingTotal)
                .multiply(this.cost)
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
}
