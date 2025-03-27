package org.esimulate.core.pojo.chart.bar3d;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Deprecated
public class SolarPower3DChartDto {
    BigDecimal x;
    BigDecimal y;
    BigDecimal z;

    public List<BigDecimal> toPoint() {
        return Arrays.asList(x, y, z);
    }
}
