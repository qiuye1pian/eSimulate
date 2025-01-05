package org.core.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 光伏出力计算 (Java 版)
 */
public class SolarPowerModel {

    // 光伏系统额定功率 (kW)
    private final BigDecimal P_pvN;
    // 光伏组件温度系数 (1/℃)，通常为负值
    private final BigDecimal t_e;
    // 参考温度 (℃)
    private final BigDecimal T_ref;
    // 参考辐照度 (W/m²)
    private final BigDecimal G_ref;

    /**
     * 构造函数，初始化光伏系统参数
     *
     * @param P_pvN 光伏系统额定功率 (kW)
     * @param t_e   温度系数 (1/℃)
     * @param T_ref 参考温度 (℃)
     * @param G_ref 参考辐照度 (W/m²)
     */
    public SolarPowerModel(String P_pvN, String t_e, String T_ref, String G_ref) {
        this.P_pvN = new BigDecimal(P_pvN);
        this.t_e = new BigDecimal(t_e);
        this.T_ref = new BigDecimal(T_ref);
        this.G_ref = new BigDecimal(G_ref);
    }

    /**
     * 计算光伏系统在温度 T_e (℃) 和辐照度 G_T (W/m²) 下的输出功率 (kW)
     *
     * @param T_e 当前组件温度 (℃)
     * @param G_T 当前辐照度 (W/m²)
     * @return 光伏输出功率 (kW)
     */
    public BigDecimal calculatePower(String T_e, String G_T) {
        BigDecimal currentTemperature = new BigDecimal(T_e);
        BigDecimal currentIrradiance = new BigDecimal(G_T);

        // 计算公式: P = P_pvN * (1 + t_e * (T_e - T_ref)) * (G_T / G_ref)
        BigDecimal temperatureEffect = t_e.multiply(currentTemperature.subtract(T_ref));
        BigDecimal irradianceRatio = currentIrradiance.divide(G_ref, 10, RoundingMode.HALF_UP);

        return P_pvN
                .multiply(BigDecimal.ONE.add(temperatureEffect))
                .multiply(irradianceRatio)
                .setScale(10, RoundingMode.HALF_UP);
    }


}
