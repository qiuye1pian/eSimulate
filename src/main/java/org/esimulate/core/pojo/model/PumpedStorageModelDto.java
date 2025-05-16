package org.esimulate.core.pojo.model;

import lombok.*;
import org.esimulate.core.model.device.PumpedStorageModel;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PumpedStorageModelDto {

    private Long id;

    private String modelName;

    // 最大抽水（蓄能）或放水（发电）功率（千瓦）
    private BigDecimal Pmax;

    // 上游水库最大储能容量（千瓦时）
    private BigDecimal Emax;

    // 抽水（蓄能）效率（0-1）
    private BigDecimal etaCh;

    // 放水（发电）效率（0-1）
    private BigDecimal etaDis;

    // 日/周能量平衡系数（λ）
    private BigDecimal lambda;

    // 当前水库储能（千瓦时）
    private BigDecimal stateOfCharge;

    // 碳排放因子
    private BigDecimal carbonEmissionFactor;

    // 发电成本
    private BigDecimal cost;

    // 建设成本
    private BigDecimal purchaseCost;

    public PumpedStorageModelDto(PumpedStorageModel pumpedStorageModel){
        this.id = pumpedStorageModel.getId();
        this.modelName = pumpedStorageModel.getModelName();
        this.Pmax = pumpedStorageModel.getPmax();
        this.Emax = pumpedStorageModel.getEmax();
        this.etaCh = pumpedStorageModel.getEtaCh();
        this.etaDis = pumpedStorageModel.getEtaDis();
        this.lambda = pumpedStorageModel.getLambda();
        this.stateOfCharge = pumpedStorageModel.getStateOfCharge();
        this.carbonEmissionFactor = pumpedStorageModel.getCarbonEmissionFactor();
        this.cost = pumpedStorageModel.getCost();
        this.purchaseCost = pumpedStorageModel.getPurchaseCost();
    }
}
