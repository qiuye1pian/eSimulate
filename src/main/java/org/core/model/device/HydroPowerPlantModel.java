package org.core.model.device;

import lombok.Getter;
import org.core.model.environment.water.WaterSpeedData;
import org.core.model.result.energy.ElectricEnergy;
import org.core.pso.simulator.facade.Producer;
import org.core.pso.simulator.facade.environment.EnvironmentValue;
import org.core.pso.simulator.facade.result.carbon.CarbonEmitter;
import org.core.pso.simulator.facade.result.energy.Energy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 小水电机组功率计算 (Java 版)
 */
@Getter
public class HydroPowerPlantModel implements Producer, CarbonEmitter {

    // 水轮机效率
    private final BigDecimal eta1;

    // 发电机效率
    private final BigDecimal eta2;

    // 机组传动效率
    private final BigDecimal eta3;

    // 总效率 = eta1 * eta2 * eta3
    private final BigDecimal eta;

    // 上游水面相对于参考面的位能 (m)
    private final BigDecimal z1;

    // 水轮机入口处相对于参考面的位能 (m)
    private final BigDecimal z2;

    // 过水断面平均流速 v1
    private final BigDecimal v1;

    // 过水断面平均流速 v2
    private final BigDecimal v2;

    // 水密度 ρ1
    private final BigDecimal p1;

    // 水密度 ρ2
    private final BigDecimal p2;

    // ρg
    private final BigDecimal pg;

    // 重力加速度g
    private final BigDecimal g;

    // 碳排放因子 (kg CO₂ / m³)
    private final BigDecimal emissionFactor;

    private final List<ElectricEnergy> electricEnergyList;

    /**
     * 构造函数
     *
     * @param eta1 水轮机效率
     * @param eta2 发电机效率
     * @param eta3 机组传动效率
     */
    public HydroPowerPlantModel(BigDecimal eta1, BigDecimal eta2, BigDecimal eta3,
                                BigDecimal z1, BigDecimal p1, BigDecimal v1,
                                BigDecimal z2, BigDecimal p2, BigDecimal v2,
                                BigDecimal pg, BigDecimal g, BigDecimal emissionFactor) {
        this.eta1 = eta1;
        this.eta2 = eta2;
        this.eta3 = eta3;
        this.eta = this.eta1.multiply(this.eta2).multiply(this.eta3).setScale(10, RoundingMode.HALF_UP);

        this.z1 = z1;
        this.p1 = p1;
        this.v1 = v1;
        this.z2 = z2;
        this.p2 = p2;
        this.v2 = v2;
        this.pg = pg;
        this.g = g;

        this.emissionFactor = emissionFactor;

        this.electricEnergyList = new ArrayList<>();
    }

    /**
     * 计算水头 H
     *
     * @return 水头 (m)
     */
    public BigDecimal calculateHead() {

        // 计算 H = [Z1 + p1/(rho*g) + v1^2/(2*g)] - [Z2 + p2/(rho*g) + v2^2/(2*g)]
        BigDecimal head1 =
                // Z1
                this.z1.add(
                        // p1/(rho*g)
                        this.p1.divide(this.pg.multiply(this.g), 10, RoundingMode.HALF_UP)
                ).add(
                        // v1^2/(2*g)
                        this.v1.pow(2).divide(this.g.multiply(new BigDecimal("2")), 10, RoundingMode.HALF_UP)
                );

        BigDecimal head2 =
                // Z2
                this.z2.add(
                        // p2/(rho*g)
                        this.p2.divide(this.pg.multiply(this.g), 10, RoundingMode.HALF_UP)
                ).add(
                        // v2^2/(2*g)
                        this.v2.pow(2).divide(this.g.multiply(new BigDecimal("2")), 10, RoundingMode.HALF_UP)
                );

        // H1 - H2
        return head1.subtract(head2).setScale(10, RoundingMode.HALF_UP);
    }

    /**
     * 计算水电机组输出功率
     *
     * @param Q 流量 (m³/s)
     * @param H 水头 (m)
     * @return 水电机组输出功率 (kW)
     */
    public BigDecimal calculatePower(BigDecimal Q, BigDecimal H) {

        // P_h = 9.81 * eta * Q * H
        BigDecimal gravity = new BigDecimal("9.81");
        return gravity.multiply(this.eta).multiply(Q).multiply(H).setScale(10, RoundingMode.HALF_UP);
    }

    @Override
    public Energy produce(List<EnvironmentValue> environmentValueList) {
        // 1. 提取环境变量中的流量 Q 和水头 H
        BigDecimal Q = environmentValueList.stream()
                .filter(env -> env instanceof WaterSpeedData)
                .map(EnvironmentValue::getValue)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        BigDecimal H = calculateHead();

        // 2. 计算水电机组输出功率 (kW)
        BigDecimal power = calculatePower(Q, H);

        // 3. 将计算出的电能存入历史记录
        ElectricEnergy generatedEnergy = new ElectricEnergy(power);
        this.electricEnergyList.add(generatedEnergy);

        // 4. 返回当前时间点的发电量
        return generatedEnergy;
    }

    @Override
    public BigDecimal getTotalEnergy() {
        return electricEnergyList.stream()
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal calculateCarbonEmissions() {
        return electricEnergyList.stream()
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO)
                .multiply(emissionFactor);
    }
}
