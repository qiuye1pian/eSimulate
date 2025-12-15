package org.esimulate.core.pojo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.esimulate.core.model.device.CogenerationModel;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CogenerationModelDto {

    private Long id;

    private String modelName;

    // 最小供热功率 PMin (kW)
    private BigDecimal PMin;

    // 最大供热功率 PMax (kW)
    private BigDecimal PMax;

    // 向上爬坡速率（单位：kW）
    private BigDecimal rampUpRate;

    // 向下爬坡速率（单位：kW）
    private BigDecimal rampDownRate;

    // 发电效率
    private BigDecimal etaElectric;

    // 散热损失率
    private BigDecimal etaLoss;

    // 溴冷机的制热系数
    private BigDecimal COP;

    // 烟气回收率
    private BigDecimal flueGasRecoveryRate;

    // 运行成本系数 a ["CNY"⋅("MW"⋅"h" )^(-1)]
    private BigDecimal a;

    // 运行成本系数 b ["CNY"⋅("MW"⋅"h" )^(-1)]
    private BigDecimal b;

    // 运行成本系数 c ("CNY"⋅"h" ^(-1))
    private BigDecimal c;

    // Cv
    private BigDecimal cv;

    // 碳排放因子
    private BigDecimal carbonEmissionFactor;

    // 单位成本
    private BigDecimal cost;

    // 建设成本
    private BigDecimal purchaseCost;

    public CogenerationModelDto(CogenerationModel cogenerationModel) {
        this.id = cogenerationModel.getId();
        this.modelName = cogenerationModel.getModelName();
        this.PMin = cogenerationModel.getPMin();
        this.PMax = cogenerationModel.getPMax();
        this.rampUpRate = cogenerationModel.getRampUpRate();
        this.rampDownRate = cogenerationModel.getRampDownRate();
        this.etaElectric = cogenerationModel.getEtaElectric();
        this.etaLoss = cogenerationModel.getEtaLoss();
        this.COP = cogenerationModel.getCOP();
        this.flueGasRecoveryRate = cogenerationModel.getFlueGasRecoveryRate();
        this.a = cogenerationModel.getA();
        this.b = cogenerationModel.getB();
        this.c = cogenerationModel.getC();
        this.cv = cogenerationModel.getCv();
        this.carbonEmissionFactor = cogenerationModel.getCarbonEmissionFactor();
        this.cost = cogenerationModel.getCost();
        this.purchaseCost = cogenerationModel.getPurchaseCost();
    }
}
