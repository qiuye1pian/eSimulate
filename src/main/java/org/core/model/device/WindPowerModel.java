package org.core.model.device;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 风力发电功率计算器 (Java 版)
 */
public class WindPowerModel {

    // 切入风速 (m/s)
    private final BigDecimal v_in;

    // 额定风速 (m/s)
    private final BigDecimal v_n;

    // 切出风速 (m/s)
    private final BigDecimal v_out;

    // 额定功率 (kW)
    private final BigDecimal P_r;

    /**
     * 构造函数，初始化风机关键参数
     *
     * @param v_in  切入风速 (m/s)
     * @param v_n   额定风速 (m/s)
     * @param v_out 切出风速 (m/s)
     * @param P_r   额定功率 (kW)
     */
    public WindPowerModel(String v_in, String v_n, String v_out, String P_r) {
        this.v_in = new BigDecimal(v_in);
        this.v_n = new BigDecimal(v_n);
        this.v_out = new BigDecimal(v_out);
        this.P_r = new BigDecimal(P_r);
    }

    /**
     * 计算给定风速下的风机出力
     *
     * @param v 当前风速 (m/s)
     * @return 风机输出功率 (kW)
     */
    public BigDecimal calculatePower(String v) {
        BigDecimal v_speed = new BigDecimal(v);

        // 1. 低于或等于切入风速 -> 输出 0
        if (v_speed.compareTo(v_in) <= 0) {
            return BigDecimal.ZERO;
        }
        // 2. 切入风速 < v <= 额定风速 -> 二次插值计算输出
        else if (v_speed.compareTo(v_in) > 0 && v_speed.compareTo(v_n) <= 0) {
            BigDecimal numerator = v_speed.pow(2).subtract(v_in.pow(2));
            BigDecimal denominator = v_n.pow(2).subtract(v_in.pow(2));
            return numerator.divide(denominator, 10, RoundingMode.HALF_UP).multiply(P_r);
        }
        // 3. 额定风速 < v <= 切出风速 -> 输出额定功率
        else if (v_speed.compareTo(v_n) > 0 && v_speed.compareTo(v_out) <= 0) {
            return P_r;
        }
        // 4. 超过切出风速 -> 输出 0
        else {
            return BigDecimal.ZERO;
        }
    }


}
