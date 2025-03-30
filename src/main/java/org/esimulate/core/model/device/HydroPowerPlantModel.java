package org.esimulate.core.model.device;

import lombok.*;
import org.esimulate.core.model.environment.water.WaterSpeedValue;
import org.esimulate.core.model.result.energy.ElectricEnergy;
import org.esimulate.core.pso.simulator.facade.Producer;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentValue;
import org.esimulate.core.pso.simulator.facade.result.carbon.CarbonEmitter;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 小水电机组功率计算 (Java 版)
 */
@Data
@Entity
@Table(name = "hydro_power_plant_model")
@AllArgsConstructor
@NoArgsConstructor
public class HydroPowerPlantModel implements Producer, CarbonEmitter {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String modelName;

    // 水轮机效率
    @Column(nullable = false)
    private BigDecimal eta1;

    // 发电机效率
    @Column(nullable = false)
    private BigDecimal eta2;

    // 机组传动效率
    @Column(nullable = false)
    private BigDecimal eta3;

    // 总效率 = eta1 * eta2 * eta3
    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private BigDecimal eta;

    // 上游水面相对于参考面的位能 (m)
    @Column(nullable = false)
    private BigDecimal z1;

    // 水轮机入口处相对于参考面的位能 (m)
    @Column(nullable = false)
    private BigDecimal z2;

    // 过水断面平均流速 v1
    @Column(nullable = false)
    private BigDecimal v1;

    // 过水断面平均流速 v2
    @Column(nullable = false)
    private BigDecimal v2;

    // 水密度 ρ1
    @Column(nullable = false)
    private BigDecimal p1;

    // 水密度 ρ2
    @Column(nullable = false)
    private BigDecimal p2;

    // ρg
    @Column(nullable = false)
    private BigDecimal pg;

    // 重力加速度g
    @Column(nullable = false)
    private BigDecimal g;

    // 碳排放因子 (kg CO₂ / m³)
    @Column(nullable = false)
    private BigDecimal carbonEmissionFactor;

    // 发电成本
    @Column(nullable = false)
    private BigDecimal cost;

    // 建设成本
    @Column(nullable = false)
    private BigDecimal purchaseCost;

    @Transient
    private List<ElectricEnergy> electricEnergyList = new ArrayList<>();

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
                .filter(env -> env instanceof WaterSpeedValue)
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
                .multiply(carbonEmissionFactor);
    }
}
