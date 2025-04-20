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

    public GridModelDto(GridModel gridModel) {
        this.id = gridModel.getId();
        this.modelName = gridModel.getModelName();
        this.gridPrice = gridModel.getGridPrice();
        this.carbonEmissionFactor = gridModel.getCarbonEmissionFactor();
    }
}
