package org.esimulate.core.pojo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.esimulate.core.model.device.ThermalSaverModel;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThermalSaverModelDto {

    private Long id;

    private String modelName;

    // 总热储能容量（例如单位：kWh）
    private BigDecimal totalStorageCapacity;

    // 当前热储能量
    private BigDecimal currentStorage;

    // 储热效率（例如：0.9表示90%的储热效率）
    private BigDecimal chargingEfficiency;

    // 放热效率（例如：0.85表示85%的放热效率）
    private BigDecimal dischargingEfficiency;

    // 热损失率（例如：0.05表示每个时段损失5%的储热能量）
    private BigDecimal thermalLossRate;

    // 碳排放因子
    private BigDecimal carbonEmissionFactor;

    // 建设成本
    private BigDecimal purchaseCost;

    public ThermalSaverModelDto(ThermalSaverModel thermalSaverModel) {
        this.id = thermalSaverModel.getId();
        this.modelName = thermalSaverModel.getModelName();
        this.totalStorageCapacity = thermalSaverModel.getTotalStorageCapacity();
        this.currentStorage = thermalSaverModel.getCurrentStorage();
        this.chargingEfficiency = thermalSaverModel.getChargingEfficiency();
        this.dischargingEfficiency = thermalSaverModel.getDischargingEfficiency();
        this.thermalLossRate = thermalSaverModel.getThermalLossRate();
        this.carbonEmissionFactor = thermalSaverModel.getCarbonEmissionFactor();
        this.purchaseCost = thermalSaverModel.getPurchaseCost();
    }
}
