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
     * 更新蓄电池储电量与SOC
     *
     * @param P_ESS_in_t  充电功率 (W)
     * @param P_ESS_dis_t 放电功率 (W)
     * @param delta_t     时间段长度 (小时)
     * @return 返回当前 Battery 对象，便于链式调用
     */
    public BatteryModel updateSOC(String P_ESS_in_t, String P_ESS_dis_t, String delta_t) {
        BigDecimal P_in = new BigDecimal(P_ESS_in_t);
        BigDecimal P_out = new BigDecimal(P_ESS_dis_t);
        BigDecimal dt = new BigDecimal(delta_t);

        if (dt.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("时段长度 delta_t 必须大于 0");
        }

        // 上一时刻储电量
        BigDecimal E_ESS_prev = this.E_ESS_t;

        // 新的储电量计算
        BigDecimal E_ESS_new = E_ESS_prev
                .multiply(BigDecimal.ONE.subtract(mu))  // 自放电损失
                .add(P_in.multiply(eta_hch).subtract(P_out.divide(eta_hdis, 10, RoundingMode.HALF_UP)).multiply(dt));

        // 限制在 [C_t * SOC_min, C_t * SOC_max] 范围内
        BigDecimal minCapacity = C_t.multiply(SOC_min);
        BigDecimal maxCapacity = C_t.multiply(SOC_max);

        if (E_ESS_new.compareTo(maxCapacity) > 0) {
            E_ESS_new = maxCapacity;
        } else if (E_ESS_new.compareTo(minCapacity) < 0) {
            E_ESS_new = minCapacity;
        }

        this.E_ESS_t = E_ESS_new;
        return this;
    }

    /**
     * 获取当前储电量 (Wh)
     *
     * @return 当前储电量 (Wh)
     */
    public BigDecimal getEnergyCapacity() {
        return this.E_ESS_t;
    }

    /**
     * 获取当前SOC (0~1)
     *
     * @return 当前SOC (0~1)
     */
    public BigDecimal getSOC() {
        return this.E_ESS_t.divide(this.C_t, 10, RoundingMode.HALF_UP);
    }


    @Override
    public Energy storage(List<Energy> differenceList) {

        BigDecimal electricEnergyDifference = differenceList.stream()
                .filter(x -> x instanceof ElectricEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        //TODO:这里需要补充计算
        //应该是根据 electricEnergyDifference，进行充/放电，并且加上电量衰减，更新电量余额，最后返回充/放电的能量
        return new ElectricEnergy(BigDecimal.ZERO);
    }
}
