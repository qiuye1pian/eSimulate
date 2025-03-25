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
public class WindPowerModelDto {

    private Long id;

    /**
     * 模型名
     */
    private String modelName;

    // 切入风速 (m/s)
    private BigDecimal v_in;

    // 额定风速 (m/s)
    private BigDecimal v_n;

    // 切出风速 (m/s)
    private BigDecimal v_out;

    // 额定功率 (kW)
    private BigDecimal P_r;

    // 碳排放因子
    private BigDecimal carbonEmissionFactor;

    // 发电成本
    private BigDecimal cost;

    // 建设成本
    private BigDecimal purchaseCost;
}
