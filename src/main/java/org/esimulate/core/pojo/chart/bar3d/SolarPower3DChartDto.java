package org.esimulate.core.pojo.chart.bar3d;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
@Setter
@AllArgsConstructor
public class SolarPower3DChartDto {
    BigDecimal x;
    BigDecimal y;
    BigDecimal z;

    public String toPoint() {
        return Arrays.asList(x, y, z).toString();
    }
}
