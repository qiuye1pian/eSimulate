package org.esimulate.core.pojo.load;

import org.esimulate.core.pojo.chart.line.ChartLineDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

public class LoadValueChartDto extends ChartLineDto<String, BigDecimal> {

    public LoadValueChartDto(List<String> xAxisData, List<BigDecimal> seriesData) {
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

        this.init(xAxisData, Collections.singletonList(new Series<>("", seriesData)), yAxisMax.toString());

        this.getXAxis().getAxisLabel().setFormatter("{value}");

    }
}
