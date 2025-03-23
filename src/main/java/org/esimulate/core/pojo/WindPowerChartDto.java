package org.esimulate.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

@Data
public class WindPowerChartDto {

    private XAxis xAxis;

    private YAxis yAxis;

    private List<Series> series;

    public WindPowerChartDto(List<BigDecimal> xAxisData, List<BigDecimal> seriesData) {
        if (xAxisData == null || seriesData == null) {
            throw new NullPointerException();
        }

        this.xAxis = new XAxis(xAxisData);
        this.series = Collections.singletonList(new Series(seriesData));
        BigDecimal max = seriesData.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        this.yAxis = new YAxis(max.multiply(BigDecimal.valueOf(1.33)).setScale(0, RoundingMode.DOWN));
    }


    @Getter
    @Setter
    @AllArgsConstructor
    public static class XAxis {
        private final String type = "category";
        private final boolean boundaryGap = false;
        private final AxisLabel axisLabel = new AxisLabel();
        private List<BigDecimal> data;

        @Getter
        @Setter
        public static class AxisLabel {
            private final String formatter = "{value} m/s"; // ECharts 需要的格式
        }
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
        private final Boolean smooth = true;
        private List<BigDecimal> data;
    }

}

