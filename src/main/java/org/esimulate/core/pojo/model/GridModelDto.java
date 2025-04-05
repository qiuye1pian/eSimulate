package org.esimulate.core.pojo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.esimulate.core.model.device.GridModel;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GridModelDto {

    private Long id;

    private String modelName;

    private BigDecimal gridPrice;

    // 碳排放因子
    private BigDecimal carbonEmissionFactor;

    // 单位运行维护成本
    private BigDecimal cost;

    // 建设成本
    private BigDecimal purchaseCost;

    public GridModelDto(GridModel gridModel) {
        this.id = gridModel.getId();
        this.modelName = gridModel.getModelName();
        this.gridPrice = gridModel.getGridPrice();
        this.carbonEmissionFactor = gridModel.getCarbonEmissionFactor();
        this.cost = gridModel.getCost();
        this.purchaseCost = gridModel.getPurchaseCost();
    }
}
