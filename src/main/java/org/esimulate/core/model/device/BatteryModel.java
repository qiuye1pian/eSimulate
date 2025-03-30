package org.esimulate.core.model.device;

import lombok.Data;
import org.esimulate.core.model.result.energy.ElectricEnergy;
import org.esimulate.core.pso.simulator.facade.Storage;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 蓄电池储能模型
 */
@Data
public class BatteryModel implements Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String modelName;

    // 蓄电池总容量 (Wh)
    @Column(nullable = false)
    private ElectricEnergy C_t;

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
    private BigDecimal eta_hch;

    // 最大放电功率 (W)
    @Column(nullable = false)
    private BigDecimal eta_hdis;

    // 当前储电量 (Wh)
    @Column(nullable = false)
    private ElectricEnergy E_ESS_t;

    // 碳排放因子
    @Column(nullable = false)
    private BigDecimal carbonEmissionFactor;

    // 建设成本
    @Column(nullable = false)
    private BigDecimal purchaseCost;

    @Transient
    // 每个时刻电池的剩余电量 (Wh)
    private List<ElectricEnergy> E_ESS_LIST = new ArrayList<>();

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
        ElectricEnergy electricEnergyDifference = differenceList.stream()
                .filter(x -> x instanceof ElectricEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .map(ElectricEnergy::new)
                .orElse(new ElectricEnergy(BigDecimal.ZERO));

        // 2. 计算自然放电并更新储电量
        // 自然放电损失
        BigDecimal naturalDischarge = this.E_ESS_t.multiply(this.mu).getValue();
        this.E_ESS_t = this.E_ESS_t.subtract(naturalDischarge);

        // 3. 充放电逻辑处理
        BigDecimal remainingDifference = updateElectricEnergy(electricEnergyDifference);

        // 4. 返回剩余的电能差值
        return new ElectricEnergy(remainingDifference);
    }

    /**
     * 用于更新电能储存系统的电能状态。
     * 首先计算可用的充电容量，并根据剩余差值和充电效率确定实际充电量，更新储电量。
     * 接着，如果剩余差值为负，则计算可用的放电容量，并确定实际放电量，更新储电量。
     * 。
     *
     * @param electricEnergyDifference 电能差值
     * @return 最后返回剩余的电能差值
     */
    private @NotNull BigDecimal updateElectricEnergy(ElectricEnergy electricEnergyDifference) {
        BigDecimal remainingDifference = electricEnergyDifference.getValue(); // 剩余差值
        if (remainingDifference.compareTo(BigDecimal.ZERO) > 0) {
            // 3.1 充电逻辑
            BigDecimal maxChargeCapacity = this.C_t.multiply(this.SOC_max).subtract(this.E_ESS_t).getValue(); // 可用充电容量
            BigDecimal actualCharge = remainingDifference.min(this.eta_hch).min(maxChargeCapacity); // 实际充电量
            this.E_ESS_t = this.E_ESS_t.add(actualCharge); // 更新储电量
            remainingDifference = remainingDifference.subtract(actualCharge); // 剩余冗余
        }
        if (remainingDifference.compareTo(BigDecimal.ZERO) < 0) {
            // 3.2 放电逻辑
            BigDecimal maxDischargeCapacity = this.E_ESS_t.subtract(this.C_t.multiply(this.SOC_min)).getValue(); // 可用放电容量
            BigDecimal actualDischarge = remainingDifference.abs().min(this.eta_hdis).min(maxDischargeCapacity); // 实际放电量
            this.E_ESS_t = this.E_ESS_t.subtract(actualDischarge); // 更新储电量
            remainingDifference = remainingDifference.add(actualDischarge); // 剩余缺口
        }
        return remainingDifference;
    }

    @Override
    public BigDecimal calculateCarbonEmissions() {
        return BigDecimal.ZERO;
    }
}
