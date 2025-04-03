package org.esimulate.core.model.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.environment.wind.WindSpeedValue;
import org.esimulate.core.model.result.energy.ElectricEnergy;
import org.esimulate.core.pojo.model.WindPowerModelDto;
import org.esimulate.core.pso.simulator.facade.Producer;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentValue;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 风力发电功率计算器
 */
@Data
@Entity
@Table(name = "wind_power_model")
@AllArgsConstructor
@NoArgsConstructor
public class WindPowerModel implements Producer, Device {

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

    @Transient
    private BigDecimal quantity;

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
}
