package org.core.model;

/**
 * 小水电机组功率计算 (Java 版)
 */
public class HydroPowerPlantModel {

    // 水轮机效率
    private double eta1;
    // 发电机效率
    private double eta2;
    // 机组传动效率
    private double eta3;
    // 总效率 = eta1 * eta2 * eta3
    private double eta;

    /**
     * 构造函数
     *
     * @param eta1 水轮机效率
     * @param eta2 发电机效率
     * @param eta3 机组传动效率
     */
    public HydroPowerPlantModel(double eta1, double eta2, double eta3) {
        this.eta1 = eta1;
        this.eta2 = eta2;
        this.eta3 = eta3;
        // 计算总效率
        this.eta = eta1 * eta2 * eta3;
    }

    /**
     * 计算水头 H
     *
     * @param Z1  上游水面相对于参考面的位能 (m)
     * @param p1  上游水面压力 (Pa)
     * @param v1  上游水面过水断面平均流速 (m/s)
     * @param Z2  水轮机入口处相对于参考面的位能 (m)
     * @param p2  水轮机入口处压力 (Pa)
     * @param v2  水轮机入口处过水断面平均流速 (m/s)
     * @param rho 水的密度 (kg/m³)，缺省可设为 1000.0
     * @param g   重力加速度 (m/s²)，缺省可设为 9.81
     * @return    水头 (m)
     */
    public double calculateHead(double Z1, double p1, double v1,
                                double Z2, double p2, double v2,
                                double rho, double g) {
        // H = [Z1 + p1/(rho*g) + v1^2/(2*g)] - [Z2 + p2/(rho*g) + v2^2/(2*g)]
        return (Z1 + p1 / (rho * g) + (v1 * v1) / (2 * g))
                - (Z2 + p2 / (rho * g) + (v2 * v2) / (2 * g));
    }

    /**
     * 不带 rho 和 g 参数的重载方法，使用默认值：rho=1000 kg/m³, g=9.81 m/s²
     */
    public double calculateHead(double Z1, double p1, double v1,
                                double Z2, double p2, double v2) {
        double rho = 1000.0;
        double g = 9.81;
        return calculateHead(Z1, p1, v1, Z2, p2, v2, rho, g);
    }

    /**
     * 计算水电机组输出功率
     *
     * @param Q 流量 (m³/s)
     * @param H 水头 (m)
     * @return  水电机组输出功率 (kW)
     */
    public double calculatePower(double Q, double H) {
        // P_h = 9.81 * eta * Q * H
        // (若想更完整体现 ρ*g，需写成 rho*g * Q * H / 1000)
        // 这里以 9.81 代替 (rho*g/1000) 形式，结果单位为 kW
        return 9.81 * this.eta * Q * H;
    }

}
