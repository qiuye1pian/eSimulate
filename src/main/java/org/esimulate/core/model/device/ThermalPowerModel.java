package org.esimulate.core.model.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.environment.sunlight.SunlightIrradianceValue;
import org.esimulate.core.model.result.energy.ThermalEnergy;
import org.esimulate.core.pojo.model.ThermalPowerModelDto;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.Producer;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentValue;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;
import org.esimulate.core.pso.simulator.result.StackedChartData;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "thermal_power_model")
@AllArgsConstructor
@NoArgsConstructor
public class ThermalPowerModel extends Device implements Producer {

    // 常量：用于将 W 转换为 kW
    private static final BigDecimal KW_CONVERSION_FACTOR = new BigDecimal("1000");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String modelName;

    // 光热转换效率 (η_SF)
    @Column(nullable = false)
    private BigDecimal etaSF;

    // CSP 电站镜场面积 (S_SF, 单位: m²)
    @Column(nullable = false)
    private BigDecimal SSF;

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

    @Transient
    // 每小时光热电站出力列表 (单位: kW)
    private List<ThermalEnergy> thermalEnergyList = new ArrayList<>();

    public ThermalPowerModel(ThermalPowerModelDto thermalPowerModelDto) {
        this.modelName = thermalPowerModelDto.getModelName();
        this.etaSF = thermalPowerModelDto.getEtaSF();
        this.SSF = thermalPowerModelDto.getSSF();
        this.carbonEmissionFactor = thermalPowerModelDto.getCarbonEmissionFactor();
        this.cost = thermalPowerModelDto.getCost();
        this.purchaseCost = thermalPowerModelDto.getPurchaseCost();
    }

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
                .divide(KW_CONVERSION_FACTOR, 10, RoundingMode.HALF_UP);
    }

    @Override
    public Energy produce(List<EnvironmentValue> environmentValueList) {
        BigDecimal output = environmentValueList.stream()
                .filter(x -> x instanceof SunlightIrradianceValue)
                .map(EnvironmentValue::getValue)
                .map(this::calculateThermalPower)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO)
                .multiply(this.quantity);

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

    @Override
    protected BigDecimal getDiscountRate() {
        return BigDecimal.valueOf(0.07);
    }

    @Override
    protected Integer getLifetimeYears() {
        return 20;
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
        List<BigDecimal> collect = this.thermalEnergyList.stream().map(ThermalEnergy::getValue).collect(Collectors.toList());
        StackedChartData stackedChartData = new StackedChartData(this.modelName, collect, 200);
        return Collections.singletonList(stackedChartData);
    }

    @Override
    public ThermalPowerModel clone() {
        ThermalPowerModel clone = (ThermalPowerModel) super.clone();

        // 深拷贝 BigDecimal 字段
        clone.etaSF = new BigDecimal(this.etaSF.toString());
        clone.SSF = new BigDecimal(this.SSF.toString());
        clone.carbonEmissionFactor = new BigDecimal(this.carbonEmissionFactor.toString());
        clone.cost = new BigDecimal(this.cost.toString());
        clone.purchaseCost = new BigDecimal(this.purchaseCost.toString());

        // 深拷贝 Timestamp
        clone.updatedAt = new Timestamp(this.updatedAt.getTime());

        // 字符串字段直接赋值（不可变类型）
        clone.modelName = this.modelName;

        // id 字段复制（如需排除可移除）
        clone.id = this.id;

        // thermalEnergyList 为 @Transient 字段，不拷贝

        return clone;
    }
}
