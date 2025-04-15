package org.esimulate.core.model.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.environment.wind.WindSpeedValue;
import org.esimulate.core.model.result.energy.ElectricEnergy;
import org.esimulate.core.pojo.model.WindPowerModelDto;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.Producer;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentValue;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;
import org.esimulate.core.pojo.simulate.result.StackedChartData;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 风力发电功率计算器
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "wind_power_model")
@AllArgsConstructor
@NoArgsConstructor
public class WindPowerModel extends Device implements Producer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String modelName;

    // 切入风速 (m/s)
    @Column(nullable = false)
    private BigDecimal v_in;

    // 额定风速 (m/s)
    @Column(nullable = false)
    private BigDecimal v_n;

    // 切出风速 (m/s)
    @Column(nullable = false)
    private BigDecimal v_out;

    // 额定功率 (kW)
    @Column(nullable = false)
    private BigDecimal P_r;

    // 碳排放因子
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

    // 每个时刻所发的电量 (kWh)
    @Transient
    private List<ElectricEnergy> electricEnergyList = new ArrayList<>();

    public WindPowerModel(WindPowerModelDto windPowerModelDto) {
        this.modelName = windPowerModelDto.getModelName();
        this.v_in = windPowerModelDto.getV_in();
        this.v_n = windPowerModelDto.getV_n();
        this.v_out = windPowerModelDto.getV_out();
        this.P_r = windPowerModelDto.getP_r();
        this.carbonEmissionFactor = windPowerModelDto.getCarbonEmissionFactor();
        this.cost = windPowerModelDto.getCost();
        this.purchaseCost = windPowerModelDto.getPurchaseCost();
    }

    /**
     * 计算给定风速下的风机出力
     *
     * @param v_speed 当前风速 (m/s)
     * @return 风机输出功率 (kW)
     */
    private ElectricEnergy calculatePower(BigDecimal v_speed) {
        // 1. 低于或等于切入风速 -> 输出 0
        if (v_speed.compareTo(v_in) <= 0) {
            return new ElectricEnergy(BigDecimal.ZERO);
        }
        // 2. 切入风速 < v <= 额定风速 -> 二次插值计算输出
        if (v_speed.compareTo(v_in) > 0 && v_speed.compareTo(v_n) <= 0) {
            BigDecimal numerator = v_speed.pow(2).subtract(v_in.pow(2));
            BigDecimal denominator = v_n.pow(2).subtract(v_in.pow(2));
            return new ElectricEnergy(numerator.divide(denominator, 10, RoundingMode.HALF_UP).multiply(P_r));
        }
        // 3. 额定风速 < v <= 切出风速 -> 输出额定功率
        if (v_speed.compareTo(v_n) > 0 && v_speed.compareTo(v_out) <= 0) {
            return new ElectricEnergy(P_r);
        }
        // 4. 超过切出风速 -> 输出 0
        return new ElectricEnergy(BigDecimal.ZERO);

    }


    @Override
    public Energy produce(List<EnvironmentValue> environmentValueList) {
        BigDecimal windSpeed = environmentValueList.stream()
                .filter(x -> x instanceof WindSpeedValue)
                .findAny()
                .map(EnvironmentValue::getValue)
                .orElse(BigDecimal.ZERO)
                .multiply(this.quantity);

        ElectricEnergy currentEnergy = calculatePower(windSpeed);

        this.electricEnergyList.add(currentEnergy);

        return currentEnergy;
    }

    @Override
    public BigDecimal getTotalEnergy() {
        return this.electricEnergyList.stream().map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal calculateCarbonEmissions() {
        return BigDecimal.ZERO;
    }

    @Override
    protected BigDecimal getDiscountRate() {
        return BigDecimal.valueOf(0.07);
    }

    @Override
    protected Integer getLifetimeYears() {
        return 25;
    }

    @Override
    protected BigDecimal getCostOfOperation() {
        return getTotalEnergy()
                .multiply(quantity)
                .multiply(cost)
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
        StackedChartData stackedChartData = new StackedChartData(this.modelName,collect,400);
        return Collections.singletonList(stackedChartData);
    }

    @Override
    public WindPowerModel clone() {
        WindPowerModel clone = (WindPowerModel) super.clone();

        // 深拷贝 BigDecimal 字段
        clone.v_in = new BigDecimal(this.v_in.toString());
        clone.v_n = new BigDecimal(this.v_n.toString());
        clone.v_out = new BigDecimal(this.v_out.toString());
        clone.P_r = new BigDecimal(this.P_r.toString());
        clone.carbonEmissionFactor = new BigDecimal(this.carbonEmissionFactor.toString());
        clone.cost = new BigDecimal(this.cost.toString());
        clone.purchaseCost = new BigDecimal(this.purchaseCost.toString());

        // 深拷贝 Timestamp
        clone.updatedAt = new Timestamp(this.updatedAt.getTime());

        // 字符串字段直接复制
        clone.modelName = this.modelName;

        // id 字段保留
        clone.id = this.id;

        // electricEnergyList 为 @Transient 字段，不拷贝

        return clone;
    }

}
