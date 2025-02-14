package org.core.model.device;

import org.core.model.environment.wind.WindSpeedData;
import org.core.model.result.energy.ElectricEnergy;
import org.core.pso.simulator.facade.Producer;
import org.core.pso.simulator.facade.environment.EnvironmentValue;
import org.core.pso.simulator.facade.result.energy.Energy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 风力发电功率计算器
 */
public class WindPowerModel implements Producer {

    // 切入风速 (m/s)
    private final BigDecimal v_in;

    // 额定风速 (m/s)
    private final BigDecimal v_n;

    // 切出风速 (m/s)
    private final BigDecimal v_out;

    // 额定功率 (kW)
    private final BigDecimal P_r;

    // 每个时刻所法的电量 (kWh)
    private final List<ElectricEnergy> electricEnergyList;

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
        this.electricEnergyList = new ArrayList<>();
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
        else if (v_speed.compareTo(v_in) > 0 && v_speed.compareTo(v_n) <= 0) {
            BigDecimal numerator = v_speed.pow(2).subtract(v_in.pow(2));
            BigDecimal denominator = v_n.pow(2).subtract(v_in.pow(2));
            return new ElectricEnergy(numerator.divide(denominator, 10, RoundingMode.HALF_UP).multiply(P_r));
        }
        // 3. 额定风速 < v <= 切出风速 -> 输出额定功率
        else if (v_speed.compareTo(v_n) > 0 && v_speed.compareTo(v_out) <= 0) {
            return new ElectricEnergy(P_r);
        }
        // 4. 超过切出风速 -> 输出 0
        else {
            return new ElectricEnergy(BigDecimal.ZERO);
        }
    }


    @Override
    public Energy produce(List<EnvironmentValue> environmentValueList) {
        BigDecimal windSpeed = environmentValueList.stream()
                .filter(x -> x instanceof WindSpeedData)
                .findAny()
                .map(EnvironmentValue::getValue)
                .orElse(BigDecimal.ZERO);

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
