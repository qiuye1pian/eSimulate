package org.core.model;

/**
 * 光伏出力计算 (Java 版)
 */
public class SolarPowerModel {

    // 光伏系统额定功率 (kW)
    private double P_pvN;
    // 光伏组件温度系数 (1/℃)，通常为负值
    private double t_e;
    // 参考温度 (℃)
    private double T_ref;
    // 参考辐照度 (W/m²)
    private double G_ref;

    /**
     * 构造函数，初始化光伏系统参数
     *
     * @param P_pvN 光伏系统额定功率 (kW)
     * @param t_e   温度系数 (1/℃)
     * @param T_ref 参考温度 (℃)
     * @param G_ref 参考辐照度 (W/m²)
     */
    public SolarPowerModel(double P_pvN, double t_e, double T_ref, double G_ref) {
        this.P_pvN = P_pvN;
        this.t_e = t_e;
        this.T_ref = T_ref;
        this.G_ref = G_ref;
    }

    /**
     * 计算光伏系统在温度 T_e (℃) 和辐照度 G_T (W/m²) 下的输出功率 (kW)
     *
     * @param T_e 当前组件温度 (℃)
     * @param G_T 当前辐照度 (W/m²)
     * @return 光伏输出功率 (kW)
     */
    public double calculatePower(double T_e, double G_T) {
        return P_pvN
                * (1 + t_e * (T_e - T_ref))
                * (G_T / G_ref);
    }
}