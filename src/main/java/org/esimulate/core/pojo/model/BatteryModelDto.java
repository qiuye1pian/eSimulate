package org.esimulate.core.pojo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.esimulate.core.model.device.BatteryModel;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BatteryModelDto {

    private Long id;

    private String modelName;

    // 蓄电池总容量 (Wh)
    private BigDecimal ct;

    // SOC 最小值 (0~1)
    private BigDecimal SOCMin;

    // SOC 最大值 (0~1)
    private BigDecimal SOCMax;

    // 自放电损失率 (无量纲)
    private BigDecimal mu;

    // 最大充电功率 (W)
    private BigDecimal maxChargePower;

    // 最大放电功率 (W)
    private BigDecimal maxDischargePower;

    // 充电效率
    private BigDecimal etaHch;

    // 放电效率
    private BigDecimal etaHDis;

    // 当前储电量 (Wh)
    private BigDecimal EESSt;

    // 碳排放因子
    private BigDecimal carbonEmissionFactor;

    // 维护成本
    private BigDecimal cost;

    // 建设成本
    private BigDecimal purchaseCost;

    public BatteryModelDto(BatteryModel batteryModel) {
        this.id = batteryModel.getId();
        this.modelName = batteryModel.getModelName();
        this.ct = batteryModel.getC_t();
        this.SOCMax = batteryModel.getSOC_max();
        this.SOCMin = batteryModel.getSOC_min();
        this.mu = batteryModel.getMu();
        this.maxChargePower = batteryModel.getMaxChargePower();
        this.maxDischargePower = batteryModel.getMaxDischargePower();
        this.etaHch = batteryModel.getEtaHch();
        this.etaHDis = batteryModel.getEtaHdis();
        this.EESSt = batteryModel.getE_ESS_t();
        this.carbonEmissionFactor = batteryModel.getCarbonEmissionFactor();
        this.cost = batteryModel.getCost();
        this.purchaseCost = batteryModel.getPurchaseCost();
    }
}
