package org.esimulate.core.pojo.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HydroPowerPlantModelDto {

    private Long id;

    /**
     * 模型名
     */
    private String modelName;

    // 水轮机效率
    private BigDecimal eta1;

    // 发电机效率
    private BigDecimal eta2;

    // 机组传动效率
    private BigDecimal eta3;

    // 上游水面相对于参考面的位能 (m)
    private BigDecimal z1;

    // 水轮机入口处相对于参考面的位能 (m)
    private BigDecimal z2;

    // 过水断面平均流速 v1
    private BigDecimal v1;

    // 过水断面平均流速 v2
    private BigDecimal v2;

    // 水密度 ρ1
    private BigDecimal p1;

    // 水密度 ρ2
    private BigDecimal p2;

    // ρg
    private BigDecimal pg;

    // 重力加速度g
    private BigDecimal g;

    // 碳排放因子 (kg CO₂ / m³)
    private BigDecimal carbonEmissionFactor;

    // 发电成本
    private BigDecimal cost;

    // 建设成本
    private BigDecimal purchaseCost;

}
