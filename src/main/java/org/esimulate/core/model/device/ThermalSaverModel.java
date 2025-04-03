package org.esimulate.core.model.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.result.energy.ElectricEnergy;
import org.esimulate.core.model.result.energy.ThermalEnergy;
import org.esimulate.core.pojo.model.ThermalSaverModelDto;
import org.esimulate.core.pso.simulator.facade.Storage;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "thermal_saver_model")
@AllArgsConstructor
@NoArgsConstructor
public class ThermalSaverModel implements Storage, Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String modelName;

    // 总热储能容量（例如单位：kWh）
    @Column(nullable = false)
    private BigDecimal totalStorageCapacity;

    // 当前热储能量
    @Column(nullable = false)
    private BigDecimal currentStorage;

    // 储热效率（例如：0.9表示90%的储热效率）
    @Column(nullable = false)
    private BigDecimal chargingEfficiency;

    // 放热效率（例如：0.85表示85%的放热效率）
    @Column(nullable = false)
    private BigDecimal dischargingEfficiency;

    // 热损失率（例如：0.05表示每个时段损失5%的储热能量）
    @Column(nullable = false)
    private BigDecimal thermalLossRate;

    // 碳排放因子
    @Column(nullable = false)
    private BigDecimal carbonEmissionFactor;

    // 建设成本
    @Column(nullable = false)
    private BigDecimal purchaseCost;

    @Transient
    private BigDecimal quantity = BigDecimal.ONE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Transient
    // 每个时刻电池的剩余电量 (Wh)
    private List<ElectricEnergy> E_ESS_LIST = new ArrayList<>();

    public ThermalSaverModel(ThermalSaverModelDto thermalSaverModelDto) {
        this.id = thermalSaverModelDto.getId();
        this.modelName = thermalSaverModelDto.getModelName();
        this.totalStorageCapacity = thermalSaverModelDto.getTotalStorageCapacity();
        this.currentStorage = thermalSaverModelDto.getCurrentStorage();
        this.chargingEfficiency = thermalSaverModelDto.getChargingEfficiency();
        this.dischargingEfficiency = thermalSaverModelDto.getDischargingEfficiency();
        this.thermalLossRate = thermalSaverModelDto.getThermalLossRate();
        this.carbonEmissionFactor = thermalSaverModelDto.getCarbonEmissionFactor();
        this.purchaseCost = thermalSaverModelDto.getPurchaseCost();
    }

    @Override
    public Energy storage(List<Energy> differenceList) {
        // 求和得到热能变化值
        BigDecimal thermalEnergyDifference = differenceList.stream()
                .filter(x -> x instanceof ThermalEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        // 考虑热损失
        currentStorage = currentStorage.multiply(BigDecimal.ONE.subtract(thermalLossRate));

        // 根据能量变化更新当前储热量
        if (thermalEnergyDifference.compareTo(BigDecimal.ZERO) > 0) {
            // 储热，考虑储热效率
            BigDecimal effectiveCharge = thermalEnergyDifference.multiply(chargingEfficiency);
            currentStorage = currentStorage.add(effectiveCharge);
        } else {
            // 放热，考虑放热效率（注意：thermalEnergyDifference 为负值）
            BigDecimal effectiveDischarge = thermalEnergyDifference.divide(dischargingEfficiency, RoundingMode.HALF_UP);
            currentStorage = currentStorage.add(effectiveDischarge);
        }

        // 限制当前储热量不能超过总容量，也不能低于零
        if (currentStorage.compareTo(BigDecimal.ZERO) < 0) {
            currentStorage = BigDecimal.ZERO;
        } else if (currentStorage.compareTo(totalStorageCapacity.multiply(quantity)) > 0) {
            currentStorage = totalStorageCapacity.multiply(quantity);
        }

        // 返回一个新的 ThermalEnergy 对象（这里仍以热能差值表示，可根据需要调整返回逻辑）
        return new ThermalEnergy(thermalEnergyDifference);
    }

    @Override
    public BigDecimal calculateCarbonEmissions() {
        return BigDecimal.ZERO;
    }
}