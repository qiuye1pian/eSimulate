package org.esimulate.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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


}
