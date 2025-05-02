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
public class ThermalPowerUnitModelDto {

    private Long id;

    private String modelName;

    // 最大出力 PMax
    private BigDecimal maxPower;

    // 最小出力 PMin
    private BigDecimal minPower;

    // 启动成本（元）
    private BigDecimal startupCost;

    // 成本函数系数 a
    private BigDecimal a;

    // 成本函数系数 b
    private BigDecimal b;

    // 成本函数系数 c
    private BigDecimal c;

    // 厂用电率 (%)
    private BigDecimal auxiliaryRate;

    // 碳排放系数（kg/kWh）
    private BigDecimal emissionRate;

    // 最小启动时间（小时）
    private int minStartupTime;

    // 最小停机时间（小时）
    private int minShutdownTime;

    // 当前运行状态：true 表示运行中，false 表示停机
    private Boolean runningStatus;

    // 碳排放因子
    private BigDecimal carbonEmissionFactor;

    // 维护成本
    private BigDecimal cost;

    // 建设成本
    private BigDecimal purchaseCost;

}
