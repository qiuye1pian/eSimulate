package org.core.model.device;

import lombok.Data;
import org.core.model.result.energy.ElectricEnergy;
import org.core.pso.simulator.facade.Storage;
import org.core.pso.simulator.facade.result.energy.Energy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Java 版蓄电池储能模型
 */
@Data
public class BatteryModel implements Storage {

    // 蓄电池总容量 (Wh)
    private final BigDecimal C_t;

    // SOC 最小值 (0~1)
    private final BigDecimal SOC_min;

    // SOC 最大值 (0~1)
    private final BigDecimal SOC_max;

    // 自放电损失率 (无量纲)
    private final BigDecimal mu;

    // 充电效率 (0~1)
    private final BigDecimal eta_hch;

    // 放电效率 (0~1)
    private final BigDecimal eta_hdis;

    // 当前储电量 (Wh)
    private BigDecimal E_ESS_t;

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

        this.C_t = new BigDecimal(C_t);
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
    }


    /**
     * 电池根据传入的能源 冗余/缺口 中的电力能源数据数据计算充放电
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
        BigDecimal naturalDischarge = this.mu.multiply(this.E_ESS_t); // 自然放电损失
        this.E_ESS_t = this.E_ESS_t.subtract(naturalDischarge);

        // 3. 处理充放电逻辑
        this.E_ESS_t = adjustEnergyByDifference(this.E_ESS_t, electricEnergyDifference);

        // 4. 检查过充和过放保护
        return enforceCapacityBounds();
    }

    /**
     * 根据电能差值调整储电量（充放电逻辑）
     */
    private BigDecimal adjustEnergyByDifference(BigDecimal currentEnergy, BigDecimal energyDifference) {
        if (energyDifference.compareTo(BigDecimal.ZERO) > 0) {
            // 充电逻辑
            BigDecimal chargeEnergy = energyDifference.multiply(this.eta_hch); // 考虑充电效率
            return currentEnergy.add(chargeEnergy);
        }

        if (energyDifference.compareTo(BigDecimal.ZERO) < 0) {
            // 放电逻辑
            BigDecimal dischargeEnergy = energyDifference.abs().divide(this.eta_hdis, 10, RoundingMode.HALF_UP); // 考虑放电效率
            return currentEnergy.subtract(dischargeEnergy);
        }
        return currentEnergy; // 没有差值，不调整
    }

    /**
     * 检查过充和过放保护，并返回多余或不足的能量
     */
    private Energy enforceCapacityBounds() {
        BigDecimal minCapacity = this.C_t.multiply(this.SOC_min);
        BigDecimal maxCapacity = this.C_t.multiply(this.SOC_max);

        if (this.E_ESS_t.compareTo(maxCapacity) > 0) {
            BigDecimal surplusEnergy = this.E_ESS_t.subtract(maxCapacity);
            this.E_ESS_t = maxCapacity; // 限制储电量到最大容量
            return new ElectricEnergy(surplusEnergy); // 返回多余能量
        }
        if (this.E_ESS_t.compareTo(minCapacity) < 0) {
            BigDecimal deficitEnergy = this.E_ESS_t.subtract(minCapacity);
            this.E_ESS_t = minCapacity; // 限制储电量到最小容量
            return new ElectricEnergy(deficitEnergy); // 返回未满足的缺口
        }
        return new ElectricEnergy(BigDecimal.ZERO); // 没有多余或缺口
    }
}
