package org.esimulate.core.pojo.simulate.result;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class StackedChartData {

    String name;

    List<BigDecimal> seriesData;

    Integer priority = 500;

    String stack;

    public StackedChartData(String name, List<BigDecimal> seriesData, Integer priority) {
        this.name = name;
        this.seriesData = seriesData;
        this.priority = priority;
        this.stack = "Total";
    }
}
