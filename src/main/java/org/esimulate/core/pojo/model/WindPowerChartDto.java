package org.esimulate.core.pojo.model;

import lombok.Data;
import org.esimulate.core.pojo.chart.line.ChartLineDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
public class WindPowerChartDto extends ChartLineDto<BigDecimal, BigDecimal> {

    public WindPowerChartDto(List<BigDecimal> xAxisData, List<BigDecimal> seriesData) {
        if (xAxisData == null) {
            throw new IllegalArgumentException("xAxisData cannot be null");
        }

        if (seriesData == null) {
            throw new IllegalArgumentException("seriesData cannot be null");
        }

        BigDecimal yAxisMax = seriesData.stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO)
                .multiply(BigDecimal.valueOf(1.33))
                .setScale(0, RoundingMode.DOWN);

        this.init(xAxisData, seriesData, yAxisMax);

        this.getXAxis().getAxisLabel().setFormatter("{value} m/s");
    }
}

