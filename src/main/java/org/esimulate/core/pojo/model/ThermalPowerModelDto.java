package org.esimulate.core.pojo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThermalPowerModelDto {

    private Long id;

    private String modelName;

    // 光热转换效率 (η_SF)
    private BigDecimal etaSF;

    // CSP 电站镜场面积 (S_SF, 单位: m²)
    private BigDecimal SSF;

    // 碳排放因子
    private BigDecimal carbonEmissionFactor;

    // 发电成本
    private BigDecimal cost;

    // 建设成本
    private BigDecimal purchaseCost;

}
