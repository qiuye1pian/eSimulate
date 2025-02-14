package org.core.model.device;

import lombok.Data;
import org.core.model.result.energy.ElectricEnergy;
import org.core.pso.simulator.facade.Storage;
import org.core.pso.simulator.facade.result.energy.Energy;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Java 版蓄电池储能模型
 */
@Data
public class BatteryModel implements Storage {

    // 蓄电池总容量 (Wh)
    private final ElectricEnergy C_t;

    // SOC 最小值 (0~1)
    private final BigDecimal SOC_min;

    // SOC 最大值 (0~1)
    private final BigDecimal SOC_max;

    // 自放电损失率 (无量纲)
    private final BigDecimal mu;

    // 最大充电功率 (W)
    private final BigDecimal eta_hch;

    // 最大放电功率 (W)
    private final BigDecimal eta_hdis;

    // 每个时刻电池的剩余电量 (Wh)
    private final List<ElectricEnergy> E_ESS_LIST;

    // 当前储电量 (Wh)
    private ElectricEnergy E_ESS_t;

    /**
     * 构造函数
     *
     * @param C_t      蓄电池总容量 (Wh)
     * @param SOC_min  SOC 最小值 (0~1)
     * @param SOC_max  SOC 最大值 (0~1)
     * @param mu       自放电损失率 (无量纲)
     * @param eta_hch  充电效率 (0~1)
     * @param eta_hdis 放电效率 (0~1)
     */
    public BatteryModel(String C_t, String SOC_min, String SOC_max,
                        String mu, String eta_hch, String eta_hdis) {

        this.C_t = new ElectricEnergy(new BigDecimal(C_t));
        this.SOC_min = new BigDecimal(SOC_min);
        this.SOC_max = new BigDecimal(SOC_max);
        this.mu = new BigDecimal(mu);
        this.eta_hch = new BigDecimal(eta_hch);
        this.eta_hdis = new BigDecimal(eta_hdis);

        // 参数检查
        if (this.SOC_min.compareTo(BigDecimal.ZERO) < 0 ||
                this.SOC_max.compareTo(BigDecimal.ONE) > 0 ||
                this.SOC_min.compareTo(this.SOC_max) >= 0) {
            throw new IllegalArgumentException("SOC_min 和 SOC_max 必须在 0 到 1 之间，且 SOC_min < SOC_max");
        }

        // 初始化储电量，默认从 SOC_min 开始
        this.E_ESS_t = this.C_t.multiply(this.SOC_min);

        this.E_ESS_LIST = new ArrayList<>();
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
