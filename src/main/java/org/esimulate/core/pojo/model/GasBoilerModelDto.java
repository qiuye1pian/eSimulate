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
public class GasBoilerModelDto {


    private Long id;

    private String modelName;

    // 燃气锅炉的燃烧效率 (η_GB)
    private BigDecimal etaGB;

    // 燃气热值 (kWh/m³)
    private BigDecimal gasEnergyDensity;

    // 碳排放因子 (kg CO₂ / m³)
    private BigDecimal carbonEmissionFactor;

    // 发电成本
    private BigDecimal cost;

    // 建设成本
    private BigDecimal purchaseCost;
}
