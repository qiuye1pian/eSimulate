package org.esimulate.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Data
public class WindPowerChartDto {

    private XAxis xAxis;

    private YAxis yAxis;

    private List<Series> series;

    public WindPowerChartDto(List<String> xAxisData, List<BigDecimal> seriesData) {
        this.xAxis = new XAxis(xAxisData);
        this.series = Collections.singletonList(new Series(seriesData));
        BigDecimal max = seriesData.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        this.yAxis = new YAxis(max.multiply(BigDecimal.valueOf(1.1)));
    }


    @Getter
    @Setter
    @AllArgsConstructor
    public static class XAxis {
        private final String type = "category";
        private final boolean boundaryGap = false;
        private List<String> data;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class YAxis {
        private final BigDecimal max;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Series {
        private final String name = "";
        private final String type = "line";
        private List<BigDecimal> data;
    }

}

