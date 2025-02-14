package org.core.model.device;

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
public class HydroPowerPlantModel implements Producer, CarbonEmitter {

    // 水轮机效率
    private final BigDecimal eta1;

    // 发电机效率
    private final BigDecimal eta2;

    // 机组传动效率
    private final BigDecimal eta3;

    // 总效率 = eta1 * eta2 * eta3
    private final BigDecimal eta;

    private final List<ElectricEnergy> electricEnergyList;

    /**
     * 构造函数
     *
     * @param eta1 水轮机效率
     * @param eta2 发电机效率
     * @param eta3 机组传动效率
     */
    public HydroPowerPlantModel(String eta1, String eta2, String eta3) {
        this.eta1 = new BigDecimal(eta1);
        this.eta2 = new BigDecimal(eta2);
        this.eta3 = new BigDecimal(eta3);
        this.eta = this.eta1.multiply(this.eta2).multiply(this.eta3).setScale(10, RoundingMode.HALF_UP);
        this.electricEnergyList = new ArrayList<>();
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
     * @return 水头 (m)
     */
    public BigDecimal calculateHead(String Z1, String p1, String v1,
                                    String Z2, String p2, String v2,
                                    String rho, String g) {

        BigDecimal bdZ1 = new BigDecimal(Z1);
        BigDecimal bdp1 = new BigDecimal(p1);
        BigDecimal bdv1 = new BigDecimal(v1);
        BigDecimal bdZ2 = new BigDecimal(Z2);
        BigDecimal bdp2 = new BigDecimal(p2);
        BigDecimal bdv2 = new BigDecimal(v2);
        BigDecimal bdrho = new BigDecimal(rho);
        BigDecimal bdg = new BigDecimal(g);

        // 计算 H = [Z1 + p1/(rho*g) + v1^2/(2*g)] - [Z2 + p2/(rho*g) + v2^2/(2*g)]
        BigDecimal head1 = bdZ1.add(bdp1.divide(bdrho.multiply(bdg), 10, RoundingMode.HALF_UP))
                .add(bdv1.pow(2).divide(bdg.multiply(new BigDecimal("2")), 10, RoundingMode.HALF_UP));

        BigDecimal head2 = bdZ2.add(bdp2.divide(bdrho.multiply(bdg), 10, RoundingMode.HALF_UP))
                .add(bdv2.pow(2).divide(bdg.multiply(new BigDecimal("2")), 10, RoundingMode.HALF_UP));

        return head1.subtract(head2).setScale(10, RoundingMode.HALF_UP);
    }

    /**
     * 不带 rho 和 g 参数的重载方法，使用默认值：rho=1000 kg/m³, g=9.81 m/s²
     */
    public BigDecimal calculateHead(String Z1, String p1, String v1,
                                    String Z2, String p2, String v2) {
        return calculateHead(Z1, p1, v1, Z2, p2, v2, "1000", "9.81");
    }

    /**
     * 计算水电机组输出功率
     *
     * @param Q 流量 (m³/s)
     * @param H 水头 (m)
     * @return 水电机组输出功率 (kW)
     */
    public BigDecimal calculatePower(String Q, String H) {
        BigDecimal bdQ = new BigDecimal(Q);
        BigDecimal bdH = new BigDecimal(H);

        // P_h = 9.81 * eta * Q * H
        BigDecimal gravity = new BigDecimal("9.81");
        return gravity.multiply(this.eta).multiply(bdQ).multiply(bdH).setScale(10, RoundingMode.HALF_UP);
    }


    @Override
    public BigDecimal calculateCarbonEmissions() {
        return null;
    }

    @Override
    public Energy produce(List<EnvironmentValue> environmentValueList) {
        // 1. 提取环境变量中的流量 Q 和水头 H
        BigDecimal Q = environmentValueList.stream()
                .filter(env -> env instanceof WaterSpeed )
                .map(EnvironmentValue::getValue)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        BigDecimal H =  BigDecimal.ZERO;
        //todo:计算H
                //calculateHead(this.,"")

        // 2. 计算水电机组输出功率 (kW)
        BigDecimal power = calculatePower(Q.toString(), H.toString());

        // 3. 将计算出的电能存入历史记录
        ElectricEnergy generatedEnergy = new ElectricEnergy(power);
        this.electricEnergyList.add(generatedEnergy);

        // 4. 返回当前时间点的发电量
        return generatedEnergy;
    }
    @Override
    public BigDecimal getTotalEnergy() {
        return null;
    }
}
