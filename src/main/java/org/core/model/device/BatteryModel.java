package org.core.model.device;

import lombok.Data;
import org.core.model.result.energy.ElectricEnergy;
import org.core.pso.simulator.facade.Storage;
import org.core.pso.simulator.facade.result.energy.Energy;

import java.math.BigDecimal;
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

        // 计算 输入的电能冗余/缺口 final BigDecimal electricEnergyDifference
        // 计算 自然放电的量 = 自放电损失率 (无量纲)mu * 蓄电池总容量 (Wh) C_t
        // 先计算自然放电： 当前储电量 = 当前储电量 - 自然放电的量

        // 判断放电保护
        //  如果 (当前储电量 < (SOC_min * C_t)) 且 (输入的电能冗余/缺口 < 0)
        //      则返回 输入的电能冗余/缺口

        // 充电放电：当前储电量 = 当前储电量 + 输入的电能冗余/缺口

        // 判断过充保护
        // 如果  当前储电量 > (SOC_max * C_t)
        //      计算 能量冗余 = 当前储电量 - (SOC_max * C_t);
        //      当前储电量 = SOC_max * C_t;
        //      则返回 能量冗余
        return new ElectricEnergy(BigDecimal.ZERO);
    }
}
