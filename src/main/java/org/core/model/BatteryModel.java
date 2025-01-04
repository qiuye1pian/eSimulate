package org.core.model;

/**
 * Java 版蓄电池储能模型
 */
public class BatteryModel {

    // 蓄电池总容量 (Wh)
    private double C_t;
    // SOC 最小值 (0~1)
    private double SOC_min;
    // SOC 最大值 (0~1)
    private double SOC_max;
    // 自放电损失率 (无量纲, 比如 0.001 表示 0.1%)
    private double mu;
    // 充电效率 (0~1)
    private double eta_hch;
    // 放电效率 (0~1)
    private double eta_hdis;
    // 当前储电量 (Wh)
    private double E_ESS_t;

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
    public BatteryModel(double C_t, double SOC_min, double SOC_max,
                        double mu, double eta_hch, double eta_hdis) {

        // 参数检查：SOC_min 必须小于 SOC_max，且均在 [0,1] 范围
        if (!(0 <= SOC_min && SOC_min < SOC_max && SOC_max <= 1)) {
            throw new IllegalArgumentException(
                    "SOC_min 和 SOC_max 必须在 0 到 1 之间，且 SOC_min < SOC_max"
            );
        }

        this.C_t = C_t;
        this.SOC_min = SOC_min;
        this.SOC_max = SOC_max;
        this.mu = mu;
        this.eta_hch = eta_hch;
        this.eta_hdis = eta_hdis;

        // 初始化储电量，默认从 SOC_min 开始
        this.E_ESS_t = C_t * SOC_min;
    }

    /**
     * 更新蓄电池储电量与SOC
     *
     * @param P_ESS_in_t  充电功率 (W)
     * @param P_ESS_dis_t 放电功率 (W)
     * @param delta_t     时间段长度 (小时)
     * @return            返回当前 Battery 对象，便于链式调用
     */
    public BatteryModel updateSOC(double P_ESS_in_t, double P_ESS_dis_t, double delta_t) {
        if (delta_t <= 0) {
            throw new IllegalArgumentException("时段长度 delta_t 必须大于 0");
        }

        // 上一时刻储电量
        double E_ESS_prev = this.E_ESS_t;

        // 新的储电量计算：考虑自放电 & 充电放电
        // E(t+Δt) = (1 - mu) * E(t) + [ P_in * η_ch - (P_out / η_dis ) ] * Δt
        double E_ESS_new = (1 - mu) * E_ESS_prev
                + (P_ESS_in_t * eta_hch - P_ESS_dis_t / eta_hdis) * delta_t;

        // 限制在 [C_t * SOC_min, C_t * SOC_max] 范围内
        if (E_ESS_new > C_t * SOC_max) {
            E_ESS_new = C_t * SOC_max;
        } else if (E_ESS_new < C_t * SOC_min) {
            E_ESS_new = C_t * SOC_min;
        }

        this.E_ESS_t = E_ESS_new;
        return this;
    }

    /**
     * 获取当前储电量 (Wh)
     *
     * @return 当前储电量 (Wh)
     */
    public double getEnergyCapacity() {
        return this.E_ESS_t;
    }

    /**
     * 获取当前SOC (0~1)
     *
     * @return 当前SOC (0~1)
     */
    public double getSOC() {
        return this.E_ESS_t / this.C_t;
    }

}
