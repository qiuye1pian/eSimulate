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
public class BatteryModelDto {
    
    private Long id;

    private String modelName;

    // 蓄电池总容量 (Wh)
    private BigDecimal Ct;

    // SOC 最小值 (0~1)
    private BigDecimal SOCMin;

    // SOC 最大值 (0~1)
    private BigDecimal SOCMax;

    // 自放电损失率 (无量纲)
    private BigDecimal mu;

    // 最大充电功率 (W)
    private BigDecimal etaHch;

    // 最大放电功率 (W)
    private BigDecimal etaHDis;

    // 当前储电量 (Wh)
    private BigDecimal EESSt;

    // 碳排放因子
    private BigDecimal carbonEmissionFactor;

    // 建设成本
    private BigDecimal purchaseCost;

}
