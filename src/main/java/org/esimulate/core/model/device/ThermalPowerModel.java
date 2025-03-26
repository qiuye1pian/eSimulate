package org.esimulate.core.model.device;

import lombok.Getter;
import org.esimulate.core.model.environment.sunlight.SunlightIrradianceValue;
import org.esimulate.core.model.result.energy.ThermalEnergy;
import org.esimulate.core.pso.simulator.facade.Producer;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentValue;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ThermalPowerModel implements Producer {

    // 常量：用于将 W 转换为 kW
    private static final BigDecimal KW_CONVERSION_FACTOR = new BigDecimal("1000");

    // 光热转换效率 (η_SF)
    private BigDecimal etaSF;

    // CSP 电站镜场面积 (S_SF, 单位: m²)
    private BigDecimal SSF;

    // 模型数量
    private int modelCount;

    // 碳排放因子
    @Column(nullable = false)
    private BigDecimal carbonEmissionFactor;

    // 发电成本
    @Column(nullable = false)
    private BigDecimal cost;

    // 建设成本
    @Column(nullable = false)
    private BigDecimal purchaseCost;

    // 每小时光热电站出力列表 (单位: kW)
    private final List<ThermalEnergy> thermalEnergyList = new ArrayList<>();


    /**
     * 计算单个时段的光热电站吸收热功率 (kW)。
     * <p>
     * 公式：P_th_solar(t) = η_SF * S_SF * D_t / 1000 * modelCount
     *
     * @param D_t 太阳光在时段 t 的平均直接辐射量 (DNI, 单位: W/m²)
     * @return t 时段的光热电站吸收热功率 (单位: kW)
     */
    private BigDecimal calculateThermalPower(BigDecimal D_t) {
        return etaSF.multiply(SSF).multiply(D_t)
                .divide(KW_CONVERSION_FACTOR, 10, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(modelCount));
    }

    @Override
    public Energy produce(List<EnvironmentValue> environmentValueList) {

        BigDecimal output = environmentValueList.stream()
                .filter(x -> x instanceof SunlightIrradianceValue)
                .map(EnvironmentValue::getValue)
                .map(this::calculateThermalPower)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        ThermalEnergy thermalEnergy = new ThermalEnergy(output);

        this.thermalEnergyList.add(thermalEnergy);

        return thermalEnergy;
    }

    @Override
    public BigDecimal getTotalEnergy() {
        return thermalEnergyList.stream()
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal calculateCarbonEmissions() {
        return BigDecimal.ZERO;
    }
}
