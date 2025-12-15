package org.esimulate.core.pojo.simulate.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StackedChartData {

    String name;

    List<BigDecimal> seriesData;

    Integer priority = 500;

    String stack;

    public StackedChartData(String name, List<BigDecimal> seriesData, Integer priority) {
        this.name = name;
        this.seriesData = seriesData.stream().map(x -> x.setScale(2, RoundingMode.HALF_UP)).collect(Collectors.toList());
        this.priority = priority;
        this.stack = "Total";
    }
}
