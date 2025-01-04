package org.core.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ThermalPowerModel {

    // 光热转换效率
    private final BigDecimal etaSF;
    // CSP 电站镜场面积 (m²)
    private final BigDecimal SSF;

    /**
     * 构造函数：初始化光热电站参数
     *
     * @param etaSF 光热转换效率 (0~1)
     * @param SSF   CSP 电站镜场面积 (m²)
     */
    public ThermalPowerModel(String etaSF, String SSF) {
        this.etaSF = new BigDecimal(etaSF);
        this.SSF = new BigDecimal(SSF);
    }

    /**
     * 静态方法：计算燃气锅炉的供热功率 (kW) 数组。
     * <p>
     * 公式：H_GB_t[i] = heatLoadCurve[i] - solarThermal[i]
     * 若结果小于 0，则取 0 (表示需求已经被太阳能覆盖)
     *
     * @param heatLoadCurveKw      系统热负荷需求曲线 (kW)，长度 N
     * @param solarThermalSupplyKw 太阳能热源供热曲线 (kW)，长度 N
     * @return 长度 N 的锅炉出力数组 (kW)
     */
    public static BigDecimal[] calculateHeatPower(BigDecimal[] heatLoadCurveKw, BigDecimal[] solarThermalSupplyKw) {
        if (heatLoadCurveKw.length != solarThermalSupplyKw.length) {
            throw new IllegalArgumentException("数组长度不一致！");
        }

        int n = heatLoadCurveKw.length;
        BigDecimal[] H_GB_t = new BigDecimal[n];
        for (int i = 0; i < n; i++) {
            BigDecimal diff = heatLoadCurveKw[i].subtract(solarThermalSupplyKw[i]);
            H_GB_t[i] = diff.max(BigDecimal.ZERO);
        }
        return H_GB_t;
    }

    /**
     * 静态方法：计算在调度周期内消耗天然气的成本。
     * <p>
     * E_GB_t[i] = H_GB_t[i] / eta_GB
     * F_GB = sum( C_CH4 * E_GB_t[i] * delta_t ), i=1..N
     *
     * @param H_GB_t  燃气锅炉的供热功率曲线 (kW)，长度 N
     * @param eta_GB  燃气锅炉的燃烧效率 (0~1)
     * @param C_CH4   天然气价格 (元/kWh 或任意货币/kWh)˚
     * @param delta_t 时间步长 (小时)
     * @return 在此周期内消耗天然气的总成本
     */
    public static BigDecimal calculateGasCost(BigDecimal[] H_GB_t, BigDecimal eta_GB, BigDecimal C_CH4, BigDecimal delta_t) {
        BigDecimal totalCost = BigDecimal.ZERO;
        for (BigDecimal power : H_GB_t) {
            BigDecimal E_GB = power.divide(eta_GB, 10, RoundingMode.HALF_UP); // 锅炉需要的燃气输入 (kWh)
            totalCost = totalCost.add(C_CH4.multiply(E_GB).multiply(delta_t));
        }
        return totalCost;
    }

    /**
     * 计算 t 时段集热装置吸收的热功率 (kW)。
     * <p>
     * P_t_th_solar = eta_SF * S_SF * D_t / 1e3
     * 其中:
     * - D_t: 太阳光在时段 t 的平均直接辐射量(DNI, W/m²)
     * - 最终返回值单位: kW
     *
     * @param D_t 太阳光在时段 t 的平均直接辐射量 (W/m²)
     * @return 光热电站吸收的热功率 (kW)
     */
    public BigDecimal calculateThermalPower(BigDecimal D_t) {
        // 将 W 转成 kW，因此除以 1e3
        return etaSF.multiply(SSF).multiply(D_t).divide(new BigDecimal("1000"), 10, RoundingMode.HALF_UP);
    }


}
