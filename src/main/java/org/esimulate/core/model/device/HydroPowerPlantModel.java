package org.esimulate.core.model.device;

import lombok.*;
import org.esimulate.core.model.environment.water.WaterSpeedValue;
import org.esimulate.core.model.result.energy.ElectricEnergy;
import org.esimulate.core.pojo.model.HydroPowerPlantModelDto;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.Producer;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentValue;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;
import org.esimulate.core.pso.simulator.result.StackedChartData;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 小水电机组功率计算 (Java 版)
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "hydro_power_plant_model")
@AllArgsConstructor
@NoArgsConstructor
public class HydroPowerPlantModel extends Device implements Producer {

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

    // 水头H
    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private BigDecimal head;

    // 碳排放因子 (kg CO₂ / m³)
    @Column(nullable = false)
    private BigDecimal carbonEmissionFactor;

    // 发电成本
    @Column(nullable = false)
    private BigDecimal cost;

    // 建设成本
    @Column(nullable = false)
    private BigDecimal purchaseCost;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Transient
    private List<ElectricEnergy> electricEnergyList = new ArrayList<>();

    public HydroPowerPlantModel(HydroPowerPlantModelDto hydroPowerPlantModelDto) {
        this.modelName = hydroPowerPlantModelDto.getModelName();
        this.eta1 = hydroPowerPlantModelDto.getEta1();
        this.eta2 = hydroPowerPlantModelDto.getEta2();
        this.eta3 = hydroPowerPlantModelDto.getEta3();
        this.eta = calculateEta();
        this.z1 = hydroPowerPlantModelDto.getZ1();
        this.z2 = hydroPowerPlantModelDto.getZ2();
        this.v1 = hydroPowerPlantModelDto.getV1();
        this.v2 = hydroPowerPlantModelDto.getV2();
        this.p1 = hydroPowerPlantModelDto.getP1();
        this.p2 = hydroPowerPlantModelDto.getP2();
        this.pg = hydroPowerPlantModelDto.getPg();
        this.g = hydroPowerPlantModelDto.getG();
        this.carbonEmissionFactor = hydroPowerPlantModelDto.getCarbonEmissionFactor();
        this.cost = hydroPowerPlantModelDto.getCost();
        this.purchaseCost = hydroPowerPlantModelDto.getPurchaseCost();
        this.head = calculateHead();
    }

    /**
     * 计算水头 H
     *
     * @return 水头 (m)
     */
    private BigDecimal calculateHead() {
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
        return head1.subtract(head2).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算eta
     * 总效率 = eta1 * eta2 * eta3
     * @return 总效率
     */
    private @NotNull BigDecimal calculateEta() {
        return eta1.multiply(eta2).multiply(eta3).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算水电机组输出功率
     *
     * @param Q 流量 (m³/s)
     * @return 水电机组输出功率 (kW)
     */
    public BigDecimal calculatePower(BigDecimal Q) {
        // 水头H
        // P_h = 9.81 * eta * Q * H
        BigDecimal gravity = new BigDecimal("9.81");
        return gravity.multiply(this.eta)
                .multiply(Q)
                .multiply(this.head)
                .setScale(10, RoundingMode.HALF_UP)
                .multiply(this.quantity);
    }

    @Override
    public Energy produce(List<EnvironmentValue> environmentValueList) {
        // 1. 提取环境变量中的流量 Q
        BigDecimal Q = environmentValueList.stream()
                .filter(env -> env instanceof WaterSpeedValue)
                .map(EnvironmentValue::getValue)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        // 2. 计算水电机组输出功率 (kW)
        BigDecimal power = calculatePower(Q);

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

    public void modifyByDto(HydroPowerPlantModelDto hydroPowerPlantModelDto) {
        this.eta1 = hydroPowerPlantModelDto.getEta1();
        this.eta2 = hydroPowerPlantModelDto.getEta2();
        this.eta3 = hydroPowerPlantModelDto.getEta3();
        this.eta = calculateEta();
        this.z1 = hydroPowerPlantModelDto.getZ1();
        this.z2 = hydroPowerPlantModelDto.getZ2();
        this.v1 = hydroPowerPlantModelDto.getV1();
        this.v2 = hydroPowerPlantModelDto.getV2();
        this.p1 = hydroPowerPlantModelDto.getP1();
        this.p2 = hydroPowerPlantModelDto.getP2();
        this.pg = hydroPowerPlantModelDto.getPg();
        this.g = hydroPowerPlantModelDto.getG();
        this.carbonEmissionFactor = hydroPowerPlantModelDto.getCarbonEmissionFactor();
        this.cost = hydroPowerPlantModelDto.getCost();
        this.purchaseCost = hydroPowerPlantModelDto.getPurchaseCost();
        this.head = calculateHead();
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }


    @Override
    protected BigDecimal getDiscountRate() {
        return BigDecimal.valueOf(0.05);
    }

    @Override
    protected Integer getLifetimeYears() {
        return 50;
    }

    @Override
    protected BigDecimal getCostOfOperation() {
        return getTotalEnergy()
                .multiply(quantity)
                .multiply(this.cost)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    protected BigDecimal getCostOfGrid() {
        return BigDecimal.ZERO;
    }

    @Override
    protected BigDecimal getCostOfControl() {
        return BigDecimal.ZERO;
    }

    @Override
    public List<StackedChartData> getStackedChartDataList() {
        List<BigDecimal> collect = this.electricEnergyList.stream().map(ElectricEnergy::getValue).collect(Collectors.toList());
        StackedChartData stackedChartData = new StackedChartData(this.modelName, collect, 100);
        return Collections.singletonList(stackedChartData);
    }

    @Override
    public HydroPowerPlantModel clone() {
        HydroPowerPlantModel clone = (HydroPowerPlantModel) super.clone();

        // 深拷贝 BigDecimal 类型字段
        clone.eta1 = new BigDecimal(this.eta1.toString());
        clone.eta2 = new BigDecimal(this.eta2.toString());
        clone.eta3 = new BigDecimal(this.eta3.toString());
        clone.eta = new BigDecimal(this.eta.toString());
        clone.z1 = new BigDecimal(this.z1.toString());
        clone.z2 = new BigDecimal(this.z2.toString());
        clone.v1 = new BigDecimal(this.v1.toString());
        clone.v2 = new BigDecimal(this.v2.toString());
        clone.p1 = new BigDecimal(this.p1.toString());
        clone.p2 = new BigDecimal(this.p2.toString());
        clone.pg = new BigDecimal(this.pg.toString());
        clone.g = new BigDecimal(this.g.toString());
        clone.head = new BigDecimal(this.head.toString());
        clone.carbonEmissionFactor = new BigDecimal(this.carbonEmissionFactor.toString());
        clone.cost = new BigDecimal(this.cost.toString());
        clone.purchaseCost = new BigDecimal(this.purchaseCost.toString());

        // 深拷贝 Timestamp
        clone.updatedAt = new Timestamp(this.updatedAt.getTime());

        // String 字段直接赋值（不可变）
        clone.modelName = this.modelName;

        // id 字段直接复制
        clone.id = this.id;

        return clone;
    }
}
