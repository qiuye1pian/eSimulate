package org.esimulate.core.pojo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.esimulate.core.model.device.SolarPowerModel;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolarPowerModelDto {

    private Long id;

    /**
     * 模型名
     */
    private String modelName;

    // 光伏系统额定功率 (kW)
    private BigDecimal ppvN;

    // 光伏组件温度系数 (1/℃)，通常为负值
    private BigDecimal te;

    // 参考温度 (℃)
    private BigDecimal tref;

    // 参考辐照度 (W/m²)
    private BigDecimal gref;

    // 碳排放因子
    private BigDecimal carbonEmissionFactor;

    // 发电成本
    private BigDecimal cost;

    // 建设成本
    private BigDecimal purchaseCost;


    public SolarPowerModelDto(SolarPowerModel solarPowerModel) {
        this.id = solarPowerModel.getId();
        this.modelName = solarPowerModel.getModelName();
        this.ppvN = solarPowerModel.getP_pvN();
        this.te = solarPowerModel.getT_e();
        this.tref = solarPowerModel.getT_ref();
        this.gref = solarPowerModel.getG_ref();
        this.carbonEmissionFactor = solarPowerModel.getG_ref();
        this.cost = solarPowerModel.getCost();
        this.purchaseCost = solarPowerModel.getPurchaseCost();

    }
}
