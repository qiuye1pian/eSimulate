package org.esimulate.core.pso.simulator.result;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.esimulate.core.pojo.chart.line.ChartLineDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class StackedChartDto extends ChartLineDto<String, BigDecimal> {

    public StackedChartDto(List<String> xAxisData, List<StackedChartData> stackedChartData) {
        if (xAxisData == null) {
            throw new IllegalArgumentException("xAxisData cannot be null");
        }

        if (stackedChartData == null) {
            throw new IllegalArgumentException("seriesData cannot be null");
        }

        List<Series<BigDecimal>> seriesData = stackedChartData.stream()
                .map(x -> new Series<>(x.getName(), x.getSeriesData()))
                .collect(Collectors.toList());

        this.init(xAxisData, seriesData, "dataMax");

        this.getXAxis().getAxisLabel().setFormatter("{value}");

    }

}
